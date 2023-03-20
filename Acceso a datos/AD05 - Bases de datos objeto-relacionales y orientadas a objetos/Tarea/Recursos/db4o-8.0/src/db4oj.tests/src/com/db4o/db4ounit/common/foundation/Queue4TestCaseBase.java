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

public class Queue4TestCaseBase implements TestCase {

	public Queue4TestCaseBase() {
		super();
	}

	protected void assertIterator(Queue4 queue, String[] data, int size) {
		Iterator4 iter = queue.iterator();
		for (int idx = 0; idx < size; idx++) {
			Assert.isTrue(iter.moveNext(),
					"should be able to move in iteration #" + idx + " of "
							+ size);
			Assert.areEqual(data[idx], iter.current());
		}
		Assert.isFalse(iter.moveNext());
	}

}