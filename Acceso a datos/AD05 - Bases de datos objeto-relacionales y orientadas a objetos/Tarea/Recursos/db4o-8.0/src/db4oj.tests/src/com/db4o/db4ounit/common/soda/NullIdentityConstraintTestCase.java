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
package com.db4o.db4ounit.common.soda;

import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class NullIdentityConstraintTestCase extends AbstractDb4oTestCase {
	
	public static class Data {
		public Data _prev;

		public Data(Data prev) {
			this._prev = prev;
		}
	}
	
	protected void store() throws Exception {
		Data a=new Data(null);
		Data b=new Data(a);
		store(b);
	}
	
	public void testNullIdentity() {
		Query query=newQuery(Data.class);
		query.descend("_prev").constrain(null).identity();
		Assert.areEqual(1,query.execute().size());
	}
}
