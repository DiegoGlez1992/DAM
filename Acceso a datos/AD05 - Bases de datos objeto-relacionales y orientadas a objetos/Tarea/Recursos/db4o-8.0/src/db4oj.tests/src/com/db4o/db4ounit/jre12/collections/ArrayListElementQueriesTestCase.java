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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ArrayListElementQueriesTestCase extends AbstractDb4oTestCase{
	
	public static class ListHolder {
		
		public ArrayList _list;
		
	}
	
	public static class Item {
		
		public String _name;
		
		public boolean equals(Object obj) {
			if( ! (obj instanceof Item)){
				return false;
			}
			Item other = (Item) obj;
			if(_name == null){
				return other._name == null;
			}
			return _name.equals(other._name);
		}
		
	}
	
	protected void store() throws Exception {
		ListHolder holder = createStringHolder();
		store(holder);
		holder = createItemHolder();
		store(holder);
	}

	private ListHolder createItemHolder() {
		ListHolder holder;
		holder = new ListHolder();
		holder._list = new ArrayList();
		Item item = createItem();
		holder._list.add(item);
		return holder;
	}

	private Item createItem() {
		Item item = new Item();
		item._name = "item";
		return item;
	}

	private ListHolder createStringHolder() {
		ListHolder holder = new ListHolder();
		holder._list = new ArrayList();
		holder._list.add("string");
		return holder;
	}
	
	public void testStringQBE(){
		ObjectSet objectSet = db().queryByExample(createStringHolder());
		assertStringHolder(objectSet);
	}
	
	public void testStringSodaQBE() {
		Query q = db().query();
		q.constrain(createStringHolder()).byExample();
		assertStringHolder(q.execute());
	}

	public void testItemQBE(){
		ObjectSet objectSet = db().queryByExample(createItemHolder());
		assertItemHolder(objectSet);
	}
	
	public void testItemSodaQBE() {
		Query q = db().query();
		q.constrain(createItemHolder()).byExample();
		assertItemHolder(q.execute());
	}
	
	public void testStringSoda(){
		Query q = db().query();
		q.descend("_list").constrain("string");
		assertStringHolder(q.execute());
	}
	
	public void testItemSoda(){
		Query q = db().query();
		q.descend("_list").descend("_name").constrain("item");
		assertItemHolder(q.execute());
	}
	
	private void assertItemHolder(ObjectSet objectSet) {
		assertHolder(objectSet, createItem());
	}

	private void assertStringHolder(ObjectSet objectSet) {
		assertHolder(objectSet, "string");
	}
	
	private void assertHolder(ObjectSet objectSet, Object expectedElement){
		Assert.areEqual(1, objectSet.size());
		ListHolder holder = (ListHolder) objectSet.next();
		Object actualElement = holder._list.get(0);
		Assert.areEqual(expectedElement, actualElement);
	}

}
