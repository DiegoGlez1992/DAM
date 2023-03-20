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


/**
 * @exclude
 * 
 * @sharpen.ignore
 */
@decaf.Ignore
public class MapEntry4<K, V> implements Map.Entry<K, V> {

    private K _key;

    private V _value;

    public MapEntry4(K key, V value) {
        _key = key;
        _value = value;
    }

    public K getKey() {
        return _key;
    }

    public V getValue() {
        return _value;
    }

    public V setValue(V value) {
        V oldValue = value;
        this._value = value;
        return oldValue;
    }

    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Map.Entry)) {
            return false;
        }

        MapEntry4<K, V> other = (MapEntry4<K, V>) o;

        return (_key == null ? other.getKey() == null : _key.equals(other
                .getKey())
                && _value == null ? other.getValue() == null : _value
                .equals(other.getValue()));

    }

    public int hashCode() {
        return (_key == null ? 0 : _key.hashCode())
                ^ (_value == null ? 0 : _value.hashCode());
    }
}
