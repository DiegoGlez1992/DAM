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

import com.db4o.events.*;
import com.db4o.foundation.*;

class EventInfo {
	public EventInfo(String eventFirerName, Procedure4<EventRegistry> eventListenerSetter) {
		this(eventFirerName, true, eventListenerSetter);
	}

	public EventInfo(String eventFirerName, boolean isClientServerEvent, Procedure4<EventRegistry> eventListenerSetter) {
		_listenerSetter = eventListenerSetter;
		_eventFirerName = eventFirerName;
		_isClientServerEvent = isClientServerEvent;
	}

	public Procedure4<EventRegistry> listenerSetter() {
		return _listenerSetter;
	}

	public String eventFirerName() {
		return _eventFirerName;
	}

	public boolean isClientServerEvent()  {
		return _isClientServerEvent;
	}
	
	private final Procedure4<EventRegistry> _listenerSetter;
	private final String _eventFirerName;
	private final boolean _isClientServerEvent;
}