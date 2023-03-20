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
import com.db4o.query.*;


public class STBooleanTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase{
	
	public boolean i_boolean;
	
	public STBooleanTestCase(){
	}
	
	private STBooleanTestCase(boolean a_boolean){
		i_boolean = a_boolean;
	}
	
	public Object[] createData() {
		return new Object[]{
			new STBooleanTestCase(false),
			new STBooleanTestCase(true),
			new STBooleanTestCase(false),
			new STBooleanTestCase(false)
		};
	}
	
	public void testEqualsTrue(){
		Query q = newQuery();
		q.constrain(new STBooleanTestCase(true));  
		
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STBooleanTestCase(true));
	}
	
	public void testEqualsFalse(){
		Query q = newQuery();
		q.constrain(new STBooleanTestCase(false));
		q.descend("i_boolean").constrain(new Boolean(false));
		
		expect(q, new int[] {0, 2, 3});
	}
	
	
	
}

