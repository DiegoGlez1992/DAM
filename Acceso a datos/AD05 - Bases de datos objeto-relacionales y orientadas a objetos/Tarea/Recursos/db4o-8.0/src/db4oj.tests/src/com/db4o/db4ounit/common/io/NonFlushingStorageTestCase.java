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

import java.io.*;

import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.io.*;

import db4ounit.*;
import db4ounit.mocking.*;

public class NonFlushingStorageTestCase implements TestCase {
	
	public void test() {
		final MockBin mock = new MockBin();
		
		BinConfiguration binConfig = new BinConfiguration("uri", true, 42L, false);
		
		final Bin storage = new NonFlushingStorage(new Storage() {
			public boolean exists(String uri) {
				throw new NotImplementedException();
            }

			public Bin open(BinConfiguration config)
                    throws Db4oIOException {
				mock.record(new MethodCall("open", config));
				return mock;
            }

			public void delete(String uri) throws IOException {
				throw new NotImplementedException();
			}

			public void rename(String oldUri, String newUri) throws IOException {
				throw new NotImplementedException();
			}
			
		}).open(binConfig);
		
		final byte[] buffer = new byte[5];
		storage.read(1, buffer, 4);
		storage.write(2, buffer, 3);
		mock.returnValueForNextCall(42);
		Assert.areEqual(42, mock.length());
		storage.sync();
		storage.close();
		
		mock.verify(
			new MethodCall("open", binConfig),
			new MethodCall("read", 1L, buffer, 4),
			new MethodCall("write", 2L, buffer, 3),
			new MethodCall("length"),
			new MethodCall("close")
		);
	}

}
