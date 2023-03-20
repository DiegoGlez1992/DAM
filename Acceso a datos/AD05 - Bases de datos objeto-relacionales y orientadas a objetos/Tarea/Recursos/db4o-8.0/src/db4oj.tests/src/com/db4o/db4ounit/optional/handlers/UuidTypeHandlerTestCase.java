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
package com.db4o.db4ounit.optional.handlers;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @sharpen.remove
 */
@decaf.Remove
public class UuidTypeHandlerTestCase extends AbstractDb4oTestCase {

	private static final UUID UUID_1_2 = new UUID(20L, 40L);
	private static final UUID UUID_2_1 = new UUID(300L, 100L);
	private static final UUID UUID_2_2 = new UUID(300L, 200L);
	
	private static final UUID[] VALUES = {
		null,
		UUID_1_2,
		UUID_2_1,
		UUID_2_2
	};
	
	public static class Item {
		public int _id;
		public UUID _typed;
		public Object _untyped;
		
		public Item(int id, UUID value) {
			_id = id;
			_typed = value;
			_untyped = value;
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.registerTypeHandler(new SingleClassTypeHandlerPredicate(UUID.class), new UuidTypeHandler());
		indexField(config, Item.class, "_typed");
	}

	@Override
	protected void store() throws Exception {
		int idx = 0;
		for (UUID uuid : VALUES) {
			store(new Item(idx++, uuid));
		}
	}

	public void testRetrieval() {
		assertRetrievedAsStored();
	}

	public void testUpdate() throws Exception {
		ObjectSet<Item> result = db().query(Item.class);
		while (result.hasNext()) {
			Item item = result.next();
			item._typed = typedUpdateValueFor(item._typed);
			item._untyped = untypedUpdateValueFor(item._untyped);
			store(item);
		}
		reopen();
		assertRetrieved(new Procedure4<Item>() {
			public void apply(Item item) {
				UUID expectedBase = VALUES[item._id];
				Assert.areEqual(typedUpdateValueFor(expectedBase), item._typed);
				Assert.areEqual(untypedUpdateValueFor(expectedBase), item._untyped);
			}
		});
	}

	private UUID untypedUpdateValueFor(Object object) {
		if (object == null) {
			return UUID_1_2;
		} else {
			UUID uuid = (UUID) object;
			long msb = uuid.getMostSignificantBits();
			long lsb = uuid.getLeastSignificantBits();
			return new UUID(msb+1, lsb*lsb);
		}
    }

	private UUID typedUpdateValueFor(UUID value) {
		if (value == null) {
			return UUID_1_2;
		} else {
			long msb = value.getMostSignificantBits();
			long lsb = value.getLeastSignificantBits();
			return new UUID(msb*msb, lsb*lsb);
		}
    }
	
	public void testOrderAscendingByTypedField() {
		final Query query = newItemQuery();
		query.descend("_typed").orderAscending();
		Iterator4Assert.areEqual(VALUES, typedValuesFrom(query.execute()));
	}

	private Iterator4 typedValuesFrom(ObjectSet<?> objectSet) {
		final Collection4 result = new Collection4();
		while (objectSet.hasNext()) {
			result.add(((Item)objectSet.next())._typed);
		}
		return result.iterator();
    }

	public void testSingleDescendTypedField() {
		assertTypedQuery(VALUES[0]);
	}

	public void testDescendTypedField() {
		for (UUID value : VALUES) {
			assertTypedQuery(value);
		}
	}

	public void testSingleDescendTypedFieldRange() {
	    Query query = newItemQuery();
	    query.descend("_typed").constrain(VALUES[1]).greater();
	    ObjectSet<Item> result = query.execute();
	    Assert.areEqual(VALUES.length - 2, result.size());
	}

	private void assertTypedQuery(UUID value) {
	    Query query = newItemQuery();
	    query.descend("_typed").constrain(value);
	    ObjectSet<Item> result = query.execute();
	    Assert.areEqual(1, result.size());
	    final Item found = result.next();
	    Assert.areEqual(value, found._typed);
	    Assert.areEqual(value, found._untyped);
    }

	private Query newItemQuery() {
	    return newQuery(Item.class);
    }
	
	public void testDelete() throws Exception {
		deleteAll(Item.class);
		assertOccurrences(Item.class, 0);
	}
	
	public void testDefrag() throws Exception {
		defragment();
		assertRetrievedAsStored();
	}
	
	private void assertRetrieved(Procedure4<Item> check) {
		Query query = newItemQuery();
		query.descend("_id").orderAscending();
		ObjectSet<Item> result = query.execute();
		Assert.areEqual(VALUES.length, result.size());
		while (result.hasNext()) {
			check.apply(result.next());
		}
	}

	private void assertRetrievedAsStored() {
		assertRetrieved(new Procedure4<Item>() {
			public void apply(Item item) {
				UUID expected = VALUES[item._id];
				Assert.areEqual(expected, item._typed);
				Assert.areEqual(expected, item._untyped);
			}
		});
	}

}
