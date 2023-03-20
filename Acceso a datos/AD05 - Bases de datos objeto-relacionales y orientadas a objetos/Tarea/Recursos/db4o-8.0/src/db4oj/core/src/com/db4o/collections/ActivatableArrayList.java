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
public class ActivatableArrayList<E> extends ArrayList<E> implements ActivatableList<E> {
	
	private transient Activator _activator;

	public ActivatableArrayList() {
	}

	public ActivatableArrayList(int size) {
		super(size);
	}

	public ActivatableArrayList(Collection<E> list) {
		super(list);
	}

	public void activate(ActivationPurpose purpose) {
		ActivatableSupport.activate(_activator, purpose);
	}

	public void bind(Activator activator) {
		_activator = ActivatableSupport.validateForBind(_activator, activator);
	}
	
	@Override
	public boolean add(E e) {
		activate(ActivationPurpose.WRITE);
		return super.add(e);
	};
	
	public void add(int index, E element) {
		activate(ActivationPurpose.WRITE);
		super.add(index, element);
	};
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		activate(ActivationPurpose.WRITE);
		return super.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		activate(ActivationPurpose.WRITE);
		return super.addAll(index, c);
	}
	
	@Override
	public void clear() {
		activate(ActivationPurpose.WRITE);
		super.clear();
	}

	@Override
	public Object clone() {
		activate(ActivationPurpose.READ);
		ActivatableArrayList<E> cloned = (ActivatableArrayList<E>) super.clone();
		cloned._activator = null;
		return cloned;
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
	public E get(int index) {
		activate(ActivationPurpose.READ);
		return super.get(index);
	}
	
	@Override
	public int hashCode() {
		activate(ActivationPurpose.READ);
		return super.hashCode();
	}
	
	@Override
	public int indexOf(Object o) {
		activate(ActivationPurpose.READ);
		return super.indexOf(o);
	}
	
	@Override
	public Iterator<E> iterator() {
		activate(ActivationPurpose.READ);
		return new ActivatingIterator(this, super.iterator());
	}
	
	@Override
	public boolean isEmpty() {
		activate(ActivationPurpose.READ);
		return super.isEmpty();
	}
	
	@Override
	public int lastIndexOf(Object o) {
		activate(ActivationPurpose.READ);
		return super.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<E> listIterator() {
		activate(ActivationPurpose.READ);
		return new ActivatingListIterator(this, super.listIterator());
	}
	
	@Override
	public ListIterator<E> listIterator(int index) {
		activate(ActivationPurpose.READ);
		return new ActivatingListIterator(this, super.listIterator(index));
	}
	
	@Override
	public E remove(int index) {
		activate(ActivationPurpose.WRITE);		
		return super.remove(index);
	}
	
	@Override
	public boolean remove(Object o) {
		activate(ActivationPurpose.WRITE);
		return super.remove(o);
	}
	
	@Override
	public E set(int index, E element) {
		activate(ActivationPurpose.WRITE);
		return super.set(index, element);
	};
	
	@Override
	public int size() {
		activate(ActivationPurpose.READ);
		return super.size();
	}
	
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		activate(ActivationPurpose.READ);
		return super.subList(fromIndex, toIndex);
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
	public boolean removeAll(Collection<?> c) {
		activate(ActivationPurpose.WRITE);
		return super.removeAll(c);
	}

	@Override
	protected void removeRange(int fromIndex, int toIndex) {
		activate(ActivationPurpose.WRITE);
		super.removeRange(fromIndex, toIndex);
	}

	@Override
	public void ensureCapacity(int minCapacity) {
		activate(ActivationPurpose.READ);
		super.ensureCapacity(minCapacity);
	}
	
	@Override
	public void trimToSize() {
		activate(ActivationPurpose.READ);
		super.trimToSize();
	}
	
	@Override
	public boolean retainAll(Collection<?> c) {
		activate(ActivationPurpose.WRITE);
		return super.retainAll(c);
	}

	@Override
	public String toString() {
		activate(ActivationPurpose.READ);
		return super.toString();
	}

}
