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
import com.db4o.cs.internal.ClientObjectContainer.MessageListener;
import com.db4o.cs.internal.messages.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class IsAliveConcurrencyTestCase extends Db4oClientServerTestCase implements OptOutAllButNetworkingCS {

	private volatile boolean processingMessage = false;
	
	public void testIsAliveInMultiThread() throws InterruptedException {
			
		final BlockingQueue4<Object> barrier =new BlockingQueue<Object>();
		
		client = (ClientObjectContainer) openNewSession();
		
		client.messageListener(new MessageListener() {			
			public void onMessage(Msg msg) {
				
				if (msg instanceof MQueryExecute) {
					processingMessage = true;
					barrier.add(new Object());
					Runtime4.sleep(500);
					processingMessage = false;					
				}
				else if (msg instanceof MIsAlive) {
					Assert.isFalse(processingMessage);					
				}
			}
		});
		
		Thread workThread = new Thread(new Runnable() {
			public void run() {
				client.queryByExample(Item.class);
			}}, "Quering");
		
		workThread.setDaemon(true);
		workThread.start();
		
		barrier.next();
		client.isAlive();		
	}

	protected void store() {
		for (int i = 0; i < 10; ++i) {
			store(new Item());
		}
	}

	public static class Item {
	}
	
	private static ClientObjectContainer client;
}
