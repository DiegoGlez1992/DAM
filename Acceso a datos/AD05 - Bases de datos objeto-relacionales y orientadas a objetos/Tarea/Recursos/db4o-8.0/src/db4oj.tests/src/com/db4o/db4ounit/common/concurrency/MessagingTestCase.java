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

import com.db4o.db4ounit.common.cs.*;
import com.db4o.ext.*;
import com.db4o.messaging.*;

import db4ounit.*;

public class MessagingTestCase extends ClientServerTestCaseBase {
    
    public static final Object lock = new Object();

	public static void main(String[] args) {
		new MessagingTestCase().runConcurrency();
	}

	public TestMessageRecipient _recipient;
	
	public MessagingTestCase() {
		_recipient = new TestMessageRecipient(threadCount());
	}

	public void conc(ExtObjectContainer oc, int seq) {
	    MessageSender sender = null;
	    
	    // Configuration is not threadsafe.
	    synchronized(lock){
    		server().ext().configure().clientServer()
    				.setMessageRecipient(_recipient);
    		sender = oc.configure().clientServer().getMessageSender();
	    }
		
		sender.send(new Data(seq));
	}

	public void check(ExtObjectContainer oc) throws Exception {
		Thread.sleep(1000);
		_recipient.check();
	}

	public static class TestMessageRecipient implements MessageRecipient {
		public int seq;

		public boolean[] processed;

		public TestMessageRecipient(int threadCount) {
			processed = new boolean[threadCount];
		}

		public void processMessage(MessageContext con, Object message) {
			Assert.isTrue(message instanceof Data);
			int value = ((Data) message).value;
			processed[value] = true;
		}

		public void check() {
			for (int i = 0; i < processed.length; ++i) {
				Assert.isTrue(processed[i]);
			}
		}
	}

	public static class Data {
		public int value;

		public Data(int seq) {
			this.value = seq;
		}
	}
}