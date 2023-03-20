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

public class Collection4TestCase implements TestCase {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(Collection4TestCase.class).run();
	}
	
	public void testRemoveAll() {
		final String[] originalElements = new String[] { "foo", "bar", "baz" };
		final Collection4 c = newCollection(originalElements);
		c.removeAll(newCollection(new String[0]));
		assertCollection(originalElements, c);
		
		c.removeAll(newCollection(new String[] { "baz", "bar", "zeng" }));
		assertCollection(new String[] { "foo" }, c);
		
		c.removeAll(newCollection(originalElements));
		assertCollection(new String[0], c);
		 
	}
	
	public void testContains() {
		Object a = new Object();
		Collection4 c = new Collection4();
		c.add(new Object());
		Assert.isFalse(c.contains(a));
		c.add(a);
		Assert.isTrue(c.contains(a));
		c.remove(a);
		Assert.isFalse(c.contains(a));
	}
	
	private static class Item {
		public int id;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + id;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Item other = (Item) obj;
			if (id != other.id)
				return false;
			return true;
		}

		public Item(int id) {
			this.id = id;
		}
		
	}
	
	public void testContainsAll() {
		
		Item a = new Item(42);
		Item b = new Item(a.id+1);
		Item c = new Item(b.id+1);
		Item a_ = new Item(a.id);
		
		Collection4 needle = new Collection4();
		Collection4 haystack = new Collection4();
		
		haystack.add(a);
		needle.add(a);
		needle.add(b);
		
		Assert.isFalse(haystack.containsAll(needle));
		
		needle.remove(b);

		Assert.isTrue(haystack.containsAll(needle));
		
		needle.add(b);
		haystack.add(b);
		
		Assert.isTrue(haystack.containsAll(needle));
		
		needle.add(a_);
		Assert.isTrue(haystack.containsAll(needle));
		
		needle.add(c);
		
		Assert.isFalse(haystack.containsAll(needle));

		needle.clear();

		Assert.isTrue(haystack.containsAll(needle));
		
		haystack.clear();

		Assert.isTrue(haystack.containsAll(needle));
	}
	
	public void testReplace(){
        final Collection4 c = new Collection4();
        c.replace("one", "two");
        c.add("one");
        c.add("two");
        c.add("three");
        c.replace("two", "two.half");
        assertCollection(new String[] {"one", "two.half", "three"}, c);
        c.replace("two.half", "one");
        c.replace("one", "half");
        assertCollection(new String[] {"half", "one", "three"}, c);
	}
	
	public void testNulls(){
		final Collection4 c = new Collection4();
		c.add("one");
		assertNotContainsNull(c);
		c.add(null);
		assertContainsNull(c);
		assertCollection(new String[] { "one", null }, c);
		c.prepend(null);
		assertCollection(new String[] { null, "one", null }, c);
		c.prepend("zero");
		c.add("two");
		assertCollection(new String[] {"zero", null, "one", null , "two"}, c);
		assertContainsNull(c);
		c.remove(null);
		assertCollection(new String[] { "zero", "one", null , "two"}, c);
		c.remove(null);
		assertNotContainsNull(c);
		assertCollection(new String[] { "zero", "one", "two"}, c);
		c.remove(null);
		assertCollection(new String[] { "zero", "one", "two"}, c);
	}
	
	public void testGetByIndex() {
		final Collection4 c = new Collection4();
		c.add("one");
		c.add("two");
		Assert.areEqual("one", c.get(0));
		Assert.areEqual("two", c.get(1));
		assertIllegalIndex(c, -1);
		assertIllegalIndex(c, 2);
	}
	
	public void testIndexOf() {
		final Collection4 c = new Collection4();
		Assert.areEqual(-1, c.indexOf("notInCollection"));
		c.add("one");
		Assert.areEqual(-1, c.indexOf("notInCollection"));
		Assert.areEqual(0, c.indexOf("one"));
		c.add("two");
		c.add("three");
		Assert.areEqual(0, c.indexOf("one"));
		Assert.areEqual(1, c.indexOf("two"));
		Assert.areEqual(2, c.indexOf("three"));
		Assert.areEqual(-1, c.indexOf("notInCollection"));
	}

	private void assertIllegalIndex(final Collection4 c, final int index) {
		Assert.expect(IllegalArgumentException.class, new CodeBlock() {
			public void run() throws Throwable {
				c.get(index);
			}
		});
	}
	
	public void testPrepend() {
		final Collection4 c = new Collection4();
		c.prepend("foo");
		assertCollection(new String[] { "foo" }, c);
		c.add("bar");
		assertCollection(new String[] { "foo", "bar" }, c);
		c.prepend("baz");
		assertCollection(new String[] { "baz", "foo", "bar" }, c);
		c.prepend("gazonk");
		assertCollection(new String[] { "gazonk", "baz", "foo", "bar" }, c);
	}
	
	public void testCopyConstructor() {
		final String[] expected = new String[] { "1", "2", "3" };
		final Collection4 c = newCollection(expected);
		assertCollection(expected, new Collection4(c));
	}
	
	public void testInvalidIteratorException() {
		final Collection4 c = newCollection(new String[] { "1", "2" });
		final Iterator4 i = c.iterator();
		Assert.isTrue(i.moveNext());
		c.add("3");
		Assert.expect(InvalidIteratorException.class, new CodeBlock() {
			public void run() throws Throwable {
				System.out.println(i.current());
			}
		});
	}
	
	public void testRemove() {
		final Collection4 c = newCollection(new String[] { "1", "2", "3", "4" });
		c.remove("3");
		assertCollection(new String[] { "1", "2", "4"} , c);
		c.remove("4");
		assertCollection(new String[] { "1", "2" } , c);
		c.add("5");
		assertCollection(new String[] { "1", "2", "5" } , c);
		c.remove("1");
		assertCollection(new String[] { "2", "5" } , c);
		c.remove("2");
		c.remove("5");
		assertCollection(new String[] {}, c);
		c.add("6");
		assertCollection(new String[] { "6" }, c);
	}
	
	private void assertCollection(String[] expected, Collection4 c) {
		Assert.areEqual(expected.length, c.size());
		Iterator4Assert.areEqual(expected, c.iterator());
	}
	
	private void assertContainsNull(Collection4 c) {
		Assert.isTrue(c.contains(null));
		Assert.isNull(c.get(null));
		int size = c.size();
		c.ensure(null);
		Assert.areEqual(size, c.size());
	}
	
	private void assertNotContainsNull(Collection4 c) {
		Assert.isFalse(c.contains(null));
		Assert.isNull(c.get(null));
		int size = c.size();
		c.ensure(null);
		Assert.areEqual(size + 1, c.size());
		c.remove(null);
		Assert.areEqual(size, c.size());
	}

	public void testIterator() {
		String[] expected = new String[] { "1", "2", "3" };
		Collection4 c = newCollection(expected);		
		Iterator4Assert.areEqual(expected, c.iterator());
	}	
	
	private Collection4 newCollection(String[] expected) {
		Collection4 c = new Collection4();		
		c.addAll(expected);
		return c;
	}
	
	public void testToString() {
		Collection4 c = new Collection4();
		Assert.areEqual("[]", c.toString());
		
		c.add("foo");
		Assert.areEqual("[foo]", c.toString());
		c.add("bar");
		Assert.areEqual("[foo, bar]", c.toString());
	}
}
