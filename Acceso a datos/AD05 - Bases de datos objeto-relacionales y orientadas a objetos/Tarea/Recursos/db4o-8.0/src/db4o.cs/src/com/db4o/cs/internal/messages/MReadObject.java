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

import com.db4o.internal.*;

public final class MReadObject extends MsgD implements MessageWithResponse {
	
	public final Msg replyFromServer() {
		StatefulBuffer bytes = null;
		// readWriterByID may fail in certain cases, for instance if
		// and object was deleted by another client
		int id = _payLoad.readInt();
		int lastCommitted = _payLoad.readInt();
		synchronized (containerLock()) {
			bytes = container().readStatefulBufferById(transaction(), id, lastCommitted==1);
		}
		if (bytes == null) {
			bytes = new StatefulBuffer(transaction(), 0, 0);
		}
		return Msg.OBJECT_TO_CLIENT.getWriter(bytes);
	}
}