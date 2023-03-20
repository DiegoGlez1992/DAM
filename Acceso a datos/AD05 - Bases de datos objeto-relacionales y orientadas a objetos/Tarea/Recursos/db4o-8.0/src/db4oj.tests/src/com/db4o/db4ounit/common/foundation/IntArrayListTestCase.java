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

/**
 * @exclude
 */
public class IntArrayListTestCase implements TestCase {
	
    public static void main(String[] args) {
        new ConsoleTestRunner(IntArrayListTestCase.class).run();
    }
    
	public void testIteratorGoesForwards() {
		IntArrayList list = new IntArrayList();
		assertIterator(new int[] {}, list.intIterator());
		
		list.add(1);
		assertIterator(new int[] { 1 }, list.intIterator());		
		
		list.add(2);
		assertIterator(new int[] { 1, 2 }, list.intIterator());
	}

	private void assertIterator(int[] expected, IntIterator4 iterator) {
		for (int i=0; i<expected.length; ++i) {
			Assert.isTrue(iterator.moveNext());
			Assert.areEqual(expected[i], iterator.currentInt());
			Assert.areEqual(new Integer(expected[i]), iterator.current());
		}
		Assert.isFalse(iterator.moveNext());
	}
	
	//test mthod add(int,int)
	public void testAddAtIndex() {
	    IntArrayList list = new IntArrayList();
	    for (int i = 0; i < 10; i++) {
	        list.add(i);
	    }
	    
	    list.add(3, 100);
	    Assert.areEqual(100, list.get(3));
	    for (int i = 4; i < 11; i++) {
	        Assert.areEqual(i - 1, list.get(i));
	    }
	}
}
