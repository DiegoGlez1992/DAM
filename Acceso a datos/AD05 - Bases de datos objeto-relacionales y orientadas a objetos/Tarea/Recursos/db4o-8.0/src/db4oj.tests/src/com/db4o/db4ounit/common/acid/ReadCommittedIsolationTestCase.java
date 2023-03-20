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
package com.db4o.db4ounit.common.acid;

import com.db4o.*;
import com.db4o.cs.internal.*;
import com.db4o.events.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ReadCommittedIsolationTestCase extends AbstractDb4oTestCase implements OptOutSolo{
    
    // We introduce this variable to be able to wait for completion.
    // For a real usecase it is not necessary.
    private final Object _updatesMonitor = new Object();

    private static final String ORIGINAL = "original";
    
    private static final String MODIFIED = "modified";
    
    private ExtObjectContainer _client2;
    
    public static void main(String[] arguments) {
        new ReadCommittedIsolationTestCase().runAll();
    }
    
    public static class Item {

        public String name;

        public Item(String name_) {
            name = name_;
        }
        
        public String toString() {
            return "Item: " + name;
        }

    }
    
    public void testRefresh(){
        Item item2 = retrieveOnlyInstance(client2());
        Assert.areEqual(ORIGINAL, item2.name);
        Item item1 = retrieveOnlyInstance(client1());
        Assert.areEqual(ORIGINAL, item1.name);
        item1.name = MODIFIED;
        client1().store(item1);
        client1().commit();
        Assert.areEqual(ORIGINAL, item2.name);
        client2().refresh(item2, 2);
        Assert.areEqual(MODIFIED, item2.name);
    }
    
    public void testPushedUpdates() throws InterruptedException{
        
        registerPushedUpdates(client2());
        
        Item item2 = retrieveOnlyInstance(client2());
        Assert.areEqual(ORIGINAL, item2.name);
        
        
        Item item1 = retrieveOnlyInstance(client1());
        Assert.areNotSame(item2, item1);
        
        Assert.areEqual(ORIGINAL, item1.name);
        item1.name = MODIFIED;
        client1().store(item1);
        
        synchronized (_updatesMonitor) {
            client1().commit();
            if(isNetworkingCS()){
                _updatesMonitor.wait(1000);
            }
        }
        Assert.areEqual(MODIFIED, item2.name);
    }
    
    protected void db4oSetupAfterStore() throws Exception {
    	_client2 = openNewSession();
    }
    
    protected void db4oTearDownBeforeClean() throws Exception {
        _client2.close();
    }
    
    protected void store() throws Exception {
        store(new Item(ORIGINAL));
    }
    
    private ExtObjectContainer client1(){
        return db();
    }
    
    private ExtObjectContainer client2(){
        return _client2;
    }

    private Item retrieveOnlyInstance(ExtObjectContainer container) {
        Query q = container.query();
        q.constrain(Item.class);
        ObjectSet objectSet = q.execute();
        Assert.areEqual(1, objectSet.size());
        return (Item) objectSet.next();
    }

    private boolean isNetworkingCS() {
        return client2() instanceof ClientObjectContainer;
    }

    private void registerPushedUpdates(final ExtObjectContainer client) {
        EventRegistry eventRegistry = EventRegistryFactory.forObjectContainer(client);
        eventRegistry.committed().addListener(new EventListener4() {
           public void onEvent(Event4 e, EventArgs args) {
               synchronized(_updatesMonitor){
                   Transaction trans = ((InternalObjectContainer)client).transaction();
                   ObjectInfoCollection updated = ((CommitEventArgs)args).updated();
                   Iterator4 infos = updated.iterator();
                   while(infos.moveNext()){
                      ObjectInfo info = (ObjectInfo) infos.current();
                      Object obj = trans.objectForIdFromCache((int)info.getInternalID());
                      if(obj == null){
                          continue;
                      }
                      // DEPTH may need to be 2 for member collections
                      // to be updated also.
                      client.refresh(obj, 1);
                   }
                   if(isNetworkingCS()){
                       _updatesMonitor.notifyAll();
                   }
               }
               
           }
        });
    }

}
