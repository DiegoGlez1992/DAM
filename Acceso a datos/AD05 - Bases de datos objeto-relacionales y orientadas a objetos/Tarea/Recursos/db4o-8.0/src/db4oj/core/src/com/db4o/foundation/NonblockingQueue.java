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
 * Unbounded queue.
 * @exclude
 */
public class NonblockingQueue<T> implements Queue4<T> {

	private List4<T> _insertionPoint;
	private List4<T> _next;
	
    /* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#add(java.lang.Object)
	 */
    public final void add(T obj) {
    	List4<T> newNode = new List4<T>(null, obj);
    	if (_insertionPoint == null) {
    		_next = newNode;
    	} else {
    		_insertionPoint._next = newNode;
    	}
    	_insertionPoint = newNode;
    }
    
	/* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#next()
	 */
	public final T next() {
		if(_next == null){
			return null;
		}
		T ret = _next._element;
		removeNext();
		return ret;
	}

	private void removeNext() {
		_next = _next._next;
		if (_next == null) {
			_insertionPoint = null;
		}
	}
	
	public T nextMatching(Predicate4<T> condition) {
		if (null == condition) {
			throw new ArgumentNullException();
		}
		
		List4<T> current = _next;
		List4<T> previous = null;
		while (null != current) {
			final T element = current._element;
			if (condition.match(element)) {
				if (previous == null) {
					removeNext();
				} else {
					previous._next = current._next;
				}
				return element;
			}
			previous = current;
			current = current._next;
		}
		return null;
	}
    
    /* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#hasNext()
	 */
    public final boolean hasNext() {
        return _next != null;
    }

	/* (non-Javadoc)
	 * @see com.db4o.foundation.Queue4#iterator()
	 */
	public Iterator4<T> iterator() {
		final List4<T> origInsertionPoint = _insertionPoint;
		final List4<T> origNext = _next;
		return new Iterator4Impl<T>(_next) {
			
			public boolean moveNext() {
				if (origInsertionPoint != _insertionPoint || origNext != _next) {
					throw new IllegalStateException();
				}
				return super.moveNext();
			}
		};
	}
}
