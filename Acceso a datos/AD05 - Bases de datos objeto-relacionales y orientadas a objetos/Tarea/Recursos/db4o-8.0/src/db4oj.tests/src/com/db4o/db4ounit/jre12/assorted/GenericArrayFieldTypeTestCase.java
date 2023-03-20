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
package com.db4o.db4ounit.jre12.assorted;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.reflect.*;
import com.db4o.reflect.jdk.*;

import db4ounit.*;
import db4ounit.extensions.util.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class GenericArrayFieldTypeTestCase extends TestWithTempFile {
	
	public static class SubData {
		public int _id;

		public SubData(int id) {
			_id = id;
		}
	}
	
	public static class Data {
		public SubData[] _data;

		public Data(SubData[] data) {
			_data = data;
		}
	}

	public void testGenericArrayFieldType() {
		Class[] excludedClasses = new Class[]{
				Data.class,
				SubData.class,
		};
		ClassLoader loader = new ExcludingClassLoader(getClass().getClassLoader(), excludedClasses);
		Configuration config = Db4o.newConfiguration();
		config.reflectWith(new JdkReflector(loader));
		ObjectContainer db = Db4o.openFile(config, tempFile());
		try {
			ReflectClass dataClazz = db.ext().reflector().forName(Data.class.getName());
			ReflectField field = dataClazz.getDeclaredField("_data");
			ReflectClass fieldType = field.getFieldType();
			Assert.isTrue(fieldType.isArray());
			ReflectClass componentType = fieldType.getComponentType();
			Assert.areEqual(SubData.class.getName(), componentType.getName());
		}
		finally {
			db.close();
		}
	}

	private void store() {
		ObjectContainer db = Db4o.openFile(Db4o.newConfiguration(), tempFile());
		SubData[] subData = {
			new SubData(1),
			new SubData(42),
		};
		Data data = new Data(subData);
		db.store(data);
		db.close();
	}

	public void setUp() throws Exception {
		store();
	}
}
