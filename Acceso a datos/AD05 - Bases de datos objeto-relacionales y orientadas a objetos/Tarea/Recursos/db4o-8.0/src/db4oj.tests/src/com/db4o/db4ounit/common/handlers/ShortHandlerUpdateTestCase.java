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

import db4ounit.*;

public class ShortHandlerUpdateTestCase extends HandlerUpdateTestCaseBase {

    private final short[] data; 
    
    public ShortHandlerUpdateTestCase() {
    	data = new short[] {
                Short.MIN_VALUE,
                Short.MIN_VALUE + 1,
                -5,
                -1,
                0,
                1,
                5,
                Short.MAX_VALUE - 1,
                usesNullMarkerValue() ? (short)0: Short.MAX_VALUE,
            };
	}

	public static class Item {
        public short _typedPrimitive;

        public Short _typedWrapper;

        public Object _untyped;
    }
    
    public static class ItemArrays {
        public short[] _typedPrimitiveArray;

        public Short[] _typedWrapperArray;

        public Object[] _untypedObjectArray;

        public Object _primitiveArrayInObject;

        public Object _wrapperArrayInObject;
    }
    
    public static void main(String[] args) {
        new ConsoleTestRunner(ShortHandlerUpdateTestCase.class).run();
    }
    
    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        ItemArrays itemArrays = (ItemArrays)obj;
        
        assertPrimitiveArray(itemArrays._typedPrimitiveArray);
        if(db4oHeaderVersion() == VersionServices.HEADER_30_40){
         // Bug in the oldest format: It accidentally short[] arrays to Short[] arrays.
            assertWrapperArray((Short[])itemArrays._primitiveArrayInObject);
        } else {
            assertPrimitiveArray((short[])itemArrays._primitiveArrayInObject);
        }
        assertWrapperArray(itemArrays._typedWrapperArray);
        assertUntypedObjectArray(itemArrays);
        assertWrapperArray((Short[])itemArrays._wrapperArrayInObject);
    }
    
    /**
     * @sharpen.remove Cannot convert 'object[]' to 'Short[]' in .net
     */
    private void assertUntypedObjectArray(ItemArrays itemArrays) {
        assertWrapperArray((Short[])itemArrays._untypedObjectArray);
    }

    private void assertPrimitiveArray(short[] primitiveArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], primitiveArray[i]);
        }
    }
    
    private void assertWrapperArray(Short[] wrapperArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(new Short(data[i]), wrapperArray[i]);
        }
        //FIXME: Arrays should also get a null Bitmap to fix.
        //Assert.isNull(wrapperArray[wrapperArray.length - 1]);
    }

    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._typedPrimitive);
            assertAreEqual(new Short(data[i]), item._typedWrapper);
            assertAreEqual(new Short(data[i]), item._untyped);
        }
        
        Item nullItem = (Item) values[values.length -1];
        assertAreEqual((short)0, nullItem._typedPrimitive);
        assertPrimitiveWrapperIsNullJavaOnly(nullItem._typedWrapper);
        Assert.isNull(nullItem._untyped);
    }

    private void assertAreEqual(short expected, short actual) {
        if(expected == Short.MAX_VALUE  && db4oHandlerVersion() == 0){
         // Bug in the oldest format: It treats Short.MAX_VALUE as null.
            expected = 0;
        }
        Assert.areEqual(expected, actual);
    }

    private void assertAreEqual(Object expected, Object actual) {
        if(new Short(Short.MAX_VALUE).equals(expected) && db4oHandlerVersion() == 0){
            // Bug in the oldest format: It treats Short.MAX_VALUE as null.
            expected = null;
        }
        Assert.areEqual(expected, actual);
    }
    
    protected Object createArrays() {
        ItemArrays itemArrays = new ItemArrays();
        itemArrays._typedPrimitiveArray = new short[data.length];
        System.arraycopy(data, 0, itemArrays._typedPrimitiveArray, 0, data.length);
        
        Short[] dataWrapper = new Short[data.length];
        for (int i = 0; i < data.length; i++) {
            dataWrapper[i] = new Short(data[i]);
        }
        
        itemArrays._typedWrapperArray = new Short[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._typedWrapperArray, 0, dataWrapper.length);
        
        initializeUntypedObjectArray(itemArrays, dataWrapper);
        
        short[] primitiveArray = new short[data.length];
        System.arraycopy(data, 0, primitiveArray, 0, data.length);
        itemArrays._primitiveArrayInObject = primitiveArray;
        
        Short[] wrapperArray = new Short[data.length + 1];
        System.arraycopy(dataWrapper, 0, wrapperArray, 0, dataWrapper.length);
        itemArrays._wrapperArrayInObject = wrapperArray;
        return itemArrays;
    }
    
    /**
     * @sharpen.remove Cannot convert 'Short[]' to 'object[]'
     */
    private void initializeUntypedObjectArray(ItemArrays itemArrays, Short[] dataWrapper) {
        itemArrays._untypedObjectArray = new Short[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._untypedObjectArray, 0, dataWrapper.length);
    }

    protected Object[] createValues() {
        Item[] values = new Item[data.length + 1];
        for (int i =0; i < data.length; i++) {
            Item item = new Item();
            item._typedPrimitive = data[i];
            item._typedWrapper = new Short(data[i]);
            item._untyped = new Short(data[i]);
            values[i] = item;
        }
        values[values.length - 1] = new Item();
        return values;
    }

    protected String typeName() {
        return "short";
    }
    

}
