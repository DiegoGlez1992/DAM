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

import db4ounit.*;
import db4ounit.extensions.*;


public class ObjectConstructorTestCase extends AbstractDb4oTestCase {
    
    
    public static class Item {
        
        final String _name;
        
        public Item(String name){
            _name = name;
        }
    }
    
    public static class ItemConstructor implements ObjectConstructor{

        public Object onInstantiate(ObjectContainer container, Object storedObject) {
            return new Item((String)storedObject);
        }

        public void onActivate(ObjectContainer container, Object applicationObject,
            Object storedObject) {
            
        }

        public Object onStore(ObjectContainer container, Object applicationObject) {
            return ((Item)applicationObject)._name;
        }

        public Class storedClass() {
            return String.class;
        }
    }
    
    protected void configure(Configuration config) throws Exception {
        config.objectClass(Item.class).translate(new ItemConstructor());
    }
    
    protected void store(){
        store(new Item("one"));
    }
    
    public void test(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        Assert.areEqual("one", item._name);
    }
    

}
