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
package com.db4o.db4ounit.common.querying;

import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class IndexOnParentFieldTestCase extends AbstractDb4oTestCase{
	
	public static class Parent {
		
		public Parent(String name) {
			_name = name;
		}

		public String _name;
		
	}
	
	public static class Child extends Parent {

		public Child(String name) {
			super(name);
		}
		
	}
	
	protected void store() {
		store(new Parent("one"));
		store(new Child("one"));
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Parent.class).objectField("_name").indexed(true);
	}
	
	public void test(){
		Query q = newQuery();
		q.constrain(Child.class);
		q.descend("_name").constrain("one");
		Assert.areEqual(1, q.execute().size());
	}

}
