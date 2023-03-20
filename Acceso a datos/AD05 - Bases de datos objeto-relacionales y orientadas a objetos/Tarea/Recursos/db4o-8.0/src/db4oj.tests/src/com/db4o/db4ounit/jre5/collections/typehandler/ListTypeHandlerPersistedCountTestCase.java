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
 */
@decaf.Ignore
public class ListTypeHandlerPersistedCountTestCase extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new ListTypeHandlerPersistedCountTestCase().runAll();
    }
    
    public static class TypedItem {
        
        ArrayList list;
        
    }
    
    public static class InterfaceItem {
        
        List list;
        
    }
    
    public static class UntypedItem {
        
        Object list;
        
    }
    
    protected void configure(Configuration config) throws Exception {
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(ArrayList.class), 
            new CollectionTypeHandler());
    }
    
    public void testTypedItem(){
        TypedItem typedItem = new TypedItem();
        typedItem.list = new ArrayList();
        store(typedItem);
        Db4oAssert.persistedCount(1, ArrayList.class);
    }
    
    public void testInterFaceItem(){
        InterfaceItem interfaceItem = new InterfaceItem();
        interfaceItem.list = new ArrayList();
        store(interfaceItem);
        Db4oAssert.persistedCount(1, ArrayList.class);
    }
    
    public void testUntypedItem(){
        UntypedItem untypedItem = new UntypedItem();
        untypedItem.list = new ArrayList();
        store(untypedItem);
        Db4oAssert.persistedCount(1, ArrayList.class);
    }
    
}
