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
package com.db4o.db4ounit.common.tp;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TransparentPersistenceTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
	    new TransparentPersistenceTestCase().runAll();
    }
	
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
	}
	
	protected void store() throws Exception {
		store(new Item("Foo"));
		store(new Item("Bar"));
	}
	
	public void testActivateOnWrite() throws Exception {
		
		Item foo = itemByName("Foo");
		foo.setName("Foo*");
		Assert.areEqual("Foo*", foo.getName());
	}
	
	public void testConcurrentClientModification() throws Exception {
		if (!isMultiSession()) {
			return;
		}
		
		final ExtObjectContainer client1 = db();
		final ExtObjectContainer client2 = openNewSession();
		try {
			Item foo1 = itemByName(client1, "Foo");
			Item foo2 = itemByName(client2, "Foo");
			foo1.setName("Foo*");
			foo2.setName("Foo**");
			
			assertUpdatedObjects(client1, foo1);
			assertUpdatedObjects(client2, foo2);
			
			client1.refresh(foo1, 1);
			Assert.areEqual(foo2.getName(), foo1.getName());
		} finally {
			client2.close();
		}
	}
	
	public void testObjectGoneAfterUpdateAndDeletion() throws Exception {		
		Item foo = itemByName("Foo");
		foo.setName("Foo*");
		db().delete(foo);
		
		reopen();
		Assert.isNull(itemByName("Foo"));
		Assert.isNull(itemByName("Foo*"));		
	}

	public void testTransparentUpdate() throws Exception {
		
		Item foo = itemByName("Foo");
		Item bar = itemByName("Bar");
		
		Assert.areEqual("Bar", bar.getName()); // accessed but not changed
		foo.setName("Bar"); // changing more than once shouldn't be a problem
		foo.setName("Foo*");
		
		assertUpdatedObjects(foo);
		
		reopen();
		
		Assert.isNotNull(itemByName("Foo*"));
		Assert.isNull(itemByName("Foo"));
		Assert.isNotNull(itemByName("Bar"));
	}
	
	public void testChangedAfterCommit() throws Exception {
		final Item item = itemByName("Foo");
		item.setName("Bar");
		assertUpdatedObjects(item);
		
		item.setName("Foo");
		assertUpdatedObjects(item);
	}
	
	public void testUpdateAfterActivation() throws Exception {
		Item foo = itemByName("Foo");
		Assert.areEqual("Foo", foo.getName());
		foo.setName("Foo*");
		assertUpdatedObjects(foo);
	}

	private void assertUpdatedObjects(Item expected) {
		assertUpdatedObjects(db(), expected);
	}

	private void assertUpdatedObjects(final ExtObjectContainer container, Item expected) {
		Collection4 updated = commitCapturingUpdatedObjects(container);
		Assert.areEqual(1, updated.size(), updated.toString());
		Assert.areSame(expected, updated.singleElement());
	}
	
	private Collection4 commitCapturingUpdatedObjects(final ExtObjectContainer container) {
		final Collection4 updated = new Collection4();
		eventRegistryFor(container).updated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				ObjectEventArgs objectArgs = (ObjectEventArgs)args;
				updated.add(objectArgs.object());
			}
		});
		container.commit();
		return updated;
	}

	private Item itemByName(String name) {
		return itemByName(db(), name);
	}

	private Item itemByName(final ExtObjectContainer container, String name) {
		Query q = newQuery(container, Item.class);
		q.descend("name").constrain(name);
		ObjectSet result = q.execute();
		if (result.hasNext()) {
			return (Item)result.next();
		}
		return null;
	}

}
