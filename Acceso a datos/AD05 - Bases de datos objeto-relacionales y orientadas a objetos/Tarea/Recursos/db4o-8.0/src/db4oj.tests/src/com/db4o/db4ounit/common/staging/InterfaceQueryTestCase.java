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
package com.db4o.db4ounit.common.staging;

import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class InterfaceQueryTestCase extends AbstractDb4oTestCase {

	private static final String FIELD_A = "fieldA";
	private static final String FIELD_B = "fieldB";

	public static interface IData {
	}

	public static class DataA implements IData {
	    public int fieldA;
	    public int fieldB;

	    public DataA(int a, int b) {
	        fieldA = a;
	        fieldB = b;
	    }
	}

	public static class DataB implements IData {
	    public int fieldA;
	    public int fieldB;

	    public DataB(int a, int b) {
	        fieldA = a;
	        fieldB = b;
	    }
	}

	protected void _configure(Configuration config) throws Exception {
		configIndexed(config, DataA.class, FIELD_A);
		configIndexed(config, DataA.class, FIELD_B);
		configIndexed(config, DataB.class, FIELD_A);
		configIndexed(config, DataB.class, FIELD_B);
	}

	private void configIndexed(Configuration config, Class clazz,
			String fieldName) {
		config.objectClass(clazz).objectField(fieldName).indexed(true);
	}
	
	protected void store() throws Exception {
        store(new DataA(10, 10));
        store(new DataA(20, 20));
        store(new DataB(10, 10));
        store(new DataB(30, 30));
	}
	
	public void testExplicitNotQuery() {
		Query query = newQuery();
		query.constrain(DataA.class).and(query.descend(FIELD_A).constrain(new Integer(10)).not()).or(
				query.constrain(DataB.class).and(query.descend(FIELD_A).constrain(new Integer(10)).not()));
		Assert.areEqual(2, query.execute().size());
	}

	public void testExplicitNotQuery2() {
		Query query = newQuery();
        query.constrain(DataA.class).or(query.constrain(DataB.class));
		query.descend(FIELD_A).constrain(new Integer(10)).not();
		Assert.areEqual(2, query.execute().size());
	}

	public void testQueryAll() {
		assertQueryResult(4, new QueryConstrainer() {
			public void constrain(Query query) {
			}
		});
	}

	public void testSingleConstraint() {
		assertQueryResult(2, new QueryConstrainer() {
			public void constrain(Query query) {
		        query.descend(FIELD_A).constrain(new Integer(10));
			}
		});
	}

	public void testAnd() {
		assertQueryResult(2, new QueryConstrainer() {
			public void constrain(Query query) {
		        Constraint icon1 = query.descend(FIELD_A).constrain(new Integer(10));
		        Constraint icon2 = query.descend(FIELD_B).constrain(new Integer(10));
		        icon1.and(icon2);
			}
		});
	}

	public void testOr() {
		assertQueryResult(2, new QueryConstrainer() {
			public void constrain(Query query) {
		        Constraint icon1 = query.descend(FIELD_A).constrain(new Integer(10));
		        Constraint icon2 = query.descend(FIELD_B).constrain(new Integer(10));
		        icon1.or(icon2);
			}
		});
	}

	public void testNot() {
		assertQueryResult(2, new QueryConstrainer() {
			public void constrain(Query query) {
		        query.descend(FIELD_A).constrain(new Integer(10)).not();
			}
		});
	}

	public void assertQueryResult(int expected, QueryConstrainer constrainer) {
		Query query = newQuery(IData.class);
		constrainer.constrain(query);
		Assert.areEqual(expected, query.execute().size());
	}
	
	public static interface QueryConstrainer {
		void constrain(Query query);
	}
}
