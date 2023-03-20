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
package com.db4o.db4ounit.jre11.assorted;

import java.util.*;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeToHashtableTestCase extends AbstractDb4oTestCase{
	
	public static class Item{
		public Hashtable ht;
	}
	
	public static class Element{
		
		public Element _child;
		
		public String _name;
		
		public Element(String name) {
			_name = name;
		}

		public Element(Element child, String name) {
			_child = child;
			_name = name;
		}
		
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Item.class).cascadeOnUpdate(true);
		config.objectClass(Item.class).cascadeOnDelete(true);
	}
	
	@Override
	protected void store() throws Exception {
		Item item = new Item();
		item.ht = new Hashtable();
		item.ht.put("key1", new Element("stored1"));
		item.ht.put("key2", new Element(new Element("storedChild1"), "stored2"));
		store(item);
	}

	public void test() throws Exception {
		Item item = retrieveOnlyInstance(Item.class);
		item.ht.put("key1", new Element("updated1"));
		Element element = (Element)item.ht.get("key2"); 
		element._name = "updated2";
		store(item);
		
		reopen();
		
		item = retrieveOnlyInstance(Item.class);
		
		element = (Element)item.ht.get("key1");
		Assert.areEqual("updated1", element._name);
		element = (Element)item.ht.get("key2");
		Assert.areEqual("updated2", element._name);
		
		reopen();
		item = retrieveOnlyInstance(Item.class);
		db().delete(item);
		
		Assert.areEqual(2, db().query(Element.class).size());
	}

}
