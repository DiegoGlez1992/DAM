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
package com.db4o.db4ounit.jre12.soda.collections;
import java.util.*;

import com.db4o.query.*;



/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class STOwnCollectionTTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase {

	MyCollection col;

	public STOwnCollectionTTestCase() {

	}

	public STOwnCollectionTTestCase(Object[] arr) {
		col = new MyCollection();
		for (int i = 0; i < arr.length; i++) {
			col.add(arr[i]);
		}
	}
	
	public Object[] createData() {
		return new Object[] {
			new STOwnCollectionTTestCase(),
			new STOwnCollectionTTestCase(new Object[0]),
			new STOwnCollectionTTestCase(new Object[] { new Integer(0), new Integer(0)}),
			new STOwnCollectionTTestCase(
				new Object[] {
					new Integer(1),
					new Integer(17),
					new Integer(Integer.MAX_VALUE - 1)}),
			new STOwnCollectionTTestCase(
				new Object[] {
					new Integer(3),
					new Integer(17),
					new Integer(25),
					new Integer(Integer.MAX_VALUE - 2)}),
			new STOwnCollectionTTestCase(new Object[] { "foo", new STElement("bar", "barbar")}),
			new STOwnCollectionTTestCase(new Object[] { "foo2", new STElement("bar", "barbar2")})
		};
	}
	

	public void testDefaultContainsInteger() {
		Query q = newQuery();
		
		q.constrain(new STOwnCollectionTTestCase(new Object[] { new Integer(17)}));
		expect(q, new int[] { 3, 4 });
	}

	public void testDefaultContainsString() {
		Query q = newQuery();
		
		q.constrain(new STOwnCollectionTTestCase(new Object[] { "foo" }));
		expect(q, new int[] { 5 });
	}

	public void testDefaultContainsTwo() {
		Query q = newQuery();
		
		q.constrain(new STOwnCollectionTTestCase(new Object[] { new Integer(17), new Integer(25)}));
		expect(q, new int[] { 4 });
	}

	public void testDescendOne() {
		Query q = newQuery();
		
		q.constrain(STOwnCollectionTTestCase.class);
		q.descend("col").constrain(new Integer(17));
		expect(q, new int[] { 3, 4 });
	}

	public void testDescendTwo() {
		Query q = newQuery();
		
		q.constrain(STOwnCollectionTTestCase.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(17));
		qElements.constrain(new Integer(25));
		expect(q, new int[] { 4 });
	}

	public void testDescendSmaller() {
		Query q = newQuery();
		
		q.constrain(STOwnCollectionTTestCase.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(3)).smaller();
		expect(q, new int[] { 2, 3 });
	}

	public void testDefaultContainsObject() {
		Query q = newQuery();
		
		q.constrain(new STOwnCollectionTTestCase(new Object[] { new STElement("bar", null)}));
		expect(q, new int[] { 5, 6 });
	}

	public void testDescendToObject() {
		Query q = newQuery();
		
		q.constrain(new STOwnCollectionTTestCase());
		q.descend("col").descend("foo1").constrain("bar");
		expect(q, new int[] { 5, 6 });
	}

	public static class MyCollection implements Collection {
		
		ArrayList myList;
		
		public MyCollection(){
			myList = new ArrayList();
		}
		
		public int size() {
			return myList.size();
		}

		public boolean isEmpty() {
			return myList.isEmpty();
		}

		public boolean contains(Object o) {
			return myList.contains(o);
		}

		public Iterator iterator() {
			return myList.iterator();
		}

		public Object[] toArray() {
			return myList.toArray();
		}

		public Object[] toArray(Object[] a) {
			return myList.toArray(a);
		}

		public boolean add(Object o) {
			return myList.add(o);
		}

		public boolean remove(Object o) {
			return myList.remove(o);
		}

		public boolean containsAll(Collection c) {
			return myList.containsAll(c);
		}

		public boolean addAll(Collection c) {
			return myList.addAll(c);
		}

		public boolean removeAll(Collection c) {
			return myList.removeAll(c);
		}

		public boolean retainAll(Collection c) {
			return myList.retainAll(c);
		}

		public void clear() {
			myList.clear();
		}

		public boolean equals(Object o) {
			return myList.equals(o);
		}

		public int hashCode() {
			return myList.hashCode();
		}
	}
}
