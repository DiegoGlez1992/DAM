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

import com.db4o.cs.internal.objectexchange.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

public class MReadMultipleObjects extends MsgD implements MessageWithResponse {
	
	public final Msg replyFromServer() {
		int prefetchDepth = readInt();
		int prefetchCount = readInt();
		IntIterator4 ids = new FixedSizeIntIterator4Base(prefetchCount) {
			@Override
			protected int nextInt() {
				return readInt();
			}
		};
		ByteArrayBuffer buffer = marshallObjects(prefetchDepth, prefetchCount, ids);
		
		return Msg.READ_MULTIPLE_OBJECTS.getWriterForBuffer(transaction(), buffer);
	}

	private ByteArrayBuffer marshallObjects(int prefetchDepth, int prefetchCount, IntIterator4 ids) {
		synchronized(containerLock()){
			final ObjectExchangeStrategy strategy = ObjectExchangeStrategyFactory.forConfig(new ObjectExchangeConfiguration(prefetchDepth, prefetchCount));
			return strategy.marshall((LocalTransaction) transaction(), ids, prefetchCount);
		}
	}
}