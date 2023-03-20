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
package com.db4o.db4ounit.common.handlers.framework;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.same;

import org.easymock.*;

import com.db4o.config.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

@decaf.Remove
public class TypeHandlerHierarchySemanticsTestCase extends AbstractInMemoryDb4oTestCase implements OptOutDefragSolo {
	
	public static class A {
	}
	
	public static class B extends A {
	}

	final InstantiatingTypeHandler _handlerA = createMock("handlerA", InstantiatingTypeHandler.class);
	
	final ReferenceTypeHandler _handlerB = createMock("handlerB", ReferenceTypeHandler.class);
	
	@Override
	protected void configure(Configuration config) throws Exception {
	    config.registerTypeHandler(
	    		new SingleClassTypeHandlerPredicate(A.class),
	    		_handlerA);
	    config.registerTypeHandler(
	    		new SingleClassTypeHandlerPredicate(B.class),
	    		_handlerB);
	}

	public void testStoreActivate() throws Exception {
		
		// store script
		final B b = new B();
		_handlerB.write(isA(WriteContext.class), same(b));
		expectLastCall();
		_handlerA.write(isA(WriteContext.class), same(b));
		expectLastCall();
		
		// activate script
		_handlerB.activate(isA(ReferenceActivationContext.class));
		expectLastCall();
		_handlerA.activate(isA(ReferenceActivationContext.class));
		expectLastCall();
		
		replay();
		
		store(b);
		reopen();
		Assert.isNotNull(retrieveOnlyInstance(B.class));
		
		verify();
	}
	
	private void verify() {
		EasyMock.verify(_handlerA, _handlerB);
	}
	
	private void replay() {
		EasyMock.replay(_handlerA, _handlerB);
	}
	
}
