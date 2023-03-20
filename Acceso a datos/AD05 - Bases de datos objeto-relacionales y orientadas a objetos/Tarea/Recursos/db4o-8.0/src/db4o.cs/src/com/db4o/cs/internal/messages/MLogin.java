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

import com.db4o.*;
import com.db4o.cs.internal.*;

/**
 * @exclude
 */
public class MLogin extends MsgD implements MessageWithResponse {

	public Msg replyFromServer() {
		synchronized (containerLock()) {
		    String userName = readString();
		    String password = readString();
		    ObjectServerImpl server = serverMessageDispatcher().server();
    		User found = server.getUser(userName);
    		if (found != null) {
    			if (found.password.equals(password)) {
    				serverMessageDispatcher().setDispatcherName(userName);
    				logMsg(32, userName);
    				int blockSize = container().blockSize();
    				int encrypt = container()._handlers.i_encrypt ? 1 : 0;
    				serverMessageDispatcher().login();
    				return Msg.LOGIN_OK.getWriterForInts(transaction(), new int[] { blockSize, encrypt, serverMessageDispatcher().dispatcherID() });
    			}
    		}
	    }
		return Msg.FAILED;
	}

}
