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
package com.db4o.defragment;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;


/**
 * In-memory mapping for IDs during a defragmentation run.
 * This is faster than the {@link DatabaseIdMapping} but
 * it uses more memory. If you have OutOfMemory conditions
 * with this id mapping, use the {@link DatabaseIdMapping}
 * instead.
 * 
 * @see Defragment
 */
public class InMemoryIdMapping extends AbstractIdMapping {
	
	private IdSlotTree _idsToSlots;
	
	private Tree _tree;
	
	public int mappedId(int oldID) {
		int classID = mappedClassID(oldID);
		if(classID != 0) {
			return classID;
		}
		TreeIntObject res = (TreeIntObject) TreeInt.find(_tree, oldID);
		if(res != null){
			return ((Integer)res._object).intValue();
		}
		return 0;
	}

	public void open() {
	}
	
	public void close() {
	}

	protected void mapNonClassIDs(int origID, int mappedID) {
		_tree = Tree.add(_tree, new TreeIntObject(origID, new Integer(mappedID)));
	}
	
	public int addressForId(int id){
		IdSlotTree node = (IdSlotTree) _idsToSlots.find(id);
		if(node == null){
			throw new IllegalStateException();
		}
		return node.slot().address();
	}

	public void mapId(int id, Slot slot) {
		IdSlotTree idSlotMapping = new IdSlotTree(id, slot);
		_idsToSlots = Tree.add(_idsToSlots, idSlotMapping);
	}

	public Visitable<SlotChange> slotChanges() {
		return new Visitable<SlotChange>() {
			public void accept(final Visitor4<SlotChange> outSideVisitor) {
				Tree.traverse(_idsToSlots, new Visitor4<IdSlotTree>() {
					public void visit(IdSlotTree idSlotMapping) {
						SlotChange slotChange = new SlotChange(idSlotMapping._key);
						slotChange.notifySlotCreated(idSlotMapping.slot());
						outSideVisitor.visit(slotChange);
					}
				});
			}
		};
	}

	public void commit() {
	}
}
