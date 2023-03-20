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

import com.db4o.ext.*;
import com.db4o.internal.CommitTimestampSupport.TimestampEntry;
import com.db4o.internal.btree.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class CommitTimestampFieldMetadata extends VirtualFieldMetadata {

    CommitTimestampFieldMetadata() {
        super(Handlers4.LONG_ID, new LongHandler());
        setName(VirtualField.COMMIT_TIMESTAMP);
    }
    
    public void addFieldIndex(ObjectIdContextImpl context)  throws FieldIndexException{
    }
    
    @Override
    public void addIndexEntry(Transaction trans, int parentID, Object indexEntry) {
    }
    
    @Override
    public void removeIndexEntry(Transaction trans, int parentID, Object indexEntry) {
    }
    
    public void delete(DeleteContextImpl context, boolean isUpdate){
    }

    void instantiate1(ObjectReferenceContext context) {
    }

    void marshall(Transaction trans, ObjectReference ref, WriteBuffer buffer, boolean isMigrating, boolean isNew) {
    }

    public int linkLength(HandlerVersionContext context) {
        return 0;
    }
    
    @Override
    public void defragAspect(DefragmentContext context) {
    }
    
    void marshallIgnore(WriteBuffer buffer) {
    }
    
    @Override
    public void activate(UnmarshallingContext context) {
    	// do nothing.
    }
    
    @Override
    public BTree getIndex(Transaction trans) {
    	return ((LocalTransaction)trans.systemTransaction()).commitTimestampSupport().timestampToId();
    }
    
    @Override
    public boolean hasIndex() {
    	return true;
    }
    
    @Override
    protected FieldIndexKey createFieldIndexKey(int parentID, Object indexEntry) {
    	return new TimestampEntry(parentID, (Long) indexEntry);
    }
    
    int counter = 0;
    @Override
    public Object read(ObjectIdContext context) {
    	int objectId = context.objectId();
    	
    	long version = context.transaction().systemTransaction().versionForId(objectId);
		return version;
    }


}