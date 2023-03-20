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

import com.db4o.foundation.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.fixtures.*;
import db4ounit.mocking.*;

public class BlockAwareBinTestSuite extends FixtureTestSuiteDescription {
	
	{
		fixtureProviders(new SubjectFixtureProvider(2, 3, 17));
		testUnits(BlockAwareBinTest.class);
	}
	
	public static class BlockAwareBinTest implements TestCase, Environment {
		
		private final MockBin _mockBin = new MockBin();
		private final BlockSize _mockBlockSize = new BlockSize() {
			public void register(Listener4<Integer> listener) {
				throw new NotImplementedException();
            }

			public void set(int newValue) {
				Assert.areEqual(blockSize(), newValue);
            }

			public int value() {
	            return blockSize();
            }
		};
		
		private BlockAwareBin _subject;
		
		public BlockAwareBinTest() {
			Environments.runWith(this, new Runnable() { public void run() {
				_subject = new BlockAwareBin(_mockBin);
			}});
		}
		
		public <T> T provide(Class<T> service) {
			if (service != BlockSize.class) {
				throw new IllegalArgumentException();
			}
			return service.cast(_mockBlockSize);
        }
		
		public void testBlockSize() {
			Assert.areEqual(blockSize(), _subject.blockSize());
		}
		
		public void testClose() {
			_subject.close();
			verify(new MethodCall("close"));
		}
		
		public void testSync() {
			_subject.sync();
			verify(new MethodCall("sync"));
		}
		
		public void testBlockReadReturnsStorageReturnValue() {

			_mockBin.returnValueForNextCall(-1);
			Assert.areEqual(-1, _subject.blockRead(0, new byte[10]));
		}
		
		public void testBlockRead() {
			byte[] buffer = new byte[10];
			_subject.blockRead(0, buffer);
			_subject.blockRead(1, buffer, 5);
			_subject.blockRead(42, buffer);
			
			verify(
				new MethodCall("read", 0L, buffer, buffer.length),
				new MethodCall("read", (long)blockSize(), buffer, 5),
				new MethodCall("read", 42L*blockSize(), buffer, buffer.length)
			);
		}
		
		public void testBlockReadWithOffset() {
			byte[] buffer = new byte[10];
			_subject.blockRead(0, 1, buffer);
			_subject.blockRead(1, 3, buffer, 5);
			_subject.blockRead(42, 5, buffer);
			
			verify(
				new MethodCall("read", 1L, buffer, buffer.length),
				new MethodCall("read", 3 + (long)blockSize(), buffer, 5),
				new MethodCall("read", 5 + 42L*blockSize(), buffer, buffer.length)
			);
		}
		
		public void testBlockWrite() {
			byte[] buffer = new byte[10];
			_subject.blockWrite(0, buffer);
			_subject.blockWrite(1, buffer, 5);
			_subject.blockWrite(42, buffer);
			
			verify(
				new MethodCall("write", 0L, buffer, buffer.length),
				new MethodCall("write", (long)blockSize(), buffer, 5),
				new MethodCall("write", 42L*blockSize(), buffer, buffer.length)
			);
		}
		
		public void testBlockWriteWithOffset() {
			byte[] buffer = new byte[10];
			_subject.blockWrite(0, 1, buffer);
			_subject.blockWrite(1, 3, buffer, 5);
			_subject.blockWrite(42, 5, buffer);
			
			verify(
				new MethodCall("write", 1L, buffer, buffer.length),
				new MethodCall("write", 3 + (long)blockSize(), buffer, 5),
				new MethodCall("write", 5 + 42L*blockSize(), buffer, buffer.length)
			);
		}
		
		private void verify(MethodCall... expectedCalls) {
			_mockBin.verify(expectedCalls);
        }

		private int blockSize() {
			return SubjectFixtureProvider.<Integer>value().intValue();
		}
	}

}
