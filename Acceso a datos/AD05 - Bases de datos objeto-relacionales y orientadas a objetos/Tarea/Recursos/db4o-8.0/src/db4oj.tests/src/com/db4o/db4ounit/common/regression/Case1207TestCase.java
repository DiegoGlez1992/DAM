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
package com.db4o.db4ounit.common.regression;

import com.db4o.*;
import com.db4o.db4ounit.common.assorted.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class Case1207TestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) throws Exception {
		new Case1207TestCase().runNetworking();
	}

	/*
	 * client 1: set and commit client 2: set and rollback
	 */
	public void test() throws Exception {
		ObjectContainer oc1 = openNewSession();
		ObjectContainer oc2 = openNewSession();
		ObjectContainer oc3 = openNewSession();
		try {
			for (int i = 0; i < 1000; i++) {
				SimpleObject obj1 = new SimpleObject("oc " + i, i);
				SimpleObject obj2 = new SimpleObject("oc2 " + i, i);
				oc1.store(obj1);
				oc2.store(obj2);
				oc2.rollback();
				obj2 = new SimpleObject("oc2.2 " + i, i);
				oc2.store(obj2);
			}
			oc1.commit();
			oc2.rollback();
			Assert.areEqual(1000, oc1.query(SimpleObject.class).size());
			Assert.areEqual(1000, oc2.query(SimpleObject.class).size());
			Assert.areEqual(1000, oc3.query(SimpleObject.class).size());
		} finally {
			oc1.close();
			oc2.close();
			oc3.close();
		}
	}
}
