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
import com.db4o.io.*;


/**
 * @sharpen.ignore
 */
public class TimerFileLockEnabled extends TimerFileLock{
    
    private final BlockAwareBin _timerFile;
    
    private final Object _timerLock;
    
    private byte[] _longBytes = new byte[Const4.LONG_LENGTH];
    
    private byte[] _intBytes = new byte[Const4.INT_LENGTH];
    
    private int _headerLockOffset = 2 + Const4.INT_LENGTH; 
    
    private final long _opentime;
    
    private int _baseAddress = -1;
    
    private int _openTimeOffset;

    private int _accessTimeOffset;
    
    private boolean _closed = false;
    
    public TimerFileLockEnabled(IoAdaptedObjectContainer file) {
        _timerLock = new Object();
        _timerFile = file.timerFile();
        _opentime = uniqueOpenTime();
    }
    
    public void checkHeaderLock() {
    	long openTime = readInt(0, _headerLockOffset);
		if( ((int)_opentime) != openTime){
    		throw new DatabaseFileLockedException(_timerFile.toString());	
    	}
		writeHeaderLock();
    }
    
    public void checkOpenTime() {
		long readOpenTime = readLong(_baseAddress, _openTimeOffset);
		if (_opentime != readOpenTime) {
			throw new DatabaseFileLockedException(_timerFile.toString());
		}
		writeOpenTime();		
	}
    
    public void checkIfOtherSessionAlive(LocalObjectContainer container, int address, int offset,
		long lastAccessTime) throws Db4oIOException {
    	if(_timerFile == null) { // need to check? 
    		return;
    	}
		long waitTime = Const4.LOCK_TIME_INTERVAL * 5;
		long currentTime = System.currentTimeMillis();
		
		// If someone changes the system clock here, he is out of luck.
		while (System.currentTimeMillis() < currentTime + waitTime) {
			Runtime4.sleep(waitTime);
		}
		
		long currentAccessTime = readLong(address, offset);
		if ((currentAccessTime > lastAccessTime)) {
			throw new DatabaseFileLockedException(container.toString());
		}
	}
    
    public void close() throws Db4oIOException {
        synchronized (_timerLock) {
        	writeAccessTime(true);
			_closed = true;
			_timerLock.notifyAll();
		}
    }
    
    public boolean lockFile() {
        return true;
    }
    
    public long openTime() {
        return _opentime;
    }

    public void run() {
		while (true) {
			synchronized (_timerLock) {
				if (_closed) {
					return;
				}
				try {
					writeAccessTime(false);
				} catch (Db4oIOException e) {
					return;
				}
				try {
					_timerLock.wait(Const4.LOCK_TIME_INTERVAL);
				} catch (Exception e) {
				}
			}
		}
	}

    public void setAddresses(int baseAddress, int openTimeOffset, int accessTimeOffset){
        _baseAddress = baseAddress;
        _openTimeOffset = openTimeOffset;
        _accessTimeOffset = accessTimeOffset;
    }
    
    public void start() throws Db4oIOException{
        writeAccessTime(false);
        checkOpenTime(); 
    }
    
    private long uniqueOpenTime(){
        return  System.currentTimeMillis();
        // TODO: More security is possible here to make this time unique
        // to other processes. 
    }
    
    private boolean writeAccessTime(boolean closing) throws Db4oIOException {
        if(noAddressSet()){
            return true;
        }
        long time = closing ? 0 : System.currentTimeMillis();
        boolean ret = writeLong(_baseAddress, _accessTimeOffset, time);
        sync();
        return ret;
    }

	private boolean noAddressSet() {
		return _baseAddress < 0;
	}

    public void writeHeaderLock(){
    	writeInt(0, _headerLockOffset, (int)_opentime);
		sync();
    }

    public void writeOpenTime() {
    	writeLong(_baseAddress, _openTimeOffset, _opentime);
		sync();
    }
    
    private boolean writeLong(int address, int offset, long time) throws Db4oIOException {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return false;
            }
            if (Deploy.debug) {
                ByteArrayBuffer lockBytes = new ByteArrayBuffer(Const4.LONG_LENGTH);
                lockBytes.writeLong(time);
                _timerFile.blockWrite(address, offset, lockBytes._buffer);
            } else {
            	PrimitiveCodec.writeLong(_longBytes, time);
                _timerFile.blockWrite(address, offset, _longBytes);
            }
            return true;
    	}
    }
    
    private long readLong(int address, int offset) throws Db4oIOException {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return 0;
            }
            if (Deploy.debug) {
                ByteArrayBuffer lockBytes = new ByteArrayBuffer(Const4.LONG_LENGTH);
                _timerFile.syncRead(address + offset, lockBytes._buffer, Const4.LONG_LENGTH);
                return lockBytes.readLong();
            }
            _timerFile.syncRead(address + offset, _longBytes, Const4.LONG_LENGTH);
            return PrimitiveCodec.readLong(_longBytes, 0);
    	}
    }

    private boolean writeInt(int address, int offset, int time) {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return false;
            }
            if (Deploy.debug) {
                ByteArrayBuffer lockBytes = new ByteArrayBuffer(Const4.INT_LENGTH);
                lockBytes.writeInt(time);
                _timerFile.blockWrite(address, offset, lockBytes._buffer);
            } else {
            	PrimitiveCodec.writeInt(_intBytes, 0, time);
                _timerFile.blockWrite(address, offset, _intBytes);
            }
            return true;
    	}
    }
    
    private long readInt(int address, int offset)  {
    	synchronized (_timerLock) {
            if(_timerFile == null){
                return 0;
            }
            if (Deploy.debug) {
                ByteArrayBuffer lockBytes = new ByteArrayBuffer(Const4.INT_LENGTH);
                _timerFile.syncRead(address + offset, lockBytes._buffer, Const4.INT_LENGTH);
                return lockBytes.readInt();
            }
            _timerFile.syncRead(address + offset, _longBytes, Const4.LONG_LENGTH);
            return PrimitiveCodec.readInt(_longBytes, 0);
    	}
    }
    
    private void sync() throws Db4oIOException {
    	try{
    		_timerFile.sync();	
    	} catch(EmergencyShutdownReadOnlyException ex){
    		// ignore this one, emergency shutdown in progress
    	}
    }
    
}


