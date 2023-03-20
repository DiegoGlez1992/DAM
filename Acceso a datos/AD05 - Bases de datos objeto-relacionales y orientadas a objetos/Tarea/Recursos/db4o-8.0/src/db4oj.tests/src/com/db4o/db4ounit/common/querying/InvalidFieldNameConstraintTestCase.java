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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class InvalidFieldNameConstraintTestCase extends AbstractDb4oTestCase {

	public static class Person {
		public String _firstName;
		public String _lastName;
		
		public Person(String firstName, String lastName) {
			_firstName = firstName;
			_lastName = lastName;
		}
	}

	@Override
	protected void configure(Configuration config) throws Exception {
		config.blockSize(8);
		config.objectClass(Person.class).objectField("_firstName").indexed(true);
		config.objectClass(Person.class).objectField("_lastName").indexed(true);
		config.add(new TransparentActivationSupport());
		
	}
	
	@Override
	protected void store() throws Exception {
		store(new Person("John", "Doe"));
	}

	public void testQuery() {
		Query query = newQuery(Person.class);
		query.descend("_nonExistent").constrain("X");
		ObjectSet<Person> result = query.execute();
		Assert.areEqual(0, result.size());
	}
}
