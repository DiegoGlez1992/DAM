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
package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore
public class NamedArrayListTypeHandlerTestCase extends AbstractDb4oTestCase{
    
    private static String NAME = "listname";
    
    private static Object[] DATA = new Object[] {
        "one", "two", new Integer(3), new Long(4), null  
    };
    
    protected void store() throws Exception {
        store(createNamedArrayList());
    }
    
    private NamedArrayList createNamedArrayList(){
        NamedArrayList namedArrayList = new NamedArrayList();
        namedArrayList.name = NAME;
        for (int i = 0; i < DATA.length; i++) {
            namedArrayList.add(DATA[i]); 
        }
        return namedArrayList;
    }
    
    private void assertRetrievedOK(NamedArrayList namedArrayList){
        Assert.areEqual(NAME, namedArrayList.name);
        Object[] listElements = new Object[namedArrayList.size()];
        int idx =  0;
        Iterator i = namedArrayList.iterator();
        while(i.hasNext()){
            listElements[idx++] = i.next();
        }
        ArrayAssert.areEqual(DATA, listElements);
    }
    
    
    public void testRetrieve(){
        NamedArrayList namedArrayList = (NamedArrayList) retrieveOnlyInstance(NamedArrayList.class);
        assertRetrievedOK(namedArrayList);
    }
    
    public void testQuery() {
        Query query = newQuery(NamedArrayList.class);
        query.descend("name").constrain(NAME);
        ObjectSet objectSet = query.execute();
        Assert.areEqual(1, objectSet.size());
        NamedArrayList namedArrayList = (NamedArrayList) objectSet.next();
        assertRetrievedOK(namedArrayList);
    }

}
