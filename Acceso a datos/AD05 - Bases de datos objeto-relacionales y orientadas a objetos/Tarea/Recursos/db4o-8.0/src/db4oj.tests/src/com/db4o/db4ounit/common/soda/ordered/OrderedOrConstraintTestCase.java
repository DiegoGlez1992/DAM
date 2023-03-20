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

/**
 * COR-1062
 */
public class OrderedOrConstraintTestCase extends AbstractDb4oTestCase{
	
	public static class Item{
		
		public Item(int int_, boolean boolean_) {
			_int = int_;
			_boolean = boolean_;
		}

		public int _int;
		
		public boolean _boolean;
		
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item(10, false));
		store(new Item(4, true));
		super.store();
	}
	
	public void test(){
		Query query = newQuery(Item.class);
		Constraint c1 = query.descend("_int").constrain(9).greater();
		Constraint c2 = query.descend("_boolean").constrain(true);
		c1.or(c2);
		query.descend("_int").orderAscending();
		ObjectSet<Item> objectSet = query.execute();
		Assert.areEqual(2, objectSet.size());
		Item item = objectSet.next();
		Assert.areEqual(4, item._int);
		item = objectSet.next();
		Assert.areEqual(10, item._int);
	}
	
	

}
