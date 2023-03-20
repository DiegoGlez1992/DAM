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
package com.db4o.db4ounit.common.querying;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class OrderedQueryTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new OrderedQueryTestCase().runSolo();
	}
	
	public static final class Item {
		public int value;
		
		public Item(int value) {
			this.value = value;
		}
	}
	
	public static class Item2 {
	    public String _name;
	    
	    public Item2(String name) {
	        _name = name;
	    }
	}
	
	protected void store() throws Exception {
		db().store(new Item(1));
		db().store(new Item(3));
		db().store(new Item(2));
	}

	public void testOrderAscending() {
		final Query query = newQuery(Item.class);
		query.descend("value").orderAscending();
		assertQuery(new int[] { 1, 2, 3 }, query.execute());
	}
	
	public void testOrderDescending() {
		final Query query = newQuery(Item.class);
		query.descend("value").orderDescending();
		assertQuery(new int[] { 3, 2, 1 }, query.execute());
	}

	public void _testCOR1212() {
	    store(new Item2("Item 2"));
	    Query query = newQuery();
	    query.constrain(Item.class).or(query.constrain(Item2.class));
	    query.descend("value").orderAscending();
	    ObjectSet result = query.execute();
	    assertQuery(new int[] { 1, 2, 3 }, result);
	}
	
	private void assertQuery(int[] expected, ObjectSet actual) {
		for (int i = 0; i < expected.length; i++) {
			Assert.isTrue(actual.hasNext());
			Assert.areEqual(expected[i], ((Item)actual.next()).value);
		}
		Assert.isFalse(actual.hasNext());
	}
}
