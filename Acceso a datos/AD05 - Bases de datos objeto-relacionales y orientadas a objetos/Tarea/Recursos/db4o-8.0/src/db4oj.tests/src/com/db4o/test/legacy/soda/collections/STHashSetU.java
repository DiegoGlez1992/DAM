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

@decaf.Remove(decaf.Platform.JDK11)
public class STHashSetU implements STClass {

	public static transient SodaTest st;
	
	Object col;

	public STHashSetU() {
	}
	
	public STHashSetU(Object[] arr) {
		col = new HashSet();
		for (int i = 0; i < arr.length; i++) {
			((HashSet)col).add(arr[i]);
		}
	}

	public Object[] store() {
		return new Object[] {
			new STHashSetU(),
			new STHashSetU(new Object[0]),
			new STHashSetU(new Object[] { new Integer(0), new Integer(0)}),
			new STHashSetU(
				new Object[] {
					new Integer(1),
					new Integer(17),
					new Integer(Integer.MAX_VALUE - 1)}),
			new STHashSetU(
				new Object[] {
					new Integer(3),
					new Integer(17),
					new Integer(25),
					new Integer(Integer.MAX_VALUE - 2)}),
			new STHashSetU(new Object[] { "foo", new STElement("bar", "barbar")}),
			new STHashSetU(new Object[] { "foo2", new STElement("bar", "barbar2")})
		};
	}

	public void testDefaultContainsInteger() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STHashSetU(new Object[] { new Integer(17)}));
		st.expect(q, new Object[] { r[3], r[4] });
	}

	public void testDefaultContainsString() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STHashSetU(new Object[] { "foo" }));
		st.expect(q, new Object[] { r[5] });
	}

	public void testDefaultContainsTwo() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STHashSetU(new Object[] { new Integer(17), new Integer(25)}));
		st.expect(q, new Object[] { r[4] });
	}

	public void testDescendOne() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STHashSetU.class);
		q.descend("col").constrain(new Integer(17));
		st.expect(q, new Object[] { r[3], r[4] });
	}

	public void testDescendTwo() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STHashSetU.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(17));
		qElements.constrain(new Integer(25));
		st.expect(q, new Object[] { r[4] });
	}

	public void testDescendSmaller() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STHashSetU.class);
		Query qElements = q.descend("col");
		qElements.constrain(new Integer(3)).smaller();
		st.expect(q, new Object[] { r[2], r[3] });
	}
	
	public void testDefaultContainsObject() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STHashSetU(new Object[] { new STElement("bar", null)}));
		st.expect(q, new Object[] { r[5], r[6] });
	}
	
	public void testDescendToObject() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STHashSetU());
		q.descend("col").descend("foo1").constrain("bar");
		st.expect(q, new Object[] { r[5], r[6] });
	}

}