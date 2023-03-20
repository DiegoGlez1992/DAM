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
package com.db4o.db4ounit.jre12.collections.map;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class HashMapQueryTestCase extends AbstractDb4oTestCase {
	
	public static class Item{
		HashMap _map = new HashMap(); 
	}
	
	public static class ReferenceTypeElement {

		public int _id;
		
		public ReferenceTypeElement(int id) {
			_id = id;
		}
		
		public boolean equals(Object obj) {
			if(this == obj) {
				return true;
			}
			if(obj == null || getClass() != obj.getClass()) {
				return false;
			}
			ReferenceTypeElement other = (ReferenceTypeElement) obj;
			return _id == other._id;
		}
		
		public int hashCode() {
			return _id;
		}
		
		public String toString() {
			return "FCE#" + _id;
		}

	}
	
	protected void store() throws Exception {
		Item item = new Item();
		for (int i = 0; i < keys().length; i++) {
			item._map.put(keys()[i], values()[i]);
		}
		store(item);
	}
	
	private Object[] keys() {
		return new Object[]{
				new ReferenceTypeElement(0),
				new ReferenceTypeElement(1),
		};
	}
	
	private Object[] values() {
		return new Object[]{
				"zero",
				"one",
		};
	}

	public void testQueryResult() throws Exception {
		Query q = newQuery(Item.class);
		q.descend("_map").constrain(keys()[0]);
		assertQuery(q);		
	}

	private void assertQuery(Query q) {
		ObjectSet set = q.execute();
		Assert.areEqual(1, set.size());
		Item item = (Item)set.next();
		assertContent(item);
	}

	private void assertContent(Item item) {
		Assert.areEqual(keys().length, item._map.size());
		for (int i = 0; i < keys().length; i++) {
			Assert.areEqual(values()[i], item._map.get(keys()[i]));
		}
	}
	
	public static void main(String[] args) {
		new HashMapQueryTestCase().runAll();
	}

}
