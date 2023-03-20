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
 * @exclude
 */
public class IntIterator4Impl implements FixedSizeIntIterator4 { 
	
	private final int _count;
	private int[] _content;
	private int _current;
	
	public IntIterator4Impl(int[] content, int count) {
		_content = content;
		_count = count;
		reset();
	}

	public int currentInt() {
		if (_content == null || _current == _count) {
			throw new IllegalStateException();
		}
		return _content[_current];
	}

	public Object current() {
		return new Integer(currentInt());
	}

	public boolean moveNext() {
		if (_current < _count - 1) {
			_current ++;
			return true;
		}
		_content = null;
		return false;
	}
	
	public void reset() {
		_current = -1;
	}

	public int size() {
		return _count;
    }

}
