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
package com.db4o.test.legacy.soda.arrays.untyped;

import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STArrMixedN implements STClass {

	public static transient SodaTest st;
	
	Object[][][] arr;

	public STArrMixedN() {
	}

	public STArrMixedN(Object[][][] arr) {
		this.arr = arr;
	}

	public Object[] store() {
		STArrMixedN[] arrMixed = new STArrMixedN[5];
		
		arrMixed[0] = new STArrMixedN();
		
		Object[][][] content = new Object[1][1][2];
		arrMixed[1] = new STArrMixedN(content);
		
		content = new Object[2][2][3];
		arrMixed[2] = new STArrMixedN(content);
		
		content = new Object[2][2][3];
		content[0][0][1] = "foo";
		content[0][1][0] = "bar";
		content[0][1][2] = "fly";
		content[1][0][0] = new Boolean(false);
		arrMixed[3] = new STArrMixedN(content);
		
		content = new Object[2][2][3];
		content[0][0][0] = "bar";
		content[0][1][0] = "wohay";
		content[0][1][1] = "johy";
		content[1][0][0] = new Integer(12);
		arrMixed[4] = new STArrMixedN(content);
		
		Object[] ret = new Object[arrMixed.length];
		System.arraycopy(arrMixed, 0, ret, 0, arrMixed.length);
		return ret;
	}

	public void testDefaultContainsString() {
		Query q = st.query();
		Object[] r = store();
		Object[][][] content = new Object[1][1][1];
		content[0][0][0] = "bar";
		q.constrain(new STArrMixedN(content));
		st.expect(q, new Object[] { r[3], r[4] });
	}
	
	public void testDefaultContainsInteger() {
		Query q = st.query();
		Object[] r = store();
		Object[][][] content = new Object[1][1][1];
		content[0][0][0] = new Integer(12);
		q.constrain(new STArrMixedN(content));
		st.expect(q, new Object[] {  r[4] });
	}
	
	public void testDefaultContainsBoolean() {
		Query q = st.query();
		Object[] r = store();
		Object[][][] content = new Object[1][1][1];
		content[0][0][0] = new Boolean(false);
		q.constrain(new STArrMixedN(content));
		st.expect(q, new Object[] {  r[3] });
	}

	public void testDefaultContainsTwo() {
		Query q = st.query();
		Object[] r = store();
		Object[][][] content = new Object[2][1][1];
		content[0][0][0] = "bar";
		content[1][0][0] = new Integer(12);
		q.constrain(new STArrMixedN(content));
		st.expect(q, new Object[] { r[4] });
	}

	public void testDescendOne() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrMixedN.class);
		q.descend("arr").constrain("bar");
		st.expect(q, new Object[] { r[3], r[4] });
	}

	public void testDescendTwo() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrMixedN.class);
		Query qElements = q.descend("arr");
		qElements.constrain("foo");
		qElements.constrain("bar");
		st.expect(q, new Object[] { r[3] });
	}

	public void testDescendOneNot() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrMixedN.class);
		q.descend("arr").constrain("bar").not();
		st.expect(q, new Object[] { r[0], r[1], r[2] });
	}

	public void testDescendTwoNot() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrMixedN.class);
		Query qElements = q.descend("arr");
		qElements.constrain("foo").not();
		qElements.constrain("bar").not();
		st.expect(q, new Object[] { r[0], r[1], r[2] });
	}

}
