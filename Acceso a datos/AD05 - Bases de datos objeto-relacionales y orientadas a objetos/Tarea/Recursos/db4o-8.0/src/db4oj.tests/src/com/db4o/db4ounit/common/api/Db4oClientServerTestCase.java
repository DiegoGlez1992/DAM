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
package com.db4o.db4ounit.common.api;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;

import db4ounit.*;

public class Db4oClientServerTestCase extends TestWithTempFile {
	
	public void testClientServerApi() {
		final ServerConfiguration config = Db4oClientServer.newServerConfiguration();
		
		final ObjectServer server = Db4oClientServer.openServer(config, tempFile(), 0xdb40);
		try {
			server.grantAccess("user", "password");
			
			final ClientConfiguration clientConfig = Db4oClientServer.newClientConfiguration();
			final ObjectContainer client1 = Db4oClientServer.openClient(clientConfig, "localhost", 0xdb40, "user", "password");
			try {
				
			} finally {
				Assert.isTrue(client1.close());
			}
		} finally {
			Assert.isTrue(server.close());
		}
	}
	
	public void testConfigurationHierarchy() {
		Assert.isInstanceOf(NetworkingConfigurationProvider.class, Db4oClientServer.newClientConfiguration());
		Assert.isInstanceOf(NetworkingConfigurationProvider.class, Db4oClientServer.newServerConfiguration());
	}
	
	
}
