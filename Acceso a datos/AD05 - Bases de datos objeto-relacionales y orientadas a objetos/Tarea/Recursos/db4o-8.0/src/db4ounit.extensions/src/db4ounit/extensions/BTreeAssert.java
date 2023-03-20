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
package db4ounit.extensions;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

public class BTreeAssert {

	public static void traverseKeys(BTreeRange result, Visitor4 visitor) {
		final Iterator4 i = result.keys();
		while (i.moveNext()) {
			visitor.visit(i.current());
		}
	}

	public static void assertKeys(final Transaction transaction, BTree btree, final int[] keys) {
		final ExpectingVisitor visitor = ExpectingVisitor.createExpectingVisitor(keys);
		btree.traverseKeys(transaction, visitor);
		visitor.assertExpectations();
	}

	public static void assertEmpty(Transaction transaction, BTree tree) {
	    final ExpectingVisitor visitor = new ExpectingVisitor(new Object[0]);
	    tree.traverseKeys(transaction, visitor);
		visitor.assertExpectations();
	    Assert.areEqual(0, tree.size(transaction));
	}

	public static void dumpKeys(Transaction trans, BTree tree) {
		tree.traverseKeys(trans, new Visitor4() {
			public void visit(Object obj) {
				System.out.println(obj);
			}
		});
	}

	public static int fillSize(BTree btree) {
		return btree.nodeSize()+1;
	}

	public static int[] newBTreeNodeSizedArray(final BTree btree, int value) {
		return IntArrays4.fill(new int[fillSize(btree)], value);
	}

	public static void assertRange(int[] expectedKeys, BTreeRange range) {
		Assert.isNotNull(range);
		final ExpectingVisitor visitor = ExpectingVisitor.createSortedExpectingVisitor(expectedKeys);
		
		traverseKeys(range, visitor);
		visitor.assertExpectations();
	}

	public static BTree createIntKeyBTree(final ObjectContainerBase container, int id, int nodeSize) {
		return new BTree(container.systemTransaction(), id, new IntHandler(), nodeSize);
	}

	public static void assertSingleElement(Transaction trans, BTree btree, Object element) {
		Assert.areEqual(1, btree.size(trans));
		
		final BTreeRange result = btree.searchRange(trans, element);
		ExpectingVisitor expectingVisitor = new ExpectingVisitor(new Object[] { element });
		BTreeAssert.traverseKeys(result, expectingVisitor);
		expectingVisitor.assertExpectations();
		
		expectingVisitor = new ExpectingVisitor(new Object[] { element });
		btree.traverseKeys(trans, expectingVisitor);
		expectingVisitor.assertExpectations();
	}
	
	public static void assertAllSlotsFreed(LocalTransaction trans, BTree bTree, CodeBlock block) throws Throwable {
		
		final LocalObjectContainer container = (LocalObjectContainer)trans.container();
		TransactionalIdSystem idSystem = trans.idSystem();
		
		Iterator4 allSlotIDs = bTree.allNodeIds(trans.systemTransaction());
        
        Collection4 allSlots = new Collection4();
        
        while(allSlotIDs.moveNext()){
            int slotID = ((Integer)allSlotIDs.current()).intValue();
			Slot slot = idSystem.currentSlot(slotID);
            allSlots.add(slot);
        }
        
        Slot bTreeSlot = idSystem.currentSlot(bTree.getID());
        allSlots.add(bTreeSlot);
        
        final Collection4 freedSlots = new Collection4();
        
        FreespaceManager freespaceManager = container.freespaceManager();
        container.installDebugFreespaceManager(
            new FreespaceManagerForDebug(new SlotListener() {
                public void onFree(Slot slot) {
                    freedSlots.add(slot);
                }
        }));
        
        block.run();

        container.installDebugFreespaceManager(freespaceManager);
        
        Assert.isTrue(freedSlots.containsAll(allSlots.iterator()));
	}


}
