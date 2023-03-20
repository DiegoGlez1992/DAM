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
package com.db4o.test.legacy.soda.wrapper.typed;


import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STBooleanWT implements STClass{
	
	final static String DESCENDANT = "i_boolean";

	public static transient SodaTest st;
	
	Boolean i_boolean;
	
	public STBooleanWT(){
	}
	
	private STBooleanWT(boolean a_boolean){
		i_boolean = new Boolean(a_boolean);
	}
	
	public Object[] store() {
		return new Object[]{
			new STBooleanWT(false),
			new STBooleanWT(true),
			new STBooleanWT(false),
			new STBooleanWT(false),
			new STBooleanWT()
		};
	}
	
	public void testEqualsTrue(){
		Query q = st.query();
		q.constrain(new STBooleanWT(true));  
		Object[] r = store();
		st.expectOne(q, new STBooleanWT(true));
	}
	
	public void testEqualsFalse(){
		Query q = st.query();
		q.constrain(new STBooleanWT(false));
		q.descend(DESCENDANT).constrain(new Boolean(false));
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	public void testNull(){
		Query q = st.query();
		q.constrain(new STBooleanWT());
		q.descend(DESCENDANT).constrain(null);
		Object[] r = store();
		st.expectOne(q, new STBooleanWT());
	}
	
	public void testNullOrTrue(){
		Query q = st.query();
		q.constrain(new STBooleanWT());
		Query qd = q.descend(DESCENDANT);
		qd.constrain(null).or(qd.constrain(new Boolean(true)));
		Object[] r = store();
		st.expect(q, new Object[] {r[1], r[4]});
	}
	
	public void testNotNullAndFalse(){
		Query q = st.query();
		q.constrain(new STBooleanWT());
		Query qd = q.descend(DESCENDANT);
		qd.constrain(null).not().and(qd.constrain(new Boolean(false)));
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
}

