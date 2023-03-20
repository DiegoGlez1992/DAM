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
package com.db4o.cs.internal.messages;

import java.util.*;

import com.db4o.cs.internal.*;
import com.db4o.drs.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;


public class MCommitReplication extends MCommit implements MessageWithResponse {
	
	public Msg replyFromServer() {
		ServerMessageDispatcher dispatcher = serverMessageDispatcher();
		synchronized (containerLock()) {
			LocalTransaction trans = serverTransaction();
			
			long replicationRecordId = readLong();
			long timestamp = readLong();
			
			List concurrentTimestamps = trans.concurrentReplicationTimestamps();
			
			serverMessageDispatcher().server().broadcastReplicationCommit(timestamp, concurrentTimestamps);
			
			ReplicationRecord replicationRecord = (ReplicationRecord) container().getByID(trans, replicationRecordId);
			container().activate(trans, replicationRecord, new FixedActivationDepth(Integer.MAX_VALUE));
			replicationRecord.setVersion(timestamp);
			replicationRecord.concurrentTimestamps(concurrentTimestamps);
			replicationRecord.store(trans);
			container().storeAfterReplication(trans, replicationRecord, container().updateDepthProvider().forDepth(Integer.MAX_VALUE), false);
			 
			trans.commit(dispatcher);
			committedInfo = dispatcher.committedInfo();
			transaction().useDefaultTransactionTimestamp();
		}
		return Msg.OK;
	}

}
