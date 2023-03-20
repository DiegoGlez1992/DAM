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
class LRUCache<K, V> implements PurgeableCache4<K, V> {
	
	private final Map<K, V> _slots;
	private final CircularBuffer4<K> _lru;
	private final int _maxSize;

	LRUCache(int size) {
		_maxSize = size;
		_slots = new HashMap<K, V>(size);
		_lru = new CircularBuffer4<K>(size);
	}

	public V produce(K key, Function4<K, V> producer, Procedure4<V> finalizer) {
		final V value = _slots.get(key);
		if (value == null) {
			final V newValue = producer.apply(key);
			if (newValue == null) {
				return null;
			}
			if (_slots.size() >= _maxSize) {
				final V discarded = _slots.remove(_lru.removeLast());
				if (null != finalizer) {
					finalizer.apply(discarded);
				}
			}
			_slots.put(key, newValue);
			_lru.addFirst(key);
			return newValue;
		}
		
		_lru.remove(key); // O(N) 
		_lru.addFirst(key);
		return value;
	}

	public Iterator iterator() {
		return _slots.values().iterator();
	}

	public V purge(K key) {
		V removed = _slots.remove(key);
		if(removed == null){
			return null;
		}
		_lru.remove(key);
		return removed;
    }
}

