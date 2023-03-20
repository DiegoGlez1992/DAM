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
package com.db4o.db4ounit.common.internal;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.references.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

import db4ounit.*;

public class EmbeddedClientObjectContainerTestCase extends Db4oTestWithTempFile {

    private static final String FIELD_NAME = "_name";

    private LocalObjectContainer _server;

    protected ExtObjectContainer _client1;

    protected ExtObjectContainer _client2;

    private static final String ORIGINAL_NAME = "original";
    
    private static final String CHANGED_NAME = "changed";

    public static class ItemHolder{
        
        public Item _item;
        
        public ItemHolder(Item item){
            _item = item;
        }
        
    }

    public static class Item{

        public String _name;
        
        public Item(){
            
        }

        public Item(String name) {
            _name = name;
        }
    }
    
    public void testReferenceSystemIsolation(){
    	Item item = new Item("one");
    	_client1.store(item);
    	_client1.commit();
    	Item client2Item = retrieveItemFromClient2();
    	Assert.areNotSame(item, client2Item);
    }
    
    public void testSetAndCommitIsolation() {
        Item item = new Item("one");
        _client1.store(item);
        assertItemCount(_client2, 0);
        _client1.commit();
        assertItemCount(_client2, 1);
    }
    
    public void testActivate(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        
        Item retrievedItem = (Item) _client2.getByID(id);
        Assert.isNull(retrievedItem._name);
        Assert.isFalse(_client2.isActive(retrievedItem));
        
        _client2.activate(retrievedItem, 1);
        Assert.areEqual(ORIGINAL_NAME, retrievedItem._name);
        Assert.isTrue(_client2.isActive(retrievedItem));
    }
    
    public void testBackup(){
        Assert.expect(NotSupportedException.class, new CodeBlock() {
            public void run() throws Throwable {
                _client1.backup("");
            }
        });
    }
    
    public void testBindIsolation(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        
        Item retrievedItem = retrieveItemFromClient2();
        
        Item boundItem = new Item(CHANGED_NAME);
        _client1.bind(boundItem, id);
        Assert.areSame(boundItem, _client1.getByID(id));
        Assert.areSame(retrievedItem, _client2.getByID(id));
    }
    
    public void testClose() {
        Transaction trans = null;
        synchronized(_server.lock()){
        	trans = _server.newUserTransaction();
        }
        ReferenceSystem referenceSystem = trans.referenceSystem();
        ObjectContainerSession client = new ObjectContainerSession(_server, trans);
        
        // FIXME: Need to unregister reference system also
        //        for crashed clients that never get closed. 
        client.close();
        
        // should have been removed on close.
        boolean wasNotRemovedYet = _server.referenceSystemRegistry().removeReferenceSystem(referenceSystem);
        
        Assert.isFalse(wasNotRemovedYet);
    }
    
    public void testCommitOnClose(){
        Item storedItem = storeItemToClient1AndCommit();
        storedItem._name = CHANGED_NAME;
        _client1.store(storedItem);
        _client1.close();
        Item retrievedItem = retrieveItemFromClient2();
        Assert.areEqual(CHANGED_NAME, retrievedItem._name);
    }
    
    public void testConfigure(){
        Assert.isNotNull(_client1.configure());
    }
    
    public void testDeactivate(){
        Item item = storeItemToClient1AndCommit();
        ItemHolder holder = new ItemHolder(item);
        _client1.store(holder);
        _client1.commit();
        _client1.deactivate(holder, 1);
        Assert.isNull(holder._item);
    }
    
    public void testDelete(){
        Item item = storeItemToClient1AndCommit();
        Assert.isTrue(_client1.isStored(item));
        _client1.delete(item);
        Assert.isFalse(_client1.isStored(item));
    }
    
    public void testDescendIsolation(){
        Item storedItem = storeItemToClient1AndCommit();
        storedItem._name = CHANGED_NAME;
        _client1.store(storedItem);
        
        int id = (int) _client1.getID(storedItem);
        Object retrievedItem = _client2.getByID(id);
        Assert.isNotNull(retrievedItem);
        
        Object descendValue = _client2.descend(retrievedItem, new String[]{FIELD_NAME});
        Assert.areEqual(ORIGINAL_NAME, descendValue);
        
        _client1.commit();
        
        descendValue = _client2.descend(retrievedItem, new String[]{FIELD_NAME});
        Assert.areEqual(CHANGED_NAME, descendValue);
    }
    
