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
public abstract class MappingIterator implements Iterator4 {

	private final Iterator4 _iterator;	

	private Object _current;
	
	public MappingIterator(Iterator4 iterator) {
		if (null == iterator) {
			throw new ArgumentNullException();
		}
		_iterator = iterator;
		_current = Iterators.NO_ELEMENT;
	}
	
	protected abstract Object map(final Object current);

	public boolean moveNext() {
		do {
			if (!_iterator.moveNext()) {
				_current = Iterators.NO_ELEMENT;
				return false;
			}
			_current = map(_iterator.current());
		} while(_current == Iterators.SKIP);
		return true;
	}
	
	public void reset() {
		_current = Iterators.NO_ELEMENT;
		_iterator.reset();
	}

	public Object current() {
		if (Iterators.NO_ELEMENT == _current) {
			throw new IllegalStateException();
		}
		return _current;
	}
}