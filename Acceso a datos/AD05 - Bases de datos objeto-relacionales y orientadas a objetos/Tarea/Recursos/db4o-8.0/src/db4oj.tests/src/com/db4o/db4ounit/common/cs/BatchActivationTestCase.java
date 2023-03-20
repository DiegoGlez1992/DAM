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
import com.db4o.config.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * Options:
 * 
 * 1) activate the objects on the server up to prefetchDepth and store them into
 * the TransportObjectContainer 1.1) connect objects to the local client cache
 * 
 * 2) activate the objects on the server up to prefetchDepth, collect all IDs
 * and send the required slots to the client 2.1) connect the objects to the
 * local client cache
 * 
 * 2') don't activate the objects but traverse slots collecting IDs instead
 * 
 * 3) Introduce slot cache in the client and prefetch slots every time objects
 * are activated and the required slots (prefetchDepth) are not available
 * 
 */
public class BatchActivationTestCase extends FixtureTestSuiteDescription implements OptOutAllButNetworkingCS {
	
	
	{
		testUnits(BatchActivationTestUnit.class);
		fixtureProviders(
			new SubjectFixtureProvider(
				// first - prefetchDepth
				// second - expected number of messages exchanged
				Pair.of(0, 2),
				Pair.of(1, 0)));
	}
	

	public static class BatchActivationTestUnit extends ClientServerTestCaseBase {
		@Override
		protected void configure(Configuration config) throws Exception {
			config.clientServer().prefetchDepth(prefetchDepth());
		}

		@Override
		protected void store() throws Exception {
			store(new Item("foo"));
		}

		public void testClassOnlyQuery() {

			final Query query = newQuery(Item.class);
			assertBatchBehaviorFor(query);

		}

		public void testConstrainedQuery() {

			final Query query = newConstrainedQuery();
			assertBatchBehaviorFor(query);

		}
		
		public void testQueryPrefetchDepth0() {
			
			final Query query = newConstrainedQuery();
			
			client().config().clientServer().prefetchDepth(0);
			
			assertBatchBehaviorFor(query, 2);
		}
		
		public void testQueryPrefetchDepth1() {
			
			final Query query = newConstrainedQuery();
			
			client().config().clientServer().prefetchDepth(1);
			
			assertBatchBehaviorFor(query, 0);
		}
		
		public void testQueryPrefetchDepth0ForClassOnlyQuery() {
			
			final Query query = newQuery(Item.class);
			
			client().config().clientServer().prefetchDepth(0);
			
			assertBatchBehaviorFor(query, 2);
		}
		
		public void testQueryPrefetchDepth1ForClassOnlyQuery() {
			
			final Query query = newQuery(Item.class);
			
			client().config().clientServer().prefetchDepth(1);
			
			assertBatchBehaviorFor(query, 0);
		}
		
		private Query newConstrainedQuery() {
			final Query query = newQuery(Item.class);
			query.descend("name").constrain("foo");
			return query;
		}

		private void assertBatchBehaviorFor(final Query query) {
			assertBatchBehaviorFor(query, expectedMessageCount());
		}

		private void assertBatchBehaviorFor(final Query query, final int expectedMessageCount) {
	        final ObjectSet<Item> result = query.execute();

			final List<Message> messages = MessageCollector.forServerDispatcher(serverDispatcher());

			Assert.areEqual("foo", result.next().name);

			Assert.areEqual(expectedMessageCount, messages.size(), messages.toString());
        }
		

		private int prefetchDepth() {
	        return subject().first;
        }

		private Pair<Integer, Integer> subject() {
	        return SubjectFixtureProvider.value();
        }

		private int expectedMessageCount() {
	        return subject().second;
        }

		public static class Item {

			public String name;

			public Item(String name) {
				this.name = name;
			}
		}
	}
}
