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

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ClientDisconnectTestCase extends Db4oClientServerTestCase implements
    OptOutAllButNetworkingCS {

    public static void main(String[] arguments) {
        new ClientDisconnectTestCase().runNetworking();
    }

    public void testDisconnect() {
        ExtObjectContainer oc1 = openNewSession();
        ExtObjectContainer oc2 = openNewSession();
        try {
            final ClientObjectContainer client1 = (ClientObjectContainer) oc1;
            final ClientObjectContainer client2 = (ClientObjectContainer) oc2;
            client1.socket().close();
            Assert.isFalse(oc1.isClosed());
            Assert.expect(Db4oException.class, new CodeBlock() {
                public void run() throws Throwable {
                    client1.queryByExample(null);
                }
            });
            // It's ok for client2 to get something.
            client2.queryByExample(null);
        } finally {
            oc1.close();
            oc2.close();
            Assert.isTrue(oc1.isClosed());
            Assert.isTrue(oc2.isClosed());
        }
    }


}
