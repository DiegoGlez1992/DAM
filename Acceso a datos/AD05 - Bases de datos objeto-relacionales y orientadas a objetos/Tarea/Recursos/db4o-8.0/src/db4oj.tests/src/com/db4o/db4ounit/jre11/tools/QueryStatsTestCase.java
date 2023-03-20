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
package com.db4o.db4ounit.jre11.tools;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.query.*;
import com.db4o.tools.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class QueryStatsTestCase extends AbstractDb4oTestCase {	
	
	public static class Item {
	}
	
	private static final int ITEM_COUNT = 10;
	private QueryStats _stats;
	
	final EventListener4 _sleepOnQueryStart = new EventListener4() {
		public void onEvent(com.db4o.events.Event4 e, com.db4o.events.EventArgs args) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException x) {
				x.printStackTrace();
			}
		}
	};

	protected void store() {
		for (int i=0; i<ITEM_COUNT; ++i) {
			db().store(new Item());
		}
	}
	
	protected void db4oSetupAfterStore() throws Exception {
		_stats = new QueryStats();		
		_stats.connect(db());
	}

	protected void db4oTearDownBeforeClean() throws Exception {
		_stats.disconnect();
	}

	public void testActivationCount() {
		
		Query q = db().query();		
		q.constrain(Item.class);
		
		ObjectSet result = q.execute();
		Assert.areEqual(0, _stats.activationCount());
		result.next();
		
		Assert.areEqual(1, _stats.activationCount());
		result.next();
		Assert.areEqual(2, _stats.activationCount());
	}

	public void testExecutionTime() {
		
		sleepOnQueryStart();
		
		Query q = db().query();		
		q.constrain(Item.class);		
		
		long started = System.currentTimeMillis();		
		q.execute();		
		long elapsed = System.currentTimeMillis() - started;
		Assert.isTrue(_stats.executionTime() >= 0);
		Assert.isTrue(_stats.executionTime() <= elapsed);
	}

	private void sleepOnQueryStart() {
		queryStartedEvent().addListener(_sleepOnQueryStart);
	}	
	
	private Event4 queryStartedEvent() {
		return EventRegistryFactory.forObjectContainer(fileSession()).queryStarted();
	}

	public static void main(String[] args) {
		new QueryStatsTestCase().runSoloAndClientServer();
	}
}
