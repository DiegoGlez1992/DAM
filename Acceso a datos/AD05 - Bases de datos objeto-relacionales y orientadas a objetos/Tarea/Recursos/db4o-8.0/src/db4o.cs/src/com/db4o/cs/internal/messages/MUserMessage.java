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
import com.db4o.messaging.*;

public final class MUserMessage extends MsgObject implements ServerSideMessage, ClientSideMessage {
	
	public final void processAtServer() {
		processUserMessage();
	}
	
	public boolean processAtClient() {
		return processUserMessage();
	}
	
	private class MessageContextImpl implements MessageContext {
	
		public MessageSender sender() {
			return new MessageSender() {
				public void send(Object message) {
					serverMessageDispatcher().write(Msg.USER_MESSAGE.marshallUserMessage(transaction(), message));
				}
			};
		}
	
		public ObjectContainer container() {
			return transaction().objectContainer();
		}

		public Transaction transaction() {
			return MUserMessage.this.transaction();
		}
	};
	
	private boolean processUserMessage() {
		final MessageRecipient recipient = messageRecipient();
		if (recipient == null) {
			return true;
		}
		
		try {
			recipient.processMessage(new MessageContextImpl(), readUserMessage());
		} catch (Exception x) {
			// TODO: use MessageContext.sender() to send
			// error back to client
			x.printStackTrace();
		}
		return true;
	}

	private Object readUserMessage() {
		unmarshall();
		return ((UserMessagePayload)readObjectFromPayLoad()).message;
	}
	
	private MessageRecipient messageRecipient() {
		return config().messageRecipient();
	}
	
	public static final class UserMessagePayload {
		public Object message;
		
		public UserMessagePayload() {
		}
		
		public UserMessagePayload(Object message_) {
			message = message_;
		}
	}

	public Msg marshallUserMessage(Transaction transaction, Object message) {
		return getWriter(Serializer.marshall(transaction, new UserMessagePayload(message)));
	}
}