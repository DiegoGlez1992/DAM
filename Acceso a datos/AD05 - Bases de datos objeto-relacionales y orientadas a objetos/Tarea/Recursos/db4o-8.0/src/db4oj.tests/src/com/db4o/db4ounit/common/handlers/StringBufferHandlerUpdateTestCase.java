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


public class StringBufferHandlerUpdateTestCase extends
        HandlerUpdateTestCaseBase {

    private static final StringBuffer[] data = new StringBuffer[] { new StringBuffer("one"), //$NON-NLS-1$
            new StringBuffer("aAzZ\u05d0\u05d1\u4e2d"), //$NON-NLS-1$
            new StringBuffer(""), //$NON-NLS-1$
            null,
    };

    public static class Item {
        public StringBuffer _typed;

        public Object _untyped;
    }

    public static class ItemArrays {
        public StringBuffer[] _typedArray;

        public Object[] _untypedArray;

        public Object _arrayInObject;
    }

    public static void main(String[] args) {
        new ConsoleTestRunner(StringBufferHandlerUpdateTestCase.class).run();
    }
    protected void assertArrays(ExtObjectContainer objectContainer, Object obj) {
        ItemArrays item = (ItemArrays) obj;
        assertTypedArray(item);
        assertUntypedArray(item);
        assertArrayInObject(item);
    }

    private void assertArrayInObject(ItemArrays item) {
        assertData((StringBuffer[]) item._arrayInObject);
    }

    private void assertUntypedArray(ItemArrays item) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], (StringBuffer)item._untypedArray[i]);
        }
        Assert.isNull(item._untypedArray[item._untypedArray.length - 1]);
    }
    
    private void assertTypedArray(ItemArrays item) {
        assertData(item._typedArray);
    }

    private void assertData(StringBuffer[] values) {
        for (int i = 0; i < data.length; i++) {
            assertAreEqual(data[i], values[i]);
        }
    }
    protected void assertValues(ExtObjectContainer objectContainer, Object[] values) {
        for (int i = 0; i < data.length; i++) {
            Item item = (Item) values[i];
            assertAreEqual(data[i], item._typed);
            assertAreEqual(data[i], (StringBuffer)item._untyped);
        }
        Item nullItem = (Item) values[values.length - 1];
        Assert.isNull(nullItem._typed);
        Assert.isNull(nullItem._untyped);
    }

    private void assertAreEqual(StringBuffer expected, StringBuffer actual) {
        String expectedString = (expected == null) ? null : expected.toString();
        String actualString = (actual == null) ? null : actual.toString();
        Assert.areEqual(expectedString, actualString);
    }
    protected Object createArrays() {
        ItemArrays item = new ItemArrays();
        createTypedArray(item);
        createUntypedArray(item);
        createArrayInObject(item);
        return item;
    }

    private void createArrayInObject(ItemArrays item) {
        StringBuffer[] stringBufferArray = new StringBuffer[data.length];
        for (int i = 0; i < data.length; i++) {
            stringBufferArray[i] = data[i];
        }
        item._arrayInObject = stringBufferArray;
    }

    private void createUntypedArray(ItemArrays item) {
        item._untypedArray = new StringBuffer[data.length + 1];
        for (int i = 0; i < data.length; i++) {
            item._untypedArray[i] = data[i];
        }
    }

    private void createTypedArray(ItemArrays item) {
        item._typedArray = new StringBuffer[data.length];
        for (int i = 0; i < data.length; i++) {
            item._typedArray[i] = data[i];
        }
    }

    protected Object[] createValues() {
        Item[] items = new Item[data.length + 1];
        
        for (int i = 0; i < data.length; i++) {
            Item item = new Item();
            item._typed = data[i];
            item._untyped = data[i];
            items[i] = item;
        }
        
        items[items.length - 1] = new Item();
        return items;
    }

    protected String typeName() {
        return "StringBuffer"; //$NON-NLS-1$
    }

}
