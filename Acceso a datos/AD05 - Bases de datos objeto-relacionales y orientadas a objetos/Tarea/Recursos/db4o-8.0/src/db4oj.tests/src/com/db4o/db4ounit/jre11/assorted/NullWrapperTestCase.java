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
package com.db4o.db4ounit.jre11.assorted;

import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class NullWrapperTestCase extends AbstractDb4oTestCase {
    
    static final int USE_AS_NULL = -9999;
    
    final int[] VALUES = new int[] {1, 2, USE_AS_NULL, 5, USE_AS_NULL, 7, USE_AS_NULL}; 
    
    public static void main(String[] args) {
        new NullWrapperTestCase().runSolo();
    }
    
    protected void configure(Configuration config) {
        config.objectClass(NullWrapperItem.class).objectField(NullWrapperItem.INTEGER_FIELDNAME).indexed(true);
    }
    
    public void test() throws Exception{
        for (int i = 0; i < VALUES.length; i++) {
            Integer integer = VALUES[i] == USE_AS_NULL ? null : new Integer(VALUES[i]);
            db().store(new NullWrapperItem(integer));
        }
        reopen();
        
        Query q = newQuery();
        q.constrain(NullWrapperItem.class);
        q.descend(NullWrapperItem.INTEGER_FIELDNAME).constrain(null);
        Assert.areEqual(3, q.execute().size());
    }

}
