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
 * A fixed size double ended queue with O(1) complexity for addFirst, removeFirst and removeLast operations.
 */
public class CircularIntBuffer4 implements Iterable4<Integer> {
	
	private static final int EMPTY = -1;
	
	private final int[] _buffer;
	private int _head;
	private int _tail;

	public CircularIntBuffer4(int size) {
		_buffer = new int[size + 1];
    }
	
	public int size() {
		return index(_tail - _head);
	}

	public void addFirst(int value) {
		final int newHead = circularIndex(_head - 1);
		if (newHead == _tail) {
			throw new IllegalStateException();
		}
		_head = newHead;
		_buffer[index(_head)] = value;
    }

	private int circularIndex(final int index) {
	    return index % _buffer.length;
    }

	private int index(int i) {
		return i < 0 ? _buffer.length + i : i;
    }

	public int removeLast() {
		assertNotEmpty();
		_tail = circularIndex(_tail - 1);
		return erase(_tail);
    }

	private void assertNotEmpty() {
	    if (isEmpty()) {
			throw new IllegalStateException();
		}
    }

	public boolean isEmpty() {
	    return index(_head) == index(_tail);
    }

	public boolean isFull() {
	    return circularIndex(_head - 1) == _tail;
    }

	public int removeFirst() {
		assertNotEmpty();
		final int erased = erase(_head);
		_head = circularIndex(_head + 1);
		return erased;
    }

	private int erase(final int index) {
	    final int bufferIndex = index(index);
		final int erasedValue = _buffer[bufferIndex];
		_buffer[bufferIndex] = EMPTY;
		return erasedValue;
    }

	public boolean remove(int value) {
		int idx = indexOf(value);
		if(idx >= 0) {
			removeAt(idx);
			return true;
		}
		return false;
    }

	public boolean contains(int value) {
		return indexOf(value) >= 0;
	}
	
	private int indexOf(int value) {
		int current = index(_head);
		int tail = index(_tail);
		while (current != tail) {
			if (value == _buffer[current]) {
				break;
			}
			current = circularIndex(current + 1);
		}
		return (current == tail ? -1 : current);
	}

	private void removeAt(int index) {
		if (index(_tail - 1) == index) {
			removeLast();
			return;
		}
		
		if (index == index(_head)) {
			removeFirst();
			return;
		}
		int current = index;
		int tail = index(_tail);
		while(current != tail){
			final int next = circularIndex(current + 1);
			_buffer[current] = _buffer[next];
			current = next;
		}
		_tail = circularIndex(_tail - 1);
    }

	public Iterator4 iterator() {
		final int tail = index(_tail);
		final int head = index(_head);
		
		// TODO: detect concurrent modification and throw IllegalStateException
		return new Iterator4() {
			
			private int _index = head;
			private Object _current = Iterators.NO_ELEMENT;

			public Object current() {
				if (_current == Iterators.NO_ELEMENT) {
					throw new IllegalStateException();
				}
				return _current;
            }

			public boolean moveNext() {
				if (_index == tail) {
					return false;
				}
				_current = _buffer[_index];
				_index = circularIndex(_index + 1);
				return true;
            }

			public void reset() {
				throw new NotImplementedException();
            }
		};
    }
}
