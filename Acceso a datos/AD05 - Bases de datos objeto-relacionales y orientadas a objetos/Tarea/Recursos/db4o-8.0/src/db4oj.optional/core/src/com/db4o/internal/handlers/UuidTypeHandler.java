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
package com.db4o.internal.handlers;

import java.util.*;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

/**
 * TypeHandler for java.util.UUID for enabling it add UuidSupport
 * ConfigurationItem to your configuration.
 * 
 * @sharpen.ignore
 * @exclude
 */
@decaf.Ignore
public class UuidTypeHandler implements ValueTypeHandler, QueryableTypeHandler, IndexableTypeHandler {

	// null marker byte + uuid most significant bytes + uuid least significant bytes
	private static final int LINK_LENGTH = 1 + 2 * Const4.LONG_LENGTH;

	public void delete(DeleteContext context) throws Db4oIOException {
		skip(context);
	}

	public void defragment(DefragmentContext context) {
		skip(context);
	}

	public void write(WriteContext context, Object obj) {
		writeTo(context, (UUID)obj);
	}

	public Object readIndexEntry(Context context, ByteArrayBuffer reader) {
		return readFrom(reader);
	}

	public void writeIndexEntry(Context context, ByteArrayBuffer writer, Object obj) {
		writeTo(writer, (UUID)obj);
	}

	public void defragIndexEntry(DefragmentContextImpl context) {
		skip(context);
	}

	public PreparedComparison prepareComparison(Context context, Object obj) {
		final UUID value = obj instanceof TransactionContext
		? ((UUID)((TransactionContext)obj)._object)
		: ((UUID)obj);
		
	return new PreparedComparison<Object>() {
		public int compareTo(Object other) {
			if (other == null) {
				return (value == null ? 0 : 1);
			}
			if(value == null) {
				return -1;
			}
		    return value.compareTo((UUID) other);
		}
	};
	}

	public int linkLength() {
		return LINK_LENGTH;
	}

	public Object indexEntryToObject(Context context, Object indexEntry) {
		return indexEntry;
	}

	public Object readIndexEntryFromObjectSlot(MarshallerFamily mf, StatefulBuffer writer) throws CorruptionException, Db4oIOException {
		return readFrom(writer);
	}

	public Object readIndexEntry(ObjectIdContext context) throws CorruptionException, Db4oIOException {
		return readFrom(context);
	}

	public boolean descendsIntoMembers() {
		return false;
	}

	public Object read(ReadContext context) {
		return readFrom(context);
	}
	
	private UUID readFrom(ReadBuffer buffer) {
		if(buffer.readByte() == 0) {
			buffer.seek(buffer.offset() + LINK_LENGTH - 1);
			return null;
		}
		return new UUID(buffer.readLong(), buffer.readLong());
	}
	
	private void writeTo(WriteBuffer buffer, UUID uuid) {
		if(uuid == null) {
			for (int byteIdx = 0; byteIdx < LINK_LENGTH; byteIdx++) {
				buffer.writeByte((byte)0);
			}
			return;
		}
		buffer.writeByte((byte)1);
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
	}

	private void skip(ReadBuffer buffer) {
		buffer.seek(buffer.offset() + linkLength());
	}

}
