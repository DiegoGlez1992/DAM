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
package com.db4o.db4ounit.common.config;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class VersionNumbersTestCase extends AbstractDb4oTestCase{
	
	private static final String ORIGINAL = "original";
	private static final String NEWER = "newer";

	public static class Item {
		public Item(String _name) {
			super();
			this._name = _name;
		}

		public String _name;
	}
	
	public static void main(String[] args) {
		new VersionNumbersTestCase().runAll();
	}
	
	protected void configure(Configuration config) throws Exception {
		config.generateCommitTimestamps(true);
	}
	
	protected void store() throws Exception {
		store(new Item(ORIGINAL));
	}
	
	public void testVersionIncrease(){
		
		Item item = (Item) retrieveOnlyInstance(Item.class);
		ObjectInfo objectInfo = db().getObjectInfo(item);
		long version1 = objectInfo.getCommitTimestamp();
		item._name = "modified";
		db().store(item);
		db().commit();
		long version2 = objectInfo.getCommitTimestamp();
		Assert.isGreater(version1, version2);
		db().store(item);
		db().commit();
		objectInfo = db().getObjectInfo(item);
		long version3 = objectInfo.getCommitTimestamp();
		Assert.isGreater(version2, version3);
	}

	public void testTransactionConsistentVersion() throws Exception{
		
		store(new Item(NEWER));
		db().commit();
		
		Item newer = itemByName(NEWER);
		Item original = itemByName(ORIGINAL);
		
		Assert.isGreater(version(original), version(newer));
		
		newer._name += " modified";
		original._name += " modified";
		
		store(newer);
		store(original);
		
		db().commit();
		
		Assert.areEqual(version(newer), version(original));
		
		reopen();
		
		newer = itemByName(newer._name);
		original = itemByName(original._name);
		
		Assert.areEqual(version(newer), version(original));
	}

	private long version(Object obj) {
		return db().getObjectInfo(obj).getCommitTimestamp();
	}

	private Item itemByName(String string) {
		Query q = db().query();
		q.constrain(Item.class);
		q.descend("_name").constrain(string);
		Object object = q.execute().next();
		return (Item) object;
	}
	
	public void testQueryForVersionNumber() {
		store(new Item(NEWER));
		db().commit();
		
		Item newer = itemByName(NEWER);
		
		long version = version(newer);
		
		Query query = db().query();
		query.descend(VirtualField.COMMIT_TIMESTAMP).constrain(
			new Long(version)).smaller().not();
		ObjectSet<Item> set = query.execute();
		Assert.areEqual(1, set.size());
		Assert.areSame(newer, set.next());
	}

}
