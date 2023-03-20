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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.io.*;
import com.db4o.query.*;

import db4ounit.*;

// see COR-714
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class ArrayListCandidatesTestCase implements TestCase {
	
	private static final String DB_ID = "in_memory";

	private static class DataHolder {
		public ArrayList _data;

		private DataHolder() {
		}
		
		public DataHolder(Object[] data) {
			this();
			_data = new ArrayList(data.length);
			for (int dataIdx = 0; dataIdx < data.length; dataIdx++) {
				_data.add(data[dataIdx]);
			}
		}
	}

	private static class DataA {
		private String _a;

		public DataA(String a) {
			_a = a;
			use(_a);
		}

		private void use(String a) {
		}
	}

	private static class DataB {
		private String _b;

		public DataB(String b) {
			_b = b;
			use(_b);
		}

		private void use(String b) {
		}
	}

	public void test() {		
		Configuration config = Db4o.newConfiguration();
		config.storage(new MemoryStorage());
		ObjectContainer db = Db4o.openFile(config, DB_ID);

		try {
			storeObjects(db);
			retrieveObjectByUsingConstraints(db);

		} 
		finally {
			db.close();
		}
	}

	private void storeObjects(ObjectContainer db) {
		Object[] data = new Object[]{new DataA("A"), new DataB("B")};
		DataHolder holder = new DataHolder(data);
		db.store(holder);
	}

	private void retrieveObjectByUsingConstraints(ObjectContainer db) {
		Query query = db.query();
		Constraint extentConstraint = query.constrain(DataHolder.class);
		Constraint aConstraint = query.descend("_data").descend("_a")
				.constrain("A");
		Constraint bConstraint = query.descend("_data").descend("_b")
				.constrain("B");
		extentConstraint.and(aConstraint);
		extentConstraint.and(bConstraint);

		query.execute();
	}
}
