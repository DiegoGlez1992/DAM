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

import com.db4o.config.*;
import com.db4o.cs.internal.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class SetSemaphoreTestCase extends Db4oClientServerTestCase implements OptOutSolo {

    private static final String SEMAPHORE_NAME = "hi";

	public static void main(String[] args) {
		new SetSemaphoreTestCase().runAll();
    }
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.storage(new MemoryStorage());
	}

    public void testSemaphoreReentrancy() {
        ExtObjectContainer container = db();
		
        Assert.isTrue(container.setSemaphore(SEMAPHORE_NAME, 0));
		Assert.isTrue(container.setSemaphore(SEMAPHORE_NAME, 0));
		
		container.releaseSemaphore(SEMAPHORE_NAME);
    }
    
    public void testOwnedSemaphoreCannotBeTaken() {
        ExtObjectContainer client1 = openNewSession();
        
        try {
	        Assert.isTrue(db().setSemaphore(SEMAPHORE_NAME, 0));
	        Assert.isFalse(client1.setSemaphore(SEMAPHORE_NAME, 0));
        }
        finally {
        	client1.close();
        }
    }
    
    public void testPreviouslyOwnedSemaphoreCannotBeTaken() {
        ExtObjectContainer client1 = openNewSession();
        
        try {
	        Assert.isTrue(db().setSemaphore(SEMAPHORE_NAME, 0));
	        Assert.isFalse(client1.setSemaphore(SEMAPHORE_NAME, 0));
	        
	        db().releaseSemaphore(SEMAPHORE_NAME);
	        ensureMessageProcessed(db());
	        Assert.isTrue(client1.setSemaphore(SEMAPHORE_NAME, 0));
	        Assert.isFalse(db().setSemaphore(SEMAPHORE_NAME, 0));
        }
        finally {
        	client1.close();
        }
    }
    
    public void testClosingClientReleasesSemaphores() {
    	final ExtObjectContainer client1 = openNewSession();
        
	    Assert.isTrue(client1.setSemaphore(SEMAPHORE_NAME, 0));
	    Assert.isFalse(db().setSemaphore(SEMAPHORE_NAME, 0));
	        
	    if (isNetworking()) {
	    	closeConnectionInNetworkingCS(client1);	    
	    } else {
		    client1.close();
	    }	    
    	
		Assert.isTrue(db().setSemaphore(SEMAPHORE_NAME, 0));
    }

	private void closeConnectionInNetworkingCS(final ExtObjectContainer client) {
		final BooleanByRef eventWasRaised = new BooleanByRef();
		final Lock4 clientDisconnectedLock = new Lock4();
		ObjectServerEvents serverEvents = (ObjectServerEvents) clientServerFixture().server();
		serverEvents.clientDisconnected().addListener(new EventListener4<StringEventArgs>() {
			public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
				clientDisconnectedLock.run(new Closure4() { public Object run() {
					eventWasRaised.value = true;
				    clientDisconnectedLock.awake();
				    
				    return null;
				}});				
			}
		});
		
		clientDisconnectedLock.run(new Closure4() { 
			public Object run() {
			    client.close();
				clientDisconnectedLock.snooze(30000);	    	
				
				return null;
			}
		});
		
		Assert.isTrue(eventWasRaised.value, "ClientDisconnected event was not raised.");
	}
    
	public void testMultipleThreads() throws InterruptedException {

        final ExtObjectContainer[] clients = new ExtObjectContainer[5];

        clients[0] = db();
        for (int i = 1; i < clients.length; i++) {
            clients[i] = openNewSession();
        }
        
        Assert.isTrue(clients[1].setSemaphore(SEMAPHORE_NAME, 50));
        Thread[] threads = new Thread[clients.length];

        for (int i = 0; i < clients.length; i++) {
            threads[i] = startGetAndReleaseThread(clients[i]);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        
        ensureMessageProcessed(clients[0]);

        Assert.isTrue(clients[0].setSemaphore(SEMAPHORE_NAME, 0));
        clients[0].close();
        

        threads[2] = startGetAndReleaseThread(clients[2]);
        threads[1] = startGetAndReleaseThread(clients[1]);

        threads[1].join();
        threads[2].join();

        for (int i = 1; i < clients.length - 1; i++) {
            clients[i].close();
        }

        clients[4].setSemaphore(SEMAPHORE_NAME, 1000);
        clients[4].close();
    }

    private Thread startGetAndReleaseThread(ExtObjectContainer client) {
        Thread t = new Thread(new GetAndRelease(client), "SetSemaphoreTestCase.startGetAndReleaseThread");
        t.start();
        return t;
    }

	private static void ensureMessageProcessed(ExtObjectContainer client) {
		client.commit();
	}

    static class GetAndRelease implements Runnable {

        private ExtObjectContainer _client;

        public GetAndRelease(ExtObjectContainer client) {
            _client = client;
        }

        public void run() {
	        Assert.isTrue(_client.setSemaphore(SEMAPHORE_NAME, 50000));

        	ensureMessageProcessed(_client);
            _client.releaseSemaphore(SEMAPHORE_NAME);
        }
     }
}
