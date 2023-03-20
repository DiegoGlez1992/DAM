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
import com.db4o.db4ounit.common.ta.*;
import com.db4o.internal.*;

import db4ounit.*;


/**
 */
@decaf.Ignore
public class ArrayList4TestCase implements TestLifeCycle {

	public static void main(String[] args) {
		new ConsoleTestRunner(ArrayList4TestCase.class).run();
	}
	
	public List <Integer> _list;

	public void setUp() throws Exception {
		_list = newList(Integer.class);
		ArrayList4Asserter.createList(_list);
	}

	public void tearDown() throws Exception {
		
	}
	
	/**
	 * @param elementType
	 */
    private <T> List<T> newList(Class <T> elementType) {
        return new ArrayList4<T>();
    }
    
    private List<Integer> newList(int capacity) {
        return new ArrayList4<Integer>(capacity);
    }
    
    private <T> List<T> newList(List<T> list) {
        return new ArrayList4<T>(list);
    }
	
	private ArrayList4<Integer> castedList(){
	    return (ArrayList4<Integer>)_list;
	}
	
    public void testCloneWontCopyActivator() throws Exception {
    	castedList().bind(new MockActivator());
		final Object clone = castedList().clone();
		Assert.isNull(Reflection4.getFieldValue(clone, "_activator"));
    }
	
	public void testConstructor() throws Exception {
		List<Integer> arrayList = newList(Integer.class);
		fill(arrayList);
		Assert.areEqual(ArrayList4Asserter.CAPACITY, arrayList.size());
	}

	public void testConstructor_I_LegalArguments1() throws Exception {
		int capacity = ArrayList4Asserter.CAPACITY;
        List<Integer> arrayList = newList(capacity);
		fill(arrayList);
		Assert.areEqual(capacity, arrayList.size());
	}

	public void testConstructor_I_LegalArguments2() throws Exception {
		List<Integer> arrayList= newList(0);
		fill(arrayList);
		Assert.areEqual(ArrayList4Asserter.CAPACITY, arrayList.size());
	}

	public void testConstructor_I_IllegalArgumentException() throws Exception {
		Assert.expect(IllegalArgumentException.class, new CodeBlock(){
			public void run() throws Throwable {
				newList(-1);
			}
		});
	}
	
	public void testConstructor_LCollection_NullPointerException() throws Exception {
		Assert.expect(NullPointerException.class, new CodeBlock(){
			public void run() throws Throwable {
				newList((List<Integer>)null);
			}
		});
	}
	
	public void testConstructor_LCollection() throws Exception {
		List<Integer> list = _list;
        List<Integer> arrayList = newList(list);
		Assert.areEqual(list.size(), arrayList.size());
		Assert.isTrue(Arrays.equals(list.toArray(), arrayList.toArray()));
		
	}

	private void fill(List <Integer> arrayList) {
		for (int i = 0; i < ArrayList4Asserter.CAPACITY; i++) {
			arrayList.add(new Integer(i));
		}
	}

	public void testAdd() throws Exception {
		ArrayList4Asserter.assertAdd(_list);
	}

	public void testAdd_LObject() throws Exception {
		ArrayList4Asserter.assertAdd_LObject(_list);
	}

	public void testAddAll_LCollection() throws Exception {
		ArrayList4Asserter.assertAddAll_LCollection(_list);
	}

	public void testClear() throws Exception {
		ArrayList4Asserter.assertClear(_list);
	}

	public void testContains() throws Exception {
		ArrayList4Asserter.assertContains(_list);
	}

	public void testContainsAll() throws Exception {
		ArrayList4Asserter.assertContainsAll(_list);
	}

	public void testIndexOf() throws Exception {
		ArrayList4Asserter.assertIndexOf(_list);
	}

	public void testIsEmpty() throws Exception {
		ArrayList4Asserter.assertIsEmpty(_list);
		Assert.isTrue(newList(Integer.class).isEmpty());
	}

	public void testIterator() throws Exception {
		ArrayList4Asserter.assertIterator(_list);
	}

	public void testLastIndexOf() throws Exception {
		ArrayList4Asserter.assertLastIndexOf(_list);
	}

	public void testRemove_LObject() throws Exception {
		ArrayList4Asserter.assertRemove_LObject(_list);
	}

	public void testRemoveAll() throws Exception {
		ArrayList4Asserter.assertRemoveAll(_list);
	}

	public void testSet() throws Exception {
		ArrayList4Asserter.assertSet(_list);
	}

	public void testSize() throws Exception {
		ArrayList4Asserter.assertSize(_list);
	}
	
	public void testToArray() throws Exception {
		ArrayList4Asserter.assertToArray(_list);
	}
	
	public void testToArray_LObject() throws Exception {
		ArrayList4Asserter.assertToArray_LObject(_list);
	}
	
	public void testToString() throws Exception {
		ArrayList4Asserter.assertToString(_list);
        List<Object> oList = newList(Object.class);    
        Assert.areEqual("[]",oList.toString());
        
        oList.add(new Integer(1));
        oList.add(new Integer(2));
        Assert.areEqual("[1, 2]",oList.toString());
        
        oList.add(oList);
        oList.add(new Integer(3));
        Assert.areEqual("[1, 2, (this Collection), 3]",oList.toString());
	}
	
	public void testTrimToSize_EnsureCapacity() throws Exception {
		ArrayList4Asserter.assertTrimToSize_EnsureCapacity(castedList());
	}
	
	public void testTrimToSize_Remove() throws Exception {
		ArrayList4Asserter.assertTrimToSize_Remove(_list);
	}
	
	public void testTrimToSize_Iterator() throws Exception {
		ArrayList4Asserter.assertTrimToSize_Iterator(castedList());
	}
	
	public void testEnsureCapacity_Iterator() throws Exception {
		ArrayList4Asserter.assertEnsureCapacity_Iterator(castedList());
	}
	
	public void testClear_Iterator() throws Exception {
		ArrayList4Asserter.assertClear_Iterator(_list);
	}
	
	public void testClone() throws Exception {
		ArrayList4Asserter.assertClone(_list);
	}
	
	public void testEquals() throws Exception {
		ArrayList4Asserter.assertEquals(_list);
	}
	
	public void testIteratorNext_NoSuchElementException() throws Exception {
		ArrayList4Asserter.assertIteratorNext_NoSuchElementException(_list);
	}
	
	public void testIteratorNext_ConcurrentModificationException() throws Exception {
		ArrayList4Asserter.assertIteratorNext_ConcurrentModificationException(_list);
	}
	
	public void testIteratorNext() throws Exception {
		ArrayList4Asserter.assertIteratorNext(_list);
	}
	
	public void testIteratorRemove() throws Exception {
		ArrayList4Asserter.assertIteratorRemove(_list);
	}
	
	public void testRemove_IllegalStateException() throws Exception {
		ArrayList4Asserter.assertRemove_IllegalStateException(_list);
	}
	
	public void testIteratorRemove_ConcurrentModificationException() throws Exception {
		ArrayList4Asserter.assertIteratorRemove_ConcurrentModificationException(_list);
	}
	
	public void testSubList() throws Exception {
		ArrayList4Asserter.assertSubList(_list);
	}
	
	public void testSubList_ConcurrentModification() throws Exception {
		ArrayList4Asserter.assertSubList_ConcurrentModification(_list);
	}
	
	public void testSubList_Clear() throws Exception {
		ArrayList4Asserter.assertSubList_Clear(_list);
	}
	
	public void testSubList_Clear2() throws Exception {
		ArrayList4Asserter.assertSubList_Clear2(_list);
	}
	
}
