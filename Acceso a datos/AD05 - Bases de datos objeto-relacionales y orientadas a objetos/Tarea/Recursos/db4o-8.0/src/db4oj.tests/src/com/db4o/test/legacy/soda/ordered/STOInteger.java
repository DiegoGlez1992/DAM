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
package com.db4o.test.legacy.soda.ordered;


import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STOInteger implements STClass{
	
	public static transient SodaTest st;
	
	int i_int;
	
	public STOInteger(){
	}
	
	private STOInteger(int a_int){
		i_int = a_int;
	}
	
	public String toString(){
		return "STInteger: " + i_int;
	}
	
	public Object[] store() {
		return new Object[]{
			new STOInteger(1001),
			new STOInteger(99),
			new STOInteger(1),
			new STOInteger(909),
			new STOInteger(1001),
			new STOInteger(0),
			new STOInteger(1010),
		};
	}
	
	public void testAscending() {
		Query q = st.query();
		q.constrain(STOInteger.class);
		q.descend("i_int").orderAscending();
		Object[] r = store();
		st.expectOrdered(q, new Object[] { r[5], r[2],  r[1], r[3], r[0], r[4], r[6] });
	}
	
	public void testDescending() {
		Query q = st.query();
		q.constrain(STOInteger.class);
		q.descend("i_int").orderDescending();
		Object[] r = store();
		st.expectOrdered(q, new Object[] { r[6], r[4],  r[0], r[3], r[1], r[2], r[5] });
	}
	
	public void testAscendingGreater(){
		Query q = st.query();
		q.constrain(STOInteger.class);
		Query qInt = q.descend("i_int");
		qInt.constrain(new Integer(100)).greater();
		qInt.orderAscending();
		Object[] r = store();
		st.expectOrdered(q, new Object[] {r[3], r[0], r[4], r[6] });
	}
	
}

