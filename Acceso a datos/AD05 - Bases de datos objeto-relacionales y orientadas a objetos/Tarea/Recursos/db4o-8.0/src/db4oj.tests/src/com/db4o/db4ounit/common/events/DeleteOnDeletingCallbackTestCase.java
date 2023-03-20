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
package com.db4o.db4ounit.common.events;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.internal.*;
import com.db4o.events.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class DeleteOnDeletingCallbackTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
	}
	
	public static class RootItem {
		
		public Item child;
		
		public RootItem() {
		}
		
		public void objectOnDelete(ObjectContainer container) {
			container.delete(child);
		}
	}
	
	@Override
	protected void store() throws Exception {
		store(new RootItem());
	}
	
	public void test() throws Exception {
		
		final BooleanByRef disconnected = new BooleanByRef();
		final Lock4 lock = new Lock4();
		if(isNetworking()){
			Db4oClientServerFixture clientServerFixture = (Db4oClientServerFixture) fixture();
			ObjectServerEvents objectServerEvents = (ObjectServerEvents) clientServerFixture.server();
			objectServerEvents.clientDisconnected().addListener(new EventListener4<StringEventArgs>() { public void onEvent(Event4<StringEventArgs> e, StringEventArgs args) {
				lock.run(new Closure4() { public Object run() {
					disconnected.value = true;
					lock.awake();
					return null;
				}});
				
			}});
		}
		
		final RootItem root = retrieveOnlyInstance(RootItem.class);
		root.child = new Item();
		db().store(root);
		db().delete(root);
		reopen();
		
		if(isNetworking()){
			lock.run(new Closure4() {
				public Object run() {
					if(! disconnected.value){
						lock.snooze(1000000);
					}
					return null;
				}
			});
		}
		
		assertClassIndexIsEmpty();
	}

	private void assertClassIndexIsEmpty() {
	    Iterator4Assert.areEqual(new Object[0], getAllIds());
    }

	private IntIterator4 getAllIds() {
	    return fileSession().getAll(fileSession().transaction(), QueryEvaluationMode.IMMEDIATE).iterateIDs();
    }

}
