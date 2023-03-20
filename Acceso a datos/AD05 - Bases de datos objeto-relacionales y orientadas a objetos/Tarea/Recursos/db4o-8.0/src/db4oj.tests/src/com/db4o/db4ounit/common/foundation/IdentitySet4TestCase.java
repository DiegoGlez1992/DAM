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
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class IdentitySet4TestCase implements TestCase{
	
	public static class Item {
		
		int _id;
		
		public Item(int id){
			_id = id;
		}
		
		@Override
		public int hashCode() {
			return _id;
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof Item)){
				return false;
			}
			Item other = (Item) obj;
			return _id == other._id;
		}
		
	}
	
	public void testByIdentity(){
		IdentitySet4 table = new IdentitySet4(2);
		Item item1 = new Item(1);
		Assert.isFalse(table.contains(item1));
		table.add(item1);
		Assert.isTrue(table.contains(item1));
		Item item2 = new Item(2);
		Assert.isFalse(table.contains(item2));
		table.add(item2);
		Assert.isTrue(table.contains(item2));
		Assert.areEqual(2, table.size());
		int size = 0;
		Iterator4 i = table.iterator();
		while(i.moveNext()){
			size++;
		}
		Assert.areEqual(2, size);
	}
	
	public void testRemove() {
		final IdentitySet4 set = new IdentitySet4();
		final Object obj = new Object();
		set.add(obj);
		Assert.isTrue(set.contains(obj));
		set.remove(obj);
		Assert.isFalse(set.contains(obj));
	}
	
	public void testIterator() {
		final IdentitySet4 set = new IdentitySet4();
		final Object o1 = new Object();
		final Object o2 = new Object();
		set.add(o1);
		set.add(o2);		
		
		Iterator4Assert.sameContent(Iterators.iterate(o1, o2), set.iterator());
	}

}
