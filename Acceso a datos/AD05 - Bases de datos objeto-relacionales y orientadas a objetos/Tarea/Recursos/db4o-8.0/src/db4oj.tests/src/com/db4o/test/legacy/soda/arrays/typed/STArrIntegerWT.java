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
package com.db4o.test.legacy.soda.arrays.typed;

import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STArrIntegerWT implements STClass{
	
	public static transient SodaTest st;
	
	Integer[] intArr;
	
	public STArrIntegerWT(){
	}
	
	public STArrIntegerWT(Integer[] arr){
		intArr = arr;
	}
	
	public Object[] store() {
		return new Object[]{
			new STArrIntegerWT(),
			new STArrIntegerWT(new Integer[0]),
			new STArrIntegerWT(new Integer[] {new Integer(0), new Integer(0)}),
			new STArrIntegerWT(new Integer[] {new Integer(1), new Integer(17), new Integer(Integer.MAX_VALUE - 1)}),
			new STArrIntegerWT(new Integer[] {new Integer(3), new Integer(17), new Integer(25), new Integer(Integer.MAX_VALUE - 2)})
		};
	}
	
	public void testDefaultContainsOne(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STArrIntegerWT(new Integer[] {new Integer(17)}));
		st.expect(q, new Object[] {r[3], r[4]});
	}
	
	public void testDefaultContainsTwo(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(new STArrIntegerWT(new Integer[] {new Integer(17), new Integer(25)}));
		st.expect(q, new Object[] {r[4]});
	}
	
	
	
}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	