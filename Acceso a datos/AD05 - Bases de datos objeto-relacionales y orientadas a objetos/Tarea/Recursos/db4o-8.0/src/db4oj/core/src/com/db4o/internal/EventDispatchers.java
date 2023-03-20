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
package com.db4o.internal;

import com.db4o.foundation.*;
import com.db4o.reflect.*;

/** @exclude */
public final class EventDispatchers {

	public static final EventDispatcher NULL_DISPATCHER = new EventDispatcher() {
		public boolean dispatch(Transaction trans, Object obj, int eventID) {
			return true;
		}

		public boolean hasEventRegistered(int eventID) {
			return false;
		}
	};

	private static final String[] events = {
		"objectCanDelete",
		"objectOnDelete",
		"objectOnActivate",
		"objectOnDeactivate",
		"objectOnNew",
		"objectOnUpdate",
		"objectCanActivate",
		"objectCanDeactivate",
	    "objectCanNew",
	    "objectCanUpdate"
	};

	static final int CAN_DELETE = 0;
	static final int DELETE = 1;
	static final int ACTIVATE = 2;
	static final int DEACTIVATE = 3;
	static final int NEW = 4;
	public static final int UPDATE = 5;
	static final int CAN_ACTIVATE = 6;
	static final int CAN_DEACTIVATE = 7;
	static final int CAN_NEW = 8;
	static final int CAN_UPDATE = 9;
	
	static final int DELETE_COUNT = 2;
	static final int COUNT = 10;

	private static class EventDispatcherImpl implements EventDispatcher {
		private final ReflectMethod[] methods;

		public EventDispatcherImpl(ReflectMethod[] methods_) {
			methods = methods_;
		}

		public boolean hasEventRegistered(int eventID) {
			return methods[eventID] != null;
		}

		public boolean dispatch(Transaction trans, Object obj, int eventID) {
			if (methods[eventID] == null) {
				return true;
			}
			Object[] parameters = new Object[] { trans.objectContainer() };
			ObjectContainerBase container = trans.container();
			int stackDepth = container.stackDepth();
			int topLevelCallId = container.topLevelCallId();
			container.stackDepth(0);
			try {
				Object res = methods[eventID].invoke(obj, parameters);
				if (res instanceof Boolean) {
					return ((Boolean) res).booleanValue();
				}
			} finally {
				container.stackDepth(stackDepth);
				container.topLevelCallId(topLevelCallId);
			}
			return true;
		}
	}

	public static EventDispatcher forClass(ObjectContainerBase container, ReflectClass classReflector) {

		if (container == null || classReflector == null) {
			throw new ArgumentNullException();
		}
		
		if (!container.dispatchsEvents()) {
			return NULL_DISPATCHER;
		}

		int count = eventCountFor(container);
		if (count == 0) {
			return NULL_DISPATCHER;
		}
			
		final ReflectMethod[] handlers = eventHandlerTableFor(container, classReflector);
		return hasEventHandler(handlers)
			? new EventDispatcherImpl(handlers)
			: NULL_DISPATCHER;
	}

	private static ReflectMethod[] eventHandlerTableFor(ObjectContainerBase container, ReflectClass classReflector) {
	    ReflectClass[] parameterClasses = { container._handlers.ICLASS_OBJECTCONTAINER };
		ReflectMethod[] methods = new ReflectMethod[COUNT];
		for (int i = COUNT - 1; i >= 0; i--) {
			ReflectMethod method = classReflector.getMethod(events[i], parameterClasses);
			if (null == method) {
				method = classReflector.getMethod(toPascalCase(events[i]), parameterClasses);
			}
			if (method != null) {
				methods[i] = method;
			}
		}
	    return methods;
    }

	private static boolean hasEventHandler(ReflectMethod[] methods) {
	    return Iterators.any(Iterators.iterate(methods), new Predicate4() {

			public boolean match(Object candidate) {
	           return candidate != null;
            }});
    }

	private static int eventCountFor(ObjectContainerBase container) {
	    CallBackMode callbackMode = container.configImpl().callbackMode();
	    if(callbackMode == CallBackMode.ALL) {
	    	return COUNT;
	    }
	    if(callbackMode == CallBackMode.DELETE_ONLY) {
	    	return DELETE_COUNT;
	    }
	    return 0;
    }
	
	private static String toPascalCase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}
