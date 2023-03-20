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

import javax.management.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.cs.internal.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.db4ounit.optional.monitoring.*;
import com.db4o.ext.*;
import com.db4o.monitoring.*;

import db4ounit.extensions.OptOutNotSupportedJavaxManagement;
import db4ounit.extensions.fixtures.*;

@decaf.Ignore
public abstract class MonitoredSocket4TestCaseBase extends TestWithTempFile implements OptOutNotSupportedJavaxManagement, OptOutCustomContainerInstantiation {

	protected static class Item {
		public String _name;

		public Item(String name) {
			_name = name;
		}		
	}
	
	protected static final int EXERCISES_COUNT = 3;
	private static final String PASSWORD = "db4o";
	private static final String USER = "db4o";
	
	public void setUp() {
		_clock = new ClockMock();
		_server = (ObjectServerImpl) Db4oClientServer.openServer(serverConfiguration(), tempFile(), Db4oClientServer.ARBITRARY_PORT);
		_server.grantAccess(USER, PASSWORD);		
	}

	protected void configureClock(EnvironmentConfiguration environment) {
		environment.add(_clock);
	}
	
	protected abstract ServerConfiguration serverConfiguration();
	protected abstract ClientConfiguration clientConfiguration();

	public void tearDown() throws Exception {
		_server.close();
		super.tearDown();
	}
	
	protected void setupCountingSocketFactory(NetworkingConfiguration networkConfig) {
		CountingSocket4Factory socketFactory = new CountingSocket4Factory(networkConfig.socketFactory());
		networkConfig.socketFactory(socketFactory);
	}

	protected CountingSocket4Factory configuredSocketFactoryFor(ObjectContainer container) {
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(container.ext().configure());
		CountingSocket4Factory factory = (CountingSocket4Factory) networkConfig.socketFactory();
		return factory;
	}

	protected void withTwoClients(TwoClientsAction action) {
		ExtObjectContainer client1 = null;
		ExtObjectContainer client2 = null;
		
		try {	
			client1 = openNewSession();
			client2 = openNewSession();			
				
			action.apply(client1, client2);				
		} finally {
			if (client1 != null) client1.close();
			if (client2 != null) client2.close();			
		}
	}

	protected ExtObjectContainer openNewSession() {
		return (ExtObjectContainer) Db4oClientServer.openClient(clientConfiguration(), "localhost", _server.ext().port(), USER, PASSWORD);
	}

	protected interface TwoClientsAction {
		void apply(ObjectContainer client1, ObjectContainer client2);
	}
	
	protected void resetAllBeanCountersFor(ObjectContainer...  clients) {
		for(ObjectContainer container : clients) {
			resetBeanCountersFor(container);
		}
	}

	protected void resetBeanCountersFor(ObjectContainer container) {
		ObjectName objectName = Db4oMBeans.mBeanNameFor(com.db4o.cs.monitoring.NetworkingMBean.class, Db4oMBeans.mBeanIDForContainer(container));
		MBeanProxy bean = new MBeanProxy(objectName);
		
		bean.resetCounters();
	}

	protected void advanceClock(int time) {
		_clock.advance(time);
	}

	protected ExtObjectServer server() {
		return _server;
	}
	
	protected class BytesSentCounterHandler extends CounterHandlerBase {
		public double actualValue(ObjectContainer container) {
			return getAttribute(container, "BytesSentPerSecond") ;
		}

		public double expectedValue(CountingSocket4 countingSocket) {
			return countingSocket.bytesSent();
		}
	}
	
	protected class BytesReceivedCounterHandler extends CounterHandlerBase {
		public double actualValue(ObjectContainer container) {
			return getAttribute(container, "BytesReceivedPerSecond") ;
		}

		public double expectedValue(CountingSocket4 countingSocket) {
			return countingSocket.bytesReceived();
		}
	}
	
	protected class MessagesSentCounterHandler extends CounterHandlerBase {
		public double actualValue(ObjectContainer container) {
			return getAttribute(container, "MessagesSentPerSecond") ;
		}

		public double expectedValue(CountingSocket4 countingSocket) {
			return countingSocket.messagesSent();
		}
	}
	
	protected abstract class CounterHandlerBase implements CounterHandler {
		
		public double expectedValue(ObjectContainer container) {
			return observedCounter(container);
		}
		
		protected double getAttribute(ObjectContainer container, String attribute) {
			ObjectName objectName = Db4oMBeans.mBeanNameFor(com.db4o.cs.monitoring.NetworkingMBean.class, Db4oMBeans.mBeanIDForContainer(container));
			MBeanProxy bean = new MBeanProxy(objectName);
			return bean.<Double>getAttribute(attribute);		
		}
		
		private double observedCounter(ObjectContainer container) {
			CountingSocket4Factory factory = configuredSocketFactoryFor(container);
			
			double total = 0.0;
			for (CountingSocket4 socket :factory.countingSockets()) {
				double expectedValue = expectedValue(socket);
				total += expectedValue;
			}
			
			return total;
		}
	}
	
	interface CounterHandler {
		
		double expectedValue(CountingSocket4 socket);
		
		double expectedValue(ObjectContainer container);

		double actualValue(ObjectContainer container);

	}

	private ObjectServerImpl _server;
	
	private ClockMock _clock;
}
