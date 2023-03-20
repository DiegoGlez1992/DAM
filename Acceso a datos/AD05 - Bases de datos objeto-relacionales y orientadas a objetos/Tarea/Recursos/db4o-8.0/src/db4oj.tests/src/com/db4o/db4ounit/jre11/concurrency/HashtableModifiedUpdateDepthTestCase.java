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
package com.db4o.db4ounit.jre11.concurrency;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class HashtableModifiedUpdateDepthTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new HashtableModifiedUpdateDepthTestCase().runConcurrency();
	}
	
	public static class Item {
		public Hashtable ht;
	}

	protected void configure(Configuration config) {
		config.updateDepth(Integer.MAX_VALUE);
	}

	protected void store() {
		Item item = new Item();
		item.ht = new Hashtable();
		item.ht.put("hi", "five");
		store(item);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		Hashtable ht = (Hashtable) retrieveOnlyInstance(oc, Hashtable.class);
		ht.put("hi", "updated" + seq);
		oc.store(ht);
	}

	public void check(ExtObjectContainer oc) {
		Hashtable ht = (Hashtable) retrieveOnlyInstance(oc, Hashtable.class);
		String s = (String) ht.get("hi");
		Assert.isTrue(s.startsWith("updated"));
		Assert.isTrue(s.length() > "updated".length());
	}
}