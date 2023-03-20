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
public class CacheStatistics <K,V> implements Cache4<K, V>{
	
	private final Cache4<K, V> _delegate;
	
	private int _calls;
	
	private int _misses;
	
	public CacheStatistics (Cache4 delegate_){
		_delegate = delegate_;
	}

	public V produce(K key, final Function4<K, V> producer, Procedure4<V> onDiscard) {
		_calls++;
		Function4<K, V> delegateProducer = new Function4<K, V>(){
			public V apply(K arg) {
				_misses++;
				return producer.apply(arg);
			}
		};
		return _delegate.produce(key, delegateProducer, onDiscard);
	}

	public Iterator<V> iterator() {
		return _delegate.iterator();
	}
	
	public int calls() {
		return _calls;
	}
	
	public int misses() {
		return _misses;
	}
	
	public String toString(){
		return "Cache statistics  Calls:" + _calls + " Misses:" + _misses;
	}

}
