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
import com.db4o.query.*;

public class QueryDeleted {
	
	public String name;
	
	public QueryDeleted(){
	}
	
	public QueryDeleted(String name){
		this.name = name;
	}
	
	public void store(){
		Test.deleteAllInstances(this);
		Test.store(new QueryDeleted("one"));
		Test.store(new QueryDeleted("two"));
	}
	
	public void test(){
		Query q = Test.query();
		q.constrain(QueryDeleted.class);
		q.descend("name").constrain("one");
		QueryDeleted qd = (QueryDeleted)q.execute().next();
		Test.delete(qd);
		checkCount(1);
		Test.rollBack();
		checkCount(2);
		Test.delete(qd);
		checkCount(1);
		Test.commit();
		checkCount(1);
	}
	
	private void checkCount(int count){
	    Query q = Test.query();
	    q.constrain(QueryDeleted.class);
	    ObjectSet res = q.execute();
	    Test.ensure(res.size() == count);
	}
	
	
}
