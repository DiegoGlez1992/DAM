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
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class InMemoryIdSystem implements StackableIdSystem {
	
	private final LocalObjectContainer _container;
	
	private IdSlotTree _ids;
	
	private Slot _slot;
	
	private final SequentialIdGenerator _idGenerator;

	private int _childId;
	
	/**
	 * for testing purposes only.
	 */
	public InMemoryIdSystem(LocalObjectContainer container, final int maxValidId){
		_container = container;
		_idGenerator = new SequentialIdGenerator(new Function4<Integer, Integer>() {
			public Integer apply(Integer start) {
				return findFreeId(start, maxValidId);
			}
		}, _container.handlers().lowestValidId(), maxValidId);
	}
	
	public InMemoryIdSystem(LocalObjectContainer container){
		this(container, Integer.MAX_VALUE);
		readThis();
	}

	private void readThis() {
		SystemData systemData = _container.systemData();
		_slot = systemData.idSystemSlot();
		if(! Slot.isNull(_slot)){
			ByteArrayBuffer buffer = _container.readBufferBySlot(_slot);
			_childId = buffer.readInt();
			_idGenerator.read(buffer);
			_ids = (IdSlotTree) new TreeReader(buffer, new IdSlotTree(0, null)).read();
		}
	}

	public void close() {
		// do nothing
	}

	public void commit(Visitable<SlotChange> slotChanges, FreespaceCommitter freespaceCommitter) {
		
		Slot oldSlot = _slot;
		
		Slot reservedSlot = allocateSlot(false, estimatedSlotLength(estimateMappingCount(slotChanges)));
		
		// No more operations against the FreespaceManager.
		// Time to free old slots.
		freespaceCommitter.commit();
		
		slotChanges.accept(new Visitor4<SlotChange>() {
			public void visit(SlotChange slotChange) {
				if(! slotChange.slotModified()){
					return;
				}
				if(slotChange.removeId()){
					_ids = (IdSlotTree) Tree.removeLike(_ids, new TreeInt(slotChange._key));
					return;
				}
				if(DTrace.enabled){
					DTrace.SLOT_COMMITTED.logLength(slotChange._key, slotChange.newSlot());
				}
				_ids = Tree.add(_ids, new IdSlotTree(slotChange._key, slotChange.newSlot()));
			}
		});
		writeThis(reservedSlot);
		
		freeSlot(oldSlot);
		
	}
	
	private Slot allocateSlot(boolean appendToFile, int slotLength) {
    	if(! appendToFile){
    		Slot slot = _container.freespaceManager().allocateSafeSlot(slotLength);
    		if(slot != null){
    			return slot;
    		}
    	}
    	return _container.appendBytes(slotLength);
	}

	private int estimateMappingCount(Visitable<SlotChange> slotChanges) {
		final IntByRef count = new IntByRef(); 
		count.value = _ids == null ? 0 :_ids.size();
		slotChanges.accept(new Visitor4<SlotChange>() {
			
			public void visit(SlotChange slotChange) {
				if(! slotChange.slotModified() || slotChange.removeId()){
					return;
				}
				count.value++;
			}
		});
		return count.value;
	}
	
	private void writeThis(Slot reservedSlot) {
		
		// We need a little dance here to keep filling free slots
		// with X bytes. The FreespaceManager would do it immediately
		// upon the free call, but then our CrashSimulatingTestCase
		// fails because we have the Xses in the file before flushing.
		Slot xByteSlot = null;
		
		if(Debug4.xbytes){
			xByteSlot = _slot;
		}
		int slotLength = slotLength();
		if (reservedSlot.length() >= slotLength){
			_slot = reservedSlot;
			reservedSlot = null;
		} else{
			if(Debug4.xbytes){
				_container.freespaceManager().slotFreed(reservedSlot);
			}
			_slot = allocateSlot(true, slotLength);
		}
		
		ByteArrayBuffer buffer = new ByteArrayBuffer(_slot.length());
		buffer.writeInt(_childId);
		_idGenerator.write(buffer);
		TreeInt.write(buffer, _ids);
		_container.writeBytes(buffer, _slot.address(), 0);
		_container.systemData().idSystemSlot(_slot);
		Runnable commitHook = _container.commitHook();
		
		_container.syncFiles(commitHook);
		
		freeSlot(reservedSlot);
		
		if(Debug4.xbytes){
			if(! Slot.isNull(xByteSlot)){
				_container.freespaceManager().slotFreed(xByteSlot);
			}
		}
	}

	private void freeSlot(Slot slot) {
		if(Slot.isNull(slot)){
			return;
		}
		FreespaceManager freespaceManager = _container.freespaceManager();
		if(freespaceManager == null){
			return;
		}
		freespaceManager.freeSafeSlot(slot);
	}

	private int slotLength() {
		return TreeInt.marshalledLength(_ids) + _idGenerator.marshalledLength() + Const4.ID_LENGTH;
	}
	
	private int estimatedSlotLength(int estimatedCount) {
		IdSlotTree template = _ids;
		if(template == null){
			template = new IdSlotTree(0, new Slot(0, 0));
		}
		return template.marshalledLength(estimatedCount) + _idGenerator.marshalledLength() + Const4.ID_LENGTH;
	}

	public Slot committedSlot(int id) {
		IdSlotTree idSlotMapping = (IdSlotTree) Tree.find(_ids, new TreeInt(id));
		if(idSlotMapping == null){
			throw new InvalidIDException(id);
		}
		return idSlotMapping.slot();
	}

	public void completeInterruptedTransaction(int address,
			int length) {
		// do nothing
	}

	public int newId() {
		int id = _idGenerator.newId();
		_ids = Tree.add(_ids, new IdSlotTree(id, Slot.ZERO));
		return id;
	}

	private int findFreeId(final int start, final int end) {
		if(_ids == null){
			return start;
		}
		final IntByRef lastId = new IntByRef();
		final IntByRef freeId = new IntByRef();
		Tree.traverse(_ids, new TreeInt(start), new CancellableVisitor4<TreeInt>() {
			public boolean visit(TreeInt node) {
				int id = node._key;
				if(lastId.value == 0){
					if( id > start){
						freeId.value = start;
						return false;
					}
					lastId.value = id;
					return true;
				}
				if(id > lastId.value + 1){
					freeId.value = lastId.value + 1;
					return false;
				}
				lastId.value = id;
				return true;
			}
		});
		if(freeId.value > 0){
			return freeId.value;
		}
		if(lastId.value < end){
			return Math.max(start, lastId.value + 1);
		}
		return 0;
	}

	public void returnUnusedIds(Visitable<Integer> visitable) {
		visitable.accept(new Visitor4<Integer>() {
			public void visit(Integer obj) {
				_ids = (IdSlotTree) Tree.removeLike(_ids, new TreeInt(obj));
			}
		});
	}

	public int childId() {
		return _childId;
	}

	public void childId(int id) {
		_childId = id;
	}

	public void traverseOwnSlots(Procedure4<Pair<Integer, Slot>> block) {
		block.apply(Pair.of(0, _slot));
	}

}
