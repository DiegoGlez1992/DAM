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

public class Hashtable4TestCase implements TestCase {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(Hashtable4TestCase.class).run();
	}
	
	public void testClear() {
		final Hashtable4 table = new Hashtable4();
		for (int i=0; i<2; ++i) {
			table.clear();
			Assert.areEqual(0, table.size());
			table.put("foo", "bar");
			Assert.areEqual(1, table.size());
			assertIterator(table, "foo");
		}
	}
	
	public void testToString() {
		final Hashtable4 table = new Hashtable4();
		table.put("foo", "bar");
		table.put("bar", "baz");
		Assert.areEqual(Iterators.join(table.iterator(), "{", "}", ", "), table.toString());
	}
	
	public void testContainsKey() {
		Hashtable4 table = new Hashtable4();
		Assert.isFalse(table.containsKey(null));
		Assert.isFalse(table.containsKey("foo"));
		
		table.put("foo", null);
		Assert.isTrue(table.containsKey("foo"));
		
		table.put("bar", "baz");
		Assert.isTrue(table.containsKey("bar"));
		Assert.isFalse(table.containsKey("baz"));
		Assert.isTrue(table.containsKey("foo"));
		
		table.remove("foo");
		Assert.isTrue(table.containsKey("bar"));
		Assert.isFalse(table.containsKey("foo"));
	}
	
	public void testByteArrayKeys() {
		byte[] key1 = new byte[] { 1, 2, 3 };
		byte[] key2 = new byte[] { 3, 2, 1 };
		byte[] key3 = new byte[] { 3, 2, 1 }; // same values as key2
		
		Hashtable4 table = new Hashtable4(2);
		table.put(key1, "foo");
		table.put(key2, "bar");
		
		Assert.areEqual("foo", table.get(key1));
		Assert.areEqual("bar", table.get(key2));
		Assert.areEqual(2, countKeys(table));
		Assert.areEqual(2, table.size());
		
		table.put(key3, "baz");
		Assert.areEqual("foo", table.get(key1));
		Assert.areEqual("baz", table.get(key2));
		Assert.areEqual(2, countKeys(table));
		Assert.areEqual(2, table.size());
		
		Assert.areEqual("baz", table.remove(key2));
		Assert.areEqual(1, countKeys(table));
		Assert.areEqual(1, table.size());
		
		Assert.areEqual("foo", table.remove(key1));
		Assert.areEqual(0, countKeys(table));
		Assert.areEqual(0, table.size());
	}
	
	public void testIterator(){
		assertIsIteratable(new Object[0]);
		assertIsIteratable(new Object[] { "one" });
		assertIsIteratable(new Object[]{
			new Integer(1),
			new Integer(3),
			new Integer(2),
		});
		
		assertIsIteratable(new Object[]{
			"one",
			"three",
			"two",
		});
		
		assertIsIteratable(new Object[]{
			new Key(1),
			new Key(3),
			new Key(2),
		});

	}
	
	public void testSameKeyTwice() {
		
		Integer key = new Integer(1);
		
		Hashtable4 table = new Hashtable4();
		table.put(key, "foo");
		table.put(key, "bar");
		
		Assert.areEqual("bar", table.get(key));		
		Assert.areEqual(1, countKeys(table));
	}
	
	public void testSameHashCodeIterator() {
		Key[] keys = createKeys(1, 5);
		assertIsIteratable(keys);
	}
	
	private Key[] createKeys(int begin, int end) {
		final int factor = 10;
		int count = (end-begin);
		Key[] keys = new Key[count*factor];
		for (int i=0; i<count; ++i) {
			final int baseIndex = i*factor;
			for (int j=0; j<factor; ++j) {
				keys[baseIndex + j] = new Key(begin+i);
			}
		}
		return keys;
	}

	public void testDifferentKeysSameHashCode() {
		Key key1 = new Key(1);
		Key key2 = new Key(1);
		Key key3 = new Key(2);
		
		Hashtable4 table = new Hashtable4(2);
		table.put(key1, "foo");
		table.put(key2, "bar");
		
		Assert.areEqual("foo", table.get(key1));
		Assert.areEqual("bar", table.get(key2));
		Assert.areEqual(2, countKeys(table));
		
		table.put(key2, "baz");
		Assert.areEqual("foo", table.get(key1));
		Assert.areEqual("baz", table.get(key2));
		Assert.areEqual(2, countKeys(table));
		
		table.put(key1, "spam");
		Assert.areEqual("spam", table.get(key1));
		Assert.areEqual("baz", table.get(key2));
		Assert.areEqual(2, countKeys(table));
		
		table.put(key3, "eggs");
		Assert.areEqual("spam", table.get(key1));
		Assert.areEqual("baz", table.get(key2));
		Assert.areEqual("eggs", table.get(key3));
		Assert.areEqual(3, countKeys(table));
		
		table.put(key2, "mice");
		Assert.areEqual("spam", table.get(key1));
		Assert.areEqual("mice", table.get(key2));
		Assert.areEqual("eggs", table.get(key3));
		Assert.areEqual(3, countKeys(table));
	}
	
	static class KeyCount {
		public int keys;
	}

	private int countKeys(Hashtable4 table) {
		int count = 0;
		Iterator4 i = table.iterator();
		while(i.moveNext()){
			count++;
		}
		return count;
	}
	
	public void assertIsIteratable(Object[] keys){
		final Hashtable4 table = tableFromKeys(keys);
		assertIterator(table, keys);
	}

	private void assertIterator(final Hashtable4 table, Object... keys) {
	    final Iterator4 iter = table.iterator();
		final Collection4 expected = new Collection4(keys);
		while (iter.moveNext()){
			Entry4 entry = (Entry4) iter.current();
			boolean removedOK = expected.remove(entry.key()); 
			Assert.isTrue(removedOK);
		}
		Assert.isTrue(expected.isEmpty(), expected.toString());
    }

	private Hashtable4 tableFromKeys(Object[] keys) {
		Hashtable4 ht = new Hashtable4();
		for (int i = 0; i < keys.length; i++) {
			ht.put(keys[i], keys[i]);
		}
		return ht;
	}
	
	static class Key {
		private int _hashCode;
		
		public Key(int hashCode) {
			_hashCode = hashCode;
		}
		
		public int hashCode() {
			return _hashCode;
		}
		
	}

}
