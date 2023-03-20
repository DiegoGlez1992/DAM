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

import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class IndexedBlockSizeQueryTestCase extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new IndexedBlockSizeQueryTestCase().runNetworking();
    }
    
    protected void configure(Configuration config) throws Exception {
        config.blockSize(10);
        config.objectClass(Item.class).objectField("_name").indexed(true);
    }
    
    public static class Item {
        
        // public Object _untypedMember;
        
        public String _name;
        
        public Item(String name){
            // _untypedMember = name;
            _name = name;
        }
    }
    
    protected void store() throws Exception {
        store(new Item("one"));
    }
    
    public void test(){
        Query q = newQuery(Item.class);
        q.descend("_name").constrain("one");
        Assert.areEqual(1, q.execute().size());
    }

}
