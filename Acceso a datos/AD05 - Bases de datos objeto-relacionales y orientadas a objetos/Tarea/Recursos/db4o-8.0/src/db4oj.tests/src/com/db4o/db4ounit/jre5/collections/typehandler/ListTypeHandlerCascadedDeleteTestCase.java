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
package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import com.db4o.config.*;
import com.db4o.typehandlers.*;

import db4ounit.extensions.*;


/**
 * @exclude
 */
@decaf.Ignore
public class ListTypeHandlerCascadedDeleteTestCase extends AbstractDb4oTestCase{

    /**
     * @param args
     */
    public static void main(String[] args) {
        new ListTypeHandlerCascadedDeleteTestCase().runSolo();
    }
    
    public static class Item{
        
        public Object _untypedList;
        
        public ArrayList _typedList;
        
    }
    
    public static class Element{
        
    }
    
    @Override
    protected void configure(Configuration config) throws Exception {
        config.objectClass(Item.class).cascadeOnDelete(true);
        config.objectClass(ArrayList.class).cascadeOnDelete(true);
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(ArrayList.class), 
            new CollectionTypeHandler());
    }
    
    @Override
    protected void store() throws Exception {
        Item item = new Item();
        item._untypedList = new ArrayList();
        ((List)item._untypedList).add(new Element());
        item._typedList = new ArrayList();
        item._typedList.add(new Element());
        store(item);
    }
    
    public void testCascadedDelete(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        Db4oAssert.persistedCount(2, Element.class);
        db().delete(item);
        db().purge();
        db().commit();
        Db4oAssert.persistedCount(0, Item.class);
        Db4oAssert.persistedCount(0, ArrayList.class);
        Db4oAssert.persistedCount(0, Element.class);
    }
    
    public void testArrayListCount(){
        Db4oAssert.persistedCount(2, ArrayList.class);
    }

}
