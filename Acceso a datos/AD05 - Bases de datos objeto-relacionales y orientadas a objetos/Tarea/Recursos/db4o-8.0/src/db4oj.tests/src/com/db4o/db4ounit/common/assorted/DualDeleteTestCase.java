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
package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;

public class DualDeleteTestCase extends Db4oClientServerTestCase {

	public static class Item {
		public Atom atom;
	}

	protected void configure(Configuration config) {
		config.objectClass(Item.class).cascadeOnDelete(true);
		config.objectClass(Item.class).cascadeOnUpdate(true);
	}

	protected void store() {
		Item dd1 = new Item();
		dd1.atom = new Atom("justone");
		store(dd1);
		
		Item dd2 = new Item();
		dd2.atom = dd1.atom;
		store(dd2);
	}
	
	public void testSingleSession(){
		deleteAll(Item.class);
		
		assertOccurrences(Atom.class, 0);
		db().rollback();
		assertOccurrences(Atom.class, 1);
		deleteAll(Item.class);
		
		assertOccurrences(Atom.class, 0);
		db().commit();
		assertOccurrences(Atom.class, 0);
		db().rollback();
		assertOccurrences(Atom.class, 0);
	}

	public void testSeparateSessions() {
		ExtObjectContainer oc1 = openNewSession();
		ExtObjectContainer oc2 = openNewSession();
		try {
			ObjectSet os1 = oc1.query(Item.class);
			ObjectSet os2 = oc2.query(Item.class);
			deleteObjectSet(oc1, os1);
			assertOccurrences(oc1, Atom.class, 0);
			assertOccurrences(oc2, Atom.class, 1);
			
			deleteObjectSet(oc2, os2);
			assertOccurrences(oc1, Atom.class, 0);
			assertOccurrences(oc2, Atom.class, 0);
			
			oc1.rollback();
			assertOccurrences(oc1, Atom.class, 1);
			assertOccurrences(oc2, Atom.class, 0);
			
			oc1.commit();
			assertOccurrences(oc1, Atom.class, 1);
			assertOccurrences(oc2, Atom.class, 0);
			
			deleteAll(oc2, Item.class);
			oc2.commit();
			assertOccurrences(oc1, Atom.class, 0);
			assertOccurrences(oc2, Atom.class, 0);
			
		} finally {
			oc1.close();
			oc2.close();
		}
	}

	public void conc1(ExtObjectContainer oc) throws Exception {
		ObjectSet os = oc.query(Item.class);
		Thread.sleep(500);
		deleteObjectSet(oc, os);
		oc.rollback();
	}

	public void check1(ExtObjectContainer oc) throws Exception {
		assertOccurrences(oc, Atom.class, 1);
	}

	public void conc2(ExtObjectContainer oc) throws Exception {
		ObjectSet os = oc.query(Item.class);
		Thread.sleep(500);
		deleteObjectSet(oc, os);
		assertOccurrences(oc, Atom.class, 0);
	}

	public void check2(ExtObjectContainer oc) throws Exception {
		assertOccurrences(oc, Atom.class, 0);
	}

}
