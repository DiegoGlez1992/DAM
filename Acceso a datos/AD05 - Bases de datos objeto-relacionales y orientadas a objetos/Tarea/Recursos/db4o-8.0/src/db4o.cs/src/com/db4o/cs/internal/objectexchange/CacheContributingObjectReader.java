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

import java.util.*;

import com.db4o.cs.caching.*;
import com.db4o.cs.internal.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

public class CacheContributingObjectReader {

	private final ByteArrayBuffer _reader;
	private final ClientTransaction _transaction;
	private final ClientSlotCache _slotCache;

	public CacheContributingObjectReader(ClientTransaction transaction, ClientSlotCache slotCache, ByteArrayBuffer reader) {
		_reader = reader;
		_transaction = transaction;
		_slotCache = slotCache;
    }
	
	public Iterator4<Pair<Integer, ByteArrayBuffer>> buffers() {
		
		final Map<Integer, Pair<Integer, ByteArrayBuffer>> slots = readSlots();
		
		return Iterators.map(readRootIds(), new Function4<Integer, Pair<Integer, ByteArrayBuffer>>() {
			public Pair<Integer, ByteArrayBuffer> apply(Integer arg) {
				return slots.get(arg);
            }
		});
	}


	public FixedSizeIntIterator4 iterator() {
		
		contributeSlotsToCache();
		
		return readRootIds();
    }

	private FixedSizeIntIterator4 readRootIds() {
	    final int size = _reader.readInt();
		return new FixedSizeIntIterator4Base(size) {
			@Override
			protected int nextInt() {
				return _reader.readInt();
			}
		};
    }

	private void contributeSlotsToCache() {
	    final int size = _reader.readInt();
		for (int i=0; i<size; ++i) {
			readNextSlot();
		}
    }

	private Map<Integer, Pair<Integer, ByteArrayBuffer>> readSlots() {
		final Map<Integer, Pair<Integer, ByteArrayBuffer>> slots = new HashMap();
		
		final int size = _reader.readInt();
		for (int i=0; i<size; ++i) {
			final Pair<Integer, ByteArrayBuffer> slot = readNextSlot();
			slots.put(slot.first, slot);
		}
		return slots;
	}
	
	private Pair<Integer, ByteArrayBuffer> readNextSlot() {
	    int id = _reader.readInt();
	    int length = _reader.readInt(); // slot length
	    if (length == 0) {
	    	return Pair.of(id, null);
	    }
	    
	    final ByteArrayBuffer slot = readNextSlot(length);
	    contributeToCache(id, slot);
	    return Pair.of(id, slot);
    }

	private void contributeToCache(int id, final ByteArrayBuffer slot) {
	    _slotCache.add(_transaction, id, slot);
    }

	private ByteArrayBuffer readNextSlot(int length) {
	    final ByteArrayBuffer slot = _reader.readPayloadReader(_reader.offset(), length);
	    _reader.skip(length);
	    return slot;
    }

}
