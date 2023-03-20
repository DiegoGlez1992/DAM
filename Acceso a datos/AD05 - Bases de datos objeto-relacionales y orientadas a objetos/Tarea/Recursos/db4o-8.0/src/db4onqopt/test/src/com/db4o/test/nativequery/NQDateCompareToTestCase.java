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
import com.db4o.internal.query.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class NQDateCompareToTestCase extends AbstractDb4oTestCase {

	public static class Item {
		public Date _date;

		public Item(Date date) {
			_date = date;
		}
	}

	@Override
	protected void store() throws Exception {
		store(new Item(new Date()));
	}
	
	public void testSingleDateCompareTo() {
		final Date cmpDate = new Date(0);
		Predicate<Item> predicate = new Predicate<Item>() {
			@Override
			public boolean match(Item item) {
				return item._date.compareTo(cmpDate) >= 0;
			}
		};
		assertDateComparison(predicate);
	}

	public void testMultipleDateCompareTo() {
		final Date cmpDatePre = new Date(0);
		final Date cmpDatePost = new Date(Long.MAX_VALUE);
		Predicate<Item> predicate = new Predicate<Item>() {
			@Override
			public boolean match(Item item) {
				return item._date.compareTo(cmpDatePre) >= 0 && item._date.compareTo(cmpDatePost) < 0;
			}
		};
		assertDateComparison(predicate);
	}

	private void assertDateComparison(Predicate<Item> predicate) {
		container().getNativeQueryHandler().addListener(new Db4oQueryExecutionListener() {
			public void notifyQueryExecuted(NQOptimizationInfo info) {
				Assert.isNotNull(info.optimized());
			}
		});
		ObjectSet<Item> result = db().query(predicate);
		Assert.areEqual(1, result.size());
	}

}
