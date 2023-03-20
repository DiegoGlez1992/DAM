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

public class SameChildOnDifferentParentQueryTestCase extends AbstractDb4oTestCase {

	public static class Holder {
		
		public Item _child;
		
		public Holder(Item belongs) {
			_child = belongs;
		}
	}
	
	public static class Item {
		
		public String _name;
		
		public Item(String name) {
			_name = name;
		}
	}
	
	@Override
	protected void store() throws Exception {
		
		Item unique = new Item("unique");
		Item shared = new Item("shared");

		store(new Holder(shared));
		store(new Holder(unique));
		store(new Holder(shared));
	}

	public void testUniqueResult() {
		Query query = db().query();
		query.constrain(Holder.class);
		query.descend("_child").descend("_name").constrain("unique");

		ObjectSet<Holder> result = query.execute();
		Assert.areEqual(1, result.size());
		Holder holder = result.next();
		Assert.areEqual("unique", holder._child._name);
	}
	
}