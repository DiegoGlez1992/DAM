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
package com.db4o.db4ounit.common.cs;

import java.util.*;

import com.db4o.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

@decaf.Remove(decaf.Platform.JDK11)
public class PrefetchConfigurationTestCase extends ClientServerTestCaseBase implements OptOutAllButNetworkingCS{
	
	@Override
	protected void db4oSetupBeforeStore() throws Exception {
		ensureQueryGraphClassMetadataHasBeenExchanged();
	}
	
	
	public void testDefaultPrefetchDepth() {
		Assert.areEqual(0, client().config().prefetchDepth());
	}
	
	public void testPrefetchingBehaviorForClassOnlyQuery() {
		
		final Query query = client().query();
		query.constrain(Item.class);
		
		assertPrefetchingBehaviorFor(query, Msg.GET_INTERNAL_IDS);
	}
	
	public void testPrefetchingBehaviorForConstrainedQuery() {
		final Query query = client().query();
		query.constrain(Item.class);
		query.descend("child").constrain(null);
		
		assertPrefetchingBehaviorFor(query, Msg.QUERY_EXECUTE);
	}
	
	public void testRefreshIsUnaffectedByPrefetchingBehavior() {
		
		ExtObjectContainer oc1 = db();
		ExtObjectContainer oc2 = openNewSession();
		
		oc1.configure().clientServer().prefetchDepth(1);
		oc2.configure().clientServer().prefetchDepth(1);
		
		try {
			final Item itemFromClient1 = new RootItem(new Item());
			oc1.store(itemFromClient1);
			oc1.commit();
			
			itemFromClient1.child = null;
			oc1.store(itemFromClient1);
			
			Item itemFromClient2 = retrieveOnlyInstance(oc2, RootItem.class);
			Assert.isNotNull(itemFromClient2.child);

			oc1.rollback();
			itemFromClient2 = retrieveOnlyInstance(oc2, RootItem.class);
			oc2.refresh(itemFromClient2, Integer.MAX_VALUE);
			Assert.isNotNull(itemFromClient2.child);

			oc1.commit();
			itemFromClient2 = retrieveOnlyInstance(oc2, RootItem.class);
			Assert.isNotNull(itemFromClient2.child);

			oc1.store(itemFromClient1);
			oc1.commit();
			oc2.refresh(itemFromClient2, Integer.MAX_VALUE);
			itemFromClient2 = retrieveOnlyInstance(oc2, RootItem.class);
			Assert.isNull(itemFromClient2.child);
		} finally {
			oc2.close();
		}
		
	}
	
	public void testMaxPrefetchingDepthBehavior() {
		
		storeAllAndPurge(
	        new Item(new Item(new Item())),
	        new Item(new Item(new Item())),
	        new Item(new Item(new Item()))); 
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(Integer.MAX_VALUE);
		
		final Query query = client().query();
        query.constrain(Item.class);
        query.descend("child").descend("child").constrain(null).not();
		
		assertQueryIterationProtocol(query, Msg.QUERY_EXECUTE, new Stimulus[] {
			new Depth2Stimulus(),
			new Depth2Stimulus(),
			new Depth2Stimulus(Msg.READ_MULTIPLE_OBJECTS)
		});
    }
	
	public void testPrefetchingWithCyclesAscending() {
		
		final Item a = new Item(1);
		final Item b = new Item(2);
		final Item c = new Item(3);
		a.child = b;
		b.child = a;
		c.child = b;
		
		storeAllAndPurge(a, b, c); 
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(2);
		
		final Query query = queryForItemsWithChild();
		query.descend("order").orderAscending();
		
		assertQueryIterationProtocol(query, Msg.QUERY_EXECUTE, new Stimulus[] {
			new Depth2Stimulus(),
			new Depth2Stimulus(),
			new Depth2Stimulus(Msg.READ_MULTIPLE_OBJECTS)
		});
    }
	
	public void testPrefetchingWithCyclesDescending() {
		
		final Item a = new Item(1);
		final Item b = new Item(2);
		final Item c = new Item(3);
		a.child = b;
		b.child = a;
		c.child = b;
		
		storeAllAndPurge(a, b, c); 
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(2);
		
		final Query query = queryForItemsWithChild();
		query.descend("order").orderDescending();
		
		assertQueryIterationProtocol(query, Msg.QUERY_EXECUTE, new Stimulus[] {
			new Depth2Stimulus(),
			new Depth2Stimulus(),
			new Depth2Stimulus()
		});
    }
	
	public void testPrefetchingDepth2Behavior() {
		
		storeDepth2Graph(); 
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(2);
		
		final Query query = queryForItemsWithChild();
		
		assertQueryIterationProtocol(query, Msg.QUERY_EXECUTE, new Stimulus[] {
			new Depth2Stimulus(),
			new Depth2Stimulus(),
			new Depth2Stimulus(Msg.READ_MULTIPLE_OBJECTS)
		});
    }
	
	public void testGraphOfDepth2WithPrefetchDepth1() {
		
		storeDepth2Graph(); 
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(1);
		
		final Query query = queryForItemsWithChild();
		
		assertQueryIterationProtocol(query, Msg.QUERY_EXECUTE, new Stimulus[] {
			new Depth2Stimulus(Msg.READ_READER_BY_ID),
			new Depth2Stimulus(Msg.READ_READER_BY_ID),
			new Depth2Stimulus(Msg.READ_MULTIPLE_OBJECTS, Msg.READ_READER_BY_ID),
		});
    }
	
