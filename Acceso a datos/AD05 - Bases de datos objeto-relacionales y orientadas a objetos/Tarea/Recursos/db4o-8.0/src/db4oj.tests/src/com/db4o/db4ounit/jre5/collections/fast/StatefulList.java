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
package com.db4o.db4ounit.jre5.collections.fast;

import java.util.*;

/**
 */
@decaf.Ignore
public class StatefulList implements List {
	
	private transient List _delegate = new ArrayList();
	
	private boolean _dirty;
	
	public boolean isDirty(){
		return _dirty;
	}
	
	public void setDirty(boolean flag){
		_dirty = flag;
	}

	public boolean add(Object o) {
		setDirty(true);
		return _delegate.add(o);
	}

	public void add(int index, Object element) {
		setDirty(true);
		_delegate.add(index, element);
	}

	public boolean addAll(Collection c) {
		setDirty(true);
		return _delegate.addAll(c);
	}

	public boolean addAll(int index, Collection c) {
		setDirty(true);
		return _delegate.addAll(index, c);
	}

	public void clear() {
		setDirty(true);
		_delegate.clear();
	}

	public boolean contains(Object o) {
		return _delegate.contains(o);
	}

	public boolean containsAll(Collection c) {
		return _delegate.containsAll(c);
	}

	public Object get(int index) {
		return _delegate.get(index);
	}

	public int indexOf(Object o) {
		return _delegate.indexOf(o);
	}

	public boolean isEmpty() {
		return _delegate.isEmpty();
	}

	public Iterator iterator() {
		return _delegate.iterator();
	}

	public int lastIndexOf(Object o) {
		return _delegate.lastIndexOf(o);
	}

	public ListIterator listIterator() {
		return _delegate.listIterator();
	}

	public ListIterator listIterator(int index) {
		return _delegate.listIterator(index);
	}

	public boolean remove(Object o) {
		setDirty(true);
		return _delegate.remove(o);
	}

	public Object remove(int index) {
		setDirty(true);
		return _delegate.remove(index);
	}

	public boolean removeAll(Collection c) {
		setDirty(true);
		return _delegate.removeAll(c);
	}

	public boolean retainAll(Collection c) {
		setDirty(true);
		return _delegate.retainAll(c);
	}

	public Object set(int index, Object element) {
		setDirty(true);
		return _delegate.set(index, element);
	}

	public int size() {
		return _delegate.size();
	}

	public List subList(int fromIndex, int toIndex) {
		return _delegate.subList(fromIndex, toIndex);
	}

	public Object[] toArray() {
		return _delegate.toArray();
	}

	public Object[] toArray(Object[] a) {
		return _delegate.toArray(a);
	}

}
