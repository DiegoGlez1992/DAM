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

public class ReAddCascadedDeleteTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new ReAddCascadedDeleteTestCase().runAll();
	}
    
    public static class Item {
        
        public String _name;
        
        public Item _member;
        
        public Item() {            
        }

        public Item(String name) {
            _name = name;
        }

        public Item(String name, Item member) {
            _name = name;
            _member = member;
        }
    }
    
    protected void configure(Configuration config){
        config.objectClass(Item.class).cascadeOnDelete(true);
        config.objectClass(Item.class).objectField("_name").indexed(true);
    }
    
    protected void store() {
        db().store(new Item("parent", new Item("child")));
    }
    
    public void testDeletingAndReaddingMember() throws Exception{
        deleteParentAndReAddChild();
        
        reopen();
        
        Assert.isNotNull(query("child"));
        Assert.isNull(query("parent"));
    }

	private void deleteParentAndReAddChild() {
		Item i = query("parent");
        db().delete(i);
        db().store(i._member);
        db().commit();
        long id = db().getID(i._member);
        new FieldIndexAssert(Item.class, "_name").assertSingleEntry(fileSession(), id);
	}
    
    private Item query(String name){
    	ObjectSet objectSet = db().queryByExample(new Item(name));
        if (!objectSet.hasNext()) {
        	return null;
        }
        return (Item) objectSet.next();
    }

}
