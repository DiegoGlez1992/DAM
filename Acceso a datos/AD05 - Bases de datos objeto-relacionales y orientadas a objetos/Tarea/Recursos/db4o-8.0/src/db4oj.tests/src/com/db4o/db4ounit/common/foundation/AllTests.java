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


import db4ounit.*;


public class AllTests extends ReflectionTestSuite {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(AllTests.class).run();
	}

	@Override
	protected Class[] testCases() {
		return new Class[] {
			Algorithms4TestCase.class,
			ArrayIterator4TestCase.class,
			Arrays4TestCase.class,
			BitMap4TestCase.class,
			BlockingQueueTestCase.class,
			PausableBlockingQueueTestCase.class,
			BufferTestCase.class,
			CircularBufferTestCase.class,
			Collection4TestCase.class,
			Collections4TestCase.class,
			CompositeIterator4TestCase.class,
			Runtime4TestCase.class,
			DynamicVariableTestCase.class,
			EnvironmentsTestCase.class,
			HashSet4TestCase.class,
			Hashtable4TestCase.class,
			IdentityHashtable4TestCase.class,
			IdentitySet4TestCase.class,
			IntArrayListTestCase.class,
			IntMatcherTestCase.class,
			Iterable4AdaptorTestCase.class,
			IteratorsTestCase.class,
			Map4TestCase.class,
			NoDuplicatesQueueTestCase.class,
			NonblockingQueueTestCase.class,
			ObjectPoolTestCase.class,
			Path4TestCase.class,
			SortedCollection4TestCase.class,
			Stack4TestCase.class,
			TimeStampIdGeneratorTestCase.class,
			TreeKeyIteratorTestCase.class,
			TreeNodeIteratorTestCase.class,
			TreeTestCase.class,
		};
	}

}
