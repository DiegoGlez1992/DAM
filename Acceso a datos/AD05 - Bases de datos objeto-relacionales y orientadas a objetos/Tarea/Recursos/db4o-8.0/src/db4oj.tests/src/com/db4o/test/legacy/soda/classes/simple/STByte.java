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

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STByte implements STClass1{
	
	final static String DESCENDANT = "i_byte";
	
	public static transient SodaTest st;
	
	public byte i_byte;
	
	
	public STByte(){
	}
	
	private STByte(byte a_byte){
		i_byte = a_byte;
	}
	
	public Object[] store() {
		return new Object[]{
			new STByte((byte)0),
			new STByte((byte)1),
			new STByte((byte)99),
			new STByte((byte)113)
		};
	}
	
	public void testEquals(){
		Query q = st.query();
		q.constrain(new STByte((byte)0));  
		
		// Primitive default values are ignored, so we need an 
		// additional constraint:
		q.descend(DESCENDANT).constrain(new Byte((byte)0));
		st.expectOne(q, store()[0]);
	}
	
	public void testNotEquals(){
		Query q = st.query();
		Object[] r = store();
		q.constrain(r[0]);
		q.descend(DESCENDANT).constrain(new Byte((byte)0)).not();
		st.expect(q, new Object[] {r[1], r[2], r[3]});
	}
	
	public void testGreater(){
		Query q = st.query();
		q.constrain(new STByte((byte)9));
		q.descend(DESCENDANT).constraints().greater();
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testSmaller(){
		Query q = st.query();
		q.constrain(new STByte((byte)1));
		q.descend(DESCENDANT).constraints().smaller();
		st.expectOne(q, store()[0]);
	}
	
	public void testContains(){
		Query q = st.query();
		q.constrain(new STByte((byte)9));
		q.descend(DESCENDANT).constraints().contains();
		Object[] r = store();
		st.expect(q, new Object[] {r[2]});
	}
	
	public void testNotContains(){
		Query q = st.query();
		q.constrain(new STByte((byte)0));
		q.descend(DESCENDANT).constrain(new Byte((byte)0)).contains().not();
		Object[] r = store();
		st.expect(q, new Object[] {r[1], r[2], r[3]});
	}
	
	public void testLike(){
		Query q = st.query();
		q.constrain(new STByte((byte)11));
		q.descend(DESCENDANT).constraints().like();
		st.expectOne(q, new STByte((byte)113));
		q = st.query();
		q.constrain(new STByte((byte)10));
		q.descend(DESCENDANT).constraints().like();
		st.expectNone(q);
	}
	
	public void testNotLike(){
		Query q = st.query();
		q.constrain(new STByte((byte)1));
		q.descend(DESCENDANT).constraints().like().not();
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2]});
	}
	
	public void testIdentity(){
		Query q = st.query();
		q.constrain(new STByte((byte)1));
		ObjectSet set = q.execute();
		STByte identityConstraint = (STByte)set.next();
		identityConstraint.i_byte = 102;
		q = st.query();
		q.constrain(identityConstraint).identity();
		identityConstraint.i_byte = 1;
		st.expectOne(q,store()[1]);
	}
	
	public void testNotIdentity(){
		Query q = st.query();
		q.constrain(new STByte((byte)1));
		ObjectSet set = q.execute();
		STByte identityConstraint = (STByte)set.next();
		identityConstraint.i_byte = 102;
		q = st.query();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.i_byte = 1;
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	public void testConstraints(){
		Query q = st.query();
		q.constrain(new STByte((byte)1));
		q.constrain(new STByte((byte)0));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if(csa.length != 2){
			st.error("Constraints not returned");
		}
	}
	
}

