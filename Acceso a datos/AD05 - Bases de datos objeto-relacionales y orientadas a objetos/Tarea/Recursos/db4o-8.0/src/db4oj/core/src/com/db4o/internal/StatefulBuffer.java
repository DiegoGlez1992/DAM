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
import com.db4o.internal.slots.*;

/**
 * public for .NET conversion reasons
 * 
 * TODO: Split this class for individual usecases. Only use the member
 * variables needed for the respective usecase.
 * 
 * @exclude
 */
public final class StatefulBuffer extends ByteArrayBuffer {
	
	Transaction _trans;
	
    private int _address;
    
    private int _addressOffset;

    private int _cascadeDelete; 

    private int _id;

    private int _length;

    public StatefulBuffer(Transaction trans, int initialBufferSize) {
        _trans = trans;
        _length = initialBufferSize;
        _buffer = new byte[_length];
    }
    
    public StatefulBuffer(Transaction trans, int address, int length) {
        this(trans, length);
        _address = address;
    }
    
    public StatefulBuffer(Transaction trans, Slot slot){
        this(trans, slot.address(), slot.length());
    }

    public StatefulBuffer(Transaction trans, Pointer4 pointer){
        this(trans, pointer._slot);
        _id = pointer._id;
    }


    public void debugCheckBytes() {
        if (Debug4.xbytes) {
            if (_offset != _length) {
                // Db4o.log("!!! YapBytes.debugCheckBytes not all bytes used");
                // This is normal for writing The FreeSlotArray, becauce one
                // slot is possibly reserved by it's own pointer.
            }
        }
    }

    public int getAddress() {
        return _address;
    }
    
    public int getID() {
        return _id;
    }

    public int length() {
        return _length;
    }

    public ObjectContainerBase container(){
        return _trans.container();
    }
    
    public LocalObjectContainer file(){
        return ((LocalTransaction)_trans).localContainer();
    }

    public Transaction transaction() {
        return _trans;
    }

    public byte[] getWrittenBytes(){
        byte[] bytes = new byte[_offset];
        System.arraycopy(_buffer, 0, bytes, 0, _offset);
        return bytes;
    }
    
    public void read() throws Db4oIOException {
        container().readBytes(_buffer, _address,_addressOffset, _length);
    }

    public final StatefulBuffer readStatefulBuffer() {
        int length = readInt();
        if (length == 0) {
            return null;
        }
        StatefulBuffer yb = new StatefulBuffer(_trans, length);
        System.arraycopy(_buffer, _offset, yb._buffer, 0, length);
        _offset += length;
        return yb;
    }

    public void removeFirstBytes(int aLength) {
        _length -= aLength;
        byte[] temp = new byte[_length];
        System.arraycopy(_buffer, aLength, temp, 0, _length);
        _buffer = temp;
        _offset -= aLength;
        if (_offset < 0) {
            _offset = 0;
        }
    }

    public void address(int address) {
        _address = address;
    }

    public void setID(int id) {
        _id = id;
    }

    public void setTransaction(Transaction aTrans) {
        _trans = aTrans;
    }

    public void useSlot(int adress) {
        _address = adress;
        _offset = 0;
    }

    // FIXME: FB remove
    public void useSlot(int address, int length) {
    	useSlot(new Slot(address, length));
    }
    
    public void useSlot(Slot slot) {
        _address = slot.address();
        _offset = 0;
        if (slot.length() > _buffer.length) {
            _buffer = new byte[slot.length()];
        }
        _length = slot.length();
    }

    // FIXME: FB remove
    public void useSlot(int id, int adress, int length) {
        _id = id;
        useSlot(adress, length);
    }
    
    public void write() {
        if (Debug4.xbytes) {
            debugCheckBytes();
        }
        file().writeBytes(this, _address, _addressOffset);
    }

    public void writeEncrypt() {
        if (Deploy.debug) {
            debugCheckBytes();
        }
        file().writeEncrypt(this, _address, _addressOffset);
    }
        
    public ByteArrayBuffer readPayloadWriter(int offset, int length){
        StatefulBuffer payLoad = new StatefulBuffer(_trans, 0, length);
        System.arraycopy(_buffer,offset, payLoad._buffer, 0, length);
        transferPayLoadAddress(payLoad, offset);
        return payLoad;
    }

    private void transferPayLoadAddress(StatefulBuffer toWriter, int offset) {
        int blockedOffset = offset / container().blockSize();
        toWriter._address = _address + blockedOffset;
        toWriter._id = toWriter._address;
        toWriter._addressOffset = _addressOffset;
    }

    public void moveForward(int length) {
        _addressOffset += length;
    }
    
    public String toString(){
        return "id " + _id + " adr " + _address + " len " + _length;
    }
    
	public Slot slot(){
		return new Slot(_address, _length);
	}
	
	public Pointer4 pointer(){
	    return new Pointer4(_id, slot());
	}
	
    public int cascadeDeletes() {
        return _cascadeDelete;
    }
    
    public void setCascadeDeletes(int depth) {
        _cascadeDelete = depth;
    }

}
