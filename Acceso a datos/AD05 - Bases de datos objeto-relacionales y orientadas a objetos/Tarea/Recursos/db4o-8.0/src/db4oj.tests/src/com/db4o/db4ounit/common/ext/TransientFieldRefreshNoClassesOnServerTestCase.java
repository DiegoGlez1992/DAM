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
package com.db4o.db4ounit.common.ext;

import com.db4o.config.*;
import com.db4o.db4ounit.common.cs.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class TransientFieldRefreshNoClassesOnServerTestCase extends ClientServerTestCaseBase implements CustomClientServerConfiguration {

	private static final String ORIGINAL_NAME = "foo";
	private static final int ORIGINAL_ID = 42;

	public static class ItemA {
		public int persistentId;
		public transient String transientName;
		
		public ItemA(int id, String name) {
			persistentId = id;
			transientName = name;
		}
	}
	
	public static class ItemB {
		public transient String transientName;
		
		public ItemB(String name) {
			transientName = name;
		}
	}
	
	public void configureClient(Configuration config) throws Exception {
		config.objectClass(ItemB.class).storeTransientFields(true);
	}

	public void configureServer(Configuration config) throws Exception {
		config.reflectWith(new ExcludingReflector(ItemA.class, ItemB.class));
	}
	
	@Override
	protected void store() throws Exception {
		store(new ItemA(ORIGINAL_ID, ORIGINAL_NAME));
		store(new ItemB(ORIGINAL_NAME));
	}

	public void testRespectsPersistTransientFieldsConfiguration() {
		ItemB itemB = retrieveOnlyInstance(ItemB.class);
		
		Assert.areEqual(ORIGINAL_NAME, itemB.transientName);
		
		itemB.transientName = ORIGINAL_NAME + "X";
		db().refresh(itemB, Integer.MAX_VALUE);
		Assert.areEqual(ORIGINAL_NAME, itemB.transientName);
	}
	
	public void testRespectsTransientModifier() {
		final ItemA item = retrieveOnlyInstance(ItemA.class);
		Assert.isNull(item.transientName);
		
		String newName = "Do not touch me";
		item.transientName = newName;
		item.persistentId = ORIGINAL_ID + 1;
		db().refresh(item, Integer.MAX_VALUE);
		
		Assert.areEqual(newName, item.transientName);
		Assert.areEqual(ORIGINAL_ID, item.persistentId);
	}
}
