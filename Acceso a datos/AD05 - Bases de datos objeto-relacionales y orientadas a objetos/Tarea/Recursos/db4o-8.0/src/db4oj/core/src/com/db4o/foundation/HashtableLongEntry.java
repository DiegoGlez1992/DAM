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
public class HashtableLongEntry extends HashtableIntEntry {

	// FIELDS ARE PUBLIC SO THEY CAN BE REFLECTED ON IN JDKs <= 1.1

	public long _longKey;

	HashtableLongEntry(long key, Object obj) {
		super((int)key, obj);
		_longKey = key;
	}
	
	public HashtableLongEntry() {
		super();
	}
	
	@Override
	public Object key(){
		return _longKey;
	}

	@Override
	public Object deepClone(Object obj) {
        return deepCloneInternal(new HashtableLongEntry(), obj);
	}
    
	@Override
	protected HashtableIntEntry deepCloneInternal(HashtableIntEntry entry, Object obj) {
        ((HashtableLongEntry)entry)._longKey = _longKey;
        return super.deepCloneInternal(entry, obj);
    }

	@Override
	public boolean sameKeyAs(HashtableIntEntry other) {
		return other instanceof HashtableLongEntry
			? ((HashtableLongEntry)other)._longKey == _longKey
			: false;
	}
	
	@Override
	public String toString() {
		return "" + _longKey + ": " + _object;
	}
}
