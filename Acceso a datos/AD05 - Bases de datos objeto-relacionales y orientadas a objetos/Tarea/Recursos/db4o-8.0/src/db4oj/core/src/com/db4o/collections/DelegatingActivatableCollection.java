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

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class DelegatingActivatableCollection<E> implements Collection<E>{
	
	private final Collection<E> _delegate;
	
	private final Activatable _activatable;
	
	DelegatingActivatableCollection(Collection<E> delegate, Activatable activatable){
		_delegate = delegate;
		_activatable = activatable;
	}

	public boolean add(E e) {
		activateForWrite();
		return _delegate.add(e);
	}

	private void activateForWrite() {
		_activatable.activate(ActivationPurpose.WRITE);
	}

	private void activateForRead() {
		_activatable.activate(ActivationPurpose.READ);
	}

	public boolean addAll(Collection<? extends E> c) {
		activateForWrite();
		return _delegate.addAll(c);
	}

	public void clear() {
		activateForWrite();
		_delegate.clear();
	}

	public boolean contains(Object o) {
		activateForRead();
		return _delegate.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		activateForRead();
		return _delegate.containsAll(c);
	}

	public boolean isEmpty() {
		activateForRead();
		return _delegate.isEmpty();
	}

	public Iterator<E> iterator() {
		activateForRead();
		return new ActivatingIterator<E>(_activatable, _delegate.iterator());
	}

	public boolean remove(Object o) {
		activateForWrite();
		return _delegate.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		activateForWrite();
		return _delegate.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		activateForWrite();
		return _delegate.retainAll(c);
	}

	public int size() {
		activateForRead();
		return _delegate.size();
	}

	public Object[] toArray() {
		activateForRead();
		return _delegate.toArray();
	}

	public <T> T[] toArray(T[] a) {
		activateForRead();
		return _delegate.toArray(a);
	}

}
