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

import java.util.*;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.internal.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;

public class ObjectServerTestCase extends TestWithTempFile {
	
    private ExtObjectServer server;    
    private String fileName;
   
    public void testClientCount(){
        assertClientCount(0);
        ObjectContainer client1 = openClient();
        try {
	        assertClientCount(1);
	        ObjectContainer client2 = openClient();
	        try {
	        	assertClientCount(2);
	        } finally {
	        	client2.close();
	        }
        } finally {
        	client1.close();
        }
        
        // closing is asynchronous, relying on completion is hard
        // That's why there is no test here. 
        // ClientProcessesTestCase tests closing.
    }
    
    public void testClientDisconnectedEvent() {
		final ClientObjectContainer client = (ClientObjectContainer) openClient();
		final String clientName = client.userName();
    	
    	final BooleanByRef eventRaised = new BooleanByRef();
    	final ObjectServerEvents events = (ObjectServerEvents)server;
    	
    	final Lock4 lock = new Lock4();
		
    	events.clientDisconnected().addListener(new EventListener4<StringEventArgs>() { public void onEvent(Event4 e, StringEventArgs args) {
			Assert.areEqual(clientName, args.message());					
			eventRaised.value = true;					
			lock.awake();
         }});
    	
		
    	lock.run(new Closure4() { public Object run() {
			client.close();
			
			long startTime = System.currentTimeMillis();
			long currentTime = startTime;
			int timeOut = 1000;
				
			long timePassed = currentTime - startTime;
			while (timePassed < timeOut && !eventRaised.value) {
				lock.snooze(timeOut - timePassed);
				
				currentTime = System.currentTimeMillis();
				timePassed = currentTime - startTime;
			}			
			Assert.isTrue(eventRaised.value);
			
			return null;
		}});
    }
    
    public void testClientConnectedEvent() {
    	
    	final ArrayList<ClientConnection> connections = new ArrayList<ClientConnection>();
    	
    	final ObjectServerEvents events = (ObjectServerEvents)server;
		events.clientConnected().addListener(new EventListener4<ClientConnectionEventArgs>() {
			public void onEvent(Event4 e, ClientConnectionEventArgs args) {
				connections.add(args.connection());
            }
		});
		
		final ObjectContainer client = openClient();
		try {
			Assert.areEqual(1, connections.size());
			Iterator4Assert.areEqual(serverMessageDispatchers(), Iterators.iterator(connections));
		} finally {
			client.close();
		}
    }

    public void testServerClosedEvent() {
    	final BooleanByRef receivedEvent = new BooleanByRef(false);
    	final ObjectServerEvents events = (ObjectServerEvents)server;
		events.closed().addListener(new EventListener4<ServerClosedEventArgs>() {
			public void onEvent(Event4 e, ServerClosedEventArgs args) {
				receivedEvent.value = true;
            }
		});
		server.close();
		Assert.isTrue(receivedEvent.value);
    }
    
	private Iterator4 serverMessageDispatchers() {
	    return ((ObjectServerImpl)server).iterateDispatchers();
    }
    
    public void setUp() throws Exception {
        fileName = tempFile();
        server = Db4oClientServer.openServer(fileName, -1).ext();
        server.grantAccess(credentials(), credentials());
    }

    @Override
    public void tearDown() throws Exception {
        server.close();
        super.tearDown();
    }

	private ObjectContainer openClient() {
	    return Db4oClientServer.openClient("localhost", port(), credentials(), credentials());
    }

    private void assertClientCount(int count) {
        Assert.areEqual(count, server.clientCount());
    }
    
    private int port(){
        return server.port();
    }
    
    private String credentials(){
        return "DB4O";
    }
}
