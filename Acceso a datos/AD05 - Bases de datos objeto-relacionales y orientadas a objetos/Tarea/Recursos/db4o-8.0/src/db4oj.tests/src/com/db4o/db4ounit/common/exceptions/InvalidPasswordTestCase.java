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
package com.db4o.db4ounit.common.exceptions;

import com.db4o.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class InvalidPasswordTestCase
	extends Db4oClientServerTestCase
	implements OptOutAllButNetworkingCS {
	
	public void testInvalidPassword() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				openClient("127.0.0.1", port, "strangeusername",
						"invalidPassword");
			}
		});
	}
	
	protected ObjectContainer openClient(String host, int port, String user,
			String password) {
		return com.db4o.cs.Db4oClientServer.openClient(host, port, user, password);
	}

	public void testEmptyUserPassword() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				openClient("127.0.0.1", port, "", "");
			}
		});
	}
	
	public void testEmptyUserNullPassword() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				openClient("127.0.0.1", port, "", null);
			}
		});
	}
	
	public void testNullPassword() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(InvalidPasswordException.class, new CodeBlock() {
			public void run() throws Throwable {
				openClient("127.0.0.1", port, null, null);
			}
		});
	}
}
