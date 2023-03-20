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
package com.db4o.db4ounit.jre12.collections.map;

import java.util.HashMap;
import java.util.Map;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

public class MapNullKeyTestCase extends AbstractDb4oTestCase {

	public static class Item {
		public String _id;
		
		public Item(String id) {
			_id = id;
		}
		
		@Override
		public boolean equals(Object other) {
			if(this == other) {
				return true;
			}
			if(other == null || getClass() != other.getClass()) {
				return false;
			}
			return _id.equals(((Item)other)._id);
		}
		
		@Override
		public int hashCode() {
			return _id.hashCode();
		}
		
		@Override
		public String toString() {
			return "Item[" + _id + "]";
		}
	}
	
	
	private static final String KEY = "foo";
	private static final String VALUE_FOR_NULL = "bar1";
	private static final String VALUE_FOR_KEY = "bar2";

	public static class Holder {
		public Map<Item, Item> _map;
		
		public Holder() {
			_map = new HashMap<Item, Item>();
			_map.put(null, new Item(VALUE_FOR_NULL));
			_map.put(new Item(KEY), new Item(VALUE_FOR_KEY));
		}
		
		public Item valueForNull() {
			return _map.get(null);
		}

		public Item valueForKey() {
			return _map.get(new Item(KEY));
		}
	}

	@Override
	protected void store() throws Exception {
		store(new Holder());
	}
	
	public void testNullKey() throws Exception {
		Holder holder = retrieveOnlyInstance(Holder.class);
		Assert.areEqual(new Item(VALUE_FOR_NULL), holder.valueForNull());
		Assert.areEqual(new Item(VALUE_FOR_KEY), holder.valueForKey());
		
		Query query = newQuery(Item.class);
		query.descend("_id").constrain(KEY);
		ObjectSet<Item> result = query.execute();
		Assert.isTrue(result.hasNext());
		db().delete(result.next());
		
		reopen();
		
		holder = retrieveOnlyInstance(Holder.class);
		Assert.areEqual(new Item(VALUE_FOR_NULL), holder.valueForNull());
		Assert.isNull(holder.valueForKey());
	}
}
