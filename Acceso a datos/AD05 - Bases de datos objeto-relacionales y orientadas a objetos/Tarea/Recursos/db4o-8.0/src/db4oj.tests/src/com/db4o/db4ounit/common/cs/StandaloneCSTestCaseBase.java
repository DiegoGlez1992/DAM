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
import com.db4o.config.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.cs.internal.config.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;

// TODO fix db4ounit call logic - this should actually be run in C/S mode
public abstract class StandaloneCSTestCaseBase implements TestCase {

	private int _port;

	public static final class Item {
	}
	
	public void test() throws Throwable {
		String fileName = databaseFile();
		File4.delete(fileName);
		final ObjectServer server = Db4oClientServer.openServer(createServerConfiguration(), fileName, -1);
		_port = server.ext().port();
		try {
			server.grantAccess("db4o", "db4o");
			
			runTest();
			
		} finally {
			server.close();
			File4.delete(fileName);
		}
	}

	private ServerConfiguration createServerConfiguration() {
		return Db4oClientServerLegacyConfigurationBridge.asServerConfiguration(createConfiguration());
	}

	private Configuration createConfiguration() {
		final Configuration config = Db4o.newConfiguration();
		configure(config);
		return config;
	}

	protected void withClient(ContainerBlock block) throws Throwable {
		ContainerServices.withContainer(openClient(), block);
	}

	protected ClientObjectContainer openClient() {
		return (ClientObjectContainer)Db4oClientServer.openClient(createClientConfiguration(), "localhost", _port, "db4o", "db4o");
	}

	private ClientConfiguration createClientConfiguration() {
		return Db4oClientServerLegacyConfigurationBridge.asClientConfiguration(createConfiguration());
	}

	protected int port() {
		return _port;
	}
	
	protected abstract void runTest() throws Throwable;

	protected abstract void configure(Configuration config);

	private String databaseFile() {
		return Path4.combine(Path4.getTempPath(), "cc.db4o");
	}

}