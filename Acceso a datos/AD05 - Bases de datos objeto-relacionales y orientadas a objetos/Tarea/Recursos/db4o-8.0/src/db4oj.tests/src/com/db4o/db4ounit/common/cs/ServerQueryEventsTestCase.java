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

import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;

public class ServerQueryEventsTestCase extends ClientServerTestCaseBase {
	
	public void testConstrainedQuery() {
		
		assertQueryEvents(new Runnable() { public void run() {
			final Query query = newQuery(Item.class);
			query.descend("id").constrain(42);
			query.execute();
		}});
	}

	public void testClassOnlyQuery() {
		assertQueryEvents(new Runnable() { public void run() {
			newQuery(Item.class).execute();
		}});
		
	}
	
	public void testGetAllQuery() {
		assertQueryEvents(new Runnable() { public void run() {
			newQuery().execute();
		}});
	}
	
	private void assertQueryEvents(final Runnable query) {
		final ArrayList<String> events = new ArrayList<String>();
		
		final EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(fileSession());
		eventRegistry.queryStarted().addListener(new EventListener4<QueryEventArgs>() {
			public void onEvent(Event4<QueryEventArgs> e, QueryEventArgs args) {
				events.add(QUERY_STARTED);
			}
		});
		eventRegistry.queryFinished().addListener(new EventListener4<QueryEventArgs>() {
			public void onEvent(Event4<QueryEventArgs> e, QueryEventArgs args) {
				events.add(QUERY_FINISHED);
			}
		});
		
		query.run();
		
		final String[] expected = new String[] { QUERY_STARTED, QUERY_FINISHED };
		Iterator4Assert.areEqual(expected, Iterators.iterator(events));
	}
	
	private static final String QUERY_FINISHED = "query finished";
	private static final String QUERY_STARTED = "query started";
	
	public static final class Item {
		public int id;
	}

}
