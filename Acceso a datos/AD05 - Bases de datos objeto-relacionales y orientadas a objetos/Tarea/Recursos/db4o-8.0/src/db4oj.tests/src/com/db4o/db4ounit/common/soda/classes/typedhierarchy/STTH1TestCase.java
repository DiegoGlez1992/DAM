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
package com.db4o.db4ounit.common.soda.classes.typedhierarchy;
import com.db4o.query.*;


/** TH: Typed Hierarchy */
public class STTH1TestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase {
	
	public STTH2 h2;
	public String foo1;
	
	public STTH1TestCase(){
	}
	
	public STTH1TestCase(STTH2 a2){
		h2 = a2;
	}
	
	public STTH1TestCase(String str){
		foo1 = str;
	}
	
	public STTH1TestCase(STTH2 a2, String str){
		h2 = a2;
		foo1 = str;
	}
	
	public Object[] createData() {
		return new Object[]{
			new STTH1TestCase(),
			new STTH1TestCase("str1"),
			new STTH1TestCase(new STTH2()),
			new STTH1TestCase(new STTH2("str2")),
			new STTH1TestCase(new STTH2(new STTH3("str3"))),
			new STTH1TestCase(new STTH2(new STTH3("str3"), "str2"))
		};
	}
	
	public void testStrNull(){
		Query q = newQuery();
		q.constrain(new STTH1TestCase());
		q.descend("foo1").constrain(null);
		
		expect(q, new int[] {0, 2, 3, 4, 5});
	}

	public void testBothNull(){
		Query q = newQuery();
		q.constrain(new STTH1TestCase());
		q.descend("foo1").constrain(null);
		q.descend("h2").constrain(null);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[0]);
	}

	public void testDescendantNotNull(){
		Query q = newQuery();
		
		q.constrain(new STTH1TestCase());
		q.descend("h2").constrain(null).not();
		expect(q, new int[] {2, 3, 4, 5});
	}
	
	public void testDescendantDescendantNotNull(){
		Query q = newQuery();
		
		q.constrain(new STTH1TestCase());
		q.descend("h2").descend("h3").constrain(null).not();
		expect(q, new int[] {4, 5});
	}
	
	public void testDescendantExists(){
		Query q = newQuery();
		
		q.constrain(_array[2]);
		expect(q, new int[] {2, 3, 4, 5});
	}
	
	public void testDescendantValue(){
		Query q = newQuery();
		
		q.constrain(_array[3]);
		expect(q, new int[] {3, 5});
	}
	
	public void testDescendantDescendantExists(){
		Query q = newQuery();
		
		q.constrain(new STTH1TestCase(new STTH2(new STTH3())));
		expect(q, new int[] {4, 5});
	}
	
	public void testDescendantDescendantValue(){
		Query q = newQuery();
		
		q.constrain(new STTH1TestCase(new STTH2(new STTH3("str3"))));
		expect(q, new int[] {4, 5});
	}
	
	public void testDescendantDescendantStringPath(){
		Query q = newQuery();
		
		q.constrain(new STTH1TestCase());
		q.descend("h2").descend("h3").descend("foo3").constrain("str3");
		expect(q, new int[] {4, 5});
	}
	
	public void testSequentialAddition(){
		Query q = newQuery();
		
		q.constrain(new STTH1TestCase());
		Query cur = q.descend("h2");
		cur.constrain(new STTH2());
		cur.descend("foo2").constrain("str2");
		cur = cur.descend("h3");
		cur.constrain(new STTH3());
		cur.descend("foo3").constrain("str3");
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[5]);
	}
	
	public void testTwoLevelOr(){
		Query q = newQuery();
		
		q.constrain(new STTH1TestCase("str1"));
		q.descend("foo1").constraints().or(
			q.descend("h2").descend("h3").descend("foo3").constrain("str3")
		);
		expect(q, new int[] {1, 4, 5});
	}
	
	public void testThreeLevelOr(){
		Query q = newQuery();
		
		q.constrain(new STTH1TestCase("str1"));
		q.descend("foo1").constraints().or(
			q.descend("h2").descend("foo2").constrain("str2")
		).or(
			q.descend("h2").descend("h3").descend("foo3").constrain("str3")
		);
		expect(q, new int[] {1, 3, 4, 5});
	}
}

