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

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;


/**
 * @exclude
 */
public class SlotChange extends TreeInt {
	
	private static class SlotChangeOperation {
		
		private final String _type;
		
		public SlotChangeOperation(String type) {
			_type = type;
		}

		static final SlotChangeOperation create = new SlotChangeOperation("create");
		
		static final SlotChangeOperation update = new SlotChangeOperation("update");
		
		static final SlotChangeOperation delete = new SlotChangeOperation("delete");
		
		@Override
		public String toString() {
			return _type;
		}
		
	}
	
	private SlotChangeOperation _firstOperation;
	
	private SlotChangeOperation _currentOperation;
	
	protected Slot _newSlot;
    
	public SlotChange(int id) {
		super(id);
	}
	
	public Object shallowClone() {
		SlotChange sc = new SlotChange(0);
		sc.newSlot(_newSlot);
		return super.shallowCloneInternal(sc);
	}
	
	public void accumulateFreeSlot(TransactionalIdSystemImpl idSystem, FreespaceCommitter freespaceCommitter, boolean forFreespace) {
        if( forFreespace() != forFreespace){
        	return;
        }
    	if(_firstOperation == SlotChangeOperation.create){
    		return;
    	}
		if(_currentOperation == SlotChangeOperation.update || _currentOperation == SlotChangeOperation.delete){
			
			Slot slot = modifiedSlotInParentIdSystem(idSystem);
			if(Slot.isNull(slot)){
				slot = idSystem.committedSlot(_key);
			}
			
			// No old slot at all can be the case if the object
			// has been deleted by another transaction and we add it again.
			if(! Slot.isNull(slot)){
				freespaceCommitter.delayedFree(slot, freeToSystemFreespaceSystem());
			}
		}
	}
	
	protected boolean forFreespace() {
		return false;
	}

	protected Slot modifiedSlotInParentIdSystem(TransactionalIdSystemImpl idSystem) {
		return idSystem.modifiedSlotInParentIdSystem(_key);
	}
	
	public boolean isDeleted() {
		return slotModified() && _newSlot.isNull();
	}
	
	public boolean isNew() {
		return _firstOperation == SlotChangeOperation.create;
	}
    
	private final boolean isFreeOnRollback() {
		return ! Slot.isNull(_newSlot);
	}

	public final boolean slotModified() {
		return _newSlot != null;
	}
	
	/**
	 * FIXME:	Check where pointers should be freed on commit.
	 * 			This should be triggered in this class.
	 */
//	private final boolean isFreePointerOnCommit() {
//		return isBitSet(FREE_POINTER_ON_COMMIT_BIT);
//	}

	public Slot newSlot() {
		return _newSlot;
	}
    
	public Object read(ByteArrayBuffer reader) {
		SlotChange change = new SlotChange(reader.readInt());
		Slot newSlot = new Slot(reader.readInt(), reader.readInt());
		change.newSlot(newSlot);
		return change;
	}

	public void rollback(FreespaceManager freespaceManager) {
		if (isFreeOnRollback()) {
			freespaceManager.free(_newSlot);
		}
	}

	public void write(ByteArrayBuffer writer) {
		if (slotModified()) {
			writer.writeInt(_key);
			writer.writeInt(_newSlot.address());
			writer.writeInt(_newSlot.length());
		} 
	}

	public final void writePointer(LocalObjectContainer container) {
		if (slotModified()) {
			container.writePointer(_key, _newSlot);
		}
	}
    
    private void newSlot(Slot slot){
    	_newSlot = slot;
    }

	public void notifySlotUpdated(FreespaceManager freespaceManager, Slot slot) {
		if(DTrace.enabled){
			DTrace.NOTIFY_SLOT_UPDATED.logLength(_key, slot);
		}
		freePreviouslyModifiedSlot(freespaceManager);
		_newSlot = slot;
		operation(SlotChangeOperation.update);
	}

	protected void freePreviouslyModifiedSlot(FreespaceManager freespaceManager) {
		if(Slot.isNull(_newSlot)){
			return;
		}
		free(freespaceManager, _newSlot);
		_newSlot = null;
	}

	protected void free(FreespaceManager freespaceManager, Slot slot) {
		if(slot.isNull()){
			return;
		}
		if(freespaceManager == null){
			return;
		}
		freespaceManager.free(slot);
	}

	private void operation(SlotChangeOperation operation) {
		if(_firstOperation == null){
			_firstOperation = operation;
		}
		_currentOperation = operation;
	}

	public void notifySlotCreated(Slot slot) {
		if(DTrace.enabled){
			DTrace.NOTIFY_SLOT_CREATED.log(_key);
			DTrace.NOTIFY_SLOT_CREATED.logLength(slot);
		}
		operation(SlotChangeOperation.create);
		_newSlot = slot;
	}

	public void notifyDeleted(FreespaceManager freespaceManager) {
		if(DTrace.enabled){
			DTrace.NOTIFY_SLOT_DELETED.log(_key);
		}
		operation(SlotChangeOperation.delete);
		freePreviouslyModifiedSlot(freespaceManager);
		_newSlot = Slot.ZERO;
	}
	
	public boolean removeId(){
		return false;
	}
	
	@Override
	public String toString() {
		String str = "id: " + _key;
		if(_newSlot != null){
			str += " newSlot: " + _newSlot; 
		}
		return str; 
	}
	
	protected boolean freeToSystemFreespaceSystem(){
		return false;
	}
    
}
