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
package com.db4o.db4ounit.common.caching;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.caching.*;

import db4ounit.*;

@decaf.Remove
public class PurgeableCacheTestCase implements TestCase {
	
	final PurgeableCache4<Integer, Integer> subject = CacheFactory.newLRUCache(2);
	final Function4 producer = createMock(Function4.class);
	final Procedure4 finalizer = createMock(Procedure4.class);
	
	public void test() {
		
		expect(producer.apply(1))
			.andReturn(10);
		expect(producer.apply(2))
			.andReturn(20);
		expect(producer.apply(3))
			.andReturn(30);
		expect(producer.apply(3))
			.andReturn(30);
		expect(producer.apply(4))
			.andReturn(40);
		
		finalizer.apply(10);
		expectLastCall().asStub();
		
		replay(producer, finalizer);
		
		produce(1);
		produce(2);
		produce(3);
		
		subject.purge(3);
		produce(3);
		
		subject.purge(2);
		produce(4);
		
		IteratorAssert.sameContent(Arrays.asList(30, 40), subject);
		
		verify(producer, finalizer);
	}
	
	public void testNullIsNotCached() {
		expect(producer.apply(1))
			.andReturn(10);
		expect(producer.apply(2))
			.andReturn(20);
		expect(producer.apply(3))
			.andReturn(null);
		
		replay(producer, finalizer);
		
		produce(1);
		produce(2);
		produce(3);
		
		IteratorAssert.sameContent(Arrays.asList(10, 20), subject);
		
		verify(producer, finalizer);
	}

	private void produce(final Integer key) {
	    subject.produce(key, producer, finalizer);
    }

}
