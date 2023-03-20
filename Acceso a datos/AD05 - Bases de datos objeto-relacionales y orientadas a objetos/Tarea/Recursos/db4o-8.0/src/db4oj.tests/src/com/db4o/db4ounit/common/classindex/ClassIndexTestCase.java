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
package com.db4o.db4ounit.common.classindex;

import com.db4o.internal.classindex.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ClassIndexTestCase extends AbstractDb4oTestCase implements OptOutMultiSession{
	
	public static class Item {
		public String name;

		public Item(String _name) {
			this.name = _name;
		}
	}
	
	public static void main(String[] args) {
		new ClassIndexTestCase().runSolo();
	}
	
	public void testDelete() throws Exception {
		Item item = new Item("test");
		store(item);
		int id=(int)db().getID(item);
		assertID(id);

		reopen();
		
		item=(Item)db().queryByExample(item).next();
		id=(int)db().getID(item);
		assertID(id);
		
		db().delete(item);
		db().commit();
		assertEmpty();
		
		reopen();

		assertEmpty();
	}

	private void assertID(int id) {
		assertIndex(new Object[]{new Integer(id)});
	}

	private void assertEmpty() {
		assertIndex(new Object[]{});
	}

	private void assertIndex(Object[] expected) {
		ExpectingVisitor visitor = new ExpectingVisitor(expected);
		ClassIndexStrategy index = classMetadataFor(Item.class).index();
		index.traverseAll(trans(),visitor);
		visitor.assertExpectations();
	}
}
