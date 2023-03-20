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
package com.db4o.consistency;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;


public class ConsistencyChecker {

	private final List<SlotDetail> _bogusSlots = new ArrayList<SlotDetail>();
	private final LocalObjectContainer _db;
	private final OverlapMap _overlaps;

	public static void main(String[] args) {
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(args[0]);
		try {
			System.out.println(new ConsistencyChecker(db).checkSlotConsistency());
		}
		finally {
			db.close();
		}
	}
	
	public ConsistencyChecker(ObjectContainer db) {
		_db = (LocalObjectContainer) db;
		_overlaps = new OverlapMap(_db.blockConverter());
	}
	
	public ConsistencyReport checkSlotConsistency() {
		return _db.syncExec(new Closure4<ConsistencyReport>() {
			public ConsistencyReport run() {
				mapIdSystem();
				mapFreespace();
				return new ConsistencyReport(
						_bogusSlots, 
						_overlaps, 
						checkClassIndices(), 
						checkFieldIndices());
			}
		});
	}

	private List<Pair<String,Integer>> checkClassIndices() {
		final List<Pair<String,Integer>> invalidIds = new ArrayList<Pair<String,Integer>>();
		final IdSystem idSystem= _db.idSystem();
		if(!(idSystem instanceof BTreeIdSystem)) {
			return invalidIds;
		}
		ClassMetadataIterator clazzIter = _db.classCollection().iterator();
		while(clazzIter.moveNext()) {
			final ClassMetadata clazz = clazzIter.currentClass();
			if(!clazz.hasClassIndex()) {
				continue;
			}
			BTreeClassIndexStrategy index = (BTreeClassIndexStrategy) clazz.index();
			index.traverseAll(_db.systemTransaction(), new Visitor4<Integer>() {
				public void visit(Integer id) {
					if(!idIsValid(id)) {
						invalidIds.add(new Pair(clazz.getName(), id));
					}
				}
			});
		}
		return invalidIds;
	}
	
	private List<Pair<String, Integer>> checkFieldIndices() {
		final List<Pair<String,Integer>> invalidIds = new ArrayList<Pair<String,Integer>>();
		ClassMetadataIterator clazzIter = _db.classCollection().iterator();
		while(clazzIter.moveNext()) {
			final ClassMetadata clazz = clazzIter.currentClass();
			clazz.traverseDeclaredFields(new Procedure4<FieldMetadata>() {
				public void apply(final FieldMetadata field) {
					if(!field.hasIndex()) {
						return;
					}
					BTree fieldIndex = field.getIndex(_db.systemTransaction());
					fieldIndex.traverseKeys(_db.systemTransaction(), new Visitor4<FieldIndexKey>() {
						public void visit(FieldIndexKey fieldIndexKey) {
							int parentID = fieldIndexKey.parentID();
							if(!idIsValid(parentID)) {
								invalidIds.add(new Pair<String, Integer>(clazz.getName() + "#" + field.getName(), parentID));
							}
						}
					});
				}
			});
		}
		return invalidIds;
	}

	private boolean idIsValid(int id) {
		try {
			return !Slot.isNull(_db.idSystem().committedSlot(id));
		}
		catch(InvalidIDException exc) {
			return false;
		}
	}

	private void mapFreespace() {
		_db.freespaceManager().traverse(new Visitor4<Slot>() {
			public void visit(Slot slot) {
				FreespaceSlotDetail detail = new FreespaceSlotDetail(slot);
				if(isBogusSlot(slot.address(), slot.length())) {
					_bogusSlots.add(detail);
				}
				_overlaps.add(detail);
			}
		});
	}

	private void mapIdSystem() {
		IdSystem idSystem= _db.idSystem();
		if(!(idSystem instanceof BTreeIdSystem)) {
			System.err.println("No btree id system found - not mapping ids.");
			return;
		}
		((BTreeIdSystem)idSystem).traverseIds(new Visitor4<IdSlotMapping>() {
			public void visit(IdSlotMapping mapping) {
				SlotDetail detail = new IdObjectSlotDetail(mapping._id, mapping.slot());
				if(isBogusSlot(mapping._address, mapping._length)) {
					_bogusSlots.add(detail);
				}
				if(mapping._address > 0) {
					_overlaps.add(detail);
				}
			}
		});
		idSystem.traverseOwnSlots(new Procedure4<Pair<Integer, Slot>>() {
			public void apply(Pair<Integer, Slot> idSlot) {
				int id = idSlot.first;
				Slot slot = idSlot.second;
				SlotDetail detail = id > 0 ? (SlotDetail)new IdObjectSlotDetail(id, slot) : (SlotDetail)new RawObjectSlotDetail(slot);
				if(isBogusSlot(idSlot.second.address(), idSlot.second.length())) {
					_bogusSlots.add(detail);
				}
				_overlaps.add(detail);
			}
		});
	}

	private boolean isBogusSlot(int address, int length) {
		return address < 0 || (long)address + length > _db.fileLength();
	}
	
}
