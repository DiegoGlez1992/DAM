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

import db4ounit.extensions.*;

@decaf.Remove
public class HashSetTestCase extends AbstractDb4oTestCase {
	
	public static class Item {

		private LinkedHashSet<SubItem> _items;

		public Item(SubItem...items) {
			_items = new LinkedHashSet<SubItem>(Arrays.asList(items));
        }
		
		public int size() {
			return _items.size();
		}
		
	}
	
	public static class SubItem {

		private String _name;

		public SubItem(String name) {
			_name = name;
        }
		
		@Override
		public int hashCode() {
			return _name.hashCode();
		}
	}
	
	@Override
	protected void store() throws Exception {
	    store(new Item(new SubItem("foo"), new SubItem("bar")));
	}
	
	public void testPeekPersisted() {
		
//		HardObjectReference.peekPersisted(trans(), db().getID(retrieveOnlyInstance(Item.class)));
	}

}
