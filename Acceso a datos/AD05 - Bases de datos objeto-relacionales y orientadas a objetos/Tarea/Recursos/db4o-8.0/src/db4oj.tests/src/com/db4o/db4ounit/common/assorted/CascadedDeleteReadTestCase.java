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
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class CascadedDeleteReadTestCase extends AbstractDb4oTestCase{

	
	public static class Item {
		
		public Item _child1;
		
		public Item _child2;
		
		public String _name;
		
		public Item() {
			
		}
		
		public Item(String name) {
			_name = name;
		}

		public Item(Item child1, Item child2, String name) {
			_child1 = child1;
			_child2 = child2;
			_name = name;
		}
	
	}
	

	public static void main(String[] args) {
		new CascadedDeleteReadTestCase().runSoloAndClientServer();
	}
	
	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		config.objectClass(Item.class).objectField("_child1").cascadeOnDelete(true);
		config.objectClass(Item.class).objectField("_child2").cascadeOnDelete(true);
		config.objectClass(Item.class).objectField("_child1").cascadeOnUpdate(true);
		config.objectClass(Item.class).objectField("_child2").cascadeOnUpdate(true);
	}
	
	protected void store() throws Exception {
		store(new Item(new Item("1"), null, "parent"));
	}
	
	public void test(){
		Item item = parentItem();
		item._child2 = item._child1;
		item._child1 = null;
		store(item);
		db().delete(item);
		assertItemCount(0);
	}
	
	private Item parentItem(){
		Query q = db().query();
		q.constrain(Item.class);
		q.descend("_name").constrain("parent");
		return (Item)q.execute().next();
	}

	private void assertItemCount(int count) {
		Query q = db().query();
		q.constrain(Item.class);
		ObjectSet objectSet = q.execute();
		Assert.areEqual(count, objectSet.size());
	}

}
