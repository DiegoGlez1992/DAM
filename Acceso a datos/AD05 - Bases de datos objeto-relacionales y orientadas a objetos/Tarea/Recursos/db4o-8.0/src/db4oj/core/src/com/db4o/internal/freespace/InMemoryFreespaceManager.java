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
import com.db4o.internal.slots.*;


public class InMemoryFreespaceManager extends AbstractFreespaceManager {
    
	private final TreeIntObject _finder   = new TreeIntObject(0);

    private Tree _freeByAddress;
    
    private Tree _freeBySize;
    
	private FreespaceListener _listener = NullFreespaceListener.INSTANCE;
    
	public InMemoryFreespaceManager(Procedure4<Slot> slotFreedCallback, int discardLimit, int remainderSizeLimit) {
		super(slotFreedCallback, discardLimit, remainderSizeLimit);
	}
	
    private void addFreeSlotNodes(int address, int length) {
        FreeSlotNode addressNode = new FreeSlotNode(address);
        addressNode.createPeer(length);
        _freeByAddress = Tree.add(_freeByAddress, addressNode);
        addToFreeBySize(addressNode._peer);
    }

	private void addToFreeBySize(FreeSlotNode node) {
		_freeBySize = Tree.add(_freeBySize, node);
		_listener.slotAdded(node._key);
	}
	
	public Slot allocateTransactionLogSlot(int length) {
		FreeSlotNode sizeNode = (FreeSlotNode) Tree.last(_freeBySize);
		if(sizeNode == null || sizeNode._key < length){
			return null;
		}

        // We can just be appending to the end of the file, using one
        // really big contigous slot that keeps growing. Let's limit.
        int limit = length + 100; 
        if(sizeNode._key > limit){
            return allocateSlot(limit);
        }
        
		removeFromBothTrees(sizeNode);
		return new Slot(sizeNode._peer._key, sizeNode._key);
	}
    
	public Slot allocateSafeSlot(int length) {
		return allocateSlot(length);
	}
	
	public void freeSafeSlot(Slot slot) {
		if(Debug4.xbytes){
			Procedure4<Slot> temp = _slotFreedCallback;
			_slotFreedCallback = null;
			free(slot);
			_slotFreedCallback = temp;
			return;
		}
		free(slot);
	}

    public void beginCommit() {
        // do nothing
    }
    
	public void commit() {
		// do nothing
	}
    
    public void endCommit() {
        // do nothing
    }
    
    public void free(final Slot slot) {
    	
    	int address = slot.address();
        if (address <= 0) {
        	throw new IllegalArgumentException();
        }
        
        int length = slot.length();
        if(DTrace.enabled){
            DTrace.FREESPACEMANAGER_RAM_FREE.logLength(address, length);
        }
        
        _finder._key = address;
        FreeSlotNode sizeNode;
        FreeSlotNode addressnode = (FreeSlotNode) Tree.findSmaller(_freeByAddress, _finder);
        if ((addressnode != null)
            && ((addressnode._key + addressnode._peer._key) == address)) {
            sizeNode = addressnode._peer;
            removeFromFreeBySize(sizeNode);
            sizeNode._key += length;
            FreeSlotNode secondAddressNode = (FreeSlotNode) Tree
                .findGreaterOrEqual(_freeByAddress, _finder);
            if ((secondAddressNode != null)
                && (address + length == secondAddressNode._key)) {
                sizeNode._key += secondAddressNode._peer._key;
                removeFromBothTrees(secondAddressNode._peer);
            }
            sizeNode.removeChildren();
            addToFreeBySize(sizeNode);
        } else {
            addressnode = (FreeSlotNode) Tree.findGreaterOrEqual(
                _freeByAddress, _finder);
            if ((addressnode != null)
                && (address + length == addressnode._key)) {
                sizeNode = addressnode._peer;
                removeFromBothTrees(sizeNode);
                sizeNode._key += length;
                addressnode._key = address;
                addressnode.removeChildren();
                sizeNode.removeChildren();
                _freeByAddress = Tree.add(_freeByAddress, addressnode);
                addToFreeBySize(sizeNode);
            } else {
                if (canDiscard(length)) {
                    return;
                }
                addFreeSlotNodes(address, length);
            }
        }
        slotFreed(slot);
    }
    
	public void freeSelf() {
        // Do nothing.
        // The RAM manager frees itself on reading.
    }
    
