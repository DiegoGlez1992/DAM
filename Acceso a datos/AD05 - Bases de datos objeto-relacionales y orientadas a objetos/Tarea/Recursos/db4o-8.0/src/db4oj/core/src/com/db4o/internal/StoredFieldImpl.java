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
import com.db4o.foundation.*;
import com.db4o.reflect.*;


/**
 * @exclude
 */
public class StoredFieldImpl implements StoredField {
    
    private final Transaction _transaction;
    
    private final FieldMetadata _fieldMetadata;

    public StoredFieldImpl(Transaction transaction, FieldMetadata fieldMetadata) {
        _transaction = transaction;
        _fieldMetadata = fieldMetadata;
    }

    public void createIndex() {
    	synchronized(lock()){
    		_fieldMetadata.createIndex();
    	}
    }
    
    public void dropIndex() {
    	synchronized(lock()){
    		_fieldMetadata.dropIndex();
    	}
    }

    private Object lock() {
		return _transaction.container().lock();
	}

	public FieldMetadata fieldMetadata(){
        return _fieldMetadata;
    }
    
    public Object get(Object onObject) {
        return _fieldMetadata.get(_transaction, onObject);
    }

    public String getName() {
        return _fieldMetadata.getName();
    }

    public ReflectClass getStoredType() {
        return _fieldMetadata.getStoredType();
    }

    public boolean hasIndex() {
        return _fieldMetadata.hasIndex();
    }

    public boolean isArray() {
        return _fieldMetadata.isArray();
    }

    public void rename(String name) {
    	synchronized(lock()){
    		_fieldMetadata.rename(name);
    	}
    }

    public void traverseValues(Visitor4 visitor) {
        _fieldMetadata.traverseValues(_transaction, visitor);
    }
    public int hashCode() {
        return _fieldMetadata.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return _fieldMetadata.equals(((StoredFieldImpl) obj)._fieldMetadata);
    }
    
}
