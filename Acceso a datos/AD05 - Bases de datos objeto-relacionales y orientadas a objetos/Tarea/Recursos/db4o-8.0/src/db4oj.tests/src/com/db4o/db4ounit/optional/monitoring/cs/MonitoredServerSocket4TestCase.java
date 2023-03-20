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
import com.db4o.cs.internal.config.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;

@decaf.Remove
public class MonitoredServerSocket4TestCase extends MonitoredSocket4TestCaseBase {

	private static final int CLIENT_1 = 0;
	private static final int CLIENT_2 = 1;

	@Override
	public void setUp() {
		super.setUp();
		_commitsReceived = installSystemTransactionCommitCounter();
	}
	
	@Override
	protected ClientConfiguration clientConfiguration() {
		ClientConfiguration clientConfig = Db4oClientServer.newClientConfiguration();
		clientConfig.networking().batchMessages(false);
		clientConfig.prefetchIDCount(1);
		clientConfig.timeoutClientSocket(Integer.MAX_VALUE);
		return clientConfig;
	}

	@Override
	protected ServerConfiguration serverConfiguration() {
		ServerConfiguration serverConfig = Db4oClientServer.newServerConfiguration();
		serverConfig.common().add(new com.db4o.cs.monitoring.NetworkingMonitoringSupport());
		serverConfig.timeoutServerSocket(Integer.MAX_VALUE);
		
		setupCountingSocketFactory(serverConfig.networking());		
			
		configureClock(serverConfig.common().environment());
		return serverConfig;
	}

	public void testBytesSentSingleClient() throws Exception {
		exerciseSingleClient(new BytesSentCounterHandler());
	}

	public void testBytesReceivedSingleClient() {
		exerciseSingleClient(new BytesReceivedCounterHandler());
	}
		
	public void testMessagesSentSingleClient() {
		exerciseSingleClient(new MessagesSentCounterHandler());
	}
	
	public void testBytesSentTwoClients() {
		assertTwoClients(new BytesSentCounterHandler());
	}
	
	public void testBytesReceivedTwoClients() {		
		assertTwoClients(new BytesReceivedCounterHandler());
	}
	
	public void testMessagesSentTwoClients() {		
		assertTwoClients(new MessagesSentCounterHandler());
	}
	
	private void assertCounter(ObjectContainer client, CounterHandler handler) {
		resetSocketCounters();
		resetAllBeanCountersFor(serverContainer());
				
		client.store(new Item("default client"));
		client.commit();
		advanceClock(1000);		
	 	
		double expected = expectedCount(handler, CLIENT_1);
		double actual = handler.actualValue(serverContainer());
		Assert.isGreater(0, (long) expected);
		
		Assert.areEqual(
				expected,				
				actual);
	}	

	private ObjectContainer serverContainer() {
		return server().objectContainer();
	}

	private void assertTwoClients(final CounterHandler handler) {
		resetClientConnectionCounter();
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			waitForClientConnectionCompleted(2);
			
			resetSocketCounters();
			resetBeanCountersFor(serverContainer());			

			client1.store(new Item("foo"));
			client2.store(new Item("bar"));
			client1.commit();
			client2.commit();
			
			advanceClock(1000);		
			
			double expected = expectedCount(handler, CLIENT_1, CLIENT_2);
			double actual = handler.actualValue(serverContainer());

			Assert.areEqual(
					expected,
					actual);			
		}});
	}

	private void resetClientConnectionCounter() {
		_commitsReceived.value = 0;
	}
	
	private double expectedCount(final CounterHandler handler, int... clientIndexes) {
		CountingSocket4Factory factory = serverCountingSocketFactory();
	
		double total = 0.0;
		for (int i : clientIndexes) {
			CountingSocket4 countingSocket = factory.connectedClients().get(i);
			total += handler.expectedValue(countingSocket);
		}
		
		return total;
	}		
	
	private void exerciseSingleClient(CounterHandler counterHandler) {
		resetClientConnectionCounter();
		ExtObjectContainer client = openNewSession();
		
		waitForClientConnectionCompleted(1);
		
		try {
			for (int i = 0; i < EXERCISES_COUNT; i++) {
				assertCounter(client, counterHandler);
			}
		}
		finally {
			client.close();
		}
	}
	
	private void resetSocketCounters() {
		CountingSocket4Factory factory = serverCountingSocketFactory();
		factory.resetCounters();		
	}
	
	protected void waitForClientConnectionCompleted(int expectedCommitCount) {
		synchronized (_commitsReceived) {
			while (expectedCommitCount != _commitsReceived.value) {
				try {
					_commitsReceived.wait(10);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private IntByRef installSystemTransactionCommitCounter() {
		final IntByRef commitsReceived = new IntByRef(0);

		ObjectServerImpl server = (ObjectServerImpl) server();
		
		server.clientConnected().addListener(
				new EventListener4<ClientConnectionEventArgs>() { public void onEvent(Event4<ClientConnectionEventArgs> e, ClientConnectionEventArgs args) {
					args.connection().messageReceived().addListener(new EventListener4<MessageEventArgs>() { public void onEvent(Event4<MessageEventArgs> e, MessageEventArgs args) {
						if (args.message().getClass() == MCommitSystemTransaction.class) {
								synchronized (commitsReceived) {
									commitsReceived.value++;
									commitsReceived.notifyAll();
								}
							}
						}
					});
				}
			});

		return commitsReceived;
	}

	protected CountingSocket4Factory serverCountingSocketFactory() {
		ObjectContainer serverContainer = serverContainer();		
		NetworkingConfiguration networkConfig = Db4oClientServerLegacyConfigurationBridge.asNetworkingConfiguration(serverContainer.ext().configure());
		
		return (CountingSocket4Factory) networkConfig.socketFactory();
	}
	
	private IntByRef _commitsReceived = null;
}
