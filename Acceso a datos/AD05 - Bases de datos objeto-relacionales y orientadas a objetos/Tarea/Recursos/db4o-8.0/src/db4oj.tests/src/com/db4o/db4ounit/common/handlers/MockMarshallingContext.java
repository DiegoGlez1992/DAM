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
package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.marshall.*;

public abstract class MockMarshallingContext {

    private final ObjectContainer _objectContainer;
    
    final ByteArrayBuffer _header;
    
    final ByteArrayBuffer _payLoad;
    
    protected ByteArrayBuffer _current;
    
    public MockMarshallingContext(ObjectContainer objectContainer){
        _objectContainer = objectContainer;
        _header = new ByteArrayBuffer(1000);
        _payLoad = new ByteArrayBuffer(1000);
        _current = _header;
    }

    public WriteBuffer newBuffer(int length) {
        return new ByteArrayBuffer(length);
    }

    public ObjectContainer objectContainer() {
        return _objectContainer;
    }

	public void useVariableLength() {
	    _current = _payLoad;
	}

	public byte readByte() {
		return _current.readByte();
	}
	
    public void readBytes(byte[] bytes) {
        _current.readBytes(bytes);
    }

	public int readInt() {
		return _current.readInt();
	}
	
	public long readLong(){
	    return _current.readLong();
	}

	public void writeByte(byte b) {
	    _current.writeByte(b);
	}

	public void writeInt(int i) {
	    _current.writeInt(i);
	}
	
    public void writeLong(long l) {
        _current.writeLong(l);
    }

    public void writeBytes(byte[] bytes) {
        _current.writeBytes(bytes);
    }
	
    public Object readObject() {
        int id = readInt();
        Object obj = container().getByID(transaction(), id);
        objectContainer().activate(obj, Integer.MAX_VALUE);
        return obj;
    }
 
    public void writeObject(Object obj) {
        int id = container().storeInternal(transaction(), obj, false);
        writeInt(id);
    }
    
    public Transaction transaction(){
        return container().transaction();
    }

    protected ObjectContainerBase container() {
        return ((InternalObjectContainer) _objectContainer).container();
    }
    
	public int offset() {
		return _current.offset();
	}

	public void seek(int offset) {
		_current.seek(offset);
	}
	
    public void seekCurrentInt() {
        seek(readInt());
    }

}