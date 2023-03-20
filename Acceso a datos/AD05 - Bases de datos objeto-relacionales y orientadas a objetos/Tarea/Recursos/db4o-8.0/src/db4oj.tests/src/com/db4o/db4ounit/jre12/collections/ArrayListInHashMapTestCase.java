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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.test.*;

import db4ounit.extensions.*;

public class ArrayListInHashMapTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
	    public HashMap hashMap;
	}
    
    public void store(){
    	Item item = new Item();
        item.hashMap = new HashMap();
        ArrayList lOne = new ArrayList();
        lOne.add("OneOne");
        lOne.add("OneTwo");
        item.hashMap.put("One", lOne);
        ArrayList lTwo = new ArrayList();
        lTwo.add("TwoOne");
        lTwo.add("TwoTwo");
        lTwo.add("TwoThree");
        item.hashMap.put("Two", lTwo);
        store(item);
    }
    
    public void testOne(){
    	Item item = (Item) retrieveOnlyInstance(Item.class);
        ArrayList list = assertContent(item.hashMap);
        db().deactivate(list, Integer.MAX_VALUE);
        store(item.hashMap);
        db().activate(item, Integer.MAX_VALUE);
        assertContent(item.hashMap);
    }
    
    private ArrayList assertContent(HashMap hm){
        Test.ensure(hm.size() == 2);
        ArrayList lOne = (ArrayList)hm.get("One");
        Test.ensure(lOne.size() == 2);
        Test.ensure(lOne.get(0).equals("OneOne"));
        Test.ensure(lOne.get(1).equals("OneTwo"));
        ArrayList lTwo = (ArrayList)hm.get("Two");
        Test.ensure(lTwo.size() == 3);
        Test.ensure(lTwo.get(0).equals("TwoOne"));
        Test.ensure(lTwo.get(1).equals("TwoTwo"));
        Test.ensure(lTwo.get(2).equals("TwoThree"));
        return lOne;
    }


}
