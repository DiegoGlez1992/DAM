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
package com.db4o.db4ounit.common.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.internal.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ServerRevokeAccessTestCase
	extends Db4oClientServerTestCase
	implements OptOutAllButNetworkingCS {

	private static final String SERVER_HOSTNAME = "127.0.0.1";

	public static void main(String[] args) {
		new ServerRevokeAccessTestCase().runAll();
	}

	/**
	 * @sharpen.if !CF
	 */
	public void test() throws IOException {
		final String user = "hohohi";
		final String password = "hohoho";
		ObjectServer server = clientServerFixture().server();
		server.grantAccess(user, password);

		ObjectContainer con = openClient(user, password);
		Assert.isNotNull(con);
		con.close();

		server.ext().revokeAccess(user);

		Assert.expect(Exception.class, new CodeBlock() {
			public void run() throws Throwable {
				openClient(user, password);
			}
		});
	}

	private ObjectContainer openClient(final String user, final String password) {
		return com.db4o.cs.Db4oClientServer.openClient(Db4oClientServerLegacyConfigurationBridge.asClientConfiguration(config()), SERVER_HOSTNAME,
				clientServerFixture().serverPort(), user, password);
	}

	private Configuration config() {
	    return clientServerFixture().config();
    }
}
