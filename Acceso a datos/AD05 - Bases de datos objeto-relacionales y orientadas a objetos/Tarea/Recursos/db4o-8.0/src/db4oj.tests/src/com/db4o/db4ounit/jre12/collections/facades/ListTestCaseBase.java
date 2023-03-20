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
package com.db4o.db4ounit.jre12.collections.facades;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ListTestCaseBase extends AbstractDb4oTestCase {

	public List _list;

	private static int CAPACITY = 100;

	protected void init(List list) {
		_list = list;
		for (int i = 0; i < CAPACITY; i++) {
			_list.add(new Integer(i));
		}
	}
	
	public void testAdd() throws Exception {
		for (int i = 0; i < CAPACITY; ++i) {
			_list.add(new Integer(CAPACITY + i));
		}

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}
	}

	public void testAdd_LObject() throws Exception {
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.add(-1, new Integer(0));
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.add(CAPACITY + 1, new Integer(0));
			}
		});

		Integer i1 = new Integer(0);
		_list.add(0, i1);
		// elements: 0, 0,1 - 100
		// index: 0, 1,2 - 101
		Assert.areSame(i1, _list.get(0));

		for (int i = 1; i < CAPACITY + 1; ++i) {
			Assert.areEqual(new Integer(i - 1), _list.get(i));
		}

		Integer i2 = new Integer(42);
		_list.add(42, i2);
		// elements: 0, 0,1 - 42, 42, 43 - 100
		// index: 0, 1,2 - 43, 44, 45 - 102
		for (int i = 1; i < 42; ++i) {
			Assert.areEqual(new Integer(i - 1), _list.get(i));
		}

		Assert.areSame(i2, _list.get(42));
		Assert.areEqual(new Integer(41), _list.get(43));

		for (int i = 44; i < CAPACITY + 2; ++i) {
			Assert.areEqual(new Integer(i - 2), _list.get(i));
		}
	}

	public void testAddAll_LCollection() throws Exception {
		final Vector v = new Vector();
		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.addAll(-1, v);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.addAll(CAPACITY + 1, v);
			}
		});

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		_list.addAll(v);

		for (int i = 0; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}
	}

	public void testAddAll_ILCollection() throws Exception {
		final Vector v = new Vector();
		final int INDEX = 42;

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(CAPACITY + i));
		}

		_list.addAll(INDEX, v);
		// elements: 0 - 41, 100 - 199, 42 - 100
		// index: 0 - 41, 42 - 141, 142 - 200
		for (int i = 0; i < INDEX; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}

		for (int i = INDEX, j = 0; j < CAPACITY; ++i, ++j) {
			Assert.areEqual(new Integer(CAPACITY + j), _list.get(i));
		}

		for (int i = INDEX + CAPACITY; i < CAPACITY * 2; ++i) {
			Assert.areEqual(new Integer(i - CAPACITY), _list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.addAll(-1, v);
			}
		});
	}

	public void testClear() throws Exception {
		_list.clear();
		Assert.areEqual(0, _list.size());
	}

	public void testContains() throws Exception {
		Assert.isTrue(_list.contains(new Integer(0)));
		Assert.isTrue(_list.contains(new Integer(CAPACITY / 2)));
		Assert.isTrue(_list.contains(new Integer(CAPACITY / 3)));
		Assert.isTrue(_list.contains(new Integer(CAPACITY / 4)));

		Assert.isFalse(_list.contains(new Integer(-1)));
		Assert.isFalse(_list.contains(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns true if and only if
		// this list contains at least one element e such that (o==null ?
		// e==null : o.equals(e)).
		Assert.isFalse(_list.contains(null));
	}

	public void testContainsAll() throws Exception {
		Vector v = new Vector();

		v.add(new Integer(0));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(0));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(CAPACITY / 2));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(CAPACITY / 3));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(CAPACITY / 4));
		Assert.isTrue(_list.containsAll(v));

		v.add(new Integer(CAPACITY));
		Assert.isFalse(_list.containsAll(v));
	}

	public void testGet() throws Exception {
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.areEqual(new Integer(i), _list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.get(-1);
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.get(CAPACITY);
			}
		});
	}

	public void testIndexOf() throws Exception {
		Assert.areEqual(0, _list.indexOf(new Integer(0)));
		Assert.areEqual(CAPACITY / 2, _list.indexOf(new Integer(CAPACITY / 2)));
		Assert.areEqual(CAPACITY / 3, _list.indexOf(new Integer(CAPACITY / 3)));
		Assert.areEqual(CAPACITY / 4, _list.indexOf(new Integer(CAPACITY / 4)));

		Assert.areEqual(-1, _list.indexOf(new Integer(-1)));
		Assert.areEqual(-1, _list.indexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, _list.indexOf(null));
	}

	public void testIsEmpty() throws Exception {
		Assert.isFalse(_list.isEmpty());
		_list.clear();
		Assert.isTrue(_list.isEmpty());
	}

	public void testIterator() throws Exception {
		Iterator iter = _list.iterator();
		int count = 0;
		while (iter.hasNext()) {
			Integer i = (Integer) iter.next();
			Assert.areEqual(count, i.intValue());
			++count;
		}
		Assert.areEqual(CAPACITY, count);

		_list.clear();
		iter = _list.iterator();
		Assert.isFalse(iter.hasNext());
	}

	public void testLastIndexOf() throws Exception {
		Assert.areEqual(0, _list.indexOf(new Integer(0)));
		Assert.areEqual(CAPACITY / 2, _list.lastIndexOf(new Integer(
				CAPACITY / 2)));
		Assert.areEqual(CAPACITY / 3, _list.lastIndexOf(new Integer(
				CAPACITY / 3)));
		Assert.areEqual(CAPACITY / 4, _list.lastIndexOf(new Integer(
				CAPACITY / 4)));

		Assert.areEqual(-1, _list.lastIndexOf(new Integer(-1)));
		Assert.areEqual(-1, _list.lastIndexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, _list.lastIndexOf(null));

		_list.add(new Integer(0));
		_list.add(new Integer(CAPACITY / 2));
		_list.add(new Integer(CAPACITY / 3));
		_list.add(new Integer(CAPACITY / 4));

		Assert.areEqual(CAPACITY, _list.lastIndexOf(new Integer(0)));
		Assert.areEqual(CAPACITY + 1, _list.lastIndexOf(new Integer(
				CAPACITY / 2)));
		Assert.areEqual(CAPACITY + 2, _list.lastIndexOf(new Integer(
				CAPACITY / 3)));
		Assert.areEqual(CAPACITY + 3, _list.lastIndexOf(new Integer(
				CAPACITY / 4)));

		Assert.areEqual(-1, _list.lastIndexOf(new Integer(-1)));
		Assert.areEqual(-1, _list.lastIndexOf(new Integer(CAPACITY)));

		// returns false because current data doesn't contain null.
		// Quotes from j.u.List spec: More formally, returns the lowest index i
		// such that (o==null ? get(i)==null : o.equals(get(i))), or -1 if there
		// is no such index.
		Assert.areEqual(-1, _list.lastIndexOf(null));
	}

	public void testRemove_Object() throws Exception {
		_list.remove(new Integer(0));
		Assert.areEqual(new Integer(1), _list.get(0));

		Assert.areEqual(CAPACITY - 1, _list.size());

		_list.remove(new Integer(43));
		Assert.areEqual(new Integer(44), _list.get(42));
		Assert.areEqual(new Integer(42), _list.get(41));
		Assert.areEqual(CAPACITY - 2, _list.size());

		for (int i = 0; i < CAPACITY - 2; ++i) {
			_list.remove(_list.get(0));
			Assert.areEqual(CAPACITY - 3 - i, _list.size());
		}
		Assert.isTrue(_list.isEmpty());
	}

	public void testRemove_I() throws Exception {
		_list.remove(0);
		Assert.areEqual(new Integer(1), _list.get(0));
		Assert.isFalse(_list.contains(new Integer(0)));
		Assert.areEqual(CAPACITY - 1, _list.size());

		_list.remove(42);
		Assert.areEqual(new Integer(44), _list.get(42));
		Assert.areEqual(new Integer(42), _list.get(41));
		Assert.isFalse(_list.contains(new Integer(43)));
		Assert.areEqual(CAPACITY - 2, _list.size());

		for (int i = 0; i < CAPACITY - 2; ++i) {
			_list.remove(0);
			Assert.areEqual(CAPACITY - 3 - i, _list.size());
		}
		Assert.isTrue(_list.isEmpty());
	}

	public void testRemoveAll() throws Exception {
		Vector v = new Vector();

		_list.removeAll(v);
		Assert.areEqual(CAPACITY, _list.size());

		v.add(new Integer(0));
		v.add(new Integer(42));
		_list.removeAll(v);
		Assert.isFalse(_list.contains(new Integer(0)));
		Assert.isFalse(_list.contains(new Integer(42)));
		Assert.areEqual(CAPACITY - 2, _list.size());

		v.add(new Integer(1));
		v.add(new Integer(2));
		_list.removeAll(v);
		Assert.isFalse(_list.contains(new Integer(1)));
		Assert.isFalse(_list.contains(new Integer(2)));
		Assert.areEqual(CAPACITY - 4, _list.size());

		for (int i = 0; i < CAPACITY; ++i) {
			v.add(new Integer(i));
		}
		_list.removeAll(v);
		Assert.isTrue(_list.isEmpty());
	}

	public void testRetainAll() throws Exception {
		Vector v = new Vector();
		v.add(new Integer(0));
		v.add(new Integer(42));

		boolean ret = _list.retainAll(_list);
		Assert.isFalse(ret);
		Assert.areEqual(100, _list.size());
		for (int i = 0; i < CAPACITY; ++i) {
			Assert.isTrue(_list.contains(new Integer(i)));
		}

		ret = _list.retainAll(v);
		Assert.isTrue(ret);
		Assert.areEqual(2, _list.size());
		_list.contains(new Integer(0));
		_list.contains(new Integer(42));

		ret = _list.retainAll(v);
		Assert.isFalse(ret);
		_list.contains(new Integer(0));
		_list.contains(new Integer(42));
	}

	public void testSet() throws Exception {
		Integer element = new Integer(1);
		_list.set(0, element);
		Assert.areSame(element, _list.get(0));

		_list.set(42, element);
		Assert.areSame(element, _list.get(42));

		for (int i = 0; i < CAPACITY; ++i) {
			element = new Integer(i);
			_list.set(i, element);
			Assert.areSame(element, _list.get(i));
		}

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.set(-1, new Integer(0));
			}
		});

		Assert.expect(IndexOutOfBoundsException.class, new CodeBlock() {
			public void run() throws Throwable {
				_list.set(CAPACITY, new Integer(0));
			}
		});
	}

	public void testSize() throws Exception {
		Assert.areEqual(CAPACITY, _list.size());
		for (int i = 0; i < CAPACITY; ++i) {
			_list.remove(0);
			Assert.areEqual(CAPACITY - 1 - i, _list.size());
		}
		for (int i = 0; i < CAPACITY; ++i) {
			_list.add(new Integer(i));
			Assert.areEqual(i + 1, _list.size());
		}
	}
	
	public void testToArray() throws Exception {
		Object[] array = _list.toArray();
		Assert.areEqual(CAPACITY, array.length);
		for(int i = 0; i < CAPACITY; ++i) {
			Integer element = (Integer) array[i];
			Assert.areEqual(new Integer(i), element);
		}
		
		_list.clear();
		array = _list.toArray();
		Assert.areEqual(0, array.length);
	}
	
	public void testToArray_LObject() throws Exception {
		Object[] array1;
		Object[] array2 = new Object[CAPACITY];
		array1 = _list.toArray(array2);
		Assert.areSame(array1, array2);
		Assert.areEqual(CAPACITY, array2.length);
		for(int i = 0; i < CAPACITY; ++i) {	
			Integer element = (Integer) array2[i];
			Assert.areEqual(new Integer(i), element);
		}
		
		_list.clear();
		
		array1 = new Object[0];
		array2 = new Object[CAPACITY];
		array1 = _list.toArray(array2);
		Assert.areSame(array1, array2);
		Assert.areEqual(CAPACITY, array1.length);
		
		array2 = new Object[0];
		array1 = _list.toArray(array2);
		Assert.areEqual(0, array1.length);
		
		
	}
}
