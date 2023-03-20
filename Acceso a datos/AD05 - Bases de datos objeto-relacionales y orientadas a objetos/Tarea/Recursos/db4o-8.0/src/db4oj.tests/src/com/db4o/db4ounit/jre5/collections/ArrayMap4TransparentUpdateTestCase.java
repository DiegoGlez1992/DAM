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
package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;


/**
 */
@decaf.Ignore
public class ArrayMap4TransparentUpdateTestCase implements TestLifeCycle {

	private ArrayMap4<Object, Object> _map;
	private MockActivator _activator;

	public void testPut() {
		assertWriteCount(0);
		_map.put("foo", "bar");
		assertWriteCount(1);
		_map.put("foo", "bar");
		assertWriteCount(2);
	}

	public void testRemove() {
		_map.put("foo", "bar");
		_map.remove("foo");
		assertWriteCount(2);
		_map.remove("baz");
		assertWriteCount(2);
	}
	
	public void testClear() {
		_map.put("foo", "bar");
		_map.clear();
		assertWriteCount(2);
	}

	public void testPutAll() {
		_map.put("foo", "bar");
		_map.putAll(createMap());
		assertWriteCount(2);
	}

	public void _testKeySetIteratorRemove() {
		_map.put("foo", "bar");
		Iterator<Object> keyIter = _map.keySet().iterator();
		keyIter.remove();
		assertWriteCount(2);
	}
	
	public void setUp() throws Exception {
		_map = new ArrayMap4<Object, Object>();
		_activator = MockActivator.activatorFor(_map);
	}

	public void tearDown() throws Exception {
	}

	private void assertWriteCount(int expected) {
		Assert.areEqual(expected, _activator.writeCount());
	}

	private Map<Object, Object> createMap() {
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		map.put("", "");
		map.put("x", "y");
		return map;
	}
}
