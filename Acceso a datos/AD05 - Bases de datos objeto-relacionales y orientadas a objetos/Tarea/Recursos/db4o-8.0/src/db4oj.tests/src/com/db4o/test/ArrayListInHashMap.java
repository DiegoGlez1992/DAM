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
package com.db4o.test;

import java.util.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ArrayListInHashMap {
    
    public HashMap hm;
    
    public void storeOne(){
        hm = new HashMap();
        ArrayList lOne = new ArrayList();
        lOne.add("OneOne");
        lOne.add("OneTwo");
        hm.put("One", lOne);
        ArrayList lTwo = new ArrayList();
        lTwo.add("TwoOne");
        lTwo.add("TwoTwo");
        lTwo.add("TwoThree");
        hm.put("Two", lTwo);
    }
    
    public void testOne(){
        ArrayList lOne = tContent();
        Test.objectContainer().deactivate(lOne, Integer.MAX_VALUE);
        Test.store(hm);
        Test.objectContainer().activate(this, Integer.MAX_VALUE);
        tContent();
    }
    
    private ArrayList tContent(){
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


