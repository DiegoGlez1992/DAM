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


import com.db4o.ext.*;

import db4ounit.*;


public class NestedArrayUpdateTestCase extends HandlerUpdateTestCaseBase{
    
    private static final Object[] primitiveArrayData = new Object[]{
        new int[] {1,2,3},
        new int[] {4,5},
        new int[] {},
    };
    
    private static final Object[] stringArrayData = new Object[]{
        new String[] {"one", null, ""},
        new String[] {"two"},
        new String[] {""},
        new String[] {},
    };
    
    private static final Object[] nestedArrayData = new Object[]{
        new Object[]{
            primitiveArrayData,
            stringArrayData,
        },
        new Object[]{
            primitiveArrayData,
            stringArrayData,
        }
    };
    
    private static final Object[] nestedNestedArrayData = new Object[]{
        new Object[]{
            nestedArrayData,
            nestedArrayData,
        }, new Object[]{
            nestedArrayData,
            nestedArrayData,
        }
    };
    
    public static class ItemArrays {
        
        public Object[] _primitiveArray;
        
        public Object _primitiveArrayInObject;
        
        public Object[] _stringArray;
        
        public Object _stringArrayInObject;
        
        public Object[] _nestedArray;
        
        public Object _nestedArrayInObject;
        
        public Object[] _nestedNestedArray;
        
        public Object _nestedNestedArrayInObject;
        
    }
    
    protected Object createArrays() {
        ItemArrays item = new ItemArrays();
        
        item._primitiveArray = primitiveArrayData;
        item._primitiveArrayInObject = primitiveArrayData;
        item._stringArray = stringArrayData;
        item._stringArrayInObject = stringArrayData;
        
        item._nestedArray = nestedArrayData;
        item._nestedArrayInObject = nestedArrayData;
        item._nestedNestedArray = nestedNestedArrayData;
        item._nestedNestedArrayInObject = nestedNestedArrayData;
        
        return item;
    }
    
    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        ItemArrays item = (ItemArrays) obj;
        
        assertPrimitiveArray(item._primitiveArray);
        assertPrimitiveArray(item._primitiveArrayInObject);
        assertStringArray(item._stringArray);
        assertStringArray(item._stringArrayInObject);
        
        assertNestedArray(nestedArrayData, item._nestedArray);
        assertNestedArray(nestedArrayData, item._nestedArrayInObject);
        assertNestedArray(nestedNestedArrayData, item._nestedNestedArray);
        assertNestedArray(nestedNestedArrayData, item._nestedNestedArrayInObject);
    }

    private void assertNestedArray(Object expected, Object actual) {
        Object[] expectedArray = (Object[]) expected;
        Object[] actualArray = (Object[]) actual;
        Assert.areEqual(expectedArray.length, actualArray.length);
        for (int i = 0; i < expectedArray.length; i++) {
            Object[] expectedSubArray = (Object[]) expectedArray[i];
            Object actualSubArray = actualArray[i];
            Object template = expectedSubArray[0]; 
            if(template instanceof int[]){
                assertPrimitiveArray(actualSubArray);
            } else if(template instanceof String[]){
                assertStringArray(actualSubArray);
            } else{
                assertNestedArray(expectedSubArray, actualSubArray);
            }
        }
    }

    private void assertStringArray(Object array) {
        Object[] stringArray = (Object[]) array;
        for (int i = 0; i < stringArray.length; i++) {
            String[] actual = (String[])stringArray[i];
            String[] expected = (String[]) stringArrayData[i];
            Assert.areEqual(actual.length, expected.length);
            for (int j = 0; j < expected.length; j++) {
                Assert.areEqual(expected[j], actual[j]);
            }
        }
    }

    private void assertPrimitiveArray(Object array) {
        Object[] primitiveArray = (Object[]) array;
        for (int i = 0; i < primitiveArray.length; i++) {
            int[] expected = (int[]) primitiveArrayData[i];
            int[] actual = castToIntArray(primitiveArray[i]);
            Assert.areEqual(actual.length, expected.length);
            for (int j = 0; j < expected.length; j++) {
                Assert.areEqual(expected[j], actual[j]);
            }
        }
    }
    
    protected Object[] createValues() {
        // not used
        return null;
    }

    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        // not used
    }

    protected String typeName() {
        return "nested_array";
    }

}
