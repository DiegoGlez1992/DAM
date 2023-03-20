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
package com.db4o.db4ounit.common.config;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;

public class ObjectContainerCustomNameTestCase implements TestCase {

	private static class CustomNameProvider implements NameProvider {
		public String name(ObjectContainer db) {
			return CUSTOM_NAME;
		}
	}

	private static final String FILE_NAME = "foo.db4o";
	protected static final String CUSTOM_NAME = "custom";

	public void testDefault() {
		assertName(config(), FILE_NAME);
	}

	public void testCustom() {
		EmbeddedConfiguration config = config();
		config.common().nameProvider(new CustomNameProvider());
		assertName(config, CUSTOM_NAME);
	}
	
	public void testNameIsAvailableAtConfigurationItemApplication() {
		EmbeddedConfiguration config = config();
		config.common().nameProvider(new CustomNameProvider());
		config.common().add(new ConfigurationItem() {

			public void apply(InternalObjectContainer container) {
				Assert.areEqual(CUSTOM_NAME, container.toString());
			}

			public void prepare(Configuration configuration) {
			}
			
		});
		assertName(config, CUSTOM_NAME);
	}

	private void assertName(EmbeddedConfiguration config, String expected) {
		EmbeddedObjectContainer db = Db4oEmbedded.openFile(config, FILE_NAME);
		Assert.areEqual(expected, db.toString());
		db.close();
	}

	private EmbeddedConfiguration config() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.file().storage(new MemoryStorage());
		return config;
	}
	
}
