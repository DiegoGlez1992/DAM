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
package com.db4o.db4ounit.common.staging;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.messaging.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class PingTestCase extends Db4oClientServerTestCase implements OptOutAllButNetworkingCS {

	public static void main(String[] args) {
		new PingTestCase().runAll();
	}

	protected void configure(Configuration config) {
		config.clientServer().timeoutClientSocket(1000);
	}

	TestMessageRecipient recipient = new TestMessageRecipient();

	public void test() {
		clientServerFixture().server().ext().configure().clientServer()
				.setMessageRecipient(recipient);

		final ExtObjectContainer client = clientServerFixture().db();
		final MessageSender sender = client.configure().clientServer()
				.getMessageSender();
		
		if(isEmbedded()){
		    Assert.expect(NotSupportedException.class, new CodeBlock(){
                public void run() throws Throwable {
                    sender.send(new Data());
                }
		    });
		    return;
		}
		
	    sender.send(new Data());
		

		// The following query will be block by the sender
		ObjectSet os = client.queryByExample(null);
		while (os.hasNext()) {
			os.next();
		}
		Assert.isFalse(client.isClosed());
	}

	public static class TestMessageRecipient implements MessageRecipient {
		public void processMessage(MessageContext con, Object message) {
			Runtime4.sleep(3000);
		}
	}

	public static class Data {
	}
}