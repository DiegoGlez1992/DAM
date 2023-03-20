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
import db4ounit.util.*;


/**
 * @exclude
 */
public class MultiDimensionalArrayHandlerUpdateTestCase extends HandlerUpdateTestCaseBase{
    
    // TODO: make asymmetrical once we support
    public static final int[][] intData2D = new int[][]{
        new int[]{
            1, 2, 3
        },
        new int[] {
            4, 5, 6
        }
    };
    
    // TODO: make asymmetrical once we support
    public static final String [][] stringData2D = new String [][]{
        new String[] {
            "one",
            "two",
        },
        new String [] {
            "three",
            "four",
        }
    };
    
    // TODO: make asymmetrical once we support
    public static final Object[][] objectData2D = new Object [][]{
        new Object []{
            new Item("one"),
            null,
            new Item("two"),
        },
        new Object []{
            new Item("three"),
            new Item("four"),
            null
        }
    };
    
    // TODO: make asymmetrical once we support
    public static final Object[][] stringObjectData2D = new Object [][]{
        new Object []{
            "one",
            "two",
        },
        new Object []{
            "three",
            "four",
        }
    };
    
    public static final byte [][] byteData2D = new byte [][]{
        ByteHandlerUpdateTestCase.data,
        ByteHandlerUpdateTestCase.data,
    };
    
    
    public static class ItemArrays {
        
        public int[][] _typedIntArray;
        
        public Object _untypedIntArray;
        
        public String[][] _typedStringArray;
        
        public Object _untypedStringArray;
        
        public Object[][] _objectArray;
        
        public Object[][] _stringObjectArray;
        
        public byte [][] _typedByteArray;
        
    }
    
    
    public static class Item {
        
        public String _name;
        
        public Item (String name){
            _name = name;
        }
        
        public boolean equals(Object obj) {
            
            if(! (obj instanceof Item)){
                return false;
            }
            
            Item other = (Item) obj;
            
            if(_name == null){
                return other._name == null;
            }
            
            return _name.equals(other._name);
        }
        
    }
    
    protected Object createArrays() {
        ItemArrays item = new ItemArrays();
        if(multiDimensionalArraysCantBeStored()){
            return item;
        }
        item._typedIntArray = intData2D;
        item._untypedIntArray = intData2D;
        item._typedStringArray = stringData2D;
        item._untypedStringArray = stringData2D;
        item._objectArray = objectData2D;
        item._stringObjectArray = stringObjectData2D;
        item._typedByteArray = byteData2D;
        return item;
    }
    
    
    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        if(multiDimensionalArraysCantBeStored()){
            return;
        }
        ItemArrays item = (ItemArrays) obj;
        assertAreEqual(intData2D, item._typedIntArray);
        assertAreEqual(intData2D, castToIntArray2D(item._untypedIntArray));
        assertAreEqual(stringData2D, item._typedStringArray);
        assertAreEqual(stringData2D, (String[][]) item._untypedStringArray);
        assertAreEqual(objectData2D, item._objectArray);
        assertAreEqual(objectData2D, item._objectArray);
        assertAreEqual(byteData2D, item._typedByteArray);
    }
    
    private boolean multiDimensionalArraysCantBeStored(){
        return PlatformInformation.isDotNet() && (db4oMajorVersion() < 6);
    }
    
    public static void assertAreEqual(int[][] expected, int[][] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            ArrayAssert.areEqual(expected[i], actual[i]);
        }
    }
    
    public static void assertAreEqual(String[][] expected, String[][] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            ArrayAssert.areEqual(expected[i], actual[i]);
        }
    }
    
    public static void assertAreEqual(Object[][] expected, Object[][] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            ArrayAssert.areEqual(expected[i], actual[i]);
        }
    }

    protected int[][] castToIntArray2D(Object obj){
        ObjectByRef byRef = new ObjectByRef(obj);
        correctIntArray2DJavaOnly(byRef);
        return (int[][]) byRef.value;
    }
    
    public static void assertAreEqual(byte[][] expected, byte[][] actual) {
        Assert.areEqual(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            ArrayAssert.areEqual(expected[i], actual[i]);
        }
    }

    
    /**
     * @sharpen.remove
     */
    protected void correctIntArray2DJavaOnly(ObjectByRef byRef){
        if(db4oHeaderVersion() == VersionServices.HEADER_30_40){
            
            // Bug in the oldest format: 
            // It accidentally converted int[][] arrays to Integer[][] arrays.
            
            Integer[][] wrapperArray = (Integer[][])byRef.value;
            int[][] res = new int[wrapperArray.length][];
            for (int i = 0; i < wrapperArray.length; i++) {
                res[i] = castToIntArray(wrapperArray[i]);
            }
            byRef.value = res;
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
        return "multidimensional_array";
    }
    
}
