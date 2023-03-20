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
package com.db4o.messaging;

import com.db4o.*;
import com.db4o.internal.*;

/**
 * Additional message-related information.
 */
public interface MessageContext {
	
	/**
	 * The container the message was dispatched to.
	 * @sharpen.property
	 */
	ObjectContainer container();
	
	/**
	 * The sender of the current message.
	 * 
	 * The reference can be used to send a reply to it.
	 * @sharpen.property
	 */
	MessageSender sender();
	
	/**
	 * The transaction the current message has been sent with.
	 * @sharpen.property
	 */
	Transaction transaction();
	
}
