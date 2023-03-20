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
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class CsDeleteReaddTestCase extends Db4oClientServerTestCase {
    
    public static class ItemParent {
        
    }
    
    public static class Item extends ItemParent{
        
        public String name;
        
        public Item(String name_){
            name = name_;
        }
    }
    
    
    @Override
    protected void configure(Configuration config) throws Exception {
    	config.generateUUIDs(ConfigScope.GLOBALLY);
    	config.generateCommitTimestamps(true);
    	config.objectClass(Item.class).objectField("name").indexed(true);
    }
    
    protected void store() throws Exception {
        store(new Item("one"));
    }
    
    public void testDeleteReadd(){
        ExtObjectContainer client1 = db();
        ExtObjectContainer client2 = openNewSession();
        
        Item item1 = (Item) retrieveOnlyInstance(client1, Item.class);
        Item item2 = (Item) retrieveOnlyInstance(client2, Item.class);
        
        long idBeforeDelete = client1.getID(item1);
        
        client1.delete(item1);
        
        client1.commit();
        
        client2.store(item2);
        client2.commit();
        client2.close();
        
        Item item3 = retrieveOnlyInstance(client1, Item.class);
        final long idAfterUpdate = client1.getID(item3);
        
        Assert.areEqual(idBeforeDelete, idAfterUpdate);
        
        new FieldIndexAssert(Item.class, "name").assertSingleEntry(fileSession(), idAfterUpdate);
    }
    
    public static void main(String[] arguments) {
        new CsDeleteReaddTestCase().runAll();
    }

}
