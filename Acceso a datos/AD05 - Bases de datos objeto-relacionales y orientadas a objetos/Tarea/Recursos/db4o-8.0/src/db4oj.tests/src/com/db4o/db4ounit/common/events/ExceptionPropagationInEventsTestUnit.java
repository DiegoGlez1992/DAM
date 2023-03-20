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

import java.util.*;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;

public class ExceptionPropagationInEventsTestUnit extends EventsTestCaseBase {

	public ExceptionPropagationInEventsTestUnit() {
		_eventFirer.put("insert", newObjectInserter());
		_eventFirer.put("query", newQueryRunner());
		_eventFirer.put("update", newObjectUpdater());
		_eventFirer.put("delete", newObjectDeleter());		
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item(1));
		store(new Item(2));		
	}
	
	public void testEvents() {
		final EventInfo event = eventToTest();
		if(isEmbedded()) {
			return;
		}
		if (isNetworking() && !event.isClientServerEvent()) {
			return;
		}
		
		assertEventThrows(event.eventFirerName(), _eventFirer.get(event.eventFirerName()), event.listenerSetter());
	}

	private EventInfo eventToTest() {
	    return (EventInfo) ExceptionPropagationInEventsTestVariables.EVENT_SELECTOR.value();
    }	

	private void assertEventThrows(String eventName, final CodeBlock codeBlock, final Procedure4<EventRegistry> listenerSetter) {
		final EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(db());		
		listenerSetter.apply(eventRegistry);		
		Assert.expect(EventException.class, NotImplementedException.class, codeBlock, eventName);
	}
	
	private CodeBlock newObjectUpdater() {
		return new CodeBlock() {
			
			public void run() throws Throwable {
				final Item item = retrieveItem(1);
				item.id = 10;
				
				db().store(item);
				db().commit();
			}
			
		};
	}

	private CodeBlock newObjectDeleter() {
		return new CodeBlock() {
			public void run() throws Throwable {
				db().delete(retrieveItem(1));
				db().commit();
			}			
		};
	}
	
	private CodeBlock newQueryRunner() {
		return new CodeBlock() {
			public void run() {
				retrieveItem(1);
			}		
		};
	}
	
	private CodeBlock newObjectInserter() {		
		return new CodeBlock() {

			public void run() throws Throwable {				
				db().store(new Item());
				db().commit();
			}			
		};
	}

	private Item retrieveItem(int id) {
		final Query query = newQuery(Item.class);
		query.descend("id").constrain(id);
		final ObjectSet<Item> results = query.execute();
		
		Assert.areEqual(1, results.size());
		
		final Item found = results.next();
		Assert.areEqual(id, found.id);
		
		return found;
	}	
	
	private HashMap<String, CodeBlock> _eventFirer = new HashMap();	
}