    public Slot allocateSlot(int length) {
    	
        _finder._key = length;
        _finder._object = null;
        _freeBySize = FreeSlotNode.removeGreaterOrEqual((FreeSlotNode) _freeBySize, _finder);

        if (_finder._object == null) {
            return null;
        }
            
        FreeSlotNode node = (FreeSlotNode) _finder._object;
        _listener.slotRemoved(node._key);
        int blocksFound = node._key;
        int address = node._peer._key;
        _freeByAddress = _freeByAddress.removeNode(node._peer);
        int remainingBlocks = blocksFound - length;
    	if(splitRemainder(remainingBlocks)){
    		addFreeSlotNodes(address + length, remainingBlocks);
    	}else{
    		length = blocksFound;
    	}
        
        if(DTrace.enabled){
        	DTrace.FREESPACEMANAGER_GET_SLOT.logLength(address, length);
        }
        
        return new Slot(address, length);
    }
    
    int marshalledLength() {
        return TreeInt.marshalledLength((TreeInt)_freeBySize);
    }

    private void read(ByteArrayBuffer reader) {
        FreeSlotNode.sizeLimit = discardLimit();
        _freeBySize = new TreeReader(reader, new FreeSlotNode(0), true).read();
        final ByRef<Tree> addressTree = ByRef.newInstance();
        if (_freeBySize != null) {
            _freeBySize.traverse(new Visitor4() {
                public void visit(Object a_object) {
                    FreeSlotNode node = ((FreeSlotNode) a_object)._peer;
                    addressTree.value = Tree.add(addressTree.value, node);
                }
            });
        }
        _freeByAddress = addressTree.value;
    }
    
    public void read(LocalObjectContainer container, Slot slot){
        if(Slot.isNull(slot)){
            return;
        }
        ByteArrayBuffer buffer = container.readBufferBySlot(slot);
        if (buffer == null) {
            return;
        }
        read(buffer);
        if(! Debug4.freespace){
		    container.free(slot);
		}
    }
    
    private void removeFromBothTrees(FreeSlotNode sizeNode){
        removeFromFreeBySize(sizeNode);
        _freeByAddress = _freeByAddress.removeNode(sizeNode._peer);
    }

	private void removeFromFreeBySize(FreeSlotNode node) {
		_freeBySize = _freeBySize.removeNode(node);
		_listener.slotRemoved(node._key);
	}
    
    public int slotCount() {
        return Tree.size(_freeByAddress);
    }
    
    public void start(int id) {
        // this is done in read(), nothing to do here
    }
    
    public byte systemType() {
        return FM_RAM;
    }
    
    public String toString(){
        final StringBuffer sb = new StringBuffer();
        sb.append("RAM FreespaceManager\n");
        sb.append("Address Index\n");
        _freeByAddress.traverse(new ToStringVisitor(sb));
        sb.append("Length Index\n");
        _freeBySize.traverse(new ToStringVisitor(sb));
        return sb.toString();
    }
    
    public void traverse(final Visitor4<Slot> visitor) {
        if (_freeByAddress == null) {
            return;
        }
        _freeByAddress.traverse(new Visitor4() {
            public void visit(Object a_object) {
                FreeSlotNode fsn = (FreeSlotNode) a_object;
                int address = fsn._key;
                int length = fsn._peer._key;
                visitor.visit(new Slot(address, length));
            }
        });
    }

    public void write(LocalObjectContainer container){
		Slot slot = container.allocateSlot(marshalledLength());
		while(slot.length() < marshalledLength()){
			// This can happen if DatabaseGrowthSize is configured.
			// Allocating a slot may produce an additional entry
			// in this FreespaceManager.
			container.free(slot);
			slot = container.allocateSlot(marshalledLength());
		}
        ByteArrayBuffer buffer = new ByteArrayBuffer(slot.length());
		TreeInt.write(buffer, (TreeInt)_freeBySize);
		container.writeEncrypt(buffer, slot.address(), 0);
		container.systemData().inMemoryFreespaceSlot(slot);
    }

    final static class ToStringVisitor implements Visitor4 {
		private final StringBuffer _sb;

		ToStringVisitor(StringBuffer sb) {
			_sb = sb;
		}

		public void visit(Object obj) {
		    _sb.append(obj);
		    _sb.append("\n");
		}
	}
    
	public void listener(FreespaceListener listener) {
		_listener = listener;
	}

	public boolean isStarted() {
		return true;
	}


}
