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
package com.db4o.db4ounit.jre5.collections;

import com.db4o.collections.*;
import com.db4o.db4ounit.common.ta.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


/**
 */
@decaf.Ignore
public class ArrayMap4TAMultiClientsTestCase extends TransparentActivationTestCaseBase
        implements OptOutSolo {

    public static void main(String[] args) {
        new ArrayMap4TAMultiClientsTestCase().runEmbedded();
    }
    
    private final ArrayMap4Operation<String, Integer> _clearOp = new ArrayMap4Operation<String, Integer>() {

        public void operate(ArrayMap4<String, Integer> map) {
            map.clear();
        }
    };
            
    private final ArrayMap4Operation<String, Integer> _putOp = new ArrayMap4Operation<String, Integer>() {

        public void operate(ArrayMap4<String, Integer> map) {
            map.put("10", Integer.valueOf(10 * 100));
        }
    }; 
    
    private final ArrayMap4Operation<String, Integer> _putAllOp = new ArrayMap4Operation<String, Integer>() {

        public void operate(ArrayMap4<String, Integer> map) {
            for (int i = 10; i < 50; i++) {
                map.put(String.valueOf(i), Integer.valueOf(i * 100));
            }
        }
    };
    
    private final ArrayMap4Operation<String, Integer> _putAllOp2 = new ArrayMap4Operation<String, Integer>() {

        public void operate(ArrayMap4<String, Integer> map) {
            for (int i = 50; i < 100; i++) {
                map.put(String.valueOf(i), Integer.valueOf(i * 100));
            }
        }
    }; 
    
    private final ArrayMap4Operation<String, Integer> _removeOp = new ArrayMap4Operation<String, Integer>() {

        public void operate(ArrayMap4<String, Integer> map) {
            map.remove("0");
        }
    }; 
    
    protected void store() throws Exception {
        ArrayMap4<String, Integer> map = new ArrayMap4<String, Integer>();
        ArrayMap4Asserter.putData(map);
        store(map);
    }

    private ArrayMap4<String, Integer> retrieveOnlyInstance(
            ExtObjectContainer db) {
        ArrayMap4<String, Integer> map = CollectionsUtil.retrieveMapFromDB(db);
        return map;
    }

    private ArrayMap4<String, Integer> retrieveOnlyInstance() {
        return CollectionsUtil.retrieveMapFromDB(db());
    }

    public void testClearClear() {
        operate(_clearOp, _clearOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        Assert.areEqual(0, map.size());
    }
    
    public void testClearPut() {
        operate(_clearOp, _putOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        Assert.areEqual(11, map.size());
        ArrayMap4Asserter.checkMap(map, 0, 11);
    }
    
    public void testClearRemove() {
        operate(_clearOp, _removeOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        Assert.areEqual(9, map.size());
        ArrayMap4Asserter.checkMap(map, 1, 10);
    }
    
    public void testClearGet() {
        ExtObjectContainer client1 = openNewSession();
        ExtObjectContainer client2 = openNewSession();
        ExtObjectContainer client3 = openNewSession();
        ExtObjectContainer client4 = openNewSession();
        ExtObjectContainer client5 = openNewSession();
        ExtObjectContainer client6 = openNewSession();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        ArrayMap4<String, Integer> map3 = retrieveOnlyInstance(client3);
        ArrayMap4<String, Integer> map4 = retrieveOnlyInstance(client4);
        ArrayMap4<String, Integer> map5 = retrieveOnlyInstance(client5);
        ArrayMap4<String, Integer> map6 = retrieveOnlyInstance(client6);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        map1.clear();
        ArrayMap4Asserter.assertContainsKey(map3);
        ArrayMap4Asserter.assertContainsValue(map4);
        ArrayMap4Asserter.assertEntrySet(map5);
        ArrayMap4Asserter.assertKeySet(map6);
        
        client1.store(map1);
        client2.store(map2);
        client3.store(map3);
        client4.store(map4);
        client5.store(map5);
        client6.store(map6);
        
        client1.close();
        client2.close();
        client3.close();
        client4.close();
        client5.close();
        client6.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        Assert.areEqual(10, map.size());
        ArrayMap4Asserter.checkMap(map, 0, 10);
    }
    
    @SuppressWarnings("unchecked")
    public void testClearClone() {
        ExtObjectContainer client1 = openNewSession();
        ExtObjectContainer client2 = openNewSession();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        map1.clear();
        ArrayMap4<String, Integer> clone = (ArrayMap4<String, Integer>) map2.clone();
        client1.store(map1);
        client2.delete(map2);
        client2.store(clone);
        client1.close();
        client2.close();
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        Assert.areEqual(10, map.size());
        ArrayMap4Asserter.checkMap(map, 0, 10);
    }
    
    public void testPutPut() {
        operate(_putOp, _putOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 11);
        Assert.isNull(map.get("11"));
    }
    
    public void testPutPutAll() {
        operate(_putOp, _putAllOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 50);
        Assert.isNull(map.get("100"));
    }
    
    public void testPutRemove() {
        operate(_putOp, _removeOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 1, 10);
        Assert.isNull(map.get("0"));
    }
    
    public void testPutClear() {
        operate(_putOp, _clearOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkClear(map);
    }
    
    public void testPutAllClear() {
        operate(_putAllOp, _clearOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkClear(map);
    }
    
    public void testPutAllPut() {
        operate(_putAllOp, _putOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 11);
    }
    
    public void testPutAllPutAll() {
        operate(_putAllOp2, _putAllOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 50);
    }
    
    public void testPutAllRemove() {
        operate(_putAllOp, _removeOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 1, 10);
    }
    
    public void testRemoveClear() {
        operate(_removeOp, _clearOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkClear(map);
    }
    
    public void testRemovePut() {
        operate(_removeOp, _putOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 11);
    }
    
    public void testRemovePutAll() {
        operate(_removeOp, _putAllOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 0, 50);
        Assert.isNull(map.get("100"));
    }
    
    public void testRemoveRemove() {
        operate(_removeOp, _removeOp);
        
        ArrayMap4<String, Integer> map = retrieveOnlyInstance();
        ArrayMap4Asserter.checkMap(map, 1, 10);
    }
    
    private void operate(ArrayMap4Operation<String, Integer> op1, ArrayMap4Operation<String, Integer> op2) {
        ExtObjectContainer client1 = openNewSession();
        ExtObjectContainer client2 = openNewSession();

        ArrayMap4<String, Integer> map1 = retrieveOnlyInstance(client1);
        ArrayMap4<String, Integer> map2 = retrieveOnlyInstance(client2);
        
        ArrayMap4Asserter.checkMap(map1, 0, 10);
        ArrayMap4Asserter.checkMap(map2, 0, 10);
        
        op1.operate(map1);
        op2.operate(map2);
        
        client1.store(map1);
        client2.store(map2);
        client1.close();
        client2.close();
    }
}
