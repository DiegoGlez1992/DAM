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
package com.db4o.db4ounit.common.soda;

import com.db4o.foundation.*;
import com.db4o.internal.query.processor.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class AndJoinOptimizationTestCase extends AbstractDb4oTestCase {

	public static class Data {
		public int _id;
		public String _name;

		public Data(int id, String name) {
			_id = id;
			_name = name;
		}
	}

	protected void store() throws Exception {
		store(new Data(1,"a"));
		store(new Data(1,"b"));
		store(new Data(2,"a"));
		store(new Data(2,"b"));
	}

	public void testAndQuery() {
		Query query = newQuery(Data.class);
		query.descend("_id").constrain(new Integer(1)).and(
				query.descend("_name").constrain("a"));
		assertJoins(query);
		Assert.areEqual(1, query.execute().size());
		assertNoJoins(query);
	}

	public void testOrQuery() {
		Query query = newQuery(Data.class);
		query.descend("_id").constrain(new Integer(1)).or(
				query.descend("_name").constrain("a"));
		assertJoins(query);
		Assert.areEqual(3, query.execute().size());
		assertJoins(query);
	}

	private void assertNoJoins(Query query) {
		Assert.isFalse(hasJoins(query));
	}

	private void assertJoins(Query query) {
		Assert.isTrue(hasJoins(query));
	}

	private boolean hasJoins(Query query) {
		Iterator4 constrIter = ((QQuery)query).iterateConstraints();
		while(constrIter.moveNext()) {
			if(hasJoins((QCon)constrIter.current())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean hasJoins(QCon con) {
		if(con.hasJoins()) {
			return true;
		}
		Iterator4 childIter = con.iterateChildren();
		while(childIter.moveNext()) {
			if(hasJoins((QCon) childIter.current())) {
				return true;
			}
		}
		return false;
	}
}
