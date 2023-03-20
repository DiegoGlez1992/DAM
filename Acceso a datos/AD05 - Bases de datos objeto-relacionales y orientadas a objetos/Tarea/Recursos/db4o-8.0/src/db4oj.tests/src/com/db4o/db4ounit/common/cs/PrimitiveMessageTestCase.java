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
import com.db4o.foundation.*;
import com.db4o.messaging.*;

import db4ounit.*;

public class PrimitiveMessageTestCase extends MessagingTestCaseBase {
	
	public static void main(String[] args) {
		new ConsoleTestRunner(PrimitiveMessageTestCase.class).run();
	}
	
	public void test() {
		
		final Collection4 expected = new Collection4(new Object[] { "PING", Boolean.TRUE, new Integer(42) });
		final MessageCollector recipient = new MessageCollector();
		final ObjectServer server = openServerWith(recipient);
		try {
			final ObjectContainer client = openClient("client", server);
			try {
				final MessageSender sender = messageSender(client);
				sendAll(expected, sender);
			} finally {
				client.close();
			}
		} finally {
			server.close();
		}
		
		Assert.areEqual(expected.toString(), recipient.messages.toString());
	}

	private void sendAll(final Iterable4 messages, final MessageSender sender) {
		final Iterator4 iterator = messages.iterator();
		while (iterator.moveNext()) {
			sender.send(iterator.current());
		}
	}

}
