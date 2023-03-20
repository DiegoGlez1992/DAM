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

import java.lang.reflect.*;
import java.util.*;

/**
 * @exclude
 * @sharpen.ignore
 */
@decaf.Ignore
public abstract class AbstractList4<E> implements Iterable<E>, Collection<E>, List<E> {

	protected transient int modCount;

	public AbstractList4() {
		super();
	}

	public boolean add(E e) {
		add(size(), e);
		return true;
	}

	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @sharpen.ignore
	 */
	public boolean addAll(Collection<? extends E> collection) {
		if(collection.isEmpty()) {
			return false;
		}
		Iterator<? extends E> cIter = collection.iterator();
		while(cIter.hasNext()) {
			add(cIter.next());
		}
		return true;
	}

	/**
	 * @sharpen.ignore
	 */
	public boolean addAll(int index, Collection<? extends E> collection) {
		if(collection.isEmpty()) {
			return false;
		}
		Iterator<? extends E> cIter = collection.iterator();
		int pos = index;
		while(cIter.hasNext()) {
			add(pos++, cIter.next());
		}
		return true;
	}

	public void clear() {
		removeRange(0, size());
	}
	
	public boolean contains(Object o) {
		return indexOf(o) != -1;
	}
	
	/**
	 * @sharpen.ignore
	 */
	public boolean containsAll(Collection<?> c) {
		Iterator<?> iter = c.iterator();
		while(iter.hasNext()) {
			if(!contains(iter.next())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @sharpen.ignore
	 */
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof List)) {
			return false;
		}
		List<?> otherList = (List<?>) other;
		if (otherList.size() != size()) {
			return false;
		}
		Iterator<E> iter = iterator();
		Iterator<?> otherIter = otherList.iterator();
		while (iter.hasNext()) {
			E e1 = iter.next();
			Object e2 = otherIter.next();
			if (!(e1 == null ? e2 == null : e1.equals(e2))) {
				return false;
			}
		}
		return true;
	}

	public abstract E get(int index);

	/**
	 * @see List#hashCode()
	 * 
	 * @sharpen.ignore
	 */
	public int hashCode() {
		int hashCode = 1;
		Iterator<E> i = iterator();
		while (i.hasNext()) {
			E obj = i.next();
			hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
		}
		return hashCode;
	}

	/**
	 * @sharpen.ignore
	 */
	public boolean isEmpty() {
		return size() == 0;
	}
	
	public Iterator<E> iterator() {
		 return new ArrayList4Iterator(-1);
	}
	
	/**
	 * @sharpen.ignore
	 */
	public int indexOf(Object o) {
		ListIterator<E> iter = listIterator();
		while(iter.hasNext()) {
			if(equals(o, iter.next())) {
				return iter.previousIndex();
			}	
		}
		return -1;
	}
	
	/**
	 * @sharpen.ignore
	 */
	public int lastIndexOf(Object o) {
		ListIterator<E> iter = listIterator(size());
		while(iter.hasPrevious()) {
			if(equals(o, iter.previous())) {
				return iter.nextIndex();
			}	
		}
		return -1;
	}
	
	/**
	 * @sharpen.ignore
	 */
	private boolean equals(Object e1,  E e2) {
		return (e1 == null ? e2 == null : e1.equals(e2));
	}

	/**
	 * @sharpen.ignore
	 */
	public ListIterator<E> listIterator() {
		return listIterator(0);
	}

	/**
	 * @sharpen.ignore
	 */
	public ListIterator<E> listIterator(int index) {
		checkIndex(index, 0, size());
		return new ArrayList4IndexIterator(index);
	}

	public E remove(int index) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * @sharpen.ignore
	 */
	public boolean remove(Object o) {
		int index = indexOf(o);
		if (index == -1) {
			return false;
		}
		remove(index);
		return true;
	}
	
	/**
	 * @sharpen.ignore
	 */
	public boolean removeAll(Collection <?> c) {
		boolean changed = false;
		Iterator<?> it = iterator();
		while (it.hasNext()) {
			if (c.contains(it.next())) {
				it.remove();
				changed = true;
			}
		}
		return changed;
	}

	/**
	 * @sharpen.ignore
	 */
	protected void removeRange(int fromIndex, int toIndex) {
		if ((fromIndex < 0 || fromIndex >= size() || toIndex > size() || toIndex < fromIndex)) {
			throw new IndexOutOfBoundsException();
		}
		if (fromIndex == toIndex) {
			return;
		}
		ListIterator<E> iter = listIterator(fromIndex);
		for(int i= fromIndex; i < toIndex; ++i) {
			iter.next();
			iter.remove();
		}
	}

