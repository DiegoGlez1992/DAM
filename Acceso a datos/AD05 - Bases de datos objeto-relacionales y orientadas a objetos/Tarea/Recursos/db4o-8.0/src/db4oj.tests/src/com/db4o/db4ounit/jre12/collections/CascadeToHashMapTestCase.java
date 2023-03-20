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

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Ignore(decaf.Platform.JDK11)
public class CascadeToHashMapTestCase extends AbstractDb4oTestCase {
	
	public static class Atom {
		public String _name;
		public Atom _child;
		
		public Atom(Atom child, String name) {
			_child = child;
			_name = name;
		}
		
		public Atom(String name) {
			this(null, name);
		}
	}
	
	public static class Item {
		public HashMap<String,Atom> _map;
		
		public Item(HashMap<String,Atom> map) {
			_map = map;
		}
	}

	@Override
	protected void configure(Configuration config) throws Exception {
        config.generateUUIDs(ConfigScope.GLOBALLY);
        config.generateCommitTimestamps(true);
        config.objectClass(Item.class).cascadeOnUpdate(true);
        config.objectClass(Item.class).cascadeOnDelete(true);
	}

	protected void store() {
		HashMap<String,Atom> map = new HashMap();
		map.put("key1", new Atom("stored1"));
		map.put("key2", new Atom(new Atom("storedChild1"), "stored2"));
		Item item = new Item(map);
		store(item);
	}

	public void test() throws Exception {
		Item item = retrieveOnlyInstance(Item.class);
		item._map.put("key1", new Atom("updated1"));
		Atom atom = item._map.get("key2"); 
		atom._name = "updated2";
		store(item);
		
		reopen();
		
		item = retrieveOnlyInstance(Item.class);
		atom = item._map.get("key1");
		Assert.areEqual("updated1", atom._name);
		atom = item._map.get("key2");
		Assert.areEqual("updated2", atom._name);
		
		reopen();

		db().delete(retrieveOnlyInstance(Item.class));
		assertOccurrences(Atom.class, 2);
	}

}
