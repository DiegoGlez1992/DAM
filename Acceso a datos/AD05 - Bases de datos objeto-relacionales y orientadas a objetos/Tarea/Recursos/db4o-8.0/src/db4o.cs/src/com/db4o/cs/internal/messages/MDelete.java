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

public final class MDelete extends MsgD implements ServerSideMessage {
	public final void processAtServer() {
		ByteArrayBuffer bytes = this.getByteLoad();
		synchronized (containerLock()) {
			Object obj = container().tryGetByID(transaction(), bytes.readInt());
            boolean userCall = bytes.readInt() == 1;
			if (obj != null) {
				try {
				    container().delete1(transaction(), obj, userCall);
				} catch (Exception e) {
					if (Debug4.atHome) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}