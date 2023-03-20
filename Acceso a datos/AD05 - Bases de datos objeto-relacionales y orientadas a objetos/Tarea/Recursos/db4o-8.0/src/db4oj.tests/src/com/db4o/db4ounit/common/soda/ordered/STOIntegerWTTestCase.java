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


public class STOIntegerWTTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public Integer i_int;
	
	public STOIntegerWTTestCase(){
	}
	
	private STOIntegerWTTestCase(int a_int){
		i_int = new Integer(a_int);
	}
	
	public Object[] createData() {
		return new Object[]{
			new STOIntegerWTTestCase(99),
			new STOIntegerWTTestCase(1),
			new STOIntegerWTTestCase(909),
			new STOIntegerWTTestCase(1001),
			new STOIntegerWTTestCase(0),
			new STOIntegerWTTestCase(1010),
			new STOIntegerWTTestCase()
		};
	}
	
	/**
	 * @sharpen.ignore test case not applicable to .net
	 */
	public void testAscending() {
		Query q = newQuery();
		q.constrain(STOIntegerWTTestCase.class);
		q.descend("i_int").orderAscending();
		
		expectOrdered(q, new int[] { 6, 4, 1, 0, 2, 3, 5 });
	}
	
	public void testDescending() {
		Query q = newQuery();
		q.constrain(STOIntegerWTTestCase.class);
		q.descend("i_int").orderDescending();
		
		expectOrdered(q, new int[] { 5, 3, 2, 0, 1, 4, 6 });
	}
	
	public void testAscendingGreater(){
		Query q = newQuery();
		q.constrain(STOIntegerWTTestCase.class);
		Query qInt = q.descend("i_int");
		qInt.constrain(new Integer(100)).greater();
		qInt.orderAscending();
		
		expectOrdered(q, new int[] { 2, 3, 5 });
	}
}

