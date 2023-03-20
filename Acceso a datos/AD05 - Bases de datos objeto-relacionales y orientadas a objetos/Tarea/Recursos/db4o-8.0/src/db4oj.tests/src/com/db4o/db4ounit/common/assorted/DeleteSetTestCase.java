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

import db4ounit.*;
import db4ounit.extensions.*;

public class DeleteSetTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new DeleteSetTestCase().runAll();
	}

	public static class Item {
		public Item() {

		}

		public Item(int v) {
			value = v;
		}

		public int value;
	}

	protected void store() throws Exception {
		store(new Item(1));
	}

	public void testDeleteStore() throws Exception {
		Object item = retrieveOnlyInstance(Item.class);
		db().delete(item);
		db().store(item);
		db().commit();
		assertOccurrences(Item.class, 1);
	}

	public void testDeleteStoreStore() throws Exception {
		Item item = (Item) retrieveOnlyInstance(Item.class);
		db().delete(item);
		item.value = 2;
		db().store(item);
		item.value = 3;
		db().store(item);
		db().commit();
		assertOccurrences(Item.class, 1);
		item = (Item) retrieveOnlyInstance(Item.class);
		Assert.areEqual(3, item.value);
	}

}
