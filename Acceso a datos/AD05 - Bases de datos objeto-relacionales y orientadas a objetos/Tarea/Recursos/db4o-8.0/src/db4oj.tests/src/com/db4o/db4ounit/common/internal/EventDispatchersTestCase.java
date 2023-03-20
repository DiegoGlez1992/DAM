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
package com.db4o.db4ounit.common.internal;

import com.db4o.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;


// FIXME: implement all cases
public class EventDispatchersTestCase extends AbstractInMemoryDb4oTestCase {

	public static class NoCallbacks {
	}
	
	public void testNoCallbacks() {
		final EventDispatcher dispatcher = eventDispatcherFor(NoCallbacks.class);
		Assert.areSame(EventDispatchers.NULL_DISPATCHER, dispatcher);
	}

	private EventDispatcher eventDispatcherFor(final Class<?> clazz) {
	    return EventDispatchers.forClass(container(), reflectClass(clazz));
    }

	public static class SingleCallback {
		boolean objectCanDelete(ObjectContainer container) {
			return false;
		}
	}

	@SuppressWarnings("unused")
	public static class AllCallbacks {
		
		public boolean objectCanDelete(ObjectContainer container) {
			return false;
		}

		protected void objectOnDelete(ObjectContainer container) {
		}

		private void objectOnActivate(ObjectContainer container) {
		}

		void objectOnDeactivate(ObjectContainer container) {
		}

		private void objectOnNew(ObjectContainer container) {
		}

		private void objectOnUpdate(ObjectContainer container) {
		}

		private boolean objectCanActivate(ObjectContainer container) {
			return false;
		}

		private boolean objectCanDeactivate(ObjectContainer container) {
			return false;
		}

		private boolean objectCanNew(ObjectContainer container) {
			return false;
		}

		private boolean objectCanUpdate(ObjectContainer container) {
			return false;
		}
	}

}
