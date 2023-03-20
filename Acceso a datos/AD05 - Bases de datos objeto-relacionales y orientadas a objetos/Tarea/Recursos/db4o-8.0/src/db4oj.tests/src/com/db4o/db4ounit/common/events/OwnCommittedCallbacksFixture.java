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
package com.db4o.db4ounit.common.events;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;

public class OwnCommittedCallbacksFixture {

	public static interface ContainerFactory extends Labeled {
		ObjectContainer openClient();
		void open();
		void close();
	}

	public static abstract class CommitAction implements Labeled {
		public void commitItem(Object item, ObjectContainer clientA, ObjectContainer clientB) {
			ObjectContainer client = selectClient(clientA, clientB);
			client.store(item);
			client.commit();
		}
	
		public abstract boolean selectsFirstClient();		
		protected abstract ObjectContainer selectClient(ObjectContainer clientA, ObjectContainer clientB);
	}

	public static class NetworkingCSContainerFactory implements ContainerFactory {
		private static final String HOST = "localhost";
		private static final String USER = "db4o";
		private static final String PASS = "db4o";
		
		private ObjectServer _server;
		
		public void open() {
			ServerConfiguration config = Db4oClientServer.newServerConfiguration();
			config.file().storage(new MemoryStorage());
			_server = Db4oClientServer.openServer(config, "", Db4oClientServer.ARBITRARY_PORT);
			_server.grantAccess(USER, PASS);
		}
		
		public ObjectContainer openClient() {
			return Db4oClientServer.openClient(HOST, _server.ext().port(), USER, PASS);
		}
	
		public void close() {
			_server.close();
		}
	
		public String label() {
			return "Networking C/S";
		}
	}

	public static class EmbeddedCSContainerFactory implements ContainerFactory {
		private ObjectServer _server;
		
		public void open() {
			ServerConfiguration config = Db4oClientServer.newServerConfiguration();
			config.file().storage(new MemoryStorage());
			_server = Db4oClientServer.openServer(config, "", 0);
		}
		
		public ObjectContainer openClient() {
			return _server.openClient();
		}
	
		public void close() {
			_server.close();
		}
	
		public String label() {
			return "Embedded C/S";
		}
	}

	public static class EmbeddedSessionContainerFactory implements ContainerFactory {
		private EmbeddedObjectContainer _server;
		
		public void open() {
			EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
			config.file().storage(new MemoryStorage());
			_server = Db4oEmbedded.openFile(config, "");
		}
		
		public ObjectContainer openClient() {
			return _server.ext().openSession();
		}
	
		public void close() {
			_server.close();
		}
	
		public String label() {
			return "Embedded Session";
		}
	}

	public static class ClientACommitAction extends CommitAction {
		@Override
		protected ObjectContainer selectClient(ObjectContainer clientA, ObjectContainer clientB) {
			return clientA;
		}
		
		@Override
		public boolean selectsFirstClient() {
			return true;
		}
	
		public String label() {
			return "Client A";
		}
	}

	public static class ClientBCommitAction extends CommitAction {
		@Override
		protected ObjectContainer selectClient(ObjectContainer clientA, ObjectContainer clientB) {
			return clientB;
		}
	
		@Override
		public boolean selectsFirstClient() {
			return false;
		}
		
		public String label() {
			return "Client B";
		}
	}

	public static class OwnCommitCallbackFlaggedTestUnit implements TestCase {
		private static final long TIMEOUT = 1000;
	
		/**
		 * @sharpen.if !CF
		 */
		public void testCommittedCallbacks() throws InterruptedException {
			final Lock4 lockObject = new Lock4();
			final BooleanByRef ownEvent = new BooleanByRef(false);
			final BooleanByRef gotEvent = new BooleanByRef(false);
			final BooleanByRef shallListen = new BooleanByRef(false);
			ContainerFactory factory = FACTORY.value();
			final CommitAction action = ACTION.value();
			factory.open();
			final ObjectContainer clientA = factory.openClient();
			final ObjectContainer clientB = factory.openClient();
			EventRegistry registry = EventRegistryFactory.forObjectContainer(clientA);
			registry.committed().addListener(new EventListener4<CommitEventArgs>() {
				public void onEvent(Event4<CommitEventArgs> e, CommitEventArgs args) {
					if(!shallListen.value) {
						return;
					}
					Assert.isFalse(gotEvent.value);
					gotEvent.value = true;
					ownEvent.value = args.isOwnCommit();
					lockObject.run(new Closure4() {
						public Object run() {
							lockObject.awake();
							return null;
						}
					});
				}
			});

			lockObject.run(new Closure4() {
				public Object run() {
					shallListen.value = true;
					action.commitItem(new OwnCommitCallbackFlaggedNetworkingTestSuite.Item(42), clientA, clientB);
					lockObject.snooze(TIMEOUT);
					return null;
				}
			});

			shallListen.value = false;
			clientB.close();
			clientA.close();
			factory.close();
			Assert.isTrue(gotEvent.value);
			Assert.areEqual(action.selectsFirstClient(), ownEvent.value);
		}
	}

	public static final FixtureVariable<ContainerFactory> FACTORY = FixtureVariable.newInstance("mode");
	public static final FixtureVariable<CommitAction> ACTION = FixtureVariable.newInstance("client");

}
