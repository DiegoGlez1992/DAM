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

import com.db4o.*;
import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeDeleteSubClassTestCase extends AbstractDb4oTestCase {
	
	public static class Member{
		
		public String _name;
		
		public Member(String name){
			_name = name;
		}
		
	}
	
	public static class SuperClass{
		
		public Member _superClassMember; 
		
	}
	
	public static class SubClass extends SuperClass{
		
		public Member _subClassMember;
		
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(SubClass.class).cascadeOnDelete(true);
	}
	
	@Override
	protected void store() throws Exception {
		SubClass subClass = new SubClass();
		subClass._superClassMember = new Member("_superClassMember");
		subClass._subClassMember = new Member("_subClassMember");
		store(subClass);
	}
	
	public void test(){
		SubClass subClass = retrieveOnlyInstance(SubClass.class);
		db().delete(subClass);
		db().commit();
		ObjectSet objectSet = db().query(Member.class);
		Assert.areEqual(0, objectSet.size());
	}

}
