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
package com.db4o.db4ounit.common.assorted;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class RollbackUpdateTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] args) {
		new RollbackUpdateTestCase().runNetworking();
	}

	protected void store() {
		store(new SimpleObject("hello", 1));
	}

	public void test() {
		ExtObjectContainer oc1 = openNewSession();
		ExtObjectContainer oc2 = openNewSession();
		ExtObjectContainer oc3 = openNewSession();
		try {
			SimpleObject o1 = (SimpleObject) retrieveOnlyInstance(oc1,
					SimpleObject.class);
			o1.setS("o1");
			oc1.store(o1);
			SimpleObject o2 = (SimpleObject) retrieveOnlyInstance(oc2,
					SimpleObject.class);
			Assert.areEqual("hello", o2.getS());

			oc1.rollback();
			o2 = (SimpleObject) retrieveOnlyInstance(oc2, SimpleObject.class);
			oc2.refresh(o2, Integer.MAX_VALUE);
			Assert.areEqual("hello", o2.getS());

			oc1.commit();
			o2 = (SimpleObject) retrieveOnlyInstance(oc2, SimpleObject.class);
			Assert.areEqual("hello", o2.getS());

			oc1.store(o1);
			oc1.commit();
			oc2.refresh(o2, Integer.MAX_VALUE);
			o2 = (SimpleObject) retrieveOnlyInstance(oc2, SimpleObject.class);
			Assert.areEqual("o1", o2.getS());

			SimpleObject o3 = (SimpleObject) retrieveOnlyInstance(oc3,
					SimpleObject.class);
			Assert.areEqual("o1", o3.getS());
		} finally {
			oc1.close();
			oc2.close();
			oc3.close();
		}
	}

}
