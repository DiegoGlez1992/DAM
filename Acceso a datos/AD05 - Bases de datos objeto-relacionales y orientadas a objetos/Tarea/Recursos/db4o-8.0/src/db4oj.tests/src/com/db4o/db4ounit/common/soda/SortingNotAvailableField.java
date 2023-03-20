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
package com.db4o.db4ounit.common.soda;

import com.db4o.ObjectSet;
import com.db4o.query.Query;
import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class SortingNotAvailableField extends AbstractDb4oTestCase{

    public static void main(String[] args) {
        new SortingNotAvailableField().runSolo();
    }
    @Override
    protected void store() throws Exception {
        super.store();
        db().store(new OrderedItem());
        db().store(new OrderedItem());
    }

    public void testOrderWithRightFieldName(){
        final Query query = db().query();
        query.constrain(OrderedItem.class);
        query.descend("myOrder").orderAscending();

        final ObjectSet<Object> result = query.execute();
        Assert.areEqual(2,result.size());
    }

    public void testOrderWithWrongFieldName(){
        final Query query = db().query();
        query.constrain(OrderedItem.class);
        query.descend("myorder").orderAscending();

        final ObjectSet<Object> result = query.execute();
        Assert.areEqual(2,result.size());
    }
    
    public static class OrderedItem {
        private int myOrder = 42;
    }
}


