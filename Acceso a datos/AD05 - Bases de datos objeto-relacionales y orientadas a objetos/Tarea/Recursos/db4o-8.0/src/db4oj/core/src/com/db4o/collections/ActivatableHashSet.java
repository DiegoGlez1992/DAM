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

/**
 * extends ArrayList with Transparent Activation and
 * Transparent Persistence support
 * @since 7.9
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableHashSet<E> extends HashSet<E> implements ActivatableSet<E> {

	private transient Activator _activator;

	public ActivatableHashSet() {
	}

	public ActivatableHashSet(int initialCapacity) {
		super(initialCapacity);
	}

	public ActivatableHashSet(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public ActivatableHashSet(Collection<? extends E> coll) {
		super(coll);
	}

	public void activate(ActivationPurpose purpose) {
		ActivatableSupport.activate(_activator, purpose);
	}

	public void bind(Activator activator) {
		_activator = ActivatableSupport.validateForBind(_activator, activator);
	}

	@Override
	public boolean add(E o) {
		activate(ActivationPurpose.WRITE);
		return super.add(o);
	};

	@Override
	public boolean addAll(Collection<? extends E> c) {
		activate(ActivationPurpose.WRITE);
		return super.addAll(c);
	}
	
	@Override
	public void clear() {
		activate(ActivationPurpose.WRITE);
		super.clear();
	}
	
	@Override
	public boolean contains(Object o) {
		activate(ActivationPurpose.READ);
		return super.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		activate(ActivationPurpose.READ);
		return super.containsAll(c);
	}

	@Override
	public boolean equals(Object o) {
		activate(ActivationPurpose.READ);
		return super.equals(o);
	}

	@Override
	public int hashCode() {
		activate(ActivationPurpose.READ);
		return super.hashCode();
	}
	
	@Override
	public boolean isEmpty() {
		activate(ActivationPurpose.READ);
		return super.isEmpty();
	}
	
	@Override
	public Iterator<E> iterator() {
		activate(ActivationPurpose.READ);
		return new ActivatingIterator<E>(this, super.iterator());
	}
	
	@Override
	public boolean remove(Object o) {
		activate(ActivationPurpose.WRITE);
		return super.remove(o);
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		activate(ActivationPurpose.WRITE);
		return super.removeAll(c);
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		activate(ActivationPurpose.WRITE);
		return super.retainAll(c);
	}
	
	@Override
	public int size() {
		activate(ActivationPurpose.READ);
		return super.size();
	}

	@Override
	public Object[] toArray() {
		activate(ActivationPurpose.READ);
		return super.toArray();
	}

	@Override
	public <T extends Object> T[] toArray(T[] a) {
		activate(ActivationPurpose.READ);
		return super.toArray(a);
	};
	
	@Override
	public Object clone() {
		activate(ActivationPurpose.READ);
		ActivatableHashSet<E> cloned = (ActivatableHashSet<E>) super.clone();
		cloned._activator = null;
		return cloned;
	}
}
