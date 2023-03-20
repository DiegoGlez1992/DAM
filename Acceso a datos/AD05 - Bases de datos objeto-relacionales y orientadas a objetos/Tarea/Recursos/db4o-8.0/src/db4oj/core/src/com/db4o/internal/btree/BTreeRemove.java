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
public class BTreeRemove extends BTreeUpdate {
	
	public BTreeRemove(Transaction transaction, Object obj) {
        super(transaction, obj);
    }
    
    protected void committed(BTree btree){
        btree.notifyRemoveListener(new TransactionContext(_transaction, getObject()));
    }
    
    public String toString() {
        return "(-) " + super.toString();
    }
    
    public boolean isRemove() {
        return true;
    }

	protected Object getCommittedObject() {
		return No4.INSTANCE;
	}

    protected void adjustSizeOnRemovalByOtherTransaction(BTree btree, BTreeNode node) {
        // The size was reduced for this entry, let's change back.
        btree.sizeChanged(_transaction, node, +1);
    }
    
	protected int sizeDiff() {
		return 0;
	}
    
}
