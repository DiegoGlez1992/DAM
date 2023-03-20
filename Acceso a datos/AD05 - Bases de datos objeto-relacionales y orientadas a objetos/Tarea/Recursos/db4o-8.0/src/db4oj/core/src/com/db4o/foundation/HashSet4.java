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

public class HashSet4 implements Set4 {

	private Hashtable4 _map;
	
	public HashSet4() {
		this(1);
	}
	
	public HashSet4(int count) {
		_map = new Hashtable4(count);
	}
	
	public boolean add(Object obj) {
		if(_map.containsKey(obj)) {
			return false;
		}
		_map.put(obj, obj);
		return true;
	}

	public void clear() {
		_map.clear();
	}

	public boolean contains(Object obj) {
		return _map.containsKey(obj);
	}

	public boolean isEmpty() {
		return _map.size() == 0;
	}

	public Iterator4 iterator() {
		return _map.values().iterator();
	}

	public boolean remove(Object obj) {
		return _map.remove(obj) != null;
	}

	public int size() {
		return _map.size();
	}
	
	public String toString() {
		return Iterators.join(_map.keys() , "{", "}", ", ");
	}


}
