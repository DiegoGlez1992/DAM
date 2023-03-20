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

public class HashSet4TestCase implements TestLifeCycle {

	private Set4 _set;
	
	public void testEmpty() {
		assertEmpty();
	}

	public void testSingleAdd() {
		Object obj = new Object();
		_set.add(obj);
		Assert.isFalse(_set.isEmpty());
		Assert.areEqual(1, _set.size());
		Assert.isTrue(_set.contains(obj));
		Assert.isFalse(_set.contains(new Object()));
		Iterator4 iter = _set.iterator();
		Assert.isTrue(iter.moveNext());
		Assert.areEqual(obj, iter.current());
	}

	public void testSingleRemove() {
		Object obj = new Object();
		_set.add(obj);
		Assert.isTrue(_set.remove(obj));
		assertEmpty();
	}

	public void testMultipleAddRemove() {
		Object[] objs = {
				new Object(),
				new Object(),
				new Object()
		};
		for (Object obj : objs) {
			_set.add(obj);
		}
		Assert.isFalse(_set.isEmpty());
		Assert.areEqual(objs.length, _set.size());
		for (Object obj : objs) {
			Assert.isTrue(_set.contains(obj));
		}
		Assert.isFalse(_set.contains(new Object()));
		Iterator4Assert.sameContent(objs, _set.iterator());
	}

	public void testClear() {
		Object[] objs = {
				new Object(),
				new Object(),
				new Object()
		};
		for (Object obj : objs) {
			_set.add(obj);
		}
		_set.clear();
		assertEmpty();
	}

	private void assertEmpty() {
		Assert.isTrue(_set.isEmpty());
		Assert.areEqual(0, _set.size());
		Assert.isFalse(_set.contains(new Object()));
		Assert.isFalse(_set.remove(new Object()));
		Assert.isFalse(_set.iterator().moveNext());
	}
	
	public void setUp() throws Exception {
		_set = new HashSet4();
	}

	public void tearDown() throws Exception {
	}
	
}
