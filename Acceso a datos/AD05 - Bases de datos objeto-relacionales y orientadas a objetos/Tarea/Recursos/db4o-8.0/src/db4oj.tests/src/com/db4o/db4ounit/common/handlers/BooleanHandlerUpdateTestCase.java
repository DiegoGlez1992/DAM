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

public class BooleanHandlerUpdateTestCase extends HandlerUpdateTestCaseBase {

    public static class Item {
        public boolean _typedPrimitive;

        public Boolean _typedWrapper;

        public Object _untyped;
    }

    public static class ItemArrays {
        public boolean[] _typedPrimitiveArray;

        public Boolean[] _typedWrapperArray;

        public Object[] _untypedObjectArray;

        public Object _primitiveArrayInObject;

        public Object _wrapperArrayInObject;
    }

    private static final boolean[] data = new boolean[] { true, false, };

    public static void main(String[] args) {
        new ConsoleTestRunner(BooleanHandlerUpdateTestCase.class).run();
    }

    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        ItemArrays itemArrays = (ItemArrays) obj;

        assertPrimitiveArray(itemArrays._typedPrimitiveArray);
        if (db4oHeaderVersion() == VersionServices.HEADER_30_40) {
            // Bug in the oldest format: It accidentally boolean[] arrays to
            // Boolean[] arrays.
            assertWrapperArray((Boolean[]) itemArrays._primitiveArrayInObject);
        } else {
            assertPrimitiveArray((boolean[]) itemArrays._primitiveArrayInObject);
        }
        assertWrapperArray(itemArrays._typedWrapperArray);
        assertUntypedObjectArray(itemArrays);
        assertWrapperArray((Boolean[]) itemArrays._wrapperArrayInObject);
    }

    /**
     * @sharpen.remove Cannot convert 'object[]' to 'Boolean[]' in .net
     */
    private void assertUntypedObjectArray(ItemArrays itemArrays) {
        assertWrapperArray((Boolean[]) itemArrays._untypedObjectArray);
    }

    private void assertPrimitiveArray(boolean[] primitiveArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], primitiveArray[i]);
        }
    }

    private void assertWrapperArray(Boolean[] wrapperArray) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(new Boolean(data[i]), wrapperArray[i]);
        }
        // FIXME: Arrays should also get a null Bitmap to fix.
        // Assert.isNull(wrapperArray[wrapperArray.length - 1]);
    }

    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._typedPrimitive);
            assertAreEqual(new Boolean(data[i]), item._typedWrapper);
            assertAreEqual(new Boolean(data[i]), item._untyped);
        }

        Item nullItem = (Item) values[values.length - 1];
        assertAreEqual(false, nullItem._typedPrimitive);
        assertPrimitiveWrapperIsNullJavaOnly(nullItem._typedWrapper);
        Assert.isNull(nullItem._untyped);
    }

    private void assertAreEqual(boolean expected, boolean actual) {
        Assert.areEqual(expected, actual);
    }

    private void assertAreEqual(Object expected, Object actual) {
        Assert.areEqual(expected, actual);
    }

    protected Object createArrays() {
        ItemArrays itemArrays = new ItemArrays();
        itemArrays._typedPrimitiveArray = new boolean[data.length];
        System.arraycopy(data, 0, itemArrays._typedPrimitiveArray, 0,
                data.length);

        Boolean[] dataWrapper = new Boolean[data.length];
        for (int i = 0; i < data.length; i++) {
            dataWrapper[i] = new Boolean(data[i]);
        }

        itemArrays._typedWrapperArray = new Boolean[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._typedWrapperArray, 0,
                dataWrapper.length);

        initializeUntypedObjectArray(itemArrays, dataWrapper);

        boolean[] primitiveArray = new boolean[data.length];
        System.arraycopy(data, 0, primitiveArray, 0, data.length);
        itemArrays._primitiveArrayInObject = primitiveArray;

        Boolean[] wrapperArray = new Boolean[data.length + 1];
        System.arraycopy(dataWrapper, 0, wrapperArray, 0, dataWrapper.length);
        itemArrays._wrapperArrayInObject = wrapperArray;
        return itemArrays;
    }

    /**
     * @sharpen.remove Cannot convert 'Boolean[]' to 'object[]'
     */
    private void initializeUntypedObjectArray(ItemArrays itemArrays,
            Boolean[] dataWrapper) {
        itemArrays._untypedObjectArray = new Boolean[data.length + 1];
        System.arraycopy(dataWrapper, 0, itemArrays._untypedObjectArray, 0,
                dataWrapper.length);
    }

    protected Object[] createValues() {
        Item[] values = new Item[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            Item item = new Item();
            item._typedPrimitive = data[i];
            item._typedWrapper = new Boolean(data[i]);
            item._untyped = new Boolean(data[i]);
            values[i] = item;
        }
        values[values.length - 1] = new Item();
        return values;
    }

    protected String typeName() {
        return "boolean";
    }

}
