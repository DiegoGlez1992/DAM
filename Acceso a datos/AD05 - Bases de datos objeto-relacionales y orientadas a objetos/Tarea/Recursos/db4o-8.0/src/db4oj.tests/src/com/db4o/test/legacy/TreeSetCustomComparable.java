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
package com.db4o.test.legacy;

import java.util.*;

import com.db4o.*;
import com.db4o.test.*;

/**
 * 
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TreeSetCustomComparable implements Comparable{
    
    public Set path;

    public TreeSetCustomComparable() {
      this.path = new TreeSet();
    }

    public int compareTo(Object that) {
      return hashCode()-that.hashCode();
    }
    
    public void store(){
        Test.deleteAllInstances(TreeMap.class);
        Map map=new TreeMap();
        map.put(new TreeSetCustomComparable(),new TreeSet());
        Test.objectContainer().store(map);
    }
    
    public void test(){
        TreeMap map=new TreeMap();
        ObjectSet result=Test.objectContainer().queryByExample(map);
        while(result.hasNext()) {
            TreeMap tm = (TreeMap)result.next();
            Test.ensure(tm.size() == 1);
            Iterator i = tm.keySet().iterator();
            Test.ensure(i.hasNext());
            TreeSetCustomComparable tscc = (TreeSetCustomComparable)i.next();
            TreeSet ts = (TreeSet)tm.get(tscc);
            Test.ensure(ts != null);
        }
    }
}
