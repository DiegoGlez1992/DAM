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

public class STLong implements STClass1{
	
	public static transient SodaTest st;
	
	public long i_long;
	
	public STLong(){
	}
	
	private STLong(long a_long){
		i_long = a_long;
	}
	
	public Object[] store() {
		return new Object[]{
			new STLong(Long.MIN_VALUE),
			new STLong(- 1),
			new STLong(0),
			new STLong(Long.MAX_VALUE - 1),
		};
	}
	
	public void testEquals(){
		Query q = st.query();
		q.constrain(new STLong(Long.MIN_VALUE));  
		st.expect(q, new Object[] {new STLong(Long.MIN_VALUE)});
	}
	
	public void testGreater(){
		Query q = st.query();
		q.constrain(new STLong(-1));
		q.descend("i_long").constraints().greater();
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testSmaller(){
		Query q = st.query();
		q.constrain(new STLong(1));
		q.descend("i_long").constraints().smaller();
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[1], r[2]});
	}

	public void testBetween() {
		Query q = st.query();
		q.constrain(new STLong());
		Query sub = q.descend("i_long");
		sub.constrain(new Long(-3)).greater();
		sub.constrain(new Long(3)).smaller();
		Object[] r = store();
		st.expect(q, new Object[]{r[1], r[2]});
	}

	public void testAnd() {
		Query q = st.query();
		q.constrain(new STLong());
		Query sub = q.descend("i_long");
		sub.constrain(new Long(-3)).greater().and(sub.constrain(new Long(3)).smaller());
		Object[] r = store();
		st.expect(q, new Object[]{r[1], r[2]});
	}

	public void testOr() {
		Query q = st.query();
		q.constrain(new STLong());
		Query sub = q.descend("i_long");
		sub.constrain(new Long(3)).greater().or(sub.constrain(new Long(-3)).smaller());
		Object[] r = store();
		st.expect(q, new Object[]{r[0], r[3]});
	}

}

