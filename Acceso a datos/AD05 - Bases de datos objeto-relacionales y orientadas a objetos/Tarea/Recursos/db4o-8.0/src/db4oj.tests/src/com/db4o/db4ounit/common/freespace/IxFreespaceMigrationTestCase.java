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
package com.db4o.db4ounit.common.freespace;

import com.db4o.config.*;
import com.db4o.db4ounit.common.handlers.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;

public class IxFreespaceMigrationTestCase extends FormatMigrationTestCaseBase {

	protected void configureForStore(Configuration config) {
		config.freespace().useIndexSystem();
	}
	
	protected void store(ObjectContainerAdapter objectContainer) {
		Item nextItem = null;
		for (int i = 9; i >= 0; i--) {
			Item storedItem = new Item("item" + i, nextItem);
			objectContainer.store(storedItem);
			nextItem = storedItem;
		}
		objectContainer.commit();
		Item item = queryForItem(objectContainer, 0);
		for (int i = 0; i < 5; i++) {
			objectContainer.delete(item);
			item = item._next;
		}
		objectContainer.commit();
	}

	private Item queryForItem(ObjectContainerAdapter objectContainer, int n) {
		return queryForItem(objectContainer.query(), n);
	}

	private Item queryForItem(Query q, int n) {
		q.constrain(Item.class);
		q.descend("_name").constrain("item" + n);
		return (Item)q.execute().next();
	}
	
	protected void assertObjectsAreReadable(ExtObjectContainer objectContainer) {
		assertItemCount(objectContainer, 5);
		Item item = queryForItem(objectContainer.query(), 5);
		for (int i = 5; i < 10; i++) {
			Assert.areEqual("item" + i, item._name);
			item = item._next;
		}
	}
	
	private void assertItemCount(ExtObjectContainer objectContainer, int i) {
		Query q = objectContainer.query();
		q.constrain(Item.class);
		Assert.areEqual(i, q.execute().size());
	}

	public static class Item{
		
		public String _name;
		
		public Item _next;
		
		public Item(String name){
			_name = name;
		}
		
		public Item(String name, Item next_){
			_name = name;
			_next = next_;
		}
		
	}

	protected String fileNamePrefix() {
		return "migrate_freespace_ix_" ;
	}

}
