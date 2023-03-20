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
package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class NoTestConstructorsQEStringCmpTestCase extends AbstractDb4oTestCase implements OptOutAllButNetworkingCS {

	public static class Item {
		public String _name;
		
		public Item(String name) {
			_name = name;
		}
	}
	
	private interface ConstraintModifier {
		void modify(Constraint constraint);
	}
	
	protected void configure(Configuration config) throws Exception {
		config.callConstructors(true);
		config.testConstructors(false);
	}
	
	protected void store() throws Exception {
		store(new Item("abc"));
	}
	
	public void testStartsWith() {
		assertSingleItem("a", new ConstraintModifier() {
			public void modify(Constraint constraint) {
				constraint.startsWith(false);
			}
		});
	}

	public void testEndsWith() {
		assertSingleItem("c", new ConstraintModifier() {
			public void modify(Constraint constraint) {
				constraint.endsWith(false);
			}
		});
	}

	public void testContains() {
		assertSingleItem("b", new ConstraintModifier() {
			public void modify(Constraint constraint) {
				constraint.contains();
			}
		});
	}

	private void assertSingleItem(String pattern, ConstraintModifier modifier) {
		Query query = baseQuery(pattern, modifier);
		ObjectSet result = query.execute();
		Assert.areEqual(1, result.size());
	}
	
	private Query baseQuery(String pattern, ConstraintModifier modifier) {
		Query query = newQuery();
		query.constrain(Item.class);
		Constraint constraint = query.descend("_name").constrain(pattern);
		modifier.modify(constraint);
		return query;
	}

	public static void main(String[] args) {
		new NoTestConstructorsQEStringCmpTestCase().runNetworking();
	}
}
