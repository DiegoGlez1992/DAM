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

public class RefreshTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new RefreshTestCase().runConcurrency();
	}

	public String name;

	public RefreshTestCase child;

	public RefreshTestCase() {

	}

	public RefreshTestCase(String name, RefreshTestCase child) {
		this.name = name;
		this.child = child;
	}

	protected void store() {
		RefreshTestCase r3 = new RefreshTestCase("o3", null);
		RefreshTestCase r2 = new RefreshTestCase("o2", r3);
		RefreshTestCase r1 = new RefreshTestCase("o1", r2);
		store(r1);
	}

	public void conc(ExtObjectContainer oc) {
		RefreshTestCase r11 = getRoot(oc);
		r11.name = "cc";
		oc.refresh(r11, 0);
		Assert.areEqual("cc", r11.name);
		oc.refresh(r11, 1);
		Assert.areEqual("o1", r11.name);
		r11.child.name = "cc";
		oc.refresh(r11, 1);
		Assert.areEqual("cc", r11.child.name);
		oc.refresh(r11, 2);
		Assert.areEqual("o2", r11.child.name);
	}

	private RefreshTestCase getRoot(ObjectContainer oc) {
		return getByName(oc, "o1");
	}

	private RefreshTestCase getByName(ObjectContainer oc, final String name) {
		Query q = oc.query();
		q.constrain(RefreshTestCase.class);
		q.descend("name").constrain(name);
		ObjectSet objectSet = q.execute();
		return (RefreshTestCase) objectSet.next();
	}

}
