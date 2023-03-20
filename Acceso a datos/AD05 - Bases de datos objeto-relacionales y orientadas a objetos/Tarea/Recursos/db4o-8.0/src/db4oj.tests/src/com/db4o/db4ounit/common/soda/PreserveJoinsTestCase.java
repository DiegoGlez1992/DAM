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
package com.db4o.db4ounit.common.soda;

import com.db4o.query.Constraint;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class PreserveJoinsTestCase extends AbstractDb4oTestCase {
	
	public static class Parent {
	
		public Parent(Child child, String value) {
			this.child = child;
			this.value = value;
		}

		public Child child;		
		public String value;
	}
	
	public static class Child {
		
		public Child(String name) {
			this.name = name;
		}

		public String name;
		
	}
	
	@Override
	protected void store() throws Exception {
		store(new Parent(new Child("bar"), "parent"));
	}
	
	public void test() {
		
		Query barQuery = db().query();
		barQuery.constrain(Child.class);
		barQuery.descend("name").constrain("bar");
		Object barObj = barQuery.execute().next();
		
		Query query = db().query();
		query.constrain(Parent.class);
		Constraint c1 = query.descend("value").constrain("dontexist");
		Constraint c2 = query.descend("child").constrain(barObj);
		Constraint c1_and_c2 = c1.and(c2);
		
		Constraint cParent = query.descend("value").constrain("parent");
		c1_and_c2.or(cParent);
		
		Assert.areEqual(1, query.execute().size());		
	}

}
