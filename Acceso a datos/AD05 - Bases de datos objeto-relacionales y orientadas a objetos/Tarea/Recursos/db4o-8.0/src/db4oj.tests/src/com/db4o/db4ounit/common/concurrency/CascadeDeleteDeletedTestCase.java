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

public class CascadeDeleteDeletedTestCase extends Db4oClientServerTestCase {

	public static class Item {
		public Item(String name) {
			this.name = name;
		}
		public String name;

		public Object untypedMember;

		public CddMember typedMember;
	}
	
	public static void main(String[] args) {
		new CascadeDeleteDeletedTestCase().runConcurrency();
	}
	
	protected void db4oSetupBeforeStore() throws Exception {
		configureThreadCount(10);
	}

	protected void configure(Configuration config) {
		config.objectClass(Item.class).cascadeOnDelete(true);
	}

	protected void store() {
		ExtObjectContainer oc = db();
		membersFirst(oc, "membersFirst commit");
		membersFirst(oc, "membersFirst");
		twoRef(oc, "twoRef");
		twoRef(oc, "twoRef commit");
		twoRef(oc, "twoRef delete");
		twoRef(oc, "twoRef delete commit");
	}

	private void membersFirst(ExtObjectContainer oc, String name) {
		Item item = new Item(name);
		item.untypedMember = new CddMember();
		item.typedMember = new CddMember();
		oc.store(item);
	}

	private void twoRef(ExtObjectContainer oc, String name) {
		Item item1 = new Item(name);
		item1.untypedMember = new CddMember();
		item1.typedMember = new CddMember();
		Item item2 = new Item(name);
		item2.untypedMember = item1.untypedMember;
		item2.typedMember = item1.typedMember;
		oc.store(item1);
		oc.store(item2);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		if (seq == 0) {
			tMembersFirst(oc, "membersFirst commit");
		} else if (seq == 1) {
			tMembersFirst(oc, "membersFirst");
		} else if (seq == 2) {
			tTwoRef(oc, "twoRef");
		} else if (seq == 3) {
			tTwoRef(oc, "twoRef commit");
		} else if (seq == 4) {
			tTwoRef(oc, "twoRef delete");
		} else if (seq == 5) {
			tTwoRef(oc, "twoRef delete commit");
		}
	}

	public void check(ExtObjectContainer oc) {
		Assert.areEqual(0, countOccurences(oc, CddMember.class));
	}

	private void tMembersFirst(ExtObjectContainer oc, String name) {
		boolean commit = name.indexOf("commit") > 1;
		Query q = oc.query();
		q.constrain(Item.class);
		q.descend("name").constrain(name);
		ObjectSet objectSet = q.execute();
		Item cdd = (Item) objectSet.next();
		oc.delete(cdd.untypedMember);
		oc.delete(cdd.typedMember);
		if (commit) {
			oc.commit();
		}
		oc.delete(cdd);
		if (!commit) {
			oc.commit();
		}
	}

	private void tTwoRef(ExtObjectContainer oc, String name) {
		boolean commit = name.indexOf("commit") > 1;
		boolean delete = name.indexOf("delete") > 1;

		Query q = oc.query();
		q.constrain(Item.class);
		q.descend("name").constrain(name);
		ObjectSet objectSet = q.execute();
		Item item1 = (Item) objectSet.next();
		Item item2 = (Item) objectSet.next();
		if (delete) {
			oc.delete(item1.untypedMember);
			oc.delete(item1.typedMember);
		}
		oc.delete(item1);
		if (commit) {
			oc.commit();
		}
		oc.delete(item2);
		if (!commit) {
			oc.commit();
		}
	}

	public static class CddMember {
		public String name;
	}

}
