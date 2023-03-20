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

public class NoDuplicatesQueueTestCase implements TestLifeCycle {

	private Queue4 _queue;

	public void test() {
		_queue.add("A");
		_queue.add("B");
		_queue.add("B");
		_queue.add("A");
		Assert.areEqual("A", _queue.next());
		Assert.areEqual("B", _queue.next());
		Assert.isFalse(_queue.hasNext());
	}
	
	public void setUp() throws Exception {
		_queue = new NoDuplicatesQueue(new NonblockingQueue());
	}

	public void tearDown() throws Exception {
		_queue = null;
	}
}
