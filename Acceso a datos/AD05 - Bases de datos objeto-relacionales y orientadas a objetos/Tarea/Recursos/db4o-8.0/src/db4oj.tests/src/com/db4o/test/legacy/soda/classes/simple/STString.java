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
import com.db4o.test.legacy.soda.arrays.typed.*;


public class STString implements STClass1, STInterface {
	
	public static transient SodaTest st;
	
	public String str;

	public STString() {
	}

	public STString(String str) {
		this.str = str;
	}

	/** needed for STInterface test */
	public Object returnSomething() {
		return str;
	}

	public Object[] store() {
		return new Object[] {
			new STString(null),
			new STString("aaa"),
			new STString("bbb"),
			new STString("dod")};
	}
	
	public void testEquals() {
		Query q = st.query();
		q.constrain(store()[2]);
		st.expectOne(q, store()[2]);
	}

	public void testNotEquals() {
		Query q = st.query();
		q.constrain(store()[2]);
		q.descend("str").constraints().not();
		Object[] r = store();
		st.expect(q, new Object[] { r[0], r[1], r[3] });
	}

	public void testDescendantEquals() {
		Query q = st.query();
		q.constrain(new STString());
		q.descend("str").constrain("bbb");
		st.expectOne(q, new STString("bbb"));
	}

	public void testContains() {
		Query q = st.query();
		q.constrain(new STString("od"));
		q.descend("str").constraints().contains();
		st.expectOne(q, new STString("dod"));
	}

	public void testNotContains() {
		Query q = st.query();
		q.constrain(new STString("od"));
		q.descend("str").constraints().contains().not();
		st.expect(
			q,
			new Object[] { new STString(null), new STString("aaa"), new STString("bbb")});
	}

	public void testLike() {
		Query q = st.query();
		q.constrain(new STString("do"));
		q.descend("str").constraints().like();
		st.expectOne(q, new STString("dod"));
		q = st.query();
		q.constrain(new STString("od"));
		q.descend("str").constraints().like();
        
		st.expectOne(q,store()[3]);
	}

	public void testNotLike() {
		Query q = st.query();
		q.constrain(new STString("aaa"));
		q.descend("str").constraints().like().not();
		st.expect(
			q,
			new Object[] { new STString(null), new STString("bbb"), new STString("dod")});
		q = st.query();
		q.constrain(new STString("xxx"));
		q.descend("str").constraints().like();
		st.expectNone(q);
	}

	public void testStartsWith() {
		Query q = st.query();
		q.constrain(new STString("do"));
		q.descend("str").constraints().startsWith(true);
		st.expectOne(q, new STString("dod"));
		q = st.query();
		q.constrain(new STString("od"));
		q.descend("str").constraints().startsWith(true);
		st.expectNone(q);
	}

	public void testEndsWith() {
		Query q = st.query();
		q.constrain(new STString("do"));
		q.descend("str").constraints().endsWith(true);
		st.expectNone(q);
		q = st.query();
		q.constrain(new STString("od"));
		q.descend("str").constraints().endsWith(true);
		st.expectOne(q, new STString("dod"));
		q = st.query();
		q.constrain(new STString("D"));
		q.descend("str").constraints().endsWith(false);
		st.expectOne(q, new STString("dod"));
	}

	public void testIdentity() {
		Query q = st.query();
		q.constrain(new STString("aaa"));
		ObjectSet set = q.execute();
		STString identityConstraint = (STString) set.next();
		identityConstraint.str = "hihs";
		q = st.query();
		q.constrain(identityConstraint).identity();
		identityConstraint.str = "aaa";
		st.expectOne(q, new STString("aaa"));
	}

	public void testNotIdentity() {
		Query q = st.query();
		q.constrain(new STString("aaa"));
		ObjectSet set = q.execute();
		STString identityConstraint = (STString) set.next();
		identityConstraint.str = null;
		q = st.query();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.str = "aaa";
		st.expect(
			q,
			new Object[] { new STString(null), new STString("bbb"), new STString("dod")});
	}

	public void testNull() {
		Query q = st.query();
		q.constrain(new STString(null));
		q.descend("str").constrain(null);
		st.expectOne(q, new STString(null));
	}

	public void testNotNull() {
		Query q = st.query();
		q.constrain(new STString(null));
		q.descend("str").constrain(null).not();
		st.expect(
			q,
			new Object[] { new STString("aaa"), new STString("bbb"), new STString("dod")});
	}

	public void testConstraints() {
		Query q = st.query();
		q.constrain(new STString("aaa"));
		q.constrain(new STString("bbb"));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if (csa.length != 2) {
			st.error("Constraints not returned");
		}
	}

	public void testEvaluation() {
		Query q = st.query();
		q.constrain(new STString(null));
		q.constrain(new IndexOfEvaluation());
		st.expectOne(q, new STString("dod"));
	}
	
    public void testCaseInsenstiveContains() {
        Query q = st.query();
        q.constrain(STString.class);
        q.constrain(new ContainsEvaluation());
        st.expectOne(q, new STString("dod"));
    }
    
	public static class ContainsEvaluation implements Evaluation {
		public void evaluate(Candidate candidate) {
		    STString sts = (STString) candidate.getObject();
		    // FIXME: NPE expected?
		    candidate.include(sts.str.toLowerCase().indexOf("od") >= 0);
		}
	}

	public static class IndexOfEvaluation implements Evaluation {
		public void evaluate(Candidate candidate) {
			STString sts = (STString) candidate.getObject();
			// FIXME: NPE expected here?
			candidate.include(sts.str.indexOf("od") == 1);
		}
	}

}
