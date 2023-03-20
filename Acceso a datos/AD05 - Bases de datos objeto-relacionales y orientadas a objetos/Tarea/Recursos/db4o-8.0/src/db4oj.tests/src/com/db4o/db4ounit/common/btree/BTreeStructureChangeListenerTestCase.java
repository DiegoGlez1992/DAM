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
package com.db4o.db4ounit.common.btree;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;

import db4ounit.*;

/**
 * @exclude
 */
public class BTreeStructureChangeListenerTestCase extends BTreeTestCaseBase {
	
	public void testSplits(){
		final BooleanByRef splitNotified = new BooleanByRef(); 
		BTreeStructureListener listener = new BTreeStructureListener(){
			public void notifySplit(Transaction trans, BTreeNode originalNode, BTreeNode newRightNode){
				Assert.isFalse(splitNotified.value);
				splitNotified.value = true;
			}
			public void notifyDeleted(Transaction trans, BTreeNode node){
				
			}
			public void notifyCountChanged(Transaction trans, BTreeNode node, int diff){
				
			}
		};
		_btree.structureListener(listener);
		for (int i = 0; i < BTREE_NODE_SIZE + 1; i++) {
			add(i);	
		}
		Assert.isTrue(splitNotified.value);
	}
	
	public void testDelete(){
		final IntByRef deletedCount = new IntByRef(); 
		BTreeStructureListener listener = new BTreeStructureListener(){
			public void notifySplit(Transaction trans, BTreeNode originalNode, BTreeNode newRightNode){
				
			}
			public void notifyDeleted(Transaction trans, BTreeNode node){
				deletedCount.value++;
			}
			public void notifyCountChanged(Transaction trans, BTreeNode node, int diff){
				
			}
		};
		_btree.structureListener(listener);
		for (int i = 0; i < BTREE_NODE_SIZE + 1; i++) {
			add(i);	
		}
		
		for (int i = 0; i < BTREE_NODE_SIZE + 1; i++) {
			remove(i);	
		}
		Assert.areEqual(2, deletedCount.value);
	}
	
	public void testItemCountChanged(){
		final IntByRef changedCount = new IntByRef(); 
		BTreeStructureListener listener = new BTreeStructureListener(){
			public void notifySplit(Transaction trans, BTreeNode originalNode, BTreeNode newRightNode){
				
			}
			public void notifyDeleted(Transaction trans, BTreeNode node){
				
			}
			public void notifyCountChanged(Transaction trans, BTreeNode node, int diff){
				changedCount.value = diff;
			}
		};
		_btree.structureListener(listener);
		changedCount.value = 0;
		add(42);
		Assert.areEqual(1, changedCount.value);
		remove(42);
		Assert.areEqual(-1, changedCount.value);
		changedCount.value = 0;
		remove(42);
		Assert.areEqual(0, changedCount.value);
	}


}
