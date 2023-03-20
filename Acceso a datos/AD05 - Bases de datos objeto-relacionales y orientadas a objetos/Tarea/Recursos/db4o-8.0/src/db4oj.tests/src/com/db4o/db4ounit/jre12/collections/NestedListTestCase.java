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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 * @exclude
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class NestedListTestCase extends AbstractDb4oTestCase{
    
	public static class Item {
        
        public List list;
        
        public boolean equals(Object obj) {
            if(! (obj instanceof Item)){
                return false;
            }
            Item otherItem = (Item) obj;
            if(list == null){
                return otherItem.list == null;
            }
            return list.equals(otherItem.list);
        }
        
    }
    
    protected void store() throws Exception {
        store(storedItem());
    }

    private Item storedItem() {
        Item item = new Item();
        item.list = newNestedList(10);
        return item;
    }
    
    private List newNestedList(int depth) {
        List list = new ArrayList();
        list.add("StringItem");
        if(depth > 0){
            list.add(newNestedList(depth - 1));
        }
        return list;
    }

    public void testNestedList(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        db().activate(item, Integer.MAX_VALUE);
        Assert.areEqual(storedItem(), item);
    }

}
