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
package com.db4o.db4ounit.jre12.collections.transparent.list;

import java.util.*;

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableStackTestCase extends AbstractDb4oTestCase {

	public static void main(String[] args) {
		new ActivatableStackTestCase().runAll();
	}
	
	public static class StackHolder<E> {
		public Stack<E> _stack;
		
		public StackHolder(Stack<E> stack) {
			_stack = stack;
		}		
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
	}
	
	@Override
	protected void store() throws Exception {
		StackHolder<CollectionElement> holder = new StackHolder(filledStack());
		store(holder);
	}

	public void testPush() throws Exception {
		final String newElementName = "John Doe";
		singleStack().push(new ActivatableElement(newElementName));
		reopen();
		Stack<CollectionElement> expectedStack = filledStack();
		expectedStack.push(new ActivatableElement(newElementName));
		assertStacksAreEqual(expectedStack, singleStack());
	}
	
	public void testPop() {
		assertCollectionElement(filledStack().pop(), singleStack().pop());
	}

	public void testPeek() {
		assertCollectionElement(filledStack().peek(), singleStack().peek());
	}
	
	public void testEmpty() throws Exception {
		final Stack<CollectionElement> stack = singleStack();
		Assert.isFalse(stack.empty());
		
		stack.clear();
		reopen();
		Assert.isTrue(singleStack().empty());
	}
	
	public void testSearch() {
		final Stack<CollectionElement> stack = filledStack();
		Assert.areEqual(1, singleStack().search(stack.peek()));
		Assert.areEqual(stack.size(), singleStack().search(stack.get(0)));		
	}
	
	private void assertCollectionElement(final CollectionElement expected, final CollectionElement actual) {
		Assert.isNotNull(actual);
		Assert.areEqual(isNotTAAware(actual), db().ext().isActive(actual));
		db().ext().activate(actual);
		Assert.areEqual(expected, actual);
	}
	
	private boolean isNotTAAware(final CollectionElement item) {
		return item instanceof Element;
	}
	
	private Stack<CollectionElement> singleStack() {
		final StackHolder holder = retrieveOnlyInstance(StackHolder.class);	
		return holder._stack;
	}

	private void assertStacksAreEqual(
			final Stack<CollectionElement> expectedStack,
			final Stack<CollectionElement> actualStack) {
		while (!expectedStack.isEmpty()) {
			final CollectionElement expectedElement = expectedStack.pop();
			Assert.areEqual(expectedElement, actualStack.pop());
		}
		
		Assert.isTrue(actualStack.isEmpty());
	}
	

	private Stack<CollectionElement> filledStack() {
		final Stack<CollectionElement> stack = new ActivatableStack<CollectionElement>();
		
		for(int i = 0; i < 5; i++) {
			stack.push(new Element("Element#" + i));
		}
		
		for(int i = 0; i < 5; i++) {
			stack.push(new ActivatableElement("ActivatableElement#" + i));
		}
		return stack;
	}
}
