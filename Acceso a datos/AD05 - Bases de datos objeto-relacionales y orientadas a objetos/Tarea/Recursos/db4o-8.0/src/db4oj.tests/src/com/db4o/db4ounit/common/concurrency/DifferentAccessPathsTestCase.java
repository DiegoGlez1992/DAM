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

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DifferentAccessPathsTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new DifferentAccessPathsTestCase().runConcurrency();
	}
	
	public String foo;

	protected void store() {
		DifferentAccessPathsTestCase dap = new DifferentAccessPathsTestCase();
		dap.foo = "hi";
		store(dap);
		dap = new DifferentAccessPathsTestCase();
		dap.foo = "hi too";
		store(dap);
	}

	public void conc(ExtObjectContainer oc) throws Exception {
		DifferentAccessPathsTestCase dap = query(oc);
		for (int i = 0; i < 10; i++) {
			Assert.areSame(dap, query(oc));
		}
		oc.purge(dap);
		Assert.areNotSame(dap, query(oc));
	}

	private DifferentAccessPathsTestCase query(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(DifferentAccessPathsTestCase.class);
		q.descend("foo").constrain("hi");
		ObjectSet os = q.execute();
		Assert.areEqual(1, os.size());
		DifferentAccessPathsTestCase dap = (DifferentAccessPathsTestCase) os.next();
		return dap;
	}

}
