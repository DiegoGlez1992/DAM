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
package com.db4o.db4ounit.common.assorted;

import com.db4o.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CanUpdateFalseRefreshTestCase extends AbstractDb4oTestCase{
	
	public static class Item {
		
		public int _id;
		
		public String _name;

		public Item(int id, String name) {
			_id = id;
			_name = name;
		}

		public boolean objectCanUpdate(ObjectContainer container) {
			return false;
		}
	}
	
	protected void store() throws Exception {
		store(new Item(1, "one"));
	}
	
	public void test(){
		Item item = (Item) retrieveOnlyInstance(Item.class);
		item._name = "two";
		db().store(item);
		
		Assert.areEqual("two", item._name);
		db().refresh(item, 2);
		Assert.areEqual("one", item._name);
	}

	public static void main(String[] args) {
		new CanUpdateFalseRefreshTestCase().runSoloAndClientServer();
	}

}
