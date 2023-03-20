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
package com.db4o.db4ounit.common.ta.mixed;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.db4ounit.common.ta.ItemTestCaseBase;
import com.db4o.query.Query;
import com.db4o.ta.Activatable;

import db4ounit.Assert;

public class MixedActivateTestCase extends ItemTestCaseBase {

    private final int ITEM_DEPTH = 10;

    public static void main(String[] args) {
        new MixedActivateTestCase().runAll();
    }

    protected void assertItemValue(Object obj) throws Exception {
        assertActivatedItemByMethod((Item) obj, ITEM_DEPTH);
    }

    void assertActivatedItemByMethod(Item item, int level) {
        for (int i = 0; i < ITEM_DEPTH; i++) {
            Assert.areEqual("Item " + (ITEM_DEPTH - i), item.getName());
            Assert.areEqual(ITEM_DEPTH - i, item.getValue());
            if (i < ITEM_DEPTH - 1) {
                Assert.isNotNull(item.next());
            } else {
                Assert.isNull(item.next());
            }
            item = item.next();
        }
    }

    protected void assertRetrievedItem(Object obj) throws Exception {
        Item item = (Item) obj;
        for (int i = 0; i < ITEM_DEPTH; i++) {
            assertNullItem(item, ITEM_DEPTH - i);
            item = item.next();
        }
    }
    
    private void assertNullItem(Item item, int level) {
        if (level % 2 == 0) {
            Assert.isNull(item._name);
            Assert.isNull(item._next);
            Assert.areEqual(0, item._value);
        } else {
            Assert.areEqual("Item " + level, item._name);
            Assert.areEqual(level, item._value);
            if (level == 1) {
                Assert.isNull(item._next);
            } else {
                Assert.isNotNull(item._next);
            }
        }
    }

    protected Object createItem() throws Exception {
        TAItem item = TAItem.newTAITem(10);
        item._isRoot = true;

        return item;
    }

    public void testActivate() {
        Item item = (Item) retrieveOnlyInstance(TAItem.class);
        Assert.isNull(item._name);
        Assert.isNull(item._next);
        Assert.areEqual(0, item._value);
        // depth = 0;
        db().activate(item, 0);
        Assert.isNull(item._name);
        Assert.isNull(item._next);
        Assert.areEqual(0, item._value);

        // depth = 1;
        // item.next();
        db().activate(item, 1);
        assertActivatedItemByField(item, 1);

        db().activate(item, 5);
        assertActivatedItemByField(item, 5);

        db().activate(item, 10);
        assertActivatedItemByField(item, 10);
    }

    void assertActivatedItemByField(Item item, int level) {
        for (int i = 0; i < level; i++) {
            Assert.areEqual("Item " + (ITEM_DEPTH - i), item._name);
            Assert.areEqual(ITEM_DEPTH - i, item._value);

            if (i < ITEM_DEPTH - 1) {
                Assert.isNotNull(item._next);
            } else {
                Assert.isNull(item._next);
            }
            item = item._next;
        }
        if (level < ITEM_DEPTH) {
            Assert.isNull(item._name);
            Assert.isNull(item._next);
            Assert.areEqual(0, item._value);
        }
    }

    public Object retrieveOnlyInstance(Class clazz) {
        Query q = db().query();
        q.constrain(clazz);
        q.descend("_isRoot").constrain(new Boolean(true));
        return q.execute().next();
    }

    public static class Item {

        public String _name;

        public int _value;

        public Item _next;

        public boolean _isRoot;

        public Item() {
            //
        }

        public Item(String name, int value) {
            _name = name;
            _value = value;
        }

        public static Item newItem(int depth) {
            if (depth == 0) {
                return null;
            }
            Item header = new Item("Item " + depth, depth);
            header._next = TAItem.newTAITem(depth - 1);
            return header;
        }

        public String getName() {
            return _name;
        }

        public int getValue() {
            return _value;
        }

        public Item next() {
            return _next;
        }

    }

    public static class TAItem extends Item implements Activatable {

        private transient Activator _activator;

        public TAItem(String name, int value) {
            super(name, value);
        }

        public static TAItem newTAITem(int depth) {
            if (depth == 0) {
                return null;
            }
            TAItem header = new TAItem("Item " + depth, depth);
            header._next = Item.newItem(depth - 1);
            return header;
        }

        public String getName() {
            activate(ActivationPurpose.READ);
            return _name;
        }

        public int getValue() {
            activate(ActivationPurpose.READ);
            return _value;
        }

        public Item next() {
            activate(ActivationPurpose.READ);
            return _next;
        }

        public void activate(ActivationPurpose purpose) {
            if (_activator == null) return;
            _activator.activate(purpose);
        }

        public void bind(Activator activator) {
            _activator = activator;
        }
    }


}
