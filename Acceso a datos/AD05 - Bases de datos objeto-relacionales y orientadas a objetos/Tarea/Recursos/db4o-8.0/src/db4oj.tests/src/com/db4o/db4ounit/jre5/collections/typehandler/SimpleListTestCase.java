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
package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
@decaf.Ignore
public class SimpleListTestCase extends AbstractDb4oTestCase{
	
	public static class Item {
		public List list;
	}
	
	public static class ReferenceTypeElement {
		
		public String name;
		
		public ReferenceTypeElement(String name_){
			name = name_;
		}
		
	}
	
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).cascadeOnDelete(true);
	}
	
	protected void store() throws Exception {
		Item item = new Item();
		item.list = new ArrayList();
		item.list.add("zero");
		item.list.add(new ReferenceTypeElement("one"));
		store(item);
	}
	
	public void testRetrieveInstance() {
		Item item = (Item) retrieveOnlyInstance(Item.class);
		Assert.areEqual(2, item.list.size());
		Assert.areEqual("zero", item.list.get(0));
	}
	
    public void testCascadingActivation(){
    	Item item = (Item) retrieveOnlyInstance(Item.class);
        List list = item.list;
        Assert.areEqual(2, list.size());
        Object element = list.get(1);
        if(db().isActive(element)){
            db().deactivate(item, Integer.MAX_VALUE);
            Assert.isFalse(db().isActive(element));
            db().activate(item, Integer.MAX_VALUE);
            Assert.isTrue(db().isActive(element));
        }
    }
	
	public void testQuery() {
		Query q = db().query();
		q.constrain(Item.class);
		q.descend("list").constrain("zero");
		ObjectSet objectSet = q.execute();
		Assert.areEqual(1, objectSet.size());
		Item item = (Item) objectSet.next();
		Assert.areEqual("zero", item.list.get(0));
	}
	
	public void testDeletion() {
		assertObjectCount(ReferenceTypeElement.class, 1);
		Item item = (Item) retrieveOnlyInstance(Item.class);
		db().delete(item);
		assertObjectCount(ReferenceTypeElement.class, 0);
	}

	private void assertObjectCount(Class clazz, int count) {
		Assert.areEqual(count, db().query(clazz).size());
	}

}
