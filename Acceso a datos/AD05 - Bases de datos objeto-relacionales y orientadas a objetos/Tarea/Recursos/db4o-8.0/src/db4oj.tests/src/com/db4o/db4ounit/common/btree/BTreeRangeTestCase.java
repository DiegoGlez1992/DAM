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

import com.db4o.internal.btree.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class BTreeRangeTestCase extends BTreeTestCaseBase {
	
	public static void main(String[] args) {
		new BTreeRangeTestCase().runSolo();
	}
	
	protected void db4oSetupAfterStore() throws Exception {
		super.db4oSetupAfterStore();
		add(new int[] { 3, 7, 4, 9 });
	}
	
	public void testLastPointer(){
		assertLastPointer(8, 7);
		assertLastPointer(11, 9);
		assertLastPointer(4, 3);
	}

	private void assertLastPointer(final int searchValue, final int expectedValue) {
		BTreeRange single = search(searchValue);
		BTreeRange smallerRange = single.smaller();
		BTreePointer lastPointer = smallerRange.lastPointer();
		Assert.areEqual(new Integer(expectedValue), lastPointer.key());
	}
    
    public void testSize(){
        assertSize(4, range(3,9));
        assertSize(3, range(4,9));
        assertSize(3, range(3,7));
        assertSize(4, range(2,9));
        assertSize(4, range(3,10));
        
        
        add(new int[]{5, 6, 8, 10, 2, 1});
        assertSize(10, range(1,10));
        assertSize(9, range(1,9));
        assertSize(9, range(2,10));
        assertSize(9, range(2,11));
        assertSize(10, range(0,10));
    }

	private void assertSize(int size, BTreeRange range) {
        Assert.areEqual(size, range.size());
    }

    public void testIntersectSingleSingle() {		
		assertIntersection(new int[] { 4, 7 }, range(3, 7), range(4, 9));		
		assertIntersection(new int[] {}, range(3, 4), range(7, 9));		
		assertIntersection(new int[] { 3, 4, 7, 9 }, range(3, 9), range(3, 9));		
		assertIntersection(new int[] { 3, 4, 7, 9 }, range(3, 10), range(3, 9));		
		assertIntersection(new int[] {}, range(1, 2), range(3, 9));		
	}
	
	public void testIntersectSingleUnion() {
		BTreeRange union = range(3, 3).union(range(7, 9));
		BTreeRange single = range(4, 7);
		assertIntersection(new int[] { 7 }, union, single);		
		assertIntersection(new int[] { 3, 7 }, union, range(3, 7));
	}
	
	public void testIntersectUnionUnion() {
		final BTreeRange union1 = range(3, 3).union(range(7, 9));
		final BTreeRange union2 = range(3, 3).union(range(9, 9));
		assertIntersection(new int[] { 3, 9 }, union1, union2);
	}
	
	public void testUnion() {		
		assertUnion(new int[] { 3, 4, 7, 9 }, range(3, 4), range(7, 9));
		assertUnion(new int[] { 3, 4, 7, 9 }, range(3, 7), range(4, 9));
		assertUnion(new int[] { 3, 7, 9 }, range(3, 3), range(7, 9));
		assertUnion(new int[] { 3, 9 }, range(3, 3), range(9, 9));		
	}
	
	public void testIsEmpty() {
		Assert.isTrue(range(0, 0).isEmpty());
		Assert.isFalse(range(3, 3).isEmpty());
		Assert.isFalse(range(9, 9).isEmpty());
		Assert.isTrue(range(10, 10).isEmpty());		
	}
	
	public void testUnionWithEmptyDoesNotCreateNewRange() {
		final BTreeRange range = range(3, 4);
		final BTreeRange empty = range(0, 0);
		Assert.areSame(range, range.union(empty));
		Assert.areSame(range, empty.union(range));
		
		final BTreeRange union = range.union(range(8, 9));
		Assert.areSame(union, union.union(empty));
		Assert.areSame(union, empty.union(union));
	}
	
	public void testUnionsMerge() {
		final BTreeRange range = range(3, 3).union(range(7, 7)).union(range(4, 4));
		assertIsRangeSingle(range);
		BTreeAssert.assertRange(new int[] { 3, 4, 7 }, range);
	}

	private void assertIsRangeSingle(final BTreeRange range) {
		Assert.isInstanceOf(BTreeRangeSingle.class, range);
	}
	
	public void testUnionsOfUnions() {
		final BTreeRange union1 = range(3, 4).union(range(8, 9));
		
		BTreeAssert.assertRange(
				new int[] { 3, 4, 9 },
				union1);
		BTreeAssert.assertRange(
				new int[] { 3, 4, 7, 9 },
				union1.union(range(7, 7)));
		
		final BTreeRange union2 = range(3, 3).union(range(7, 7));
		assertUnion(new int[] { 3, 4, 7, 9 }, union1, union2);
		
		assertIsRangeSingle(union1.union(union2));
		assertIsRangeSingle(union2.union(union1));
		
		final BTreeRange union3 = range(3, 3).union(range(9, 9));
		assertUnion(new int[] { 3, 7, 9 }, union2, union3);
	}
	
	public void testExtendToLastOf() {
		BTreeAssert.assertRange(new int[] { 3, 4, 7 }, range(3, 7));		
		BTreeAssert.assertRange(new int[] { 4, 7, 9 }, range(4, 9));
	}
	
	public void testUnionOfOverlappingSingleRangesYieldSingleRange() {		
		Assert.isInstanceOf(BTreeRangeSingle.class, range(3, 4).union(range(4, 9)));
	}

	private void assertUnion(int[] expectedKeys, BTreeRange range1, BTreeRange range2) {
		BTreeAssert.assertRange(expectedKeys, range1.union(range2));
		BTreeAssert.assertRange(expectedKeys, range2.union(range1));
	}

	private void assertIntersection(int[] expectedKeys, BTreeRange range1, BTreeRange range2) {
		BTreeAssert.assertRange(expectedKeys, range1.intersect(range2));
		BTreeAssert.assertRange(expectedKeys, range2.intersect(range1));
	}
}
