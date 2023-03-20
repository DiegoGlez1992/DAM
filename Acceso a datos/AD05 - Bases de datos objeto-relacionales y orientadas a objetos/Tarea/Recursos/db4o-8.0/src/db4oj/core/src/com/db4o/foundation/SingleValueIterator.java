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


public class SingleValueIterator implements Iterator4 {
	
	private Object _value;
	private boolean _moved;

	public SingleValueIterator(Object value) {
		_value = value;
	}

	public Object current() {
		if (!_moved || _value == Iterators.NO_ELEMENT) {
			throw new IllegalStateException();
		}
		return _value;
	}

	public boolean moveNext() {
		if (!_moved) {
			_moved = true;
			return true;
		}
		_value = Iterators.NO_ELEMENT;
		return false;
	}

	public void reset() {
		throw new NotImplementedException();
	}

}
