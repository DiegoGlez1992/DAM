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
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class GreaterOrEqualTestCase extends Db4oClientServerTestCase {

	public static void main(String[] args) {
		new GreaterOrEqualTestCase().runConcurrency();
	}

	public int val;

	public GreaterOrEqualTestCase() {

	}

	public GreaterOrEqualTestCase(int val) {
		this.val = val;
	}

	protected void store() {
		store(new GreaterOrEqualTestCase(1));
		store(new GreaterOrEqualTestCase(2));
		store(new GreaterOrEqualTestCase(3));
		store(new GreaterOrEqualTestCase(4));
		store(new GreaterOrEqualTestCase(5));
	}

	public void conc(ExtObjectContainer oc) {
		int[] expect = { 3, 4, 5 };
		Query q = oc.query();
		q.constrain(GreaterOrEqualTestCase.class);
		q.descend("val").constrain(new Integer(3)).greater().equal();
		ObjectSet res = q.execute();
		while (res.hasNext()) {
			GreaterOrEqualTestCase r = (GreaterOrEqualTestCase) res.next();
			for (int i = 0; i < expect.length; i++) {
				if (expect[i] == r.val) {
					expect[i] = 0;
				}
			}
		}
		for (int i = 0; i < expect.length; i++) {
			Assert.areEqual(0, expect[i]);
		}
	}

}
