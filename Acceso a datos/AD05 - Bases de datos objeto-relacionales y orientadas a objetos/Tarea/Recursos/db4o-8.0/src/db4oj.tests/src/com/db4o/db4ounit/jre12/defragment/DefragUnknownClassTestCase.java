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
package com.db4o.db4ounit.jre12.defragment;

import java.io.*;
import java.lang.reflect.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.defragment.*;
import com.db4o.foundation.io.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.util.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class DefragUnknownClassTestCase extends TestWithTempFile implements OptOutExcludingClassLoaderIssue {

	public static void main(String[] args) {
		new ConsoleTestRunner(DefragUnknownClassTestCase.class).run();
	}
	
	public static class Unknown {
	}
	
	public static class ClassHolder {
		public Class _clazz;
		public /* Unknown */ Object _unknown;

		// 2nd argument cant be typed to Unknown because this might cause ConstructorSupport to not 
		// find this constructor for platforms that require it
		// (Confirmed for JDK1.3.1_20 on Windows XP)
		public ClassHolder(Class clazz, /* Unknown */ Object unknown) {
			_clazz = clazz;
			_unknown = unknown;
		}
	}

	public void testUnknownClassDefrag() throws Exception {
		store();
		defragment();
		assertRetrieveClass();
	}

	private void defragment() throws Exception {
		ClassLoader loader = new ExcludingClassLoader(getClass().getClassLoader(), new Class[]{ Unknown.class });
		Class starterClazz = loader.loadClass(DefragStarter.class.getName());
		Method defragMethod = starterClazz.getDeclaredMethod("defrag", String.class);
		defragMethod.invoke(null, tempFile());
	}

	public static class DefragStarter {
		public static void defrag(String fileName) throws IOException {
			DefragmentConfig defragConfig = new DefragmentConfig(fileName);
			defragConfig.db4oConfig(config());
			defragConfig.forceBackupDelete(true);
			defragConfig.readOnly(false);
			Defragment.defrag(defragConfig);
			File4.delete(fileName + ".backup");
		}
	}
	
	private void store() {
		ObjectContainer db = openDatabase();
		db.store(new ClassHolder(Unknown.class, new Unknown()));
		db.close();
	}

	private void assertRetrieveClass() {
		ObjectContainer db = openDatabase();
		ObjectSet result = db.query(ClassHolder.class);
		Assert.areEqual(1, result.size());
		ClassHolder trans = (ClassHolder) result.next();
		Assert.areEqual(Unknown.class, trans._clazz);
		db.close();
	}

	private ObjectContainer openDatabase() {
		return Db4o.openFile(config(), tempFile());
	}
	
	public static Configuration config() {
		Configuration config = Db4o.newConfiguration();
		config.reflectWith(Platform4.reflectorForType(ClassHolder.class));
		return config;
	}
}
