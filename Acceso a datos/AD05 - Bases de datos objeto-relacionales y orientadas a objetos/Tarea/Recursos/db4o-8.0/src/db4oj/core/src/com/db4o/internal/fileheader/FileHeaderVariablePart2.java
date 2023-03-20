/* This file is part of the db4o object database http://www.db4o.com

Copyright (C) 2004 - 2011  Versant Corporation http://www.versant.com

db4o is free software; you can redistribute it and/or modify it under
the terms of version 3 of the GNU General Public License as published
by the Free Software Foundation.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program.  If not, see http://www.gnu.org/licenses/. */
package com.db4o.internal.fileheader;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class FileHeaderVariablePart2 extends FileHeaderVariablePart {
	
    // The variable part format is:

    // (long) checksum
	// (int) address of InMemoryIdSystem slot
	// (int) length of InMemoryIdSystem slot
    // (int) address of InMemoryFreespace
	// (int) length of InMemoryFreespace
    // (int) BTreeFreespace id
    // (int) converter version
	// (int) uuid index ID
    // (int) identity ID
    // (long) versionGenerator
    // (byte) freespace system used

	
	private static final int CHECKSUM_LENGTH = Const4.LONG_LENGTH;
	
	private static final int SINGLE_LENGTH =
		CHECKSUM_LENGTH +
		(Const4.INT_LENGTH * 8) + 
		Const4.LONG_LENGTH +
		1 +
		Const4.ADDED_LENGTH;

	
	private int _address;
	
	private int _length;
	
	public FileHeaderVariablePart2(LocalObjectContainer container, int address, int length){
		super(container);
		_address = address;
		_length = length;
	}

	public FileHeaderVariablePart2(LocalObjectContainer container) {
		this(container, 0, 0);
	}

	@Override
	public Runnable commit(boolean shuttingDown) {
		final int length = ownLength();
		if(_address == 0 || _length < length){
			final Slot slot = allocateSlot(marshalledLength(length));
			_address = slot.address();
			_length = length;
		}
		final ByteArrayBuffer buffer = new ByteArrayBuffer(length);
		marshall(buffer, shuttingDown);
		writeToFile(0, buffer);
		return new Runnable(){
			public void run() {
				writeToFile(length * 2, buffer);
			}
		};
	}

	private int marshalledLength(final int length) {
		return length * 4;
	}

	private void writeToFile(int startAdress, ByteArrayBuffer buffer) {
		_container.writeEncrypt(buffer, _address, startAdress);
		_container.writeEncrypt(buffer, _address, startAdress + _length);
	}

    public int ownLength() {
        return SINGLE_LENGTH;
    }

	public int address() {
		return _address;
	}

	public int length() {
		return _length;
	}

	@Override
	public void read(int address, int length) {
		_address = address;
		_length = length;
    	ByteArrayBuffer buffer = _container.readBufferBySlot(new Slot(address, marshalledLength(length)));
    	boolean versionsAreConsistent = versionsAreConsistentAndSeek(buffer);
    	
    	// TODO: Diagnostic message if versions aren't consistent.
    	
		readBuffer(buffer, versionsAreConsistent);
	}

	protected void readBuffer(ByteArrayBuffer buffer, boolean versionsAreConsistent) {
		if (Deploy.debug) {
		    buffer.readBegin(getIdentifier());
		}
		buffer.incrementOffset(CHECKSUM_LENGTH);
		SystemData systemData = systemData();
		systemData.idSystemSlot(readSlot(buffer, false));
		systemData.inMemoryFreespaceSlot(readSlot(buffer, ! versionsAreConsistent));
		systemData.bTreeFreespaceId(buffer.readInt());
		systemData.converterVersion(buffer.readInt());
		systemData.uuidIndexId(buffer.readInt());
		systemData.identityId(buffer.readInt());
		systemData.lastTimeStampID(buffer.readLong());
		systemData.freespaceSystem(buffer.readByte());
	}
	
    private Slot readSlot(ByteArrayBuffer buffer, boolean readZero) {
    	Slot slot = new Slot(buffer.readInt(), buffer.readInt());
    	if(readZero){
    		return Slot.ZERO;
    	}
		return slot; 
	}

	private void marshall(ByteArrayBuffer buffer, boolean shuttingDown) {
		if (Deploy.debug) {
		    buffer.writeBegin(getIdentifier());
		}
		int checkSumOffset = buffer.offset();
		buffer.incrementOffset(CHECKSUM_LENGTH);
		int checkSumBeginOffset = buffer.offset();
		writeBuffer(buffer, shuttingDown);
        int checkSumEndOffSet = buffer.offset();
        byte[] bytes = buffer._buffer;
        int length = checkSumEndOffSet - checkSumBeginOffset;
        long checkSum = CRC32.checkSum(bytes, checkSumBeginOffset, length);
        buffer.seek(checkSumOffset);
        buffer.writeLong(checkSum);
        buffer.seek(checkSumEndOffSet);
    }

	protected void writeBuffer(ByteArrayBuffer buffer, boolean shuttingDown) {
		SystemData systemData = systemData();
		writeSlot(buffer,systemData.idSystemSlot(),false);
		writeSlot(buffer, systemData.inMemoryFreespaceSlot(), ! shuttingDown);
		buffer.writeInt(systemData.bTreeFreespaceId());
		buffer.writeInt(systemData.converterVersion());
		buffer.writeInt(systemData.uuidIndexId());
        Db4oDatabase identity = systemData.identity();
        buffer.writeInt(identity == null ? 0 : identity.getID(_container.systemTransaction()));
        buffer.writeLong(systemData.lastTimeStampID());
        buffer.writeByte(systemData.freespaceSystem());
	}

	
    private void writeSlot(ByteArrayBuffer buffer, Slot slot, boolean writeZero) {
    	if( writeZero || slot == null){
    		buffer.writeInt(0);
    		buffer.writeInt(0);
    		return;
    	}
    	buffer.writeInt(slot.address());
    	buffer.writeInt(slot.length());
	}

	private boolean checkSumOK(ByteArrayBuffer buffer, int offset){
    	int initialOffSet = buffer.offset();
    	int length = ownLength();
		if (Deploy.debug) {
		    length -= Const4.ADDED_LENGTH;
		}
		length -= CHECKSUM_LENGTH;
		buffer.seek(offset);
		long readCheckSum = buffer.readLong();
		int checkSumBeginOffset = buffer.offset();
		byte[] bytes = buffer._buffer;
		long calculatedCheckSum = CRC32.checkSum(bytes, checkSumBeginOffset, length);
		buffer.seek(initialOffSet);
		return calculatedCheckSum == readCheckSum;
    }

	private boolean versionsAreConsistentAndSeek(ByteArrayBuffer buffer) {
		byte[] bytes = buffer._buffer;
		int length = ownLength();
		int[] offsets = offsets();
		boolean different = false;
		for (int i = 0; i < length; i++) {
			byte b = bytes[offsets[0] + i];
			for (int j = 1; j < 4; j++) {
				if(b != bytes[offsets[j] + i]){
					different = true;
					break;
				}
			}
		}
		if(! different){
			
			// The following line cements our checksum algorithm in stone.
			// Things should be safe enough if we remove the throw.
			// If all four versions of the header are the same,
			// it's bound to be OK. (unless all bytes are zero or
			// greyed out by some kind of overwriting algorithm.)
			
			int firstOffset = 0;
			if(Deploy.debug){
				firstOffset += Const4.IDENTIFIER_LENGTH + Const4.BRACKETS_BYTES;
			}
			if( ! checkSumOK(buffer, firstOffset) ){
				throw new Db4oFileHeaderCorruptionException();
			}
			return true;
		}
		boolean firstPairDiffers = false;
		boolean secondPairDiffers = false;
		for (int i = 0; i < length; i++) {
			if(bytes[offsets[0] + i] !=  bytes[offsets[1] + i]){
				firstPairDiffers = true;
			}
			if(bytes[offsets[2] + i] !=  bytes[offsets[3] + i]){
				secondPairDiffers = true;
			}
		}
		if(! secondPairDiffers){
			if(checkSumOK(buffer, offsets[2])){
				buffer.seek(offsets[2]);
				return false;
			}
		}
		if(firstPairDiffers){
			// Should never ever happen, we are toast.
			// We could still try to use any random version of
			// the header but which one?
			
			// Maybe the first of the second pair could be an 
			// option for a recovery tool, or it could try all
			// versions.
			throw new Db4oFileHeaderCorruptionException();
		}
		if( ! checkSumOK(buffer, 0) ){
			throw new Db4oFileHeaderCorruptionException();
		}
		return false;
	}

	private int[] offsets() {
		return new int[]{0, ownLength(), ownLength() * 2, ownLength() * 3};
	}

	@Override
	public int marshalledLength() {
		return marshalledLength(ownLength());
	}

}
