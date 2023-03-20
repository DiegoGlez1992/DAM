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

import db4ounit.extensions.*;


public class CsCascadedDeleteReaddChildReferenceTestCase extends Db4oClientServerTestCase{
	
    public static class ItemParent {
    	
    	public Item child;
        
    }
    
    public static class Item {
        
        public String name;
        
        public Item(String name_){
            name = name_;
        }
    }
    
    
    @Override
    protected void configure(Configuration config) throws Exception {
    	config.objectClass(Item.class).objectField("name").indexed(true);
    	config.objectClass(ItemParent.class).objectField("child").indexed(true);
    }
    
    protected void store() throws Exception {
    	ItemParent parent = new ItemParent();
        Item child = new Item("child");
        parent.child = child;
		store(parent);
    }
    
    public void testDeleteReadd(){
        ExtObjectContainer client1 = db();
        ExtObjectContainer client2 = openNewSession();
        
        ItemParent parent1 = retrieveOnlyInstance(client1, ItemParent.class);
        ItemParent parent2 = retrieveOnlyInstance(client2, ItemParent.class);
        
        client1.delete(parent1);
        
        client1.commit();
        
        client2.ext().store(parent2, Integer.MAX_VALUE);
        client2.commit();
        client2.close();
        
        assertInstanceCountAndFieldIndexes(client1);
    }

    public void testRepeatedStore(){
        ExtObjectContainer client1 = db();
        ExtObjectContainer client2 = openNewSession();
        
        ItemParent parent1 = retrieveOnlyInstance(client1, ItemParent.class);
        ItemParent parent2 = retrieveOnlyInstance(client2, ItemParent.class);
        
        client1.ext().store(parent1, Integer.MAX_VALUE);
        
        client1.commit();
        
        client2.ext().store(parent2, Integer.MAX_VALUE);
        client2.commit();
        client2.close();
        
        assertInstanceCountAndFieldIndexes(client1);
    	
    }
    
	private void assertInstanceCountAndFieldIndexes(ExtObjectContainer client1) {
		ItemParent parent3 = retrieveOnlyInstance(client1, ItemParent.class);
        retrieveOnlyInstance(client1, Item.class);
        client1.refresh(parent3, Integer.MAX_VALUE);
        final long parentIdAfterUpdate = client1.getID(parent3);
        final long childIdAfterUpdate = client1.getID(parent3.child);
        
        new FieldIndexAssert(ItemParent.class, "child").assertSingleEntry(fileSession(), parentIdAfterUpdate);
        new FieldIndexAssert(Item.class, "name").assertSingleEntry(fileSession(), childIdAfterUpdate);
	}
    
    public static void main(String[] arguments) {
        new CsCascadedDeleteReaddChildReferenceTestCase().runAll();
    }


}
