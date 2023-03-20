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

import java.util.*;

/**
 * ThreadLocal implementation for less capable platforms such as JRE 1.1 and
 * Silverlight.
 * 
 * This class is not intended to be used directly, use {@link DynamicVariable}.
 * 
 * WARNING: This implementation might leak Thread references unless
 * {@link #set(Object)} is called with null on the right thread to clean it up. This
 * behavior is currently guaranteed by {@link DynamicVariable}.
 */
public class ThreadLocal4<T> {
	
	private final Map<Thread, T> _values = new HashMap<Thread, T>();
	
	public synchronized void set(T value) {
		if (value == null) {
			_values.remove(Thread.currentThread());
		} else {
			_values.put(Thread.currentThread(), value);
		}
    }
	
	public synchronized T get() {
		return _values.get(Thread.currentThread());
	}

	protected final T initialValue() {
	    return null;
    }
}