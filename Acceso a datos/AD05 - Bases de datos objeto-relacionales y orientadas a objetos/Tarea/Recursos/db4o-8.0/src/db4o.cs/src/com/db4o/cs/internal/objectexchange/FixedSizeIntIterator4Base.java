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
package com.db4o.cs.internal.objectexchange;

import com.db4o.foundation.*;

public abstract class FixedSizeIntIterator4Base implements FixedSizeIntIterator4 {
    private final int _size;
    private int _current;
    private int _available;

    public FixedSizeIntIterator4Base(int size) {
	    this._size = size;
	    _available = size;
    }

    public int size() {
    	return _size;
    }

    public int currentInt() {
    	return _current;
    }

    public Object current() {
    	return _current;
    }

    public boolean moveNext() {
    	if (_available > 0) {
    		--_available;
    		_current = nextInt();
    		return true;
    	}
    	return false;
    }
    
    protected abstract int nextInt();

    public void reset() {
        throw new com.db4o.foundation.NotImplementedException();
    }
}