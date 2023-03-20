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
package com.db4o.internal.slots;

import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class ReferencedSlot extends TreeInt {

	private Slot _slot;

	private int _references;

	public ReferencedSlot(int a_key) {
		super(a_key);
	}

	public Object shallowClone() {
		ReferencedSlot rs = new ReferencedSlot(_key);
		rs._slot = _slot;
		rs._references = _references;
		return super.shallowCloneInternal(rs);
	}

	public void pointTo(Slot slot) {
		_slot = slot;
	}

	public Tree free(LocalObjectContainer file, Tree treeRoot, Slot slot) {
		file.free(_slot.address(), _slot.length());
		if (removeReferenceIsLast()) {
			if(treeRoot != null){
				return treeRoot.removeNode(this);
			}
		}
		pointTo(slot);
		return treeRoot;
	}

	public boolean addReferenceIsFirst() {
		_references++;
		return (_references == 1);
	}

	public boolean removeReferenceIsLast() {
		_references--;
		return _references < 1;
	}

    public Slot slot() {
        return _slot;
    }

}
