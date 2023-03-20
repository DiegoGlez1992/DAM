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

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.extensions.*;

public class CommittedCallbacksByAnotherClientTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new CommittedCallbacksByAnotherClientTestCase().runAll();
	}
	
	private static final ObjectInfo[] NONE = new ObjectInfo[0];
	
	public static final class Item {
		public int id;
		
		public Item() {
		}
		
		public Item(int id_) {
			id = id_;
		}
		
		public String toString() {
			return "Item(" + id + ")";
		}
	}

	private EventRecorder _eventRecorder;
	private ExtObjectContainer _anotherClient;	
	
	protected void configure(Configuration config) {
		indexField(config, Item.class, "id");
	}
	
	protected void store() throws Exception {
		for (int i=0; i<3; ++i) {
			store(new Item(i));
		}
	}
	
	protected void db4oSetupAfterStore() throws Exception {
		_eventRecorder = new EventRecorder(db().lock());
		committed().addListener(_eventRecorder);
		_anotherClient = openNewSession();
	}
	
	protected void db4oTearDownBeforeClean() throws Exception {
		committed().removeListener(_eventRecorder);
		if(_anotherClient != null) {
			_anotherClient.close();
		}
	}
	
	public void testCommittedAdded() {
		Item item4 = new Item(4);
		Item item5 = new Item(5);
		_anotherClient.store(item4);
		_anotherClient.store(item5);
		
		ObjectInfo info4 = getInfo(_anotherClient, 4);
		ObjectInfo info5 = getInfo(_anotherClient, 5);
		
		assertNoEvents();
		
		_anotherClient.commit();
	
		assertAddedDeletedUpdated(new ObjectInfo[] { info4, info5 }, NONE, NONE);
	}
	
	public void testCommittedAddedDeleted() {
		Item item4 = new Item(4);
		Item item1 = getItem(_anotherClient, 1);
		Item item2 = getItem(_anotherClient, 2);
		
		ObjectInfo info1 = getInfo(_anotherClient, 1);
		ObjectInfo info2 = getInfo(_anotherClient, 2);
		
		_anotherClient.store(item4);
		_anotherClient.delete(item1);
		_anotherClient.delete(item2);
		
		ObjectInfo info4 = getInfo(_anotherClient, 4);
		
		assertNoEvents();
		
		_anotherClient.commit();
		assertAddedDeletedUpdated(new ObjectInfo[] { info4 }, new ObjectInfo[] { info1, info2 }, NONE);
	}
	
	public void testCommittedForItemAddedAndImmediatelyDeleted() {
		
		Item item4 = new Item(4);
		_anotherClient.store(item4);
		
		_anotherClient.delete(item4);
		
		assertNoEvents();
		_anotherClient.commit();
		assertAddedDeletedUpdated(NONE, NONE, NONE);
	}
	
	public void testCommittedAddedUpdatedDeleted() {
		Item item1 = getItem(_anotherClient, 1);
		Item item2 = getItem(_anotherClient, 2);
		
		ObjectInfo info1 = getInfo(_anotherClient, 1);
		ObjectInfo info2 = getInfo(_anotherClient, 2);
		
		Item item4 = new Item(4);
		_anotherClient.store(item4);
		_anotherClient.store(item2);
		_anotherClient.delete(item1);
		
		ObjectInfo info4 = getInfo(_anotherClient, 4);
		
		assertNoEvents();
		
		_anotherClient.commit();
		assertAddedDeletedUpdated(new ObjectInfo[] { info4 }, new ObjectInfo[] { info1 }, new ObjectInfo[] { info2 });
	}
	
	public void testCommittedDeleted(){
		Item item1 = getItem(_anotherClient, 1);
		ObjectInfo info1 = getInfo(_anotherClient, 1);
		
		assertNoEvents();
		
		_anotherClient.delete(item1);
		
		_anotherClient.commit();
		
		assertAddedDeletedUpdated(NONE, new ObjectInfo[] { info1 }, NONE);
	}
	
	public void testObjectSetTwiceShouldStillAppearAsAdded() {
		final Item item4 = new Item(4);
		_anotherClient.store(item4);
		_anotherClient.store(item4);
		
		ObjectInfo info4 = getInfo(_anotherClient, 4);
		
		_anotherClient.commit();
		assertAddedDeletedUpdated(new ObjectInfo[] { info4 }, NONE, NONE);
	}
	
	private Item getItem(ExtObjectContainer oc, int id) {
		Query query = oc.query();
		query.constrain(Item.class);
		query.descend("id").constrain(new Integer(id));
		return (Item)query.execute().next();
	}
	
	private ObjectInfo getInfo(ExtObjectContainer oc, int itemId) {
		Item item = getItem(oc, itemId);
		Transaction trans = ((InternalObjectContainer)oc).transaction();
		return new FrozenObjectInfo(trans, trans.referenceForObject(item), false);
	}

	private void assertAddedDeletedUpdated(
			final ObjectInfo[] expectedAdded,
			final ObjectInfo[] expectedDeleted,
			final ObjectInfo[] expectedUpdated) {
		
		EventAssert.assertCommitEvent(_eventRecorder, committed(), expectedAdded, expectedDeleted, expectedUpdated);
	}

	private void assertNoEvents() {
		EventAssert.assertNoEvents(_eventRecorder);
	}

	private Event4 committed() {
		return eventRegistry().committed();
	}
}
