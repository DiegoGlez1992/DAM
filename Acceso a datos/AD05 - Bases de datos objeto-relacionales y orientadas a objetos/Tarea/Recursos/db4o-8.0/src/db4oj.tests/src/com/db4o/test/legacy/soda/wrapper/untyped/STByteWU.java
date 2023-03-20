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

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STByteWU implements STClass{
	
	final static String DESCENDANT = "i_byte";
	
	public static transient SodaTest st;
	
	Object i_byte;
	
	public STByteWU(){
	}
	
	private STByteWU(byte a_byte){
		i_byte = new Byte(a_byte);
	}
	
	public Object[] store() {
		return new Object[]{
			new STByteWU((byte)0),
			new STByteWU((byte)1),
			new STByteWU((byte)99),
			new STByteWU((byte)113),
		};
	}
	
	public void testEquals(){
		Query q = st.query();
		q.constrain(new STByteWU((byte)0));  
		st.expectOne(q, store()[0]);
	}
	
	public void testNotEquals(){
		Query q = st.query();
		Object[] r = store();
		Constraint c = q.constrain(r[0]);
		q.descend(DESCENDANT).constraints().not();
		st.expect(q, new Object[] {r[1], r[2], r[3]});
	}
	
	public void testGreater(){
		Query q = st.query();
		Constraint c = q.constrain(new STByteWU((byte)9));
		q.descend(DESCENDANT).constraints().greater();
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testSmaller(){
		Query q = st.query();
		Constraint c = q.constrain(new STByteWU((byte)1));
		q.descend(DESCENDANT).constraints().smaller();
		st.expectOne(q, store()[0]);
	}
	
	public void testContains(){
		Query q = st.query();
		Constraint c = q.constrain(new STByteWU((byte)9));
		q.descend(DESCENDANT).constraints().contains();
		Object[] r = store();
		st.expect(q, new Object[] {r[2]});
	}
	
	public void testNotContains(){
		Query q = st.query();
		Constraint c = q.constrain(new STByteWU((byte)0));
		q.descend(DESCENDANT).constraints().contains().not();
		Object[] r = store();
		st.expect(q, new Object[] {r[1], r[2], r[3]});
	}
	
	public void testLike(){
		Query q = st.query();
		Constraint c = q.constrain(new STByteWU((byte)11));
		q.descend(DESCENDANT).constraints().like();
		st.expectOne(q, new STByteWU((byte)113));
		q = st.query();
		c = q.constrain(new STByteWU((byte)10));
		q.descend(DESCENDANT).constraints().like();
		st.expectNone(q);
	}
	
	public void testNotLike(){
		Query q = st.query();
		Constraint c = q.constrain(new STByteWU((byte)1));
		q.descend(DESCENDANT).constraints().like().not();
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2]});
	}
	
	public void testIdentity(){
		Query q = st.query();
		Constraint c = q.constrain(new STByteWU((byte)1));
		ObjectSet set = q.execute();
		STByteWU identityConstraint = (STByteWU)set.next();
		identityConstraint.i_byte = new Byte((byte)102);
		q = st.query();
		q.constrain(identityConstraint).identity();
		identityConstraint.i_byte = new Byte((byte)1);
		st.expectOne(q,store()[1]);
	}
	
	public void testNotIdentity(){
		Query q = st.query();
		Constraint c = q.constrain(new STByteWU((byte)1));
		ObjectSet set = q.execute();
		STByteWU identityConstraint = (STByteWU)set.next();
		identityConstraint.i_byte = new Byte((byte)102);
		q = st.query();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.i_byte = new Byte((byte)1);
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	public void testConstraints(){
		Query q = st.query();
		q.constrain(new STByteWU((byte)1));
		q.constrain(new STByteWU((byte)0));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if(csa.length != 2){
			st.error("Constraints not returned");
		}
	}
	
	public void testNull(){
		
	}
	
	public void testEvaluation(){
		Query q = st.query();
		q.constrain(new STByteWU());
		q.constrain(new Evaluation() {
			public void evaluate(Candidate candidate) {
				STByteWU sts = (STByteWU)candidate.getObject();
				candidate.include((((Byte)sts.i_byte).byteValue() + 2) > 100);
			}
		});
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
}

