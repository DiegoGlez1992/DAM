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
package com.db4o.db4ounit.common.soda.arrays.untyped;
import com.db4o.query.*;


public class STArrStringUTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public Object[] strArr;
	
	public STArrStringUTestCase(){
	}
	
	public STArrStringUTestCase(Object[] arr){
		strArr = arr;
	}
	
	public Object[] createData() {
		return new Object[]{
			new STArrStringUTestCase(),
			new STArrStringUTestCase(new Object[] {null}),
			new STArrStringUTestCase(new Object[] {null, null}),
			new STArrStringUTestCase(new Object[] {"foo", "bar", "fly"}),
			new STArrStringUTestCase(new Object[] {null, "bar", "wohay", "johy"})
		};
	}

	public void testDefaultContainsOne(){
		Query q = newQuery();
		
		q.constrain(new STArrStringUTestCase(new Object[] {"bar"}));
		expect(q, new int[] {3, 4});
	}

	public void testDefaultContainsTwo(){
		Query q = newQuery();
		
		q.constrain(new STArrStringUTestCase(new Object[] {"foo", "bar"}));
		expect(q, new int[] {3});
	}
	
	public void testDescendOne(){
		Query q = newQuery();
		
		q.constrain(STArrStringUTestCase.class);
		q.descend("strArr").constrain("bar");
		expect(q, new int[] {3, 4});
	}
	
	public void testDescendTwo(){
		Query q = newQuery();
		
		q.constrain(STArrStringUTestCase.class);
		Query qElements = q.descend("strArr");
		qElements.constrain("foo");
		qElements.constrain("bar");
		expect(q, new int[] {3});
	}
	
	public void testDescendOneNot(){
		Query q = newQuery();
		
		q.constrain(STArrStringUTestCase.class);
		q.descend("strArr").constrain("bar").not();
		expect(q, new int[] {0, 1, 2});
	}
	
	public void testDescendTwoNot(){
		Query q = newQuery();
		
		q.constrain(STArrStringUTestCase.class);
		Query qElements = q.descend("strArr");
		qElements.constrain("foo").not();
		qElements.constrain("bar").not();
		expect(q, new int[] {0, 1, 2});
	}
	
}