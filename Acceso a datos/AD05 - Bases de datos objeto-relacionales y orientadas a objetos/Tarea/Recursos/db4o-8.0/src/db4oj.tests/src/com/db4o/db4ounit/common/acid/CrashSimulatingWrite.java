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
package com.db4o.db4ounit.common.acid;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.internal.transactionlog.*;

public class CrashSimulatingWrite {
	
	int _index;
    
    byte[] _data;
    long _offset;
    int _length;
    
    byte[] _lockFileData;
    byte[] _logFileData;
    
    public CrashSimulatingWrite(int index, byte[] data, long offset, int length, byte[] lockFileData, byte[] logFileData) {
    	_index = index;
        _data = data;
        _offset = offset;
        _length = length;
        _lockFileData = lockFileData;
        _logFileData = logFileData;
    }

    public void write(String path, RandomAccessFile raf, boolean writeTrash) throws IOException {
    	if(_offset == 0){
    		writeTrash = false;
    	}
        raf.seek(_offset);
        raf.write(bytesToWrite(_data, writeTrash), 0, _length);
        write(FileBasedTransactionLogHandler.lockFileName(path), _lockFileData, writeTrash);
        write(FileBasedTransactionLogHandler.logFileName(path), _logFileData, writeTrash);
    }
    
    public String toString(){
        return "" + _index + " A:(" + _offset + ") L:(" + _length + ")";
    }
    
    private void write(String fileName, byte[] bytes, boolean writeTrash){
    	if(bytes == null){
    		return;
    	}
    	try {
        	RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        	raf.write(bytesToWrite(bytes, writeTrash));
			raf.close();
		} catch (IOException e) {
			throw new Db4oException(e);
		}
    }
    
    private byte[] bytesToWrite(byte[] bytes, boolean writeTrash){
    	if(! writeTrash){
    		return bytes;
    	}
		byte[] trash = new byte[bytes.length];
		for (int i = 0; i < trash.length; i++) {
			trash[i] = (byte) (i + 100);
		}
		return trash;
    }

}
