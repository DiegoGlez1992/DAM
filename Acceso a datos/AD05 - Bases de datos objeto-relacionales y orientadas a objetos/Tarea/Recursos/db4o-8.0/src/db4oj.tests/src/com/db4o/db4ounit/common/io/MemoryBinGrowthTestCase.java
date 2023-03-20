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

public class MemoryBinGrowthTestCase implements TestCase {

	private final static class MockGrowthStrategy implements GrowthStrategy {
		private int[] _values;
		private int _idx;
		
		public MockGrowthStrategy(int[] values) {
			_values = values;
			_idx = 0;
		}
		
		public long newSize(long curSize, long requiredSize) {
			return _values[_idx++];
		}
		
		public void verify() {
			Assert.areEqual(_values.length, _idx);
		}	
	}

	private static final String URI = "growingbin";
	private static final int INITIAL_SIZE = 20;

	public void testGrowth() {
		final int[] values = {42, 47, 48};
		MockGrowthStrategy strategy = new MockGrowthStrategy(values);
		MemoryBin bin = newBin(INITIAL_SIZE, strategy);
		write(bin, 0, INITIAL_SIZE + 1, values[0]);
		write(bin, values[0], 1, values[1]);
		write(bin, values[1], 1, values[2]);
		strategy.verify();
	}

	public void testDoublingStrategy() {
		MemoryBin bin = newBin(0, new DoublingGrowthStrategy());
		write(bin, 0, 1, 1);
		write(bin, 0, 2, 2);
		write(bin, 0, 3, 4);

		bin = newBin(INITIAL_SIZE, new DoublingGrowthStrategy());
		write(bin, 0, INITIAL_SIZE + 1, 2 * INITIAL_SIZE);
	}

	public void testConstantStrategy() {
		final int growth = 100;
		MemoryBin bin = newBin(INITIAL_SIZE, new ConstantGrowthStrategy(growth));
		write(bin, 0, INITIAL_SIZE + 1, growth + INITIAL_SIZE);
		write(bin, 0, growth + INITIAL_SIZE + 1, INITIAL_SIZE + (2 * growth));
	}

	private MemoryBin newBin(final int initialSize, GrowthStrategy strategy) {
		MemoryStorage storage = new MemoryStorage(strategy);
		MemoryBin bin = (MemoryBin) storage.open(new BinConfiguration(URI, false, initialSize, false));
		return bin;
	}
	
	private void write(MemoryBin bin, int pos, int count, int expectedSize) {
		bin.write(pos, new byte[count], count);
		Assert.areEqual(expectedSize, bin.bufferSize());
	}
}
