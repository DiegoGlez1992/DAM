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
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class CollectionSubQuery {
	private final static String ID="X";
	
	public static class Data {
		public String id;

		public Data(String id) {
			this.id = id;
		}
	}
	
	public List list;
	
    public void storeOne(){
    	this.list=new ArrayList();
    	this.list.add(new Data(ID));
    }
    
    public void test(){
        Query q = Test.query();
        q.constrain(CollectionSubQuery.class);
        Query sub=q.descend("list");
        // Commenting out this constraint doesn't effect behavior
        sub.constrain(Data.class);
        // If this subsub constraint is commented out, the result
        // contains a Data instance as expected. With this constraint,
        // we get the containing ArrayList.
        Query subsub=sub.descend("id");
        subsub.constrain(ID);
        ObjectSet result=sub.execute();
        Test.ensure(result.size()==1);
        Test.ensure(result.next().getClass()==Data.class);
    }
    
    public static void main(String[] args) {
		AllTests.run(CollectionSubQuery.class);
	}
}
