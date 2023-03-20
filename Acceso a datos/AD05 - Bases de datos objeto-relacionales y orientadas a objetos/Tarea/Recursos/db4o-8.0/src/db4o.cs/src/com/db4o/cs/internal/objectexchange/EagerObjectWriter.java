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

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public class EagerObjectWriter {

	private LocalTransaction _transaction;
	private ObjectExchangeConfiguration _config;

	public EagerObjectWriter(ObjectExchangeConfiguration config, LocalTransaction transaction) {
		_config = config;
		_transaction = transaction;
    }
	
	public ByteArrayBuffer write(IntIterator4 idIterator, int maxCount) {
		
		List<Integer> rootIds = readSlots(idIterator, maxCount);
		List<Pair<Integer, Slot>> slots = slotsFor(rootIds);
		
		int marshalledSize = marshalledSizeFor(slots) + Const4.INT_LENGTH + rootIds.size() * Const4.INT_LENGTH;
		
		ByteArrayBuffer buffer = new ByteArrayBuffer(marshalledSize);
		writeIdSlotPairsTo(slots, buffer);
		writeIds(buffer, rootIds);
		
		return buffer;
	}

	private void writeIds(ByteArrayBuffer buffer, List<Integer> ids) {
	    buffer.writeInt(ids.size());
		for (Integer id : ids) {
			buffer.writeInt(id);
        }
    }

	private List<Pair<Integer, Slot>> slotsFor(List<Integer> ids) {
	    return new SlotCollector(
	    		_config.prefetchDepth,
	    		new StandardReferenceCollector(_transaction),
	    		new StandardSlotAccessor(_transaction)).collect(
	    				Iterators.take(_config.prefetchCount, Iterators.iterator(ids)));
    }

	private void writeIdSlotPairsTo(List<Pair<Integer, Slot>> slots, ByteArrayBuffer buffer) {
	    buffer.writeInt(slots.size());
		for (Pair<Integer, Slot> idSlotPair : slots) {
			final int id = idSlotPair.first;
			final Slot slot = idSlotPair.second;
			
			if (Slot.isNull(slot)) {
				buffer.writeInt(id);
				buffer.writeInt(0);
				continue;
			}
			
			final ByteArrayBuffer slotBuffer = _transaction.localContainer().readBufferBySlot(slot);
			buffer.writeInt(id);
			buffer.writeInt(slot.length());
			buffer.writeBytes(slotBuffer._buffer);
		}
    }

	private int marshalledSizeFor(List<Pair<Integer, Slot>> slots) {
		int total = Const4.INT_LENGTH; // count
		for (Pair<Integer, Slot> idSlotPair : slots) {
			total += Const4.INT_LENGTH; // id
			total += Const4.INT_LENGTH; // length
			
			final Slot slot = idSlotPair.second;
			if (slot != null) {
				total += slot.length();
			}
		}
		return total;
    }

	private List<Integer> readSlots(IntIterator4 idIterator, int maxCount) {
	    
		final ArrayList slots = new ArrayList();
        while(idIterator.moveNext()){
            final int id = idIterator.currentInt();
            slots.add(id);
            if(slots.size() >= maxCount){
            	break;
            }
        }
		return slots;
    }
	

}
