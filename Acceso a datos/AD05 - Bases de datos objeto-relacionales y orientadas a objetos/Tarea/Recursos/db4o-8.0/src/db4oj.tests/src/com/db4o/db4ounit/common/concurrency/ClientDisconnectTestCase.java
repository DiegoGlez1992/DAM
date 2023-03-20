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
package com.db4o.db4ounit.common.concurrency;

import com.db4o.cs.internal.*;
import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ClientDisconnectTestCase extends Db4oClientServerTestCase {
	
	public static void main(String[] arguments) {
        new ClientDisconnectTestCase().runConcurrency();
        new ClientDisconnectTestCase().runConcurrency();
	}
	
	public void _concDelete(ExtObjectContainer oc, int seq) throws Exception {
		final ClientObjectContainer client = (ClientObjectContainer) oc;
		try {
			if (seq % 2 == 0) {
				// ok to get something
				client.queryByExample(null);
			} else {
				client.socket().close();
				Assert.isFalse(oc.isClosed());
				Assert.expect(Db4oException.class, new CodeBlock() {
					public void run() throws Throwable {
						client.queryByExample(null);	
					}
				});
			}
		} finally {
			oc.close();
			Assert.isTrue(oc.isClosed());
		}
	}
}
