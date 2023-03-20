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
package com.db4o.db4ounit.common.migration;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.db4ounit.common.handlers.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;
import com.db4o.reflect.generic.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Remove(decaf.Platform.JDK11)
public class MigrationHopsTestCase extends TestWithTempFile implements OptOutWorkspaceIssue {
	
	private Db4oLibraryEnvironmentProvider _environmentProvider;
	
	public static class Item {
		
		public String version;
		
		public Item() {
		}
		
		public Item(String version) {
			this.version = version;
        }
	}
	
	public static class Tester {
		
		public void createDatabase(final String filename) {
			withContainer(filename, new Function4<ObjectContainer, Object>() { public Object apply(ObjectContainer container) {
				ObjectContainerAdapter adapter = ObjectContainerAdapterFactory.forVersion(1, 1);
				adapter.forContainer((ExtObjectContainer) container);
				adapter.store(new Item(Db4o.version().substring(5)));
				
				return null;
			}});
		}
		
		public String currentVersion(String filename) {
			return withContainer(filename, new Function4<ObjectContainer, String>() { public String apply(ObjectContainer container) {
				return currentVersion(container);
			}});
		}

		public String currentVersion(ObjectContainer container) {
	        return ((Item)container.query(Item.class).next()).version;
        }

		private static <T> T withContainer(String filename, final Function4<ObjectContainer, T> block) {
	        final ObjectContainer container = Db4o.openFile(filename);
			try {
				return block.apply(container);
			} finally {
				container.close();
			}
        }
	}

	public void test() throws Exception {
		
		final Db4oLibraryEnvironment originalEnv = environmentForVersion("6.0");
		originalEnv.invokeInstanceMethod(Tester.class, "createDatabase", tempFile());
		
		for (String hop : new String[] { "6.4", "7.4", currentVersion()}) {
			final Db4oLibraryEnvironment hopEnvironment = environmentForVersion(hop);
			Assert.areEqual(originalEnv.version(), invokeTesterMethodOn(hopEnvironment, "currentVersion"));
		}
		
		final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().reflectWith(new ExcludingReflector(Item.class));
		
		final EmbeddedObjectContainer container = Db4oEmbedded.openFile(config, tempFile());
		try {
			Query query = container.query();
			query.constrain(Item.class);
			
			Object item = query.execute().get(0);
			Assert.areEqual(originalEnv.version(), ((GenericObject)item).get(0));
		} finally {
			container.close();
		}
	}

	private String currentVersion() {
		return Db4oVersion.MAJOR + "." + Db4oVersion.MINOR;
	}

	private Object invokeTesterMethodOn(final Db4oLibraryEnvironment env74, final String methodName) throws Exception {
	    return env74.invokeInstanceMethod(Tester.class, methodName, tempFile());
    }

	private Db4oLibraryEnvironment environmentForVersion(final String version) throws IOException {
	    return new Db4oLibrarian(_environmentProvider).forVersion(version).environment;
    }

	@Override
	public void setUp() throws Exception {
		super.setUp();
		_environmentProvider = new Db4oLibraryEnvironmentProvider(PathProvider.testCasePath());
    }

	@Override
	public void tearDown() throws Exception {
		_environmentProvider.disposeAll();
		super.tearDown();
    }

}
