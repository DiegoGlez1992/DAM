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
package com.db4o.test.legacy.soda.wrapper.untyped;

import java.util.*;

import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STDateU implements STClass{
	
	public static transient SodaTest st;
	
	Object i_date;
	
	public STDateU(){
	}
	
	private STDateU(Date a_date){
		i_date = a_date;
	}
	
	public Object[] store() {
		return new Object[]{
			new STDateU(null),
			new STDateU(new Date(4000)),
			new STDateU(new Date(5000)),
			new STDateU(new Date(6000)),
			new STDateU(new Date(7000)),
		};
	}
	
	public void testEquals(){
		Query q = st.query();
		q.constrain(store()[1]); 
		st.expectOne(q, store()[1]);
	}
	
	public void testGreater(){
		Query q = st.query();
		q.constrain(store()[2]);
		q.descend("i_date").constraints().greater();
		Object[] r = store();
		st.expect(q, new Object[] { r[3], r[4]});
	}
	
	public void testSmaller(){
		Query q = st.query();
		q.constrain(store()[4]);
		q.descend("i_date").constraints().smaller();
		Object[] r = store();
		st.expect(q, new Object[] {r[1], r[2], r[3]});
	}
	
	public void testNotGreaterOrEqual(){
		Query q = st.query();
		q.constrain(store()[3]);
		q.descend("i_date").constraints().not().greater().equal();
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[1], r[2]});
	}
	
	public void testNull(){
		Query q = st.query();
		q.constrain(new STDateU());
		q.descend("i_date").constrain(null);
		st.expectOne(q, new STDateU(null));
	}
	
	public void testNotNull(){
		Query q = st.query();
		q.constrain(new STDateU());
		q.descend("i_date").constrain(null).not();
		Object[] r = store();
		st.expect(q, new Object[] {r[1], r[2], r[3], r[4]});
	}
}

