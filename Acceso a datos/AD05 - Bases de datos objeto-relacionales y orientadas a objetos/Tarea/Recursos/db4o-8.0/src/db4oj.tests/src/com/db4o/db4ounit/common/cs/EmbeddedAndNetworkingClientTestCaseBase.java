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

import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;

public abstract class EmbeddedAndNetworkingClientTestCaseBase  implements TestLifeCycle {

	private static final String USERNAME = "db4o";
	private static final String PASSWORD = "db4o";

	private ExtObjectServer _server;
	private ExtObjectContainer _networkingClient;
	private ObjectContainerSession _embeddedClient;

	public void setUp() throws Exception {
		ServerConfiguration serverConfiguration = Db4oClientServer.newServerConfiguration();
		serverConfiguration.file().storage(new MemoryStorage());
		_server = Db4oClientServer.openServer(serverConfiguration, "", Db4oClientServer.ARBITRARY_PORT).ext();
		_server.grantAccess(USERNAME, PASSWORD);
		_networkingClient = Db4oClientServer.openClient("localhost", _server.port(), USERNAME, PASSWORD).ext();
		this._embeddedClient = ((ObjectContainerSession) _server.openClient().ext());
	}

	public void tearDown() throws Exception {
		embeddedClient().close();
		networkingClient().close();
		_server.close();
	}

	protected ExtObjectContainer networkingClient() {
		return _networkingClient;
	}

	protected ObjectContainerSession embeddedClient() {
		return _embeddedClient;
	}
	
	protected ExtObjectContainer serverObjectContainer() {
		return _server.objectContainer().ext();
	}

}