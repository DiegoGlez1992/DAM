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

// Why is this duplicated in jdk1.2/jdk5?

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ObjectSetAsList {
    
    String name;
    
    public ObjectSetAsList(){
    }
    
    public ObjectSetAsList(String name_){
        name = name_;
    }
    
    public void store(){
        Test.deleteAllInstances(this);
        Test.store(new ObjectSetAsList("one"));
        Test.store(new ObjectSetAsList("two"));
        Test.store(new ObjectSetAsList("three"));
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(ObjectSetAsList.class);
        List list = q.execute();
        Test.ensure(list.size() == 3);
        Iterator i = list.iterator();
        boolean found = false;
        while(i.hasNext()){
            ObjectSetAsList osil = (ObjectSetAsList)i.next();
            if(osil.name.equals("two")){
                found = true;
            }
        }
        Test.ensure(found);
    }

	public void testAccessOrder() {
		Query query=Test.query();
		query.constrain(getClass());
		ObjectSet result=query.execute();
		Test.ensureEquals(3,result.size());
		for(int i=0;i<3;i++) {
			Test.ensure(result.get(i) == result.next());
		}
		Test.ensure(!result.hasNext());
	}
}
