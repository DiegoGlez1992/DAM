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
package com.db4o.test.legacy.soda.arrays.object;

import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STArrIntegerWTON implements STClass{
	
	public static transient SodaTest st;
	
	Object intArr;
	
	public STArrIntegerWTON(){
	}
	
	public STArrIntegerWTON(Integer[][][] arr){
		intArr = arr;
	}
	
	public Object[] store() {
		STArrIntegerWTON[] arr = new STArrIntegerWTON[5];
		
		arr[0] = new STArrIntegerWTON();
		
		Integer[][][] content = new Integer[0][0][0];
		arr[1] = new STArrIntegerWTON(content);
		
		content = new Integer[1][2][3];
		content[0][0][1] = new Integer(0);
		content[0][1][0] = new Integer(0);
		arr[2] = new STArrIntegerWTON(content);
		
		content = new Integer[1][2][3];
		content[0][0][0] = new Integer(1);
		content[0][1][0] = new Integer(17);
		content[0][1][1] = new Integer(Integer.MAX_VALUE - 1);
		arr[3] = new STArrIntegerWTON(content);
		
		content = new Integer[1][2][2];
		content[0][0][0] = new Integer(3);
		content[0][0][1] = new Integer(17);
		content[0][1][0] = new Integer(25);
		content[0][1][1] = new Integer(Integer.MAX_VALUE - 2);
		arr[4] = new STArrIntegerWTON(content);
		
		Object[] ret = new Object[arr.length];
		System.arraycopy(arr, 0, ret, 0, arr.length);
		return ret;
	}
	
	
	public void testDefaultContainsOne(){
		Query q = st.query();
		Object[] r = store();
		Integer[][][] content = new Integer[1][1][1];
		content[0][0][0] = new Integer(17);
		q.constrain(new STArrIntegerWTON(content));
		st.expect(q, new Object[] {r[3], r[4]});
	}
	
	public void testDefaultContainsTwo(){
		Query q = st.query();
		Object[] r = store();
		Integer[][][] content = new Integer[2][1][1];
		content[0][0][0] = new Integer(17);
		content[1][0][0] = new Integer(25);
		q.constrain(new STArrIntegerWTON(content));
		st.expect(q, new Object[] {r[4]});
	}
	
	public void testDescendOne(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrIntegerWTON.class);
		q.descend("intArr").constrain(new Integer(17));
		st.expect(q, new Object[] {r[3], r[4]});
	}
	
	public void testDescendTwo(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrIntegerWTON.class);
		Query qElements = q.descend("intArr");
		qElements.constrain(new Integer(17));
		qElements.constrain(new Integer(25));
		st.expect(q, new Object[] {r[4]});
	}
	
	public void testDescendSmaller(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrIntegerWTON.class);
		Query qElements = q.descend("intArr");
		qElements.constrain(new Integer(3)).smaller();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testDescendNotSmaller(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(STArrIntegerWTON.class);
		Query qElements = q.descend("intArr");
		qElements.constrain(new Integer(3)).smaller();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
}
	
