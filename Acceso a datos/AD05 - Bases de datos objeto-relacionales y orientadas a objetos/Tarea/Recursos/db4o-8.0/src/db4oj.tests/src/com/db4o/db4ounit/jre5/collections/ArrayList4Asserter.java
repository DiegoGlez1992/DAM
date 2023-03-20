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
import com.db4o.internal.*;

import db4ounit.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore
public class ArrayList4Asserter {
	
	public static int CAPACITY = 100;
	
	private static final Integer ITEM = new Integer((CAPACITY/2));
	
	public static void createList(final List<Integer> list) throws Exception {
		for (int i = 0; i < CAPACITY; i++) {
			list.add(new Integer(i));
		}
	}

	public static void assertAdd(final List<Integer> list) throws Exception {
		for (int i = 0; i < CAPACITY; ++i) {
			list.add(new Integer(CAPACITY + i));
		}
		checkAdd(list);
	}

	public static void checkAdd(final List<Integer> list) {
		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}

	
	public static void assertAdd_LObject(final List<Integer> list) throws Exception {
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.add(-1, new Integer(0));
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.add(CAPACITY + 1, new Integer(0));
			}
		});

		Integer i1 = new Integer(0);
		list.add(0, i1);
		// elements: 0, 0,1 - 100
		// index: 0, 1,2 - 101
		Assert.areSame(i1, list.get(0));

		for (int i = 1; i < CAPACITY + 1; ++i) {
			Assert.areEqual(new Integer(i - 1), list.get(i));
		}

		list.add(CAPACITY/2, ITEM);
		checkAdd_LObject(list);
	}

	public static void checkAdd_LObject(final List<Integer> list) {
		// elements: 0, 0,1 - C/2, C/2, C+1 - C
		// index: 0, 1,2 - C/2-1, C/2, C/2+1 - C
		for (int i = 1; i < (CAPACITY/2); ++i) {
			Assert.areEqual(new Integer(i - 1), list.get(i));
		}

		Assert.areEqual(ITEM, list.get(CAPACITY/2));
		Assert.areEqual(new Integer((CAPACITY/2)/2), list.get((CAPACITY/2)/2+1));

		for (int i = (CAPACITY/2)+1 ; i < CAPACITY + 2; ++i) {
			Assert.areEqual(new Integer(i - 2), list.get(i));
		}
	}

	public static void assertAddAll_LCollection(final List<Integer> list) throws Exception {
		final Vector<Integer> v = new Vector<Integer>();
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.addAll(-1, v);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.addAll(CAPACITY + 1, v);
			}
		});

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		list.addAll(v);

		checkAddAll_LCollection(list);
	}

	public static void checkAddAll_LCollection(final List<Integer> list) {
		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}

	public static void assertAddAll_ILCollection(final List<Integer> list) throws Exception {
		final Vector<Integer> v = new Vector<Integer>();
		final int INDEX = 42;

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		list.addAll(INDEX, v);
		// elements: 0 - 41, 100 - 199, 42 - 100
		// index: 0 - 41, 42 - 141, 142 - 200
		for (int i = 0; i < INDEX; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}

		for (int i = INDEX, j = 0; j < CAPACITY; ++i, ++j) {
			Assert.areEqual(new Integer(CAPACITY + j), list.get(i));
		}

		for (int i = INDEX + CAPACITY; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i - CAPACITY), list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.addAll(-1, v);
			}
		});
	}

	public static void assertClear(final List<Integer> list) throws Exception {
		list.clear();
		checkClear(list);
	}

	public static void checkClear(final List<Integer> list) {
		Assert.areEqual(0, list.size());
	}

	public static void assertContains(final List<Integer> list) throws Exception {
		Assert.isTrue(list.contains(new Integer(0)));
		Assert.isTrue(list.contains(new Integer(CAPACITY / 2)));
		Assert.isTrue(list.contains(new Integer(CAPACITY / 3)));
		Assert.isTrue(list.contains(new Integer(CAPACITY / 4)));

		Assert.isFalse(list.contains(new Integer(-1)));
		Assert.isFalse(list.contains(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns true if and only if
		// this list contains at least one element e such that (o==null ?
		// e==null : o.equals(e)).
		Assert.isFalse(list.contains(null));
	}

	public static void assertContainsAll(final List<Integer> list) throws Exception {
		Vector<Integer> v = new Vector<Integer>();

		v.add(new Integer(0));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(0));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(CAPACITY / 2));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(CAPACITY / 3));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(CAPACITY / 4));
		Assert.isTrue(list.containsAll(v));

		v.add(new Integer(CAPACITY));
		Assert.isFalse(list.containsAll(v));
	}

	public static void assertGet(final List<Integer> list) throws Exception {
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.get(-1);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.get(CAPACITY);
			}
		});
	}

	public static void assertIndexOf(final List<Integer> list) throws Exception {
		Assert.areEqual(0, list.indexOf(new Integer(0)));
		Assert.areEqual(CAPACITY / 2, list.indexOf(new Integer(CAPACITY / 2)));
		Assert.areEqual(CAPACITY / 3, list.indexOf(new Integer(CAPACITY / 3)));
		Assert.areEqual(CAPACITY / 4, list.indexOf(new Integer(CAPACITY / 4)));

		Assert.areEqual(-1, list.indexOf(new Integer(-1)));
		Assert.areEqual(-1, list.indexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, list.indexOf(null));
	}

	public static void assertIsEmpty(final List<Integer> list) throws Exception {
		Assert.isFalse(list.isEmpty());
		list.clear();
		Assert.isTrue(list.isEmpty());
	}

	public static void assertIterator(final List<Integer> list) throws Exception {
		Iterator<Integer> iter = list.iterator();
		int count = 0;
		while (iter.hasNext()) {
			Integer i = iter.next();
			Assert.areEqual(count, i.intValue());
			++count;
		}
		Assert.areEqual(CAPACITY, count);

		list.clear();
		iter = list.iterator();
		Assert.isFalse(iter.hasNext());
	}

	public static void assertLastIndexOf(final List<Integer> list) throws Exception {
		Assert.areEqual(0, list.indexOf(new Integer(0)));
		Assert.areEqual(CAPACITY / 2, list.lastIndexOf(new Integer(
				CAPACITY / 2)));
		Assert.areEqual(CAPACITY / 3, list.lastIndexOf(new Integer(
				CAPACITY / 3)));
		Assert.areEqual(CAPACITY / 4, list.lastIndexOf(new Integer(
				CAPACITY / 4)));

		Assert.areEqual(-1, list.lastIndexOf(new Integer(-1)));
		Assert.areEqual(-1, list.lastIndexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, list.lastIndexOf(null));

		list.add(new Integer(0));
		list.add(new Integer(CAPACITY / 2));
		list.add(new Integer(CAPACITY / 3));
		list.add(new Integer(CAPACITY / 4));

		Assert.areEqual(CAPACITY, list.lastIndexOf(new Integer(0)));
		Assert.areEqual(CAPACITY + 1, list.lastIndexOf(new Integer(
				CAPACITY / 2)));
		Assert.areEqual(CAPACITY + 2, list.lastIndexOf(new Integer(
				CAPACITY / 3)));
		Assert.areEqual(CAPACITY + 3, list.lastIndexOf(new Integer(
				CAPACITY / 4)));

		Assert.areEqual(-1, list.lastIndexOf(new Integer(-1)));
		Assert.areEqual(-1, list.lastIndexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, list.lastIndexOf(null));
	}

	public static void assertRemove_LObject(final List<Integer> list) throws Exception {
		list.remove(new Integer(0));
		Assert.areEqual(new Integer(1), list.get(0));

		Assert.areEqual(CAPACITY - 1, list.size());

		int val = CAPACITY/2;
		list.remove(new Integer(val));
		Assert.areEqual(new Integer(val+1), list.get(val-1));
		Assert.areEqual(new Integer(val+2), list.get(val));
		Assert.areEqual(new Integer(val+3), list.get(val+1));
		Assert.areEqual(CAPACITY - 2, list.size());

		for (int i = 0; i < CAPACITY - 2; ++i) {
			list.remove(list.get(0));
			Assert.areEqual(CAPACITY - 3 - i, list.size());
		}
		Assert.isTrue(list.isEmpty());
	}

	public static void assertRemove_I(final List<Integer> list) throws Exception {
		list.remove(0);
		Assert.areEqual(new Integer(1), list.get(0));
		Assert.isFalse(list.contains(new Integer(0)));
		Assert.areEqual(CAPACITY - 1, list.size());

		list.remove(42);
		Assert.areEqual(new Integer(44), list.get(42));
		Assert.areEqual(new Integer(42), list.get(41));
		Assert.isFalse(list.contains(new Integer(43)));
		Assert.areEqual(CAPACITY - 2, list.size());

		for (int i = 0; i < CAPACITY - 2; ++i) {
			list.remove(0);
			Assert.areEqual(CAPACITY - 3 - i, list.size());
		}
		checkRemove_LObject(list);
	}

	public static void checkRemove_LObject(final List<Integer> list) {
		Assert.isTrue(list.isEmpty());
	}

	public static void assertRemoveAll(final List<Integer> list) throws Exception {
		Vector<Integer>v = new Vector<Integer>();

		list.removeAll(v);
		Assert.areEqual(CAPACITY, list.size());

		int val = CAPACITY/2;
		v.add(new Integer(0));
		v.add(new Integer(val));
		list.removeAll(v);
		Assert.isFalse(list.contains(new Integer(0)));
		Assert.isFalse(list.contains(new Integer(val)));
		Assert.areEqual(CAPACITY - 2, list.size());

		v.add(new Integer(1));
		v.add(new Integer(2));
		list.removeAll(v);
		Assert.isFalse(list.contains(new Integer(1)));
		Assert.isFalse(list.contains(new Integer(2)));
		Assert.areEqual(CAPACITY - 4, list.size());

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(i));
		}
		list.removeAll(v);
		checkRemoveAll(list);
	}

	public static void checkRemoveAll(final List<Integer> list) {
		Assert.isTrue(list.isEmpty());
	}

	public static void assertRetainAll(final List<Integer> list) throws Exception {
		Vector <Integer>v = new Vector<Integer>();
		v.add(new Integer(0));
		v.add(new Integer(42));

		boolean ret = list.retainAll(list);
		Assert.isFalse(ret);
		Assert.areEqual(100, list.size());
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.isTrue(list.contains(new Integer(i)));
		}

		ret = list.retainAll(v);
		Assert.isTrue(ret);
		Assert.areEqual(2, list.size());
		list.contains(new Integer(0));
		list.contains(new Integer(42));

		ret = list.retainAll(v);
		Assert.isFalse(ret);
		list.contains(new Integer(0));
		list.contains(new Integer(42));
	}

	public static void assertSet(final List<Integer> list) throws Exception {
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.set(-1, new Integer(0));
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				list.set(CAPACITY, new Integer(0));
			}
		});
		
		Integer element = new Integer(1);
		
		Integer previousElement = list.get(0);
		Assert.areSame(previousElement, list.set(0, element));
		Assert.areSame(element, list.get(0));

		int val = CAPACITY/2;
		previousElement = list.get(val);
		Assert.areSame(previousElement, list.set(val, element));
		Assert.areSame(element, list.get(val));

		for (int i = 0; i < CAPACITY; ++i) {
			element = new Integer(i);
			previousElement = list.get(i);
			Assert.areSame(previousElement, list.set(i, element));
			Assert.areSame(element, list.get(i));
		}
	}

	public static void assertSize(final List<Integer> list) throws Exception {
		Assert.areEqual(CAPACITY, list.size());
		for (int i = 0; i < CAPACITY; ++i) {
			list.remove(0);
			Assert.areEqual(CAPACITY - 1 - i, list.size());
		}
		for (int i = 0; i < CAPACITY; ++i) {
			list.add(new Integer(i));
			Assert.areEqual(i + 1, list.size());
		}
	}
	
	public static void assertToArray(final List<Integer> list) throws Exception {
		Object[] array = list.toArray();
		Assert.areEqual(CAPACITY, array.length);
		for(int i = 0; i < CAPACITY; ++i) {
			Integer element = (Integer) array[i];
			Assert.areEqual(new Integer(i), element);
		}
		
		list.clear();
		array = list.toArray();
		Assert.areEqual(0, array.length);
	}
	
	public static void assertToArray_LObject(final List<Integer> list) throws Exception {
		Object[] array1;
		Object[] array2 = new Integer[CAPACITY];
		array1 = list.toArray(array2);
		Assert.areSame(array1, array2);
		Assert.areEqual(CAPACITY, array2.length);
		for(int i = 0; i < CAPACITY; ++i) {	
			Integer element = (Integer) array2[i];
			Assert.areEqual(new Integer(i), element);
		}
		
		list.clear();
		
		array1 = new Integer[0];
		array2 = new Integer[CAPACITY];
		array1 = list.toArray(array2);
		Assert.areSame(array1, array2);
		Assert.areEqual(CAPACITY, array1.length);
		
		array2 = new Integer[0];
		array1 = list.toArray(array2);
		Assert.areEqual(0, array1.length);
	}
	
	public static void assertToString(final List<Integer> list) throws Exception {
		StringBuffer expected = new StringBuffer("[");
		for(int i = 0; i < CAPACITY-1; ++i) {
			expected.append(i + ", ");
		}
		expected.append(CAPACITY - 1  + "]");
		Assert.areEqual(expected.toString(), list.toString());
	}
	
	public static void assertTrimToSize_Remove(final List<Integer> list) throws Exception {
		for (int i = CAPACITY-1; i >= 10 ; i--) {
			list.remove(i);
		}
		Assert.areEqual(10, list.size());
		for(int i = 0; i < 10; ++i) {
			Integer element = list.get(i);
			Assert.areEqual(new Integer(i), element);
		}
	}
	
	public static void assertClear_Iterator(final List<Integer> list) throws Exception {
		final Iterator<Integer> iterator = list.iterator();
		list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
	}
	
	
	@SuppressWarnings("unchecked")
	public static void assertClone(final List<Integer> list) throws Exception {
		list.add(null);
		List<Integer> cloned = listClone(list);
		for (int i = 0; i < CAPACITY; i++) {
			Assert.areSame(list.get(i), cloned.get(i));
		}
	}
	
	public static void assertEquals(final List<Integer> list) throws Exception {
		Assert.isFalse(list.equals(null));
		Assert.isFalse(list.equals(new Integer(1)));
		Assert.isTrue(list.equals(list));
		Vector<Integer> v = new Vector<Integer>(list);
		Assert.isTrue(list.equals(v));
		v = new Vector<Integer>();
		Assert.isFalse(list.equals(v));
		Assert.isTrue(list.equals(listClone(list)));
	}
	
	public static void assertIteratorNext_NoSuchElementException(final List<Integer> list) throws Exception {
		final Iterator<Integer> iterator = list.iterator();
		Assert.expect(NoSuchElementException.class, new CodeBlock(){
			public void run() throws Throwable {
				while(true){iterator.next();}
			}
		});
	}
	
	public static void assertIteratorNext_ConcurrentModificationException(final List<Integer> list) throws Exception {
		final Iterator<Integer> iterator = list.iterator();
		Assert.expect(NoSuchElementException.class, new CodeBlock(){
			public void run() throws Throwable {
				while(true){iterator.next();}
			}
		});
		list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.next();
			}
		});
		
	}
	
	public static void assertIteratorNext(final List<Integer> list) throws Exception {
		final Iterator<Integer> iterator = list.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Integer e1 = iterator.next();
			Assert.areSame(e1, list.get(i));
			i++;
		}
	}
	
	public static void assertIteratorRemove(final List<Integer> list) throws Exception {
		final Iterator<Integer> iterator = list.iterator();
		int i = CAPACITY-1;
		while (iterator.hasNext()) {
			Integer e1 = iterator.next();
			Assert.areSame(e1, list.get(0));
			Assert.areEqual(new Integer(CAPACITY-1), list.get(i));
			iterator.remove();
			Assert.areEqual(i, list.size());
			i--;
		}
	}
	
	public static void assertRemove_IllegalStateException(final List<Integer> list) throws Exception {
		final Iterator<Integer> iterator = list.iterator();
		Assert.expect(IllegalStateException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.remove();
			}
		});
		
		iterator.next();
		
		Assert.expect(IllegalStateException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.remove();
				iterator.remove();
			}
		});
	}
	
	public static void assertIteratorRemove_ConcurrentModificationException(final List<Integer> list) throws Exception {
		final Iterator<Integer> iterator = list.iterator();
		iterator.next();
		list.clear();
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				iterator.remove();
			}
		});
	}

	public static void assertSubList(List<Integer> list) throws Exception {
		int val = CAPACITY/2;
		List<Integer> subList1 = list.subList(val, CAPACITY);
		for (int index = 0; index < subList1.size(); index++) {
			Assert.areSame(list.get(val+index),subList1.get(index));
		}
		list.set(val, new Integer(1001));
		Assert.areEqual(new Integer(1001), subList1.get(0));
		
		subList1.set(1, new Integer(1001));
		Assert.areEqual(new Integer(1001), list.get(val+1));
	}
	
	public static void assertSubList_ConcurrentModification(List<Integer> list) throws Exception {
		int val = CAPACITY/2;
		final List<Integer> subList1 = list.subList(val, CAPACITY);
		for (int index = 0; index < subList1.size(); index++) {
			Assert.areSame(list.get(val+index),subList1.get(index));
		}
		list.remove(0);
		Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
			public void run() throws Throwable {
				subList1.get(0);
			}
		});
	}
	
	public static void assertSubList_Clear(List<Integer> list) throws Exception {
		list.subList(CAPACITY-10, CAPACITY).clear();
		int expectedSize = CAPACITY-10;
		Assert.areEqual(expectedSize, list.size());
		for (int i = 0; i < expectedSize; ++i) {
			Assert.areEqual(new Integer(i), list.get(i));
		}
	}
	
	public static void assertSubList_Clear2(List<Integer> list) throws Exception {
		list.subList(0, 10).clear();
		int expectedSize = CAPACITY-10;
		Assert.areEqual(expectedSize, list.size());
		for (int i = 0; i < expectedSize; ++i) {
			Assert.areEqual(new Integer(i+10), list.get(i));
		}
	}
	
	public static <T> List<T> listClone(List<T> list){
	    return (List<T>) clone((Cloneable) list);
	}
	
	public static Cloneable clone(Cloneable obj){
	    return (Cloneable) Reflection4.invoke(obj, "clone");
	}

    public static void assertTrimToSize_EnsureCapacity(final ArrayList4<Integer> list) throws Exception {
        list.ensureCapacity(CAPACITY*2);
        ArrayList4Asserter.checkTrimToSize_EnsureCapacity(list);
        list.trimToSize();
        ArrayList4Asserter.checkTrimToSize_EnsureCapacity(list);
    }

    public static void checkTrimToSize_EnsureCapacity(final ArrayList4<Integer> list) {
        Assert.areEqual(CAPACITY, list.size());
        for(int i = 0; i < CAPACITY; ++i) {
            Integer element = list.get(i);
            Assert.areEqual(new Integer(i), element);
        }
    }

    public static void assertEnsureCapacity_Iterator(final ArrayList4<Integer> list) throws Exception {
    	final Iterator<Integer> iterator = list.iterator();
    	list.ensureCapacity(CAPACITY*2);
    	Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
    		public void run() throws Throwable {
    			iterator.next();
    		}
    	});
    }

    public static void assertTrimToSize_Iterator(final ArrayList4<Integer> list) throws Exception {
    	final Iterator<Integer> iterator = list.iterator();
    	list.trimToSize();
    	Assert.expect(ConcurrentModificationException.class, new CodeBlock(){
    		public void run() throws Throwable {
    			iterator.next();
    		}
    	});
    }
	
}
