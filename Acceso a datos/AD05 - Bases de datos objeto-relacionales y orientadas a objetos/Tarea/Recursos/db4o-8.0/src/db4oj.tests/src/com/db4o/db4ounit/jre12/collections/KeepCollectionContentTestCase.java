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

public class KeepCollectionContentTestCase extends AbstractDb4oTestCase{
	
	public static class Item implements Comparable {
		
		public String name;
		
		public Item(String name_){
			name = name_;
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof Item)){
				return false;
			}
			Item other = (Item) obj;
			if(name == null){
				return other.name == null;
			}
			return name.equals(other.name);
		}
		
		public int hashCode() {
			if(name == null){
				return 0;
			}
			return name.hashCode();
		}

		public int compareTo(Object obj) {
			if(! (obj instanceof Item)){
				throw new IllegalArgumentException();
			}
			return name.compareTo(((Item) obj).name);
		}
		
		public String toString() {
			return "Item " + name;
		}
	}
	
	protected void store() throws Exception {
		HashMap hm = new HashMap();
		hm.put(new Item("HashMap key"), new Item("HashMap value"));
		store(hm);
		Hashtable ht = new Hashtable();
		ht.put(new Item("Hashtable key"), new Item("Hashtable value"));
		store(ht);
		ArrayList al = new ArrayList();
		al.add(new Item("ArrayList"));
		store(al);
		Vector vec = new Vector();
		vec.add(new Item("Vector"));
		store(vec);
		TreeMap tm = new TreeMap();
		tm.put(new Item("TreeMap key"), new Item("TreeMap value"));
		store(tm);
	}
	
	public void test(){
		deleteAllInstances(new HashMap());
		deleteAllInstances(new Hashtable());
		deleteAllInstances(new ArrayList());
		deleteAllInstances(new Vector());
		deleteAllInstances(new TreeMap());
		db().commit();
		assertCount(Item.class, 8);
	}

	private void deleteAllInstances(Object obj) {
		Class clazz = obj.getClass();
		ObjectSet objectSet = allInstances(clazz);
		while(objectSet.hasNext()){
			db().delete(objectSet.next());
		}
	}

	private ObjectSet allInstances(Class clazz) {
		Query q = db().query();
		q.constrain(clazz);
		ObjectSet objectSet = q.execute();
		return objectSet;
	}
	
	private void assertCount(Class clazz, int count) {
		Assert.areEqual(count, allInstances(clazz).size()); 
	}

}
