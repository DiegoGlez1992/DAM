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

import java.io.*;
import java.util.Stack;

import com.db4o.Db4oEmbedded;
import com.db4o.EmbeddedObjectContainer;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.db4ounit.common.api.TestWithTempFile;
import com.db4o.defragment.Defragment;
import com.db4o.defragment.DefragmentConfig;
import com.db4o.reflect.Reflector;

import db4ounit.Assert;
import db4ounit.extensions.ExcludingReflector;
import db4ounit.extensions.OptOutExcludingClassLoaderIssue;
import db4ounit.extensions.fixtures.OptOutNetworkingCS;

public class UnavailableClassesWithTypeHandlerTestCase extends TestWithTempFile implements OptOutNetworkingCS, OptOutExcludingClassLoaderIssue {
	
	public static class HolderForClassWithTypeHandler {
		public HolderForClassWithTypeHandler(Stack stack) {
			_fieldWithTypeHandler = stack;
		}

		public Stack _fieldWithTypeHandler;
	}

	/**
	 * @sharpen.ignore
	 */
	@decaf.Ignore(decaf.Platform.JDK11)
	public void testDefrag() throws IOException {
		final DefragmentConfig config = new DefragmentConfig(tempFile());
		config.db4oConfig(configWith(new com.db4o.reflect.jdk.JdkReflector(new db4ounit.extensions.util.ExcludingClassLoader(getClass().getClassLoader(), Stack.class))));
		Defragment.defrag(config);
	}

	public void testStoredClasses() {		
		assertStoredClasses(tempFile());
	}
	
	@Override
	public void setUp() throws Exception {
	    super.setUp();
	    store(tempFile(), new HolderForClassWithTypeHandler(new Stack()));
	}

	private void assertStoredClasses(final String databaseFileName) {
		ObjectContainer db = openFileExcludingStackClass(databaseFileName);
		try {
			Assert.isGreater(2, db.ext().storedClasses().length);
		} finally {
			db.close();
		}
	}

	private EmbeddedObjectContainer openFileExcludingStackClass(final String databaseFileName) {
	    return Db4oEmbedded.openFile(configExcludingStack(), databaseFileName);
    }

	private void store(final String databaseFileName, Object obj) {
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), databaseFileName);
		try {
			db.store(obj);
		} finally {
			db.close();
		}
	}

	private EmbeddedConfiguration configExcludingStack() {
		return configWith(new ExcludingReflector(Stack.class));
	}

	private EmbeddedConfiguration configWith(final Reflector reflector) {
	    final EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();			
		config.common().reflectWith(reflector);
		return config;
    }
}
