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

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class HashMapTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {
	public static void main(String[] args) {
		new HashMapTestCase().runSolo();
	}

	protected void store() throws Exception {
		HashMap hashmap = new HashMap();
		for (int i = 0; i < 42; ++i) {
			hashmap.put(new Integer(i), "hello" + i);
		}
		store(hashmap);
	}

	public void test() throws Exception {
		HashMap hashmap = (HashMap) retrieveOnlyInstance(HashMap.class);
		for (int i = 0; i < 10; ++i) {
			store(hashmap);
			db().commit();
		}
		long oldSize = db().systemInfo().totalSize();
		store(hashmap);
		db().commit();
		long newSize = db().systemInfo().totalSize();
		Assert.areEqual(oldSize, newSize);
	}
}
