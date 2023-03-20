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
package com.db4o.test.legacy.soda.collections;

import java.util.*;

import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;
import com.db4o.test.util.*;

@decaf.Remove(decaf.Platform.JDK11)
public class STOwnCollectionW implements STClass {

	public static transient SodaTest st;
	
	Collection col;

	public STOwnCollectionW() {

	}

	public STOwnCollectionW(Object[] arr) {
		col = new MyCollection();
		for (int i = 0; i < arr.length; i++) {
			col.add(arr[i]);
		}
	}

	public Object[] store() {
		return new Object[] {
			new STOwnCollectionW(),
			new STOwnCollectionW(new Object[0]),
			new STOwnCollectionW(new Object[] { new Integer(0), new Integer(0)}),
			new STOwnCollectionW(
				new Object[] {
					new Integer(1),
					new Integer(17),
					new Integer(Integer.MAX_VALUE - 1)}),
			new STOwnCollectionW(
				new Object[] {
					new Integer(3),
					new Integer(17),
					new Integer(25),
					new Integer(Integer.MAX_VALUE - 2)}),
			new STOwnCollectionW(new Object[] { "foo", new STElement("bar", "barbar")}),
			new STOwnCollectionW(new Object[] { "foo2", new STElement("bar", "barbar2")})
		};
	}
	
	public void testDefaultContainsInteger() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STOwnCollectionW(new Object[] { new Integer(17)}));
		st.expect(q, new Object[] { r[3], r[4]});
	}

	public void testDefaultContainsString() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STOwnCollectionW(new Object[] { "foo" }));
		st.expect(q, new Object[] { r[5] });
	}

	public void testDefaultContainsTwo() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STOwnCollectionW(new Object[] { new Integer(17), new Integer(25)}));
		st.expect(q, new Object[] { r[4] });
	}

	public void testDescendOne() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STOwnCollectionW.class);
		q.descend("col").constrain(new Integer(17));
		st.expect(q, new Object[] { r[3], r[4] });
	}

	public void testDescendTwo() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STOwnCollectionW.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(17));
		qElements.constrain(new Integer(25));
		st.expect(q, new Object[] { r[4] });
	}

	public void testDescendSmaller() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STOwnCollectionW.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(3)).smaller();
		st.expect(q, new Object[] { r[2], r[3] });
	}

	public void testDefaultContainsObject() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STOwnCollectionW(new Object[] { new STElement("bar", null)}));
		st.expect(q, new Object[] { r[5], r[6] });
	}

	public void testDescendToObject() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STOwnCollectionW());
		q.descend("col").descend("foo1").constrain("bar");
		st.expect(q, new Object[] { r[5], r[6] });
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
			if(o instanceof MyCollection){
				return new TCompare().isEqual(myList, ((MyCollection)o).myList);
			}
			return false;
		}

		public int hashCode() {
			return myList.hashCode();
		}
	}
}
