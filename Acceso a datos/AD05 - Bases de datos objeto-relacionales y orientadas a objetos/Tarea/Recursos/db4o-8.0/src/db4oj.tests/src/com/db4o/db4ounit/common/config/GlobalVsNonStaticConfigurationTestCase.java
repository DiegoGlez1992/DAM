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
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;
import com.db4o.internal.*;

import db4ounit.*;

public class GlobalVsNonStaticConfigurationTestCase extends Db4oTestWithTempFile {

	public static void main(String[] args) {
		new ConsoleTestRunner(GlobalVsNonStaticConfigurationTestCase.class).run();
	}
	
	public static class Data {
		public int id;

		public Data(int id) {
			this.id = id;
		}
	}

	public void testOpenWithNonStaticConfiguration() {
		final EmbeddedConfiguration config1 = newConfiguration();
		config1.file().readOnly(true);
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4oEmbedded.openFile(config1, tempFile());
			}
		});

		EmbeddedConfiguration config2 = newConfiguration();
		ObjectContainer db2 = Db4oEmbedded.openFile(config2, tempFile());
		try {
			db2.store(new Data(2));
			Assert.areEqual(1, db2.query(Data.class).size());
		} finally {
			db2.close();
		}
	}

	/**
	 * @deprecated using deprecated api
	 * 
	 * @sharpen.if !SILVERLIGHT
	 */
	public void testOpenWithStaticConfiguration() {
		Db4o.configure().readOnly(true);
		Assert.expect(DatabaseReadOnlyException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4o.openFile(tempFile());
			}
		});
		Db4o.configure().readOnly(false);
		ObjectContainer db = Db4o.openFile(tempFile());
		db.store(new Data(1));
		db.close();

		db = Db4o.openFile(tempFile());
		Assert.areEqual(1, db.query(Data.class).size());
		db.close();
	}

	public void testIndependentObjectConfigs() {
		EmbeddedConfiguration config = newConfiguration();
		ObjectClass objectConfig = config.common().objectClass(Data.class);
		objectConfig.translate(new TNull());
		EmbeddedConfiguration otherConfig = newConfiguration();
		Assert.areNotSame(config, otherConfig);
		Config4Class otherObjectConfig = (Config4Class) otherConfig.common().objectClass(Data.class);
		Assert.areNotSame(objectConfig, otherObjectConfig);
		Assert.isNull(otherObjectConfig.getTranslator());
	}
}
