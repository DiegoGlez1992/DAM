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
package com.db4o.internal.fieldindex;

import com.db4o.foundation.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.query.processor.*;

/**
 * @exclude
 */
public class IndexedLeaf extends IndexedNodeBase implements IndexedNodeWithRange {
	
	private final BTreeRange _range;
    
    public IndexedLeaf(QConObject qcon) {
    	super(qcon);
    	_range = search();
    }
    
    private BTreeRange search() {
		final BTreeRange range = search(constraint().getObject());
        final QEBitmap bitmap = QEBitmap.forQE(constraint().evaluator());
        if (bitmap.takeGreater()) {        
            if (bitmap.takeEqual()) {
                return range.extendToLast();
            }            
            final BTreeRange greater = range.greater();
            if (bitmap.takeSmaller()) {
            	return greater.union(range.smaller());
            }
			return greater;
        }
        if (bitmap.takeSmaller()) {
        	if (bitmap.takeEqual()) {
        		return range.extendToFirst();
        	}
        	return range.smaller();
        }
        return range;
    }

	public int resultSize() {
        return _range.size();
    }

	public Iterator4 iterator() {
		return _range.keys();
	}

	public BTreeRange getRange() {
		return _range;
	}
	
	public void markAsBestIndex() {
		_constraint.setProcessedByIndex();
	}

}
