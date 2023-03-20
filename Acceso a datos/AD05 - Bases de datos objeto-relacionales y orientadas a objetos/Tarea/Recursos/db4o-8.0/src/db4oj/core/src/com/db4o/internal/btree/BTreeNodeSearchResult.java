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
public class BTreeNodeSearchResult {
    
    private final Transaction _transaction;

	private final BTree _btree;
	
    private final BTreePointer _pointer;
    
    private final boolean _foundMatch;
    
    BTreeNodeSearchResult(Transaction transaction, BTree btree, BTreePointer pointer, boolean foundMatch) {
    	if (null == transaction || null == btree) {
    		throw new ArgumentNullException();
    	}
    	_transaction = transaction;
    	_btree = btree;
        _pointer = pointer;
        _foundMatch = foundMatch;
    }

    BTreeNodeSearchResult(Transaction trans, ByteArrayBuffer nodeReader, BTree btree, BTreeNode node, int cursor, boolean foundMatch) {
        this(trans, btree, pointerOrNull(trans, nodeReader, node, cursor), foundMatch);
    }

    BTreeNodeSearchResult(Transaction trans, ByteArrayBuffer nodeReader, BTree btree, Searcher searcher, BTreeNode node) {
        this(trans,
        	btree,
            nextPointerIf(pointerOrNull(trans, nodeReader, node, searcher.cursor()), searcher.isGreater()),
            searcher.foundMatch());
    }
    
    private static BTreePointer nextPointerIf(BTreePointer pointer, boolean condition) {
        if (null == pointer) {
            return null;
        }
        if (condition) {
            return pointer.next();
        }
        return pointer;
    }
    
    private static BTreePointer pointerOrNull(Transaction trans, ByteArrayBuffer nodeReader, BTreeNode node, int cursor) {
        return node == null ? null : new BTreePointer(trans, nodeReader, node, cursor);
    }
    
    public BTreeRange createIncludingRange(BTreeNodeSearchResult end) {
    	BTreePointer firstPointer = firstValidPointer();
        BTreePointer endPointer = end._foundMatch ? end._pointer.next() : end.firstValidPointer();
        return new BTreeRangeSingle(_transaction, _btree, firstPointer, endPointer);
    }

	public BTreePointer firstValidPointer() {
		if (null == _pointer) {
			return null;
		}
		if (_pointer.isValid()) {
			return _pointer;
        }
		return _pointer.next();
	}
   
}
