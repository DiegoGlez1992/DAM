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

import db4ounit.extensions.*;

public class CascadeOnDeleteHierarchyTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new CascadeOnDeleteHierarchyTestCase().runAll();
	}

	public static class Item {

	}

	public static class SubItem extends Item {
		
		public Data data;

		public SubItem() {
			data = new Data();
		}
	}

	public static class Data {
	}

	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).cascadeOnDelete(true);
		config.objectClass(SubItem.class);
		super.configure(config);
	}

	protected void store() throws Exception {
		store(new SubItem());
	}

	public void test() throws Exception {
		SubItem item = (SubItem) retrieveOnlyInstance(SubItem.class);
		db().delete(item);
		assertOccurrences(Data.class, 0);
		db().commit();
		assertOccurrences(Data.class, 0);
	}
	
	public void testMultipleStoreCalls(){
		SubItem item = retrieveOnlyInstance(SubItem.class);
		store(item);
		assertOccurrences(Data.class, 1);
	}
	
	
}
