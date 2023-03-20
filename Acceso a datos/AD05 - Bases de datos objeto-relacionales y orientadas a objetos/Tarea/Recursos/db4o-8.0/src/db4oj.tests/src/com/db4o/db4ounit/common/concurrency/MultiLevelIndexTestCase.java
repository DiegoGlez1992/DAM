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

public class MultiLevelIndexTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new MultiLevelIndexTestCase().runConcurrency();
	}

	public MultiLevelIndexTestCase _child;

	public int _i;

	public int _level;

	protected void configure(Configuration config) {
		config.objectClass(this).objectField("_child").indexed(true);
		config.objectClass(this).objectField("_i").indexed(true);
	}

	protected void store() {
		store(3);
		store(2);
		store(5);
		store(1);
		for (int i = 6; i < 103; i++) {
			store(i);
		}
	}

	private void store(int val) {
		MultiLevelIndexTestCase root = new MultiLevelIndexTestCase();
		root._i = val;
		root._child = new MultiLevelIndexTestCase();
		root._child._level = 1;
		root._child._i = -val;
		store(root);
	}

	public void conc1(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(MultiLevelIndexTestCase.class);
		q.descend("_child").descend("_i").constrain(new Integer(-102));
		ObjectSet objectSet = q.execute();
		Assert.areEqual(1, objectSet.size());
		MultiLevelIndexTestCase mli = (MultiLevelIndexTestCase) objectSet.next();
		Assert.areEqual(102, mli._i);
	}

	public void conc2(ExtObjectContainer oc, int seq) {
		oc.configure().objectClass(MultiLevelIndexTestCase.class).cascadeOnUpdate(true);
		Query q = oc.query();
		q.constrain(MultiLevelIndexTestCase.class);
		q.descend("_child").descend("_i").constrain(new Integer(seq - 102));
		ObjectSet objectSet = q.execute();
		Assert.areEqual(1, objectSet.size());
		MultiLevelIndexTestCase mli = (MultiLevelIndexTestCase) objectSet.next();
		Assert.areEqual(102 - seq, mli._i);
		mli._child._i = -(seq + 201);
		oc.store(mli);
	}

	public void check2(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(MultiLevelIndexTestCase.class);
		q.descend("_child").descend("_i").constrain(new Integer(-200))
				.smaller();
		ObjectSet objectSet = q.execute();
		Assert.areEqual(threadCount(), objectSet.size());
	}

}
