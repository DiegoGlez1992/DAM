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
package com.db4o.cs.internal;

import com.db4o.cs.internal.messages.*;
import com.db4o.foundation.*;

public class CommittedCallbacksDispatcher implements Runnable {
	
	private boolean _stopped;
	
	private final BlockingQueue _committedInfosQueue;
	
	private final ObjectServerImpl _server;
	
	public CommittedCallbacksDispatcher(ObjectServerImpl server, BlockingQueue committedInfosQueue) {
		_server = server;
		_committedInfosQueue = committedInfosQueue;
	}
	
	public void run () {
		setThreadName();
		messageLoop();
	}

	private void messageLoop() {
	    while(! _stopped){
			MCommittedInfo committedInfos;
			try {
				committedInfos = (MCommittedInfo) _committedInfosQueue.next();
			} catch (BlockingQueueStoppedException e) {
				break;
			}
			_server.broadcastMsg(committedInfos, new BroadcastFilter() {
				public boolean accept(ServerMessageDispatcher dispatcher) {
					return dispatcher.caresAboutCommitted();
				}
			});
		}
    }

	private void setThreadName() {
	    Thread.currentThread().setName("committed callback thread");
    }
	
	public void stop(){
		_committedInfosQueue.stop();
		_stopped = true;
	}

}
