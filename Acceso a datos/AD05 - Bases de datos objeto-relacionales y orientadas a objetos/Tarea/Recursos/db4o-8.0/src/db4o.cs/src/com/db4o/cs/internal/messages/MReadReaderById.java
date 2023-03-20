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

import com.db4o.ext.*;
import com.db4o.internal.*;

public class MReadReaderById extends MsgD implements MessageWithResponse {
	
	public final Msg replyFromServer() {
		ByteArrayBuffer bytes = null;
		// readWriterByID may fail in certain cases, for instance if
		// and object was deleted by another client
		try {
			synchronized (containerLock()) {
				bytes = container().readBufferById(transaction(), _payLoad.readInt(), _payLoad.readInt()==1);
			}
			if (bytes == null) {
				bytes = new ByteArrayBuffer(0);
			}
		}
		catch(Db4oRecoverableException exc) {
			throw exc;
		}
		catch(Throwable exc) {
			throw new Db4oRecoverableException(exc);
		}
		return Msg.READ_BYTES.getWriter(transaction(), bytes);
	}
}