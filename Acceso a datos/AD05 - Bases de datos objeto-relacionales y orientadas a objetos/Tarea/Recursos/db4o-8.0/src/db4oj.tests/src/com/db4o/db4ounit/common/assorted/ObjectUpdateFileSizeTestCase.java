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
package com.db4o.db4ounit.common.assorted;

import java.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ObjectUpdateFileSizeTestCase extends AbstractDb4oTestCase implements OptOutMultiSession, OptOutDefragSolo{

	public static void main(String[] args) {
		new ObjectUpdateFileSizeTestCase().runAll();
	}
	
	public static class Item{
		public String _name;
		
		public Item(String name){
			_name = name;
		}
	}

	protected void store() throws Exception {
		Item item = new Item("foo");
		store(item);
	}

	public void testFileSize() throws Exception {
		warmUp();
		assertFileSizeConstant();	
	}

	private void assertFileSizeConstant() throws Exception {
		
		long beforeUpdate = dbSize();
		
		for (int j = 0; j < 10; j++) {
			
			defragment();
			
			for (int i = 0; i < 15; ++i) {
				updateItem();
			}
			defragment();
			long afterUpdate = dbSize();
			
			/*
			 * FIXME: the database file size is uncertain? 
			 * We met similar problem before.
			 */
			Assert.isSmaller(30, afterUpdate - beforeUpdate);
		}		
		
	}

	private void warmUp() throws Exception, IOException {
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 3; ++i) {
				updateItem();
				db().commit();
				defragment();
			}
		}
	}

	private void updateItem() throws Exception, IOException {
		Item item = retrieveOnlyInstance(Item.class);
		store(item);
		db().commit();
	}
	
	private long dbSize() {
		return db().systemInfo().totalSize();
	}

}
