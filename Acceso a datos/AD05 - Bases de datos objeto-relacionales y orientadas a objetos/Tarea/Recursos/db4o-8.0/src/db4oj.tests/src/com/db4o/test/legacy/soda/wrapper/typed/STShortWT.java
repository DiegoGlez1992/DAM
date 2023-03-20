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

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.legacy.soda.*;

public class STShortWT implements STClass{
	
	final static String DESCENDANT = "i_short";
	
	public static transient SodaTest st;
	
	Short i_short;
	
	
	public STShortWT(){
	}
	
	private STShortWT(short a_short){
		i_short = new Short(a_short);
	}
	
	public Object[] store() {
		return new Object[]{
			new STShortWT((short)0),
			new STShortWT((short)1),
			new STShortWT((short)99),
			new STShortWT((short)909)
		};
	}
	
	public void testEquals(){
		Query q = st.query();
		q.constrain(new STShortWT((short)0));  
		
		// Primitive default values are ignored, so we need an 
		// additional constraint:
		q.descend(DESCENDANT).constrain(new Short((short)0));
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
		Constraint c = q.constrain(new STShortWT((short)9));
		q.descend(DESCENDANT).constraints().greater();
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testSmaller(){
		Query q = st.query();
		Constraint c = q.constrain(new STShortWT((short)1));
		q.descend(DESCENDANT).constraints().smaller();
		st.expectOne(q, store()[0]);
	}
	
	public void testContains(){
		Query q = st.query();
		Constraint c = q.constrain(new STShortWT((short)9));
		q.descend(DESCENDANT).constraints().contains();
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
	public void testNotContains(){
		Query q = st.query();
		Constraint c = q.constrain(new STShortWT((short)0));
		q.descend(DESCENDANT).constraints().contains().not();
		Object[] r = store();
		st.expect(q, new Object[] {r[1], r[2]});
	}
	
	public void testLike(){
		Query q = st.query();
		Constraint c = q.constrain(new STShortWT((short)90));
		q.descend(DESCENDANT).constraints().like();
		st.expectOne(q, store()[3]);
		q = st.query();
		c = q.constrain(new STShortWT((short)10));
		q.descend(DESCENDANT).constraints().like();
		st.expectNone(q);
	}
	
	public void testNotLike(){
		Query q = st.query();
		Constraint c = q.constrain(new STShortWT((short)1));
		q.descend(DESCENDANT).constraints().like().not();
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	public void testIdentity(){
		Query q = st.query();
		Constraint c = q.constrain(new STShortWT((short)1));
		ObjectSet set = q.execute();
		STShortWT identityConstraint = (STShortWT)set.next();
		identityConstraint.i_short = new Short((short)9999);
		q = st.query();
		q.constrain(identityConstraint).identity();
		identityConstraint.i_short = new Short((short)1);
		st.expectOne(q,store()[1]);
	}
	
	public void testNotIdentity(){
		Query q = st.query();
		Constraint c = q.constrain(new STShortWT((short)1));
		ObjectSet set = q.execute();
		STShortWT identityConstraint = (STShortWT)set.next();
		identityConstraint.i_short = new Short((short)9080);
		q = st.query();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.i_short = new Short((short)1);
		Object[] r = store();
		st.expect(q, new Object[] {r[0], r[2], r[3]});
	}
	
	public void testConstraints(){
		Query q = st.query();
		q.constrain(new STShortWT((short)1));
		q.constrain(new STShortWT((short)0));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if(csa.length != 2){
			st.error("Constraints not returned");
		}
	}
	
	public void testEvaluation(){
		Query q = st.query();
		q.constrain(new STShortWT());
		q.constrain(new Evaluation() {
			public void evaluate(Candidate candidate) {
				STShortWT sts = (STShortWT)candidate.getObject();
				candidate.include((sts.i_short.shortValue() + 2) > 100);
			}
		});
		Object[] r = store();
		st.expect(q, new Object[] {r[2], r[3]});
	}
	
}

