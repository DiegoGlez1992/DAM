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
 * Simplified version of the algorithm taken from here:
 * http://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.34.2641
 */
class LRU2QCache<K,V> implements Cache4<K,V>{
	
	private final CircularBuffer4<K> _am;
	
	private final CircularBuffer4<K> _a1;
	
	private final Map<K,V> _slots;
	
	private final int _maxSize;

	private final int _a1_threshold;
	
	LRU2QCache(int maxSize) {
		_maxSize = maxSize;
		_a1_threshold = _maxSize / 4;
		_am = new CircularBuffer4<K>(_maxSize);
		_a1 = new CircularBuffer4<K>(_maxSize);
		_slots = new HashMap<K, V>(maxSize);
	}
	
	public V produce(K key, Function4<K, V> producer, Procedure4<V> finalizer) {
		
		if(key == null){
			throw new ArgumentNullException();
		}
		
		if(_am.remove(key)){
			_am.addFirst(key);
			return _slots.get(key);
		}
		
		if(_a1.remove(key)){
			_am.addFirst(key);
			return _slots.get(key);
		}
		
		if(_slots.size() >= _maxSize){
			discardPage(finalizer);
		}
		
		final V value = producer.apply(key);
		_slots.put(key, value);
		_a1.addFirst(key);
		return value;
	}

	private void discardPage(Procedure4<V> finalizer) {
	    if(_a1.size() >= _a1_threshold) {
	    	discardPageFrom(_a1, finalizer);
	    } else {
	    	discardPageFrom(_am, finalizer);
	    }
    }

	private void discardPageFrom(final CircularBuffer4<K> list, Procedure4<V> finalizer) {
	    discard(list.removeLast(), finalizer);
    }

	private void discard(K key, Procedure4<V> finalizer) {
		if (null != finalizer) {
			finalizer.apply(_slots.get(key));
		}
	    _slots.remove(key);
    }

	public String toString() {
		return "LRU2QCache(am=" + toString(_am)  + ", a1=" + toString(_a1) + ")";
	}

	private String toString(Iterable4<K> buffer) {
		return Iterators.toString(buffer);
	}

	public Iterator<V> iterator() {
		return _slots.values().iterator();
    }
}
