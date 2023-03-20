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

public class TreeSetInstantiationInstrumentationTestCase implements TestCase {

	public void testConstructorIsExchanged() throws Exception {
		Class instrumented = instrument(TreeSetFactory.class);
		Object instance = instrumented.newInstance();
		assertReturnsActivatableSet(instance, "createTreeSet");
		assertReturnsActivatableSet(instance, "createTreeSetWithComparator");
		assertReturnsActivatableSet(instance, "createTreeSetFromCollection");
		assertReturnsActivatableSet(instance, "createTreeSetFromSortedSet");
	}
	
	public void testBaseTypeIsExchanged() throws Exception {
		Class instrumented = instrument(MyTreeSet.class);
		Set set = (Set)instrumented.newInstance();
		assertActivatableSet(set);
		Set delegateSet = (Set)instrumented.getField("_delegate").get(set);
		assertActivatableSet(delegateSet);
	}

	public void testBaseInvocationIsExchanged() throws Exception {
		Class instrumented = instrument(MyTreeSet.class);
		Set set = (Set)instrumented.newInstance();
		set.add("foo");
		Assert.isTrue(set.contains("foo"));
		
		Set delegateSet = (Set)instrumented.getField("_delegate").get(set);
		Assert.isTrue(delegateSet.contains("foo"));
	}
	
	private void assertActivatableSet(Set delegateSet) {
	    Assert.isInstanceOf(ActivatableTreeSet.class, delegateSet);
    }
	
	private void assertReturnsActivatableSet(Object instance, String methodName) {
		Set set = (Set)Reflection4.invoke(instance, methodName);
		assertActivatableSet(set);
	}

	private Class instrument(Class clazz) throws ClassNotFoundException {
		return InstrumentationEnvironment.enhance(clazz, new ReplaceClassOnInstantiationEdit(TreeSet.class, ActivatableTreeSet.class));
	}
}
