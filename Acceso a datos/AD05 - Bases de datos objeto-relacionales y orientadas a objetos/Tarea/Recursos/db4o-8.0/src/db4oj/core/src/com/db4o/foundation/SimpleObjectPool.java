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

public class SimpleObjectPool<T> implements ObjectPool<T> {
	
	private final Object[] _objects;
	private int _available;

	public SimpleObjectPool(T... objects) {
		final int length = objects.length;
		_objects = new Object[length];
		for (int i=0; i<length; ++i) {
	        _objects[length-i-1] = objects[i];
        }
		_available = length;
    }

	@SuppressWarnings("unchecked")
    public T borrowObject() {
		if (_available == 0) {
			throw new IllegalStateException();
		}
		return (T)_objects[--_available];
	}

	public void returnObject(T o) {
		if (_available == _objects.length) {
			throw new IllegalStateException();
		}
		_objects[_available++] = o;
	}
}
