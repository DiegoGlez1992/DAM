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
public class HashtableIntEntry implements Entry4, DeepClone  {

	// FIELDS ARE PUBLIC SO THEY CAN BE REFLECTED ON IN JDKs <= 1.1

	public int _key;

	public Object _object;

	public HashtableIntEntry _next;

	HashtableIntEntry(int key, Object obj) {
		_key = key;
		_object = obj;
	}

	public HashtableIntEntry() {
	}

	public Object key() {
		return new Integer(_key);
	}
	
	public Object value(){
		return _object;
	}

	public Object deepClone(Object obj) {
		return deepCloneInternal(new HashtableIntEntry(), obj);
	}

	public boolean sameKeyAs(HashtableIntEntry other) {
		return _key == other._key;
	}

	protected HashtableIntEntry deepCloneInternal(HashtableIntEntry entry, Object obj) {
		entry._key = _key;
		entry._next = _next;
		if (_object instanceof DeepClone) {
			entry._object = ((DeepClone) _object).deepClone(obj);
		} else {
			entry._object = _object;
		}
		if (_next != null) {
			entry._next = (HashtableIntEntry) _next.deepClone(obj);
		}
		return entry;
	}
	
	public String toString() {
		return "" + _key + ": " + _object;
	}
}
