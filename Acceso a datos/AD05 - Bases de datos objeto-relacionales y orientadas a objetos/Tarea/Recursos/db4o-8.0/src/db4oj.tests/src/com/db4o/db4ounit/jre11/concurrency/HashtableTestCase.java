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

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class HashtableTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		for (int i=0; i<50; ++i) {
			new HashtableTestCase().runEmbeddedConcurrency();
		}
	}
	
	private static long _id;
	
//	private static int run;
	
	protected void store() {
		Hashtable ht = new Hashtable();
		ht.put("key1", "val1");
		ht.put("key2", "val2");
		store(ht);
		_id = db().getID(ht);
	}
	
	public void conc(ExtObjectContainer oc) {
		Hashtable ht = (Hashtable) oc.getByID(_id);
		oc.activate(ht, Integer.MAX_VALUE);
		ht.put("key1", "updated1");
		String str = (String) ht.get("key2");
		Assert.areEqual("val2", str);
		oc.store(ht);
	}
	
}
