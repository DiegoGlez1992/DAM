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
package com.db4o.db4ounit.jre12.defragment;

import java.util.ArrayList;
import java.util.List;

import db4ounit.Assert;
import db4ounit.extensions.AbstractDb4oTestCase;

@decaf.Remove(decaf.Platform.JDK11)
public class DefragmentPrimitiveArrayInCollectionTestCase extends AbstractDb4oTestCase {

	private static final int ITEM_SIZE = 42;

	public static class Item {
		public List _data;
		
		public Item(int size) {
			_data = new ArrayList();
			_data.add(new byte[size]);
		}
	}

	protected void store() throws Exception {
		store(new Item(ITEM_SIZE));
	}
	
	public void testDefragment() throws Exception {
		assertItemSizes();
		defragment();
		assertItemSizes();
	}

	private void assertItemSizes() {
		Item item = (Item) retrieveOnlyInstance(Item.class);
		Assert.areEqual(1, item._data.size());
		Assert.areEqual(ITEM_SIZE, ((byte[])item._data.get(0)).length);
	}
}
