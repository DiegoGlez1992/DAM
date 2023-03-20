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

import java.util.*;

import com.db4o.collections.*;

import db4ounit.*;


/**
 */
@decaf.Ignore
public class SubArrayList4TestCase implements TestLifeCycle {

	List<Integer> _subList;
	
	public void setUp() throws Exception {
		ArrayList4Asserter.CAPACITY = 100;
		ArrayList4<Integer> list = new ArrayList4<Integer>();
		ArrayList4Asserter.createList(list);
		_subList = list.subList(0, 10);
		ArrayList4Asserter.CAPACITY = 10;
	}
	
	public void tearDown() throws Exception {
		ArrayList4Asserter.CAPACITY = 100;
	}
	
	public void testAdd() throws Exception {
		ArrayList4Asserter.assertAdd(_subList);
	}

	public void testAdd_LObject() throws Exception {
		ArrayList4Asserter.assertAdd_LObject(_subList);
	}

	public void testAddAll_LCollection() throws Exception {
		ArrayList4Asserter.assertAddAll_LCollection(_subList);
	}

	public void testClear() throws Exception {
		ArrayList4Asserter.assertClear(_subList);
	}

	public void testContains() throws Exception {
		ArrayList4Asserter.assertContains(_subList);
	}

	public void testContainsAll() throws Exception {
		ArrayList4Asserter.assertContainsAll(_subList);
	}

	public void testIndexOf() throws Exception {
		ArrayList4Asserter.assertIndexOf(_subList);
	}

	public void testIsEmpty() throws Exception {
		ArrayList4Asserter.assertIsEmpty(_subList);
	}

	public void testIterator() throws Exception {
		ArrayList4Asserter.assertIterator(_subList);
	}

	public void testLastIndexOf() throws Exception {
		ArrayList4Asserter.assertLastIndexOf(_subList);
	}

	public void testRemove_LObject() throws Exception {
		ArrayList4Asserter.assertRemove_LObject(_subList);
	}

	public void testRemoveAll() throws Exception {
		ArrayList4Asserter.assertRemoveAll(_subList);
	}

	public void testSet() throws Exception {
		ArrayList4Asserter.assertSet(_subList);
	}

	public void testSize() throws Exception {
		ArrayList4Asserter.assertSize(_subList);
	}
	
	public void testToArray() throws Exception {
		ArrayList4Asserter.assertToArray(_subList);
	}
	
	public void testToArray_LObject() throws Exception {
		ArrayList4Asserter.assertToArray_LObject(_subList);
	}
	
	public void testToString() throws Exception {
		ArrayList4Asserter.assertToString(_subList);
	}
	
	public void testTrimToSize_Remove() throws Exception {
		ArrayList4Asserter.assertTrimToSize_Remove(_subList);
	}
	
	public void testIteratorNext_NoSuchElementException() throws Exception {
		ArrayList4Asserter.assertIteratorNext_NoSuchElementException(_subList);
	}
	
	public void testIteratorNext_ConcurrentModificationException() throws Exception {
		ArrayList4Asserter.assertIteratorNext_ConcurrentModificationException(_subList);
	}
	
	public void testIteratorNext() throws Exception {
		ArrayList4Asserter.assertIteratorNext(_subList);
	}
	
	public void testIteratorRemove() throws Exception {
		ArrayList4Asserter.assertIteratorRemove(_subList);
	}
	
	public void testRemove_IllegalStateException() throws Exception {
		ArrayList4Asserter.assertRemove_IllegalStateException(_subList);
	}
	
	public void testIteratorRemove_ConcurrentModificationException() throws Exception {
		ArrayList4Asserter.assertIteratorRemove_ConcurrentModificationException(_subList);
	}
	
	public void testSubList() throws Exception {
		ArrayList4Asserter.assertSubList(_subList);
	}
	
	public void testSubList_ConcurrentModification() throws Exception {
		ArrayList4Asserter.assertSubList_ConcurrentModification(_subList);
	}	

}
