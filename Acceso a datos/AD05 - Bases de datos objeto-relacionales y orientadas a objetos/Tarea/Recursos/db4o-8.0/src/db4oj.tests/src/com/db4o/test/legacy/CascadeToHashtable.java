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
package com.db4o.test.legacy;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.test.*;

public class CascadeToHashtable {

	public Hashtable ht;

	public void configure() {
		Db4o.configure().objectClass(this).cascadeOnUpdate(true);
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
	}

	public void store() {
		Test.deleteAllInstances(this);
		Test.deleteAllInstances(new Atom());
		CascadeToHashtable cth = new CascadeToHashtable();
		cth.ht = new Hashtable();
		cth.ht.put("key1", new Atom("stored1"));
		cth.ht.put("key2", new Atom(new Atom("storedChild1"), "stored2"));
		Test.store(cth);
	}

	public void test() {
		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeToHashtable cth = (CascadeToHashtable) obj;
				cth.ht.put("key1", new Atom("updated1"));
				Atom atom = (Atom)cth.ht.get("key2"); 
				atom.name = "updated2";
				Test.store(cth);
			}
		});
		Test.reOpen();
		
		Test.forEach(this, new Visitor4() {
			public void visit(Object obj) {
				CascadeToHashtable cth = (CascadeToHashtable) obj;
				Atom atom = (Atom)cth.ht.get("key1");
				Test.ensure(atom.name.equals("updated1"));
				atom = (Atom)cth.ht.get("key2");
				Test.ensure(atom.name.equals("updated2"));
			}
		});
		
		// Cascade-On-Delete Test: We only want one atom to remain.
		
		Test.reOpen();
		Test.deleteAllInstances(this);
		Test.ensureOccurrences(new Atom(), 2);
	}
}
