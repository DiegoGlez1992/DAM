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
package com.db4o.internal;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;


/**
 * @exclude
 */
public abstract class PersistentBase extends Identifiable implements Persistent, LinkLengthAware {
	
    void cacheDirty(Collection4 col) {
        if (!bitIsTrue(Const4.CACHED_DIRTY)) {
            bitTrue(Const4.CACHED_DIRTY);
            col.add(this);
        }
    }

    public void free(LocalTransaction trans){
    	idSystem(trans.systemTransaction()).notifySlotDeleted(getID(), slotChangeFactory());
    }

    public final int linkLength() {
        return Const4.ID_LENGTH;
    }

    final void notCachedDirty() {
        bitFalse(Const4.CACHED_DIRTY);
    }

    public void read(Transaction trans) {
		if (!beginProcessing()) {
			return;
		}
		try {
			read(trans, produceReadBuffer(trans));
		} finally {
			endProcessing();
		}
	}

	protected void read(Transaction trans, ByteArrayBuffer reader) {
		if (Deploy.debug) {
			reader.readBegin(getIdentifier());
		}
		readThis(trans, reader);
		setStateOnRead(reader);
	}
    
    protected final ByteArrayBuffer produceReadBuffer(Transaction trans){
    	return readBufferById(trans);
    }
    
    protected ByteArrayBuffer readBufferById(Transaction trans){
    	return trans.container().readBufferById(trans, getID());
    }
    
    void setStateOnRead(ByteArrayBuffer reader) {
        if (Deploy.debug) {
            reader.readEnd();
        }
        if (bitIsTrue(Const4.CACHED_DIRTY)) {
            setStateDirty();
        } else {
            setStateClean();
        }
    }

    public void write(Transaction trans) {
        if (! writeObjectBegin()) {
            return;
        }
        try {
	            
	        LocalObjectContainer container = (LocalObjectContainer)trans.container();
	        
	        if(DTrace.enabled){
	            DTrace.PERSISTENT_OWN_LENGTH.log(getID());
	        }
	        
	        int length = ownLength();
	        length = container.blockConverter().blockAlignedBytes(length);
	        
	        Slot slot = container.allocateSlot(length);
	        
	        if(isNew()){
	            setID(idSystem(trans).newId(slotChangeFactory()));
                idSystem(trans).notifySlotCreated(_id, slot, slotChangeFactory());
	        }else{
	            idSystem(trans).notifySlotUpdated(_id, slot, slotChangeFactory());
	        }
	        
	        if(DTrace.enabled){
	        	DTrace.PERSISTENT_BASE_NEW_SLOT.logLength(getID(), slot);
	        }
	        
	        ByteArrayBuffer writer = produceWriteBuffer(trans, length);
	        
	        writeToFile(trans, writer, slot);
        }finally{
        	endProcessing();
        }

    }

	public TransactionalIdSystem idSystem(Transaction trans) {
		return trans.idSystem();
	}

	protected ByteArrayBuffer produceWriteBuffer(Transaction trans, int length) {
		return newWriteBuffer(length);
	}
	
	protected ByteArrayBuffer newWriteBuffer(int length) {
		return new ByteArrayBuffer(length);
	}
    
	private final void writeToFile(Transaction trans, ByteArrayBuffer writer, Slot slot) {
		
        if(DTrace.enabled){
        	DTrace.PERSISTENTBASE_WRITE.log(getID());
        }
		
		LocalObjectContainer container = (LocalObjectContainer)trans.container();
		
		if (Deploy.debug) {
		    writer.writeBegin(getIdentifier());
		}

		writeThis(trans, writer);

		if (Deploy.debug) {
		    writer.writeEnd();
		}
		
		container.writeEncrypt(writer, slot.address(), 0);

		if (isActive()) {
		    setStateClean();
		}
	}

    public boolean writeObjectBegin() {
        if (isDirty()) {
            return beginProcessing();
        }
        return false;
    }

    public void writeOwnID(Transaction trans, ByteArrayBuffer writer) {
        write(trans);
        writer.writeInt(getID());
    }
    
    public SlotChangeFactory slotChangeFactory(){
    	return SlotChangeFactory.SYSTEM_OBJECTS;
    }
    
}
