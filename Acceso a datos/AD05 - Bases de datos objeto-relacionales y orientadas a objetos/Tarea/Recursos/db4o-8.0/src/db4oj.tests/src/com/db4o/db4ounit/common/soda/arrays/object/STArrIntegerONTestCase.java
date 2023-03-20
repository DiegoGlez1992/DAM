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
package com.db4o.db4ounit.common.soda.arrays.object;
import com.db4o.query.*;


public class STArrIntegerONTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public Object intArr;
	
	public STArrIntegerONTestCase(){
	}
	
	public STArrIntegerONTestCase(int[][][] arr){
		intArr = arr;
	}
	
	public Object[] createData() {
		STArrIntegerONTestCase[] arr = new STArrIntegerONTestCase[5];
		
		arr[0] = new STArrIntegerONTestCase();
		
		int[][][] content = new int[0][0][0];
		arr[1] = new STArrIntegerONTestCase(content);
		
		content = new int[1][2][3];
		content[0][0][1] = 0;
		content[0][1][0] = 0;
		arr[2] = new STArrIntegerONTestCase(content);
		
		content = new int[1][2][3];
		content[0][0][0] = 1;
		content[0][1][0] = 17;
		content[0][1][1] = Integer.MAX_VALUE - 1;
		arr[3] = new STArrIntegerONTestCase(content);
		
		content = new int[1][2][2];
		content[0][0][0] = 3;
		content[0][0][1] = 17;
		content[0][1][0] = 25;
		content[0][1][1] = Integer.MAX_VALUE - 2;
		arr[4] = new STArrIntegerONTestCase(content);
		
		Object[] ret = new Object[arr.length];
		System.arraycopy(arr, 0, ret, 0, arr.length);
		return ret;
	}
	
	
	public void testDefaultContainsOne(){
		Query q = newQuery();
		
		int[][][] content = new int[1][1][1];
		content[0][0][0] = 17;
		q.constrain(new STArrIntegerONTestCase(content));
		expect(q, new int[] {3, 4});
	}
	
	public void testDefaultContainsTwo(){
		Query q = newQuery();
		
		int[][][] content = new int[2][1][1];
		content[0][0][0] = 17;
		content[1][0][0] = 25;
		q.constrain(new STArrIntegerONTestCase(content));
		expect(q, new int[] {4});
	}
	
	public void testDescendOne(){
		Query q = newQuery();
		
		q.constrain(STArrIntegerONTestCase.class);
		q.descend("intArr").constrain(new Integer(17));
		expect(q, new int[] {3, 4});
	}
	
	public void testDescendTwo(){
		Query q = newQuery();
		
		q.constrain(STArrIntegerONTestCase.class);
		Query qElements = q.descend("intArr");
		qElements.constrain(new Integer(17));
		qElements.constrain(new Integer(25));
		expect(q, new int[] {4});
	}
	
	public void testDescendSmaller(){
		Query q = newQuery();
		
		q.constrain(STArrIntegerONTestCase.class);
		Query qElements = q.descend("intArr");
		qElements.constrain(new Integer(3)).smaller();
		expect(q, new int[] {2, 3});
	}
	
	public void testDescendNotSmaller(){
		Query q = newQuery();
		
		q.constrain(STArrIntegerONTestCase.class);
		Query qElements = q.descend("intArr");
		qElements.constrain(new Integer(3)).smaller();
		expect(q, new int[] {2, 3});
	}
	
}
	
