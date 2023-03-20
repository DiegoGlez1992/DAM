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

import java.util.*;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Remove(decaf.Platform.JDK11)
public class TransientCloneTestCase extends AbstractDb4oTestCase {

	public static class Item {
		public List list;
		public Hashtable ht;
		public String str;
		public int myInt;
		public Molecule[] molecules;
	}
	
	@Override
	protected void store() throws Exception {
	 	Item item = new Item();
		item.list = new ArrayList();
		item.list.add(new Atom("listAtom"));
		item.list.add(item);
		item.ht = new Hashtable();
		item.ht.put("htc", new Molecule("htAtom"));
		item.ht.put("recurse", item);
		item.str = "str";
		item.myInt = 100;
		item.molecules = new Molecule[3];
		for (int i = 0; i < item.molecules.length; i++) {
			item.molecules[i] = new Molecule("arr" + i);
			item.molecules[i].child = new Atom("arr" + i);
			item.molecules[i].child.child = new Atom("arrc" + i);
		}
		
		store(item);
	}

	public void test() {
		final Item item = retrieveOnlyInstance(Item.class);
		
		db().activate(item, Integer.MAX_VALUE);
		Item originalValues = peekPersisted(false);
		cmp(item, originalValues);
		db().deactivate(item, Integer.MAX_VALUE);
		Item modified = peekPersisted(false);
		cmp(originalValues, modified);
		db().activate(item, Integer.MAX_VALUE);

		modified.str = "changed";
		modified.molecules[0].name = "changed";
		item.str = "changed";
		item.molecules[0].name = "changed";
		db().store(item.molecules[0]);
		db().store(item);

		Item tc = peekPersisted(true);
		cmp(originalValues, tc);

		tc = peekPersisted(false);
		cmp(modified, tc);

		db().commit();
		tc = peekPersisted(true);
		cmp(modified, tc);
	}

	private void cmp(Item to, Item tc) {
		Assert.isTrue(tc != to);
		Assert.isTrue(tc.list != to);
		Assert.isTrue(tc.list.size() == to.list.size());
		Iterator i = tc.list.iterator();
		Atom tca = next(i);
		Iterator j = to.list.iterator();
		Atom tct = next(j);
		Assert.isTrue(tca != tct);
		Assert.isTrue(tca.name.equals(tct.name));
		Assert.areSame(next(i), tc);
		Assert.areSame(next(j), to);
		Assert.isTrue(tc.ht != to.ht);
		Molecule tcm = (Molecule) tc.ht.get("htc");
		Molecule tom = (Molecule) to.ht.get("htc");
		Assert.isTrue(tcm != tom);
		Assert.isTrue(tcm.name.equals(tom.name));
		Assert.areSame(tc.ht.get("recurse"), tc);
		Assert.areSame(to.ht.get("recurse"), to);
		Assert.areEqual(to.str, tc.str);
		Assert.isTrue(tc.str.equals(to.str));
		Assert.isTrue(tc.myInt == to.myInt);
		Assert.isTrue(tc.molecules.length == to.molecules.length);
		Assert.isTrue(tc.molecules.length == to.molecules.length);
		tcm = tc.molecules[0];
		tom = to.molecules[0];
		Assert.isTrue(tcm != tom);
		Assert.isTrue(tcm.name.equals(tom.name));
		Assert.isTrue(tcm.child != tom.child);
		Assert.isTrue(tcm.child.name.equals(tom.child.name));
	}

	private <T> T next (Iterator  i) {
		Assert.isTrue(i.hasNext());
		return (T)i.next();
	}

	private Item peekPersisted(boolean committed) {
		ExtObjectContainer oc = db();
		return oc.peekPersisted(retrieveOnlyInstance(Item.class), Integer.MAX_VALUE, committed);
	}
}
