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
package com.db4o.db4ounit.common.defragment;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class DefragInheritedFieldIndexTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {

	private static final String FIELD_NAME = "_name";
	private static final String[] NAMES = {"Foo", "Bar", "Baz"};
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(ParentItem.class).objectField(FIELD_NAME).indexed(true);
	}

	@Override
	protected void store() throws Exception {
		for (String name : NAMES) {
			store(new ChildItem(name));
		}
	}

	public void testDefragInheritedFieldIndex() throws Exception {
		assertQueryByIndex();
		defragment();
		assertQueryByIndex();
	}
	
	private void assertQueryByIndex() {
		Query query = newQuery(ChildItem.class);
		query.descend(FIELD_NAME).constrain(NAMES[0]);
		ObjectSet<ChildItem> result = query.execute();
		Assert.areEqual(1, result.size());
		Assert.areEqual(NAMES[0], result.next()._name);
	}

	public static class ParentItem {
		public String _name;
		
		public ParentItem(String name) {
			_name = name;
		}
	}
	
	public static class ChildItem extends ParentItem {
		public ChildItem(String name) {
			super(name);
		}
	}
}
