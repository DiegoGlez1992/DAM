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

public class FrozenObjectInfo implements ObjectInfo {
	
    private final Db4oDatabase _sourceDatabase;
    private final long _uuidLongPart;
	private final long _id;
	private final long _commitTimestamp;
	private final Object _object;
	
    public FrozenObjectInfo(Object object, long id, Db4oDatabase sourceDatabase, long uuidLongPart, long commitTimestamp) {
        _sourceDatabase = sourceDatabase;
        _uuidLongPart = uuidLongPart;
        _id = id;
        _commitTimestamp = commitTimestamp;
        _object = object;
    }

    private FrozenObjectInfo(ObjectReference ref, VirtualAttributes virtualAttributes) {
        this(
            ref == null ? null : ref.getObject(), 
            ref == null ? -1 :ref.getID(), 
            virtualAttributes == null ? null : virtualAttributes.i_database, 
            virtualAttributes == null ? -1 : virtualAttributes.i_uuid,
            virtualAttributes == null ? 0 : virtualAttributes.i_version);
    }
    
	public FrozenObjectInfo(Transaction trans, ObjectReference ref, boolean committed) {
	    this(ref, isInstantiatedReference(ref) ? ref.virtualAttributes(trans, committed) : null);
	}

	private static boolean isInstantiatedReference(ObjectReference ref){
		return ref != null && ref.getObject() != null;
	}
	
	public long getInternalID() {
		return _id;
	}

	public Object getObject() {
		return _object;
	}

	public Db4oUUID getUUID() {
	    if(_sourceDatabase == null ){
	        return null;
	    }
	    return new Db4oUUID(_uuidLongPart, _sourceDatabase.getSignature());
	}

	public long getVersion() {
		return getCommitTimestamp();
	}
	
	public long getCommitTimestamp() {
		return _commitTimestamp;
	}

    public long sourceDatabaseId(Transaction trans) {
        if(_sourceDatabase == null){
            return -1;
        }
        return _sourceDatabase.getID(trans);
    }

    public long uuidLongPart() {
        return _uuidLongPart;
    }
}