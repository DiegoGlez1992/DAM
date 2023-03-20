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

@SuppressWarnings("unchecked")
public class StackInstantiationInstrumentationTestCase implements TestCase {
	public static void main(String[] args) {
		new ConsoleTestRunner(StackInstantiationInstrumentationTestCase.class).run();
	}
	
	public void testConstructorIsExchanged() throws Exception {
		Class instrumented = instrument(StackFactory.class);
		Object instance = instrumented.newInstance();
		assertReturnsActivatableStack(instance, "createStack");
	}

	public void testBaseTypeIsExchanged() throws Exception {
		Class instrumented = instrument(MyStack.class);
		Stack stack = (Stack)instrumented.newInstance();
		assertActivatableStack(stack);
		Stack delegateStack = (Stack)instrumented.getField("_delegate").get(stack);
		assertActivatableStack(delegateStack);
	}
	
	public void testBaseInvocationIsExchanged() throws Exception {
		Class instrumented = instrument(MyStack.class);
		Stack stack = (Stack)instrumented.newInstance();
		stack.push("foo");
		Assert.areEqual("foo", stack.peek());
		
		Stack delegateStack = (Stack)instrumented.getField("_delegate").get(stack);
		Assert.areEqual("foo", delegateStack.peek());
	}
	
	private void assertActivatableStack(Stack delegateStack) {
	    Assert.isInstanceOf(ActivatableStack.class, delegateStack);
    }
	
	private void assertReturnsActivatableStack(Object instance, String methodName) {
		Stack stack = invokeForStackCreation(instance, methodName);
		assertActivatableStack(stack);
	}

	private Stack invokeForStackCreation(Object instance, String methodName) {
		return (Stack) Reflection4.invoke(instance, methodName);
	}

	private Class instrument(Class clazz) throws ClassNotFoundException {
		return InstrumentationEnvironment.enhance(clazz, new ReplaceClassOnInstantiationEdit(Stack.class, ActivatableStack.class));
	}

}
