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
package com.db4o.db4ounit.jre5.collections;

import com.db4o.collections.*;

import db4ounit.*;


/**
 * @exclude
 */
@decaf.Ignore
public class ArrayList4TATestCase extends ArrayList4TATestCaseBase {

	public static void main(String[] args) {
		new ArrayList4TATestCase().runAll();
	}

	public void testAdd() throws Exception {
		ArrayList4Asserter.assertAdd(retrieveAndAssertNullArrayList4());
	}
	
	public void testAdd_LObject() throws Exception {
		ArrayList4Asserter.assertAdd_LObject(retrieveAndAssertNullArrayList4());
	}

	public void testAddAll_LCollection() throws Exception {
		ArrayList4Asserter.assertAddAll_LCollection(retrieveAndAssertNullArrayList4());
	}

	public void testClear() throws Exception {
		ArrayList4Asserter.assertClear(retrieveAndAssertNullArrayList4());
	}

	public void testContains() throws Exception {
		ArrayList4Asserter.assertContains(retrieveAndAssertNullArrayList4());
	}

	public void testContainsAll() throws Exception {
		ArrayList4Asserter.assertContainsAll(retrieveAndAssertNullArrayList4());
	}

	public void testIndexOf() throws Exception {
		ArrayList4Asserter.assertIndexOf(retrieveAndAssertNullArrayList4());
	}

	public void testIsEmpty() throws Exception {
		ArrayList4Asserter.assertIsEmpty(retrieveAndAssertNullArrayList4());
		Assert.isTrue(new ArrayList4<Integer>().isEmpty());
	}

	public void testIterator() throws Exception {
		ArrayList4Asserter.assertIterator(retrieveAndAssertNullArrayList4());
	}

	public void testLastIndexOf() throws Exception {
		ArrayList4Asserter.assertLastIndexOf(retrieveAndAssertNullArrayList4());
	}

	public void testRemove_LObject() throws Exception {
		ArrayList4Asserter.assertRemove_LObject(retrieveAndAssertNullArrayList4());
	}

	public void testRemoveAll() throws Exception {
		ArrayList4Asserter.assertRemoveAll(retrieveAndAssertNullArrayList4());
	}

	public void testSet() throws Exception {
		ArrayList4Asserter.assertSet(retrieveAndAssertNullArrayList4());
	}

	public void testSize() throws Exception {
		ArrayList4Asserter.assertSize(retrieveAndAssertNullArrayList4());
	}
	
	public void testToArray() throws Exception {
		ArrayList4Asserter.assertToArray(retrieveAndAssertNullArrayList4());
	}
	
	public void testToArray_LObject() throws Exception {
		ArrayList4Asserter.assertToArray_LObject(retrieveAndAssertNullArrayList4());
	}
	
	public void testToString() throws Exception {
		ArrayList4Asserter.assertToString(retrieveAndAssertNullArrayList4());
	}
	
	public void testTrimToSize_EnsureCapacity() throws Exception {
		ArrayList4Asserter.assertTrimToSize_EnsureCapacity(retrieveAndAssertNullArrayList4());
	}
	
	public void testTrimToSize_Remove() throws Exception {
		ArrayList4Asserter.assertTrimToSize_Remove(retrieveAndAssertNullArrayList4());
	}
	
	public void testTrimToSize_Iterator() throws Exception {
		ArrayList4Asserter.assertTrimToSize_Iterator(retrieveAndAssertNullArrayList4());
	}
	
	public void testEnsureCapacity_Iterator() throws Exception {
		ArrayList4Asserter.assertEnsureCapacity_Iterator(retrieveAndAssertNullArrayList4());
	}
	
	public void testClear_Iterator() throws Exception {
		ArrayList4Asserter.assertClear_Iterator(retrieveAndAssertNullArrayList4());
	}
	
	public void testClone() throws Exception {
		ArrayList4Asserter.assertClone(retrieveAndAssertNullArrayList4());
	}
	
	public void testEquals() throws Exception {
		ArrayList4Asserter.assertEquals(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorNext_NoSuchElementException() throws Exception {
		ArrayList4Asserter.assertIteratorNext_NoSuchElementException(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorNext_ConcurrentModificationException() throws Exception {
		ArrayList4Asserter.assertIteratorNext_ConcurrentModificationException(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorNext() throws Exception {
		ArrayList4Asserter.assertIteratorNext(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorRemove() throws Exception {
		ArrayList4Asserter.assertIteratorRemove(retrieveAndAssertNullArrayList4());
	}
	
	public void testRemove_IllegalStateException() throws Exception {
		ArrayList4Asserter.assertRemove_IllegalStateException(retrieveAndAssertNullArrayList4());
	}
	
	public void testIteratorRemove_ConcurrentModificationException() throws Exception {
		ArrayList4Asserter.assertIteratorRemove_ConcurrentModificationException(retrieveAndAssertNullArrayList4());
	}
	
	public void testSubList() throws Exception {
		ArrayList4Asserter.assertSubList(retrieveAndAssertNullArrayList4());
	}
	
	public void testSubList_ConcurrentModification() throws Exception {
		ArrayList4Asserter.assertSubList_ConcurrentModification(retrieveAndAssertNullArrayList4());
	}
	public void testSubList_Clear() throws Exception {
		ArrayList4Asserter.assertSubList_Clear(retrieveAndAssertNullArrayList4());
	}
	
	public void testSubList_Clear2() throws Exception {
		ArrayList4Asserter.assertSubList_Clear2(retrieveAndAssertNullArrayList4());
	}

}
