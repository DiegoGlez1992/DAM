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
 * @exclude
 */
public class Hashtable4 extends HashtableBase implements DeepClone, Map4 {

	public Hashtable4(int size) {
		super(size);
	}

	public Hashtable4() {
		this(1);
	}
	
    /** @param cloneOnlyCtor */
	protected Hashtable4(DeepClone cloneOnlyCtor) {
		super(cloneOnlyCtor);
	}
	
	public Object deepClone(Object obj) {
		return deepCloneInternal(new Hashtable4((DeepClone)null), obj);
	}

	public void forEachKeyForIdentity(Visitor4 visitor, Object obj) {
		for (int i = 0; i < _table.length; i++) {
			HashtableIntEntry entry = _table[i];
			while (entry != null) {
				if (entry._object == obj) {
					visitor.visit(entry.key());
				}
				entry = entry._next;
			}
		}
	}

	public Object get(byte[] key) {
		int intKey = HashtableByteArrayEntry.hash(key);
		return getFromObjectEntry(intKey, key);
	}

	public Object get(int key) {
		HashtableIntEntry entry = _table[key & _mask];
		while (entry != null) {
			if (entry._key == key) {
				return entry._object;
			}
			entry = entry._next;
		}
		return null;
	}

	public Object get(Object key) {
		if (key == null) {
			return null;
		}
		return getFromObjectEntry(key.hashCode(), key);
	}
	
	public Object get(long key){
		return getFromLongEntry((int)key, key);
	}
	
	public boolean containsKey(Object key) {
		if (null == key) {
			return false;
		}
		return null != getObjectEntry(key.hashCode(), key); 
	}
	
	public boolean containsAllKeys(Iterable4 collection) {
		return containsAllKeys(collection.iterator());
	}

	public boolean containsAllKeys(Iterator4 iterator) {
		while (iterator.moveNext()) {
			if (!containsKey(iterator.current())) {
				return false;
			}
		}
		return true;
	}

	public void put(byte[] key, Object value) {
		putEntry(new HashtableByteArrayEntry(key, value));
	}

	public void put(int key, Object value) {
		putEntry(new HashtableIntEntry(key, value));
	}
	
	public void put(long key, Object value) {
		putEntry(new HashtableLongEntry(key, value));
	}

	public void put(Object key, Object value) {
		if (null == key) {
			throw new ArgumentNullException();
		}
		putEntry(new HashtableObjectEntry(key, value));
	}
	
	public Object remove(Object objectKey) {
		int intKey = objectKey.hashCode();
		return removeObjectEntry(intKey, objectKey);
	}
	
	public Object remove(long longKey) {
		return removeLongEntry((int)longKey, longKey);
	}
	
	public Object remove(byte[] key) {
		int intKey = HashtableByteArrayEntry.hash(key);
		return removeObjectEntry(intKey, key);
	}

	public Object remove(int key) {
		return removeIntEntry(key);
	}

	/**
	 * Iterates through all the {@link Entry4 entries}.
	 *   
	 * @return {@link Entry4} iterator
	 * @see #values()
	 * @see #keys()
	 * #see {@link #valuesIterator()}
	 */
	public Iterator4 iterator(){
		return hashtableIterator();
	}
	
	protected Hashtable4 deepCloneInternal(Hashtable4 ret, Object obj) {
		ret._mask = _mask;
		ret._maximumSize = _maximumSize;
		ret._size = _size;
		ret._tableSize = _tableSize;
		ret._table = new HashtableIntEntry[_tableSize];
		for (int i = 0; i < _tableSize; i++) {
			if (_table[i] != null) {
				ret._table[i] = (HashtableIntEntry) _table[i].deepClone(obj);
			}
		}
		return ret;
	}

	private Object getFromObjectEntry(int intKey, Object objectKey) {
		final HashtableObjectEntry entry = getObjectEntry(intKey, objectKey);		
		return entry == null ? null : entry._object;
	}

	private HashtableObjectEntry getObjectEntry(int intKey, Object objectKey) {
		HashtableObjectEntry entry = (HashtableObjectEntry) _table[intKey & _mask];
		while (entry != null) {
			if (entry._key == intKey && entry.hasKey(objectKey)) {
				return entry;
			}
			entry = (HashtableObjectEntry) entry._next;
		}
		return null;
	}
	
	private Object getFromLongEntry(int intKey, long longKey) {
		final HashtableLongEntry entry = getLongEntry(intKey, longKey);		
		return entry == null ? null : entry._object;
	}
	
	private HashtableLongEntry getLongEntry(int intKey, long longKey) {
		HashtableLongEntry entry = (HashtableLongEntry) _table[intKey & _mask];
		while (entry != null) {
			if (entry._key == intKey && entry._longKey == longKey) {
				return entry;
			}
			entry = (HashtableLongEntry) entry._next;
		}
		return null;
	}

}
