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

public class FloatHandlerUpdateTestCase extends HandlerUpdateTestCaseBase {
	
    private final float[] data;
                        
    public FloatHandlerUpdateTestCase() {
    	data = new float[] {
                Float.NEGATIVE_INFINITY,
                Float.MIN_VALUE,
                Float.MIN_VALUE + 1,
                -5,
                -1,
                0,
                1,
                5,
                Float.MAX_VALUE - 1,
                Float.MAX_VALUE,
                Float.POSITIVE_INFINITY,
                usesNullMarkerValue() ? 0:Float.NaN,
            };
        
	}

    
    public static class Item {
        public float _typedPrimitive;

        public Float _typedWrapper;

        public Object _untyped;
    }
    
    public static class ItemArrays {
        public float[] _typedPrimitiveArray;

        public Float[] _typedWrapperArray;

        public Object[] _untypedObjectArray;

        public Object _primitiveArrayInObject;

        public Object _wrapperArrayInObject;
    }
    
    
    public static void main(String[] args) {
        new ConsoleTestRunner(FloatHandlerUpdateTestCase.class).run();
    }
    
    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        ItemArrays itemArrays = (ItemArrays)obj;
        
        assertPrimitiveArray(itemArrays._typedPrimitiveArray);
        if(db4oHeaderVersion() == VersionServices.HEADER_30_40){
         // Bug in the oldest format: It accidentally float[] arrays to Float[] arrays.
            assertWrapperArray((Float[])itemArrays._primitiveArrayInObject);
        } else {
            assertPrimitiveArray((float[])itemArrays._primitiveArrayInObject);
        }
        assertWrapperArray(itemArrays._typedWrapperArray);
        assertUntypedObjectArray(itemArrays);
        assertWrapperArray((Float[])itemArrays._wrapperArrayInObject);
    }
    
    /**
     * @sharpen.remove Cannot convert 'object[]' to 'Float[]' in .net
     */
    private void assertUntypedObjectArray(ItemArrays itemArrays) {
        assertWrapperArray((Float[])itemArrays._untypedObjectArray);
    }

    private void assertPrimitiveArray(float[] primitiveArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], primitiveArray[i]);
        }
    }
    
    private void assertWrapperArray(Float[] wrapperArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(new Float(data[i]), wrapperArray[i]);
        }
        //FIXME: Arrays should also get a null Bitmap to fix.
        //Assert.isNull(wrapperArray[wrapperArray.length - 1]);
    }

    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._typedPrimitive);
            assertAreEqual(new Float(data[i]), item._typedWrapper);
            assertAreEqual(new Float(data[i]), item._untyped);
        }
        
        Item nullItem = (Item) values[values.length -1];
        assertAreEqual((float)0, nullItem._typedPrimitive);
        assertPrimitiveWrapperIsNullJavaOnly(nullItem._typedWrapper);
        Assert.isNull(nullItem._untyped);
    }

    private void assertAreEqual(float expected, float actual) {
        if (Float.isNaN(expected) && db4oHandlerVersion() == 0) {
            expected = 0;
        }
        if (Float.isNaN(expected) && Float.isNaN(actual)) {
            return;
        }
        Assert.areEqual(expected, actual);
    }

    private void assertAreEqual(Object expected, Object actual) {
        if (((Float)expected).isNaN() && db4oHandlerVersion() == 0) {
            expected = null;
        }
        Assert.areEqual(expected, actual);
    }
    
    protected Object createArrays() {
        ItemArrays itemArrays = new ItemArrays();
        itemArrays._typedPrimitiveArray = new float[data.length];
        System.arraycopy(data, 0, itemArrays._typedPrimitiveArray, 0, data.length);
        
        Float[] dataWrapper = new Float[data.length];
        for (int i = 0; i < data.length; i++) {
            dataWrapper[i] = new Float(data[i]);
        }
        
        itemArrays._typedWrapperArray = new Float[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._typedWrapperArray, 0, dataWrapper.length);
        
        initializeUntypedObjectArray(itemArrays, dataWrapper);
        
        float[] primitiveArray = new float[data.length];
        System.arraycopy(data, 0, primitiveArray, 0, data.length);
        itemArrays._primitiveArrayInObject = primitiveArray;
        
        Float[] wrapperArray = new Float[data.length + 1];
        System.arraycopy(dataWrapper, 0, wrapperArray, 0, dataWrapper.length);
        itemArrays._wrapperArrayInObject = wrapperArray;
        return itemArrays;
    }
    
    /**
     * @sharpen.remove Cannot convert 'Float[]' to 'object[]'
     */
    private void initializeUntypedObjectArray(ItemArrays itemArrays, Float[] dataWrapper) {
        itemArrays._untypedObjectArray = new Float[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._untypedObjectArray, 0, dataWrapper.length);
    }

    protected Object[] createValues() {
        Item[] values = new Item[data.length + 1];
        for (int i =0; i < data.length; i++) {
            Item item = new Item();
            item._typedPrimitive = data[i];
            item._typedWrapper = new Float(data[i]);
            item._untyped = new Float(data[i]);
            values[i] = item;
        }
        values[values.length - 1] = new Item();
        return values;
    }

    protected String typeName() {
        return "float";
    }

}
