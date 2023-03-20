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
 * Fast linked list for all usecases.
 * 
 * @exclude
 */
public class Collection4<T> implements Sequence4<T>, Iterable4<T>, DeepClone, Unversioned {
	
	@decaf.Public
	private List4<T> _first;

	@decaf.Public
	private List4<T> _last;

	@decaf.Public
	private int _size;

	@decaf.Public
	private int _version;
	
	public Collection4() {
	}
	
	/**
	 * For jdk11 compatibility only.
	 */
	@decaf.Ignore(except=decaf.Platform.JDK11)
	public Collection4(int initialLength) {
	}
	
	public Collection4(T[] elements) {
		addAll(elements);
	}

	public Collection4(Iterable4<T> other) {
		addAll(other);
	}
	
	public Collection4(Iterator4<T> iterator) {
		addAll(iterator);
	}

	public T singleElement() {
		if (size() != 1) {
			throw new IllegalStateException();
		}
		return _first._element;
	}

	/**
	 * Adds an element to the end of this collection.
	 * 
	 * @param element
	 */
	public final boolean add(T element) {
		doAdd(element);
		changed();
		return true;
	}	
	
	public final void prepend(T element) {
		doPrepend(element);
		changed();
	}

	private void doPrepend(T element) {
		if (_first == null) {
			doAdd(element);
		} else {
			_first = new List4<T>(_first, element);
			_size++;
		}
	}

	private void doAdd(T element) {
		if (_last == null) {
			_first = new List4<T>(element);
			_last = _first;
		} else {
			_last._next = new List4<T>(element);
			_last = _last._next;
		}
		_size++;
	}

	public final void addAll(T[] elements) {
		assertNotNull(elements);
		for (int i = 0; i < elements.length; i++) {
			add(elements[i]);
		}
	}

	public final void addAll(Iterable4<T> other) {
		assertNotNull(other);
		addAll(other.iterator());
	}

	public final void addAll(Iterator4<T> iterator) {
		assertNotNull(iterator);
		while (iterator.moveNext()) {
			add(iterator.current());
		}
	}
	
	public final void clear() {
		_first = null;
		_last = null;
		_size = 0;
		changed();
	}

	public final boolean contains(T element) {		
		return find(element) != null;
	}
	
	public boolean containsAll(Iterable4<T> iter) {
		return containsAll(iter.iterator());
	}

	public boolean containsAll(Iterator4<T> iter) {
		assertNotNull(iter);
		while (iter.moveNext()) {
			if (!contains(iter.current())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * tests if the object is in the Collection. == comparison.
	 */
	public final boolean containsByIdentity(T element) {
		Iterator4<T> i = internalIterator();
		while (i.moveNext()) {
			T current = i.current();
			if (current == element) {
				return true;
			}
		}
		return false;
	}
	
    private List4<T> find(T obj){
        List4<T> current = _first;
        while (current != null) {
            if (current.holds(obj)) {
                return current;
            }
            current = current._next;
        }
        return null;
    }
    
    private List4<T> findByIdentity(T obj){
        List4<T> current = _first;
        while (current != null) {
            if (current._element == obj) {
                return current;
            }
            current = current._next;
        }
        return null;
    }


	/**
	 * returns the first object found in the Collections that equals() the
	 * passed object
	 */
	public final T get(T element) {
	    List4<T> holder = find(element);
	    return holder == null ? null : holder._element;
	}
	
	public Object deepClone(Object newParent) {
		Collection4 col = new Collection4();
		Object element = null;
		Iterator4<T> i = internalIterator();
		while (i.moveNext()) {
			element = i.current();
			if (element instanceof DeepClone) {
				col.add(((DeepClone) element).deepClone(newParent));
			} else {
				col.add(element);
			}
		}
		return col;
	}

	/**
	 * makes sure the passed object is in the Collection. equals() comparison.
	 */
	public final T ensure(T element) {
		List4<T> list = find(element);
		if(list == null){
			add(element);
			return element;
		}
		return list._element;
	}

	/**
	 * Iterates through the collection in reversed insertion order which happens
	 * to be the fastest.
	 * 
	 * @return
	 */
	public final Iterator4<T> iterator() {
		return _first == null
			? Iterators.EMPTY_ITERATOR
			: new Collection4Iterator(this, _first);
	}
	
	public T get(int index) {
		if(index < 0) {
			throw new IllegalArgumentException();
		}
		List4<T> cur = _first;
		while(index > 0 && cur != null) {
			cur = cur._next;
			index--;
		}
		if(cur == null) {
			throw new IllegalArgumentException();
		}
		return cur._element;
	}
	
	/**
	 * Removes all the elements from this collection that are returned by
	 * iterable.
	 * 
	 * @param iterable
	 */
	public void removeAll(Iterable4<T> iterable) {
		removeAll(iterable.iterator());
	}

	/**
	 * Removes all the elements from this collection that are returned by
	 * iterator.
	 */
	public void removeAll(Iterator4<T> iterator) {
		while (iterator.moveNext()) {
			remove(iterator.current());
		}
	}

	/**
	 * removes an object from the Collection equals() comparison returns the
	 * removed object or null, if none found
	 */
	public boolean remove(T a_object) {
		List4<T> previous = null;
		List4<T> current = _first;
		while (current != null) {
			if (current.holds(a_object)) {
				_size--;
				adjustOnRemoval(previous, current);
				changed();
				return true;
			}
			previous = current;
			current = current._next;
		}
		return false;
	}
	
    public void replace(T oldObject, T newObject) {
        List4<T> list = find(oldObject);
        if(list != null){
            list._element = newObject;
        }
    }
    
    public void replaceByIdentity(T oldObject, T newObject) {
        List4<T> list = findByIdentity(oldObject);
        if(list != null){
            list._element = newObject;
        }
    }
    
	private void adjustOnRemoval(List4<T> previous, List4<T> removed) {
		if (removed == _first) {
			_first = removed._next;
		} else {
			previous._next = removed._next;
		}
		if (removed == _last) {
			_last = previous;
		}
	}

	public final int size() {
		return _size;
	}
	
	public int indexOf(T obj){
		int index = 0;
		List4<T> current = _first;
		while (current != null) {
			if (current.holds(obj)) {
				return index;
			}
			index++;
			current = current._next;
		}
		return -1;
	}
	
	public final boolean isEmpty() {
		return _size == 0;
	}

	/**
	 * This is a non reflection implementation for more speed. In contrast to
	 * the JDK behaviour, the passed array has to be initialized to the right
	 * length.
	 */
	public final T[] toArray(T[] array) {
		int j = 0;
		Iterator4<T> i = internalIterator();
		while (i.moveNext()) {
			array[j++] = i.current();
		}
		return array;
	}

	public final Object[] toArray() {
		int j = 0;
		Object[] array = new Object[size()];
		Iterator4<T> i = internalIterator();
		while (i.moveNext()) {
			array[j++] = i.current();
		}
		return array;
	}

	public String toString() {
		return Iterators.toString(internalIterator());
	}

	private void changed() {
		++_version;
	}

	int version() {
		return _version;
	}

	private void assertNotNull(Object element) {
		if (element == null) {
			throw new ArgumentNullException();
		}
	}
	
	/**
	 * Leaner iterator for faster iteration (but unprotected against
	 * concurrent modifications).
	 */
	private Iterator4<T> internalIterator() {
		return new Iterator4Impl(_first);
	}
	
}