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
package com.db4o.db4ounit.common.api;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.internal.*;
import com.db4o.cs.internal.ClientObjectContainer.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class StoreAllTestCase extends AbstractDb4oTestCase{
	
	public static void main(String[] args) {
		new StoreAllTestCase().runAll();
	}
	
	public static class Item{
		
		public int _id;
		
		public Item(int id){
			_id = id;
		}

		@Override
		public boolean equals(Object obj) {
			Item other = (Item) obj;
			return _id != other._id;
		}
		
	}
	
	Item item1 = new Item(1);
	
	Item item2 = new Item(2);
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.clientServer().batchMessages(false);
	}
	
	public void test(){
		storeAll(container());
		ObjectSetAssert.sameContent(queryAllItems(), item1, item2);
	}

	private void storeAll(InternalObjectContainer internalObjectContainer) {
		internalObjectContainer.storeAll(trans(), Iterators.iterate(item1, item2));
	}
	
	public void testClientSendsSingleMessage(){
		if(! (container() instanceof ClientObjectContainer)){
			return;
		}
		ClientObjectContainer clientObjectContainer = (ClientObjectContainer) container();
		final ArrayList<Msg> messages = new ArrayList();
		MessageListener listener = new MessageListener(){
			public void onMessage(Msg msg) {
				messages.add(msg);
			}
		};
		db().store(new Item(0));  // class creation
		clientObjectContainer.messageListener(listener);
		storeAll(clientObjectContainer);
		clientObjectContainer.commit();
		Assert.areEqual(1, messages.size());
	}

	private ObjectSet<Object> queryAllItems() {
		com.db4o.query.Query q = db().query();
		q.constrain(Item.class);
		return q.<Object>execute();
	}

}
