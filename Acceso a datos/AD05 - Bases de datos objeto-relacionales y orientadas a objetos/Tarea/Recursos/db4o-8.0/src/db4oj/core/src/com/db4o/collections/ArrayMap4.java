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

import java.io.*;
import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * Transparent activatable Map implementation.
 * Implements Map interface using two arrays to store keys and values.<br><br>
 * When instantiated as a result of a query, all the internal members
 * are NOT activated at all. When internal members are required to 
 * perform an operation, the instance transparently activates all 
 * the members.   
 * 
 * @see java.util.Map
 * @see com.db4o.ta.Activatable
 * 
 * @sharpen.ignore.implements
 * @sharpen.rename ArrayDictionary4
 * @sharpen.partial
 */

@decaf.Ignore
public class ArrayMap4<K, V> implements Map<K, V>, Serializable, Cloneable,
        Activatable {

	/**
	 * @sharpen.ignore
	 */
    private static final long serialVersionUID = 1L;

    private K[] _keys;

    private V[] _values;

    private int _size;

    private transient Activator _activator;

    public ArrayMap4() {
        this(16);
    }

    public ArrayMap4(int initialCapacity) {
        initializeBackingArray(initialCapacity);
    }

	/**
	 * activate basic implementation.
	 * 
	 * @see com.db4o.ta.Activatable
	 */
    public void activate(ActivationPurpose purpose) {
        if (_activator != null) {
            _activator.activate(purpose);
        }
    }

	/**
	 * bind basic implementation.
	 * 
	 * @see com.db4o.ta.Activatable
	 */
    public void bind(Activator activator) {
    	if (_activator == activator) {
    		return;
    	}
    	if (activator != null && _activator != null) {
            throw new IllegalStateException();
        }
        _activator = activator;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public void clear() {
        activate(ActivationPurpose.WRITE);
        
        _size = 0;
        Arrays.fill(_keys, defaultKeyValue());
        Arrays.fill(_values, defaultValue());
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    public boolean containsKey(Object key) {
        return containsKeyImpl((K) key);
    }

	private boolean containsKeyImpl(K key) {
		activate(ActivationPurpose.READ);
        
        return indexOfKey(key) != -1;
	}

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    @SuppressWarnings("unchecked")
	public boolean containsValue(Object value) {
		activate(ActivationPurpose.READ);
        
        return indexOf(_values, ((V)value)) != -1;
	}

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    public Set<Map.Entry<K, V>> entrySet() {
        activate(ActivationPurpose.READ);
        
        HashSet<Map.Entry<K, V>> set = new HashSet<Entry<K, V>>();
        for (int i = 0; i < _size; i++) {
            MapEntry4<K, V> entry = new MapEntry4<K, V>(keyAt(i), valueAt(i));
            set.add(entry);
        }
        return set;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    public V get(Object key) {
        activate(ActivationPurpose.READ);
        
        int index = indexOfKey(key);
        return index == -1 ? null : valueAt(index);
    }

	private V valueAt(int index) {
		return _values[index];
	}

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    public boolean isEmpty() {
        return size() == 0;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    public Set<K> keySet() {
        activate(ActivationPurpose.READ);
        
        HashSet<K> set = new HashSet<K>();
        for (int i = 0; i < _size; i++) {
            set.add(keyAt(i));
        }
        return set;
    }

	private K keyAt(int i) {
		return _keys[i];
	}

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    public V put(K key, V value) {
        activate(ActivationPurpose.WRITE);
        return putInternal(key, value);
    }

    /**
	 * @sharpen.ignore
     */
	private V putInternal(K key, V value) {
		int index = indexOfKey(key);
        if (index == -1) {
            insert(key, value);
            return null;
        }
        return replace(index, value);
	}

    /**
     * @sharpen.ignore 
     */
	private int indexOfKey(Object key) {
		return indexOf(_keys, key);
	}

	private V replace(int index, V value) {
		V oldValue = valueAt(index);
		_values[index] = value;
		return oldValue;
	}

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    public void putAll(Map<? extends K, ? extends V> t) {
    	activate(ActivationPurpose.WRITE);
        for (Map.Entry<? extends K, ? extends V> entry : t.entrySet()) {
            putInternal(entry.getKey(), entry.getValue());
        }
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    @SuppressWarnings("unchecked")
    public V remove(Object key) {        
        activate(ActivationPurpose.READ);
        int index = indexOfKey(key);
        if (index == -1) {
            return null;
        }
        return delete(index);
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public int size() {
        activate(ActivationPurpose.READ);
        
        return _size;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.property
	 */
    public Collection<V> values() {
        activate(ActivationPurpose.READ);
        
        ArrayList<V> list = new ArrayList<V>();
        for (int i = 0; i < _size; i++) {
            list.add(valueAt(i));
        }
        return list;
    }

	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    @SuppressWarnings("unchecked")
    public ArrayMap4<K, V> clone() {
        activate(ActivationPurpose.READ);
        try {
            ArrayMap4<K, V> mapClone = (ArrayMap4<K, V>) super.clone();
            mapClone._activator = null;
            mapClone._keys =  _keys.clone();
            mapClone._values = _values.clone();
            return mapClone;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
    
	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        Map<K, V> other = (Map<K, V>) obj;
        if (size() != other.size()) {
            return false;
        }
        
        Set<K> otherKeySet = other.keySet(); 
        for (Map.Entry<K, V> entry : entrySet()) {
            K key = entry.getKey();
            if (!otherKeySet.contains(key)) {
                return false;
            }
            
            V value = entry.getValue();
            if (!(value == null ? other.get(key) == null : value.equals(other.get(key)))) {
                return false;
            }
        }
        return true;
    }
    
	/**
	 * java.util.Map implementation but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.Map 
	 * @see com.db4o.ta.Activatable
	 */
    public int hashCode() {
        int hashCode = 0;
        for (Map.Entry<K, V> entry : entrySet()) {
            hashCode += entry.hashCode();
        }
        return hashCode;
    }

    @SuppressWarnings("unchecked")
    private void initializeBackingArray(int length) {
        _keys = allocateKeyStorage(length);
        _values = allocateValueStorage(length);
    }

    /**
     * @sharpen.ignore
     */
    private int indexOf(Object[] array, Object obj) {
        int index = -1;
        for (int i = 0; i < _size; i++) {
            if (array[i] ==null ? obj == null : array[i].equals(obj)) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    private void insert(K key, V value) {
        ensureCapacity();
        _keys[_size] = key;
        _values[_size] = value;
        
        _size ++;
    }
    
    @SuppressWarnings("unchecked")
    private void ensureCapacity() {
        if (_size == _keys.length) {
            int count = _keys.length * 2;
			K[] newKeys = allocateKeyStorage(count);
            V[] newValues = allocateValueStorage(count);
            System.arraycopy(_keys, 0, newKeys, 0, _size);
            System.arraycopy(_values, 0, newValues, 0, _size);
            _keys = newKeys;
            _values = newValues;
        }
    }
   
	private V delete(int index) {
        activate(ActivationPurpose.WRITE);
        V value = valueAt(index);
        for (int i = index; i < _size -1; i++) {
            _keys[i] = _keys[i + 1];
            _values[i] = _values[i + 1];
        }
        _size--;
        _keys[_size] = defaultKeyValue();
        _values[_size] = defaultValue();
        return value;
    }
    
    /**
     * @sharpen.ignore 
     */
    private K defaultKeyValue() {
    	return null;
    }
    
    /**
     * @sharpen.ignore 
     */
    private V defaultValue() {
    	return null;
    }
    
    /**
     * @sharpen.ignore
     */
    @SuppressWarnings("unchecked")
	private V[] allocateValueStorage(int count) {
		return (V[]) new Object[count];
	}

    /**
     * @sharpen.ignore
     */
	@SuppressWarnings("unchecked")
	private K[] allocateKeyStorage(int count) {
		return (K[]) new Object[count];
	}    
}
