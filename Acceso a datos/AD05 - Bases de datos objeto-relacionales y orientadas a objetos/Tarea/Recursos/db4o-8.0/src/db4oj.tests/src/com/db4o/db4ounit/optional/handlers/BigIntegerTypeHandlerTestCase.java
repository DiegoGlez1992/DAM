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

import java.math.*;

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
public class BigIntegerTypeHandlerTestCase extends AbstractDb4oTestCase {

	private static final BigInteger ZERO = new BigInteger("0");
	private static final BigInteger ONE = new BigInteger("1");
	private static final BigInteger LONG_MAX = new BigInteger(String.valueOf(Long.MAX_VALUE));
	private static final BigInteger LONG_MIN = new BigInteger(String.valueOf(Long.MIN_VALUE));
	private static final BigInteger LARGE = LONG_MAX.multiply(new BigInteger("2"));
	
	private static final BigInteger[] VALUES = {
		null,
		LONG_MIN,
		ZERO,
		ONE,
		LONG_MAX,
		LARGE,
	};
	
	public static class Item {
		public int _id;
		public BigInteger _typed;
		public Object _untyped;
		
		public Item(int id, BigInteger value) {
			_id = id;
			_typed = value;
			_untyped = value;
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
//		if (SubjectFixtureProvider.<Boolean>value()) {
			// FIXME: indices are simply being ignored
			indexField(config, Item.class, "_typed");
//		}
		config.registerTypeHandler(new SingleClassTypeHandlerPredicate(BigInteger.class), new BigIntegerTypeHandler());
	}

	@Override
	protected void store() throws Exception {
		int idx = 0;
		for (BigInteger bi : VALUES) {
			store(new Item(idx, bi));
			idx++;
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
				BigInteger expectedBase = VALUES[item._id];
				Assert.areEqual(typedUpdateValueFor(expectedBase), item._typed);
				Assert.areEqual(untypedUpdateValueFor(expectedBase), item._untyped);
			}
		});
	}

	private BigInteger untypedUpdateValueFor(Object object) {
		return null == object ? ONE : ((BigInteger)object).add(ONE);
    }

	private BigInteger typedUpdateValueFor(BigInteger value) {
	    return null == value ? ONE : value.multiply(value);
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
		for (BigInteger value : VALUES) {
			assertTypedQuery(value);
		}
	}

	public void testSingleDescendTypedFieldRange() {
	    Query query = newItemQuery();
	    query.descend("_typed").constrain(VALUES[1]).greater();
	    ObjectSet<Item> result = query.execute();
	    Assert.areEqual(VALUES.length - 2, result.size());
	}

	// FIXME
	public void _testSingleDescendTypedFieldRangeSub() {
	    Query query = newItemQuery();
	    Query sub = query.descend("_typed");
		sub.constrain(VALUES[0]).greater();
	    ObjectSet<Item> result = sub.execute();
	    System.out.println(result.size());
	    Object[] expected = new Object[VALUES.length - 2];
	    System.arraycopy(VALUES, 1, expected, 0, expected.length);
	    ObjectSetAssert.sameContent(result, expected);
	}

	private void assertTypedQuery(BigInteger value) {
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
				BigInteger expected = VALUES[item._id];
				Assert.areEqual(expected, item._typed);
				Assert.areEqual(expected, item._untyped);
			}
		});
	}

}
