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
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.fieldindex.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class FieldIndexProcessorTestCase extends FieldIndexProcessorTestCaseBase {
	
	public static void main(String[] args) {
		new FieldIndexProcessorTestCase().runAll();
	}
	
	protected void configure(Configuration config) {
		super.configure(config);
		indexField(config,NonIndexedFieldIndexItem.class, "indexed");
	}
	
	protected void store() {
		container().produceClassMetadata(reflectClass(NonIndexedFieldIndexItem.class));
		storeItems(new int[] { 3, 4, 7, 9 });
		storeComplexItems(
						new int[] { 3, 4, 7, 9 },
						new int[] { 2, 2, 8, 8 });
	}
    
    public void testIdentity(){
        Query query = createComplexItemQuery();
        query.descend("foo").constrain(new Integer(3));
        ComplexFieldIndexItem item = (ComplexFieldIndexItem) query.execute().next();
        
        query = createComplexItemQuery();
        query.descend("child").constrain(item).identity();
        assertExpectedFoos(ComplexFieldIndexItem.class, new int[]{4}, query);
    }

    public void testSingleIndexNotSmaller(){
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(5)).smaller().not();     
        assertExpectedFoos(FieldIndexItem.class, new int[]{7, 9}, query);
    }
    
    public void testSingleIndexNotGreater(){
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(4)).greater().not();     
        assertExpectedFoos(FieldIndexItem.class, new int[]{3, 4}, query);
    }
    
    public void testSingleIndexSmallerOrEqual() {
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(7)).smaller().equal();
        assertExpectedFoos(FieldIndexItem.class, new int[] { 3,4,7 }, query);
    }

    public void testSingleIndexGreaterOrEqual() {
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(7)).greater().equal();
        assertExpectedFoos(FieldIndexItem.class, new int[] { 7, 9 }, query);
    }
    
    public void testSingleIndexRange(){
        final Query query = createItemQuery();
        query.descend("foo").constrain(new Integer(3)).greater();
        query.descend("foo").constrain(new Integer(9)).smaller();
        assertExpectedFoos(FieldIndexItem.class, new int[] { 4, 7 }, query);
    }
    
    public void testSingleIndexAndRange(){
        final Query query = createItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(3)).greater();
        Constraint c2 = query.descend("foo").constrain(new Integer(9)).smaller();
        c1.and(c2);
        assertExpectedFoos(FieldIndexItem.class, new int[] { 4, 7 }, query);
    }
    
    public void testSingleIndexOr(){
        final Query query = createItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(4)).smaller();
        Constraint c2 = query.descend("foo").constrain(new Integer(7)).greater();
        c1.or(c2);
        assertExpectedFoos(FieldIndexItem.class, new int[] { 3, 9 }, query);
    }    
    
    public void testExplicitAndOverOr() {
    	assertAndOverOrQuery(true);
    }
    
    public void testImplicitAndOverOr() {
    	assertAndOverOrQuery(false);
    }

    public void testSingleIndexOrRange() {
    	Query query = createItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(1)).greater();
        Constraint c2 = query.descend("foo").constrain(new Integer(4)).smaller();
        Constraint c3 = query.descend("foo").constrain(new Integer(4)).greater();
        Constraint c4 = query.descend("foo").constrain(new Integer(10)).smaller();
        Constraint cc1 = c1.and(c2);
        Constraint cc2 = c3.and(c4);
        cc1.or(cc2);
        assertExpectedFoos(FieldIndexItem.class, new int[] { 3, 7, 9 }, query);
    }
    
    public void testImplicitAndOnOrs() {
    	Query query = createItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(4)).smaller();
        Constraint c2 = query.descend("foo").constrain(new Integer(3)).greater();
        Constraint c3 = query.descend("foo").constrain(new Integer(4)).greater();
        c1.or(c2);
        c1.or(c3);
        
        assertExpectedFoos(FieldIndexItem.class, new int[] { 3, 4, 7, 9 }, query);
    }
    
    public void testTwoLevelDescendOr() {
    	Query query = createComplexItemQuery();
        Constraint c1 = query.descend("child").descend("foo").constrain(new Integer(4)).smaller();
        Constraint c2 = query.descend("child").descend("foo").constrain(new Integer(4)).greater();        
        c1.or(c2);
        assertExpectedFoos(ComplexFieldIndexItem.class, new int[] { 4, 9 }, query);
    }
    
    public void testThreeOrs(){
    	Query query = createItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(3));
        Constraint c2 = query.descend("foo").constrain(new Integer(4));
        Constraint c3 = query.descend("foo").constrain(new Integer(7));
        c1.or(c2).or(c3);
        assertExpectedFoos(FieldIndexItem.class, new int[] { 3, 4, 7}, query);
    }
    
    public void _testOrOnDifferentFields(){
        final Query query = createComplexItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(3));
        Constraint c2 = query.descend("bar").constrain(new Integer(8));
        c1.or(c2);
        assertExpectedFoos(ComplexFieldIndexItem.class, new int[] { 3, 7, 9 }, query);
    }
    
    public void testCantOptimizeOrInvolvingNonIndexedField() {
    	final Query query = createQuery(NonIndexedFieldIndexItem.class);
    	final Constraint c1 = query.descend("indexed").constrain(new Integer(1));
    	final Constraint c2 = query.descend("foo").constrain(new Integer(2));
    	c1.or(c2);
    	assertCantOptimize(query);
    }
    
    public void testCantOptimizeDifferentLevels(){
        final Query query = createComplexItemQuery();
        Constraint c1 = query.descend("child").descend("foo").constrain(new Integer(4)).smaller();
        Constraint c2 = query.descend("foo").constrain(new Integer(7)).greater();
        c1.or(c2);
        assertCantOptimize(query);
    }
    
    public void testCantOptimizeJoinOnNonIndexedFields() {
    	final Query query = createQuery(NonIndexedFieldIndexItem.class);
    	final Constraint c1 = query.descend("foo").constrain(new Integer(1));
    	final Constraint c2 = query.descend("foo").constrain(new Integer(2));
    	c1.or(c2);
    	assertCantOptimize(query);
    }
	
	public void testIndexSelection() {		
		Query query = createComplexItemQuery();		
		query.descend("bar").constrain(new Integer(2));
		query.descend("foo").constrain(new Integer(3));
		
		assertBestIndex("foo", query);
		
		query = createComplexItemQuery();
		query.descend("foo").constrain(new Integer(3));
		query.descend("bar").constrain(new Integer(2));
		
		assertBestIndex("foo", query);
	}

	public void testDoubleDescendingOnQuery() {
		final Query query = createComplexItemQuery();
		query.descend("child").descend("foo").constrain(new Integer(3));
		assertExpectedFoos(ComplexFieldIndexItem.class, new int[] { 4 }, query);
	}
	
	public void testTripleDescendingOnQuery() {
		final Query query = createComplexItemQuery();
		query.descend("child").descend("child").descend("foo").constrain(new Integer(3));
		assertExpectedFoos(ComplexFieldIndexItem.class, new int[] { 7 }, query);
	}

	public void testMultiTransactionSmallerWithCommit() {
		final Transaction transaction = newTransaction();
		fillTransactionWith(transaction, 0);
		
		int[] expectedZeros = newBTreeNodeSizedArray(0);
		assertSmaller(transaction, expectedZeros, 3);
		
		transaction.commit();
		
		fillTransactionWith(transaction, 5);
        assertSmaller(IntArrays4.concat(expectedZeros, new int[] { 3, 4 }), 7);
	}

	public void testMultiTransactionWithRollback() {
		final Transaction transaction = newTransaction();
		fillTransactionWith(transaction, 0);
		
		int[] expectedZeros = newBTreeNodeSizedArray(0);
		assertSmaller(transaction, expectedZeros, 3);
		
		transaction.rollback();
		
		assertSmaller(transaction, new int[0], 3);
		
		fillTransactionWith(transaction, 5);
        assertSmaller(new int[] { 3, 4 }, 7);
	}
	
	public void testMultiTransactionSmaller() {
		final Transaction transaction = newTransaction();
		fillTransactionWith(transaction, 0);
		
		int[] expected = newBTreeNodeSizedArray(0);
		assertSmaller(transaction, expected, 3);
		
		fillTransactionWith(transaction, 5);
        assertSmaller(new int[] { 3, 4 }, 7);
	}

    public void testMultiTransactionGreater() {
        fillTransactionWith(systemTrans(), 10);
        fillTransactionWith(systemTrans(), 5);      
        assertGreater(new int[] { 4, 7, 9 }, 3);
        removeFromTransaction(systemTrans(), 5);
        assertGreater(new int[] { 4, 7, 9 }, 3);
        removeFromTransaction(systemTrans(), 10);
        assertGreater(new int[] { 4, 7, 9 }, 3);
    }
	
    public void testSingleIndexEquals() {
        final int expectedBar = 3;
        assertExpectedFoos(FieldIndexItem.class, new int[] { expectedBar }, createQuery(expectedBar));
    }
    
	public void testSingleIndexSmaller() {
		assertSmaller(new int[] { 3, 4 }, 7);
	}

	public void testSingleIndexGreater() {
		assertGreater(new int[] { 4, 7, 9 }, 3);
	}
	
	private void assertCantOptimize(Query query) {
		final FieldIndexProcessorResult result = executeProcessor(query);
		Assert.areSame(FieldIndexProcessorResult.NO_INDEX_FOUND,  result);
	}

	private void assertBestIndex(String expectedFieldIndex, final Query query) {
		IndexedNode node = selectBestIndex(query);
		assertComplexItemIndex(expectedFieldIndex, node);
	}

	private void assertAndOverOrQuery(boolean explicitAnd) {
		Query query = createItemQuery();
        Constraint c1 = query.descend("foo").constrain(new Integer(3));
        Constraint c2 = query.descend("foo").constrain(new Integer(9));
        Constraint c3 = query.descend("foo").constrain(new Integer(3));
        Constraint c4 = query.descend("foo").constrain(new Integer(7));
        Constraint cc1 = c1.or(c2);
        Constraint cc2 = c3.or(c4);
        if (explicitAnd) {
        	cc1.and(cc2);
        }
        assertExpectedFoos(FieldIndexItem.class, new int[] { 3 }, query);
	}

	private void assertGreater(int[] expectedFoos, int greaterThan) {
		final Query query = createItemQuery();
		query.descend("foo").constrain(new Integer(greaterThan)).greater();		
		assertExpectedFoos(FieldIndexItem.class, expectedFoos, query);
	}
	
	private void assertExpectedFoos(Class itemClass, final int[] expectedFoos, final Query query) {
		final Transaction trans = transactionFromQuery(query);
		final int[] expectedIds = mapToObjectIds(createQuery(trans, itemClass), expectedFoos);
		assertExpectedIDs(expectedIds, query);
	}
	
	private void assertExpectedIDs(final int[] expectedIds, final Query query) {
		final FieldIndexProcessorResult result = executeProcessor(query);		
		if (expectedIds.length == 0) {
			Assert.areSame(FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH, result);
			return;
		}
				 
		assertTreeInt(expectedIds, result.toTreeInt());
	}

	private FieldIndexProcessorResult executeProcessor(final Query query) {
		return createProcessor(query).run();
	}

	private BTree btree(){
        return fieldIndexBTree(FieldIndexItem.class, "foo");
    }

	private void store(final Transaction trans, final FieldIndexItem item) {
		container().store(trans, item);
	}
	
	private void fillTransactionWith(Transaction trans, final int bar) {
		for (int i=0; i<BTreeAssert.fillSize(btree()); ++i) {
			store(trans, new FieldIndexItem(bar));
		}
	}

	private int[] newBTreeNodeSizedArray(int value) {
		final BTree btree = btree();
		return BTreeAssert.newBTreeNodeSizedArray(btree, value);
	}

	private void removeFromTransaction(Transaction trans, final int foo) {
		final ObjectSet found = createItemQuery(trans).execute();
		while (found.hasNext()) {
			FieldIndexItem item = (FieldIndexItem)found.next();
			if (item.foo == foo) {
				container().delete(trans, item);
			}
		}
	}
	
	private void assertSmaller(final int[] expectedFoos, final int smallerThan) {
		assertSmaller(trans(), expectedFoos, smallerThan);
	}

	private void assertSmaller(final Transaction transaction, final int[] expectedFoos, final int smallerThan) {
		final Query query = createItemQuery(transaction);
		query.descend("foo").constrain(new Integer(smallerThan)).smaller();
		assertExpectedFoos(FieldIndexItem.class, expectedFoos, query);
	}

}
