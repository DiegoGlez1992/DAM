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

public class STArrStringU implements STClass{
	
	public static transient SodaTest st;
	
	Object[] strArr;
	
	public STArrStringU(){
	}
	
	public STArrStringU(Object[] arr){
		strArr = arr;
	}
	
	public Object[] store() {
		return new Object[]{
			new STArrStringU(),
			new STArrStringU(new Object[] {null}),
			new STArrStringU(new Object[] {null, null}),
			new STArrStringU(new Object[] {"foo", "bar", "fly"}),
			new STArrStringU(new Object[] {null, "bar", "wohay", "johy"})
		};
	}

	public void testDefaultContainsOne(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STArrStringU(new Object[] {"bar"}));
		st.expect(q, new Object[] {r[3], r[4]});
	}

	public void testDefaultContainsTwo(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STArrStringU(new Object[] {"foo", "bar"}));
		st.expect(q, new Object[] {r[3]});
	}
	
	public void testDescendOne(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrStringU.class);
		q.descend("strArr").constrain("bar");
		st.expect(q, new Object[] {r[3], r[4]});
	}
	
	public void testDescendTwo(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrStringU.class);
		Query qElements = q.descend("strArr");
		qElements.constrain("foo");
		qElements.constrain("bar");
		st.expect(q, new Object[] {r[3]});
	}
	
	public void testDescendOneNot(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrStringU.class);
		q.descend("strArr").constrain("bar").not();
		st.expect(q, new Object[] {r[0], r[1], r[2]});
	}
	
	public void testDescendTwoNot(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrStringU.class);
		Query qElements = q.descend("strArr");
		qElements.constrain("foo").not();
		qElements.constrain("bar").not();
		st.expect(q, new Object[] {r[0], r[1], r[2]});
	}
	
}