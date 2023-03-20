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
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class IdentityHashtable4TestCase implements TestLifeCycle {

	private static final int KEY = 42;

	public static class Item {
		public int _id;
		
		public Item(int id) {
			_id = id;
		}
		
		public boolean equals(Object other) {
			if(other == this) {
				return true;
			}
			if(other == null || other.getClass() != getClass()) {
				return false;
			}
			return _id == ((Item)other)._id;
		}
		
		@Override
		public int hashCode() {
			return _id;
		}
	}
	
	private Map4 _map;
	
	public void setUp() throws Exception {
		_map = new IdentityHashtable4();
	}

	public void tearDown() throws Exception {
	}

	public void testEmpty() {
		Assert.isFalse(_map.containsKey(new Item(KEY)));
		Assert.isNull(_map.get(new Item(KEY)));
		Assert.isFalse(_map.values().iterator().moveNext());
		Assert.isNull(_map.remove(new Item(KEY)));
		Assert.areEqual(0, _map.size());
	}
	
	public void testSingleEntry() {
		Item key = new Item(KEY);
		_map.put(key, KEY);
		assertSingleEntry(key, KEY);
		_map.put(key, KEY);
		assertSingleEntry(key, KEY);
	}

	public void testDuplicateEntry() {
		_map.put(new Item(KEY), KEY);
		_map.put(new Item(KEY), KEY);
		Iterator4Assert.areEqual(new Object[] { KEY, KEY }, _map.values().iterator());
	}
	
	public void testMultipleEntries() {
		Item key1 = new Item(KEY);
		Item key2 = new Item(KEY + 1);
		_map.put(key1, KEY);
		_map.put(key2, KEY + 1);
		Assert.isTrue(_map.containsKey(key1));
		Assert.isTrue(_map.containsKey(key2));
		Assert.isFalse(_map.containsKey(new Item(KEY)));
		Assert.isFalse(_map.containsKey(new Item(KEY + 1)));
		Assert.areEqual(KEY, _map.get(key1));
		Assert.areEqual(KEY + 1, _map.get(key2));
		Assert.isNull(_map.get(new Item(KEY)));
		Assert.isNull(_map.get(new Item(KEY + 1)));
		Assert.areEqual(2, _map.size());
		Iterator4Assert.sameContent(new Object[] { KEY, KEY + 1}, _map.values().iterator());
	}
	
	public void testRemove() {
		Item key1 = new Item(KEY);
		Item key2 = new Item(KEY + 1);
		_map.put(key1, KEY);
		_map.put(key2, KEY + 1);
		Assert.areEqual(KEY, _map.remove(key1));
		Assert.isFalse(_map.containsKey(key1));
		Assert.isTrue(_map.containsKey(key2));
		Assert.isNull(_map.get(key1));
		Assert.areEqual(KEY + 1, _map.get(key2));
		Assert.areEqual(1, _map.size());
		Iterator4Assert.areEqual(new Object[] { KEY + 1}, _map.values().iterator());
	}

	public void testClear() {
		Item key1 = new Item(KEY);
		Item key2 = new Item(KEY + 1);
		_map.put(key1, KEY);
		_map.put(key2, KEY + 1);
		_map.clear();
		Assert.isFalse(_map.containsKey(key1));
		Assert.isFalse(_map.containsKey(key2));
		Assert.isNull(_map.get(key1));
		Assert.isNull(_map.get(key2));
		Assert.areEqual(0, _map.size());
		Iterator4Assert.areEqual(new Object[] {}, _map.values().iterator());
	}

	private void assertSingleEntry(Item key, Integer value) {
		Assert.isTrue(_map.containsKey(key));
		Assert.isFalse(_map.containsKey(new Item(value)));
		Assert.areEqual(value, _map.get(key));
		Assert.isNull(_map.get(new Item(value)));
		Assert.areEqual(1, _map.size());
		Iterator4Assert.areEqual(new Object[] { value }, _map.values().iterator());
	}
}
