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

import db4ounit.*;


public class EventAssert {
	
	public static void assertNoEvents(final EventRecorder eventRecorder) {
		Assert.areEqual(0, eventRecorder.size());
	}

	public static void assertCommitEvent(final EventRecorder eventRecorder,
			final Event4 expectedEvent, final ObjectInfo[] expectedAdded,
			final ObjectInfo[] expectedDeleted, final ObjectInfo[] expectedUpdated) {
		eventRecorder.waitForEventCount(1);
		Assert.areEqual(1, eventRecorder.size(), eventRecorder.toString());		
		Assert.areSame(expectedEvent, eventRecorder.get(0).e);
		CommitEventArgs args = (CommitEventArgs)eventRecorder.get(0).args;
		assertContainsAll(expectedAdded, args.added(), "added");
		assertContainsAll(expectedDeleted, args.deleted(), "deleted");
		assertContainsAll(expectedUpdated, args.updated(), "updated");
	}
	
	private static void assertContainsAll(ObjectInfo[] expectedInfos, ObjectInfoCollection actualItems, String label) {
		for (int i = 0; i < expectedInfos.length; i++) {
			assertContains(expectedInfos[i], actualItems, label);
		}
		if (expectedInfos.length != Iterators.size(actualItems)) {
			Assert.fail(label + ": " + toString(expectedInfos) + " != " + toObjectString(actualItems));
		}
	}

	private static String toObjectString(ObjectInfoCollection actualItems) {
		return Iterators.toString(Iterators.map(actualItems.iterator(), new Function4() {
			public Object apply(Object arg) {
				final ObjectInfo info = (ObjectInfo)arg;
				return "ObjectInfo(internalID=" + info.getInternalID() + ", object=" + info.getObject() + ")";
			}
		}));
	}

	private static String toString(Object[] expectedItems) {
		return Iterators.toString(Iterators.iterate(expectedItems));
	}

	private static void assertContains(ObjectInfo expectedInfo, ObjectInfoCollection items, String label) {
		final Iterator4 iterator = items.iterator();
		while (iterator.moveNext()) {
			ObjectInfo info = (ObjectInfo)iterator.current();
			if (expectedInfo.getInternalID() == info.getInternalID()) {
				Assert.areEqual(expectedInfo.getUUID(), info.getUUID());
				Assert.areEqual(expectedInfo.getCommitTimestamp(), info.getCommitTimestamp());
				return;
			}
		}
		Assert.fail("Object '" + expectedInfo + "' not found for " + label + ".");
	}


}
