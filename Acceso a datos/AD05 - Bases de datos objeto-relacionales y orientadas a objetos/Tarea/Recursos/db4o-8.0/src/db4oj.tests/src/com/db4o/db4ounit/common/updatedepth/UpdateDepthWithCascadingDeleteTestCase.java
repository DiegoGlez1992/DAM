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
package com.db4o.db4ounit.common.updatedepth;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class UpdateDepthWithCascadingDeleteTestCase extends AbstractDb4oTestCase {

	private static final int CHILD_ID = 2;
	private static final int ROOT_ID = 1;

	public static class Item {
		public Item _item;
		public int _id;
		
		public Item(int id, Item item) {
			_id = id;
			_item = item;
		}
	}
	
	@Override
	protected void configure(Configuration config) {
		config.objectClass(Item.class).cascadeOnDelete(true);
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item(ROOT_ID, new Item(CHILD_ID, null)));
	}
	
	public void testUpdateDepth() throws Exception {
		Item item = queryItemByID(ROOT_ID);
		final int changedRootID = 42;
		item._id = changedRootID;
		item._item._id = 43;
		store(item);
		reopen();
		Item changed = queryItemByID(changedRootID);
		Assert.areEqual(CHILD_ID, changed._item._id);
	}

	private Item queryItemByID(int id) {
		Query query = newQuery(Item.class);
		query.descend("_id").constrain(id);
		ObjectSet<Item> result = query.execute();
		Assert.isTrue(result.hasNext());
		Item item = result.next();
		return item;
	}
}
