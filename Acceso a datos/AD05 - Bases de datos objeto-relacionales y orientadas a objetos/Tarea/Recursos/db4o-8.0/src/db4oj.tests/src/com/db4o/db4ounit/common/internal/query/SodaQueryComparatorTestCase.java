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
package com.db4o.db4ounit.common.internal.query;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.query.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class SodaQueryComparatorTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {

	@Override
	protected void store() throws Exception {
		storeItem(1, "bb", "ca");
		storeItem(2, "aa", "cb");
	}
	
	public void testNullInThePath() {
		store(new Item(3, "cc", null));
		final Integer[] expectedItemIds = new Integer[] { 3, 1, 2 };
		assertQuery(expectedItemIds, ascending("child", "name"));
	}
	
	public void testFirstLevelAscending() {
		final Integer[] expectedItems = new Integer[] { 2, 1 };
		assertQuery(expectedItems, ascending("name"));
	}
		
	public void testSecondLevelAscending() {
		final Integer[] expectedItems = new Integer[] { 1, 2 };
		assertQuery(expectedItems, ascending("child", "name"));
	}
	
	public void testFirstLevelThenSecondLevel() {
		storeItem(3, "aa", "cc");
		storeItem(4, "bb", "cc");
		
		final Integer[] expectedItems = new Integer[] { 2, 3, 1, 4 };
		assertQuery(expectedItems,
				ascending("name"),
				ascending("child", "name"));
	}
	
	public void testSecondLevelThenFirstLevel() {
		storeItem(3, "cc", "ca");
		storeItem(4, "cc", "ce");
		
		final Integer[] expectedItems = new Integer[] { 1, 3, 2, 4 };
		assertQuery(expectedItems,
				ascending("child", "name"),
				ascending("name"));
	}
	
	public void testFirstLevelDescending() {
		final Integer[] expectedItems = new Integer[] { 1, 2 };
		assertQuery(expectedItems, descending("name"));
	}
		
	public void testSecondLevelDescending() {
		final Integer[] expectedItems = new Integer[] { 2, 1 };
		assertQuery(expectedItems, descending("child", "name"));
	}
	
	public void testFirstLevelThenSecondLevelDescending() {
		storeItem(3, "aa", "cc");
		storeItem(4, "bb", "cc");
		
		final Integer[] expectedItems = new Integer[] { 4, 1, 3, 2 };
		assertQuery(expectedItems,
				descending("name"),
				descending("child", "name"));
	}
	
	public void testSecondLevelThenFirstLevelDescending() {
		storeItem(3, "cc", "ca");
		storeItem(4, "cc", "ce");
		
		final Integer[] expectedItems = new Integer[] { 4, 2, 3, 1 };
		assertQuery(expectedItems,
				descending("child", "name"),
				descending("name"));
	}
	
	public void testFirstLevelAscendingThenSecondLevelDescending() {
		storeItem(3, "aa", "cc");
		storeItem(4, "bb", "cc");
		
		final Integer[] expectedItems = new Integer[] { 3, 2, 4, 1 };
		assertQuery(expectedItems,
				ascending("name"),
				descending("child", "name"));
	}
	
	public void testSecondLevelAscendingThenFirstLevelDescending() {
		storeItem(3, "cc", "ca");
		storeItem(4, "cc", "ce");
		
		final Integer[] expectedItems = new Integer[] { 3, 1, 2, 4  };
		assertQuery(expectedItems,
				ascending("child", "name"),
				descending("name"));
	}

	private SodaQueryComparator.Ordering ascending(String... fieldPath) {
		return new SodaQueryComparator.Ordering(SodaQueryComparator.Direction.ASCENDING, fieldPath);
	}
	
	private SodaQueryComparator.Ordering descending(String... fieldPath) {
		return new SodaQueryComparator.Ordering(SodaQueryComparator.Direction.DESCENDING, fieldPath);
	}

	private void storeItem(int id, String name, String childName) {
		store(new Item(id, name, new ItemChild(childName)));
	}

	private void assertQuery(final Integer[] expectedItemIds, SodaQueryComparator.Ordering... orderings) {
		final long[] ids = newQuery(Item.class).execute().ext().getIDs();
		final List<Integer> sorted = new SodaQueryComparator(fileSession(), Item.class, orderings).sort(ids);
		Iterator4Assert.areEqual(
				Iterators.map(expectedItemIds, oidByItemId),
				Iterators.iterator(sorted));
	}
	
	final Function4<Integer, Integer> oidByItemId = new Function4<Integer, Integer>() {
		public Integer apply(Integer id) {
			final int oid = itemByName(id);
//			System.out.println(id + " -> " + oid);
			return oid;
		}
	};
	
	private int itemByName(Integer id) {
		final Query query = newQuery(Item.class);
		query.descend("id").constrain(id);
		return (int)query.execute().ext().getIDs()[0];
	}
	
	public static class Item {
		
		public Item(int id, String name, ItemChild child) {
			this.id = id;
			this.name = name;
			this.child = child;
		}
		
		public int id;
		public String name;
		public ItemChild child;
	}
	
	public static class ItemChild {
		
		public ItemChild(String name) {
			this.name = name;
		}
		
		public String name;
	}
	
}
