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
package com.db4o.db4ounit.jre11.events;

import com.db4o.events.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class GlobalLifecycleEventsTestCase extends AbstractDb4oTestCase implements OptOutMultiSession{
	
	public static void main(String[] arguments) {
		new GlobalLifecycleEventsTestCase().runNetworking();
	}
	
	public static final class Item {

		public int id;
		
		public Item() {
		}

		public Item(int id_) {
			id = id_;
		}
		
		public boolean equals(Object obj) {
			if (!(obj instanceof Item)) return false;
			return ((Item)obj).id == id;
		} 
		
		public String toString() {
			return "Item(" + id + ")";
		}
	}
	
	private EventRecorder _recorder;
	
	protected void db4oSetupBeforeStore() throws Exception {
		_recorder = new EventRecorder(fileSession().lock());
	}	
	
	public void testStoreLifecycle() {
		listenToEvent(eventRegistry().instantiated());
		final Event4 creating = eventRegistry().creating();
		final Event4 created = eventRegistry().created();
		
		listenToEvent(creating);
		listenToEvent(created);
		
		final Item item = storeItem();
		assertEvents(new Event4[] { creating, created }, item);
	}
	
	public void testActivating() throws Exception {
		// FIXME: It fails in c/s mode. The objects are activated twice during
		// readPrefetch.
		// The first place is in YapObject#readPrefetch:
		// a_reader.setInstantiationDepth(_class.configOrAncestorConfig() ==
		// null ? 1 : 0);
		// The second place is in ClientQueryResultIterator#current():
		// return _client.activate(prefetchedCurrent());
		// Therefore, activating event listener is notified twice. The test is
		// good, and the implementation code should be FIXED.
		storeAndReopen();
		assertActivationEvent(eventRegistry().activating());
	}
	
	public void testCancelDeactivating() {
		listenToEvent(eventRegistry().deactivating());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		db().deactivate(item, 1);
		
		assertSingleObjectEventArgs(eventRegistry().deactivating(), item);
		
		Assert.areEqual(1, item.id);
	}
	
	public void testDeleting() throws Exception {
		assertDeletionEvent(eventRegistryForDelete().deleting());
	}
	
	public void testDeleted() throws Exception {
		assertDeletionEvent(eventRegistryForDelete().deleted());
	}
	
	private void assertDeletionEvent(Event4 event4) throws Exception {
		assertDeletionEvent(event4, false);
	}

	private void assertDeletionEvent(Event4 event, boolean cancel) throws Exception {
		listenToEvent(event);
		
		Item item = storeItem();
		if (cancel) {
			_recorder.cancel(true);
		}
		
		Item expectedItem = isMultiSession() ? queryServerItem(item) : item;
		db().delete(item);
		sync();
		
		assertSingleObjectEventArgs(event, expectedItem);
		
		if (cancel) {
			Assert.areSame(item, db().queryByExample(Item.class).next());
		} else {
			Assert.areEqual(0, db().queryByExample(Item.class).size());
		}
	}
	
	public void testCancelDeleting() throws Exception {
		assertDeletionEvent(eventRegistryForDelete().deleting(), true);
	}	
	
	public void testCancelCreating() {	
		listenToEvent(eventRegistry().creating());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		
		assertSingleObjectEventArgs(eventRegistry().creating(), item);
		
		Assert.areEqual(0, db().queryByExample(Item.class).size());
	}

	public void testCancelUpdating() throws Exception {
		listenToEvent(eventRegistry().updating());
		
		_recorder.cancel(true);
		
		Item item = storeItem();
		item.id = 42;
		db().store(item);
		
		assertSingleObjectEventArgs(eventRegistry().updating(), item);
		
		reopen();
		
		item = (Item)db().queryByExample(Item.class).next();
		Assert.areEqual(1, item.id);
	}
	
	public void testCreating() {
		assertCreationEvent(eventRegistry().creating());
	}
	
	public void testDeactivating() throws Exception {
		assertDeactivationEvent(eventRegistry().deactivating());
	}
	
	public void testUpdating() {
		assertUpdateEvent(eventRegistry().updating());
	}
	
	public void testActivated() throws Exception {
		storeAndReopen();
		assertActivationEvent(eventRegistry().activated());
	}

	public void testDeactivated() throws Exception {
		assertDeactivationEvent(eventRegistry().deactivated());
	}
	
	public void testCreated() {
		assertCreationEvent(eventRegistry().created());
	}
	
	public void testUpdated() {
		assertUpdateEvent(eventRegistry().updated());
	}

	private void assertActivationEvent(Event4 event) throws Exception {
		listenToEvent(event);
		
		Item item = (Item)db().queryByExample(Item.class).next();
		
		assertSingleObjectEventArgs(event, item);
	}
	
	private void assertCreationEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = storeItem();
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areSame(item, db().queryByExample(Item.class).next());
	}
	
	private void assertDeactivationEvent(Event4 event) throws Exception {
		listenToEvent(event);
		
		Item item = storeItem();
		db().deactivate(item, 1);
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areEqual(0, item.id);
	}
	
	private Item queryServerItem(Item item) {
		return (Item)fileSession().queryByExample(item).next();
	}

	private void assertSingleObjectEventArgs(Event4 expectedEvent, Item expectedItem) {
		assertEvents(new Event4[] { expectedEvent }, expectedItem);
	}

	private void assertEvents(Event4[] expectedEvents, Item expectedItem) {
		Assert.areEqual(expectedEvents.length, _recorder.size());
		for (int i=0; i<expectedEvents.length; ++i) {
			EventRecord record = _recorder.get(i);
			Assert.areSame(expectedEvents[i], record.e);
			
			final Object actual = ((ObjectEventArgs)record.args).object();
			Assert.areSame(expectedItem, actual);
		}
	}	
	
	private void assertUpdateEvent(Event4 event) {
		listenToEvent(event);
		
		Item item = storeItem();		
		item.id = 42;
		db().store(item);
		
		assertSingleObjectEventArgs(event, item);
		
		Assert.areSame(item, db().queryByExample(Item.class).next());
	}
	
	private EventRegistry eventRegistryForDelete() {
		return serverEventRegistry();
	}

	private void listenToEvent(Event4 event) {
		event.addListener(_recorder);
	}
	
	private void storeAndReopen() throws Exception {
		storeItem();
		reopen();
	}

	private Item storeItem() {
		Item item = new Item(1);
		db().store(item);
		db().commit();
		sync();
		return item;
	}

	private void sync() {
		if (!isMultiSession()) return;
		final String name = "___";
		db().setSemaphore(name, 0);
		db().releaseSemaphore(name);
	}
	
}
