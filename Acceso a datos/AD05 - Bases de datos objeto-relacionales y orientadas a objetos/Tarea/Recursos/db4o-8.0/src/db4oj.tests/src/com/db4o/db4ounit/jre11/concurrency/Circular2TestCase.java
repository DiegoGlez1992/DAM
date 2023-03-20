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

public class Circular2TestCase extends Db4oClientServerTestCase {

	public Hashtable ht;

	public String name;

	protected void configure(Configuration config) {
		config.updateDepth(Integer.MAX_VALUE);
	}

	protected void store() {
		ht = new Hashtable();
		name = "parent";
		C2C c2c = new C2C();
		c2c.parent = this;
		ht.put("test", c2c);
		store(ht);
	}

	public void conc(ExtObjectContainer oc) {
		ht = (Hashtable) retrieveOnlyInstance(oc, Hashtable.class);
		C2C c2c = (C2C) ht.get("test");
		Assert.areEqual("parent", c2c.parent.name);
	}

	public static class C2C {
		public Circular2TestCase parent;
	}
}
