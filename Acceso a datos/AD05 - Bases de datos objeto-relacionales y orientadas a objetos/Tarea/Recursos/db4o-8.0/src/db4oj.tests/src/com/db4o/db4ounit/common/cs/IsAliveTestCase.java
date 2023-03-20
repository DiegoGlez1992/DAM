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

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.internal.*;
import com.db4o.db4ounit.common.api.*;

import db4ounit.*;

public class IsAliveTestCase extends TestWithTempFile {
	
	private final static String USERNAME = "db4o";
	private final static String PASSWORD = "db4o";
	
	public void testIsAlive() {
		ObjectServer server = openServer();
		int port = server.ext().port();
		ClientObjectContainer client = openClient(port);
		Assert.isTrue(client.isAlive());
		client.close();
		server.close();
	}

	public void testIsNotAlive() {
		ObjectServer server = openServer();
		int port = server.ext().port();
		ClientObjectContainer client = openClient(port);
		server.close();
		Assert.isFalse(client.isAlive());
		client.close();
	}
	
	private ObjectServer openServer() {
		ObjectServer server = Db4oClientServer.openServer(Db4oClientServer.newServerConfiguration(), tempFile(), -1);
		server.grantAccess(USERNAME, PASSWORD);
		return server;
	}

	private ClientObjectContainer openClient(int port) {
		ClientObjectContainer client = (ClientObjectContainer) Db4oClientServer.openClient(Db4oClientServer.newClientConfiguration(), "localhost", port, USERNAME, PASSWORD);
		return client;
	}

}
