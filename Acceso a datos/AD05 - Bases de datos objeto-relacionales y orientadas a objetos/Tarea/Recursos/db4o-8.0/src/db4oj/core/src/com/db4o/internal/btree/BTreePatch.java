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
package com.db4o.internal.btree;

import com.db4o.internal.*;


public abstract class BTreePatch {    
    
    protected final Transaction _transaction;
    
    protected Object _object;

    public BTreePatch(Transaction transaction, Object obj) {
        _transaction = transaction;
        _object = obj;
    }    
    
    public abstract Object commit(Transaction trans, BTree btree, BTreeNode node);

    public abstract BTreePatch forTransaction(Transaction trans);
    
    public Object getObject() {
        return _object;
    }
    
    public boolean isAdd() {
        return false;
    }
    
	public boolean isCancelledRemoval() {
		return false;
	}
	
    public boolean isRemove() {
        return false;
    }

    public abstract Object key(Transaction trans);
    
    public abstract Object rollback(Transaction trans, BTree btree);
    
    public String toString(){
        if(_object == null){
            return "[NULL]";
        }
        return _object.toString();
    }

	public abstract int sizeDiff(Transaction trans);

}
