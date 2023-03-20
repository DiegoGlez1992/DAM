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

import com.db4o.foundation.*;
import com.db4o.internal.*;

/**
 * @exclude
 */
public class BTreeAdd extends BTreePatch{

    public BTreeAdd(Transaction transaction, Object obj) {
        super(transaction, obj);
    }

    protected Object rolledBack(BTree btree){
        btree.notifyRemoveListener(new TransactionContext(_transaction, getObject()));
        return No4.INSTANCE;
    }
    
    public String toString() {
        return "(+) " + super.toString();
    }

	public Object commit(Transaction trans, BTree btree, BTreeNode node) {
	    if(_transaction == trans){
	    	return getObject();
	    }
	    return this;
	}

	public BTreePatch forTransaction(Transaction trans) {
	    if(_transaction == trans){
	        return this;
	    }
	    return null;
	}
	
	public Object key(Transaction trans) {
		if (_transaction != trans) {
			return No4.INSTANCE;
		}
		return getObject();
	}

	public Object rollback(Transaction trans, BTree btree) {
	    if(_transaction == trans){
	        return rolledBack(btree);
	    }
	    return this;
	}

    public boolean isAdd() {
        return true;
    }

	public int sizeDiff(Transaction trans) {
		return _transaction == trans ? 1 : 0;
	}

}
