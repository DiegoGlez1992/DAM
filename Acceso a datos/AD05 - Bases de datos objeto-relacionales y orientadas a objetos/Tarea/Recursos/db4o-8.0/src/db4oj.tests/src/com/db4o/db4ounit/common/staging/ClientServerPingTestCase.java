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

import com.db4o.config.*;
import com.db4o.cs.internal.*;
import com.db4o.cs.internal.messages.*;
import com.db4o.db4ounit.common.cs.*;
import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @exclude
 */
public class ClientServerPingTestCase extends ClientServerTestCaseBase {

	private static final int	ITEM_COUNT	= 100;

	public static void main(String[] arguments) {
		new ClientServerPingTestCase().runNetworking();
	}

	protected void configure(Configuration config) {
		config.clientServer().batchMessages(false);
	}

	public void test() throws Exception {
	    if(isEmbedded()){
	        // This test really doesn't make sense for MTOC, there
	        // is no client to ping.
	        return;
	    }
		ServerMessageDispatcher dispatcher = serverDispatcher();
		PingThread pingThread = new PingThread(dispatcher);
		pingThread.start();
		for (int i = 0; i < ITEM_COUNT; i++) {
			Item item = new Item(i);
			store(item);
		}
		Assert.areEqual(ITEM_COUNT, db().queryByExample(Item.class).size());
		pingThread.close();
	}

	public static class Item {

		public int	data;

		public Item(int i) {
			data = i;
		}

	}

	static class PingThread extends Thread {

		ServerMessageDispatcher	_dispatcher;
		boolean					_stop;
		
		private final Object   lock = new Object();

		public PingThread(ServerMessageDispatcher dispatcher) {
			_dispatcher = dispatcher;
		}

		public void close() {
		    synchronized(lock){
		        _stop = true;
		    }
		}
		
		private boolean notStopped(){
		    synchronized(lock){
		        return !_stop;
		    }
		}

		public void run() {
			while (notStopped()) {
				_dispatcher.write(Msg.PING);
				Runtime4.sleep(1);
			}
		}
	}

}
