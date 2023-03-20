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
package com.db4o.db4ounit.common.soda;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class QueryUnknownClassTestCase extends AbstractDb4oTestCase {

	public static class Data {
		public int _id;

		public Data(int id) {
			_id = id;
		}
	}

	public static class DataHolder {
		public Vector _data;

		public DataHolder(Object data) {
			_data = new Vector();
			_data.addElement(data);
		}
	}

	public static class Unrelated {
		public int _uid;

		public Unrelated(int id) {
			_uid = id;
		}
	}
	
	public void testQueryUnknownClass() {
		Query query = newQuery(Data.class);
		query.descend("_id").constrain(new Integer(42));
		ObjectSet result = query.execute();
		Assert.areEqual(0, result.size());
	}

	public void testQueryUnknownClassInUnknownCollection() {
		Query query = newQuery(DataHolder.class);
		query.descend("_data").descend("_id").constrain(new Integer(42));
		ObjectSet result = query.execute();
		Assert.areEqual(0, result.size());
	}

	public void _testQueryUnknownClassInCollection() {
		store(new DataHolder(new Unrelated(42)));
		Query query = newQuery(DataHolder.class);
		query.descend("_data").descend("_id").constrain(new Integer(42));
		ObjectSet result = query.execute();
		Assert.areEqual(0, result.size());
	}

	public void _testQueryUnknownClassInCollectionConjunction() {
		store(new DataHolder(new Unrelated(42)));
		Query query = newQuery(DataHolder.class);
		query.descend("_data").descend("_id").constrain(new Integer(42)).and(
				query.descend("_data").descend("_uid").constrain(new Integer(42)));
		ObjectSet result = query.execute();
		Assert.areEqual(0, result.size());
	}

	public void testQueryUnknownClassInCollectionDisjunction() {
		store(new DataHolder(new Unrelated(42)));
		Query query = newQuery(DataHolder.class);
		query.descend("_data").descend("_id").constrain(new Integer(42)).or(
				query.descend("_data").descend("_uid").constrain(new Integer(42)));
		ObjectSet result = query.execute();
		Assert.areEqual(1, result.size());
	}
}
