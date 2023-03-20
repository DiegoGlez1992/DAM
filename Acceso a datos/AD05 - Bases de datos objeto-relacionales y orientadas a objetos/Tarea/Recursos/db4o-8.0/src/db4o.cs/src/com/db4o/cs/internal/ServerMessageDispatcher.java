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
import com.db4o.internal.*;


/**
 * @exclude
 */
public interface ServerMessageDispatcher extends ClientConnection, MessageDispatcher, CommittedCallbackDispatcher {

	public void queryResultFinalized(int queryResultID);

	public Socket4Adapter socket();

	public int dispatcherID();

	public LazyClientObjectSetStub queryResultForID(int queryResultID);

	public void switchToMainFile();

	public void switchToFile(MSwitchToFile file);

	public void useTransaction(MUseTransaction transaction);

	public void mapQueryResultToID(LazyClientObjectSetStub stub, int queryResultId);

	public ObjectServerImpl server();

	public void login();

	public boolean close();
	
	public boolean close(ShutdownMode mode);
	
	public void closeConnection();

	public void caresAboutCommitted(boolean care);
	
	public boolean caresAboutCommitted();
	
	public boolean write(Msg msg);

	public CallbackObjectInfoCollections committedInfo();

	public ClassInfoHelper classInfoHelper();

	public boolean processMessage(Msg message);

	public void join() throws InterruptedException;

	public void setDispatcherName(String name);
	
	public Transaction transaction();

}
