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

import com.db4o.*;
import com.db4o.query.*;


/**
 * 
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class QueryForList {
    
    List _list;
    
    public void storeOne(){
        _list = new QueryForListArrayList();
        _list.add("hi");
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(List.class);
        ObjectSet objectSet = q.execute();
        int found = 0;
        while(objectSet.hasNext()){
            Object obj = objectSet.next();
            Test.ensure(obj instanceof List);
            List list = (List)obj;
            if(list instanceof QueryForListArrayList){
                found++;
                Test.ensure(list.get(0).equals("hi"));
            }
        }
        Test.ensure(found == 1);
    }
    
    static class QueryForListArrayList extends ArrayList{
    }

}
