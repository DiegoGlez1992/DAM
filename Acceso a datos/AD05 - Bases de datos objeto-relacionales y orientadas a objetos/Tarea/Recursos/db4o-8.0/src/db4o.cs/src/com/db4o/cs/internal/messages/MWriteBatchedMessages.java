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
import com.db4o.internal.*;

public class MWriteBatchedMessages extends MsgD implements ServerSideMessage {
	public final void processAtServer() {
		ServerMessageDispatcher dispatcher = (ServerMessageDispatcher) messageDispatcher();
		int count = readInt();
		Transaction ta = transaction();
		for (int i = 0; i < count; i++) {
			StatefulBuffer writer = _payLoad.readStatefulBuffer();
			int messageId = writer.readInt();
			Msg message = Msg.getMessage(messageId);
			Msg clonedMessage = message.publicClone();
			clonedMessage.setMessageDispatcher(messageDispatcher());
			clonedMessage.setTransaction(ta);
			if (clonedMessage instanceof MsgD) {
				MsgD msgd = (MsgD) clonedMessage;
				msgd.payLoad(writer);
				if (msgd.payLoad() != null) {
					msgd.payLoad().incrementOffset(Const4.INT_LENGTH);
					Transaction t = checkParentTransaction(ta, msgd.payLoad());
					msgd.setTransaction(t);
					if (Debug4.messages) {
						System.out.println(msgd + " dispatched at " + ta.container());
					}
					dispatcher.processMessage(msgd);
				}
			} else {
				if (Debug4.messages) {
					System.out.println(clonedMessage + " dispatched at " + ta.container());
				}
				dispatcher.processMessage(clonedMessage);
			}
		}
	}

}
