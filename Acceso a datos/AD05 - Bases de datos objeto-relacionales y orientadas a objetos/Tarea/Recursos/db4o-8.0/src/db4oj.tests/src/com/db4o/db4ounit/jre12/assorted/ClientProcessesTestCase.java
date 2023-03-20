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
package com.db4o.db4ounit.jre12.assorted;

import com.db4o.*;
import com.db4o.db4ounit.util.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ClientProcessesTestCase extends AbstractDb4oTestCase implements OptOutAllButNetworkingCS, OptOutNoInheritedClassPath {

    public static final int ITEM_COUNT = 10;
    
    public static final String CLIENT_STARTED_OK = "[STARTED]";
    
    public static final String CLIENT_COMPLETED_OK = "[COMPLETED]";

    public static void main(String[] args) {
        new ClientProcessesTestCase().runNetworking();
    }
    
    public void _testMassiveClientConnect() throws InterruptedException{

        final int CLIENT_COUNT = 20;  // more than 200 clients will need more than 3 GB of memory
        
        final StringBuffer results = new StringBuffer();
        
        ThreadServices.spawnAndJoin("ClientProcessesTestCase.testMassiveClientConnect", CLIENT_COUNT, new CodeBlock() {
            public void run() throws Throwable {
                String result = JavaServices.java(clientRunnerCommand());
                results.append(result);
                Assert.isTrue(result.indexOf(CLIENT_COMPLETED_OK) >= 0);
            }
        });
        System.out.println(results);
        asserItemCount(CLIENT_COUNT * ITEM_COUNT);
    }

    public void _testKillingClients() throws InterruptedException{

        final int CLIENT_COUNT = 3;  
        
        final StringBuffer results = new StringBuffer();
        
        ThreadServices.spawnAndJoin("ClientProcessesTestCase.testKillingClients", CLIENT_COUNT, new CodeBlock() {
            public void run() throws Throwable {
                results.append(JavaServices.startAndKillJavaProcess(clientRunnerCommand(), CLIENT_STARTED_OK, 10000));
            }
        });
        
        Assert.areEqual(1, connectedClients());
        System.out.println(results);
    }
    
    private int connectedClients() {
        Db4oClientServerFixture csFixture = (Db4oClientServerFixture) fixture();
        return (csFixture.server()).ext().clientCount();
    }

    private void asserItemCount(final int expectedCount) {
        Query query = db().query();
        query.constrain(Item.class);
        int itemCount = query.execute().size();
        Assert.areEqual(expectedCount, itemCount);
    }

    String clientRunnerCommand() {
        return ClientRunner.class.getName() + " " + ((Db4oClientServerFixture) fixture()).serverPort();
    }
    
    public static class ClientRunner {
        
        private final int _port;
        
        private ClientRunner(int port){
            _port = port;
        }
        
        public static void main(String[] arguments) {
            if(arguments == null || arguments.length == 0){
                return;
            }
            int port = new Integer(arguments[0]).intValue();
            new ClientRunner(port).start();
        }

        private void start() {
            ObjectContainer oc = com.db4o.cs.Db4oClientServer.openClient(Db4oNetworking.HOST, _port, Db4oNetworking.USERNAME, Db4oNetworking.PASSWORD);
            oc.store(new Item(0));
            oc.commit();
            print("[0]");
            print(CLIENT_STARTED_OK);
            for (int i = 1; i < ITEM_COUNT; i++) {
                oc.store(new Item(i));
                oc.commit();
                print("[" + i + "]");
            }
            oc.close();
            print(CLIENT_COMPLETED_OK);
        }
        
        private void print(String str){
            System.out.println(str);
        }
        
    }
    
    public static class Item{
        
        public int _number;
        
        public Item(int number){
            _number = number;
        }
        
    }
    

}

