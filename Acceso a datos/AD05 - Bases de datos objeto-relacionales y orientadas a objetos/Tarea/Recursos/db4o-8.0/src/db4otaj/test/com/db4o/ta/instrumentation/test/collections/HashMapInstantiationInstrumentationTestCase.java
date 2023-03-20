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
package com.db4o.ta.instrumentation.test.collections;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.internal.*;
import com.db4o.ta.instrumentation.*;
import com.db4o.ta.instrumentation.test.*;

import db4ounit.*;

public class HashMapInstantiationInstrumentationTestCase implements TestCase {

	public void testConstructorIsExchanged() throws Exception {
		Class instrumented = instrument(HashMapFactory.class);
		Object instance = instrumented.newInstance();
		assertReturnsActivatableMap(instance, "createHashMap");
		assertReturnsActivatableMap(instance, "createHashMapWithSize");
		assertReturnsActivatableMap(instance, "createHashMapWithSizeAndLoad");
		assertReturnsActivatableMap(instance, "createHashMapFromMap");
	}
	
	public void testBaseTypeIsExchanged() throws Exception {
		Class instrumented = instrument(MyHashMap.class);
		Map map = (Map)instrumented.newInstance();
		assertActivatableMap(map);
		Map delegateMap = (Map)instrumented.getField("_delegate").get(map);
		assertActivatableMap(delegateMap);
	}

	public void testBaseInvocationIsExchanged() throws Exception {
		Class instrumented = instrument(MyHashMap.class);
		Map map = (Map)instrumented.newInstance();
		map.put("foo", "bar");
		Assert.isTrue(map.containsKey("foo"));
		
		Map delegateMap = (Map)instrumented.getField("_delegate").get(map);
		Assert.isTrue(delegateMap.containsKey("foo"));
		Assert.areEqual("bar", delegateMap.get("foo"));
	}
	
	private void assertActivatableMap(Map delegateMap) {
	    Assert.isInstanceOf(ActivatableHashMap.class, delegateMap);
    }
	
	private void assertReturnsActivatableMap(Object instance, String methodName) {
		Map map = (Map)Reflection4.invoke(instance, methodName);
		assertActivatableMap(map);
	}

	private Class instrument(Class clazz) throws ClassNotFoundException {
		return InstrumentationEnvironment.enhance(clazz, new ReplaceClassOnInstantiationEdit(HashMap.class, ActivatableHashMap.class));
	}
}
