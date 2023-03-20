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
package com.db4o.db4ounit.common.querying;

import com.db4o.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ConjunctiveQbETestCase extends AbstractDb4oTestCase {

	public static class Sup {
		public boolean _flag;

		public Sup(boolean flag) {
			this._flag = flag;
		}
		
		public ObjectSet query(ObjectContainer db) {
			Query query=db.query();
			query.constrain(this);
			query.descend("_flag").constrain(Boolean.TRUE).not();
			return query.execute();
		}
	}

	public static class Sub1 extends Sup {
		public Sub1(boolean flag) {
			super(flag);
		}
	}

	public static class Sub2 extends Sup {
		public Sub2(boolean flag) {
			super(flag);
		}
	}
	
	protected void store() throws Exception {
		store(new Sub1(false));
		store(new Sub1(true));
		store(new Sub2(false));
		store(new Sub2(true));
	}
	
	public void testAndedQbE() {
		Assert.areEqual(1,new Sub1(false).query(db()).size());
	}
}
