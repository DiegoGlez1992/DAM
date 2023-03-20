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
package com.db4o.test.nativequery;

import java.util.*;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.query.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class NQUnoptimizableCollectionMethodTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {

	public static class Item {
		public ArrayList<String> _data;
		
		public Item(int size) {
			_data = new ArrayList<String>(size);
			for (int i = 0; i < size; i++) {
				_data.add(String.valueOf(i));
			}
		}
	}

	private static final int MAX_SIZE = 5;
	
	@Override
	protected void store() throws Exception {
		for (int i = 0; i < MAX_SIZE; i++) {
			store(new Item(i));
		}
	}

	public void testSize() {
		assertNotOptimized(new Predicate<Item>() {
			@Override
			public boolean match(Item candidate) {
				return candidate._data.size() == MAX_SIZE - 1;
			}
		}, 1);
	}

	public void testIsEmpty() {
		assertNotOptimized(new Predicate<Item>() {
			@Override
			public boolean match(Item candidate) {
				return candidate._data.isEmpty();
			}
		}, 1);
	}

	private void assertNotOptimized(Predicate<Item> predicate, int expectedSize) {
		final BooleanByRef optimized = new BooleanByRef();
		((LocalObjectContainer)db()).getNativeQueryHandler().addListener(new Db4oQueryExecutionListener() {
			public void notifyQueryExecuted(NQOptimizationInfo info) {
				optimized.value = info.optimized() != null;
			}
		});
		ObjectSet<Item> result = db().query(predicate);
		Assert.areEqual(expectedSize, result.size());
		Assert.isFalse(optimized.value);
	}
}
