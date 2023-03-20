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
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;

public class CreationEventsTestCase extends EventsTestCaseBase {

	public void testObjectInfoIsNotAvailableOnCreatingHandler() {
		final ByRef<Boolean> executed = ByRef.newInstance(false);
		
		eventRegistry().creating().addListener(new EventListener4<CancellableObjectEventArgs>() {
			
			public void onEvent(Event4<CancellableObjectEventArgs> e, final CancellableObjectEventArgs args) {
				Assert.expect(IllegalStateException.class, new CodeBlock() {				
					public void run() throws Throwable {
						executed.value = true;
						usefulForCSharp(args.info());						
					}

					private void usefulForCSharp(ObjectInfo info) {
						Assert.fail();
					}
				});
			}
		});
		
		store(new Item());
		Assert.isTrue(executed.value);
	}
}
