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
package com.db4o.db4ounit.common.soda.ordered;

import com.db4o.query.*;

import db4ounit.extensions.*;

/**
 * @exclude
 */
public class OrderByParentFieldTestCase extends AbstractDb4oTestCase {
	
	public static class Parent {
		
		public String _name;

		public Parent(String name) {
			_name = name;
		}
		
	}
	
	public static class Child extends Parent {
		
		public int _age;
		
		public Child(String name, int age) {
			super(name);
			_age = age;
		}
		
	}
	
	@Override
	protected void store() throws Exception {
		store(new Child("One", 1));
		store(new Child("Two", 2));
	}
	
	public void test() throws Exception{
		Query query = newQuery(Child.class);
		query.descend("_name").orderAscending();
		query.execute();
	}

}
