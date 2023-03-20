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
package com.db4o.db4ounit.jre5.enums;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class OrderByWithEnumsTestCase extends AbstractDb4oTestCase {

	public static enum ItemEnum {
		FIRST, SECOND
	}
	
	public static class Item {
		public int _id;
		public ItemEnum _itemEnum;
		
		public Item(int id, ItemEnum itemEnum) {
			_id = id;
			_itemEnum = itemEnum;
		}
		
		public ItemEnum itemEnum() {
			return _itemEnum;
		}
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item(1, ItemEnum.FIRST));
		store(new Item(2, null));
		store(new Item(3, ItemEnum.SECOND));
		store(new Item(4, null));
	}

	public void testOrderByWithEnums() {
		Query query = newQuery();
		query.constrain(Item.class);
		query.descend("_id").constrain(1).or(query.descend("_id").constrain(3));
		query.descend("_itemEnum").orderAscending();
		ObjectSet<Item> result = query.execute();
		Assert.areEqual(2, result.size());
		Assert.areEqual(ItemEnum.FIRST, result.next().itemEnum());
		Assert.areEqual(ItemEnum.SECOND, result.next().itemEnum());
	}

	public void testOrderByWithNullValues() {
		Query query = newQuery();
		query.constrain(Item.class);
		query.descend("_itemEnum").orderAscending();
		ObjectSet<Item> result = query.execute();
		Assert.areEqual(4, result.size());
		Assert.isNull(result.next().itemEnum());
		Assert.isNull(result.next().itemEnum());
		Assert.areEqual(ItemEnum.FIRST, result.next().itemEnum());
		Assert.areEqual(ItemEnum.SECOND, result.next().itemEnum());
	}
	
}
