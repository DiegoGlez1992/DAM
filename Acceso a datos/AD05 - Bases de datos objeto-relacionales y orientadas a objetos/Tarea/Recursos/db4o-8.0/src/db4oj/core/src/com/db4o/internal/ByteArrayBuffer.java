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

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.encoding.*;
import com.db4o.internal.handlers.*;

/**
 * 
 * @exclude
 */
public class ByteArrayBuffer implements ReadWriteBuffer {
	
	private static final ThreadLocal<Boolean> _checkXBytes = Debug4.xbytes ? new ThreadLocal<Boolean>() : null;
	
	// for coding convenience, we allow objects to grab into the buffer
	public byte[] _buffer;
	public int _offset;

	
	ByteArrayBuffer(){
		if(Debug4.xbytes){
			checkXBytes(true);
		}
	}
	
	public ByteArrayBuffer(int length){
		this();
		_buffer = new byte[length];
	}
	
	public ByteArrayBuffer(byte[] buffer){
		this();
		_buffer = buffer;
	}
	
	public void seek(int offset) {
		_offset = offset;
	}
	
	public void writeBytes(byte[] bytes) {
        System.arraycopy(bytes, 0, _buffer, _offset, bytes.length);
        _offset += bytes.length;
	}
	
    // TODO: Change all callers to call writeBytes directly.
	public void append(byte[] bytes) {
        writeBytes(bytes);
    }
	
	public final boolean containsTheSame(ByteArrayBuffer other) {
	    if (other != null) {
	        return Arrays4.equals(_buffer, other._buffer);
	    }
	    return false;
	}
	
    public void copyTo(ByteArrayBuffer to, int fromOffset, int toOffset, int length) {
        System.arraycopy(_buffer, fromOffset, to._buffer, toOffset, length);
    }

	public int length() {
		return _buffer.length;
	}
	
    public void incrementOffset(int a_by) {
        _offset += a_by;
    }
    
    /**
     * non-encrypted read, used for indexes
     */
    public void read(ObjectContainerBase stream, int address, int addressOffset){
        stream.readBytes(_buffer, address, addressOffset, length());
    }
	
    public final void readBegin(byte identifier) {
		if (Deploy.debug) {
		    Debug4.readBegin(this, identifier);
		}
	}
    
    public BitMap4 readBitMap(int bitCount){
        BitMap4 map = new BitMap4(_buffer, _offset, bitCount);
        _offset += map.marshalledLength();
        return map;
    }
	
	public byte readByte() {
		return _buffer[_offset++];
	}
	
	public byte[] readBytes(int a_length){
	    byte[] bytes = new byte[a_length];
		readBytes(bytes);
	    return bytes;
	}
	
	public void readBytes(byte[] bytes) {
		int length = bytes.length;
	    System.arraycopy(_buffer, _offset, bytes, 0, length);
	    _offset += length;
	}
    
	public final ByteArrayBuffer readEmbeddedObject(Transaction trans) throws Db4oIOException {
	    int address = readInt();
	    int length = readInt();
	    if(address == 0){
	        return null;
	    }
		return trans.container().decryptedBufferByAddress(address, length);
	}
	
	public void readEncrypt(ObjectContainerBase stream, int address) throws Db4oIOException {
		stream.readBytes(_buffer, address, length());
		stream._handlers.decrypt(this);
	}

    public void readEnd() {
        if(Deploy.debug){
            Debug4.readEnd(this);
        }
    }

    public final int readInt() {
        if (Deploy.debug) {
			int ret = 0;
            readBegin(Const4.YAPINTEGER);
            if (Deploy.debugLong) {
                ret =
                    Integer.valueOf(new LatinStringIO().read(this, Const4.INTEGER_BYTES).trim())
                        .intValue();
            } else {
                for (int i = 0; i < Const4.INTEGER_BYTES; i++) {
                    ret = (ret << 8) + (_buffer[_offset++] & 0xff);
                }
            }
            readEnd();
			return ret;
        }
            
        int o = (_offset += 4) - 1;
        
        return (_buffer[o] & 255) | (_buffer[--o] & 255)
            << 8 | (_buffer[--o] & 255)
            << 16 | _buffer[--o]
            << 24;
        
    }
    
