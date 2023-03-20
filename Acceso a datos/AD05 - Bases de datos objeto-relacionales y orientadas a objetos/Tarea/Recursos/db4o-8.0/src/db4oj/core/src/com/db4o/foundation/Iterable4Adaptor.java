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
package com.db4o.foundation;

/**
 * Adapts Iterable4/Iterator4 iteration model (moveNext, current) to the old db4o
 * and jdk model (hasNext, next).
 * 
 * @exclude
 */
public class Iterable4Adaptor {
	
	private static final Object EOF_MARKER = new Object();
	private static final Object MOVE_NEXT_MARKER = new Object();
	
	private final Iterable4 _delegate;
    
    private Iterator4 _iterator; 
    
    private Object _current = MOVE_NEXT_MARKER;
    
    public Iterable4Adaptor(Iterable4 delegate_) {
    	_delegate = delegate_;
    }
    
    public boolean hasNext() {
    	if (_current == MOVE_NEXT_MARKER) {
    		return moveNext();
    	}
    	return _current != EOF_MARKER;
    }
    
    public Object next() {
    	if (!hasNext()) {
    		throw new IllegalStateException();
    	}
        Object returnValue = _current;
        _current = MOVE_NEXT_MARKER;
        return returnValue;
    }

    protected boolean moveNext() {
    	if (null == _iterator) {
    		_iterator = _delegate.iterator();
    	}
    	if (_iterator.moveNext()) {
    		_current = _iterator.current();
    		return true;
    	}
    	_current = EOF_MARKER;
    	return false;
	}

	public void reset() {
        _iterator = null;
        _current = MOVE_NEXT_MARKER;
    }
}
