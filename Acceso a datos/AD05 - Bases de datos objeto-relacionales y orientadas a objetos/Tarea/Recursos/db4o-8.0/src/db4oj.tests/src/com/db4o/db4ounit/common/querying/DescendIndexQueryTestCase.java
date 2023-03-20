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


public class DescendIndexQueryTestCase extends AbstractDb4oTestCase{
	
	public static class Parent {
		
		public Child _child;
		
		public String _name;
		
	}
	
	public static class Child {
		
		public Child _child;
		
		public int _id;
		
	}
	
	
	protected void configure(Configuration config) throws Exception {
		ObjectClass parentObjectClass = config.objectClass(Parent.class);
		parentObjectClass.objectField("_child").indexed(true);
		parentObjectClass.objectField("_name").indexed(true);
		ObjectClass childObjectClass = config.objectClass(Child.class);
		childObjectClass.objectField("_child").indexed(true);
		childObjectClass.objectField("_id").indexed(true);
	}
	
	@Override
	protected void store() throws Exception {
		storeParent("one", 0);
		storeParent("two", 0);
		storeParent("two", 10);
		storeParent("three", 0);
		storeParent("three", 10);
		storeParent("three", 100);
	}

	private void storeParent(String name, int addToId) {
		Parent parent = new Parent();
		parent._name = name;
		Child previousChild = null;
		for (int i = 4; i >= 0; i--) {
			Child currentChild = new Child();
			currentChild._id = i + addToId;
			currentChild._child = previousChild;
			previousChild = currentChild;
		}
		parent._child = previousChild;
		store(parent);
	}
	
	public void testDescendParentName(){
		Query q = newQuery(Parent.class);
		q.descend("_name").constrain("two");
		q.descend("_child").descend("_id").constrain(0);
		assertResultSize(q, 1);
	}
	
	public void testDescendParentNameSubQuery(){
		Query q = newQuery(Parent.class);
		q.descend("_name").constrain("two");
		Query qChild = q.descend("_child");
		qChild.descend("_id").constrain(0);
		assertResultSize(qChild, 1);
	}
	
	public void testDescendChildId(){
		Query q = newQuery(Parent.class);
		q.descend("_child").descend("_id").constrain(0);
		assertResultSize(q, 3);
	}
	
	public void testDescendChildIdSubQuery(){
		Query q = newQuery(Parent.class);
		Query qChild = q.descend("_child");
		qChild.descend("_id").constrain(0);
		assertResultSize(qChild, 3);
	}
	
	public void testImplicitAndChildId(){
		Query q = newQuery(Parent.class);
		Query qChild = q.descend("_child");
		Query qId = qChild.descend("_id");
		qId.constrain(0);
		qId.constrain(10);
		qId.constrain(100);
		assertResultSize(q, 0);
	}
	
	public void testOrChildId(){
		Query q = newQuery(Parent.class);
		Query qChild = q.descend("_child");
		Query qId = qChild.descend("_id");
		Constraint c1 = qId.constrain(0);
		Constraint c2 = qId.constrain(10);
		Constraint c3 = qId.constrain(100);
		c1.or(c2).or(c3);
		assertResultSize(q, 6);
	}
	
	
	
	private void assertResultSize(Query q, int count) {
		Assert.areEqual(count, q.execute().size());
	}

}
