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

import com.db4o.internal.*;
import com.db4o.internal.btree.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class BTreeAddRemoveTestCase extends BTreeTestCaseBase {
	
	public void testFirstPointerMultiTransactional(){
		int count = BTREE_NODE_SIZE + 1;
		for (int i = 0; i < count; i++) {
			add(count + i + 1);
		}
		int smallest = count + 1;
		Transaction trans = newTransaction();
		for (int i = 0; i < count; i++) {
			add(trans, i);	
		}
		final BTreePointer firstPointer = _btree.firstPointer(trans());
		assertPointerKey(smallest, firstPointer);
	}
	
	public void testSingleRemoveAdd() {
		
		final int element = 1;
		add(element);		
		assertSize(1);
		
		remove(element);		
		assertSize(0);
		
		add(element);
		
		assertSingleElement(element);
	}
	
	public void testSearchingRemoved() {
		final int[] keys = new int[] { 3, 4, 7, 9 };
		add(keys);
		remove(4);
		final BTreeRange result = search(4);
		Assert.isTrue(result.isEmpty());
		
		final BTreeRange range = result.greater();
		BTreeAssert.assertRange(new int[] { 7, 9 }, range);
	}

	public void testMultipleRemoveAdds() {
		
		final int element = 1;
		
		add(element);
		remove(element);
		remove(element);
		add(element);
		
		assertSingleElement(element);
	}
	
	public void testMultiTransactionCancelledRemoval() {
		final int element = 1;
		add(element);
		commit();
		
		final Transaction trans1 = newTransaction();
		final Transaction trans2 = newTransaction();
		
		remove(trans1, element);
		assertSingleElement(trans2, element);
		add(trans1, element);
		assertSingleElement(trans1, element);
		assertSingleElement(trans2, element);
		
		trans1.commit();
		assertSingleElement(element);
	}
	
	public void testMultiTransactionSearch() {
		
		final int[] keys = new int[] { 3, 4, 7, 9 };
		add(trans(), keys);
		commit(trans());
		
        final int[] assorted = new int[] { 1, 2, 11, 13, 21, 52, 51, 66, 89, 10 };
		add(systemTrans(), assorted);
		assertKeys(keys);
		
        remove(systemTrans(), assorted);
        assertKeys(keys);
        
        BTreeAssert.assertRange(new int[] { 7, 9 }, search(trans(), 4).greater());
	}

	private void assertKeys(final int[] keys) {
		BTreeAssert.assertKeys(trans(), _btree, keys);
	}

	public void testAddRemoveInDifferentTransactions() {
		
		final int element = 1;
		
		add(trans(), element);
		add(systemTrans(), element);
		
		remove(systemTrans(), element);
		remove(trans(), element);
		
		assertEmpty(systemTrans());
		assertEmpty(trans());
		
        _btree.commit(systemTrans());
		_btree.commit(trans());
		
        assertEmpty(systemTrans());
        assertEmpty(trans());
	}
	
    public void testRemoveCommitInDifferentTransactions() {
        
        final int element = 1;
        
        add(trans(), element);
        _btree.commit(trans());
        
        remove(systemTrans(), element);
        remove(trans(), element);
        
        assertEmpty(systemTrans());
        assertEmpty(trans());
        
        _btree.commit(systemTrans());
        _btree.commit(trans());
        
        assertEmpty(systemTrans());
        assertEmpty(trans());
    }
	
	
	public void testRemoveAddInDifferentTransactions() {
		final int element = 1;
		
		add(element);
		
		db().commit();
		
		remove(trans(), element);
		remove(systemTrans(), element);
		
		assertEmpty(systemTrans());
		assertEmpty(trans());
		
		add(trans(), element);
		assertSingleElement(trans(), element);
		
		add(systemTrans(), element);
		assertSingleElement(systemTrans(), element);
	}
	
	public void testAddAddRollbackCommmitInDifferentTransactions() {
		final int element = 1;

		add(trans(),element);
		add(systemTrans(), element);
		db().rollback();

		assertSingleElement(systemTrans(), element);
		
		db().commit();
		
		assertSingleElement(trans(), element);
		assertSingleElement(systemTrans(), element);
	}
    
    public void testMultipleConcurrentRemoves(){
        int count = 100;
        for (int i = 0; i < count; i++) {
            add(trans(), i);
        }
        db().commit();
        Transaction secondTransaction = newTransaction();
        for (int i = 1; i < count; i++) {
            if(i % 2 == 0){
                remove(trans(), i);
            }else{
                remove(secondTransaction, i);
            }
        }
        secondTransaction.commit();
        db().commit();
        assertSize(1);
    }
	
	public static void main(String[] args) {
		new BTreeAddRemoveTestCase().runSolo();
	}
}
