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

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.db4o.activation.*;
import com.db4o.ta.*;

/**
 * Transparent activatable ArrayList implementation.
 * Implements List interface using an array to store elements.
 * Each ArrayList4 instance has a capacity, which indicates the 
 * size of the internal array. <br><br>
 * When instantiated as a result of a query, all the internal members
 * are NOT activated at all. When internal members are required to 
 * perform an operation, the instance transparently activates all 
 * the members.   
 * 
 * @see java.util.ArrayList
 * @see com.db4o.ta.Activatable
 * 
 * @sharpen.partial
 * @sharpen.ignore.implements
 * @sharpen.ignore.extends
 * @sharpen.if !SILVERLIGHT
 */

@decaf.Ignore
public class ArrayList4<E> extends AbstractList4<E> implements Cloneable,
		Serializable, RandomAccess, Activatable {

	/**
	 * @sharpen.ignore
	 */
	private static final long serialVersionUID = 7971683768827646182L;

	private E[] elements;

	private int listSize;
	
	private transient Activator _activator;
	
	/**
	 * activate basic implementation.
	 * 
	 * @see com.db4o.ta.Activatable
	 */
	public void activate(ActivationPurpose purpose) {
		if(_activator != null) {
			_activator.activate(purpose);
		}
	}

	/**
	 * bind basic implementation.
	 * 
	 * @see com.db4o.ta.Activatable
	 */
	public void bind(Activator activator) {
    	if (_activator == activator) {
    		return;
    	}
    	if (activator != null && _activator != null) {
            throw new IllegalStateException();
        }
		_activator = activator;
	}
	
	/**
	 * Same behavior as java.util.ArrayList
	 * 
	 * @see java.util.ArrayList 
	 */
	public ArrayList4() {
		this(10);
	}

	/**
	 * Same behaviour as java.util.ArrayList
	 * 
	 * @see java.util.ArrayList 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList4(Collection<? extends E> c) {
		E[] data = collectionToArray(c);
		elements = allocateStorage(data.length);
		listSize = data.length;
		System.arraycopy(data, 0, elements, 0, data.length);
	}

	/**
	 * @sharpen.ignore 
	 */
	@SuppressWarnings("unchecked")
	private E[] allocateStorage(int size) {
		return (E[]) new Object[size];
	}

	/**
	 * @sharpen.ignore 
	 */
	@SuppressWarnings("unchecked")
	private E[] collectionToArray(Collection<? extends E> c) {
		return (E[]) c.toArray();
	}

	/**
	 * Same behaviour as java.util.ArrayList
	 * 
	 * @see java.util.ArrayList 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList4(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException();
		}
		elements = allocateStorage(initialCapacity);
		listSize = 0;
	}
	
	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.internal
	 */
	public void add(int index, E element) {
		checkIndex(index, 0, size());
		ensureCapacity(size() + 1);
		arrayCopyElements(index, index + 1, listSize - index);
		elements[index] = element;
		increaseSize(1);
		markModified();
	}

	private void arrayCopyElements(int sourceIndex, int targetIndex, int length) {
		activateForWrite();
		System.arraycopy(elements, sourceIndex, elements, targetIndex, length);
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
	public boolean addAll(Collection<? extends E> c) {
		return addAll(size(), c);
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
	@SuppressWarnings("unchecked")
	public boolean addAll(int index, Collection<? extends E> c) {
		return addAllImpl(index, (E[]) c.toArray());
	}

	/**
	 * @sharpen.internal 
	 */
	private boolean addAllImpl(int index, E[] toBeAdded) {
		checkIndex(index, 0, size());
		int length = toBeAdded.length;
		if(length == 0) {
			return false;
		}
		ensureCapacity(size() + length);
		arrayCopyElements(index, index+length, size() - index);
		System.arraycopy(toBeAdded, 0, elements, index, length);
		increaseSize(length);
		markModified();
		return true;
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 */
	public void clear() { 
		int size = size();
		activateForWrite();
		Arrays.fill(elements, 0, size, defaultValue());
		setSize(0);
		markModified();
	}
	
	/**
	 * Used to abstract default value gathering because java does not support <b>default(E)</b>
	 * 
	 * @sharpen.ignore
	 */
	private E defaultValue() {
		return null;
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
	@SuppressWarnings("unchecked")
	public Object clone() {
		activate(ActivationPurpose.READ);
		try {
			ArrayList4 <E> clonedList = (ArrayList4<E>) super.clone();
			clonedList.elements = elements.clone();
			clonedList._activator = null;
			return clonedList;
		} catch (CloneNotSupportedException e) {
			throw new Error(e);
		}
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 */
	public void ensureCapacity(int minCapacity) {
		activate(ActivationPurpose.READ);
		if (minCapacity <= capacity()) {
			return;
		}
		resize(minCapacity);
	}

	private int capacity() {
		return elements.length;
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 */
	public E get(int index) {
		checkIndex(index, 0, size() - 1);
		return elements[index];
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
	public int indexOf(Object o) {
		for (int index = 0; index < size(); ++index) {
			E element = get(index);
			if (o == null ? element == null : o.equals(element)) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
	@SuppressWarnings("unchecked")
	public int lastIndexOf(Object o) {
		for (int index = size() - 1; index >= 0; --index) {
			E element = get(index);
			if (o == null ? element == null : o.equals(element)) {
				return index;
			}
		}
		return -1;
	}
	
	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 *
	 * @sharpen.internal
	 * @sharpen.rename RemoveImpl
	 */
	public E remove(int index) {
		int size = size();
		E element = get(index);
		arrayCopyElements(index + 1, index, size - index - 1);
		elements[size - 1] = defaultValue();
		decreaseSize(1);
		markModified();
		return element;
	}

	/**
	 * @sharpen.ignore
	 */
	protected void removeRange(int fromIndex, int toIndex) {
		removeRangeImpl(fromIndex, toIndex - fromIndex);
	}

	private void removeRangeImpl(int fromIndex, int count) {
		int size = size();
		int toIndex = fromIndex + count;
		if ((fromIndex < 0 || fromIndex >= size || toIndex > size || toIndex < fromIndex)) {
			throw new IndexOutOfBoundsException();
		}
		if (count == 0) {
			return;
		}
		System.arraycopy(elements, toIndex, elements, fromIndex, size - toIndex);
		Arrays.fill(elements, size - count, size, defaultValue());
		decreaseSize(count);
		markModified();
	}
	
	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.internal
	 */
	public E set(int index, E element) {
		E oldValue = get(index);
		activateForWrite();
		elements[index] = element;
		return oldValue;
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 */
	public int size() {
		activate(ActivationPurpose.READ);
		return listSize;
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
	public Object[] toArray() {
		int size = size();
		Object[] data = new Object[size];
		System.arraycopy(elements, 0, data, 0, size);
		return data;
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.ignore
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		int size = size();
		if(a.length < size) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), size);
		}
		System.arraycopy(elements, 0, a, 0, size);
		return a;
	}

	/**
	 * same as java.util.ArrayList but transparently 
	 * activates the members as required.
	 * 
	 * @see java.util.ArrayList 
	 * @see com.db4o.ta.Activatable
	 * 
	 * @sharpen.rename TrimExcess
	 */
	public void trimToSize() {
		activateForWrite();
		resize(size());
	}

	@SuppressWarnings("unchecked")
	private void resize(int minCapacity) {
		markModified();
		E[] temp = allocateStorage(minCapacity);
		System.arraycopy(elements, 0, temp, 0, size());
		elements = temp;
	}

	/**
	 * @sharpen.internal
	 */
	void setSize(int count) {
		listSize = count;
	}
	
	/**
	 * @sharpen.internal
	 */
	void increaseSize(int count) {
		listSize += count;
	}
	/**
	 * @sharpen.internal
	 */
	void decreaseSize(int count) {
		listSize -= count;
	}
	
	/**
	 * @sharpen.internal
	 */
	void markModified() {
		++modCount;
	}
	
	private void activateForWrite() {
		activate(ActivationPurpose.WRITE);
	}
}
