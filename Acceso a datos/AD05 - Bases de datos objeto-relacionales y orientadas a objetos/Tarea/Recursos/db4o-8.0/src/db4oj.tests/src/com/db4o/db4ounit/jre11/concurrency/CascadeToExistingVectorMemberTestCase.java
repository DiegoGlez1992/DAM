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
package com.db4o.db4ounit.jre11.concurrency;

import java.util.*;

import com.db4o.config.*;
import com.db4o.db4ounit.common.persistent.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeToExistingVectorMemberTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new CascadeToExistingVectorMemberTestCase().runConcurrency();
	}
	
	public static class Item {
		public Vector vec;
	}

	protected void configure(Configuration config) {
		config.objectClass(Item.class).cascadeOnUpdate(true);
		config.objectClass(Atom.class).cascadeOnUpdate(false);
	}

	protected void store() {
		Item item = new Item();
		item.vec = new Vector();
		Atom atom = new Atom("one");
		store(atom);
		item.vec.addElement(atom);
		store(item);
	}

	public void conc(final ExtObjectContainer oc, final int seq) {
		Item item = (Item) retrieveOnlyInstance(oc, Item.class);
		Atom atom = (Atom) item.vec.elementAt(0);
		atom.name = "two" + seq;
		oc.store(item);
		atom.name = "three" + seq;
		oc.store(item);
	}

	public void check(final ExtObjectContainer oc) {
		Item item = (Item) retrieveOnlyInstance(oc, Item.class);
		Atom atom = (Atom) item.vec.elementAt(0);
		Assert.isTrue(atom.name.startsWith("three"));
		Assert.isTrue(atom.name.length() > "three".length());	
	}
}
