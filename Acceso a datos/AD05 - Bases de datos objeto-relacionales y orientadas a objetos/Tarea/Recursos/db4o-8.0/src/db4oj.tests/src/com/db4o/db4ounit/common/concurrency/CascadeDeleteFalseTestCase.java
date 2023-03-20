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
package com.db4o.db4ounit.common.concurrency;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.extensions.*;

public class CascadeDeleteFalseTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new CascadeDeleteFalseTestCase().runConcurrency();
	}

	public static class Item {
		public CascadeDeleteFalseHelper h1;

		public CascadeDeleteFalseHelper h2;

		public CascadeDeleteFalseHelper h3;
	}

	protected void configure(Configuration config) {
		config.objectClass(Item.class).cascadeOnDelete(true);
		config.objectClass(Item.class).objectField("h3").cascadeOnDelete(false);
	}

	protected void store() {
		Item item = new Item();
		item.h1 = new CascadeDeleteFalseHelper();
		item.h2 = new CascadeDeleteFalseHelper();
		item.h3 = new CascadeDeleteFalseHelper();
		store(item);
	}

	public void concDelete(ExtObjectContainer oc) throws Exception {
		ObjectSet os = oc.query(Item.class);
		if (os.size() == 0) { // the object has been deleted
			return;
		}
		if(! os.hasNext()){
			// object can be deleted after query 
			return;
		}
		Item cdf = (Item) os.next();
		// sleep 1000 ms, waiting for other threads.
		// Thread.sleep(500);
		oc.delete(cdf);
		oc.commit();
		assertOccurrences(oc, Item.class, 0);
		assertOccurrences(oc, CascadeDeleteFalseHelper.class, 1);
	}

	public void checkDelete(ExtObjectContainer oc) {
		assertOccurrences(oc, CascadeDeleteFalseTestCase.class, 0);
		assertOccurrences(oc, CascadeDeleteFalseHelper.class, 1);
	}

	public static class CascadeDeleteFalseHelper {

	}
}
