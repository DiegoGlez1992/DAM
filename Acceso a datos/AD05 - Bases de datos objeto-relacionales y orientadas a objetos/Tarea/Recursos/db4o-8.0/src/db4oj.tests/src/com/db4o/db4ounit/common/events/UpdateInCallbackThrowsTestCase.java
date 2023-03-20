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
package com.db4o.db4ounit.common.events;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class UpdateInCallbackThrowsTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new UpdateInCallbackThrowsTestCase().runAll();
	}
	
	public static class Item {
		public String _name;
		public Item _child;

		public Item(String name) {
			this(name, null);
		}
		
		public Item(String name, Item child) {
			_name = name;
			_child = child;
		}
	}

	@Override
	protected void store() throws Exception {
		store(new Item("foo", new Item("bar")));
	}
	
	public void testUpdatingInDeletingCallback() {
		final boolean isNetworking = isNetworking();
		
		eventRegistryFor(fileSession()).deleting().addListener(new EventListener4<CancellableObjectEventArgs>() {
			public void onEvent(Event4 e, CancellableObjectEventArgs args) {
				final Object obj = args.object();
				if (! (obj instanceof Item)) {
					return;
				}
				
				final Transaction transaction = (Transaction)args.transaction();
				final ObjectContainer container = transaction.objectContainer();
				
				Item foo = (Item)obj;
				Item child = foo._child;
				if (isNetworking) {
					container.activate(child, 1);
				}				
				child._name += "*";				
				container.store(child);
			}
		});
		
		db().delete(itemByName("foo"));
		Assert.isNotNull(itemByName("bar*"));
	}
	
	public void testReentrantUpdateAfterActivationThrows() {
		
		final Item foo = itemByName("foo");
		db().deactivate(foo);
		
		eventRegistry().activated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4 e, ObjectInfoEventArgs args) {
				final Object obj = args.object();
				if (! (obj instanceof Item)) {
					return;
				}
				
				final Item item = (Item) obj;
				if (!item._name.equals("foo")) {
					return;
				}
					
				Assert.expect(Db4oIllegalStateException.class, new CodeBlock() { public final void run() {						
					item._child = new Item("baz");				
					store(item);					
				}});
			}
		});
		
		db().activate(foo, 1);
	}

	private Item itemByName(final String name) {
		return queryItemsByName(name).next();
	}

	public void testReentrantUpdateThrows() {
		final ByRef<Boolean> updatedTriggered = new ByRef();
		updatedTriggered.value = false;
		
		EventRegistry registry = EventRegistryFactory.forObjectContainer(db());
		registry.updated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4 e, ObjectInfoEventArgs args) {
				final Object obj = args.object();
				if (! (obj instanceof Item)) {
					return;
				}
				
				final Item item = (Item) obj;
				if (!item._name.equals("foo")) {
					return;
				}
				
				updatedTriggered.value = true;
					
				Assert.expect(Db4oIllegalStateException.class, new CodeBlock() { public final void run() {						
					item._child = new Item("baz");				
					store(item);					
				}});
			}
		});
		
		ObjectSet items = queryItemsByName("foo");
		Assert.areEqual(1, items.size());
		
		Assert.isFalse(updatedTriggered.value);
		
		store(items.next());
	
		Assert.isTrue(updatedTriggered.value);		
	}

	private ObjectSet<Item> queryItemsByName(final String name) {
	    final Query query = newQuery(Item.class);
		query.descend("_name").constrain(name);
		return query.<Item>execute();
    }
}