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
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

public class IdSlotChanges {
	
    private final LockedTree _slotChanges = new LockedTree();
    
    private final TransactionalIdSystemImpl _idSystem;
	
	private final Closure4<FreespaceManager> _freespaceManager;
	
	private TreeInt _prefetchedIDs;

	public IdSlotChanges(TransactionalIdSystemImpl idSystem, Closure4<FreespaceManager> freespaceManager) {
		_idSystem = idSystem;
		_freespaceManager = freespaceManager;
	}

	public final void accumulateFreeSlots(final FreespaceCommitter freespaceCommitter, final boolean forFreespace, boolean traverseMutable) {
        Visitor4 visitor = new Visitor4() {
            public void visit(Object obj) {
                ((SlotChange)obj).accumulateFreeSlot(_idSystem, freespaceCommitter, forFreespace);
            }
        };
        if(traverseMutable){
            _slotChanges.traverseMutable(visitor);
        } else {
        	_slotChanges.traverseLocked(visitor);
        }
    }
	
	public void clear() {
		_slotChanges.clear();
	}	
    
	public void rollback() {
		_slotChanges.traverseLocked(new Visitor4() {
            public void visit(Object slotChange) {
                ((SlotChange) slotChange).rollback(freespaceManager());
            }
        });
	}

	public boolean isDeleted(int id) {
    	SlotChange slot = findSlotChange(id);
		if (slot == null) {
			return false;
		}
		return slot.isDeleted();
    }
	
    public SlotChange produceSlotChange(int id, SlotChangeFactory slotChangeFactory){
    	if(DTrace.enabled){
    		DTrace.PRODUCE_SLOT_CHANGE.log(id);
    	}
        SlotChange slot = slotChangeFactory.newInstance(id);
        _slotChanges.add(slot);
        return (SlotChange)slot.addedOrExisting();
    }    

    public final SlotChange findSlotChange(int id) {
        return (SlotChange)_slotChanges.find(id);
    }    

    public void traverseSlotChanges(Visitor4<SlotChange> visitor){
        _slotChanges.traverseLocked(visitor);
	}
	
	public boolean isDirty() {
		return ! _slotChanges.isEmpty();
	}
	
	public void readSlotChanges(ByteArrayBuffer buffer) {
		_slotChanges.read(buffer, new SlotChange(0));
	}

	public void addPrefetchedID(int id) {
		_prefetchedIDs = Tree.add(_prefetchedIDs, new TreeInt(id));		
	}
	
	public void prefetchedIDConsumed(int id) {
        _prefetchedIDs = _prefetchedIDs.removeLike(new TreeInt(id));
	}
	
    final void freePrefetchedIDs(IdSystem idSystem) {
        if (_prefetchedIDs == null) {
        	return;
        }
        idSystem.returnUnusedIds(_prefetchedIDs);
        _prefetchedIDs = null;
    }

	public void notifySlotCreated(int id, Slot slot, SlotChangeFactory slotChangeFactory) {
		produceSlotChange(id, slotChangeFactory).notifySlotCreated(slot);
	}
	
	void notifySlotUpdated(int id, Slot slot,  SlotChangeFactory slotChangeFactory) {
        produceSlotChange(id, slotChangeFactory).notifySlotUpdated(freespaceManager(), slot);
	}
	
	public void notifySlotDeleted(int id, SlotChangeFactory slotChangeFactory) {
		produceSlotChange(id, slotChangeFactory).notifyDeleted(freespaceManager());
	}
	
	private FreespaceManager freespaceManager() {
		return _freespaceManager.run();
	}


}
