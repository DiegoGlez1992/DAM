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

public class FlatteningIterator extends CompositeIterator4 {
	
	private static class IteratorStack {
		public final Iterator4 iterator;
		public final IteratorStack next;
		
		public IteratorStack(Iterator4 iterator_, IteratorStack next_) {
			iterator = iterator_;
			next = next_;
		}
	}
	
	private IteratorStack _stack;

	public FlatteningIterator(Iterator4 iterators) {
		super(iterators);
	}

	public boolean moveNext() {
		if (null == _currentIterator) {
			if (null == _stack) {
				_currentIterator = _iterators;
			} else {
				_currentIterator = pop();
			}
		}
		if (!_currentIterator.moveNext()) {
			if (_currentIterator == _iterators) {
				return false;
			}
			_currentIterator = null;
			return moveNext();
		}
		
		final Object current = _currentIterator.current();
		if (current instanceof Iterator4) {
			push(_currentIterator);
			_currentIterator = nextIterator(current);
			return moveNext();
		}
		return true;
	}

	private void push(Iterator4 currentIterator) {
		_stack = new IteratorStack(currentIterator, _stack);
	}

	private Iterator4 pop() {
		final Iterator4 iterator = _stack.iterator;
		_stack = _stack.next;
		return iterator;
	}

}
