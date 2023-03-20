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

import com.db4o.types.*;

/**
 * simplest possible linked list
 * 
 * @exclude
 */
public final class List4<T> implements Unversioned
{
	// TODO: encapsulate field access
	/**
	 * next element in list
	 */
	public List4<T> _next;
	
	/**
	 * carried object
	 */
	public T _element;  
	
	/**
	 * db4o constructor to be able to store objects of this class
	 */
	public List4() {}
	
	public List4(T element) {
		_element = element;
	}

	public List4(List4<T> next, T element) {
		_next = next;
		_element = element;
	}

	boolean holds(T obj) {
		if(obj == null){
			return _element == null;
		}
		return obj.equals(_element);
	}

	public static int size(List4<?> list) {
		int counter = 0;
		List4 nextList = list; 
		while(nextList != null){
			counter++;
			nextList = nextList._next;
		}
		return counter;
	}
	
}
