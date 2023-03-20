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
package com.db4o.internal.freespace;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class BTreeFreespaceManager extends AbstractFreespaceManager {
	
    private final LocalObjectContainer     _file;
	
	private InMemoryFreespaceManager _delegate;
	
	private BTree _slotsByAddress;
	
	private BTree _slotsByLength;
	
	private PersistentIntegerArray _idArray;
    
	private int _delegationRequests;

	private FreespaceListener _listener = NullFreespaceListener.INSTANCE;
	
	private TransactionalIdSystem _idSystem;
	
	public BTreeFreespaceManager(LocalObjectContainer file, Procedure4<Slot> slotFreedCallback,  int discardLimit, int remainderSizeLimit) {
		super(slotFreedCallback, discardLimit, remainderSizeLimit);
		_file = file;
		_delegate = new InMemoryFreespaceManager(slotFreedCallback, discardLimit, remainderSizeLimit);
		_idSystem = file.systemData().freespaceIdSystem();
	}
	
	private void addSlot(Slot slot) {
		_slotsByLength.add(transaction(), slot);
		_slotsByAddress.add(transaction(), slot);
		_listener.slotAdded(slot.length());
	}
	
	public Slot allocateSafeSlot(int length) {
		return _delegate.allocateSafeSlot(length);
	}

	public void beginCommit() {
        beginDelegation();
	}

	private void beginDelegation(){
        _delegationRequests++;
    }
	
	public void commit() {
		_slotsByAddress.commit(transaction());
		_slotsByLength.commit(transaction());
	}

	private void createBTrees(int addressID, int lengthID) {
		BTreeConfiguration config = new BTreeConfiguration(_idSystem, SlotChangeFactory.FREE_SPACE, 64, false);
		_slotsByAddress = new BTree(transaction(), config, addressID, new AddressKeySlotHandler());
		_slotsByLength = new BTree(transaction(), config, lengthID, new LengthKeySlotHandler());
	}
	
	public void endCommit() {
        endDelegation();
	}
	
	private void endDelegation(){
        _delegationRequests--;        
    }

	public void free(Slot slot) {
		if(! isStarted()){
			return;
		}
		if(isDelegating()){
			_delegate.free(slot);
			return;
		}
		try{
            beginDelegation();
	        if(DTrace.enabled){
	            DTrace.FREESPACEMANAGER_BTREE_FREE.logLength(slot.address(), slot.length());
	        }
            Slot remove[] = new Slot[2];
	        Slot newFreeSlot = slot;
	        BTreePointer pointer = searchBTree(_slotsByAddress, slot, SearchTarget.LOWEST);
			BTreePointer previousPointer = pointer != null ? pointer.previous() : _slotsByAddress.lastPointer(transaction()); 
			if(previousPointer != null){
				Slot previousSlot = (Slot) previousPointer.key();
				if(previousSlot.isDirectlyPreceding(newFreeSlot)){
                    remove[0] = previousSlot;
					newFreeSlot = previousSlot.append(newFreeSlot);
				}
			}
			if(pointer != null){
				Slot nextSlot = (Slot) pointer.key();
				if(newFreeSlot.isDirectlyPreceding(nextSlot)){
                    remove[1] = nextSlot;
					newFreeSlot = newFreeSlot.append(nextSlot);
				}
			}
            for (int i = 0; i < remove.length; i++) {
                if(remove[i] != null){
                    removeSlot(remove[i]);   
                }
            }
			if(! canDiscard(newFreeSlot.length())){
				addSlot(newFreeSlot);
			}
			slotFreed(slot);
		} finally{
            endDelegation();
		}
	}

    public void freeSelf() {
        _slotsByAddress.free(transaction());
        _slotsByLength.free(transaction());
	}

	public void freeSafeSlot(Slot slot) {
		_delegate.freeSafeSlot(slot);
	}

	public Slot allocateSlot (int length) {
		if(! isStarted()){
			return null;
		}
		if(isDelegating()){
			return _delegate.allocateSlot(length);
		}
		try{
            beginDelegation();
            BTreePointer pointer = searchBTree(_slotsByLength, new Slot(0, length), SearchTarget.HIGHEST);
			if(pointer == null){
				return null;
			}
			Slot slot = (Slot) pointer.key();
			removeSlot(slot);
			int remainingLength = slot.length() - length;
			if(splitRemainder(remainingLength)){
                addSlot(slot.subSlot(length));
                slot = slot.truncate(length);
			}
	        if(DTrace.enabled){
	        	DTrace.FREESPACEMANAGER_GET_SLOT.logLength(slot.address(), slot.length());
	        }
			return slot;
		} finally{
            endDelegation();
		}
	}

	private void initializeExisting(int id) {
        _idArray = new PersistentIntegerArray(SlotChangeFactory.FREE_SPACE, _idSystem, id);
        _idArray.read(transaction());
        int[] ids = _idArray.array();
        int addressId = ids[0];
        int lengthID = ids[1];
        createBTrees(addressId, lengthID);
        _slotsByAddress.read(transaction());
        _slotsByLength.read(transaction());
        _delegate.read(_file, _file.systemData().inMemoryFreespaceSlot());
    }
	
	private void initializeNew() {
        createBTrees(0 , 0);
        _slotsByAddress.write(transaction());
        _slotsByLength.write(transaction());
        int[] ids = new int[] { _slotsByAddress.getID(), _slotsByLength.getID()};
        _idArray = new PersistentIntegerArray(SlotChangeFactory.FREE_SPACE, _idSystem, ids);
        _idArray.write(transaction());
        _file.systemData().bTreeFreespaceId(_idArray.getID());
    }
	
	private boolean isDelegating(){
		return _delegationRequests > 0;
	}

    public void read(LocalObjectContainer container, int freeSpaceID) {
        // do nothing
    	// reading happens in start( )
	}

	private void removeSlot(Slot slot) {
		_slotsByLength.remove(transaction(), slot);
		_slotsByAddress.remove(transaction(), slot);
		_listener.slotRemoved(slot.length());
	}

	private BTreePointer searchBTree(BTree bTree, Slot slot, SearchTarget target) {
        BTreeNodeSearchResult searchResult = bTree.searchLeaf(transaction(), slot, target);
        return searchResult.firstValidPointer();
    }

	public int slotCount() {
		return _slotsByAddress.size(transaction()) + _delegate.slotCount();
	}

	public void start(int id) {
		try{
            beginDelegation();
			if(id == 0){
				initializeNew();
			}else{
			    initializeExisting(id);
            }
		}finally{
            endDelegation();
		}
	}

	public boolean isStarted(){
		return _idArray != null;
	}

	public byte systemType() {
		return FM_BTREE;
	}
	
	public String toString() {
		return _slotsByLength.toString();
	}
    
    public int totalFreespace() {
        return super.totalFreespace() + _delegate.totalFreespace();
    }
	
    public void traverse(final Visitor4 visitor) {
		_slotsByAddress.traverseKeys(transaction(), visitor);
	}
    
    public void migrateTo(final FreespaceManager fm) {
    	super.migrateTo(fm);
    	_delegate.migrateTo(fm);
    }
    
    public void write(LocalObjectContainer container) {
        try{
            beginDelegation();
            _delegate.write(container);
            container.systemData().bTreeFreespaceId(_idArray.getID());
        }finally{
            endDelegation();
        }
	}

	public void listener(FreespaceListener listener) {
		_listener = listener;
	}
	
    private final LocalTransaction transaction(){
        return (LocalTransaction)_file.systemTransaction();
    }

	public Slot allocateTransactionLogSlot(int length) {
		return _delegate.allocateTransactionLogSlot(length);
	}
	
	public void read(LocalObjectContainer container, Slot slot) {
		// do nothing
		// everything happens in start
	}


    
}