    public long readLong() {
        return LongHandler.readLong(this);
    }
    
    public ByteArrayBuffer readPayloadReader(int offset, int length){
        ByteArrayBuffer payLoad = new ByteArrayBuffer(length);
        System.arraycopy(_buffer,offset, payLoad._buffer, 0, length);
        return payLoad;
    }

    void replaceWith(byte[] a_bytes) {
        System.arraycopy(a_bytes, 0, _buffer, 0, length());
    }
    
    public String toString() {
		String str = "";
		for (int i = 0; i < _buffer.length; i++) {
			if (i > 0) {
				str += " , ";
			}
			str += _buffer[i];
		}
		return str;
	}
    
    public void writeBegin(byte a_identifier) {
        if (Deploy.debug) {
            if (Deploy.brackets) {
                writeByte(Const4.YAPBEGIN);
            }
            if (Deploy.identifiers) {
                writeByte(a_identifier);
            }
        }
    }
    
    public final void writeBitMap(BitMap4 nullBitMap) {
        nullBitMap.writeTo(_buffer, _offset);
        _offset += nullBitMap.marshalledLength();
    }
    
    public final void writeByte(byte a_byte) {
        _buffer[_offset++] = a_byte;
    }
    
    public void writeEnd() {
        if (Deploy.debug && Deploy.brackets) {
            writeByte(Const4.YAPEND);
        }
    }
    
    public final void writeInt(int a_int) {
        if (Deploy.debug) {
            IntHandler.writeInt(a_int, this);
        } else {
            int o = _offset + 4;
            _offset = o;
            byte[] b = _buffer;
            b[--o] = (byte)a_int;
            b[--o] = (byte) (a_int >>= 8);
            b[--o] = (byte) (a_int >>= 8);
            b[--o] = (byte) (a_int >> 8);
        }
    }
    
    public void writeIDOf(Transaction trans, Object obj) {
        if(obj == null){
            writeInt(0);
            return;
        }
        
        if(obj instanceof PersistentBase){
            writeIDOf(trans, (PersistentBase)obj);
            return;
        }
        
        writeInt(((Integer)obj).intValue());
    }
    
    public void writeIDOf(Transaction trans, PersistentBase persistent) {
        if(persistent == null){
            writeInt(0);
            return;
        }
        if(canWritePersistentBase()){
        	persistent.writeOwnID(trans, this);
        }else{
        	writeInt(persistent.getID());
        }
    }
    
	protected boolean canWritePersistentBase(){
		return true;
	}
    
    public void writeShortString(Transaction trans, String a_string) {
        trans.container()._handlers._stringHandler.writeShort(trans, a_string, this);
    }

    public void writeLong(long l) {
        LongHandler.writeLong(this, l);
    }

	public void incrementIntSize() {
		incrementOffset(Const4.INT_LENGTH);
	}

	public int offset() {
		return _offset;
	}
	
	public void ensureSize(int size){
		if(size == _buffer.length){
			return;
		}
		_buffer = new byte[size];
	}

	public void skip(int length) {
		seek(_offset + length);
    }
	
	public void checkXBytes(boolean flag){
		if(Debug4.xbytes){
			_checkXBytes.set(flag);
		}
	}

	public boolean checkXBytes() {
		if(Debug4.xbytes){
			return _checkXBytes.get();
		}
		throw new IllegalStateException();
	}

	public boolean eof() {
		if(Deploy.debug){
			return _offset >= _buffer.length - Const4.ADDED_LENGTH; 
		}
		return _offset == _buffer.length;
	}

	public int remainingByteCount() {
		return _buffer.length - _offset;
	}
}
