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


import java.util.*;

import com.db4o.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;


/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ArrayListUpdateTestCase extends HandlerUpdateTestCaseBase{
    
    private static final Object[] DATA = new Object[] { 
        "one",
        "aAzZ|!§$%&/()=?ßöäüÄÖÜYZ;:-_+*~#^°'@",
        "",
        createNestedList(10),
        null,
    };
    
    private static List createNestedList(int depth){
        List list = new ArrayList();
        list.add("nested1");
        list.add("nested2");
        if(depth > 0){
            list.add(createNestedList(depth - 1));
        }
        return list;
    }

    protected String typeName() {
        return "ArrayList";
    }
    
    public static class Item {
        
        public String _listClassName;
        
        public ArrayList _typed;
        
        public Object _untyped;
        
        public ArrayList _emptyTyped;
        
        public Object _emptyUntyped;
        
        public List _interface;
        
        public List _emptyInterface;
        
    }
    
    /** Todo: add as type to Item **/
    public static class ArrayListExtensionWithField extends ArrayList{
        
        public static final String STORED_NAME = "outListsName";
        
        public String name;
        
        public boolean equals(Object obj) {
            if(! super.equals(obj)){
                return false;
            }
            ArrayListExtensionWithField other = (ArrayListExtensionWithField) obj;
            if(name == null){
                return other.name == null;
            }
            return name.equals(other.name);
        }
    }
    
    /** Todo: add as type to Item **/
    public static class ArrayListExtensionWithoutField extends ArrayList{
        
    }
    
    
    protected Object[] createValues() {
        if(testNotCompatibleToOldVersion()){
            return new Item[0];
        }
        Item[] values = new Item[3];
        values[0] = createItem(ArrayList.class);
        values[1] = createItem(ArrayListExtensionWithField.class);
        values[2] = createItem(ArrayListExtensionWithoutField.class);
        return values;
    }
    
    private Item createItem(Class clazz){
        Item item = new Item();
        item._listClassName = clazz.getName();
        createLists(item, clazz);
        return item;
    }

    private void createLists(Item item, Class clazz) {
        item._typed = (ArrayList) createFilledList(clazz);
        item._untyped = createFilledList(clazz);
        item._interface = createFilledList(clazz);
        item._emptyTyped = (ArrayList)createList(clazz);
        item._emptyUntyped = createList(clazz);
        item._emptyInterface = createList(clazz);
    }
    
    private List createFilledList(Class clazz){
        List list = createList(clazz); 
        fillList(list);
        if( list instanceof ArrayListExtensionWithField){
            ArrayListExtensionWithField typedList = (ArrayListExtensionWithField) list;
            typedList.name = ArrayListExtensionWithField.STORED_NAME;
        }
        return list;
    }

    private List createList(Class clazz) {
        List list = null;
        try {
            list = (List) clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private void fillList(Object list){
        for (int i = 0; i < DATA.length; i++) {
            ((List)list).add(DATA[i]);
        }
    }
    
    protected Object createArrays() {
        return null;
    }
    
    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        if(testNotCompatibleToOldVersion()){
            return;
        }
        assertItem(values[0], ArrayList.class);
        assertItem(values[1], ArrayListExtensionWithField.class);
        assertItem(values[2], ArrayListExtensionWithoutField.class);
    }
    
    protected void assertQueries(ExtObjectContainer objectContainer) {
        if(testNotCompatibleToOldVersion()){
            return;
        }
        assertQueries(objectContainer, ArrayList.class);
        assertQueries(objectContainer, ArrayListExtensionWithField.class);
        assertQueries(objectContainer, ArrayListExtensionWithoutField.class);
    }
    
    private void assertQueries(ExtObjectContainer objectContainer, Class clazz){
        assertQuery(objectContainer, clazz, "_typed");
//        assertQuery(objectContainer, clazz, "_untyped");
//        assertQuery(objectContainer, clazz, "_interface");
    }

    
    private void assertQuery(ExtObjectContainer objectContainer, Class clazz, String fieldName ){
        Query q = objectContainer.query();
        q.constrain(Item.class);
        q.descend("_listClassName").constrain(clazz.getName());
        q.descend(fieldName).constrain("one");
        ObjectSet objectSet = q.execute();
        Assert.areEqual(1, objectSet.size());
        Item item = (Item) objectSet.next();
        assertItem(item, clazz);
    }
    

    private void assertItem(Object obj, Class clazz) {
        Item item = (Item) obj;
        assertList(item._typed, clazz);
        assertList(item._untyped, clazz);
        assertList(item._interface, clazz);
        assertEmptyList(item._emptyTyped);
        assertEmptyList(item._emptyUntyped);
        assertEmptyList(item._emptyInterface);
    }
    
    private void assertEmptyList(Object obj) {
        List list = (List) obj;
        Assert.areEqual(0, list.size());
    }

    private void assertList(Object obj, Class clazz) {
        List list = (List) obj;
        Object[] array = new Object[list.size()];
        int idx = 0;
        Iterator i = list.iterator();
        while(i.hasNext()){
            array[idx++] = i.next();
        }
        ArrayAssert.areEqual(DATA, array);
        Assert.isInstanceOf(clazz, list);
        if( list instanceof ArrayListExtensionWithField){
            ArrayListExtensionWithField typedList = (ArrayListExtensionWithField) list;
            Assert.areEqual(ArrayListExtensionWithField.STORED_NAME, typedList.name);
        }

    }

    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        // do nothing
    }
    
    private boolean testNotCompatibleToOldVersion() {
        // This test fails for 3.0 and 4.0 versions, probably
        // because translators are incompatible.
        
        if(db4oMajorVersion() < 5) {
            return true;
        }
        return db4oHeaderVersion() == VersionServices.HEADER_30_40;
    }


    
}
