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

public class QueryForUnknownFieldTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new QueryForUnknownFieldTestCase().runConcurrency();
	}

	public String _name;

	public QueryForUnknownFieldTestCase() {
	}

	public QueryForUnknownFieldTestCase(String name) {
		_name = name;
	}

	protected void store() {
		_name = "name";
		store(this);
	}

	public void conc(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(QueryForUnknownFieldTestCase.class);
		q.descend("_name").constrain("name");
		Assert.areEqual(1, q.execute().size());

		q = oc.query();
		q.constrain(QueryForUnknownFieldTestCase.class);
		q.descend("name").constrain("name");
		Assert.areEqual(0, q.execute().size());
	}

}
