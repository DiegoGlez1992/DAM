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
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class StringIndexWithSuperClassTestCase extends AbstractDb4oTestCase {

	private static final String FIELD_NAME = "_name";
	private static final String FIELD_VALUE = "test";

	public static class ItemParent {
		public int _id;
	}
	
	public static class Item extends ItemParent {
		public String _name;

		public Item(String name) {
			_name = name;
		}
	}
	
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).objectField(FIELD_NAME).indexed(true);
	}
	
	protected void store() throws Exception {
		store(new Item(FIELD_VALUE));
		store(new Item(FIELD_VALUE + "X"));
	}

	public void testIndexAccess() {
		Query query = newQuery(Item.class);
		query.descend(FIELD_NAME).constrain(FIELD_VALUE);
		Assert.areEqual(1, query.execute().size());
	}
}
