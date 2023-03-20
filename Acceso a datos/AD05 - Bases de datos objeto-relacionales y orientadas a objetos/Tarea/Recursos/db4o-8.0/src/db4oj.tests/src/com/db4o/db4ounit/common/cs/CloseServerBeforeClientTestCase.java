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
import com.db4o.cs.*;
import com.db4o.cs.internal.*;
import com.db4o.db4ounit.common.api.*;
import com.db4o.ext.*;

import db4ounit.*;

public class CloseServerBeforeClientTestCase extends TestWithTempFile {

	public static void main(String[] arguments) {
		for (int i = 0; i < 1000; i++)
			new ConsoleTestRunner(CloseServerBeforeClientTestCase.class).run();
	}

	public void test() throws Exception {
		ObjectServer server = Db4oClientServer.openServer(tempFile(), Db4oClientServer.ARBITRARY_PORT);
		server.grantAccess("", "");
		
		ObjectContainer client = Db4oClientServer.openClient("localhost", ((ObjectServerImpl)server).port(), "", "");
		ObjectContainer client2 = Db4oClientServer.openClient("localhost", ((ObjectServerImpl)server).port(), "", "");
		
		client.commit();
		client2.commit();
		
		try {
			server.close();
		} finally {
			try{
				client.close();
				client2.close();
			} catch(Db4oException e) {
				// database may have been closed
			}
		}
	}
}
