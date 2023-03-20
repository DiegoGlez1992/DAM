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
public class Stack4 {
	
	private List4 _tail;

	public void push(Object obj) {
		_tail = new List4(_tail, obj);
	}

	public Object peek() {
		if(_tail == null){
			return null;
		}
		return _tail._element;
	}
	
	public Object pop() {
		if(_tail == null){
			throw new IllegalStateException();
		}
		Object res = _tail._element;
		_tail = _tail._next;
		return res;
	}

	public boolean isEmpty() {
		return _tail==null;
	}
	
}
