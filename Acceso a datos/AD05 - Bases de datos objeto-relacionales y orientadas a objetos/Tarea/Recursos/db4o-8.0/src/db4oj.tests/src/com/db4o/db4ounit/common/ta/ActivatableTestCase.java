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
package com.db4o.db4ounit.common.ta;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.mocking.*;

public class ActivatableTestCase extends TransparentActivationTestCaseBase {
	
	public static void main(String[] args) {
		new ActivatableTestCase().runAll();
	}
	
	public void testActivatorIsBoundUponStore() {
		
		final MockActivatable mock = storeNewMock();
		assertSingleBindCall(mock);
	}

	public void testActivatorIsBoundUponRetrieval() throws Exception {
		
		storeNewMock();
		reopen();
		assertSingleBindCall(retrieveMock());
	}

	
	public void testActivatorIsUnboundUponClose() throws Exception {
		final MockActivatable mock = storeNewMock();
		reopen();
		assertBindUnbindCalls(mock);
	}

	public void testUnbindingIsIsolated() {
		if (!isMultiSession()) {
			return;
		}
		
		final MockActivatable mock1 = storeNewMock();
		db().commit();
		
		final MockActivatable mock2 = retrieveMockFromNewClientAndClose();
		assertBindUnbindCalls(mock2);
		
		// mock1 has only be bound by store so far
		// client.close should have no effect on it
		mock1.recorder().verify(
			new MethodCall("bind", new MethodCall.ArgumentCondition() {
				public void verify(Object argument) {
					Assert.isNotNull(argument);
				}
			})
		);
	}

	private MockActivatable retrieveMockFromNewClientAndClose() {
		final ExtObjectContainer client = openNewSession();
		try {
			return retrieveMock(client);
		} finally {
			client.close();
		}
	}
	
	private void assertBindUnbindCalls(final MockActivatable mock) {
		mock.recorder().verify(
			new MethodCall("bind", MethodCall.IGNORED_ARGUMENT),
			new MethodCall("bind", new Object[] { null })
		);
	}

	private void assertSingleBindCall(final MockActivatable mock) {
		mock.recorder().verify(
			new MethodCall("bind", MethodCall.IGNORED_ARGUMENT)
		);
	}

	private MockActivatable retrieveMock() {
		return retrieveMock(db());
	}

	private MockActivatable retrieveMock(final ExtObjectContainer container) {
		return (MockActivatable) retrieveOnlyInstance(container, MockActivatable.class);
	}
	
	private MockActivatable storeNewMock() {
		final MockActivatable mock = new MockActivatable();
		store(mock);
		return mock;
	}
}
