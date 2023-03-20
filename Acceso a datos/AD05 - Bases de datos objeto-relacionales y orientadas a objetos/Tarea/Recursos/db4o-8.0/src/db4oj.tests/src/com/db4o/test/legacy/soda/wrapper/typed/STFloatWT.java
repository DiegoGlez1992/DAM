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

public class STFloatWT implements STClass{
	
	public static transient SodaTest st;
	
	Float i_float;
	
	public STFloatWT(){
	}
	
	private STFloatWT(float a_float){
		i_float = new Float(a_float);
	}
	
	public Object[] store() {
		return new Object[]{
			new STFloatWT(Float.MIN_VALUE),
			new STFloatWT((float) 0.0000123),
			new STFloatWT((float) 1.345),
			new STFloatWT(Float.MAX_VALUE),
		};
	}
	
	public void testEquals(){
		Query q = st.query();
		q.constrain(store()[0]); 
		st.expectOne(q, store()[0]);
	}
	
	public void testGreater(){
		Query q = st.query();
		q.constrain(new STFloatWT((float)0.1));
		q.descend("i_float").constraints().greater();
		Object[] r = store();
		st.expect(q, new Object[] { r[2], r[3]});
	}
	
	public void testSmaller(){
		Query q = st.query();
		q.constrain(new STFloatWT((float)1.5));
		q.descend("i_float").constraints().smaller();
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[1], r[2]});
	}
}

