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
package com.db4o.collections;

import java.util.*;

import com.db4o.activation.*;

/**
 * extends HashMap with Transparent Activation and
 * Transparent Persistence support
 * @since 7.9
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableHashMap<K,V> extends HashMap<K,V> implements ActivatableMap<K,V> {

	private transient Activator _activator;

	public ActivatableHashMap() {
	}

	public ActivatableHashMap(int initialCapacity) {
		super(initialCapacity);
	}

	public ActivatableHashMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ActivatableHashMap(Map<? extends K, ? extends V> map) {
		super(map);
	}

	public void activate(ActivationPurpose purpose) {
		ActivatableSupport.activate(_activator, purpose);
	}

	public void bind(Activator activator) {
		_activator = ActivatableSupport.validateForBind(_activator, activator);
	}

	@Override
	public void clear() {
		activate(ActivationPurpose.WRITE);
		super.clear();
	}
	
	@Override
	public Object clone() {
		activate(ActivationPurpose.READ);
		ActivatableHashMap cloned = (ActivatableHashMap) super.clone();
		cloned._activator = null;
		return cloned;
	}
	
	@Override
	public boolean containsKey(Object key) {
		activate(ActivationPurpose.READ);
		return super.containsKey(key);
	}
	
	@Override
	public boolean containsValue(Object value) {
		activate(ActivationPurpose.READ);
		return super.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		activate(ActivationPurpose.READ);
		return super.entrySet();
	}
	
	@Override
	public V get(Object key) {
		activate(ActivationPurpose.READ);
		return super.get(key);
	}

	@Override
	public boolean isEmpty() {
		activate(ActivationPurpose.READ);
		return super.isEmpty();
	}
	
	@Override
	public Set<K> keySet() {
		activate(ActivationPurpose.READ);		
		return new DelegatingActivatableSet<K>(ActivatableHashMap.this, super.keySet());		
	}
	
	@Override
	public V put(K key, V value) {
		activate(ActivationPurpose.WRITE);
		return super.put(key, value);
	};
	
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		activate(ActivationPurpose.WRITE);
		super.putAll(m);
	}
	
	@Override
	public V remove(Object key) {
		activate(ActivationPurpose.WRITE);
		return super.remove(key);
	}
	
	@Override
	public int size() {
		activate(ActivationPurpose.READ);
		return super.size();
	}
	
	@Override
	public Collection<V> values() {
		activate(ActivationPurpose.READ);		
		return new DelegatingActivatableCollection<V>(super.values(), this);		
	}
	
	@Override
	public boolean equals(Object o) {
		activate(ActivationPurpose.READ);
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		activate(ActivationPurpose.READ);
		return super.hashCode();
	}

}
