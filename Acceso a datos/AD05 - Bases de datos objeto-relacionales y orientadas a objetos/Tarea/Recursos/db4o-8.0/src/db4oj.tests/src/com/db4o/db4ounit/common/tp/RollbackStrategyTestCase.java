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
package com.db4o.db4ounit.common.tp;

import com.db4o.config.*;
import com.db4o.ta.*;

import db4ounit.extensions.*;
import db4ounit.mocking.*;

public class RollbackStrategyTestCase extends AbstractDb4oTestCase {
	
	private final RollbackStrategyMock _mock = new RollbackStrategyMock();
	
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport(_mock));
	}
	
	public void testRollbackStrategyIsCalledForChangedObjects() {
		Item item1 = storeItem("foo");
		Item item2 = storeItem("bar");
		storeItem("baz");
		
		change(item1);
		change(item2);
		
		_mock.verify(new MethodCall[0]);
		
		db().rollback();
		
		_mock.verifyUnordered(new MethodCall[] {
			new MethodCall("rollback", db(), item1),
			new MethodCall("rollback", db(), item2),
		});
		
	}

	private void change(Item item) {
		item.setName(item.getName() + "*");
	}

	private Item storeItem(String name) {
		final Item item = new Item(name);
		store(item);
		return item;
	}
	
	public static void main(String []args) {
		new RollbackStrategyTestCase().runAll();
	}

}