	public void testPrefetchCount1() {
		
		storeAllAndPurge(new Item(), new Item(), new Item());
		
		client().config().prefetchObjectCount(1);
		client().config().prefetchDepth(1);
		
		final Query query = queryForItemsWithoutChildren();
		
		assertQueryIterationProtocol(query, Msg.QUERY_EXECUTE, new Stimulus[] {
			new Stimulus(),
			new Stimulus(Msg.READ_MULTIPLE_OBJECTS),
			new Stimulus(Msg.READ_MULTIPLE_OBJECTS),
		});
    }
	
	public void testPrefetchingAfterDeleteFromOtherClient() {
		
		storeAllAndPurge(new Item(), new Item(), new Item());
		
		client().config().prefetchObjectCount(1);
		client().config().prefetchDepth(1);
		
		final Query query = queryForItemsWithoutChildren();
		final ObjectSet<Item> result = query.execute();
		
		deleteAllItemsFromSecondClient();
		
		Assert.isNotNull(result.next());
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				result.next();
			}
		});
	}

	private Query queryForItemsWithoutChildren() {
	    final Query query = newQuery(Item.class);
		query.descend("child").constrain(null);
	    return query;
    }

	private void deleteAllItemsFromSecondClient() {
	    final ExtObjectContainer client = openNewSession();
	    try {
	    	deleteAll(client, Item.class);
	    	client.commit();
	    } finally {
	    	client.close();
	    }
    }

	private Query queryForItemsWithChild() {
	    final Query query = client().query();
		query.constrain(Item.class);
		query.descend("child").constrain(null).not();
	    return query;
    }

	private void storeDepth2Graph() {
	    storeAllAndPurge(
				new Item(new Item()),
				new Item(new Item()),
				new Item(new Item()));
    }

	private void assertPrefetchingBehaviorFor(final Query query, final MsgD expectedFirstMessage) {
		
		storeFlatItemGraph();
		
		client().config().prefetchObjectCount(2);
		client().config().prefetchDepth(1);
		
		assertQueryIterationProtocol(query, expectedFirstMessage, new Stimulus[] {
				new Stimulus(),
				new Stimulus(),
				new Stimulus(Msg.READ_MULTIPLE_OBJECTS),
				new Stimulus(),
				new Stimulus(Msg.READ_MULTIPLE_OBJECTS),
		});
    }

	private void assertQueryIterationProtocol(final Query query, final MsgD expectedResultMessage, Stimulus[] stimuli) {
	    final List<Message> messages = MessageCollector.forServerDispatcher(serverDispatcher());
		
		final ObjectSet<Item> result = query.execute();
		assertMessages(messages, expectedResultMessage);
		messages.clear();
		
		for (Stimulus stimulus : stimuli) {
			stimulus.actUpon(result);
			assertMessages(messages, stimulus.expectedMessagesAfter);
			messages.clear();
        }
		
		if (result.hasNext()) {
			Assert.fail("Unexpected item: " + result.next());
		}
		assertMessages(messages);
    }
	
	private class Depth2Stimulus extends Stimulus {
		
		public Depth2Stimulus(MsgD... expectedMessagesAfter) {
			super(expectedMessagesAfter);
        }
		
	    @Override
	    public void actUpon(ObjectSet<Item> result) {
	    	actUpon(result.next());
	    }

		protected void actUpon(final Item item) {
	        Assert.isNotNull(item.child);
	        db().activate(item.child, 1); // ensure no further messages are exchange
        }
    }

	public static class Stimulus {
		public final MsgD[] expectedMessagesAfter;

		public Stimulus(MsgD... expectedMessagesAfter) {
			this.expectedMessagesAfter = expectedMessagesAfter;
        }

		public void actUpon(ObjectSet<Item> result) {
	        Assert.isNotNull(result.next());
        }
    }

	private void assertMessages(List<Message> actualMessages, Message... expectedMessages) {
		Iterator4Assert.areEqual(expectedMessages, Iterators.iterator(actualMessages));
    }

	private void ensureQueryGraphClassMetadataHasBeenExchanged() {
		
		container().produceClassMetadata(reflectClass(Item.class));
		
		// ensures classmetadata exists for query objects
	    final Query query = client().query();
	    query.constrain(Item.class);
	    query.descend("child").descend("child").constrain(null).not();
	    query.descend("order").orderAscending();
		Assert.areEqual(0, query.execute().size());
    }

	private void storeFlatItemGraph() {
		storeAllAndPurge(
				new Item(),
				new Item(),
				new Item(),
				new Item(),
				new Item());
	}
	
	private void storeAllAndPurge(Item... items) {
	    storeAll(items);
	    purgeAll(items);
	    client().commit();
    }

	private void storeAll(Item... items) {
	    for (Item item : items) {
			client().store(item);
		}
    }

	private void purgeAll(Item... items) {
	    final HashSet<Item> purged = new HashSet<Item>();
	    for (Item item : items) {
			purge(purged, item);
	    }
    }

	private void purge(Set<Item> purged, Item item) {
		if (purged.contains(item)) {
			return;
		}
		purged.add(item);
		
	    client().purge(item);
	    
	    final Item child = item.child;
		if (null != child) {;
	    	purge(purged, child);
	    }
    }
	
	public static class Item {
		public Item(Item child) {
			this.child = child;
        }
		
		public Item() {
		}

		public Item(int order) {
			this.order = order;
        }

		public Item child;
		public int order;
	}
	
	public static class RootItem extends Item {

		public RootItem() {
	        super();
        }

		public RootItem(Item child) {
	        super(child);
        }
		
	}

}
