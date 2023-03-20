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

import db4ounit.*;

public class DeletionEventsTestCase extends EventsTestCaseBase {
	
	protected void configure(Configuration config) {
		config.activationDepth(1);
	}
	
	public void testDeletionEvents() {
		
		if (isEmbedded()) {
			// TODO: something wrong when embedded c/s is run as part
			// of the full test suite
			return;
		}
		final EventLog deletionLog = new EventLog();
		
		serverEventRegistry().deleting().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				deletionLog.xing = true;
				assertItemIsActive(args);
			}
		});
		serverEventRegistry().deleted().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				deletionLog.xed = true;
				assertItemIsActive(args);
			}
		});
		
		db().delete(retrieveOnlyInstance(Item.class));
		db().commit();
		Assert.isTrue(deletionLog.xing);
		Assert.isTrue(deletionLog.xed);
	}

	private void assertItemIsActive(EventArgs args) {
		Assert.areEqual(1, itemForEvent(args).id);
	}

	private Item itemForEvent(EventArgs args) {
		return ((Item)((ObjectEventArgs)args).object());
	}
}
