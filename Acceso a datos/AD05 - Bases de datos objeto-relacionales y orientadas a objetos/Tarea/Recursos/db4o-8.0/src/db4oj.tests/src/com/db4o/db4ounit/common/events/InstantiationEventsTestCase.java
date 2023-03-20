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

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.*;

import db4ounit.*;

public class InstantiationEventsTestCase extends EventsTestCaseBase {

	protected void configure(Configuration config) {
		config.activationDepth(0);
	}
	
	public void testInstantiationEvents() {
		
		final EventLog instantiatedLog = new EventLog();
		
		eventRegistry().instantiated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4 e, ObjectInfoEventArgs args) {
				assertClientTransaction(args);
				
				instantiatedLog.xed = true;
				Object obj = args.object();
				final ObjectReference objectReference = trans().referenceSystem().referenceForObject(obj);
				
				Assert.isNotNull(objectReference);
				Assert.areSame(objectReference, args.info());
			}
		});
		
		retrieveOnlyInstance(Item.class);
		
		Assert.isFalse(instantiatedLog.xing);
		Assert.isTrue(instantiatedLog.xed);
	}
}
