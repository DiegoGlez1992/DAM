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
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class CommittingCallbacksForClientServerTestCase extends AbstractDb4oTestCase implements OptOutSolo {
	
	public static final class Item {
	}
	
	public static void main(String[] arguments) {
		new CommittingCallbacksForClientServerTestCase().runNetworking();
	}
	
	
	public void testTriggerCommitting() {
		
		final EventRecorder clientRecorder = new EventRecorder(fixture().db().lock());
		clientRegistry().committing().addListener(clientRecorder);
		
		final EventRecorder serverRecorder = new EventRecorder(fileSession().lock());
		serverEventRegistry().committing().addListener(serverRecorder);		
		
		final Item item = new Item();
		final ExtObjectContainer client = db();
		client.store(item);
		client.commit();
		
		Runtime4.sleep(50);
		
		EventAssert.assertCommitEvent(serverRecorder, serverEventRegistry().committing(), new ObjectInfo[] { infoFor(item) }, new ObjectInfo[0], new ObjectInfo[0]);
	    
		// For MTOC we expect the same events, in a normal client we don't want to see these events. 
		if(isEmbedded()){
		    EventAssert.assertCommitEvent(clientRecorder, serverEventRegistry().committing(), new ObjectInfo[] { infoFor(item) }, new ObjectInfo[0], new ObjectInfo[0]);
		}else{
		    EventAssert.assertNoEvents(clientRecorder);
		}
		
	}
	
	private ObjectInfo infoFor(Object obj){
		int id = (int) db().getID(obj);
		return new LazyObjectReference(fileSession().transaction(), id);
	}

	private EventRegistry clientRegistry() {
		return EventRegistryFactory.forObjectContainer(db());
	}
}
