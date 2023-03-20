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
import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;


/** RTH: Roundtrip Typed Hierarchy */
public class STRTH1TestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public STRTH2 h2;
	public String foo1;
	
	public STRTH1TestCase(){
	}
	
	public STRTH1TestCase(STRTH2 a2){
		h2 = a2;
	}
	
	public STRTH1TestCase(String str){
		foo1 = str;
	}
	
	public STRTH1TestCase(STRTH2 a2, String str){
		h2 = a2;
		foo1 = str;
	}
	
	public Object[] createData() {
		
		STRTH1TestCase[] objects = {
			new STRTH1TestCase(),
			new STRTH1TestCase("str1"),
			new STRTH1TestCase(new STRTH2()),
			new STRTH1TestCase(new STRTH2("str2")),
			new STRTH1TestCase(new STRTH2(new STRTH3("str3"))),
			new STRTH1TestCase(new STRTH2(new STRTH3("str3"), "str2"))
		};
		for (int i = 0; i < objects.length; i++) {
			objects[i].adjustParents();
			
		}
		return objects;
	}
	
	/** this is the special part of this test: circular references */
	void adjustParents(){
		if(h2 != null){
			h2.parent = this;
			if(h2.h3 != null){
				h2.h3.parent = h2;
				h2.h3.grandParent = this;
			}
		}
	}
	
	public void testStrNull(){
		Query q = newQuery();
		q.constrain(new STRTH1TestCase());
		q.descend("foo1").constrain(null);
		
		expect(q, new int[] {0, 2, 3, 4, 5});
	}

	public void testBothNull(){
		Query q = newQuery();
		q.constrain(new STRTH1TestCase());
		q.descend("foo1").constrain(null);
		q.descend("h2").constrain(null);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[0]);
	}

	public void testDescendantNotNull(){
		Query q = newQuery();
		
		q.constrain(new STRTH1TestCase());
		q.descend("h2").constrain(null).not();
		expect(q, new int[] {2, 3, 4, 5});
	}
	
	public void testDescendantDescendantNotNull(){
		Query q = newQuery();
		
		q.constrain(new STRTH1TestCase());
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
		
		q.constrain(new STRTH1TestCase(new STRTH2(new STRTH3())));
		expect(q, new int[] {4, 5});
	}
	
	public void testDescendantDescendantValue(){
		Query q = newQuery();
		
		q.constrain(new STRTH1TestCase(new STRTH2(new STRTH3("str3"))));
		expect(q, new int[] {4, 5});
	}
	
	public void testDescendantDescendantStringPath(){
		Query q = newQuery();
		
		q.constrain(new STRTH1TestCase());
		q.descend("h2").descend("h3").descend("foo3").constrain("str3");
		expect(q, new int[] {4, 5});
	}
	
	public void testSequentialAddition(){
		Query q = newQuery();
		
		q.constrain(new STRTH1TestCase());
		Query cur = q.descend("h2");
		cur.constrain(new STRTH2());
		cur.descend("foo2").constrain("str2");
		cur = cur.descend("h3");
		cur.constrain(new STRTH3());
		cur.descend("foo3").constrain("str3");
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[5]);
	}
	
	public void testTwoLevelOr(){
		Query q = newQuery();
		
		q.constrain(new STRTH1TestCase("str1"));
		q.descend("foo1").constraints().or(
			q.descend("h2").descend("h3").descend("foo3").constrain("str3")
		);
		expect(q, new int[] {1, 4, 5});
	}
	
	public void testThreeLevelOr(){
		Query q = newQuery();
		
		q.constrain(new STRTH1TestCase("str1"));
		q.descend("foo1").constraints().or(
			q.descend("h2").descend("foo2").constrain("str2")
		).or(
			q.descend("h2").descend("h3").descend("foo3").constrain("str3")
		);
		expect(q, new int[] {1, 3, 4, 5});
	}
	
	public void testDeleteAll() throws Exception {
		ExtObjectContainer oc = fixture().db(); 
		ObjectSet os = oc.queryByExample(null); 
		while(os.hasNext()) {
			oc.delete(os.next()); 
		}
		db().commit();
		fixture().reopen(this); 
		oc = fixture().db(); 
		os = oc.queryByExample(null);
		Assert.areEqual(0, os.size()); 
	}
	
	public static void main(String[] arguments) {
		new STRTH1TestCase().runSolo();
	}

}

