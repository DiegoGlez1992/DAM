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
package com.db4o.db4ounit.common.ext;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class StoredClassTestCase extends AbstractDb4oTestCase{
    
    private static final String FIELD_NAME = "_name";
    private static final String ITEM_NAME = "item";

    public static class ItemParent {
        public String[] _array;
    }

    public static class Item extends ItemParent {
        
        public String _name;
        
        public Item(String name){
            _name = name;
        }
    }
    
    private long _id;
    
    protected void configure(Configuration config) throws Exception {
        config.objectClass(Item.class).objectField(FIELD_NAME).indexed(true);
    }
    
    protected void store() throws Exception {
        Item item = new Item(ITEM_NAME);
        store(item);
    }

    protected void db4oSetupAfterStore() throws Exception {
    	_id = db().getID(retrieveOnlyInstance(Item.class));
    }
    
    public void testUnknownStoredClass(){
        Assert.isNull(storedClass(this.getClass()));
    }
    
    public void testStoredClassImpl(){
        Assert.isInstanceOf(StoredClassImpl.class, itemStoredClass());
    }
    
    public void testGetIds(){
        StoredClass itemClass = itemStoredClass();
        long[] ids = itemClass.getIDs();
        Assert.areEqual(1, ids.length);
        Assert.areEqual(_id, ids[0]);
    }
    
    public void testGetName(){
        StoredClass itemClass = itemStoredClass();
        Assert.areEqual(reflector().forClass(Item.class).getName(), itemClass.getName());
    }
    
    public void testGetParentStoredClass(){
        StoredClass itemClass = itemStoredClass();
        StoredClass parentStoredClass = itemClass.getParentStoredClass();
        Assert.areEqual(reflector().forClass(ItemParent.class).getName(), parentStoredClass.getName());
        Assert.areEqual(parentStoredClass, db().storedClass(ItemParent.class));
    }
    
    public void testGetStoredFields(){
        assertStoredField(Item.class, FIELD_NAME, ITEM_NAME, String.class, true,false);
        assertStoredField(ItemParent.class, "_array", null, String.class, false,true);
        
        StoredClass itemStoredClass = itemStoredClass();
        StoredField storedField = itemStoredClass.storedField(FIELD_NAME, null);
        StoredField sameStoredField = itemStoredClass.getStoredFields()[0];
        StoredField otherStoredField = storedClass(ItemParent.class).getStoredFields()[0];
        Assert.equalsAndHashcode(storedField, sameStoredField, otherStoredField);
        
        Assert.isNull(itemStoredClass.storedField("", null));
    }

    private void assertStoredField(Class objectClass, String fieldName, final Object expectedFieldValue,
        Class expectedFieldType, boolean hasIndex, boolean isArray) {
        StoredClass storedClass = storedClass(objectClass);
        StoredField[] storedFields = storedClass.getStoredFields();
        Assert.areEqual(1, storedFields.length);
        final StoredField storedField = storedFields[0];
        Assert.areEqual(fieldName, storedField.getName());
        StoredField storedFieldByName = storedClass.storedField(fieldName, expectedFieldType);
        Assert.areEqual(storedField, storedFieldByName);
        
        Object item = retrieveOnlyInstance(objectClass);
        Assert.areEqual(expectedFieldValue, storedField.get(item));
        ReflectClass fieldType = storedField.getStoredType();
        Assert.areEqual(reflector().forClass(expectedFieldType), fieldType);
        Assert.areEqual(isArray, storedField.isArray());
        
        if(isMultiSession()){
            return;
        }
        
        Assert.areEqual(hasIndex, storedField.hasIndex());
        
        // FIXME: test rename
        
        if(! hasIndex){
            Assert.expect(RuntimeException.class, new CodeBlock() {
                public void run() throws Throwable {
                    storedField.traverseValues(new Visitor4(){
                        public void visit(Object obj) {
                        }
                    });
                }
            });
        } else{
            final IntByRef count = new IntByRef();
            storedField.traverseValues(new Visitor4() {
                public void visit(Object obj) {
                    count.value ++;
                    Assert.areEqual(expectedFieldValue, obj);
                }
            });
            Assert.areEqual(1, count.value);
        }
    }

    public void testEqualsAndHashCode() {
        StoredClass clazz = itemStoredClass();
        StoredClass same = itemStoredClass();
        StoredClass other = db().storedClass(ItemParent.class);
        Assert.equalsAndHashcode(clazz, same, other);
    }

    private StoredClass itemStoredClass() {
        return storedClass(Item.class);
    }

    private StoredClass storedClass(Class clazz) {
        return db().storedClass(clazz);
    }

}
