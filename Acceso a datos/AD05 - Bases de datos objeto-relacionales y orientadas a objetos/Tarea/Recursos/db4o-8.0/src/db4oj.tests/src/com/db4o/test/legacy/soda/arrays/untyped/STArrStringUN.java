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

public class STArrStringUN implements STClass {

	public static transient SodaTest st;
	
	Object[][][] strArr;

	public STArrStringUN() {
	}

	public STArrStringUN(Object[][][] arr) {
		strArr = arr;
	}

	public Object[] store() {
		STArrStringUN[] arr = new STArrStringUN[5];
		
		arr[0] = new STArrStringUN();
		
		String[][][] content = new String[1][1][2];
		arr[1] = new STArrStringUN(content);
		
		content = new String[1][2][3];
		arr[2] = new STArrStringUN(content);
		
		content = new String[1][2][3];
		content[0][0][1] = "foo";
		content[0][1][0] = "bar";
		content[0][1][2] = "fly";
		arr[3] = new STArrStringUN(content);
		
		content = new String[1][2][3];
		content[0][0][0] = "bar";
		content[0][1][0] = "wohay";
		content[0][1][1] = "johy";
		arr[4] = new STArrStringUN(content);
		
		Object[] ret = new Object[arr.length];
		System.arraycopy(arr, 0, ret, 0, arr.length);
		return ret;
	}

	public void testDefaultContainsOne() {
		Query q = st.query();
		Object[] r = store();
		String[][][] content = new String[1][1][1];
		content[0][0][0] = "bar";
		q.constrain(new STArrStringUN(content));
		st.expect(q, new Object[] { r[3], r[4] });
	}

	public void testDefaultContainsTwo() {
		Query q = st.query();
		Object[] r = store();
		String[][][] content = new String[2][1][1];
		content[0][0][0] = "bar";
		content[1][0][0] = "foo";
		q.constrain(new STArrStringUN(content));
		st.expect(q, new Object[] { r[3] });
	}

	public void testDescendOne() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrStringUN.class);
		q.descend("strArr").constrain("bar");
		st.expect(q, new Object[] { r[3], r[4] });
	}

	public void testDescendTwo() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrStringUN.class);
		Query qElements = q.descend("strArr");
		qElements.constrain("foo");
		qElements.constrain("bar");
		st.expect(q, new Object[] { r[3] });
	}

	public void testDescendOneNot() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrStringUN.class);
		q.descend("strArr").constrain("bar").not();
		st.expect(q, new Object[] { r[0], r[1], r[2] });
	}

	public void testDescendTwoNot() {
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrStringUN.class);
		Query qElements = q.descend("strArr");
		qElements.constrain("foo").not();
		qElements.constrain("bar").not();
		st.expect(q, new Object[] { r[0], r[1], r[2] });
	}

}
