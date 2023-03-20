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

import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;

public class CharHandlerUpdateTestCase extends HandlerUpdateTestCaseBase {

    public static class Item {
        public char _typedPrimitive;

        public Character _typedWrapper;

        public Object _untyped;
    }
    
    public static class ItemArrays {
        public char[] _typedPrimitiveArray;

        public Character[] _typedWrapperArray;

        public Object[] _untypedObjectArray;

        public Object _primitiveArrayInObject;

        public Object _wrapperArrayInObject;
    }
    
    private static final char[] data = new char[] {
        Character.MIN_VALUE,
    	(char)0x0000,
    	(char)0x000F,
    	(char)0x00F0,
    	(char)0x00FF,
    	(char)0x0F00,
    	(char)0x0F0F,
    	(char)0x0FF0,
    	(char)0x0FFF,
    	(char)0xF000,
    	(char)0xF00F,
    	(char)0xF0F0,
    	(char)0xF0FF,
    	(char)0xFF00,
    	(char)0xFF0F,
    	(char)0xFFF0,
    	(char)0xFFFF,
    	Character.MAX_VALUE,
    };
    
    public static void main(String[] args) {
        new ConsoleTestRunner(CharHandlerUpdateTestCase.class).run();
    }
    
    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        ItemArrays itemArrays = (ItemArrays)obj;
        
        assertPrimitiveArray(itemArrays._typedPrimitiveArray);
        assertPrimitiveArray(castToCharArray(itemArrays._primitiveArrayInObject));
        assertWrapperArray(itemArrays._typedWrapperArray);
        assertUntypedObjectArray(itemArrays);
        assertWrapperArray((Character[])itemArrays._wrapperArrayInObject);
    }
    
    /**
     * @sharpen.remove Cannot convert 'object[]' to 'Short[]' in .net
     */
    private void assertUntypedObjectArray(ItemArrays itemArrays) {
        assertWrapperArray((Character[])itemArrays._untypedObjectArray);
    }

    private void assertPrimitiveArray(char[] primitiveArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], primitiveArray[i]);
        }
    }
    
    private void assertWrapperArray(Character[] wrapperArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(new Character(data[i]), wrapperArray[i]);
        }
        //FIXME: Arrays should also get a null Bitmap to fix.
        //Assert.isNull(wrapperArray[wrapperArray.length - 1]);
    }

    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._typedPrimitive);
            assertAreEqual(new Character(data[i]), item._typedWrapper);
            assertAreEqual(new Character(data[i]), item._untyped);
        }
        
        Item nullItem = (Item) values[values.length -1];
        assertAreEqual((char)0, nullItem._typedPrimitive);
        assertCharWrapperIsNullJavaOnly(nullItem._typedWrapper);
        Assert.isNull(nullItem._untyped);
    }

    protected Object createArrays() {
        ItemArrays itemArrays = new ItemArrays();
        itemArrays._typedPrimitiveArray = new char[data.length];
        System.arraycopy(data, 0, itemArrays._typedPrimitiveArray, 0, data.length);
        
        Character[] dataWrapper = new Character[data.length];
        for (int i = 0; i < data.length; i++) {
            dataWrapper[i] = new Character(data[i]);
        }
        
        itemArrays._typedWrapperArray = new Character[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._typedWrapperArray, 0, dataWrapper.length);
        
        initializeUntypedObjectArray(itemArrays, dataWrapper);
        
        char[] primitiveArray = new char[data.length];
        System.arraycopy(data, 0, primitiveArray, 0, data.length);
        itemArrays._primitiveArrayInObject = primitiveArray;
        
        Character[] wrapperArray = new Character[data.length + 1];
        System.arraycopy(dataWrapper, 0, wrapperArray, 0, dataWrapper.length);
        itemArrays._wrapperArrayInObject = wrapperArray;
        return itemArrays;
    }
    
    /**
     * @sharpen.remove Cannot convert 'Character[]' to 'object[]'
     */
    private void initializeUntypedObjectArray(ItemArrays itemArrays, Character[] dataWrapper) {
        itemArrays._untypedObjectArray = new Character[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._untypedObjectArray, 0, dataWrapper.length);
    }

    protected Object[] createValues() {
        Item[] values = new Item[data.length + 1];
        for (int i =0; i < data.length; i++) {
            Item item = new Item();
            item._typedPrimitive = data[i];
            item._typedWrapper = new Character(data[i]);
            item._untyped = new Character(data[i]);
            values[i] = item;
        }
        values[values.length - 1] = new Item();
        return values;
    }

    protected String typeName() {
        return "char";
    }
    
    private void assertAreEqual(char expected, char actual){
        Assert.areEqual(expected, actual);
    }
    
    private void assertAreEqual(Object expected, Object actual){
        Assert.areEqual(expected, actual);
    }
    
    /**
     * @sharpen.remove
     */
    private void assertCharWrapperIsNullJavaOnly(Object obj){
        if(db4oHandlerVersion() == 0){
            
            // Bug when reading old format:
            // Null wrappers are converted to Character.MAX_VALUE
            
            Assert.areEqual(new Character(Character.MAX_VALUE), obj);
        } else {
            Assert.isNull(obj);
        }
    }
    
    private char[] castToCharArray(Object obj){
        ObjectByRef byRef = new ObjectByRef(obj);
        castToCharArrayJavaOnly(byRef);
        return (char[]) byRef.value;
    }
    
    /**
     * @sharpen.remove
     */
    private void castToCharArrayJavaOnly(ObjectByRef byRef) {
        if(db4oHeaderVersion() != VersionServices.HEADER_30_40){
            return;
        }
            
        // Bug in the oldest format: 
        // It accidentally converted char[] arrays to Character[] arrays.
        
        Character[] wrapperArray = (Character[])byRef.value;
        char[] res = new char[wrapperArray.length];
        for (int i = 0; i < wrapperArray.length; i++) {
            if(wrapperArray[i] != null){
                res[i] = wrapperArray[i].charValue();
            }
        }
        byRef.value = res;
    }

}
