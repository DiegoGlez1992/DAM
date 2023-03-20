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
 */
public class CircularBufferTestCase implements TestCase {
	
	private static final int BUFFER_SIZE = 4;

	final CircularBuffer4<Integer> buffer = new CircularBuffer4<Integer>(BUFFER_SIZE);
	
	public void testAddFirstRemoveLast() {
		for (int i=1; i<11; ++i) {
			buffer.addFirst(i);
			assertRemoveLast(i);
		}
	}
	
	public void testAddFirstBounds() {
		fillBuffer();
		assertIllegalAddFirst();
		buffer.removeLast();
		buffer.addFirst(5);
		assertIllegalAddFirst();
		buffer.removeLast();
		buffer.addFirst(6);
	}

	private void assertIllegalAddFirst() {
	    Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() {
				buffer.addFirst(3);
			}
		});
    }
	
	public void testRemoveLastBounds() {
		for (int i=0; i<3; ++i) {
			assertIllegalRemoveLast();
			
			buffer.addFirst(1);
			buffer.addFirst(3);
			assertRemoveLast(1);
			assertRemoveLast(3);
			
			assertIllegalRemoveLast();
		}
	}
	
	private void assertIllegalRemoveFirst() {
	    Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() {
				buffer.removeFirst();
			}
		});
    }

	private void assertIllegalRemoveLast() {
	    Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() {
				buffer.removeLast();
			}
		});
    }
	
	private void assertRemoveLast(int value) {
	    Assert.areEqual(value, (int)buffer.removeLast());
    }
	
	public void testIterator() {
		for (int i=0; i<3; ++i) {
			assertIterator();
			buffer.addFirst(1);
			assertIterator(1);
			buffer.addFirst(2);
			assertIterator(2, 1);
			buffer.removeLast();
			assertIterator(2);
			buffer.removeLast();
		}
	}
	
	public void testContains() {
		buffer.addFirst(1);
		buffer.addFirst(3);
		buffer.addFirst(5);
		Assert.isTrue(buffer.contains(1));
		Assert.isFalse(buffer.contains(2));
		Assert.isTrue(buffer.contains(3));
		Assert.isFalse(buffer.contains(4));
		Assert.isTrue(buffer.contains(5));
	}
	
	public void testFullEmpty() {
		Assert.isTrue(buffer.isEmpty());
		Assert.isFalse(buffer.isFull());
		buffer.addFirst(1);
		Assert.isFalse(buffer.isEmpty());
		Assert.isFalse(buffer.isFull());
		buffer.addFirst(2);
		buffer.addFirst(3);
		buffer.addFirst(4);
		Assert.isFalse(buffer.isEmpty());
		Assert.isTrue(buffer.isFull());
		buffer.removeLast();
		Assert.isFalse(buffer.isEmpty());
		Assert.isFalse(buffer.isFull());
	}
	
	public void testSize() {
		for (int i=0; i<3; ++i) {
			assertSize(0);
			for (int j=0; j<BUFFER_SIZE; ++j) {
				buffer.addFirst(j);
				assertSize(j+1);
			}
			for (int j=0; j<BUFFER_SIZE; ++j) {
				buffer.removeLast();
				assertSize(BUFFER_SIZE - j - 1);
			}
		}
	}

	private void assertSize(final int expected) {
	    Assert.areEqual(expected, buffer.size());
    }

	private void assertIterator(Object... expected) {
	    Iterator4Assert.areEqual(expected, buffer.iterator());
    }
	
	public void testRemove() {
		assertIllegalRemove(42);
		
		buffer.addFirst(1);
		assertRemove(1);
		
		fillBuffer();
		assertRemovals(1, 2, 3, 4);
		
		fillBuffer();
		assertRemovals(2, 3, 4, 1);
		
		fillBuffer();
		assertRemovals(3, 2, 4, 1);
		
		fillBuffer();
		assertRemovals(4, 3, 2, 1);
		
		fillBuffer();
		assertRemovals(4, 1, 2, 3);
		
		fillBuffer();
		assertRemoveLast(1);
		assertRemoveLast(2);
		assertRemoveLast(3);
		assertRemoveLast(4);
		
	}

	private void assertRemovals(int... indexes) {
		for(int i : indexes){
			assertRemove(i);	
		}
		assertIllegalRemoveLast();
		assertIllegalRemoveFirst();
	}

	private void assertRemove(int value) {
	    Assert.isTrue(buffer.remove(value));
		assertIllegalRemove(value);
    }

	private void assertIllegalRemove(final int value) {
	   Assert.isFalse(buffer.remove(value));
    }
	
	private void fillBuffer() {
		for (int i = 1; i <= BUFFER_SIZE; i++) {
			buffer.addFirst(i);	
		}
	}

}
