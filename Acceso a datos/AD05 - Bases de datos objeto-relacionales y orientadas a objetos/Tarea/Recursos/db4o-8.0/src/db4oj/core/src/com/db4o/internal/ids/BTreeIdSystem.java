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

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class BTreeIdSystem implements StackableIdSystem {
	
	private static final int BTREE_ID_INDEX = 0;
	
	private static final int ID_GENERATOR_INDEX = 1;
	
	private static final int CHILD_ID_INDEX = 2;
	
	
	private final LocalObjectContainer _container;
	
	private final StackableIdSystem _parentIdSystem;
	
	private final TransactionalIdSystem _transactionalIdSystem;
	
	private final SequentialIdGenerator _idGenerator;
	
	private BTree _bTree;
	
	private PersistentIntegerArray _persistentState;
	
	public BTreeIdSystem(LocalObjectContainer container, final StackableIdSystem parentIdSystem, int maxValidId) {
		_container = container;
		_parentIdSystem = parentIdSystem;
		_transactionalIdSystem = container.newTransactionalIdSystem(null, new Closure4<IdSystem>() {
			public IdSystem run() {
				return parentIdSystem;
			}
		});
		
		int persistentArrayId = parentIdSystem.childId();
		if(persistentArrayId == 0){
			initializeNew();
		} else {
			initializeExisting(persistentArrayId);
		}
		_idGenerator = new SequentialIdGenerator(new Function4<Integer, Integer>() {
			public Integer apply(Integer start) {
				return findFreeId(start);
			}
		},  idGeneratorValue(), _container.handlers().lowestValidId(), maxValidId);
	}
	
	public BTreeIdSystem(LocalObjectContainer container, StackableIdSystem idSystem){
		this(container, idSystem, Integer.MAX_VALUE);
	}

	private void initializeExisting(int persistentArrayId) {
		_persistentState = new PersistentIntegerArray(SlotChangeFactory.ID_SYSTEM, _transactionalIdSystem, persistentArrayId);
		_persistentState.read(transaction());
		_bTree = new BTree(transaction(), bTreeConfiguration(), bTreeId(), new IdSlotMappingHandler());
	}

	private BTreeConfiguration bTreeConfiguration() {
		return new BTreeConfiguration(_transactionalIdSystem, SlotChangeFactory.ID_SYSTEM, 64, false);
	}

	private int idGeneratorValue() {
		return _persistentState.array()[ID_GENERATOR_INDEX];
	}
	
	private void idGeneratorValue(int value) {
		_persistentState.array()[ID_GENERATOR_INDEX] = value;
	}


	private int bTreeId() {
		return _persistentState.array()[BTREE_ID_INDEX];
	}

	private void initializeNew() {
		_bTree = new BTree(transaction(), bTreeConfiguration(), new IdSlotMappingHandler());
		int idGeneratorValue = _container.handlers().lowestValidId() - 1;
		_persistentState = new PersistentIntegerArray(SlotChangeFactory.ID_SYSTEM, _transactionalIdSystem, 
				new int[]{_bTree.getID(), idGeneratorValue, 0 });
		_persistentState.write(transaction());
		_parentIdSystem.childId(_persistentState.getID());
	}
	
	private int findFreeId(int start) {
		throw new NotImplementedException();
	}

	public void close() {
		
	}

	public Slot committedSlot(int id) {
		IdSlotMapping mapping = (IdSlotMapping) _bTree.search(transaction(), new IdSlotMapping(id, 0, 0));
		if(mapping == null){
			throw new InvalidIDException(id);
		}
		return mapping.slot();
	}

	public void completeInterruptedTransaction(
			int transactionId1, int transactionId2) {
		// do nothing
	}

	public int newId() {
		int id = _idGenerator.newId();
		_bTree.add(transaction(), new IdSlotMapping(id, 0, 0));
		return id;
	}
	
	private Transaction transaction(){
		return _container.systemTransaction();
	}

	public void commit(Visitable<SlotChange> slotChanges, FreespaceCommitter freespaceCommitter) {
		
		_container.freespaceManager().beginCommit();
		slotChanges.accept(new Visitor4<SlotChange>() {
			public void visit(SlotChange slotChange) {
				if(! slotChange.slotModified()){
					return;
				}
				_bTree.remove(transaction(), new IdSlotMapping(slotChange._key, 0, 0));
				if(slotChange.removeId()){
					return;
				}
				
				// TODO: Maybe we want a BTree that doesn't allow duplicates.
				// Then we could do the following in one step without removing first.
				_bTree.add(transaction(), new IdSlotMapping(slotChange._key, slotChange.newSlot()));
				
				if(DTrace.enabled){
					DTrace.SLOT_MAPPED.logLength(slotChange._key, slotChange.newSlot());
				}
				
			}
		});
		_bTree.commit(transaction());
		idGeneratorValue(_idGenerator.persistentGeneratorValue());
		if(_idGenerator.isDirty()){
			_idGenerator.setClean();
			_persistentState.setStateDirty();
			
		}
		if(_persistentState.isDirty()){
			_persistentState.write(transaction());
		}
		_container.freespaceManager().endCommit();
		
		
		_transactionalIdSystem.commit(freespaceCommitter);
		_transactionalIdSystem.clear();
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		visitable.accept(new Visitor4<Integer>() {
			public void visit(Integer id) {
				_bTree.remove(transaction(), new IdSlotMapping(id, 0, 0));			
			}
		});
	}
	
	public static class IdSlotMappingHandler implements Indexable4<IdSlotMapping> {

		public void defragIndexEntry(DefragmentContextImpl context) {
			throw new NotImplementedException();
		}

		public IdSlotMapping readIndexEntry(Context context, ByteArrayBuffer buffer) {
			return IdSlotMapping.read(buffer);
		}

		public void writeIndexEntry(Context context, ByteArrayBuffer buffer,
				IdSlotMapping mapping) {
			mapping.write(buffer);
		}

		public PreparedComparison prepareComparison(Context context, final IdSlotMapping sourceMapping) {
			return new PreparedComparison<IdSlotMapping>() {
				public int compareTo(IdSlotMapping targetMapping) {
					return sourceMapping._id == targetMapping._id ? 
							0 : (sourceMapping._id < targetMapping._id ? - 1 : 1); 
				}
			};
		}

		public final int linkLength() {
			return Const4.INT_LENGTH * 3;
		}

	}

	public TransactionalIdSystem freespaceIdSystem() {
		return _transactionalIdSystem;
	}

	public int childId() {
		return _persistentState.array()[CHILD_ID_INDEX];
	}

	public void childId(int id) {
		_persistentState.array()[CHILD_ID_INDEX] = id;
		_persistentState.setStateDirty();
	}

	public void traverseIds(Visitor4<IdSlotMapping> visitor) {
		_bTree.traverseKeys(_container.systemTransaction(), visitor);
	}

	public void traverseOwnSlots(Procedure4<Pair<Integer, Slot>> block) {
		_parentIdSystem.traverseOwnSlots(block);
		block.apply(ownSlotInfo(_persistentState.getID()));
		block.apply(ownSlotInfo(_bTree.getID()));
		Iterator4<Integer> nodeIds = _bTree.allNodeIds(_container.systemTransaction());
		while(nodeIds.moveNext()) {
			block.apply(ownSlotInfo(nodeIds.current()));
		}
	}
	
	private Pair<Integer, Slot> ownSlotInfo(int id) {
		return Pair.of(id, _parentIdSystem.committedSlot(id));
	}
}