    public void testExt(){
        Assert.isInstanceOf(ExtObjectContainer.class, _client1.ext());
    }
    
    public void testGet(){
        Item storedItem = storeItemToClient1AndCommit();
        Object retrievedItem = _client1.queryByExample(new Item()).next();
        Assert.areSame(storedItem, retrievedItem);
    }
    
    public void testGetID(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        Assert.isGreater(1, id);
    }
    
    public void testGetByID(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        Assert.areSame(storedItem, _client1.getByID(id));
    }
    
    public void testGetObjectInfo(){
        Item storedItem = storeItemToClient1AndCommit();
        ObjectInfo objectInfo = _client1.getObjectInfo(storedItem);
        Assert.isNotNull(objectInfo);
    }
    
    public void testGetByUUID(){
        Item storedItem = storeItemToClient1AndCommit();
        ObjectInfo objectInfo = _client1.getObjectInfo(storedItem);
        
        Object retrievedItem = _client1.getByUUID(objectInfo.getUUID());
        Assert.areSame(storedItem, retrievedItem);
        
        retrievedItem = _client2.getByUUID(objectInfo.getUUID());
        Assert.areNotSame(storedItem, retrievedItem);
    }
    
    public void testIdenity(){
        Db4oDatabase identity1 = _client1.identity();
        Assert.isNotNull(identity1);
        Db4oDatabase identity2 = _client2.identity();
        Assert.isNotNull(identity2);
        
        // TODO: Db4oDatabase is shared between embedded clients.
        // This should work, since there is an automatic bind
        // replacement. Replication test cases will tell.
        Assert.areSame(identity1, identity2);
    }
    
    public void testIsCached(){
        Item storedItem = storeItemToClient1AndCommit();
        long id = _client1.getID(storedItem);
        
        Assert.isFalse(_client2.isCached(id));
        
        Item retrievedItem = (Item) _client2.getByID(id);
        Assert.isNotNull(retrievedItem);
        Assert.isTrue(_client2.isCached(id));
    }
    
    public void testIsClosed(){
        _client1.close();
        Assert.isTrue(_client1.isClosed());
    }
    
    public void testIsStored(){
        Item storedItem = storeItemToClient1AndCommit();
        Assert.isTrue(_client1.isStored(storedItem));
        Assert.isFalse(_client2.isStored(storedItem));
    }
    
    public void testKnownClasses(){
        ReflectClass[] knownClasses = _client1.knownClasses();
        ReflectClass itemClass = _client1.reflector().forClass(Item.class);
        ArrayAssert.containsByIdentity(knownClasses, new ReflectClass[]{itemClass});
    }
    
    public void testLock(){
        Assert.areSame(_server.lock(), _client1.lock());
    }
    
    public void testPeekPersisted(){
        Item storedItem = storeItemToClient1AndCommit();
        storedItem._name = CHANGED_NAME;
        _client1.store(storedItem);
        
        Item peekedItem = (Item) _client1.peekPersisted(storedItem, 2, true);
        Assert.isNotNull(peekedItem);
        Assert.areNotSame(peekedItem, storedItem);
        Assert.areEqual(ORIGINAL_NAME, peekedItem._name);
        
        peekedItem = (Item) _client1.peekPersisted(storedItem, 2, false);
        Assert.isNotNull(peekedItem);
        Assert.areNotSame(peekedItem, storedItem);
        Assert.areEqual(CHANGED_NAME, peekedItem._name);
        
        Item retrievedItem = retrieveItemFromClient2();
        peekedItem = (Item) _client2.peekPersisted(retrievedItem, 2, false);
        Assert.isNotNull(peekedItem);
        Assert.areNotSame(peekedItem, retrievedItem);
        Assert.areEqual(ORIGINAL_NAME, peekedItem._name);
    }
    
    public void testPurge(){
        Item storedItem = storeItemToClient1AndCommit();
        Assert.isTrue(_client1.isStored(storedItem));
        _client1.purge(storedItem);
        Assert.isFalse(_client1.isStored(storedItem));
    }
    
