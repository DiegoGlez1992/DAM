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
package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class PlainObjectTestCase extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new PlainObjectTestCase().runAll();
    }
    
    public static class Item{
        
        public String _name;
        
        public Object _plainObject;
        
        public Item(String name, Object plainObject) {
            _name = name;
            _plainObject = plainObject;
        }
        
    }
    
    protected void configure(Configuration config) throws Exception {
        config.objectClass(Item.class).cascadeOnDelete(true);
    }
    
    protected void store() throws Exception {
        Object plainObject = new Object();
        Item item = new Item("one", plainObject);
        store(item);
        Assert.isTrue(db().isStored(item._plainObject));
        store(new Item("two", plainObject));
    }
    
    public void testRetrieve(){
        Item itemOne = retrieveItem("one");
        Assert.isNotNull(itemOne._plainObject);
        Assert.isTrue(db().isStored(itemOne._plainObject));
        Item itemTwo = retrieveItem("two");
        Assert.areSame(itemOne._plainObject, itemTwo._plainObject);
    }
    
    public void testDelete(){
        Item itemOne = retrieveItem("one");
        db().delete(itemOne);
    }
    
    public void _testEvaluationQuery(){
        
        // The evaluation doesn't work in C/S mode
        // because TransportObjectContainer#storeInteral  
        // never gets a chance to intercept.
        
        Item itemOne = retrieveItem("one");
        final Object plainObject = itemOne._plainObject;
        Query q = newQuery(Item.class);
        q.constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
                Item item = (Item) candidate.getObject();
                candidate.include(item._plainObject == plainObject);
            }
        });
        ObjectSet objectSet = q.execute();
        Assert.areEqual(2, objectSet.size());
    }
    
    public void testIdentityQuery(){
        Item itemOne = retrieveItem("one");
        final Object plainObject = itemOne._plainObject;
        Query q = newQuery(Item.class);
        q.descend("_plainObject").constrain(plainObject).identity();
        ObjectSet objectSet = q.execute();
        Assert.areEqual(2, objectSet.size());
    }

    private Item retrieveItem(String name) {
        Query query = newQuery(Item.class);
        query.descend("_name").constrain(name);
        ObjectSet objectSet = query.execute();
        Assert.areEqual(1, objectSet.size());
        return (Item) objectSet.next();
    }
    
}
