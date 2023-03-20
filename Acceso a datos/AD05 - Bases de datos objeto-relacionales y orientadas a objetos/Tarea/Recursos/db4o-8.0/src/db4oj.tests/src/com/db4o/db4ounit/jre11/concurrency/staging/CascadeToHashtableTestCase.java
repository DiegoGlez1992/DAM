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
package com.db4o.db4ounit.jre11.concurrency.staging;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.persistent.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeToHashtableTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new CascadeToHashtableTestCase().runConcurrency();
	}
	
	public Hashtable ht;

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnUpdate(true);
		config.objectClass(this).cascadeOnDelete(true);
		config.objectClass(Atom.class).cascadeOnDelete(false);
	}

	protected void store() {
		CascadeToHashtableTestCase cth = new CascadeToHashtableTestCase();
		cth.ht = new Hashtable();
		cth.ht.put("key1", new Atom("stored1"));
		cth.ht.put("key2", new Atom(new Atom("storedChild1"), "stored2"));
		store(cth);
	}

	public void conc(ExtObjectContainer oc) {
		CascadeToHashtableTestCase cth = (CascadeToHashtableTestCase) retrieveOnlyInstance(oc, CascadeToHashtableTestCase.class);
		cth.ht.put("key1", new Atom("updated1"));
		Atom atom = (Atom) cth.ht.get("key2");
		atom.name = "updated2";
		oc.store(cth);
	}

	public void check(ExtObjectContainer oc) {
		CascadeToHashtableTestCase cth = (CascadeToHashtableTestCase) retrieveOnlyInstance(oc, CascadeToHashtableTestCase.class);
		Atom atom = (Atom) cth.ht.get("key1");
		Assert.areEqual("updated1", atom.name);
		atom = (Atom) cth.ht.get("key2");
		Assert.areEqual("updated2", atom.name);
	}

	public void concDelete(ExtObjectContainer oc, int seq) throws Exception {
		ObjectSet os = oc.query(CascadeToHashtableTestCase.class);
		if (os.size() == 0) { // already deleted
			return;
		}
		Assert.areEqual(1, os.size());
		CascadeToHashtableTestCase cth = (CascadeToHashtableTestCase) os.next();
		// wait for other threads
		Thread.sleep(500);
		oc.delete(cth);
	}

	public void checkDelete(ExtObjectContainer oc) {
		// Cascade-On-Delete Test: We only want one atom to remain.
		assertOccurrences(oc, Atom.class, 1);
	}
}
