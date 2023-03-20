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

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;

public class ObjectSetIDs {
	
	static final int COUNT = 11;
	
	public void store(){
		Test.deleteAllInstances(this);
		for (int i = 0; i < COUNT; i++) {
			Test.store(new ObjectSetIDs());
        }
	}
	
	public void test(){
		ExtObjectContainer con = Test.objectContainer();
		Query q = Test.query();
		q.constrain(this.getClass());
		ObjectSet res = q.execute();
		long[] ids1 = new long[res.size()];
		int i =0;
		while(res.hasNext()){
			final long id = con.getID(res.next());
			Assert.areNotEqual(0, id);
			ids1[i++]=id;
		}
		Assert.areEqual(res.size(), i);
		
		//res.reset();
		long[] ids2 = res.ext().getIDs();
		
		Assert.areEqual(COUNT, ids1.length);
		Assert.areEqual(COUNT, ids2.length);
		
		for (int j = 0; j < ids1.length; j++) {
			final long expected = ids1[j];
			ArrayAssert.contains(ids2, expected);
        }
	}	
	
	public static void main(String[] args) {
		AllTests.run(ObjectSetIDs.class);
	}
}
