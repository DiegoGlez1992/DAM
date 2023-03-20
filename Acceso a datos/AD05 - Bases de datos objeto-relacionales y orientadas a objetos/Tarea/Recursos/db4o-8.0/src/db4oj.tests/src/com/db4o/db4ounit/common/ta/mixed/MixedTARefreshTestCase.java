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

import com.db4o.activation.*;
import com.db4o.db4ounit.common.ta.*;
import com.db4o.ext.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;

public class MixedTARefreshTestCase extends TransparentActivationTestCaseBase
        implements OptOutSolo {

    public static void main(String[] args) {
        new MixedTARefreshTestCase().runNetworking();
    }
    
    private static final int ITEM_DEPTH = 10;

    protected void store() throws Exception {
        Item item = TAItem.newItem(ITEM_DEPTH);
        item._isRoot = true;
        store(item);
    }
    
    public void testRefresh() {
        ExtObjectContainer client1 = openNewSession();
        ExtObjectContainer client2 = openNewSession();
        Item item1 = retrieveInstance(client1);
        Item item2 = retrieveInstance(client2);

        Item next1 = item1;
        int value = 10;
        while (next1 != null) {
            Assert.areEqual(value, next1.getValue());
            next1 = next1.next();
            value --;
        }
        
        Item next2 = item2;
        value = 10;
        while (next2 != null) {
            Assert.areEqual(value, next2.getValue());
            next2 = next2.next();
            value --;
        }
        
        item1.setValue(100);
        item1.next().setValue(200);
        client1.store(item1, 2);
        client1.commit();
        
        Assert.areEqual(100, item1.getValue());
        Assert.areEqual(200, item1.next().getValue());
        
        Assert.areEqual(10, item2.getValue());
        Assert.areEqual(9, item2.next().getValue());
        
        //refresh 0
        client2.refresh(item2, 0);
        Assert.areEqual(10, item2.getValue());
        Assert.areEqual(9, item2.next().getValue());
        
        //refresh 1
        client2.refresh(item2, 1);
        Assert.areEqual(100, item2.getValue());
        Assert.areEqual(9, item2.next().getValue());
        
        //refresh 2
        client2.refresh(item2, 2);
        Assert.areEqual(100, item2.getValue());
        //FIXME: maybe a bug
        //Assert.areEqual(200, item2.next().getValue());
        
        next1 = item1;
        value = 1000;
        while (next1 != null) {
            next1.setValue(value);
            next1 = next1.next();
            value++;
        }
        client1.store(item1, 5);
        client1.commit();
        
        client2.refresh(item2, 5);
        next2 = item2;
        for (int i = 1000; i < 1005; i++) {
            Assert.areEqual(i, next2.getValue());
            next2 = next2.next();
        }
        
        client1.close();
        client2.close();
    }

    private Item retrieveInstance(ExtObjectContainer client) {
        Query query = client.query();
        query.constrain(Item.class);
        query.descend("_isRoot").constrain(new Boolean(true));
        return (Item)query.execute().next();
    }

    public static class Item {

        public int _value;

        public Item _next;

        public boolean _isRoot;

        public Item() {
            //
        }

        public Item(int value) {
            _value = value;
        }

        public static Item newItem(int depth) {
            if (depth == 0) {
                return null;
            }
            Item header = new Item(depth);
            header._next = TAItem.newTAITem(depth - 1);
            return header;
        }

        public int getValue() {
            return _value;
        }

        public void setValue(int value) {
            _value = value;
        }
        
        public Item next() {
            return _next;
        }

    }

    public static class TAItem extends Item implements Activatable {

        private transient Activator _activator;

        public TAItem(int value) {
            super(value);
        }

        public static TAItem newTAITem(int depth) {
            if (depth == 0) {
                return null;
            }
            TAItem header = new TAItem(depth);
            header._next = Item.newItem(depth - 1);
            return header;
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
