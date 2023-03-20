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
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class ObjectPoolTestCase implements TestCase {
	
	public void test() {
		
		final Object o1 = new Object();
		final Object o2 = new Object();
		final Object o3 = new Object();
		
		final ObjectPool<Object> pool = new SimpleObjectPool<Object>(o1, o2, o3);
		Assert.areSame(o1, pool.borrowObject());
		Assert.areSame(o2, pool.borrowObject());
		Assert.areSame(o3, pool.borrowObject());
		
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				pool.borrowObject();
            }
		});
		
		pool.returnObject(o2);
		Assert.areSame(o2, pool.borrowObject());
	}
}
