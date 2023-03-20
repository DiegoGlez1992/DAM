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
package com.db4o.db4ounit.common.btree;

import com.db4o.config.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;

import db4ounit.extensions.*;


public class DebugBTreeNodeMarshalledLength extends AbstractDb4oTestCase{
	
	public static class Item{
		public int _int;
		public String _string;
	}

	public static void main(String[] args) {
		new DebugBTreeNodeMarshalledLength().runSolo();
	}
	
	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		config.objectClass(Item.class).objectField("_int").indexed(true);
		config.objectClass(Item.class).objectField("_string").indexed(true);
	}
	
	protected void store() throws Exception {
		for (int i = 0; i < 50000; i++) {
			store(new Item());
		}
	}
	
	public void test(){
		BTree btree = btree().debugLoadFully(systemTrans());
		store(new Item());
		btree.write(systemTrans());
	}
	
	private BTree btree(){
		ClassIndexStrategy index = classMetadataFor(Item.class).index();
		return ((BTreeClassIndexStrategy)index).btree();
	}

}
