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
package com.db4o.db4ounit.jre5.concurrency.query;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 */
@decaf.Ignore
public class ConcurrentLazyQueriesTestCase extends Db4oClientServerTestCase {

	private static final int ITEM_COUNT = 100;

	public static final class Item {
		
		public int id;
		public Item parent;
		
		public Item() {
		}
		
		public Item(Item parent_, int id_) {
			id = id_;
			parent = parent_;
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		configLazyQueries(config);
	}

	private void configLazyQueries(Configuration config) {
		config.queries().evaluationMode(QueryEvaluationMode.LAZY);
	}
	
	protected void store() throws Exception {
		Item root = new Item(null, -1);
		for (int i=0; i<ITEM_COUNT; ++i) {
			store(new Item(root, i));
		}
	}
	
	public void conc(ExtObjectContainer client) {
		
		final ExtObjectContainer container = fileSession();
		final Item root = queryRoot(container);
		for (int i=0; i<100; ++i) {
			assertAllItems(queryItems(root, container));
		}
	}

	private Item queryRoot(final ExtObjectContainer container) {
		final Query q = itemQuery(container);
		q.descend("id").constrain(new Integer(-1));
		return (Item)q.execute().next();
	}

	private void assertAllItems(final Iterator result) {
		Collection4 expected = range(ITEM_COUNT);
		for (int i=0; i<ITEM_COUNT; ++i) {
			final Item nextItem = (Item)IteratorPlatform.next(result);
			expected.remove(new Integer(nextItem.id));
		}
		Assert.areEqual("[]", expected.toString());
	}

	private Collection4 range(int end) {
		Collection4 range = new Collection4();
		for (int i=0; i<end; ++i) {
			range.add(new Integer(i));
		}
		return range;
	}

	private Iterator queryItems(Item parent, ExtObjectContainer container) {
		final Query q = itemQuery(container);
		q.descend("parent").constrain(parent).identity();
		// the cast is necessary for sharpen
		return ((Iterable)q.execute()).iterator();
	}

	private Query itemQuery(ExtObjectContainer container) {
		return newQuery(container, Item.class);
	}
}
