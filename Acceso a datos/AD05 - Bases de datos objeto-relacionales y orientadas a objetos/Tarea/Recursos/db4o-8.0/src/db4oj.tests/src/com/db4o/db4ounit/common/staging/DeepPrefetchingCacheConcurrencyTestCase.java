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
package com.db4o.db4ounit.common.staging;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * COR-1762
 */
public class DeepPrefetchingCacheConcurrencyTestCase extends AbstractDb4oTestCase implements OptOutAllButNetworkingCS {
	
	public static class Item {

		public String _name;

		public Item(String name) {
			_name = name;
		}

	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		ClientConfiguration clientConfiguration = Db4oClientServerLegacyConfigurationBridge.asClientConfiguration(config);
		clientConfiguration.prefetchDepth(3);
		clientConfiguration.prefetchObjectCount(3);
	}

	@Override
	protected void store() throws Exception {
		for (int i = 0; i < 2; i++) {
			Item item = new Item("original");
			store(item);
		}
	}
	
	public void test(){
		int[] ids = new int[2];
		
		ObjectSet<Item> originalResult = newQuery(Item.class).execute();
		Item firstOriginalItem = originalResult.next();
		db().purge(firstOriginalItem);
		
		ExtObjectContainer otherClient = openNewSession();
		ObjectSet<Item> updateResult = otherClient.query(Item.class);
		int idx = 0;
		for(Item updateItem : updateResult){
			ids[idx] = (int) otherClient.getID(updateItem);
			updateItem._name = "updated";
			otherClient.store(updateItem);
			idx++;
		}
		otherClient.commit();
		otherClient.close();
		
		for (int i = 0; i < ids.length; i++) {
			Item checkItem = db().getByID(ids[i]);
			db().activate(checkItem);
			Assert.areEqual("updated", checkItem._name);
		}
		
//		ObjectSet<Item> checkResult = newQuery(Item.class).execute();
//		for (Item checkItem : checkResult) {
//			Assert.areEqual("updated", checkItem._name);
//		}
	}
	
	

}
