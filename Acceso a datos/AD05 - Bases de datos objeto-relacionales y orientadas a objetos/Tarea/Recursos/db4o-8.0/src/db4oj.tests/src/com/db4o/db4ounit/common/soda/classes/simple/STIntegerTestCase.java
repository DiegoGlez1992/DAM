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
package com.db4o.db4ounit.common.soda.classes.simple;
import com.db4o.*;
import com.db4o.query.*;


public class STIntegerTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public int i_int;
	
	public STIntegerTestCase(){
	}
	
	private STIntegerTestCase(int a_int){
		i_int = a_int;
	}
	
	public Object[] createData() {
		return new Object[]{
			new STIntegerTestCase(0),
			new STIntegerTestCase(1),
			new STIntegerTestCase(99),
			new STIntegerTestCase(909)
		};
	}
	
	public void testEquals(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(0));  
		
		// Primitive default values are ignored, so we need an 
		// additional constraint:
		q.descend("i_int").constrain(new Integer(0));
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[0]);
	}
	
	public void testNotEquals(){
		Query q = newQuery();
		
		q.constrain(_array[0]);
		q.descend("i_int").constrain(new Integer(0)).not();
		expect(q, new int[] {1, 2, 3});
	}
	
	public void testGreater(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(9));
		q.descend("i_int").constraints().greater();
		
		expect(q, new int[] {2, 3});
	}
	
	public void testSmaller(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(1));
		q.descend("i_int").constraints().smaller();
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[0]);
	}
	
	public void testContains(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(9));
		q.descend("i_int").constraints().contains();
		
		expect(q, new int[] {2, 3});
	}
	
	public void testNotContains(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(0));
		q.descend("i_int").constrain(new Integer(0)).contains().not();
		
		expect(q, new int[] {1, 2});
	}
	
	public void testLike(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(90));
		q.descend("i_int").constraints().like();
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STIntegerTestCase(909));
		q = newQuery();
		q.constrain(new STIntegerTestCase(10));
		q.descend("i_int").constraints().like();
		expect(q, new int[] {});
	}
	
	public void testNotLike(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(1));
		q.descend("i_int").constraints().like().not();
		
		expect(q, new int[] {0, 2, 3});
	}
	
	public void testIdentity(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(1));
		ObjectSet set = q.execute();
		STIntegerTestCase identityConstraint = (STIntegerTestCase)set.next();
		identityConstraint.i_int = 9999;
		q = newQuery();
		q.constrain(identityConstraint).identity();
		identityConstraint.i_int = 1;
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q,_array[1]);
	}
	
	public void testNotIdentity(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(1));
		ObjectSet set = q.execute();
		STIntegerTestCase identityConstraint = (STIntegerTestCase)set.next();
		identityConstraint.i_int = 9080;
		q = newQuery();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.i_int = 1;
		
		expect(q, new int[] {0, 2, 3});
	}
	
	public void testConstraints(){
		Query q = newQuery();
		q.constrain(new STIntegerTestCase(1));
		q.constrain(new STIntegerTestCase(0));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if(csa.length != 2){
			db4ounit.Assert.fail("Constraints not returned");
		}
	}
	
}

