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
package com.db4o.db4ounit.common.io;

import com.db4o.io.*;

import db4ounit.*;

public class MemoryIoAdapterTestCase implements TestCase {

	private static final String URL = "url";
	private static final int GROW_BY = 100;

	public void testGrowth() {
		MemoryIoAdapter factory = new MemoryIoAdapter();
		factory.growBy(GROW_BY);
		IoAdapter io = factory.open(URL, false, 0, false);
		assertLength(factory, 0);
		writeBytes(io, GROW_BY - 1);
		assertLength(factory, GROW_BY);
		writeBytes(io, GROW_BY - 1);
		assertLength(factory, GROW_BY * 2);
		writeBytes(io, GROW_BY * 2);
		assertLength(factory, GROW_BY * 4 - 2);
	}

	private void writeBytes(IoAdapter io, int numBytes) {
		io.write(new byte[numBytes]);
	}

	private void assertLength(MemoryIoAdapter factory, int expected) {
		Assert.areEqual(expected, factory.get(URL).length);
	}
	
}
