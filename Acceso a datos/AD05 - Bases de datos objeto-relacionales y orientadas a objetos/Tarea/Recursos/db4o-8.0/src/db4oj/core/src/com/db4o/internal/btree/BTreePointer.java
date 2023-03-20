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
public final class BTreePointer{
	
	public static BTreePointer max(BTreePointer x, BTreePointer y) {
		if (x == null) {
			return x;
		}
		if (y == null) {
			return y;
		}
		if (x.compareTo(y) > 0) {
			return x;
		}
		return y;
	}

	public static BTreePointer min(BTreePointer x, BTreePointer y) {
		if (x == null) {
			return y;
		}
		if (y == null) {
			return x;
		}
		if (x.compareTo(y) < 0) {
			return x;
		}
		return y;
	}
    
    private final BTreeNode _node;
    
    private final int _index;

	private final Transaction _transaction;

	private final ByteArrayBuffer _nodeReader;
   
    public BTreePointer(Transaction transaction, ByteArrayBuffer nodeReader, BTreeNode node, int index) {
    	if(transaction == null || node == null){
            throw new ArgumentNullException();
        }
        _transaction = transaction;
        _nodeReader = nodeReader;
        _node = node;
        _index = index;
	}

	public final int index(){
        return _index;
    }
    
    public final BTreeNode node() {
        return _node;
    }
    
    public final Object key() {
		return _node.key(_transaction, _nodeReader, _index);
	}

	public BTreePointer next(){
        int indexInMyNode = _index + 1;
        while(indexInMyNode < _node.count()){
            if(_node.indexIsValid(_transaction, indexInMyNode)){
                return new BTreePointer(_transaction, _nodeReader, _node, indexInMyNode);
            }
            indexInMyNode ++;
        }
        int newIndex = -1;
        BTreeNode nextNode = _node;
        ByteArrayBuffer nextReader = null;
        while(newIndex == -1){
            nextNode = nextNode.nextNode();
            if(nextNode == null){
                return null;
            }
            nextReader = nextNode.prepareRead(_transaction);
            newIndex = nextNode.firstKeyIndex(_transaction);
        }
        btree().convertCacheEvictedNodesToReadMode();
        return new BTreePointer(_transaction, nextReader, nextNode, newIndex);
    }
    
	public BTreePointer previous() {
		int indexInMyNode = _index - 1;
		while(indexInMyNode >= 0){
			if(_node.indexIsValid(_transaction, indexInMyNode)){
				return new BTreePointer(_transaction, _nodeReader, _node, indexInMyNode);
			}
			indexInMyNode --;
		}
		int newIndex = -1;
		BTreeNode previousNode = _node;
		ByteArrayBuffer previousReader = null;
		while(newIndex == -1){
			previousNode = previousNode.previousNode();
			if(previousNode == null){
				return null;
			}
			previousReader = previousNode.prepareRead(_transaction);
			newIndex = previousNode.lastKeyIndex(_transaction);
		}
		return new BTreePointer(_transaction, previousReader, previousNode, newIndex);
	}    

    
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(! (obj instanceof BTreePointer)){
            return false;
        }
        BTreePointer other = (BTreePointer) obj;
        
        if(_index != other._index){
            return false;
        }
        
        return _node.equals(other._node);
    }	
    
    public int hashCode() {
    	return _node.hashCode();
    }
    
    public String toString() {
        return "BTreePointer(index=" + _index + ", node=" + _node + ")";      
    }

	public int compareTo(BTreePointer y) {
		if (null == y) {
			throw new ArgumentNullException();
		}
		if (btree() != y.btree()) {
			throw new IllegalArgumentException();
		}		
		return btree().compareKeys(_transaction.context(), key(), y.key());
	}

	private BTree btree() {
		return _node.btree();
	}

	public static boolean lessThan(BTreePointer x, BTreePointer y) {
		return BTreePointer.min(x, y) == x
			&& !equals(x, y);
	}

	public static boolean equals(BTreePointer x, BTreePointer y) {
		if (x == null) {
			return y == null;
		}
		return x.equals(y);
	}

	public boolean isValid() {
		return _node.indexIsValid(_transaction, _index);
	}    
}
