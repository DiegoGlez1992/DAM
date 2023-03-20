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
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class ObjectSetTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new ObjectSetTestCase().runNetworking();
    }
	
	public static class Item {
		public String name;
		
		public Item() {			
		}
		
		public Item(String name_) {
			name = name_;
		}
		
		public String toString() {
			return "Item(\"" + name + "\")";
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.queries().evaluationMode(QueryEvaluationMode.LAZY);
	}
	
	protected void store() throws Exception {
		db().store(new Item("foo"));
		db().store(new Item("bar"));
		db().store(new Item("baz"));
	}
	
	public void _testObjectsCantBeSeenAfterDelete() {
		final Transaction trans1 = newTransaction();
		final Transaction trans2 = newTransaction();
		deleteItemAndCommit(trans2, "foo");
		
		final ObjectSet os = queryItems(trans1);
		assertItems(new String[] { "bar", "baz" }, os);
	}
	
	public void _testAccessOrder() {
		ObjectSet result = newQuery(Item.class).execute();
		for (int i=0; i < result.size(); ++i) {
			Assert.isTrue(result.hasNext());
			Assert.areSame(result.ext().get(i), result.next());
		}
		Assert.isFalse(result.hasNext());
	}

	public void testInvalidNext() {
		Query query = newQuery(Item.class);
		query.descend("name").constrain("foo");
		final ObjectSet result = query.execute();
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				while(true) {
					result.hasNext();
					result.next();
				}
			}
		});
	}
	
	private void assertItems(String[] expectedNames, ObjectSet actual) {
		for (int i = 0; i < expectedNames.length; i++) {
			Assert.isTrue(actual.hasNext());
			Assert.areEqual(expectedNames[i], ((Item)actual.next()).name);
		}
		Assert.isFalse(actual.hasNext());
	}

	private void deleteItemAndCommit(Transaction trans, String name) {
		container().delete(trans, queryItem(trans, name));
		trans.commit();
	}

	private Item queryItem(Transaction trans, String name) {
		final Query q = newQuery(trans, Item.class);
		q.descend("name").constrain(name);
		return (Item) q.execute().next();
	}

	private ObjectSet queryItems(final Transaction trans) {
		final Query q = newQuery(trans, Item.class);
		q.descend("name").orderAscending();
		return q.execute();
	}

}
