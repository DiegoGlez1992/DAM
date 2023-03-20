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
package com.db4o.db4ounit.jre11.soda.wrapper.typed;

import com.db4o.query.*;

public class STBooleanWTTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	final static String DESCENDANT = "i_boolean";

	public Boolean i_boolean;
	
	public STBooleanWTTestCase(){
	}
	
	private STBooleanWTTestCase(boolean a_boolean){
		i_boolean = new Boolean(a_boolean);
	}
	
	public Object[] createData() {
		return new Object[]{
			new STBooleanWTTestCase(false),
			new STBooleanWTTestCase(true),
			new STBooleanWTTestCase(false),
			new STBooleanWTTestCase(false),
			new STBooleanWTTestCase()
		};
	}
	
	public void testEqualsTrue(){
		Query q = newQuery();
		q.constrain(new STBooleanWTTestCase(true));  
		
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STBooleanWTTestCase(true));
	}
	
	public void testEqualsFalse(){
		Query q = newQuery();
		q.constrain(new STBooleanWTTestCase(false));
		q.descend(DESCENDANT).constrain(new Boolean(false));
		
		expect(q, new int[] {0, 2, 3});
	}	
	
	/**
	 * @sharpen.ignore does not make sense in .net
	 */
	public void testNull(){
		Query q = newQuery();
		q.constrain(new STBooleanWTTestCase());
		q.descend(DESCENDANT).constrain(null);
		
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STBooleanWTTestCase());
	}
	
	/**
	 * @sharpen.ignore does not make sense in .net
	 */
	public void testNullOrTrue(){
		Query q = newQuery();
		q.constrain(new STBooleanWTTestCase());
		Query qd = q.descend(DESCENDANT);
		qd.constrain(null).or(qd.constrain(new Boolean(true)));
		
		expect(q, new int[] {1, 4});
	}
	
	/**
	 * @sharpen.ignore does not make sense in .net
	 */
	public void testNotNullAndFalse(){
		Query q = newQuery();
		q.constrain(new STBooleanWTTestCase());
		Query qd = q.descend(DESCENDANT);
		qd.constrain(null).not().and(qd.constrain(new Boolean(false)));
		
		expect(q, new int[] {0, 2, 3});
	}
	
}

