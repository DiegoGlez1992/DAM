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
package com.db4o.internal.ids;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
* @exclude
*/
public class IdSlotMapping {
	
	// persistent and indexed in DatabaseIdMapping, don't change the name
	public int _id;
	
	public int _address;
	
	public int _length;
	
	public IdSlotMapping(int id, int address, int length) {
		_id = id;
		_address = address;
		_length = length;
	}
	
	public IdSlotMapping(int id, Slot slot){
		this(id, slot.address(), slot.length());
	}
	
	public Slot slot(){
		return new Slot(_address, _length);
	}

	public void write(ByteArrayBuffer buffer) {
		buffer.writeInt(_id);
		buffer.writeInt(_address);
		buffer.writeInt(_length);
	}
	
	public static IdSlotMapping read(ByteArrayBuffer buffer){
		return new IdSlotMapping(buffer.readInt(), buffer.readInt(), buffer.readInt());
	}
	
	@Override
	public String toString() {
		return "" + _id + ":" + _address + "," + _length;
	}
	
}