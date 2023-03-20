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
package com.db4o.cs.internal.objectexchange;

import com.db4o.cs.caching.*;
import com.db4o.cs.internal.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

public class DeferredObjectExchangeStrategy implements ObjectExchangeStrategy {

	public static final ObjectExchangeStrategy INSTANCE = new DeferredObjectExchangeStrategy();

	public ByteArrayBuffer marshall(LocalTransaction transaction, IntIterator4 ids, int count) {
	   final ByteArrayBuffer buffer = new ByteArrayBuffer(Const4.INT_LENGTH + count * Const4.INT_LENGTH);
	   final int sizeOffset = buffer.offset();
	   buffer.writeInt(0);
	   
	   int written = 0;
	   while (count > 0 && ids.moveNext()) {
		   buffer.writeInt(ids.currentInt());
		   ++written;
		   --count;
	   }
	   
	   buffer.seek(sizeOffset);
	   buffer.writeInt(written);
	   
	   return buffer;
    }

	public FixedSizeIntIterator4 unmarshall(ClientTransaction transaction, ClientSlotCache slotCache, final ByteArrayBuffer reader) {
		final int size = reader.readInt();
		return new FixedSizeIntIterator4() {
			
			int _current;
			int _available = size;

			public int size() {
	            return size;
            }

			public int currentInt() {
				return _current;
            }

			public Object current() {
				return _current;
            }

			public boolean moveNext() {
				if (_available > 0) {
					_current = reader.readInt();
					--_available;
					return true;
				}
				return false;
            }

			public void reset() {
	            throw new UnsupportedOperationException();
            }
			
		};
    }

}
