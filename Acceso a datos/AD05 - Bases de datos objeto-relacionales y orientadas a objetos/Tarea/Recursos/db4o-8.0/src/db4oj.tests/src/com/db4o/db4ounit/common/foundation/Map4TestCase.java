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

public class Map4TestCase implements TestCase {
	
	private final Map4 subject = new Hashtable4();
	
	public void testRemove() {
		
		for (int i=0; i<5; ++i) {
			final String key = "key" + i;
			final String value = "value" + i;
			subject.put(key, value);
			Assert.areEqual(value, subject.remove(key));
		}
	}
	
	public void testContainsKey() {
		final String key1 = "foo";
		final String key2 = "bar";
		subject.put(key1, "v");
		subject.put(key2, "v");
		Assert.isTrue(subject.containsKey(key1));
		Assert.isTrue(subject.containsKey(key2));
		Assert.isFalse(subject.containsKey(null));
		Assert.isFalse(subject.containsKey(key1.toUpperCase()));
		Assert.isFalse(subject.containsKey(key2.toUpperCase()));
	}
	
	public void testValuesIterator() {
		
		final Object[] values = new Object[5];
		for (int i=0; i<values.length; ++i) {
			values[i] = ("value" + i);
		}
		
		for (Object v : values) {
	        subject.put("key4" + v, v);
        }
		
		Iterator4Assert.sameContent(values, subject.values().iterator());
	}

}
