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
package com.db4o.db4ounit.common.soda.ordered;
import com.db4o.query.*;


public class STOIntegerTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public int i_int;
	
	public STOIntegerTestCase(){
	}
	
	private STOIntegerTestCase(int a_int){
		i_int = a_int;
	}
	
	public String toString(){
		return "STInteger: " + i_int;
	}
	
	public Object[] createData() {
		return new Object[]{
			new STOIntegerTestCase(1001),
			new STOIntegerTestCase(99),
			new STOIntegerTestCase(1),
			new STOIntegerTestCase(909),
			new STOIntegerTestCase(1001),
			new STOIntegerTestCase(0),
			new STOIntegerTestCase(1010),
		};
	}
	
	public void testAscending() {
		Query q = newQuery();
		q.constrain(STOIntegerTestCase.class);
		q.descend("i_int").orderAscending();
		
		expectOrdered(q, new int[] { 5, 2,  1, 3, 0, 4, 6 });
	}
	
	public void testDescending() {
		Query q = newQuery();
		q.constrain(STOIntegerTestCase.class);
		q.descend("i_int").orderDescending();
		
		expectOrdered(q, new int[] { 6, 4,  0, 3, 1, 2, 5 });
	}
	
	public void testAscendingGreater(){
		Query q = newQuery();
		q.constrain(STOIntegerTestCase.class);
		Query qInt = q.descend("i_int");
		qInt.constrain(new Integer(100)).greater();
		qInt.orderAscending();
		
		expectOrdered(q, new int[] {3, 0, 4, 6 });
	}
	
}

