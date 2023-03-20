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
package com.db4o.monitoring;

import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.internal.*;

/**
 * Publishes statistics about object lifecycle events to JMX.
 * 
 * In client/server setups the counters ObjectsStoredPerSec,
 * ObjectsActivatedPerSec and ObjectsDeactivatedPerSec are
 * only tracked on the client side. The counter 
 * ObjectsDeletedPerSec is only tracked on the server side.
 */
@decaf.Ignore
public class ObjectLifecycleMonitoringSupport implements ConfigurationItem {

	public void apply(InternalObjectContainer container) {
		
		final ObjectLifecycle objectLifecycle = Db4oMBeans.newObjectLifecycleMBean(container);
		
		EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(container);
		
		eventRegistry.created().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				objectLifecycle.notifyStored();
			}
		});
		
		eventRegistry.updated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				objectLifecycle.notifyStored();
			}
		});
		
		eventRegistry.activated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				objectLifecycle.notifyActivated();
			}
		});
		
		eventRegistry.deactivated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				objectLifecycle.notifyDeactivated();
			}
		});
		
		if(container.isClient()){
			return;
		}
		
		eventRegistry.deleted().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				objectLifecycle.notifyDeleted();
			}
		});
	

		
	}

	public void prepare(Configuration configuration) {
		// TODO Auto-generated method stub
		
	}

}
