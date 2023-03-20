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
package com.db4o.db4ounit.jre12.foundation;

import java.util.*;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class IterableBaseTestCase implements TestCase {

	private static final String[] ITEMS = {"a", "b", "c"};
	
	private static class CustomIterable {
		public Iterator iterator() {
			return createList().iterator();
		}
	}
	
	public void testReflectionIterableBase() throws Exception {
		Assert.expect(NoSuchMethodException.class, new CodeBlock() {
			public void run() throws Throwable {
				new ReflectionIterableBase(new Object());
			}
		});
		CustomIterable delegate = new CustomIterable();
		ReflectionIterableBase iterable = new ReflectionIterableBase(delegate);
		assertIterableWrapper(iterable, delegate);
	}

	public void testCollectionIterableBase() throws Exception {
		List list = createList();
		CollectionIterableBase iterable = new CollectionIterableBase(list);
		assertIterableWrapper(iterable, list);
	}

	public void testIterableBaseFactory() {
		CustomIterable customIterable = new CustomIterable();
		IterableBaseWrapper customCoerced = (IterableBaseWrapper) IterableBaseFactory.coerce(customIterable);
		Assert.isInstanceOf(ReflectionIterableBase.class, customCoerced);
		Assert.areSame(customIterable, IterableBaseFactory.unwrap(customCoerced));
		List list = createList();
		IterableBase listCoerced = IterableBaseFactory.coerce(list);
		Assert.isInstanceOf(CollectionIterableBase.class, listCoerced);
		Assert.areSame(list, IterableBaseFactory.unwrap(listCoerced));
	}
	
	private void assertIterableWrapper(IterableBaseWrapper iterable, Object delegate) throws Exception {
		IteratorAssert.areEqual(ITEMS, iterable.iterator());
		Assert.areSame(delegate, iterable.delegate());
	}

	private static List createList() {
		return Arrays.asList(ITEMS);
	}
}
