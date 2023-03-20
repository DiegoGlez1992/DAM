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
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class SortedCollection4TestCase implements TestCase {
	
	static final Comparison4 INTEGER_COMPARISON = new Comparison4() {
		public int compare(Object x, Object y) {
			return ((Integer)x).intValue()-((Integer)y).intValue();
		}
	};
	
	public void testAddAllAndToArray() {
		final Object[] array = IntArrays4.toObjectArray(new int[] { 6, 4, 1, 2, 7, 3 });
		
		SortedCollection4 collection = newSortedCollection();	
		Assert.areEqual(0, collection.size());
		collection.addAll(new ArrayIterator4(array));
		
		assertCollection(new int[] { 1, 2, 3, 4, 6, 7 }, collection);
	}
	
	public void testToArrayOnEmptyCollection() {
		final Object[] array = new Object[0];
		Assert.areSame(array, newSortedCollection().toArray(array));
	}
	
	public void testAddRemove() {
		final SortedCollection4 collection = newSortedCollection();
		collection.add(new Integer(3));
		collection.add(new Integer(1));
		collection.add(new Integer(5));
		
		assertCollection(new int[] { 1, 3, 5 }, collection);
		
		collection.remove(new Integer(3));
		assertCollection(new int[] { 1, 5 }, collection);
		
		collection.remove(new Integer(1));
		assertCollection(new int[] { 5 }, collection);
	}
	
	private void assertCollection(final int[] expected, SortedCollection4 collection) {
		Assert.areEqual(expected.length, collection.size());
		ArrayAssert.areEqual(
				IntArrays4.toObjectArray(expected),
				collection.toArray(new Object[collection.size()]));
	}
	
	private SortedCollection4 newSortedCollection() {
		return new SortedCollection4(INTEGER_COMPARISON);
	}

}
