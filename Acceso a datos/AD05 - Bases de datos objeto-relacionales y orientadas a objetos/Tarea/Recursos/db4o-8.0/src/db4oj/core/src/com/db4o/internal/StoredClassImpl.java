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

/**
 * @exclude
 */
public class StoredClassImpl implements StoredClass {
    
    private final Transaction _transaction;
    
    private final ClassMetadata _classMetadata;
    
    public StoredClassImpl(Transaction transaction, ClassMetadata classMetadata){
        if(classMetadata == null){
            throw new IllegalArgumentException();
        }
        _transaction = transaction;
        _classMetadata = classMetadata;
    }

    public long[] getIDs() {
        return _classMetadata.getIDs(_transaction);
    }

    public String getName() {
        return _classMetadata.getName();
    }

    public StoredClass getParentStoredClass() {
        ClassMetadata parentClassMetadata = _classMetadata.getAncestor();
        if(parentClassMetadata == null){
            return null;
        }
        return new StoredClassImpl(_transaction, parentClassMetadata);
    }

    public StoredField[] getStoredFields() {
        StoredField[] fieldMetadata = _classMetadata.getStoredFields();
        StoredField[] storedFields = new StoredField[fieldMetadata.length];
        for (int i = 0; i < fieldMetadata.length; i++) {
            storedFields[i] = new StoredFieldImpl(_transaction, (FieldMetadata)fieldMetadata[i]);
        }
        return storedFields;
    }
    
    public boolean hasClassIndex() {
        return _classMetadata.hasClassIndex();
    }

    public void rename(final String newName) {
    	InternalObjectContainer container = (InternalObjectContainer) _transaction.objectContainer();
    	container.syncExec(new Closure4() { public Object run() {
    		_classMetadata.rename(newName);
			return null;
		}});
    }

    public StoredField storedField(String name, Object type) {
        FieldMetadata fieldMetadata = (FieldMetadata) _classMetadata.storedField(name, type);
        if(fieldMetadata == null){
            return null;
        }
        return new StoredFieldImpl(_transaction, fieldMetadata);
    }
    
    public int hashCode() {
        return _classMetadata.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        return _classMetadata.equals(((StoredClassImpl) obj)._classMetadata);
    }
    
    @Override
    public String toString() {
        return "StoredClass(" + _classMetadata + ")";
    }

	public int instanceCount() {
		return _classMetadata.instanceCount(_transaction);
	}

}
