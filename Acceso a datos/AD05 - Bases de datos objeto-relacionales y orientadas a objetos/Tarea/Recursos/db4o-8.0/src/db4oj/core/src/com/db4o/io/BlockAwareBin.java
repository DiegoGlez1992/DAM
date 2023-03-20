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
package com.db4o.io;

import static com.db4o.foundation.Environments.*;

import com.db4o.*;
import com.db4o.ext.*;

/**
 * @exclude
 */
public class BlockAwareBin extends BinDecorator {

	private static final int COPY_SIZE = 4096;

	private boolean _readOnly;
	
	private final BlockSize _blockSize = my(BlockSize.class);
	
	public BlockAwareBin(Bin bin) {
		super(bin);
    }

	/**
	 * converts address and address offset to an absolute address
	 */
	protected final long regularAddress(int blockAddress, int blockAddressOffset) {
		if (0 == blockSize()) {
			throw new IllegalStateException();
		}
		return (long) blockAddress * blockSize() + blockAddressOffset;
	}

	/**
	 * copies a block within a file in block mode
	 */
	public void blockCopy(int oldAddress, int oldAddressOffset, int newAddress,
			int newAddressOffset, int length) throws Db4oIOException {
		copy(
			regularAddress(oldAddress, oldAddressOffset),
			regularAddress(newAddress, newAddressOffset),
			length);
	}
	
	/**
	 * copies a block within a file in absolute mode
	 */
	public void copy(long oldAddress, long newAddress, int length)
			throws Db4oIOException {

		if (DTrace.enabled) {
			DTrace.IO_COPY.logLength(newAddress, length);
		}

		if (length > COPY_SIZE) {
			byte[] buffer = new byte[COPY_SIZE];
			int pos = 0;
			while (pos + COPY_SIZE < length) {
				copy(buffer, oldAddress + pos, newAddress + pos);
				pos += COPY_SIZE;
			}
			oldAddress += pos;
			newAddress += pos;
			length -= pos;
		}

		copy(new byte[length], oldAddress, newAddress);
	}

	private void copy(byte[] buffer, long oldAddress, long newAddress)
			throws Db4oIOException {
		read(oldAddress, buffer);
		write(oldAddress, buffer);
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public int blockRead(int address, int offset, byte[] buffer) throws Db4oIOException {
		return blockRead(address, offset, buffer, buffer.length);
	}

	/**
	 * implement to read a buffer at the seeked address
	 */
	public int blockRead(int address, int offset, byte[] bytes, int length) throws Db4oIOException {
		return read(regularAddress(address, offset), bytes, length);
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public int blockRead(int address, byte[] buffer) throws Db4oIOException {
		return blockRead(address, 0, buffer, buffer.length);
	}

	/**
	 * implement to read a buffer at the seeked address
	 */
	public int blockRead(int address, byte[] bytes, int length) throws Db4oIOException {
		return blockRead(address, 0, bytes, length);
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public int read(long pos, byte[] buffer) throws Db4oIOException {
		return read(pos, buffer, buffer.length);
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public void blockWrite(int address, int offset, byte[] buffer) throws Db4oIOException {
		blockWrite(address, offset, buffer, buffer.length);
	}

	/**
	 * implement to read a buffer at the seeked address
	 */
	public void blockWrite(int address, int offset, byte[] bytes, int length) throws Db4oIOException {
		write(regularAddress(address, offset), bytes, length);
	}
	
	/**
	 * reads a buffer at the seeked address
	 * 
	 * @return the number of bytes read and returned
	 */
	public void blockWrite(int address, byte[] buffer) throws Db4oIOException {
		blockWrite(address, 0, buffer, buffer.length);
	}

	/**
	 * implement to read a buffer at the seeked address
	 */
	public void blockWrite(int address, byte[] bytes, int length) throws Db4oIOException {
		blockWrite(address, 0, bytes, length);
	}
	
	@Override
	public void sync() {
		validateReadOnly();
		try{
			super.sync();
		} catch(Db4oIOException e){
			_readOnly = true;
			throw e;
		}
	}
	
	@Override
	public void sync(Runnable runnable) {
		validateReadOnly();
		try{
			super.sync(runnable);
		} catch(Db4oIOException e){
			_readOnly = true;
			throw e;
		}
	}

	/**
	 * writes a buffer to the seeked address
	 */
	public void write(long pos, byte[] bytes) throws Db4oIOException {
		validateReadOnly();
		try{
			write(pos, bytes, bytes.length);
		} catch(Db4oIOException e){
			_readOnly = true;
			throw e;
		}
	}

	private void validateReadOnly() {
		if(_readOnly){
			throw new EmergencyShutdownReadOnlyException();
		}
	}

	/**
	 * returns the block size currently used
	 */
	public int blockSize() {
		return _blockSize.value();
	}
	
	/**
	 * outside call to set the block size of this adapter
	 */
	public void blockSize(int blockSize) {
		if (blockSize < 1) {
			throw new IllegalArgumentException();
		}
		_blockSize.set(blockSize);
	}
}