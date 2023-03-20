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

public class NoDuplicatesQueue implements Queue4 {

	private Queue4 _queue;
	private Hashtable4 _seen;
	
	public NoDuplicatesQueue(Queue4 queue) {
		_queue = queue;
		_seen = new Hashtable4();
	}
	
	public void add(Object obj) {
		if(_seen.containsKey(obj)) {
			return;
		}
		_queue.add(obj);
		_seen.put(obj, obj);
	}

	public boolean hasNext() {
		return _queue.hasNext();
	}

	public Iterator4 iterator() {
		return _queue.iterator();
	}

	public Object next() {
		return _queue.next();
	}

	public Object nextMatching(Predicate4 condition) {
		return _queue.nextMatching(condition);
	}

}
