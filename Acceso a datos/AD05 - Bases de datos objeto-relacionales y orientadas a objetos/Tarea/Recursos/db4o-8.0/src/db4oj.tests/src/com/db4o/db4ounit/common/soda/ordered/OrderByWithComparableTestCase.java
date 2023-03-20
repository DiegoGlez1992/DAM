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
package com.db4o.db4ounit.common.soda.ordered;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Remove(decaf.Platform.JDK11)
public class OrderByWithComparableTestCase extends AbstractDb4oTestCase {

	public static class ItemComparable implements Comparable {

		public int _id;
		
		public ItemComparable(int id) {
			_id = id;
		}
		
		public int compareTo(Object other) {
			ItemComparable cmp = (ItemComparable) other;
			if(_id == cmp._id) {
				return 0;
			}
			return _id < cmp._id ? -1 : 1;
		}
		
		public int id() {
			return _id;
		}
	}
	
	public static class Item {
		public int _id;
		public ItemComparable _itemCmp;
		
		public Item(int id, ItemComparable itemCmp) {
			_id = id;
			_itemCmp = itemCmp;
		}
		
		public ItemComparable itemCmp() {
			return _itemCmp;
		}
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item(1, new ItemComparable(1)));
		store(new Item(2, null));
		store(new Item(3, new ItemComparable(2)));
		store(new Item(4, null));
	}

	public void testOrderByWithEnums() {
		Query query = newQuery();
		query.constrain(Item.class);
		query.descend("_id").constrain(1).or(query.descend("_id").constrain(3));
		query.descend("_itemCmp").orderAscending();
		ObjectSet<Item> result = query.execute();
		Assert.areEqual(2, result.size());
		Assert.areEqual(1, result.next().itemCmp().id());
		Assert.areEqual(2, result.next().itemCmp().id());
	}

	public void testOrderByWithNullValues() {
		Query query = newQuery();
		query.constrain(Item.class);
		query.descend("_itemCmp").orderAscending();
		ObjectSet<Item> result = query.execute();
		Assert.areEqual(4, result.size());
		Assert.isNull(result.next().itemCmp());
		Assert.isNull(result.next().itemCmp());
		Assert.areEqual(1, result.next().itemCmp().id());
		Assert.areEqual(2, result.next().itemCmp().id());
	}
	
}
