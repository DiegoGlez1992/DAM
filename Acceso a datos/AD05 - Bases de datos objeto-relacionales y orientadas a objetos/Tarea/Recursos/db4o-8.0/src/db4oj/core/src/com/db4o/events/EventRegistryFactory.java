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
package com.db4o.events;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.callbacks.*;
import com.db4o.internal.events.*;

/**
 * Provides an interface for getting an {@link EventRegistry} from an {@link ObjectContainer}. 
 */
public class EventRegistryFactory {
	
	/**
	 * Returns an {@link EventRegistry} for registering events with the specified container.
	 */
	public static EventRegistry forObjectContainer(ObjectContainer objectContainer) {
		if (null == objectContainer) {
			throw new ArgumentNullException();
		}
		
		InternalObjectContainer container = ((InternalObjectContainer)objectContainer);
		Callbacks callbacks = container.callbacks();
		if (callbacks instanceof EventRegistry) {
			return (EventRegistry)callbacks;
		}		
		if (callbacks instanceof NullCallbacks) {
			EventRegistryImpl impl = container.newEventRegistry();
			container.callbacks(impl);
			return impl;
		}
		
		// TODO: create a MulticastingCallbacks and register both
		// the current one and the new one
		throw new IllegalArgumentException();
	}
}
