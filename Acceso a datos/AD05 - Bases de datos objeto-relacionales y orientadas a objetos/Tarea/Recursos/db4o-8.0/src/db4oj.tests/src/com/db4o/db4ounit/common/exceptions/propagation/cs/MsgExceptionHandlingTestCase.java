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
package com.db4o.db4ounit.common.exceptions.propagation.cs;

import java.util.*;

import com.db4o.config.*;
import com.db4o.cs.internal.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.db4ounit.common.cs.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.io.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class MsgExceptionHandlingTestCase extends ClientServerTestCaseBase implements OptOutAllButNetworkingCS {

	private static final String EXCEPTION_MESSAGE = "exc";

	private static class CloseAwareBin extends BinDecorator {

		private final CloseAwareStorage _storage;
		
		public CloseAwareBin(CloseAwareStorage storage, Bin bin) {
			super(bin);
			_storage = storage;
		}

		@Override
		public void close() {
			super.close();
			_storage.notifyClosed(this);
		}
		
		@Override
		public void sync() {
			super.sync();
			_storage.notifySyncInvocation();
		}
		
		@Override
		public void sync(Runnable runnable) {
			super.sync(runnable);
			_storage.notifySyncInvocation();
		}
		
	}
	
	private static class CloseAwareStorage extends StorageDecorator {

		private final Map<Bin, Bin> _openBins = new HashMap<Bin, Bin>();
		private boolean _syncAllowed = true;
		private boolean _illegalSyncInvocation = false;
		
		public CloseAwareStorage(Storage storage) {
			super(storage);
		}

		@Override
		protected Bin decorate(BinConfiguration config, Bin bin) {
			CloseAwareBin decorated = new CloseAwareBin(this, bin);
			synchronized(_openBins) {
				_openBins.put(decorated, decorated);
			}
			return decorated;
		}
		
		public void notifyClosed(CloseAwareBin bin) {
			synchronized(_openBins) {
				_openBins.remove(bin);
			}
		}
		
		public int numOpenBins() {
			synchronized(_openBins) {
				return _openBins.size();
			}
		}
		
		public synchronized void syncAllowed(boolean isAllowed) {
			_syncAllowed = isAllowed;
		}
		
		public boolean illegalSyncInvocation() {
			return _illegalSyncInvocation;
		}
		
		public synchronized void notifySyncInvocation() {
			if(!_syncAllowed) {
				_illegalSyncInvocation = true;
			}
		}
	}
	
	private CloseAwareStorage _storage;
	
	private boolean _serverClosed;
	
	@Override
	protected void db4oSetupAfterStore() throws Exception {
		_serverClosed = false;
		ObjectServerEvents events = server();
		events.closed().addListener(new EventListener4<ServerClosedEventArgs>() {
			public void onEvent(Event4 e, com.db4o.cs.internal.ServerClosedEventArgs args) {
				_serverClosed = true;
			}
		});
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		_storage = new CloseAwareStorage(config.storage());
		config.storage(_storage);
	}
	
	public void testRecoverableExceptionWithResponse() {
		client().write(Msg.REQUEST_EXCEPTION_WITH_RESPONSE.getWriterForSingleObject(trans(), new Db4oRecoverableException(EXCEPTION_MESSAGE)));
		try {
			client().expectedResponse(Msg.OK);
			Assert.fail();
		}
		catch(Db4oRecoverableException exc) {
			assertExceptionMessage(exc);
		}
		Assert.isTrue(client().isAlive());
		assertServerContainerStateClosed(false);
	}

	public void testNonRecoverableExceptionWithResponse() {
		assertNonRecoverableExceptionForMessage(Msg.REQUEST_EXCEPTION_WITH_RESPONSE, new Db4oException(EXCEPTION_MESSAGE));
	}

	public void testRecoverableExceptionWithoutResponse() {
		client().write(Msg.REQUEST_EXCEPTION_WITHOUT_RESPONSE.getWriterForSingleObject(trans(), new Db4oRecoverableException(EXCEPTION_MESSAGE)));
		assertServerContainerStateClosed(false);
	}

	public void testNonRecoverableExceptionWithoutResponse() {
		assertNonRecoverableExceptionForMessage(Msg.REQUEST_EXCEPTION_WITHOUT_RESPONSE, new Db4oException(EXCEPTION_MESSAGE));
	}
	
	public void testVmErrorWithResponse(){
		assertNonRecoverableExceptionForMessage(Msg.REQUEST_EXCEPTION_WITH_RESPONSE, new OutOfMemoryError());
	}
	
	public void testVmErrorWithoutResponse(){
		assertNonRecoverableExceptionForMessage(Msg.REQUEST_EXCEPTION_WITHOUT_RESPONSE, new OutOfMemoryError());
	}

	private void assertNonRecoverableExceptionForMessage(
			MsgD message, Throwable throwable) {
		
		// Make sure the ClassMetadata of the exception is in the
		// ObjectContainer otherwise we get side effects from producing it.
		ReflectClass reflectClass = client().reflector().forClass(throwable.getClass());
		client().produceClassMetadata(reflectClass);

		_storage.syncAllowed(false);
		client().write(message.getWriterForSingleObject(trans(), throwable));
		assertDatabaseClosedException();
		assertServerContainerStateClosed(true);
	}

	private void assertDatabaseClosedException() {
		Assert.expect(DatabaseClosedException.class, new CodeBlock() {
			public void run() throws Throwable {
				client().expectedResponse(Msg.OK);
			}
		});
		Assert.isFalse(client().isAlive());
	}

	@decaf.RemoveFirst(decaf.Platform.JDK11)
	private void assertExceptionMessage(Db4oRecoverableException exc) {
		Assert.areEqual(EXCEPTION_MESSAGE, exc.getMessage());
	}


	private void assertServerContainerStateClosed(boolean expectedClosed) {
		if(expectedClosed) {
			final long timeout = 1000;
			final long startTime = System.currentTimeMillis();
			while(!_serverClosed && (System.currentTimeMillis() - startTime < timeout)) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		Assert.isFalse(_storage.illegalSyncInvocation());
		Assert.areEqual(expectedClosed, _serverClosed);
		Assert.areEqual(!expectedClosed, _storage.numOpenBins() > 0);
		if(!expectedClosed) {
			tryToOpenNewClient();
		}
		else {
// TODO: fails on .NET
//			Assert.expect(Db4oIOException.class, new CodeBlock() {
//				public void run() throws Throwable {
//					tryToOpenNewClient();
//				}
//			});
		}
	}

	private void tryToOpenNewClient() {
		ExtObjectContainer otherClient = openNewSession();
		otherClient.close();
	}

}
