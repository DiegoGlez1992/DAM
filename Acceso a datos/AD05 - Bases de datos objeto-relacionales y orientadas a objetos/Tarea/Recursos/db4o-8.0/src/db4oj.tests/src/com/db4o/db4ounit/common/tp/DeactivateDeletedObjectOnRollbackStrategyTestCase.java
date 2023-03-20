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

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeactivateDeletedObjectOnRollbackStrategyTestCase extends
		AbstractDb4oTestCase {
	
	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		
		config.add(
				new TransparentPersistenceSupport(
						new RollbackStrategy() {
							public void rollback(ObjectContainer container, Object obj) {
								container.ext().deactivate(obj);
							}
						})
				);						
	}
	
	protected void store() throws Exception {
		db().store(new Item("foo.tbd"));
	}
	
	public void test() {
		Item tbd = insertAndRetrieve();
		
		tbd.setName("foo.deleted");		
		db().delete(tbd);
		
		db().rollback();
		Assert.areEqual("foo.tbd", tbd.getName());
	}

	private Item insertAndRetrieve() {
		Query query = newQuery(Item.class);
		query.descend("name").constrain("foo.tbd");		
		ObjectSet set = query.execute();
		Assert.areEqual(1, set.size());
		
		return (Item) set.next();
	}
	
	public static void main(String[] args) {
		new DeactivateDeletedObjectOnRollbackStrategyTestCase().runAll();
	}
}
