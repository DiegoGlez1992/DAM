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

public class CompositeIterator4 implements Iterator4 {

	protected final Iterator4 _iterators;	

	protected Iterator4 _currentIterator;
	
	public CompositeIterator4(Iterator4[] iterators) {
		this(new ArrayIterator4(iterators));
	}

	public CompositeIterator4(Iterator4 iterators) {
		if (null == iterators) {
			throw new ArgumentNullException();
		}
		_iterators = iterators;
	}

	public boolean moveNext() {
		if (null == _currentIterator) {
			if (!_iterators.moveNext()) {
				return false;
			}
			_currentIterator = nextIterator(_iterators.current());
		}
		if (!_currentIterator.moveNext()) {
			_currentIterator = null;
			return moveNext();
		}
		return true;
	}
	
	public void reset() {
		resetIterators();
		_currentIterator = null;
		_iterators.reset();
	}

	private void resetIterators() {
		_iterators.reset();
		while (_iterators.moveNext()) {
			nextIterator(_iterators.current()).reset();
		}
	}
	
	public Iterator4 currentIterator() {
		return _currentIterator;
	}

	public Object current() {
		return _currentIterator.current();
	}
	
	protected Iterator4 nextIterator(final Object current) {
		return (Iterator4)current;
	}
}