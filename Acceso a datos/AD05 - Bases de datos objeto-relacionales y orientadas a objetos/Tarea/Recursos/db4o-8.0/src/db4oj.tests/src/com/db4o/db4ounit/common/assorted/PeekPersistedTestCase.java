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
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class PeekPersistedTestCase extends AbstractDb4oTestCase implements OptOutStackOverflowIssue {
	
	public static final class Item {
		
	    public String name;
	    
	    public Item child;
	    
	    public Item() {
	    }
	    
	    public Item(String name, Item child) {
	    	this.name = name;
	    	this.child = child;
	    }
	    
	    public String toString() {
	    	return "Item(" + name + ", " + child + ")";
	    }
	}
    
    protected void store() {
        final Item root = new Item("1", null);
        Item current = root;
        for (int i = 2; i < 11; i++) {
            current.child = new Item("" + i, null);
            current = current.child;
        }
        store(root);
    }
    
    public void test(){
        Item root = queryRoot();
        for (int i = 0; i < 10; i++) {
            peek(root, i);
        }
    }

	private Item queryRoot() {
		Query q = newQuery(Item.class);
        q.descend("name").constrain("1");
        ObjectSet objectSet = q.execute();
        return (Item)objectSet.next();
	}
    
    private void peek(Item original, int depth){
        Item peeked = (Item)db().peekPersisted(original, depth, true);
        for (int i = 0; i <= depth; i++) {
            Assert.isNotNull(peeked, "Failed to peek at child " + i + " at depth " + depth);
            Assert.isFalse(db().isStored(peeked));
            peeked = peeked.child;
        }
        Assert.isNull(peeked);
    }

}
