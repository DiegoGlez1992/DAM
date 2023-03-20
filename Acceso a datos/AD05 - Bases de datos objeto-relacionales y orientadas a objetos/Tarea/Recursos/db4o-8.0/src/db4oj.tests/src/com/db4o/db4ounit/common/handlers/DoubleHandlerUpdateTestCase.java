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



public class DoubleHandlerUpdateTestCase extends HandlerUpdateTestCaseBase {

    private final double[] data; 
    
    public DoubleHandlerUpdateTestCase(){
    	data = new double[] {
    	        Double.MIN_VALUE, 
    	        Double.MIN_VALUE + 1,
    	        -3.1415926535789,
    	        -1,
    	        0,
    	        usesNullMarkerValue() ? 0 : Double.NaN,
    	        Double.NEGATIVE_INFINITY,
    	        Double.POSITIVE_INFINITY,
    	        1,
    	        3.1415926535789,
    	        Double.MAX_VALUE - 1,
    	        Double.MAX_VALUE,
    	    };
    }
    
    public static class Item {
        
        public double _typedPrimitive;
        
        public Double _typedWrapper;
        
        public Object _untyped;
    }
    
    public static class ItemArrays {
        
        public double[] _typedPrimitiveArray;
        
        public Double[] _typedWrapperArray;
        
        public Object[] _untypedObjectArray;
        
        public Object _primitiveArrayInObject;
        
        public Object _wrapperArrayInObject;
        
    }
    
    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        ItemArrays item = (ItemArrays) obj;
        assertTypedPrimitiveArray(item);
        assertTypedWrapperArray(item);
        
        // Will be removed for .NET by sharpen.
        assertUntypedObjectArray(item);
        
        assertPrimitiveArrayInObject(item);
        assertWrapperArrayInObject(item);

	}
    private void assertTypedPrimitiveArray(ItemArrays item) {
        assertData(item._typedPrimitiveArray);
    }

    private void assertTypedWrapperArray(ItemArrays item) {
        assertWrapperData(item._typedWrapperArray);
    }
    /**
     * @sharpen.remove
     */
    protected void assertUntypedObjectArray(ItemArrays item) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(new Double(data[i]), item._untypedObjectArray[i]);
        }
        Assert.isNull(item._untypedObjectArray[item._untypedObjectArray.length - 1]);
    }
    
    private void assertPrimitiveArrayInObject(ItemArrays item) {
        if(db4oHeaderVersion() == VersionServices.HEADER_30_40){
           // Bug in the oldest format: It accidentally double[] arrays to Double[] arrays.
            assertWrapperData((Double[]) item._primitiveArrayInObject);
        } else{
            assertData((double[]) item._primitiveArrayInObject);
        }
    }
    
    private void assertWrapperArrayInObject(ItemArrays item) {
        assertWrapperData((Double[]) item._wrapperArrayInObject);
    }


    private void assertData(double[] values) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], values[i]);
        }
    }

    private void assertWrapperData(Double[] values) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(new Double(data[i]), values[i]);
        }
        
        // FIXME: The following fails as is because of a deficiency 
        //        in the storage format of arrays.
        
        //        Arrays should also get a null Bitmap to fix.
        
        // Assert.isNull(values[values.length - 1]);
    }

	protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._typedPrimitive);
            assertAreEqual(new Double(data[i]), item._typedWrapper);
            assertAreEqual(new Double(data[i]), item._untyped);
        }
        Item nullItem = (Item) values[values.length - 1];
        Assert.areEqual(0, nullItem._typedPrimitive);
        assertPrimitiveWrapperIsNullJavaOnly(nullItem._typedWrapper);
        Assert.isNull(nullItem._untyped);

	}
	
	protected Object[] createValues() {
        Item[] values = new Item[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            Item item = new Item();
            values[i] = item;
            item._typedPrimitive = data[i];
            item._typedWrapper = new Double(data[i]);
            item._untyped = new Double(data[i]);
        }
        values[values.length - 1] = new Item();
        return values;
	}



	protected Object createArrays() {
        ItemArrays item = new ItemArrays();
        createTypedPrimitiveArray(item);
        createTypedWrapperArray(item);
        
        // Will be removed for .NET by sharpen.
        createUntypedObjectArray(item);
        
        createPrimitiveArrayInObject(item);
        createWrapperArrayInObject(item);
        return item;
	}
	
    /**
     * @sharpen.remove
     */
    private void createUntypedObjectArray(ItemArrays item){
        item._untypedObjectArray = new Double[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            item._untypedObjectArray[i] = new Double(data[i]);
        }
    }
    
    private void createTypedPrimitiveArray(ItemArrays item){
        item._typedPrimitiveArray = new double[data.length];
        System.arraycopy(data, 0, item._typedPrimitiveArray, 0, data.length);
    }
    
    private void createTypedWrapperArray(ItemArrays item){
        item._typedWrapperArray = new Double[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            item._typedWrapperArray[i] = new Double(data[i]);
        }
    }
    
    private void createPrimitiveArrayInObject(ItemArrays item){
        double[] arr = new double[data.length];
        System.arraycopy(data, 0, arr, 0, data.length);
        item._primitiveArrayInObject = arr;
    }
    
    private void createWrapperArrayInObject(ItemArrays item){
        Double[] arr = new Double[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            arr[i] = new Double(data[i]);
        }
        item._wrapperArrayInObject = arr;
    }
	

	protected String typeName() {
		return "double";
	}

    private void assertAreEqual(double expected, double actual){
        if(Double.isNaN(expected) && usesNullMarkerValue()){
            expected = 0;
        }
        if(Double.isNaN(expected) && Double.isNaN(actual)){
        	return;
        }
        Assert.areEqual(expected, actual);
    }
    
    private void assertAreEqual(Object expected, Object actual){
        if(((Double)expected).isNaN() && usesNullMarkerValue()){
            expected = null;
        }
        if(expected != null && actual != null && ((Double)expected).isNaN() && ((Double)actual).isNaN()){
        	return;
        }
        Assert.areEqual(expected, actual);
    }

}
