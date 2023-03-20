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
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class IndexedUpdatesWithNullTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new IndexedUpdatesWithNullTestCase().runConcurrency();
	}

	public String str;

	public IndexedUpdatesWithNullTestCase() {
	}

	public IndexedUpdatesWithNullTestCase(String str) {
		this.str = str;
	}

	protected void configure(Configuration config) {
		config.objectClass(this).objectField("str").indexed(true);
	}

	protected void store() {
		store(new IndexedUpdatesWithNullTestCase("one"));
		store(new IndexedUpdatesWithNullTestCase("two"));
		store(new IndexedUpdatesWithNullTestCase("three"));
		store(new IndexedUpdatesWithNullTestCase(null));
		store(new IndexedUpdatesWithNullTestCase(null));
		store(new IndexedUpdatesWithNullTestCase(null));
		store(new IndexedUpdatesWithNullTestCase(null));
		store(new IndexedUpdatesWithNullTestCase("four"));
	}

	public void conc1(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(IndexedUpdatesWithNullTestCase.class);
		q.descend("str").constrain(null);
		ObjectSet objectSet = q.execute();
		Assert.areEqual(4, objectSet.size());
	}

	public void conc2(ExtObjectContainer oc) throws Exception {
		Query q = oc.query();
		q.constrain(IndexedUpdatesWithNullTestCase.class);
		q.descend("str").constrain(null);
		ObjectSet objectSet = q.execute();
		if (objectSet.size() == 0) { // already set by other threads
			return;
		}
		Assert.areEqual(4, objectSet.size());
		// wait for other threads
		Thread.sleep(500);
		while (objectSet.hasNext()) {
			IndexedUpdatesWithNullTestCase iuwn = (IndexedUpdatesWithNullTestCase) objectSet
					.next();
			iuwn.str = "hi";
			oc.store(iuwn);
			Thread.sleep(100);
		}
	}

	public void check2(ExtObjectContainer oc) {
		Query q1 = oc.query();
		q1.constrain(IndexedUpdatesWithNullTestCase.class);
		q1.descend("str").constrain(null);
		ObjectSet objectSet1 = q1.execute();
		Assert.areEqual(0, objectSet1.size());

		Query q2 = oc.query();
		q2.constrain(IndexedUpdatesWithNullTestCase.class);
		q2.descend("str").constrain("hi");
		ObjectSet objectSet2 = q2.execute();
		Assert.areEqual(4, objectSet2.size());
	}

}
