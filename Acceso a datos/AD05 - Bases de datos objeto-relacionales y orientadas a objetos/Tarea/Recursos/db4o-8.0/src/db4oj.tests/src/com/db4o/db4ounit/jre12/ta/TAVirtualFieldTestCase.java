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
package com.db4o.db4ounit.jre12.ta;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;
import com.db4o.reflect.generic.*;
import com.db4o.reflect.jdk.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.util.*;

/**
 * @sharpen.ignore
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class TAVirtualFieldTestCase extends Db4oTestWithTempFile {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(TAVirtualFieldTestCase.class).run();
	}
	
	private Db4oUUID _uuid;
	
	public static class Item {
		public Item _next;
	}
	
	public void test() {
		ObjectContainer db = Db4oEmbedded.openFile(config(true), tempFile());
		ObjectSet result = db.query(Item.class);
		Assert.areEqual(1, result.size());
		Object obj = result.next();
		Assert.isInstanceOf(GenericObject.class, obj);
		Assert.areEqual(_uuid, db.ext().getObjectInfo(obj).getUUID());
		db.close();
	}

	public void setUp() throws Exception {
		ObjectContainer db = Db4oEmbedded.openFile(config(false), tempFile());
		Item obj = new Item();
		db.store(obj);
		_uuid = db.ext().getObjectInfo(obj).getUUID();
		db.close();
	}

	private EmbeddedConfiguration config(boolean withCL) {
		EmbeddedConfiguration config = newConfiguration();
		config.file().generateUUIDs(ConfigScope.GLOBALLY);
		config.common().add(new TransparentActivationSupport());
		if(withCL) {
			ClassLoader cl = new ExcludingClassLoader(Item.class.getClassLoader(), new Class[] { Item.class });
			config.common().reflectWith(new JdkReflector(cl));
		}
		return config;
	}
}