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
package com.db4o.db4ounit.common.types;

import java.util.*;

import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @sharpen.remove
 */
public class UnmodifiableListTestCase extends AbstractDb4oTestCase implements OptOutDefragSolo {
	
	public static void main(String[] args) {
		new UnmodifiableListTestCase().runAll();
	}
	
	public static class ListHolder {
		
		public List list;
		
		public ListHolder(List list) {
			this.list = list;
		}
	}
	
	public static class Item {
		
		public String name;
		
		public Item(String name) {
			this.name = name;
		}
		
	}
	
	@Override
	protected void store() throws Exception {
		ArrayList underlyingList = new ArrayList();
		underlyingList.add("42");
		underlyingList.add(new Item("item"));
		ListHolder listHolder = new ListHolder(Collections.unmodifiableList(underlyingList));
		store(listHolder);
	}
	
	public void test(){
		ListHolder listHolder = retrieveOnlyInstance(ListHolder.class);
		Assert.areEqual("42", listHolder.list.get(0));
	}
	
	public void testStringQuery(){
		assertStringQuery(1, "42");
		assertStringQuery(0, "43");
	}
	
	public void testItemQuery(){
		assertItemQuery("item", 1);
		assertItemQuery("invalid", 0);
	}
	
	public void testActivation(){
		ListHolder listHolder = retrieveOnlyInstance(ListHolder.class);
		Item item = (Item) listHolder.list.get(1);
		Assert.isTrue(db().isActive(item));
		db().deactivate(listHolder,10);
		Assert.isFalse(db().isActive(item));
		db().activate(listHolder,10);
		Assert.isTrue(db().isActive(item));
	}
	
	public void testDelete() {
		ListHolder listHolder = retrieveOnlyInstance(ListHolder.class);
		Item item = (Item) listHolder.list.get(1);
		db().delete(listHolder.list);
		db().commit();
		Assert.isTrue(db().isStored(item));
	}
	
	public void testCascadeDelete() throws Exception {
		fixture().configureAtRuntime(new RuntimeConfigureAction() {
			public void apply(Configuration config) {
				config.objectClass(ListHolder.class).cascadeOnDelete(true);	
			}
		});
		reopen();
		ListHolder listHolder = retrieveOnlyInstance(ListHolder.class);
		Item item = (Item) listHolder.list.get(1);
		long id = db().getID(item);
		db().delete(listHolder);
		db().commit();
		reopen();
		Assert.isNull(db().getByID(id));
		// isStored wont work since the client reference system is not updated.
		// Assert.isFalse(db().isStored(item));
	}

	private void assertItemQuery(String constraint, int expected) {
		Query query = db().query();
		query.constrain(ListHolder.class);
		query.descend("list").descend("name").constrain(constraint);
		Assert.areEqual(expected, query.execute().size());
	}

	private void assertStringQuery(int expected, String valueToQuery) {
		Query query = db().query();
		query.constrain(ListHolder.class);
		query.descend("list").constrain(valueToQuery);
		Assert.areEqual(expected, query.execute().size());
	}

}
