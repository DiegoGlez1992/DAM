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
package com.db4o.db4ounit.common.refactor;

import com.db4o.config.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;

// COR-1721
public class RefactorFieldToTransientTestCase extends AbstractDb4oTestCase{

	public static class Before {
		public int _id;
		
		public Before(int id) {
			_id = id;
		}
	}

	public static class After {
		public transient int _id;
		
		public After(int id) {
			_id = id;
		}
	}

	@Override
	protected void store() throws Exception {
		store(new Before(42));
	}

	public void testRetrieval() throws Exception {
		fixture().resetConfig();
		Configuration config = fixture().config();
		Reflector reflector = new ExcludingReflector(Before.class);
		config.reflectWith(reflector);
		TypeAlias alias = new TypeAlias(Before.class, After.class);
		config.addAlias(alias);
		reopen();
		
		After after = retrieveOnlyInstance(After.class);
		Assert.areEqual(0, after._id);
		
		config = fixture().config();
		config.reflectWith(new ExcludingReflector());
		config.removeAlias(alias);
		reopen();
		
		Before before = retrieveOnlyInstance(Before.class);
		Assert.areEqual(42, before._id);
	}
}
