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

import java.io.*;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.events.*;
import com.db4o.internal.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
public class ExceptionStackTraceTestCase extends TestWithTempFile implements db4ounit.extensions.fixtures.OptOutDefragSolo {

	public static class Item {		
	}
	
	public static void main(String[] args) {	
		new ConsoleTestRunner(ExceptionStackTraceTestCase.class).run();
	}
	
	private ObjectServer _server;
	private ObjectContainer _client;	
	
	public void testStackTracesContainsServerSideMethods() {
		if (!testIsSupportedOnCurrentPlatform()) {
			System.out.println(getClass().getName() + " has been disabled on JDK 1." + jdkVersion());
			return;
		}
		
		_client.store(new Item());
		
		try {
			_client.commit();
			Assert.fail("Commit should have thrown.");
		} catch (EventException ex) {
			Assert.isInstanceOf(EventException.class, ex);
			assertExceptionContainsServerStackTrace(ex);			
		}
	}

	/**
	 * @sharpen.remove true 
	 */
	private boolean testIsSupportedOnCurrentPlatform() {
		return jdkVersion() >= 4;
	}

	private void assertExceptionContainsServerStackTrace(EventException ex) {		
		final String stackTrace = stackTraceFor(ex);
		if (stackTrace.indexOf("MCommit.replyFromServer") < 0) {
			Assert.fail("Missing server stack trace.\r\n----------------------" + stackTrace + "\r\n-----------------------");
		}
	}

	private String stackTraceFor(EventException ex) {
		final StringWriter stackTrace = new StringWriter();
		ex.printStackTrace(new PrintWriter(stackTrace));
		
		return stackTrace.toString();
	}

	private void registerForCommitEventOnServer() {
		if (_server == null) {
			throw new IllegalStateException();
		}
		
		final EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(serverContainer());
		eventRegistry.committing().addListener(new EventListener4() {
													private boolean shouldThrow = true; 
													
													public void onEvent(Event4 e, EventArgs args) {
														if (shouldThrow) {
															shouldThrow = false;
															throw new IllegalStateException();
														}
													}
												});
		
		
	}

	private ObjectContainer serverContainer() {
		return _server.ext().objectContainer();
	}


	private void openClient() {
		_client = Db4oClientServer.openClient(newClientConfig(), "localhost", 0xdb40, "db4o", "db4o");
	}

	private void openServer() {
		_server = Db4oClientServer.openServer(Db4oClientServer.newServerConfiguration(), tempFile(), 0xdb40);
		_server.grantAccess("db4o", "db4o");
	}

	private ClientConfiguration newClientConfig() {
		return Db4oClientServer.newClientConfiguration();
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		if (!testIsSupportedOnCurrentPlatform()) return;
		
		openServer();
		registerForCommitEventOnServer();
		openClient();		
	}

	/**
	 * @sharpen.ignore
	 */
	private int jdkVersion() {
		return Platform4.jdk().ver();
	}
	
	@Override
	public void tearDown() throws Exception {
		
		if (_client != null) {
			_client.close();
		}
		
		if (_server != null) {
			_server.close();
		}
		
		super.tearDown();
	}
}
