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
package com.db4o.internal.transactionlog;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public abstract class TransactionLogHandler {
	
	protected final LocalObjectContainer _container;
	
	protected TransactionLogHandler(LocalObjectContainer container){
		_container = container;
	}
	
	protected LocalObjectContainer localContainer() {
		return _container;
	}
	
    protected final void flushDatabaseFile() {
		_container.syncFiles();
	}
    
	protected final void appendSlotChanges(final ByteArrayBuffer writer, Visitable slotChangeVisitable){
		slotChangeVisitable.accept(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).write(writer);
			}
		});
    }
	
    protected boolean writeSlots(Visitable<SlotChange> slotChangeTree) {
        final BooleanByRef ret = new BooleanByRef();
        slotChangeTree.accept(new Visitor4() {
			public void visit(Object obj) {
				((SlotChange)obj).writePointer(_container);
				ret.value = true;
			}
		});
        return ret.value;
    }
    
	protected final int transactionLogSlotLength(int slotChangeCount){
    	// slotchanges * 3 for ID, address, length
    	// 2 ints for slotlength and count
    	return ((slotChangeCount * 3) + 2) * Const4.INT_LENGTH;
    }

	public abstract Slot allocateSlot(boolean append, int slotChangeCount);

	public abstract void applySlotChanges(Visitable<SlotChange> slotChangeTree, int slotChangeCount, Slot reservedSlot);

	public abstract void completeInterruptedTransaction(int transactionId1, int transactionId2);

	public abstract void close();
	
	protected void readWriteSlotChanges(ByteArrayBuffer buffer) {
		final LockedTree slotChanges = new LockedTree();
		slotChanges.read(buffer, new SlotChange(0));
		if(writeSlots(new Visitable<SlotChange>() {
			public void accept(Visitor4<SlotChange> visitor) {
				slotChanges.traverseMutable(visitor);
			}
		})){
			flushDatabaseFile();
		}
	}
	

}
