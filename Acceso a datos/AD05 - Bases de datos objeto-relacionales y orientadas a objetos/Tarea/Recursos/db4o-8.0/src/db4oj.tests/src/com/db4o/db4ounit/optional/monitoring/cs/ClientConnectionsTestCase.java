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
package com.db4o.db4ounit.optional.monitoring.cs;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.db4ounit.optional.monitoring.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.monitoring.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

@decaf.Ignore
public class ClientConnectionsTestCase extends TestWithTempFile implements OptOutAllButNetworkingCS {

	private static final String USER = "db4o";
	private static final String PASSWORD = "db4o";

	private BooleanByRef _closeEventRaised = new BooleanByRef();	
	private EventListener4<StringEventArgs> _listener;
	private ObjectServerImpl _server;

	public void testConnectedClients() {
		for(int i=0; i < 5; i++) {
			Assert.areEqual(0, connectedClientCount(), "No client yet.");
			ExtObjectContainer client1 = openNewSession();
			Assert.areEqual(1, connectedClientCount(), "client1:" + i);
			ExtObjectContainer client2 = openNewSession();
			Assert.areEqual(2, connectedClientCount(), "client1 and client2: " + i);
			ensureClose(client1);
			Assert.areEqual(1, connectedClientCount(), "client2: " + i);
			ensureClose(client2);
			Assert.areEqual(0, connectedClientCount(), "" + i);
		}		
	}

	private void ensureClose(ExtObjectContainer client) {
		synchronized (_closeEventRaised) {
			_closeEventRaised.value = false;
			client.close();
			while (!_closeEventRaised.value) {
				try {
					_closeEventRaised.wait();
				} catch (InterruptedException ex) {
				}
			}
		}
	}

	private ExtObjectContainer openNewSession() {
		return (ExtObjectContainer) Db4oClientServer.openClient("localhost", _server.ext().port(), USER, PASSWORD);
	}

	private long connectedClientCount() {
		MBeanProxy bean = new MBeanProxy(Db4oMBeans.mBeanNameFor(com.db4o.cs.monitoring.ClientConnectionsMBean.class, Db4oMBeans.mBeanIDForContainer(_server.objectContainer())));
		return bean.<Integer>getAttribute("ConnectedClientCount").longValue();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		_listener = new EventListener4<StringEventArgs>() { public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
			synchronized (_closeEventRaised) {
				_closeEventRaised.value = true;
				_closeEventRaised.notifyAll();
			}
		}};		
		ServerConfiguration serverConfiguration = Db4oClientServer.newServerConfiguration();
		// We depend on the order of client connection/disconnection event firing.
		// We want the bean to be notified before the _listener in the test.
		serverConfiguration.addConfigurationItem(new ConnectionCloseEventSupport(_listener));
		serverConfiguration.addConfigurationItem(new com.db4o.cs.monitoring.ClientConnectionsMonitoringSupport());
		_server = (ObjectServerImpl) Db4oClientServer.openServer(serverConfiguration, tempFile(), Db4oClientServer.ARBITRARY_PORT);
		_server.grantAccess(USER, PASSWORD);
	}

	public void tearDown() throws Exception {
		_server.clientDisconnected().removeListener(_listener);
		_server.close();
		super.tearDown();
	}
	
	private static class ConnectionCloseEventSupport implements ServerConfigurationItem {
		private EventListener4<StringEventArgs> _listener;
		
		public ConnectionCloseEventSupport(EventListener4<StringEventArgs> listener) {
			_listener = listener;
		}
		
		public void prepare(ServerConfiguration configuration) {
		}

		public void apply(ObjectServer server) {
			((ObjectServerEvents)server).clientDisconnected().addListener(_listener);
		}
	}

	public static void main(String[] args) {
		for(int i=0;i<100;i++) {
			new ConsoleTestRunner(ClientConnectionsTestCase.class).run();
		}
	}
}
