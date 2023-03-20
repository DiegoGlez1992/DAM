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


/**
 * 
 */
public class MObjectByUuid extends MsgD implements MessageWithResponse {
	public final Msg replyFromServer() {
		long uuid = readLong();
		byte[] signature = readBytes();
		int id = 0;
		Transaction trans = transaction();
		synchronized (containerLock()) {
			try {
				HardObjectReference hardRef = container().getHardReferenceBySignature(trans, uuid, signature);
			    if(hardRef._reference != null){
			        id = hardRef._reference.getID();
			    }
			} catch (Exception e) {
			    if(Deploy.debug){
			        e.printStackTrace();
			    }
			}
		}
		return Msg.OBJECT_BY_UUID.getWriterForInt(trans, id);
	}
}
