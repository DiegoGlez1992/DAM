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

public class NonblockingQueueTestCase extends Queue4TestCaseBase {

	public void testIterator() {
		Queue4 queue=new NonblockingQueue();
		String[] data = {"a","b","c","d"};
		for (int idx = 0; idx < data.length; idx++) {
			assertIterator(queue, data, idx);
			queue.add(data[idx]);
			assertIterator(queue, data, idx+1);
		}
	}
	
	public void testIteratorThrowsOnConcurrentModification() {
		
		final Object[] elements = new Object[] { "foo", "bar" };
		final Queue4 queue = newQueue(elements);
		final Iterator4 iterator = queue.iterator();
		Iterator4Assert.assertNext("foo", iterator);
		queue.add("baz");
		
		Assert.areEqual("foo", iterator.current(), "accessing current element should be harmless");
		
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				iterator.moveNext();
			}
		});
	}
	
	public void testNextMatchingFailure() {
		
		final Object[] elements = new Object[] { "foo", "bar" };
		final Queue4 queue = newQueue(elements);
		Assert.isNull(queue.nextMatching(new Predicate4() {
			public boolean match(Object candidate) {
				return false;
			}
		}));
		assertNext(elements, queue);
	}	
	
	public void testNextMatchingOnEmptyQueue() {

		final Object[] empty = new Object[0];
		assertNextMatching(empty, null, empty);
	}
	
	public void testNextMatching() {
		
		final Object first = "42";
		final Object second = new Integer(42);
		final Object last = new Float(42.0);
		
		final Object[] elements = new Object[] { first, second, last };
		
		assertNextMatching(new Object[] { first, last }, second, elements);
		assertNextMatching(new Object[] { second, last }, first, elements);
		assertNextMatching(new Object[] { first, second }, last, elements);
	}

	private void assertNextMatching(final Object[] expectedAfterRemoval,
			final Object removedElement,
			final Object[] originalElements) {
		final Queue4 queue = newQueue(originalElements);
		Assert.areEqual(removedElement, queue.nextMatching(new Predicate4() {
			public boolean match(Object candidate) {
				return removedElement == candidate;
			}
		}));
		assertNext(expectedAfterRemoval, queue);
	}

	private void assertNext(final Object[] expected, final Queue4 queue) {
		for (int i = 0; i < expected.length; i++) {
			Object object = expected[i];
			Assert.isTrue(queue.hasNext(), "Expecting '" + object + "'");
			Assert.areSame(object, queue.next());
		}
		Assert.isFalse(queue.hasNext());
	}

	private Queue4 newQueue(Object[] items) {
		final NonblockingQueue queue = new NonblockingQueue();
		for (int i = 0; i < items.length; i++) {
			queue.add(items[i]);
		}
		return queue;
	}
	
	public void testNext() {
		Queue4 queue = new NonblockingQueue();
		String[] data = { "a", "b", "c" };
		queue.add(data[0]);
		Assert.areSame(data[0], queue.next());
		queue.add(data[1]);
		queue.add(data[2]);
		assertNext(new Object[] { data[1], data[2] }, queue);
	}
	
}
