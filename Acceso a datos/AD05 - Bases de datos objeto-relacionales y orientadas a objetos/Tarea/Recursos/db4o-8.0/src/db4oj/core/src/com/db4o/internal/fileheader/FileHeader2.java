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
import com.db4o.internal.*;

/**
 * @exclude
 */
public class FileHeader2 extends NewFileHeaderBase {
	
    // The header format is:

    // (byte) 'd'
    // (byte) 'b'
    // (byte) '4'
    // (byte) 'o'
    // (byte) headerVersion
    // (int) headerLock
    // (long) openTime
    // (long) accessTime
    // (int) blockSize
    // (int) classCollectionID
	// (byte) idSystemType
	// (int) variable part address
	// (int) variable part length
	// (int) transaction pointer address
	
	
    private static final int BLOCKSIZE_OFFSET = ACCESS_TIME_OFFSET + Const4.LONG_LENGTH;
    
    public static final int HEADER_LENGTH = BLOCKSIZE_OFFSET + (Const4.INT_LENGTH * 5) + 1;
    
    private int _transactionPointerAddress = 0;


	@Override
	public int length() {
		return HEADER_LENGTH;
	}

	@Override
	protected void read(LocalObjectContainer container, ByteArrayBuffer reader) {
        newTimerFileLock(container);
		oldEncryptionOff(container);
        checkThreadFileLock(container, reader);
        reader.seek(BLOCKSIZE_OFFSET);
        container.blockSizeReadFromFile(reader.readInt());
        SystemData systemData = container.systemData();
        systemData.classCollectionID(reader.readInt());
        container.systemData().idSystemType(reader.readByte());
        _variablePart = createVariablePart(container);
        int variablePartAddress = reader.readInt();
        int variablePartLength = reader.readInt();
        _variablePart.read(variablePartAddress, variablePartLength);
        _transactionPointerAddress = reader.readInt();
        if(_transactionPointerAddress != 0){
    		ByteArrayBuffer buffer = new ByteArrayBuffer(TRANSACTION_POINTER_LENGTH); 
            buffer.read(container, _transactionPointerAddress, 0);
            systemData.transactionPointer1(buffer.readInt());
            systemData.transactionPointer2(buffer.readInt());
        }
	}


	@Override
	public void writeFixedPart(LocalObjectContainer file,
			boolean startFileLockingThread, boolean shuttingDown,
			StatefulBuffer writer, int blockSize) {
		
    	SystemData systemData = file.systemData();
        writer.append(SIGNATURE);
        writer.writeByte(version()); 
        writer.writeInt((int)timeToWrite(_timerFileLock.openTime(), shuttingDown));
        writer.writeLong(timeToWrite(_timerFileLock.openTime(), shuttingDown));
        writer.writeLong(timeToWrite(System.currentTimeMillis(), shuttingDown));
        writer.writeInt(blockSize);
		writer.writeInt(systemData.classCollectionID());
		writer.writeByte(systemData.idSystemType());
        writer.writeInt(((FileHeaderVariablePart2)_variablePart).address());
        writer.writeInt(((FileHeaderVariablePart2)_variablePart).length());
        writer.writeInt(_transactionPointerAddress);
        if (Debug4.xbytes) {
        	writer.checkXBytes(false);
        }
        writer.write();
        if(shuttingDown){
        	writeVariablePart(file, true);
        } else {
        	file.syncFiles();
        }
        if(startFileLockingThread){
        	file.threadPool().start("db4o lock thread", _timerFileLock);
        }
	}

	@Override
	public void writeTransactionPointer(Transaction systemTransaction, int transactionPointer) {
		if(_transactionPointerAddress == 0){
			LocalObjectContainer file = ((LocalTransaction)systemTransaction).localContainer();
			_transactionPointerAddress = file.allocateSafeSlot(TRANSACTION_POINTER_LENGTH).address();
			file.writeHeader(false, false);
		}
		writeTransactionPointer(systemTransaction, transactionPointer, _transactionPointerAddress, 0);
	}

	@Override
    protected byte version() {
		return (byte) 2;
	}
    
    @Override
    protected NewFileHeaderBase createNew() {
    	return new FileHeader2();
    }
    
    @Override
    public FileHeaderVariablePart createVariablePart(LocalObjectContainer file) {
    	return new FileHeaderVariablePart2(file);
    }
    
}
