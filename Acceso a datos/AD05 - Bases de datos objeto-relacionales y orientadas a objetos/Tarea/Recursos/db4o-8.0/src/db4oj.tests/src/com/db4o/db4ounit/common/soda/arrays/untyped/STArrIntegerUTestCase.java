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


public class STArrIntegerUTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public Object[] intArr;
	
	public STArrIntegerUTestCase(){
	}
	
	public STArrIntegerUTestCase(Object[] arr){
		intArr = arr;
	}
	
	public Object[] createData() {
		return new Object[]{
			new STArrIntegerUTestCase(),
			new STArrIntegerUTestCase(new Object[0]),
			new STArrIntegerUTestCase(new Object[] {new Integer(0), new Integer(0)}),
			new STArrIntegerUTestCase(new Object[] {new Integer(1), new Integer(17), new Integer(Integer.MAX_VALUE - 1)}),
			new STArrIntegerUTestCase(new Object[] {new Integer(3), new Integer(17), new Integer(25), new Integer(Integer.MAX_VALUE - 2)})
		};
	}
	
	public void testDefaultContainsOne(){
		Query q = newQuery();
		
		q.constrain(new STArrIntegerUTestCase(new Object[] {new Integer(17)}));
		expect(q, new int[] {3, 4});
	}

	public void testDefaultContainsTwo(){
		Query q = newQuery();
		
		q.constrain(new STArrIntegerUTestCase(new Object[] {new Integer(17), new Integer(25)}));
		expect(q, new int[] {4});
	}
	
	public void testDescendOne(){
		Query q = newQuery();
		
		q.constrain(STArrIntegerUTestCase.class);
		q.descend("intArr").constrain(new Integer(17));
		expect(q, new int[] {3, 4});
	}
	
	public void testDescendTwo(){
		Query q = newQuery();
		
		q.constrain(STArrIntegerUTestCase.class);
		Query qElements = q.descend("intArr");
		qElements.constrain(new Integer(17));
		qElements.constrain(new Integer(25));
		expect(q, new int[] {4});
	}
	
	public void testDescendSmaller(){
		Query q = newQuery();
		
		q.constrain(STArrIntegerUTestCase.class);
		Query qElements = q.descend("intArr");
		qElements.constrain(new Integer(3)).smaller();
		expect(q, new int[] {2, 3});
	}
	
	
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	