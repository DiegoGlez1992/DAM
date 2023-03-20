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
package com.db4o.db4ounit.common.querying;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnActivate extends AbstractDb4oTestCase implements OptOutTA {

	public String name;

	public CascadeOnActivate child;

	protected void configure(Configuration conf) {
		conf.objectClass(this).cascadeOnActivate(true);
	}

	protected void store() {
		CascadeOnActivate coa = new CascadeOnActivate();
		coa.name = "1";
		coa.child = new CascadeOnActivate();
		coa.child.name = "2";
		coa.child.child = new CascadeOnActivate();
		coa.child.child.name = "3";

		db().store(coa);
	}

	public void test() {
		Query q = newQuery(getClass());
		q.descend("name").constrain("1");
		ObjectSet os = q.execute();

		CascadeOnActivate coa = (CascadeOnActivate) os.next();
		CascadeOnActivate coa3 = coa.child.child;

		Assert.areEqual("3", coa3.name);

		db().deactivate(coa, Integer.MAX_VALUE);

		Assert.isNull(coa3.name);

		db().activate(coa, 1);

		Assert.areEqual("3", coa3.name);
	}
}
