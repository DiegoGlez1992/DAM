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

public class IdentityHashtable4 extends HashtableBase implements Map4 {

	public IdentityHashtable4(){
	}
	
	public IdentityHashtable4(int size){
		super(size);
	}
	
	public boolean contains(Object obj){
		return getEntry(obj) != null;
	}
	
	public Object remove(Object obj) {
		if(null == obj){
			throw new ArgumentNullException();
		}
		
		return removeIntEntry(System.identityHashCode(obj));
	}
	
	public boolean containsKey(Object key) {
		return getEntry(key) != null;
	}

	public Object get(Object key) {
		HashtableIntEntry entry = getEntry(key);
		return (entry == null ? null : entry._object);
	}

	private HashtableIntEntry getEntry(Object key) {
		return findWithSameKey(new IdentityEntry(key));
	}

	public void put(Object key, Object value) {
		if(null == key){
			throw new ArgumentNullException();
		}
		putEntry(new IdentityEntry(key, value));
	}
	
	public static class IdentityEntry extends HashtableObjectEntry{
		
		public IdentityEntry(Object obj){
			this(obj, null);
		}
		
		public IdentityEntry(Object key, Object value){
			super(System.identityHashCode(key), key, value);
		}
		
		@Override
		public boolean hasKey(Object key) {
			return _objectKey == key;
		}
	}

}
