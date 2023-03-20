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
package com.db4o.internal.caching;

import java.util.*;

import com.db4o.foundation.*;

/**
 * @exclude
 */
class LRULongCache<V> implements PurgeableCache4<Long, V> {
	
	private static class Entry<V> {
		
		final long _key;
		
		final V _value;
		
		Entry _previous;
		
		Entry _next;
		
		public Entry(long key, V value){
			_key = key;
			_value = value;
		}
		
		@Override
		public String toString() {
			return "" + _key;
		}
	}
	
	private final Hashtable4 _slots;
	
	private final int _maxSize;
	
	private int _size;
	
	private Entry _first;
	
	private Entry _last;
	

	LRULongCache(int size) {
		_maxSize = size;
		_slots = new Hashtable4(size);
	}

	public V produce(Long key, Function4<Long, V> producer, Procedure4<V> finalizer) {
		long longKey = key;
		if(_last == null){
			V lastValue = producer.apply(key);
			if(lastValue == null){
				return null;
			}
			_size = 1;
			Entry lastEntry = new Entry(longKey, lastValue);
			_slots.put(longKey, lastEntry);
			_first = lastEntry;
			_last = lastEntry;
			return lastValue;
		}
		
		final Entry<V> entry = (Entry)_slots.get(longKey);
		
		if (entry == null) {
			if (_size >= _maxSize) {
				Entry oldEntry = (Entry) _slots.remove(_last._key);
				_last = oldEntry._previous;
				_last._next = null;
				if (null != finalizer) {
					finalizer.apply((V) oldEntry._value);
				}
				_size --;
			}
			V newValue = producer.apply(key);
			if (newValue == null) {
				return null;
			}
			_size++;
			Entry newEntry = new Entry(longKey, newValue);
			_slots.put(longKey, newEntry);
			_first._previous = newEntry;
			newEntry._next = _first;
			_first = newEntry;
			return newValue;
		}
		if(_first == entry){
			return entry._value;
		}
		Entry previous = entry._previous;
		entry._previous = null;
		if(_last == entry){
			_last = previous;
		}
		previous._next = entry._next;
		if(previous._next != null){
			previous._next._previous = previous;
		}
		_first._previous = entry;
		entry._next = _first;
		_first = entry;
		return entry._value;
	}

	public Iterator iterator() {
		Iterator4 i = new Iterator4 () {
			private Entry _cursor = _first;
			private Entry _current;
			public Object current() {
				return _current._value;
			}
			public boolean moveNext() {
				if(_cursor == null){
					_current = null;
					return false;
				}
				_current = _cursor;
				_cursor = _cursor._next;
				return true;
			}
			public void reset() {
				_cursor = _first;
				_current = null;
			}
		};
		return Iterators.platformIterator(i);
	}

	public V purge(Long key) {
		long longKey = key;
		Entry<V> entry = (Entry<V>) _slots.remove(longKey);
		if(entry == null){
			return null;
		}
		_size --;
		if(_first == entry){
			_first = entry._next;
		}
		if(_last == entry){
			_last = entry._previous;
		}
		if(entry._previous != null){
			entry._previous._next = entry._next;
		}
		if(entry._next != null){
			entry._next._previous = entry._previous;
		}
		return entry._value;
    }
}

