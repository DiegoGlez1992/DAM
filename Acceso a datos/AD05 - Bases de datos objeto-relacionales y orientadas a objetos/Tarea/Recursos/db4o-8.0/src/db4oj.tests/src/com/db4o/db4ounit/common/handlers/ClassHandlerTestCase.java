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
package com.db4o.db4ounit.common.handlers;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class ClassHandlerTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new ClassHandlerTestCase().runSolo();
    }
    
    public static class Item{
        
        public String _name;
        
        public Item _child;
        
        public Item(String name, Item child){
            _name = name;
            _child = child;
        }
    }
    
    public void testStoreObject() throws Exception{
        Item expectedItem = new Item("parent", new Item("child", null));
        db().store(expectedItem);
        db().purge(expectedItem);
        Query q = db().query();
        q.constrain(Item.class);
        q.descend("_name").constrain("parent");
        ObjectSet objectSet = q.execute();
        Item readItem = (Item) objectSet.next();
        Assert.areNotSame(expectedItem, readItem);
        assertAreEqual(expectedItem, readItem);
    }
    
    private void assertAreEqual(Item expectedItem, Item readItem) {
        Assert.areEqual(expectedItem._name, readItem._name);
        Assert.areEqual(expectedItem._child._name, readItem._child._name);
    }

}
