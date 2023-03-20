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

public class STStringU implements STClass1 {

	public static transient SodaTest st;
	
	public Object str;

	public STStringU() {
	}

	public STStringU(String str) {
		this.str = str;
	}

	public Object[] store() {
		return new Object[] {
			new STStringU(null),
			new STStringU("aaa"),
			new STStringU("bbb"),
			new STStringU("dod")};
	}

	public void testEquals() {
		Query q = st.query();
		q.constrain(store()[2]);
		st.expectOne(q, store()[2]);
	}

	public void testNotEquals() {
		Query q = st.query();
		Constraint c = q.constrain(store()[2]);
		q.descend("str").constraints().not();
		Object[] r = store();
		st.expect(q, new Object[] { r[0], r[1], r[3] });
	}

	public void testDescendantEquals() {
		Query q = st.query();
		q.constrain(new STStringU());
		q.descend("str").constrain("bbb");
		st.expectOne(q, new STStringU("bbb"));
	}

	public void testContains() {
		Query q = st.query();
		Constraint c = q.constrain(new STStringU("od"));
		q.descend("str").constraints().contains();
		st.expectOne(q, new STStringU("dod"));
	}

	public void testNotContains() {
		Query q = st.query();
		Constraint c = q.constrain(new STStringU("od"));
		q.descend("str").constraints().contains().not();
		st.expect(
			q,
			new Object[] { new STStringU(null), new STStringU("aaa"), new STStringU("bbb")});
	}

	public void testLike() {
		Query q = st.query();
		Constraint c = q.constrain(new STStringU("do"));
		q.descend("str").constraints().like();
		st.expectOne(q, new STStringU("dod"));
		q = st.query();
		c = q.constrain(new STStringU("od"));
		q.descend("str").constraints().like();
		st.expectOne(q,store()[3]);
	}

	public void testNotLike() {
		Query q = st.query();
		Constraint c = q.constrain(new STStringU("aaa"));
		q.descend("str").constraints().like().not();
		st.expect(
			q,
			new Object[] { new STStringU(null), new STStringU("bbb"), new STStringU("dod")});
		q = st.query();
		c = q.constrain(new STStringU("xxx"));
		q.descend("str").constraints().like();
		st.expectNone(q);
	}

	public void testIdentity() {
		Query q = st.query();
		Constraint c = q.constrain(new STStringU("aaa"));
		ObjectSet set = q.execute();
		STStringU identityConstraint = (STStringU) set.next();
		identityConstraint.str = "hihs";
		q = st.query();
		q.constrain(identityConstraint).identity();
		identityConstraint.str = "aaa";
		st.expectOne(q, new STStringU("aaa"));
	}

	public void testNotIdentity() {
		Query q = st.query();
		Constraint c = q.constrain(new STStringU("aaa"));
		ObjectSet set = q.execute();
		STStringU identityConstraint = (STStringU) set.next();
		identityConstraint.str = null;
		q = st.query();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.str = "aaa";
		st.expect(
			q,
			new Object[] { new STStringU(null), new STStringU("bbb"), new STStringU("dod")});
	}

	public void testNull() {
		Query q = st.query();
		Constraint c = q.constrain(new STStringU(null));
		q.descend("str").constrain(null);
		st.expectOne(q, new STStringU(null));
	}

	public void testNotNull() {
		Query q = st.query();
		Constraint c = q.constrain(new STStringU(null));
		q.descend("str").constrain(null).not();
		st.expect(
			q,
			new Object[] { new STStringU("aaa"), new STStringU("bbb"), new STStringU("dod")});
	}

	public void testConstraints() {
		Query q = st.query();
		q.constrain(new STStringU("aaa"));
		q.constrain(new STStringU("bbb"));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if (csa.length != 2) {
			st.error("Constraints not returned");
		}
	}

}
