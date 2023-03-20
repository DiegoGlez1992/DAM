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

import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class IndexedJoinQueriesTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		
		public int _id;
		
		public String _name;
		
	}
	
	@Override
	protected void store() throws Exception {
		for (int i = 0; i < 10; i++) {
			Item item = new Item();
			item._id = i;
			item._name = i < 5 ? "A" :  "B";
			store(item);
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		ObjectClass objectClass = config.objectClass(Item.class);
		objectClass.objectField("_id").indexed(true);
		objectClass.objectField("_name").indexed(true);
	}
	
	public void testSimpleAndExpectOne(){
		Query q = newItemQuery();
		Constraint c1 = q.descend("_id").constrain(3);
		Constraint c2 = q.descend("_name").constrain("A");
		c1.and(c2);
		assertResultSize(q, 1);
	}
	
	public void testSimpleAndExpectNone(){
		Query q = newItemQuery();
		Constraint c1 = q.descend("_id").constrain(3);
		Constraint c2 = q.descend("_name").constrain("B");
		c1.and(c2);
		assertResultSize(q, 0);
	}
	
	
	public void testSimpleOrExpectTwo(){
		Query q = newItemQuery();
		Constraint c1 = q.descend("_id").constrain(3);
		Constraint c2 = q.descend("_id").constrain(4);
		c1.or(c2);
		assertResultSize(q, 2);
	}
	
	public void testSimpleOrExpectOne(){
		Query q = newItemQuery();
		Constraint c1 = q.descend("_id").constrain(3);
		Constraint c2 = q.descend("_id").constrain(11);
		c1.or(c2);
		assertResultSize(q, 1);
	}

	
	public void testSimpleOrExpectNone(){
		Query q = newItemQuery();
		Constraint c1 = q.descend("_id").constrain(11);
		Constraint c2 = q.descend("_id").constrain(13);
		c1.or(c2);
		assertResultSize(q, 0);
	}
	
	public void testThreeOrsExpectTen(){
		Query q = newItemQuery();
		Constraint c1 = q.descend("_name").constrain("A");
		Constraint c2 = q.descend("_name").constrain("B");
		Constraint c3 = q.descend("_name").constrain("C");
		c1.or(c2).or(c3);
		assertResultSize(q, 10);
	}
	
	public void testAndOr(){
		Query q = newItemQuery();
		Constraint c1 = q.descend("_id").constrain(1);
		Constraint c2 = q.descend("_id").constrain(2);
		Constraint c3 = q.descend("_name").constrain("A");
		c1.or(c2).and(c3);
		assertResultSize(q, 2);
	}
	
	public void testOrAnd(){
		Query q = newItemQuery();
		Constraint c1 = q.descend("_id").constrain(1);
		Constraint c2 = q.descend("_name").constrain("A");
		Constraint c3 = q.descend("_name").constrain("B");
		c1.and(c2).or(c3);
		assertResultSize(q, 6);
	}


	private void assertResultSize(Query q, int count) {
		Assert.areEqual(count, q.execute().size());
	}

	private Query newItemQuery() {
		return newQuery(Item.class);
	}

}