    public void testReflector(){
        Assert.isNotNull(_client1.reflector());
    }
    
    public void testRefresh(){
        Item storedItem = storeItemToClient1AndCommit();
        storedItem._name = CHANGED_NAME;
        _client1.refresh(storedItem, 2);
        Assert.areEqual(ORIGINAL_NAME, storedItem._name);
    }
    
    public void testRollback(){
        Item storedItem = storeItemToClient1AndCommit();
        storedItem._name = CHANGED_NAME;
        _client1.store(storedItem);
        _client1.rollback();
        _client1.commit();
        
        Item retrievedItem = retrieveItemFromClient2();
        Assert.areEqual(ORIGINAL_NAME, retrievedItem._name);
    }
    
    public void testSetSemaphore(){
        String semaphoreName = "sem";
        Assert.isTrue(_client1.setSemaphore(semaphoreName, 0));
        Assert.isFalse(_client2.setSemaphore(semaphoreName, 0));
        _client1.releaseSemaphore(semaphoreName);
        Assert.isTrue(_client2.setSemaphore(semaphoreName, 0));
        _client2.close();
        Assert.isTrue(_client1.setSemaphore(semaphoreName, 0));
    }
    
    public void testSetWithDepth(){
        Item item = storeItemToClient1AndCommit();
        ItemHolder holder = new ItemHolder(item);
        _client1.store(holder);
        _client1.commit();
        item._name = CHANGED_NAME;
        _client1.store(holder, 3);
        _client1.refresh(holder, 3);
        Assert.areEqual(CHANGED_NAME, item._name);
    }
    
    public void testStoredFieldIsolation(){
        Item storedItem = storeItemToClient1AndCommit();
        storedItem._name = CHANGED_NAME;
        _client1.store(storedItem);
        
        Item retrievedItem = retrieveItemFromClient2();
        
        StoredClass storedClass = _client2.storedClass(Item.class);
        StoredField storedField = storedClass.storedField(FIELD_NAME, null);
        Object retrievedName = storedField.get(retrievedItem);
        Assert.areEqual(ORIGINAL_NAME, retrievedName);
        
        _client1.commit();
        
        retrievedName = storedField.get(retrievedItem);
        Assert.areEqual(CHANGED_NAME, retrievedName);
    }
    
    public void testStoredClasses(){
        storeItemToClient1AndCommit();
        StoredClass[] storedClasses = _client1.storedClasses();
        StoredClass storedClass = _client1.storedClass(Item.class);
        ArrayAssert.containsByEquality(storedClasses, new Object[]{storedClass});
    }
    
    public void testSystemInfo(){
        SystemInfo systemInfo = _client1.systemInfo();
        Assert.isNotNull(systemInfo);
        Assert.isGreater(1, systemInfo.totalSize());
    }
    
    public void testVersion(){
        storeItemToClient1AndCommit();
        Assert.isGreater(1, _client1.version());
    }

    private void assertItemCount(ExtObjectContainer client, int count) {
        Query query = client.query();
        query.constrain(Item.class);
        ObjectSet result = query.execute();
        Assert.areEqual(count, result.size());
    }
    
    protected Item storeItemToClient1AndCommit() {
        Item storedItem = new Item(ORIGINAL_NAME);
        _client1.store(storedItem);
        _client1.commit();
        return storedItem;
    }

    private Item retrieveItemFromClient2() {
        Query query = _client2.query();
        query.constrain(Item.class);
        ObjectSet objectSet = query.execute();
        Item retrievedItem = (Item) objectSet.next();
        return retrievedItem;
    }

	public void setUp() throws Exception {
    	EmbeddedConfiguration config = newConfiguration();
        config.common().objectClass(Item.class).generateUUIDs(true);
        
        _server = (LocalObjectContainer) Db4oEmbedded.openFile(config, tempFile());
        _client1 = _server.openSession().ext();
        _client2 = _server.openSession().ext();
    }

    public void tearDown() throws Exception {
        _client1.close();
        _client2.close();
        _server.close();
        
        super.tearDown();
    }
    
}