	/**
	 * @sharpen.ignore
	 */
	public boolean retainAll(Collection <?> c) {
		boolean changed = false;
		Iterator<?> it = iterator();
		while (it.hasNext()) {
			if (!c.contains(it.next())) {
				it.remove();
				changed = true;
			}
		}
		return changed;
	}
	
	/**
	 * @sharpen.ignore
	 */
	public E set(int index, E element) {
		throw new UnsupportedOperationException();		
	}
	
	/**
	 * @sharpen.internal
	 * @sharpen.property
	 */
	public abstract int size();
	
	/**
	 * @sharpen.ignore
	 */
	public List<E> subList(int fromIndex, int toIndex) {
		return new SubArrayList4 <E> (this, fromIndex, toIndex);
	}
	
	/**
	 * @sharpen.ignore
	 */
	public Object[] toArray() {
		int size = size();
		Object[] data = new Object[size];
		Iterator<E> iter = iterator();
		int i = 0;
		while(iter.hasNext()) {
			data[i++] = iter.next();
		}
		return data;
	}

	/**
	 * @sharpen.ignore
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size = size();
		if(a.length < size) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		}
		Iterator<E> iter = iterator();
		int i = 0;
		while(iter.hasNext()) {
			a[i++] = (T) iter.next();
		}
		return a;
	}

	/**
	 * @see Collection#toString()
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append('[');
		Iterator<E> iter = iterator();
		while (iter.hasNext()) {
			E element = iter.next();
			if (element != this) {
				buffer.append(element);
			} else {
				buffer.append("(this Collection)"); //$NON-NLS-1$
			}
            if(iter.hasNext()) {
                buffer.append(", "); //$NON-NLS-1$
            }
		}
		buffer.append(']');
		return buffer.toString();
	}
		
	void checkIndex(int index, int from, int to) {
		if (index < from || index > to) {
			throw new IndexOutOfBoundsException();
		}
	}
	
	/**
	 * @sharpen.ignore
	 */
	class ArrayList4Iterator implements Iterator<E> {

		protected int currentIndex;
		
		private int _iteratorModCount;
		
		protected boolean canOperate;
		
		public ArrayList4Iterator (int pos) {
			currentIndex = pos;
			syncModCount();
		}
		
		public boolean hasNext() {
			return currentIndex + 1 < size();
		}

		public E next() {
			checkConcurrentModification();
			try {
				E element = get(currentIndex + 1);
				++currentIndex;
				setCanOperateFlag(true);
				return element;
			} catch (IndexOutOfBoundsException e) {
				checkConcurrentModification();
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			checkCanOperate();
			checkConcurrentModification();
			AbstractList4.this.remove(currentIndex);
			--currentIndex;
			syncModCount();
			setCanOperateFlag(false);
		}
		
		protected void syncModCount() {
			_iteratorModCount = modCount;
		}

		protected void checkCanOperate() {
			if(!canOperate) {
				throw new IllegalStateException();
			}
		}
		
		protected void setCanOperateFlag(boolean enabled) {
			canOperate = enabled;
		}
		
		protected void checkConcurrentModification() {
			if(_iteratorModCount != modCount) {
				throw new ConcurrentModificationException();
			}
		}

	}
	
	/**
	 * @sharpen.ignore
	 */
	class ArrayList4IndexIterator extends ArrayList4Iterator
			implements ListIterator<E> {
		public ArrayList4IndexIterator(int index) {
			super(index - 1);
		}

		public void add(E element) {
			checkCanOperate();
			checkConcurrentModification();
			try {
				AbstractList4.this.add(currentIndex, element);
				++currentIndex;
				syncModCount();
				setCanOperateFlag(false);
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}

		public boolean hasPrevious() {
			return currentIndex != -1;
		}

		public int nextIndex() {
			return currentIndex + 1;
		}

		public E previous() {
			checkConcurrentModification();
			try {
				E element = get(currentIndex);
				--currentIndex;
				setCanOperateFlag(true);
				return element;
			} catch (IndexOutOfBoundsException e) {
				checkConcurrentModification();
				throw new NoSuchElementException();
			}
		}

		public int previousIndex() {
			return currentIndex;
		}

		public void set(E element) {
			checkCanOperate();
			checkConcurrentModification();
			try {
				AbstractList4.this.set(currentIndex, element);
				setCanOperateFlag(false);
			} catch (IndexOutOfBoundsException e) {
				throw new ConcurrentModificationException();
			}
		}
	}

}