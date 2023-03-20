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
package com.db4o.test.legacy.soda.classes.simple;


import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STBoolean implements STClass1{
	
	public static transient SodaTest st;
	
	public boolean i_boolean;
	
	public STBoolean(){
	}
	
	private STBoolean(boolean a_boolean){
		i_boolean = a_boolean;
	}
	
	public Object[] store() {
		return new Object[]{
			new STBoolean(false),
			new STBoolean(true),
			new STBoolean(false),
			new STBoolean(false)
		};
	}
	
	public void testEqualsTrue(){
		Query q = st.query();
		q.constrain(new STBoolean(true));  
		store();
		st.expectOne(q, new STBoolean(true));
	}
	
	public void testEqualsFalse(){
		Query q = st.query();
		q.constrain(new STBoolean(false));
		q.descend("i_boolean").constrain(new Boolean(false));
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	
	
}

