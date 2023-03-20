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

import com.db4o.config.*;
import com.db4o.db4ounit.common.persistent.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnUpdateTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new CascadeOnUpdateTestCase().runConcurrency();
	}
	
	private static final int ATOM_COUNT = 10;

	public static class Item {
		public Atom[] child;
	}

	protected void configure(Configuration config) {
		config.objectClass(Item.class).cascadeOnUpdate(true);
		config.objectClass(Atom.class).cascadeOnUpdate(true);
	}

	protected void store() {
		Item item = new Item();
		item.child = new Atom[ATOM_COUNT];
		for (int i = 0; i < ATOM_COUNT; i++) {
			item.child[i] = new Atom(new Atom("storedChild"), "stored");
		}
		store(item);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		Item item = (Item) retrieveOnlyInstance(oc, Item.class);
		for (int i = 0; i < ATOM_COUNT; i++) {
			item.child[i].name = "updated" + seq;
			item.child[i].child.name = "updated" + seq;
			oc.store(item);
		}
	}

	public void check(ExtObjectContainer oc) {
		Item item = (Item) retrieveOnlyInstance(Item.class);
		String name = item.child[0].name;
		Assert.isTrue(name.startsWith("updated"));
		for (int i = 0; i < ATOM_COUNT; i++) {
			Assert.areEqual(name, item.child[i].name);
			Assert.areEqual(name, item.child[i].child.name);
		}
	}
}
