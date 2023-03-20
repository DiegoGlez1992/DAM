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
package com.db4o.collections;

import java.util.*;

/**
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class DelegatingSet<K> implements Set<K> {
	private final Set<K> _delegating;

	DelegatingSet(Set<K> originalSet) {
		_delegating = originalSet;
	}

	public boolean add(K e) {
		return _delegating.add(e);
	}

	public boolean addAll(Collection<? extends K> c) {
		return _delegating.addAll(c);
	}

	public void clear() {
		_delegating.clear();
	}

	public boolean contains(Object o) {
		return _delegating.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return _delegating.containsAll(c);
	}

	public boolean equals(Object o) {
		return _delegating.equals(o);
	}

	public int hashCode() {
		return _delegating.hashCode();
	}

	public boolean isEmpty() {
		return _delegating.isEmpty();
	}

	public boolean remove(Object o) {
		return _delegating.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return _delegating.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return _delegating.retainAll(c);
	}

	public int size() {
		return _delegating.size();
	}

	public Object[] toArray() {
		return _delegating.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return _delegating.toArray(a);
	}

	public Iterator<K> iterator() {
		return _delegating.iterator();
	}
}