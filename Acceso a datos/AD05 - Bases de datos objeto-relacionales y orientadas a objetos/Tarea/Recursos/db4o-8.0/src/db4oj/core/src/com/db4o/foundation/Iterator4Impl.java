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
package  com.db4o.foundation;


/**
 * @exclude
 */
public class Iterator4Impl<T> implements Iterator4 {
	
    private final List4<T> _first;
    private List4<T> _next;
	
	private Object _current;

	public Iterator4Impl(List4 first){
		_first = first;
		_next = first;
		
		_current = Iterators.NO_ELEMENT;
	}

	public boolean moveNext() {
		if (_next == null) {
			_current = Iterators.NO_ELEMENT;
			return false;
		}
		_current = _next._element;
		_next = _next._next;
		return true;
	}

	public T current(){
		if (Iterators.NO_ELEMENT == _current) {
			throw new IllegalStateException();
		}
		return (T) _current;
	}
	
	public void reset() {
		_next = _first;
		_current = Iterators.NO_ELEMENT;
	}
}
