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
package com.db4o.db4ounit.common.concurrency;

import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class QueryNonExistantTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new QueryNonExistantTestCase().runConcurrency();
	}

	QueryNonExistant1 member;

	public QueryNonExistantTestCase() {
		// db4o constructor
	}

	public QueryNonExistantTestCase(boolean createMembers) {
		member = new QueryNonExistant1();
		member.member = new QueryNonExistant2();
		member.member.member = this;
		// db4o constructor
	}

	public void conc(ExtObjectContainer oc) {
		oc.queryByExample((new QueryNonExistantTestCase(true)));
		assertOccurrences(oc, QueryNonExistantTestCase.class, 0);
		Query q = oc.query();
		q.constrain(new QueryNonExistantTestCase(true));
		Assert.areEqual(0, q.execute().size());
	}

	public static class QueryNonExistant1 {
		QueryNonExistant2 member;
	}

	public static class QueryNonExistant2 extends QueryNonExistant1 {
		QueryNonExistantTestCase member;
	}

}
