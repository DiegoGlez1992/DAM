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
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeToArray extends AbstractDb4oTestCase {

	public static class Atom {

		public Atom child;

		public String name;

		public Atom() {
		}

		public Atom(Atom child) {
			this.child = child;
		}

		public Atom(String name) {
			this.name = name;
		}

		public Atom(Atom child, String name) {
			this(child);
			this.name = name;
		}
	}

	public Object[] objects;

	protected void configure(Configuration conf) {
		conf.objectClass(this).cascadeOnUpdate(true);
		conf.objectClass(this).cascadeOnDelete(true);
	}

	protected void store() {
		CascadeToArray cta = new CascadeToArray();
		cta.objects = new Object[] { new Atom("stored1"),
				new Atom(new Atom("storedChild1"), "stored2") };
		db().store(cta);
	}

	public void test() throws Exception {
		foreach(getClass(), new Visitor4() {
			public void visit(Object obj) {
				CascadeToArray cta = (CascadeToArray) obj;
				for (int i = 0; i < cta.objects.length; i++) {
					Atom atom = (Atom) cta.objects[i];
					atom.name = "updated";
					if (atom.child != null) {
						// This one should NOT cascade
						atom.child.name = "updated";
					}
				}
				db().store(cta);
			}
		});

		reopen();

		foreach(getClass(), new Visitor4() {
			public void visit(Object obj) {
				CascadeToArray cta = (CascadeToArray) obj;
				for (int i = 0; i < cta.objects.length; i++) {
					Atom atom = (Atom) cta.objects[i];
					Assert.areEqual("updated", atom.name);
					if (atom.child != null) {
						Assert.areNotEqual("updated", atom.child.name);
					}
				}
			}
		});

		// Cascade-On-Delete Test: We only want one Atom to remain.
		db().commit();
		reopen();

		ObjectSet os = newQuery(getClass()).execute();
		while (os.hasNext())
			db().delete(os.next());

		Assert.areEqual(1, countOccurences(Atom.class));
	}
	
	public static void main(String[] arguments) {
        new CascadeToArray().runSolo();
    }
}
