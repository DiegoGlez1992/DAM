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
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class UUIDFieldMetadata extends VirtualFieldMetadata {
    
    UUIDFieldMetadata() {
        super(Handlers4.LONG_ID, new LongHandler());
        setName(Const4.VIRTUAL_FIELD_PREFIX + "uuid");
    }
    
    public void addFieldIndex(ObjectIdContextImpl context)  throws FieldIndexException{
    	
    	LocalTransaction transaction = (LocalTransaction) context.transaction();
		LocalObjectContainer localContainer = (LocalObjectContainer)transaction.container();
    	Slot oldSlot = transaction.idSystem().committedSlot(context.objectId());
    	
        int savedOffset = context.offset();
        int db4oDatabaseIdentityID = context.readInt();
        long uuid = context.readLong();
        context.seek(savedOffset);
        
        boolean isnew = (oldSlot.isNull());
        
        if ((uuid == 0 || db4oDatabaseIdentityID == 0) && context.objectId() > 0
                && !isnew) {
            DatabaseIdentityIDAndUUID identityAndUUID = readDatabaseIdentityIDAndUUID(
                    localContainer, context.classMetadata(), oldSlot, false);
            db4oDatabaseIdentityID = identityAndUUID.databaseIdentityID;
            uuid = identityAndUUID.uuid;
        }
        
        if(db4oDatabaseIdentityID == 0){
            db4oDatabaseIdentityID = localContainer.identity().getID(transaction);
        }
        
        if(uuid == 0){
            uuid = localContainer.generateTimeStampId();
        }
        
        StatefulBuffer writer = (StatefulBuffer) context.buffer();
        
        writer.writeInt(db4oDatabaseIdentityID);
        writer.writeLong(uuid);
        
        if(isnew){
            addIndexEntry(writer, new Long(uuid));
        }
        
    }
    
    
    static class DatabaseIdentityIDAndUUID {
    	public int databaseIdentityID;
    	public long uuid;
		public DatabaseIdentityIDAndUUID(int databaseIdentityID_, long uuid_) {
			databaseIdentityID = databaseIdentityID_;
			uuid = uuid_;
		}
    }

   private DatabaseIdentityIDAndUUID readDatabaseIdentityIDAndUUID(ObjectContainerBase container, ClassMetadata classMetadata, Slot oldSlot, boolean checkClass) throws Db4oIOException {
        if(DTrace.enabled){
            DTrace.REREAD_OLD_UUID.logLength(oldSlot.address(), oldSlot.length());
        }
		ByteArrayBuffer reader = container.decryptedBufferByAddress(oldSlot.address(), oldSlot.length());
		if(checkClass){
            ClassMetadata realClass = ClassMetadata.readClass(container,reader);
            if(realClass != classMetadata){
                return null;
            }
        }
		if (classMetadata.seekToField(container.transaction(),  reader, this) == HandlerVersion.INVALID ) {
			return null;
		}
		return new DatabaseIdentityIDAndUUID(reader.readInt(), reader.readLong());
	}

    public void delete(DeleteContextImpl context, boolean isUpdate){
        if(isUpdate){
            context.seek(context.offset() + linkLength(context));
            return;
        }
        context.seek(context.offset() + Const4.INT_LENGTH);
        long longPart = context.readLong();
        if(longPart > 0){
            if (context.container().maintainsIndices()){
                removeIndexEntry(context.transaction(), context.objectId(), new Long(longPart));
            }
        }
    }
    
    public boolean hasIndex() {
    	return true;
    }
    
    public BTree getIndex(Transaction transaction) {
    	ensureIndex(transaction);
    	return super.getIndex(transaction);
    }
    
    protected void rebuildIndexForObject(LocalObjectContainer container,
			ClassMetadata classMetadata, int objectId) throws FieldIndexException {
		Slot slot = container.systemTransaction().idSystem().currentSlot(objectId);
		DatabaseIdentityIDAndUUID data = readDatabaseIdentityIDAndUUID(container,
				classMetadata, slot, true);
		if (null == data) {
			return;
		}
		addIndexEntry(container.localSystemTransaction(), objectId, new Long(
				data.uuid));
	}
    
	private void ensureIndex(Transaction transaction) {
		if (null == transaction) {
    		throw new ArgumentNullException();
    	}
    	if (null != super.getIndex(transaction)) {
    		return;    		
    	}
        LocalObjectContainer file = ((LocalObjectContainer)transaction.container());
        SystemData sd = file.systemData();
        if(sd == null){
            // too early, in new file, try again later.
            return;
        }
    	initIndex(transaction, sd.uuidIndexId());
    	if (sd.uuidIndexId() == 0) {
            sd.uuidIndexId(super.getIndex(transaction).getID());
            file.getFileHeader().writeVariablePart(file);
    	}
	}

    void instantiate1(ObjectReferenceContext context) {
        int dbID = context.readInt();
        Transaction trans = context.transaction();
        ObjectContainerBase container = trans.container();
        container.showInternalClasses(true);
        try {
	        Db4oDatabase db = (Db4oDatabase)container.getByID2(trans, dbID);
	        if(db != null && db.i_signature == null){
	            container.activate(trans, db, new FixedActivationDepth(2));
	        }
	        VirtualAttributes va = context.objectReference().virtualAttributes();
	        va.i_database = db; 
	        va.i_uuid = context.readLong();
        } finally {
        	container.showInternalClasses(false);
        }
    }

    public int linkLength(HandlerVersionContext context) {
        return Const4.LONG_LENGTH + Const4.ID_LENGTH;
    }
    
    void marshall(Transaction trans, ObjectReference ref, WriteBuffer buffer, boolean isMigrating, boolean isNew) {
        VirtualAttributes attr = ref.virtualAttributes();
        ObjectContainerBase container = trans.container();
        boolean doAddIndexEntry = isNew && container.maintainsIndices();
        int dbID = 0;
		boolean linkToDatabase =  (attr != null && attr.i_database == null) ?  true  :  ! isMigrating;
        if(linkToDatabase){
            Db4oDatabase db = ((InternalObjectContainer)container).identity();
            if(db == null){
                // can happen on early classes like Metaxxx, no problem
                attr = null;
            }else{
    	        if (attr.i_database == null) {
    	            attr.i_database = db;
                    
                    // TODO: Should be check for ! client instead of instanceof
    	            if (container instanceof LocalObjectContainer){
    					attr.i_uuid = container.generateTimeStampId();
    	                doAddIndexEntry = true;
    	            }
    	        }
    	        db = attr.i_database;
    	        if(db != null) {
    	            dbID = db.getID(trans);
    	        }
            }
        }else{
            if(attr != null){
                dbID = attr.i_database.getID(trans);
            }
        }
        buffer.writeInt(dbID);
        if(attr == null){
            buffer.writeLong(0);
            return;
        }
        buffer.writeLong(attr.i_uuid);
        if(doAddIndexEntry){
            addIndexEntry(trans, ref.getID(), new Long(attr.i_uuid));
        }
    }
    
    void marshallIgnore(WriteBuffer buffer) {
        buffer.writeInt(0);
        buffer.writeLong(0);
    }

	public final HardObjectReference getHardObjectReferenceBySignature(final Transaction transaction, final long longPart, final byte[] signature) {
		final BTreeRange range = search(transaction, new Long(longPart));		
		final Iterator4 keys = range.keys();
		while (keys.moveNext()) {
			final FieldIndexKey current = (FieldIndexKey) keys.current();
			final HardObjectReference hardRef = getHardObjectReferenceById(transaction, current.parentID(), signature);
			if (null != hardRef) {
				return hardRef;
			}
		}
		return HardObjectReference.INVALID;
	}

	protected final HardObjectReference getHardObjectReferenceById(Transaction transaction, int parentId, byte[] signature) {
		HardObjectReference hardRef = transaction.container().getHardObjectReferenceById(transaction, parentId);
        if (hardRef._reference == null) {
        	return null;
        }
        VirtualAttributes vad = hardRef._reference.virtualAttributes(transaction, false);
        if (!Arrays4.equals(signature, vad.i_database.i_signature)) {
            return null;
        }
        return hardRef;
	}
 
	public void defragAspect(DefragmentContext context) {
		// database id
		context.copyID(); 
		// uuid
		context.incrementOffset(Const4.LONG_LENGTH);
	}
}