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

public class ArrayListInstantiationInstrumentationTestCase implements TestCase {

	public void testConstructorIsExchanged() throws Exception {
		Class instrumented = instrument(ArrayListFactory.class);
		Object instance = instrumented.newInstance();
		assertReturnsActivatableList(instance, "createArrayList");
		assertReturnsActivatableList(instance, "createSizedArrayList");
		assertReturnsActivatableList(instance, "createNestedArrayList");
		assertReturnsActivatableList(instance, "createMethodArgArrayList");
		assertReturnsActivatableList(instance, "createConditionalArrayList");
	}

	public void testBaseTypeIsExchanged() throws Exception {
		Class instrumented = instrument(MyArrayList.class);
		List list = (List)instrumented.newInstance();
		assertActivatableList(list);
		List delegateList = (List)instrumented.getField("_delegate").get(list);
		assertActivatableList(delegateList);
	}
	
	public void testBaseInvocationIsExchanged() throws Exception {
		Class instrumented = instrument(MyArrayList.class);
		List list = (List)instrumented.newInstance();
		list.add("foo");
		Assert.isTrue(list.contains("foo"));
		
		List delegateList = (List)instrumented.getField("_delegate").get(list);
		Assert.isTrue(delegateList.contains("foo"));
	}
	
	private void assertActivatableList(List delegateList) {
	    Assert.isInstanceOf(ActivatableArrayList.class, delegateList);
    }
	
	private void assertReturnsActivatableList(Object instance, String methodName) {
		List list = invokeForListCreation(instance, methodName);
		assertActivatableList(list);
	}

	private List invokeForListCreation(Object instance, String methodName) {
		return (List)Reflection4.invoke(instance, methodName);
	}

	private Class instrument(Class clazz) throws ClassNotFoundException {
		return InstrumentationEnvironment.enhance(clazz, new ReplaceClassOnInstantiationEdit(ArrayList.class, ActivatableArrayList.class));
	}
}
