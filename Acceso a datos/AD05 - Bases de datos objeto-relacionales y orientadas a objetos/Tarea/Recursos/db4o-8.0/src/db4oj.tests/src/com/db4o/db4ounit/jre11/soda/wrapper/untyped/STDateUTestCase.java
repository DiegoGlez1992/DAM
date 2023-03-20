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
package com.db4o.db4ounit.jre11.soda.wrapper.untyped;
import java.util.*;

import com.db4o.query.*;


public class STDateUTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public Object i_date;
	
	public STDateUTestCase(){
	}
	
	private STDateUTestCase(Date a_date){
		i_date = a_date;
	}
	
	public Object[] createData() {
		return new Object[]{
			new STDateUTestCase(null),
			new STDateUTestCase(new Date(4000)),
			new STDateUTestCase(new Date(5000)),
			new STDateUTestCase(new Date(6000)),
			new STDateUTestCase(new Date(7000)),
		};
	}
	
	public void testEquals(){
		Query q = newQuery();
		q.constrain(_array[1]); 
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[1]);
	}
	
	public void testGreater(){
		Query q = newQuery();
		q.constrain(_array[2]);
		q.descend("i_date").constraints().greater();
		
		expect(q, new int[] { 3, 4});
	}
	
	public void testSmaller(){
		Query q = newQuery();
		q.constrain(_array[4]);
		q.descend("i_date").constraints().smaller();
		
		expect(q, new int[] {1, 2, 3});
	}
	
	public void testNotGreaterOrEqual(){
		Query q = newQuery();
		q.constrain(_array[3]);
		q.descend("i_date").constraints().not().greater().equal();
		
		expect(q, new int[] {0, 1, 2});
	}
	
	public void testNull(){
		Query q = newQuery();
		q.constrain(new STDateUTestCase());
		q.descend("i_date").constrain(null);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STDateUTestCase(null));
	}
	
	public void testNotNull(){
		Query q = newQuery();
		q.constrain(new STDateUTestCase());
		q.descend("i_date").constrain(null).not();
		
		expect(q, new int[] {1, 2, 3, 4});
	}
}

