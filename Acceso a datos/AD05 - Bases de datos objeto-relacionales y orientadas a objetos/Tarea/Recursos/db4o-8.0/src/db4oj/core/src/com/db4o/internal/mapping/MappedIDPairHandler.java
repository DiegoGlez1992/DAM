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
package com.db4o.internal.mapping;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class MappedIDPairHandler implements Indexable4 {

	private final IntHandler _origHandler;
	private final IntHandler _mappedHandler;
	
	public MappedIDPairHandler() {
		_origHandler=new IntHandler();
		_mappedHandler=new IntHandler();
	}

	public void defragIndexEntry(DefragmentContextImpl context) {
        throw new NotImplementedException();
	}

	public int linkLength() {
		return _origHandler.linkLength()+_mappedHandler.linkLength();
	}

	public Object readIndexEntry(Context context, ByteArrayBuffer reader) {
		int origID=readID(context, reader);
		int mappedID=readID(context, reader);
        return new MappedIDPair(origID,mappedID);
	}

	public void writeIndexEntry(Context context, ByteArrayBuffer reader, Object obj) {
		MappedIDPair mappedIDs=(MappedIDPair)obj;
		_origHandler.writeIndexEntry(context, reader, new Integer(mappedIDs.orig()));
		_mappedHandler.writeIndexEntry(context, reader, new Integer(mappedIDs.mapped()));
	}

	private int readID(Context context, ByteArrayBuffer a_reader) {
		return ((Integer)_origHandler.readIndexEntry(context, a_reader)).intValue();
	}

	public PreparedComparison prepareComparison(Context context, Object source) {
		MappedIDPair sourceIDPair = (MappedIDPair)source;
		final int sourceID = sourceIDPair.orig();
		return new PreparedComparison() {
			public int compareTo(Object target) {
				MappedIDPair targetIDPair = (MappedIDPair)target;
				int targetID = targetIDPair.orig();
				return sourceID == targetID ? 0 : (sourceID < targetID ? - 1 : 1); 
			}
		};
	}
}
