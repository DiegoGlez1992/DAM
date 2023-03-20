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
package com.db4o.db4ounit.jre12.fieldindex;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
/**
 */
@decaf.Ignore(decaf.Platform.JDK11)
public class CollectionFieldIndexTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new CollectionFieldIndexTestCase().runSolo();
	}
	
	private static class Item {
		private String _name;
		
		public Item(String name) {
			_name = name;
		}
		
		public String getName() {
			return _name;
		}
	}
	
	private static class UntypedContainer {
		private Object _set = new HashSet();
		
		public UntypedContainer(Object item) {
			((Set)_set).add(item);
		}
		
		public Iterator iterator() {
			return ((Set)_set).iterator();
		}
	}
	
	protected void configure(Configuration config) {
		indexField(config,Item.class, "_name");
		indexField(config,UntypedContainer.class, "_set");
	}
	
	protected void store() throws Exception {
		db().store(new UntypedContainer(new Item("foo")));
		db().store(new UntypedContainer(new Item("bar")));
	}
	
	public void testUntypedContainer() {
		final Query q = db().query();
		q.constrain(UntypedContainer.class);
		q.descend("_set").descend("_name").constrain("foo");
		
		final ObjectSet result = q.execute();
		Assert.areEqual(1, result.size());
		
		final UntypedContainer container = (UntypedContainer)result.next();
		final Item item = (Item)container.iterator().next();
		Assert.areEqual("foo", item.getName());
	}

}
