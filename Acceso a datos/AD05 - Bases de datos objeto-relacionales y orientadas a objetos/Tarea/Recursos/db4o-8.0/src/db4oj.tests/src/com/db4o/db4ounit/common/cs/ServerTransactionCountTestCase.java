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


import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.cs.internal.*;
import com.db4o.io.*;

import db4ounit.*;

public class ServerTransactionCountTestCase implements TestCase {
	
	private static final int TIMEOUT = 100;

	public void test() throws Exception{
		ServerConfiguration config = Db4oClientServer.newServerConfiguration();
		config.timeoutServerSocket(TIMEOUT);
		config.file().storage(new MemoryStorage());
		ObjectServerImpl server = (ObjectServerImpl) Db4oClientServer.openServer(config, "", Db4oClientServer.ARBITRARY_PORT);
		Thread.sleep(TIMEOUT * 2);
		Assert.areEqual(0, server.transactionCount());
		server.close();
	}

}
