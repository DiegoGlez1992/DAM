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

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class IdSlotTree extends TreeInt {
	
	private final Slot _slot;

	public IdSlotTree(int id, Slot slot) {
		super(id);
		_slot = slot;
	}

	public Slot slot() {
		return _slot;
	}
	
	@Override
	public Tree onAttemptToAddDuplicate(Tree oldNode) {
		_preceding = oldNode._preceding;
		_subsequent = oldNode._subsequent;
		_size = oldNode._size;
		return this;
	}
	
	@Override
	public int ownLength() {
		return Const4.INT_LENGTH * 3;   // _key, _slot._address, _slot._length 
	}
	
	@Override
	public Object read(ByteArrayBuffer buffer) {
		int id = buffer.readInt();
		Slot slot = new Slot(buffer.readInt(), buffer.readInt());
		return new IdSlotTree(id, slot);
	}
	
	@Override
	public void write(ByteArrayBuffer buffer) {
		buffer.writeInt(_key);
		buffer.writeInt(_slot.address());
		buffer.writeInt(_slot.length());
	}

}
