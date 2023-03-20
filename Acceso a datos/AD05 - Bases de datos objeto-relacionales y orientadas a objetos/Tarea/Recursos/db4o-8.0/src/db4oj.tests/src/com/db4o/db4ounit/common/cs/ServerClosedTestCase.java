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

import com.db4o.cs.internal.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ServerClosedTestCase extends Db4oClientServerTestCase implements OptOutAllButNetworkingCS{
    
	public static void main(String[] args) {
		new ServerClosedTestCase().runAll();
	}

	public void test() throws Exception {
		final ExtObjectContainer db = fixture().db();
		
		ObjectServerImpl serverImpl = (ObjectServerImpl) clientServerFixture()
				.server();
		try {
			Iterator4 iter = serverImpl.iterateDispatchers();
			iter.moveNext();
			ServerMessageDispatcherImpl serverDispatcher = (ServerMessageDispatcherImpl) iter
					.current();
			serverDispatcher.socket().close();
			Runtime4.sleep(1000);
			Assert.expect(DatabaseClosedException.class, new CodeBlock() {
				public void run() throws Throwable {
					db.queryByExample(null);
				}
			});
			Assert.isTrue(db.isClosed());
		} finally {
			serverImpl.close();
		}
	}

}
