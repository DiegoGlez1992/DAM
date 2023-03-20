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
package com.db4o.test.legacy.soda.classes.typedhierarchy;

import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

/** TH: Typed Hierarchy */
public class STTH1 implements STClass1 {
	
	public static transient SodaTest st;
	
	public STTH2 h2;
	public String foo1;
	
	public STTH1(){
	}
	
	public STTH1(STTH2 a2){
		h2 = a2;
	}
	
	public STTH1(String str){
		foo1 = str;
	}
	
	public STTH1(STTH2 a2, String str){
		h2 = a2;
		foo1 = str;
	}
	
	public Object[] store() {
		return new Object[]{
			new STTH1(),
			new STTH1("str1"),
			new STTH1(new STTH2()),
			new STTH1(new STTH2("str2")),
			new STTH1(new STTH2(new STTH3("str3"))),
			new STTH1(new STTH2(new STTH3("str3"), "str2"))
		};
	}
	
	public void testStrNull(){
		Query q = st.query();
		q.constrain(new STTH1());
		q.descend("foo1").constrain(null);
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3], r[4], r[5]});
	}

	public void testBothNull(){
		Query q = st.query();
		q.constrain(new STTH1());
		q.descend("foo1").constrain(null);
		q.descend("h2").constrain(null);
		st.expectOne(q, store()[0]);
	}

	public void testDescendantNotNull(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STTH1());
		q.descend("h2").constrain(null).not();
		st.expect(q, new Object[] {r[2], r[3], r[4], r[5]});
	}
	
	public void testDescendantDescendantNotNull(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STTH1());
		q.descend("h2").descend("h3").constrain(null).not();
		st.expect(q, new Object[] {r[4], r[5]});
	}
	
	public void testDescendantExists(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(r[2]);
		st.expect(q, new Object[] {r[2], r[3], r[4], r[5]});
	}
	
	public void testDescendantValue(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(r[3]);
		st.expect(q, new Object[] {r[3], r[5]});
	}
	
	public void testDescendantDescendantExists(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STTH1(new STTH2(new STTH3())));
		st.expect(q, new Object[] {r[4], r[5]});
	}
	
	public void testDescendantDescendantValue(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STTH1(new STTH2(new STTH3("str3"))));
		st.expect(q, new Object[] {r[4], r[5]});
	}
	
	public void testDescendantDescendantStringPath(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STTH1());
		q.descend("h2").descend("h3").descend("foo3").constrain("str3");
		st.expect(q, new Object[] {r[4], r[5]});
	}
	
	public void testSequentialAddition(){
		Query q = st.query();
		store();
		q.constrain(new STTH1());
		Query cur = q.descend("h2");
		cur.constrain(new STTH2());
		cur.descend("foo2").constrain("str2");
		cur = cur.descend("h3");
		cur.constrain(new STTH3());
		cur.descend("foo3").constrain("str3");
		st.expectOne(q, store()[5]);
	}
	
	public void testTwoLevelOr(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STTH1("str1"));
		q.descend("foo1").constraints().or(
			q.descend("h2").descend("h3").descend("foo3").constrain("str3")
		);
		st.expect(q, new Object[] {r[1], r[4], r[5]});
	}
	
	public void testThreeLevelOr(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STTH1("str1"));
		q.descend("foo1").constraints().or(
			q.descend("h2").descend("foo2").constrain("str2")
		).or(
			q.descend("h2").descend("h3").descend("foo3").constrain("str3")
		);
		st.expect(q, new Object[] {r[1], r[3], r[4], r[5]});
	}
}

