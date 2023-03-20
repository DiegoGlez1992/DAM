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
 * extends Stack with Transparent Activation and
 * Transparent Persistence support.
 * @sharpen.ignore
 * @since 7.9
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableStack<E> extends Stack<E> implements ActivatableList<E> {

	private transient Activator _activator;

	public ActivatableStack() {
	}

	public void activate(ActivationPurpose purpose) {
		ActivatableSupport.activate(_activator, purpose);
	}

	public void bind(Activator activator) {
		_activator = ActivatableSupport.validateForBind(_activator, activator);
	}	

	public boolean add(E e) {
		activate(ActivationPurpose.WRITE);
		return super.add(e);
	}
	
	public void add(int index, E element) {
		activate(ActivationPurpose.WRITE);
		super.add(index, element);
	}
	
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
		ActivatableStack<E> cloned = (ActivatableStack<E>) super.clone();
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
	public boolean empty() {
		activate(ActivationPurpose.READ);
		return super.empty();
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
	protected synchronized void removeRange(int fromIndex, int toIndex) {
		activate(ActivationPurpose.WRITE);
		super.removeRange(fromIndex, toIndex);
	}
	
	@Override
	public E set(int index, E element) {
		activate(ActivationPurpose.WRITE);
		return super.set(index, element);
	}
	
	@Override
	public int size() {
		activate(ActivationPurpose.READ);
		return super.size();
	}
	
	@Override
	public synchronized int search(Object o) {
		activate(ActivationPurpose.READ);
		return super.search(o);
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
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		activate(ActivationPurpose.WRITE);
		return super.removeAll(c);
	}
	
	@Override
	public E push(E item) {
		activate(ActivationPurpose.WRITE);
		return super.push(item);
	}
	
	@Override
	public synchronized E pop() {
		activate(ActivationPurpose.READ);
		return super.pop();
	}
	
	@Override
	public synchronized E peek() {
		activate(ActivationPurpose.READ);
		return super.peek();
	}
}
