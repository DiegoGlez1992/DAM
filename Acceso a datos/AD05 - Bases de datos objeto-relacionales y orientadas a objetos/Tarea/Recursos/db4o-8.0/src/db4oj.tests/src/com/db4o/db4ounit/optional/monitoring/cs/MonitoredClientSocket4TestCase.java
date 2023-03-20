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
import com.db4o.ext.*;

import db4ounit.*;

@decaf.Ignore
public class MonitoredClientSocket4TestCase extends MonitoredSocket4TestCaseBase {
	
	@Override
	protected ServerConfiguration serverConfiguration() {
		return Db4oClientServer.newServerConfiguration();
	}

	@Override
	protected ClientConfiguration clientConfiguration() {
		ClientConfiguration clientConfig = Db4oClientServer.newClientConfiguration();
		clientConfig.common().add(new com.db4o.cs.monitoring.NetworkingMonitoringSupport());
		
		clientConfig.networking().batchMessages(false);
		clientConfig.prefetchIDCount(1);

		setupCountingSocketFactory(clientConfig.networking());
		configureClock(clientConfig.common().environment());
		
		return clientConfig;
	}
	
	public void testBytesReceived() {
		exerciseSingleClient(new BytesReceivedCounterHandler());
	}
	
	public void testBytesSent() {
		exerciseSingleClient(new BytesSentCounterHandler());
	}

	public void testMessagesSent() {
		exerciseSingleClient(new MessagesSentCounterHandler());
	}

	public void testBytesSentTwoClients() {
		exerciseTwoClients(new BytesSentCounterHandler());
	}

	public void testMessagesSentTwoClients() {
		exerciseTwoClients(new MessagesSentCounterHandler());
	}
	
	public void testBytesReceivedTwoClients() {
		exerciseTwoClients(new BytesReceivedCounterHandler());
	}

	public void testBytesSentTwoClientsInterleaved() {
		exerciseTwoClientsInterleaved(new BytesSentCounterHandler());
	}

	public void testBytesReceivedTwoClientsInterleaved() {
		exerciseTwoClientsInterleaved(new BytesReceivedCounterHandler());
	}

	private void assertTwoClients(final CounterHandler counterHandler) {
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			assertCounter(client1, new Item("bar"), counterHandler);
			assertCounter(client2, new Item("foobar"), counterHandler);
		}});
	}
	
	private void assertTwoClientsInterleaved(final CounterHandler counterHandler) {
		withTwoClients(new TwoClientsAction() { public void apply(ObjectContainer client1, ObjectContainer client2) {
			double clientCount1 = storeAndReturnObservedCounters(client1, new Item("bar"), counterHandler);			
			double clientCount2 = storeAndReturnObservedCounters(client2, new Item("foobar"), counterHandler);
			advanceClock(1000);
			
			Assert.isGreater(0, (long) clientCount1);
			Assert.isGreater(0, (long) clientCount2);
			Assert.areEqual(clientCount1, counterHandler.actualValue(client1), "Client 1");
			Assert.areEqual(clientCount2, counterHandler.actualValue(client2), "Client 2");					
		}});
	}
	
	private void assertCounter(ObjectContainer client, Item item, CounterHandler bytesSentHandler) {
		double expectedCount = storeAndReturnObservedCounters(client, item, bytesSentHandler);
		advanceClock(1000);
		Assert.isGreater(0, (long) expectedCount);
		Assert.areEqual(expectedCount, bytesSentHandler.actualValue(client));
	}
	
	private double storeAndReturnObservedCounters(ObjectContainer client, Item item, CounterHandler handler) {		
		resetAllBeanCountersFor(client); 
		resetCountingSocket(client);
		
		client.store(item);
		return handler.expectedValue(client);
	}
	
	private void resetCountingSocket(ObjectContainer container) {
		CountingSocket4Factory countingSocketFactory = configuredSocketFactoryFor(container);

		for(CountingSocket4 socket : countingSocketFactory.countingSockets()) {
			socket.resetCount();
		}
	}
	
	private void exerciseSingleClient(CounterHandler handler) {
		ExtObjectContainer client = openNewSession();
		try {
			for(int i = 0; i < EXERCISES_COUNT; i++) {
				assertCounter(client, new Item("foo"), handler);
			}
		}
		finally {
			client.close();
		}
	}
	
	private void exerciseTwoClients(CounterHandler handler) {
		for(int i=0; i < EXERCISES_COUNT; i++){
			assertTwoClients(handler);
		}
	}	
	
	private void exerciseTwoClientsInterleaved(CounterHandler handler) {
		for (int i=0; i < EXERCISES_COUNT; i++){
			assertTwoClientsInterleaved(handler);
		}
	}
}