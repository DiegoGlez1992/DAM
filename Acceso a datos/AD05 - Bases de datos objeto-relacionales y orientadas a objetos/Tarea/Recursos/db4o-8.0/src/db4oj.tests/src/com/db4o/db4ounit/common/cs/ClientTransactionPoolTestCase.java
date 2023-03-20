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
package com.db4o.db4ounit.common.cs;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.cs.internal.*;
import com.db4o.internal.*;
import com.db4o.io.*;

import db4ounit.*;

public class ClientTransactionPoolTestCase implements TestLifeCycle {

	public void testPool() {
		Configuration config = Db4o.newConfiguration();
		config.storage(new MemoryStorage());
		final LocalObjectContainer db = (LocalObjectContainer) Db4o.openFile(config, ClientTransactionTestUtil.MAINFILE_NAME);
		final ClientTransactionPool pool = new ClientTransactionPool(db);
		try {
			Assert.areEqual(0, pool.openTransactionCount());
			Assert.areEqual(1, pool.openFileCount());
			Transaction trans1 = pool.acquire(ClientTransactionTestUtil.MAINFILE_NAME);
			Assert.areEqual(db, trans1.container());			
			Assert.areEqual(1, pool.openTransactionCount());
			Assert.areEqual(1, pool.openFileCount());
			Transaction trans2 = pool.acquire(ClientTransactionTestUtil.FILENAME_A);
			Assert.areNotEqual(db, trans2.container());			
			Assert.areEqual(2, pool.openTransactionCount());
			Assert.areEqual(2, pool.openFileCount());
			Transaction trans3 = pool.acquire(ClientTransactionTestUtil.FILENAME_A);
			Assert.areEqual(trans2.container(), trans3.container());
			Assert.areEqual(3, pool.openTransactionCount());
			Assert.areEqual(2, pool.openFileCount());
			pool.release(ShutdownMode.NORMAL, trans3, true);
			Assert.areEqual(2, pool.openTransactionCount());
			Assert.areEqual(2, pool.openFileCount());
			pool.release(ShutdownMode.NORMAL, trans2, true);
			Assert.areEqual(1, pool.openTransactionCount());
			Assert.areEqual(1, pool.openFileCount());
			
			
		}
		finally {
			Assert.isFalse(db.isClosed());
			Assert.isFalse(pool.isClosed());
			pool.close();
			Assert.isTrue(db.isClosed());
			Assert.isTrue(pool.isClosed());
			Assert.areEqual(0, pool.openFileCount());
		}
	}

	public void setUp() throws Exception {
		ClientTransactionTestUtil.deleteFiles();
	}

	public void tearDown() throws Exception {
		ClientTransactionTestUtil.deleteFiles();
	}
}
