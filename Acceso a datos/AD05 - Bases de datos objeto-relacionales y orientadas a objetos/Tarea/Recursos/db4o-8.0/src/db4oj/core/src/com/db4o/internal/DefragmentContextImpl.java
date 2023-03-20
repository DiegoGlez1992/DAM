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
package com.db4o.internal;

import java.io.*;

import com.db4o.*;
import com.db4o.defragment.*;
import com.db4o.foundation.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.mapping.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public final class DefragmentContextImpl implements ReadWriteBuffer, DefragmentContext {
	
	private ByteArrayBuffer _source;
	
	private ByteArrayBuffer _target;
	
	private DefragmentServices _services;
	
	private final ObjectHeader _objectHeader;
	
	private int _declaredAspectCount;

	private int _currentParentSourceID;
	
	public DefragmentContextImpl(ByteArrayBuffer source, DefragmentContextImpl context) {
		this(source, context._services, context._objectHeader);
	}

	public DefragmentContextImpl(ByteArrayBuffer source,DefragmentServices services) {
	    this(source, services, null);
	}
	
	public DefragmentContextImpl(ByteArrayBuffer source, DefragmentServices services, ObjectHeader header){
        _source = source;
        _services=services;
        _target = new ByteArrayBuffer(length());
        _source.copyTo(_target, 0, 0, length());
        _objectHeader = header;
	}
	
	public DefragmentContextImpl(DefragmentContextImpl parentContext, ObjectHeader header){
	    _source = parentContext._source;
	    _target = parentContext._target;
	    _services = parentContext._services;
	    _objectHeader = header;
	}
	
	public int offset() {
		return _source.offset();
	}

	public void seek(int offset) {
		_source.seek(offset);
		_target.seek(offset);
	}

	public void incrementOffset(int numBytes) {
		_source.incrementOffset(numBytes);
		_target.incrementOffset(numBytes);
	}

	public void incrementIntSize() {
		incrementOffset(Const4.INT_LENGTH);
	}
	
	public int copySlotlessID() {
	    return copyUnindexedId(false);
	}

	public int copyUnindexedID() {
	    return copyUnindexedId(true);
	}
	
	private int copyUnindexedId(boolean doRegister){
        int orig=_source.readInt();

        // TODO: There is no test case for the zero case
        if(orig == 0){
            _target.writeInt(0);
            return 0;
        }
        
        int mapped=-1;
        try {
            mapped=_services.strictMappedID(orig);
        } catch (MappingNotFoundException exc) {
            mapped=_services.targetNewId();
            _services.mapIDs(orig,mapped, false);
            if(doRegister){
                _services.registerUnindexed(orig);
            }
        }
        _target.writeInt(mapped);
        return mapped;
	}

	public int copyID() {
		// This code is slightly redundant. 
		// The profiler shows it's a hotspot.
		// The following would be non-redudant. 
		// return copy(false, false);
		
		int id = _source.readInt();
		return writeMappedID(id);
	}

	public int copyID(boolean flipNegative) {
		int id=_source.readInt();
		return internalCopyID(flipNegative, id);
	}

	public int copyIDReturnOriginalID() {
		return copyIDReturnOriginalID(false);
	}
	
	public int copyIDReturnOriginalID(boolean flipNegative) {
		int id=_source.readInt();
		internalCopyID(flipNegative, id);
		boolean flipped = flipNegative && (id < 0);
		if(flipped) {
			return -id;
		}
		return id;
	}

	private int internalCopyID(boolean flipNegative, int id) {
		boolean flipped = flipNegative && (id < 0);
		if(flipped) {
			id=-id;
		}
		int mapped=_services.mappedID(id);
		if(flipped) {
			mapped=-mapped;
		}
		_target.writeInt(mapped);
		return mapped;
	}
	
	public void readBegin(byte identifier) {
		_source.readBegin(identifier);
		_target.readBegin(identifier);
	}
	
	public byte readByte() {
		byte value=_source.readByte();
		_target.incrementOffset(1);
		return value;
	}
	
	public void readBytes(byte[] bytes) {
		_source.readBytes(bytes);
		_target.incrementOffset(bytes.length);
	}

	public int readInt() {
		int value=_source.readInt();
		_target.incrementOffset(Const4.INT_LENGTH);
		return value;
	}

	public void writeInt(int value) {
		_source.incrementOffset(Const4.INT_LENGTH);
		_target.writeInt(value);
	}
	
	public void write(LocalObjectContainer file,int address) {
		file.writeBytes(_target,address,0);
	}
	
	public void incrementStringOffset(LatinStringIO sio) {
	    incrementStringOffset(sio, _source);
	    incrementStringOffset(sio, _target);
	}
	
	private void incrementStringOffset(LatinStringIO sio, ByteArrayBuffer buffer) {
		sio.readLengthAndString(buffer);
	}
	
	public ByteArrayBuffer sourceBuffer() {
		return _source;
	}

	public ByteArrayBuffer targetBuffer() {
		return _target;
	}
	
	public IDMapping mapping() {
		return _services;
	}

	public Transaction systemTrans() {
		return transaction();
	}

	public DefragmentServices services() {
		return _services;
	}

	public static void processCopy(DefragmentServices context, int sourceID,SlotCopyHandler command) {
		ByteArrayBuffer sourceReader = context.sourceBufferByID(sourceID);
		processCopy(context, sourceID, command, sourceReader);
	}

	public static void processCopy(DefragmentServices services, int sourceID,SlotCopyHandler command, ByteArrayBuffer sourceReader) {
		int targetID=services.strictMappedID(sourceID);
	
		Slot targetSlot = services.allocateTargetSlot(sourceReader.length());
		
		services.mapping().mapId(targetID, targetSlot);
		
		DefragmentContextImpl context=new DefragmentContextImpl(sourceReader,services);
		command.processCopy(context);
		services.targetWriteBytes(context,targetSlot.address());
	}

	public void writeByte(byte value) {
		_source.incrementOffset(1);
		_target.writeByte(value);
	}

	public long readLong() {
		long value=_source.readLong();
		_target.incrementOffset(Const4.LONG_LENGTH);
		return value;
	}

	public void writeLong(long value) {
		_source.incrementOffset(Const4.LONG_LENGTH);
		_target.writeLong(value);
	}

	public BitMap4 readBitMap(int bitCount) {
		BitMap4 value=_source.readBitMap(bitCount);
		_target.incrementOffset(value.marshalledLength());
		return value;
	}

	public void readEnd() {
		_source.readEnd();
		_target.readEnd();
	}

    public int writeMappedID(int originalID) {
		int mapped=_services.mappedID(originalID);
		_target.writeInt(mapped);
		return mapped;
	}

	public int length() {
		return _source.length();
	}
	
	public Transaction transaction() {
		return services().systemTrans();
	}
	
	public ObjectContainerBase container() {
	    return transaction().container();
	}

	public TypeHandler4 typeHandlerForId(int id) {
		return container().typeHandlerForClassMetadataID(id);
	}
	
	public int handlerVersion(){
		return _objectHeader.handlerVersion();
	}

	public boolean isLegacyHandlerVersion() {
		return handlerVersion() == 0;
	}

	public int mappedID(int origID) {
		return mapping().strictMappedID(origID);
	}

	public ObjectContainer objectContainer() {
		return container();
	}

	/**
	 * only used by old handlers: OpenTypeHandler0, StringHandler0, ArrayHandler0.
	 * Doesn't need to work with modern IdSystems.
	 */
	public Slot allocateTargetSlot(int length) {
		return _services.allocateTargetSlot(length);
	}

	/**
	 * only used by old handlers: OpenTypeHandler0, StringHandler0, ArrayHandler0.
	 * Doesn't need to work with modern IdSystems.
	 */
	public Slot allocateMappedTargetSlot(int sourceAddress, int length) {
		Slot slot = allocateTargetSlot(length);
		_services.mapIDs(sourceAddress, slot.address(), false);
		return slot;
	}

	public int copySlotToNewMapped(int sourceAddress, int length) throws IOException {
    	Slot slot = allocateMappedTargetSlot(sourceAddress, length);
    	ByteArrayBuffer sourceBuffer = sourceBufferByAddress(sourceAddress, length);
    	targetWriteBytes(slot.address(), sourceBuffer);
		return slot.address();
	}

	public void targetWriteBytes(int address, ByteArrayBuffer buffer) {
		_services.targetWriteBytes(buffer, address);
	}

	public ByteArrayBuffer sourceBufferByAddress(int sourceAddress, int length) throws IOException {
		ByteArrayBuffer sourceBuffer = _services.sourceBufferByAddress(sourceAddress, length);
		return sourceBuffer;
	}

	public ByteArrayBuffer sourceBufferById(int sourceId) throws IOException {
		ByteArrayBuffer sourceBuffer = _services.sourceBufferByID(sourceId);
		return sourceBuffer;
	}

	public void writeToTarget(int address) {
		_services.targetWriteBytes(this, address);
	}

    public void writeBytes(byte[] bytes) {
        _target.writeBytes(bytes);
        _source.incrementOffset(bytes.length);
    }

    public ReadBuffer buffer() {
        return _source;
    }

    public void defragment(TypeHandler4 handler) {
        final TypeHandler4 typeHandler = HandlerRegistry.correctHandlerVersion(this, handler);
        if(Handlers4.useDedicatedSlot(this, typeHandler)){
            if(Handlers4.hasClassIndex(typeHandler)){
                copyID();
            } else {
                copyUnindexedID();
            }
            return;
        }
        typeHandler.defragment(DefragmentContextImpl.this);
    }

    public void beginSlot() {
        // do nothing
    }

    public ClassMetadata classMetadata() {
        return _objectHeader.classMetadata();
    }

    public boolean isNull(int fieldIndex) {
        return _objectHeader._headerAttributes.isNull(fieldIndex);
    }

	public int declaredAspectCount() {
		return _declaredAspectCount;
	}

	public void declaredAspectCount(int count) {
		_declaredAspectCount = count;
	}

	public SlotFormat slotFormat() {
		return SlotFormat.forHandlerVersion(handlerVersion());
	}

	public void currentParentSourceID(int id) {
		_currentParentSourceID = id;
	}
	
	public int consumeCurrentParentSourceID() {
		int id = _currentParentSourceID;
		_currentParentSourceID = 0;
		return id;
	}

	public void copyAddress() {
		int sourceEntryAddress = _source.readInt();
		int sourceId = consumeCurrentParentSourceID();
		int sourceObjectAddress = _services.sourceAddressByID(sourceId);
		int entryOffset = sourceEntryAddress - sourceObjectAddress;
		int targetObjectAddress = _services.targetAddressByID(_services.strictMappedID(sourceId));
		_target.writeInt(targetObjectAddress + entryOffset);
	}
    
}
