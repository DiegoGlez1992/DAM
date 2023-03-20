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
package com.db4o.db4ounit.common.constraints;

import java.util.*;

import com.db4o.config.*;
import com.db4o.constraints.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class UniqueFieldValueDoesNotThrowTestCase
	extends AbstractDb4oTestCase
	implements CustomClientServerConfiguration {
	
	public static class Item {
		public Long id;
		public String name;
		
		public Item() {
		}
		
		public Item(int id, String name) {
			this.id = new Long(id);
			this.name = name;
		}
		
		@Override
		public int hashCode() {
			return id.hashCode();
		}
	}
	
	public static class Holder {
		public HashMap<Item, Long> _items = new HashMap<Item, Long>();
		
		public void add(Item item) {
			_items.put(item, item.id);
		}
	}
	
	public void configureClient(Configuration config) {
    }

	public void configureServer(Configuration config) throws Exception {
		configure(config);
    }	
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).objectField("id").indexed(true);
		
		config.add(new UniqueFieldValueConstraint(Item.class, "id"));
		config.objectClass(Holder.class).callConstructor(true);
	}
	
	public void test() throws Exception {
		store(newHolder("foo", "bar"));
		db().commit();
	}


	private Object newHolder(String... names) {
		final Holder holder = new Holder();
		for(int i = 0; i < names.length; i++) {
			holder.add(new Item(i, names[i]));
		}
		
		return holder;
	}
}
