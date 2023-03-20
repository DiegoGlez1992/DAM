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
import com.db4o.ext.*;
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ServerPortUsedTestCase extends Db4oClientServerTestCase implements OptOutAllButNetworkingCS{

	private static final String DATABASE_FILE = "PortUsed.db";

	public static void main(String[] args) {
		new ServerPortUsedTestCase().runAll();
	}

	protected void db4oTearDownBeforeClean() throws Exception {
		File4.delete(DATABASE_FILE);

	}

	public void test() {
		final int port = clientServerFixture().serverPort();
		Assert.expect(Db4oIOException.class, new CodeBlock() {
			public void run() throws Throwable {
				Db4oClientServer.openServer(DATABASE_FILE, port);
			}
		});

	}
}