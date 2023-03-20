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
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class EventCountTestCase extends AbstractDb4oTestCase {

	private static final int MAX_CHECKS = 10;
	private static final long WAIT_TIME = 10;
	private SafeCounter _activated = new SafeCounter();
	private SafeCounter _updated = new SafeCounter();
	private SafeCounter _deleted = new SafeCounter();
	private SafeCounter _created = new SafeCounter();
	private SafeCounter _committed = new SafeCounter();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new EventCountTestCase().runAll();
	}

	public void testEventRegistryCounts() throws Exception {
		registerEventHandlers();

		for (int i = 0; i < 1000; i++) {
			Item item = new Item(i);
			db().store(item);
			Assert.isTrue(db().isStored(item));

			if (((i + 1) % 100) == 0) {
				db().commit();
			}
		}

		assertCount(_created, 1000, "created");
		assertCount(_committed, 10, "commit");

		reopenAndRegister();

		ObjectSet items = newQuery(Item.class).execute();
		Assert.areEqual(1000, items.size(), "Wrong number of objects retrieved");
		
		while (items.hasNext()) {
			Item item = (Item) items.next();
			item._value++;
			store(item);
		}

		assertCount(_activated, 1000, "activated");
		assertCount(_updated, 1000, "updated");

		items.reset();		
		while (items.hasNext()) {

			Object item = items.next();
			db().delete(item);
			Assert.isFalse(db().isStored(item));
		}

		assertCount(_deleted, 1000, "deleted");
	}

	private void assertCount(SafeCounter actual, int expected, String name) throws InterruptedException {
		actual.assertEquals(expected, MAX_CHECKS);
	}
	
	private void reopenAndRegister() throws Exception {
		reopen();
		registerEventHandlers();
	}

	private void registerEventHandlers() {
		ObjectContainer deletionEventSource = db();
		if (fixture() instanceof Db4oClientServerFixture) {
			Db4oClientServerFixture clientServerFixture = (Db4oClientServerFixture) fixture();
			deletionEventSource = clientServerFixture.server().ext().objectContainer();
		}
		
		EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(db());
		EventRegistry deletionEventRegistry = EventRegistryFactory.forObjectContainer(deletionEventSource);

		// No dedicated IncrementListener class due to sharpen event semantics
		
		deletionEventRegistry.deleted().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_deleted.increment();
			}
		});		
		eventRegistry.activated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_activated.increment();
			}
		});
		eventRegistry.committed().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_committed.increment();
			}
		});
		eventRegistry.created().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_created.increment();
			}
		});
		eventRegistry.updated().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				_updated.increment();
			}
		});
	}

	public static class Item {
		public Item(int i) {
			_value = i;
		}

		public int _value;
	}
	
	private static class SafeCounter {
		private int _value;
		private Lock4 _lock = new Lock4();		
		
		public void increment() {
			_lock.run(new Closure4() { public Object run() {
				_value++;
				return null; 
			}});
		}

		public void assertEquals(final int expected, int maxChecks) {
			final IntByRef ret = new IntByRef();
			for(int checkCount = 0; checkCount < MAX_CHECKS && ret.value != expected; checkCount++) {
				_lock.run(new Closure4() { public Object run() {
					if(_value != expected) {
						_lock.snooze(WAIT_TIME);
					}
					ret.value = _value;
					return null;
				}});
			}
			Assert.areEqual(expected, ret.value);
		}
	}	
	
}
