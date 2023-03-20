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
import com.db4o.internal.*;

public final class MGetClasses extends MsgD implements MessageWithResponse {
	public final Msg replyFromServer() {
	    synchronized (containerLock()) {
			try {

				// Since every new Client reads the class
				// collection from the file, we have to 
				// make sure, it has been written.
				container().classCollection().write(transaction());

			} catch (Exception e) {
				if (Deploy.debug) {
					System.out.println("Msg.GetConfig failed.");
				}
			}
		}
		MsgD message = Msg.GET_CLASSES.getWriterForLength(transaction(), Const4.INT_LENGTH + 1);
		ByteArrayBuffer writer = message.payLoad();
		writer.writeInt(container().classCollection().getID());
		writer.writeByte(container().stringIO().encodingByte());
		return message;
	}
}