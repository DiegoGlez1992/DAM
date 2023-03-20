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


public class Stack4TestCase implements TestCase {

	public static void main(String[] args) {
		new ConsoleTestRunner(Stack4TestCase.class).run(); 
	}
	
	public void testPushPop(){
		final Stack4 stack = new Stack4();
		assertEmpty(stack);
		stack.push("a");
		stack.push("b");
		stack.push("c");
		Assert.isFalse(stack.isEmpty());
		Assert.areEqual("c", stack.peek());
		Assert.areEqual("c", stack.peek());
		Assert.areEqual("c", stack.pop());
		Assert.areEqual("b", stack.pop());
		Assert.areEqual("a", stack.peek());
		Assert.areEqual("a", stack.pop());
		assertEmpty(stack);
	}

	private void assertEmpty(final Stack4 stack) {
		Assert.isTrue(stack.isEmpty());
		Assert.isNull(stack.peek());
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				stack.pop();
			}
		});
	}

}
