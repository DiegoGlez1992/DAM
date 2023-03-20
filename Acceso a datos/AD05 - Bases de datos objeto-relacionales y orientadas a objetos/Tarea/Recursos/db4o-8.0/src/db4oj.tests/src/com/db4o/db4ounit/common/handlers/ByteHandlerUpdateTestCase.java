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

import com.db4o.*;
import com.db4o.db4ounit.util.*;
import com.db4o.ext.*;

import db4ounit.*;

public class ByteHandlerUpdateTestCase extends HandlerUpdateTestCaseBase {

    public static class Item {
        public byte _typedPrimitive;

        public Byte _typedWrapper;

        public Object _untyped;
    }

    public static class ItemArrays {
        public byte[] _typedPrimitiveArray;

        public Byte[] _typedWrapperArray;

        public Object[] _untypedObjectArray;

        public Object _primitiveArrayInObject;

        public Object _wrapperArrayInObject;
    }

    public static final byte[] data = new byte[] { Byte.MIN_VALUE,
            Byte.MIN_VALUE + 1, (byte) 0xFB, (byte) 0xFF, 0, 1, 5,
            Byte.MAX_VALUE - 1, Byte.MAX_VALUE, };

    public static void main(String[] args) {
        new ConsoleTestRunner(ByteHandlerUpdateTestCase.class).run();
    }

    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        ItemArrays itemArrays = (ItemArrays) obj;

        assertPrimitiveArray(itemArrays._typedPrimitiveArray);

        if (db4oHeaderVersion() == VersionServices.HEADER_30_40) {
            // Bug in the oldest format: It accidentally byte[] arrays to Byte[]
            // arrays.
            assertWrapperArray((Byte[]) itemArrays._primitiveArrayInObject);
        } else {
        	if(db4oHandlerVersion() == 1  && db4oHeaderVersion() == 100 && itemArrays._primitiveArrayInObject == null){
        		
        		// do nothing
        		
        		// We started treating byte[] in untyped variables differently
        		// but we forgot to update the handler version.
        		// Concerns only 6.3.500: Updates are not possible.
        		
        	}else{
        		assertPrimitiveArray((byte[])itemArrays._primitiveArrayInObject);
        	}
        }
        assertWrapperArray(itemArrays._typedWrapperArray);
        assertUntypedObjectArray(itemArrays);
        assertWrapperArray((Byte[]) itemArrays._wrapperArrayInObject);
    }

    /**
     * @sharpen.remove Cannot convert 'object[]' to 'Byte[]' in .net
     */
    private void assertUntypedObjectArray(ItemArrays itemArrays) {
        assertWrapperArray((Byte[]) itemArrays._untypedObjectArray);
    }

    private void assertPrimitiveArray(byte[] primitiveArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], primitiveArray[i]);
        }
    }

    /**
     * FIXME: The byte optimization format change, COR-884 also hits .NET for the 
     * wrapper array. Convert to Dotnet again after we install special handlers
     * for byte[] 
     * 
     * @sharpen.remove
     */
    private void assertWrapperArray(Byte[] wrapperArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(new Byte(data[i]), wrapperArray[i]);
        }
        // FIXME: Arrays should also get a null Bitmap to fix.
        // Assert.isNull(wrapperArray[wrapperArray.length - 1]);
    }

    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._typedPrimitive);
            assertAreEqual(new Byte(data[i]), item._typedWrapper);
            assertAreEqual(new Byte(data[i]), item._untyped);
        }

        Item nullItem = (Item) values[values.length - 1];
        assertAreEqual((byte) 0, nullItem._typedPrimitive);
        assertByteWrapperIsNullJavaOnly(nullItem._typedWrapper);
        Assert.isNull(nullItem._untyped);
    }

    private void assertAreEqual(byte expected, byte actual) {
        Assert.areEqual(expected, actual);
    }

    private void assertAreEqual(Object expected, Object actual) {
        Assert.areEqual(expected, actual);
    }

    protected Object createArrays() {
        ItemArrays itemArrays = new ItemArrays();
        itemArrays._typedPrimitiveArray = new byte[data.length];
        System.arraycopy(data, 0, itemArrays._typedPrimitiveArray, 0,
                data.length);

        Byte[] dataWrapper = new Byte[data.length];
        for (int i = 0; i < data.length; i++) {
            dataWrapper[i] = new Byte(data[i]);
        }

        itemArrays._typedWrapperArray = new Byte[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._typedWrapperArray, 0,
                dataWrapper.length);

        initializeUntypedObjectArray(itemArrays, dataWrapper);

        byte[] primitiveArray = new byte[data.length];
        System.arraycopy(data, 0, primitiveArray, 0, data.length);

        if(primitiveArrayInUntypedVariableSupported()){
        	itemArrays._primitiveArrayInObject = primitiveArray;
        }

        Byte[] wrapperArray = new Byte[data.length + 1];
        System.arraycopy(dataWrapper, 0, wrapperArray, 0, dataWrapper.length);
        if( primitiveArrayInUntypedVariableSupported() || ! Deploy.csharp){
        	itemArrays._wrapperArrayInObject = wrapperArray;
        }
        return itemArrays;
    }

	private boolean primitiveArrayInUntypedVariableSupported() {
    	// We can't convert primitive arrays in object variables in 6.3
    	// Keeping the member to null value helps to identify the exact version.

		return ! (db4oMajorVersion() == 6 && db4oMinorVersion() == 3);
	}

    /**
     * @sharpen.remove Cannot convert 'Byte[]' to 'object[]'
     */
    private void initializeUntypedObjectArray(ItemArrays itemArrays,
            Byte[] dataWrapper) {
        itemArrays._untypedObjectArray = new Byte[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._untypedObjectArray, 0,
                dataWrapper.length);
    }

    protected Object[] createValues() {
        Item[] values = new Item[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            Item item = new Item();
            item._typedPrimitive = data[i];
            item._typedWrapper = new Byte(data[i]);
            item._untyped = new Byte(data[i]);
            values[i] = item;
        }
        values[values.length - 1] = new Item();
        return values;
    }
    

    /**
     * @sharpen.remove
     */
    private void assertByteWrapperIsNullJavaOnly(Object obj){
        if(db4oHandlerVersion() == 0){
            
            // Bug when reading old format:
            // Null wrappers are converted to 0
            
            Assert.areEqual(new Byte((byte)0), obj);
        } else {
            Assert.isNull(obj);
        }
    }

    protected String typeName() {
        return "byte";
    }
}
