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

public class STIntegerWU implements STClass{
	
	public static transient SodaTest st;
	
	Object i_int;
	
	public STIntegerWU(){
	}
	
	private STIntegerWU(int a_int){
		i_int = new Integer(a_int);
	}
	
	public Object[] store() {
		return new Object[]{
			new STIntegerWU(0),
			new STIntegerWU(1),
			new STIntegerWU(99),
			new STIntegerWU(909)
		};
	}
	
	public void testEquals(){
		Query q = st.query();
		q.constrain(new STIntegerWU(0));  
		
		// Primitive default values are ignored, so we need an 
		// additional constraint:
		q.descend("i_int").constrain(new Integer(0));
		st.expectOne(q, store()[0]);
	}
	
	public void testNotEquals(){
		Query q = st.query();
		Object[] r = store();
		Constraint c = q.constrain(new STIntegerWU());
		q.descend("i_int").constrain(new Integer(0)).not();
		st.expect(q, new Object[] {r[1], r[2], r[3]});
	}
	
	public void testGreater(){
		Query q = st.query();
		Constraint c = q.constrain(new STIntegerWU(9));
		q.descend("i_int").constraints().greater();
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testSmaller(){
		Query q = st.query();
		Constraint c = q.constrain(new STIntegerWU(1));
		q.descend("i_int").constraints().smaller();
		st.expectOne(q, store()[0]);
	}
	
	public void testContains(){
		Query q = st.query();
		Constraint c = q.constrain(new STIntegerWU(9));
		q.descend("i_int").constraints().contains();
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testNotContains(){
		Query q = st.query();
		Constraint c = q.constrain(new STIntegerWU());
		q.descend("i_int").constrain(new Integer(0)).contains().not();
		Object[] r = store();
		st.expect(q, new Object[] {r[1], r[2]});
	}
	
	public void testLike(){
		Query q = st.query();
		Constraint c = q.constrain(new STIntegerWU(90));
		q.descend("i_int").constraints().like();
		st.expectOne(q, new STIntegerWU(909));
		q = st.query();
		c = q.constrain(new STIntegerWU(10));
		q.descend("i_int").constraints().like();
		st.expectNone(q);
	}
	
	public void testNotLike(){
		Query q = st.query();
		Constraint c = q.constrain(new STIntegerWU(1));
		q.descend("i_int").constraints().like().not();
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	public void testIdentity(){
		Query q = st.query();
		Constraint c = q.constrain(new STIntegerWU(1));
		ObjectSet set = q.execute();
		STIntegerWU identityConstraint = (STIntegerWU)set.next();
		identityConstraint.i_int = new Integer(9999);
		q = st.query();
		q.constrain(identityConstraint).identity();
		identityConstraint.i_int = new Integer(1);
		st.expectOne(q,store()[1]);
	}
	
	public void testNotIdentity(){
		Query q = st.query();
		Constraint c = q.constrain(new STIntegerWU(1));
		ObjectSet set = q.execute();
		STIntegerWU identityConstraint = (STIntegerWU)set.next();
		identityConstraint.i_int = new Integer(9080);
		q = st.query();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.i_int = new Integer(1);
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	public void testConstraints(){
		Query q = st.query();
		q.constrain(new STIntegerWU(1));
		q.constrain(new STIntegerWU(0));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if(csa.length != 2){
			st.error("Constraints not returned");
		}
	}
	
	public void testEvaluation(){
		Query q = st.query();
		q.constrain(new STIntegerWU());
		q.constrain(new Evaluation() {
			public void evaluate(Candidate candidate) {
				STIntegerWU sti = (STIntegerWU)candidate.getObject();
				candidate.include((((Integer)sti.i_int).intValue() + 2) > 100);
			}
		});
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
}

