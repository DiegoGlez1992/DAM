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

public abstract class AbstractBTreeRangeIterator implements Iterator4 {

	private final BTreeRangeSingle _range;
	private BTreePointer _cursor;
	private BTreePointer _current;

	public AbstractBTreeRangeIterator(BTreeRangeSingle range) {
		_range = range;
		_cursor = range.first();
	}

	public boolean moveNext() {
		if (reachedEnd(_cursor)) {
			_current = null;
			return false;
		}
		_current = _cursor;
		_cursor = _cursor.next();
		return true;		
	}
	
	public void reset() {
		_cursor = _range.first();
	}

	protected BTreePointer currentPointer() {
		if (null == _current) {
			throw new IllegalStateException();
		}
		return _current;
	}

	private boolean reachedEnd(BTreePointer cursor) {
	    if(cursor == null){
	        return true;
	    }
	    if(_range.end() == null){
	        return false;
	    }
	    return _range.end().equals(cursor);
	}
}