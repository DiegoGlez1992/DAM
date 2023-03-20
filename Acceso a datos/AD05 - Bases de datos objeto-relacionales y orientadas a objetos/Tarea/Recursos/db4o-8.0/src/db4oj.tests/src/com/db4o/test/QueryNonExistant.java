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

public class QueryNonExistant {
	
	QueryNonExistant1 member;
	
	public QueryNonExistant(){
		// db4o constructor
	}
	
	public QueryNonExistant(boolean createMembers){
		member = new QueryNonExistant1();
		member.member = new QueryNonExistant2();
		member.member.member = this;
		// db4o constructor
	}
	
	public void test(){
		ObjectContainer con = Test.objectContainer(); 
		con.queryByExample((new QueryNonExistant(true)));
		Test.ensureOccurrences(new QueryNonExistant(), 0);
		Query q = con.query();
		q.constrain(new QueryNonExistant(true));
		Test.ensure(q.execute().size() == 0);
	}
	
	public static class QueryNonExistant1{
		QueryNonExistant2 member;
	}
	
	public static class QueryNonExistant2 extends QueryNonExistant1{
		QueryNonExistant member;
	}
	
}
