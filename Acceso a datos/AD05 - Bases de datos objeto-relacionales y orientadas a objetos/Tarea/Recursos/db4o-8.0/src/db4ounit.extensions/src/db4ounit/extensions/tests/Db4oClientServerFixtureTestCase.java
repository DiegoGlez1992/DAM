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
package db4ounit.extensions.tests;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;

import org.easymock.*;

import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

@decaf.Remove
public class Db4oClientServerFixtureTestCase implements TestCase {
	
	public void testOpenWithCustomClientServerConfiguration() throws Exception {

		final String userName = "db4o";
		final String password = "db4o";
		final int port = 42;
		
		final IMocksControl mockery = createControl();
		mockery.checkOrder(false);
		
		final ClientServerFactory clientServerFactoryMock = mockery.createMock("factory", ClientServerFactory.class);
		final ExtObjectServer objectServerMock = EasyMock.createNiceMock("server", ExtObjectServer.class);
		final ExtClient clientMock = mockery.createMock("client", ExtClient.class);
		final CustomClientServerConfiguration testInstanceMock = mockery.createMock(CustomClientServerConfiguration.class);
		
		testInstanceMock.configureServer(isA(Configuration.class));
			expectLastCall().once();
		
		expect(clientServerFactoryMock.openServer(isA(ServerConfiguration.class), isA(String.class), eq(-1)))
			.andReturn(objectServerMock)
			.once();
		
		expect(objectServerMock.ext())
			.andReturn(objectServerMock)
			.anyTimes();
		
		expect(objectServerMock.port())
			.andReturn(42)
			.anyTimes();
		
		objectServerMock.grantAccess(userName, password);
			expectLastCall().once();
	
		testInstanceMock.configureClient(isA(Configuration.class));
			expectLastCall().once();
			
		expect(clientServerFactoryMock.openClient(isA(ClientConfiguration.class), eq("127.0.0.1"), eq(port), eq(userName), eq(password)))
			.andReturn(clientMock)
			.once();
		
		expect(clientMock.ext())
			.andReturn(clientMock)
			.anyTimes();
		
		mockery.replay();
		
		replay(objectServerMock);
		
		final Db4oNetworking fixture = new Db4oNetworking(clientServerFactoryMock, "C/S");
		fixture.open(testInstanceMock);
		
		mockery.verify();
	} 

}
