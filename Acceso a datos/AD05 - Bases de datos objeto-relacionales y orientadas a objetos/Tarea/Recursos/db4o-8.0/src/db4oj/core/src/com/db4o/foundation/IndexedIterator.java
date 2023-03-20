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
 * Basic functionality for implementing iterators for
 * fixed length structures whose elements can be efficiently
 * accessed by a numeric index.
 */
public abstract class IndexedIterator implements Iterator4 {

	private final int _length;
	private int _next;

	public IndexedIterator(int length) {
		_length = length;
		_next = -1;
	}

	public boolean moveNext() {
		if (_next < lastIndex()) {
			++_next;
			return true;
		}
		// force exception on unexpected call to current
		_next = _length;
		return false;
	}

	public Object current() {
		return get(_next); 
	}
	
	public void reset() {
		_next = -1;
	}
	
	protected abstract Object get(final int index);

	private int lastIndex() {
		return _length - 1;
	}

}