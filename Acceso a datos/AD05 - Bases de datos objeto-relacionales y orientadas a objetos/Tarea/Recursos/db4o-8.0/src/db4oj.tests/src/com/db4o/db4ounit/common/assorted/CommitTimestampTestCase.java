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
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CommitTimestampTestCase extends AbstractDb4oTestCase {
	
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.generateCommitTimestamps(true);
	}
	
	public static class Item {
		
	}
	
	public void testUpdateAndQuery(){
		
		Item item1 = new Item();
		store(item1);
		Item item2 = new Item();
		store(item2);
		commit();
		
		long initialCommitTimestamp1 = db().getObjectInfo(item1).getCommitTimestamp();
		long initialCommitTimestamp2 = db().getObjectInfo(item2).getCommitTimestamp();
		
		Assert.areEqual(initialCommitTimestamp1,initialCommitTimestamp2);
		
		store(item2);
		commit();
		
		long secondCommitTimestamp1 = db().getObjectInfo(item1).getCommitTimestamp();
		long secondCommitTimestamp2 = db().getObjectInfo(item2).getCommitTimestamp();
		
		Assert.areEqual(initialCommitTimestamp1,secondCommitTimestamp1);
		Assert.areNotEqual(initialCommitTimestamp2,secondCommitTimestamp2);
		
		assertQueryForTimestamp(item1, initialCommitTimestamp1);
		assertQueryForTimestamp(item2, secondCommitTimestamp2);
		
		
	}

	private void assertQueryForTimestamp(Item expected, long timestamp) {
		Query query = db().query();
		query.constrain(Item.class);
		query.descend(VirtualField.COMMIT_TIMESTAMP).constrain(timestamp);
		ObjectSet<Object> objectSet = query.execute();
		Assert.areEqual(1, objectSet.size());
		Item actual = (Item) objectSet.next();
		Assert.areSame(expected, actual);
	}
	
	

}
