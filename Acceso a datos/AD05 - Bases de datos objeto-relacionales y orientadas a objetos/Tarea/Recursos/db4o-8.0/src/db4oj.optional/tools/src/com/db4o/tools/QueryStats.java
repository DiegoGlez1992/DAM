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
package com.db4o.tools;

import com.db4o.*;
import com.db4o.events.*;
import com.db4o.foundation.*;

/**
 * Keeps track of query statistics.
 * 
 * @sharpen.ignore
 */
public class QueryStats {	
	
	private EventRegistry _registry = null;
	
	protected int _activationCount;
	
	protected final StopWatch _watch = new StopWatch();
	
	private final EventListener4 _queryStarted = new EventListener4<QueryEventArgs>() {			
		public void onEvent(Event4 e, QueryEventArgs args) {
			_activationCount = 0;
			_watch.start();
		}			
	};
	
	private final EventListener4 _queryFinished = new EventListener4<QueryEventArgs>() {
		public void onEvent(Event4 e, QueryEventArgs args) {
			_watch.stop();
		}
	};
	
	private final EventListener4 _activated = new EventListener4() {
		public void onEvent(Event4 e, EventArgs args) {
			++_activationCount;
		}
	};
	
	/**
	 * How long the last query took to execute.
	 * 
	 * @return time in miliseconds
	 */
	public long executionTime() {
		return _watch.elapsed();
	}
	
	/**
	 * How many objects were activated so far.
	 */
	public int activationCount() {
		return _activationCount;
	}

	/**
	 * Starts gathering query statistics for the specified container.
	 */
	public void connect(ObjectContainer container) {
		if (_registry != null) {
			throw new IllegalArgumentException("Already connected to an ObjectContainer");
		}
		_registry = EventRegistryFactory.forObjectContainer(container);
		_registry.queryStarted().addListener(_queryStarted);
		_registry.queryFinished().addListener(_queryFinished);
		_registry.activated().addListener(_activated);
	}
	
	/**
	 * Disconnects from the current container.
	 */
	public void disconnect() {
		if (null != _registry) {
			_registry.queryStarted().removeListener(_queryStarted);
			_registry.queryFinished().removeListener(_queryFinished);
			_registry.activated().removeListener(_activated);
			_registry = null;
		}
	}
}