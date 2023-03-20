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
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class DeleteDeepTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new DeleteDeepTestCase().runConcurrency();
	}

	public String name;

	public DeleteDeepTestCase child;

	protected void store() {
		addNodes(10);
		name = "root";
		store(this);
	}

	protected void configure(Configuration config) {
		config.objectClass(DeleteDeepTestCase.class).cascadeOnDelete(true);
		// config.objectClass(DeleteDeepTestCase.class).cascadeOnActivate(true);
	}

	private void addNodes(int count) {
		if (count > 0) {
			child = new DeleteDeepTestCase();
			child.name = "" + count;
			child.addNodes(count - 1);
		}
	}

	public void conc(ExtObjectContainer oc) throws Exception {
		Query q = oc.query();
		q.constrain(DeleteDeepTestCase.class);
		q.descend("name").constrain("root");
		ObjectSet os = q.execute();
		if (os.size() == 0) { // already deleted
			return;
		}
		Assert.areEqual(1, os.size());
		if(!os.hasNext()){
			return;
		}
		DeleteDeepTestCase root = (DeleteDeepTestCase) os.next();
		
		// wait for other threads
		// Thread.sleep(500);
		oc.delete(root);
		oc.commit();
		assertOccurrences(oc, DeleteDeepTestCase.class, 0);
	}

	public void check(ExtObjectContainer oc) {
		assertOccurrences(oc, DeleteDeepTestCase.class, 0);
	}

}
