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
import com.db4o.internal.btree.algebra.*;

/**
 * @exclude
 */
public class BTreeRangeSingle implements BTreeRange {
	
	public static final Comparison4 COMPARISON = new Comparison4() {
		public int compare(Object x, Object y) {
			BTreeRangeSingle xRange = (BTreeRangeSingle)x;
			BTreeRangeSingle yRange = (BTreeRangeSingle)y;
			return xRange.first().compareTo(yRange.first());
		}
	};
    
    private final Transaction _transaction;

	private final BTree _btree;
	
	private final BTreePointer _first;
    
    private final BTreePointer _end;

    public BTreeRangeSingle(Transaction transaction, BTree btree, BTreePointer first, BTreePointer end) {
    	if (transaction == null || btree == null) {
    		throw new ArgumentNullException();
    	}
    	_transaction = transaction;
    	_btree = btree;
        _first = first;
        _end = end;
    }
    
    public void accept(BTreeRangeVisitor visitor) {
    	visitor.visit(this);
    }
    
    public boolean isEmpty() {
		return BTreePointer.equals(_first, _end);
	}
    
    public int size(){
        if(isEmpty()){
            return 0;
        }
        
// TODO: This was an attempt to improve size calculation.
//       Since all nodes are read, there is no improvement.        

//        BTreeNode currentNode = _first.node();
//        int sizeOnFirst = currentNode.count() - _first.index();
//
//        BTreeNode endNode = _end == null ? null : _end.node();
//        int substractForEnd = 
//            (endNode == null) ? 0 : (endNode.count() -  _end.index());
//        
//        int size = sizeOnFirst - substractForEnd;
//        while(! currentNode.equals(endNode)){
//            currentNode = currentNode.nextNode();
//            if(currentNode == null){
//                break;
//            }
//            currentNode.prepareRead(transaction());
//            size += currentNode.count(); 
//        }
//        return size;
        
    	int size = 0;
		final Iterator4 i = keys();
		while (i.moveNext()) {
			++size;
		}
		return size;
    }
    
    public Iterator4 pointers() {
    	return new BTreeRangePointerIterator(this);
    }

	public Iterator4 keys() {
		return new BTreeRangeKeyIterator(this);
	}

    public final BTreePointer end() {
		return _end;
	}

	public Transaction transaction() {
		return _transaction;
	}

	public BTreePointer first() {
        return _first;
    }

	public BTreeRange greater() {
		return newBTreeRangeSingle(_end, null);
	}
	
	public BTreeRange union(BTreeRange other) {
		if (null == other) {
			throw new ArgumentNullException();
		}
		return new BTreeRangeSingleUnion(this).dispatch(other);
	}
	
	public boolean adjacent(BTreeRangeSingle range) {
		return BTreePointer.equals(_end, range._first)
			|| BTreePointer.equals(range._end, _first);
	}

	public boolean overlaps(BTreeRangeSingle range) {
		return firstOverlaps(this, range) || firstOverlaps(range, this);
	}

	private boolean firstOverlaps(BTreeRangeSingle x, BTreeRangeSingle y) {
		return BTreePointer.lessThan(y._first, x._end)
			&& BTreePointer.lessThan(x._first, y._end);
	}

	public BTreeRange extendToFirst() {
		return newBTreeRangeSingle(firstBTreePointer(), _end);
	}

	public BTreeRange extendToLast() {
		return newBTreeRangeSingle(_first, null);
	}

	public BTreeRange smaller() {
		return newBTreeRangeSingle(firstBTreePointer(), _first);
	}

	public BTreeRangeSingle newBTreeRangeSingle(BTreePointer first, BTreePointer end) {
		return new BTreeRangeSingle(transaction(), _btree, first, end);
	}
	
	public BTreeRange newEmptyRange() {
		return newBTreeRangeSingle(null, null);
	}

	private BTreePointer firstBTreePointer() {
		return btree().firstPointer(transaction());
	}

	private BTree btree() {
		return _btree;
	}

	public BTreeRange intersect(BTreeRange range) {
		if (null == range) {
			throw new ArgumentNullException();
		}
		return new BTreeRangeSingleIntersect(this).dispatch(range);
	}

	public BTreeRange extendToLastOf(BTreeRange range) {
		BTreeRangeSingle rangeImpl = checkRangeArgument(range);
		return newBTreeRangeSingle(_first, rangeImpl._end);
	}
	
	public String toString() {
		return "BTreeRangeSingle(first=" + _first + ", end=" + _end + ")";
	}

	private BTreeRangeSingle checkRangeArgument(BTreeRange range) {
		if (null == range) {
			throw new ArgumentNullException();
		}
		BTreeRangeSingle rangeImpl = (BTreeRangeSingle)range;
		if (btree() != rangeImpl.btree()) {
			throw new IllegalArgumentException();
		}
		return rangeImpl;
	}

	public BTreePointer lastPointer() {
		if(_end == null){
			return btree().lastPointer(transaction());
		}
		return _end.previous();
	}

}
