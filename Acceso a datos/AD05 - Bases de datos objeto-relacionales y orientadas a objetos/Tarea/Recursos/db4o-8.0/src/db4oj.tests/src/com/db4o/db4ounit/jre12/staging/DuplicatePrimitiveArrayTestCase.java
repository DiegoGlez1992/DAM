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
package com.db4o.db4ounit.jre12.staging;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.reflect.jdk.*;

import db4ounit.*;
import db4ounit.extensions.util.*;

// OMR-70
/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class DuplicatePrimitiveArrayTestCase implements TestCase {

	private static final String FILENAME = "duplicate.db4o";

	public static class Data {
		public boolean[] _array;

		public Data(boolean[] _array) {
			this._array = _array;
		}
	}

	public void testDuplicate() {
		new File(FILENAME).delete();
		store();
		query();
	}

	private void store() {
		ObjectContainer db = Db4o.openFile(Db4o.newConfiguration(), FILENAME);
		db.store(new Data(new boolean[] { true, false }));
		db.close();
	}

	private void query() {
		Configuration config = Db4o.newConfiguration();
		Collection4 exclude = new Collection4();
		exclude.add(Data.class.getName());
		ExcludingClassLoader loader = new ExcludingClassLoader(getClass().getClassLoader(), exclude);
		config.reflectWith(new JdkReflector(loader));
		ObjectContainer db = Db4o.openFile(config, FILENAME);
		StoredClass sc = db.ext().storedClass(Data.class);
		Assert.areEqual(1,sc.getStoredFields().length);
		db.close();
	}
}