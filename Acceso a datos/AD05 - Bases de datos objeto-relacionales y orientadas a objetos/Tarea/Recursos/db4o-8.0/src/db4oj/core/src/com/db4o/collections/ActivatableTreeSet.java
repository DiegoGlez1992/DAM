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
public class ActivatableTreeSet<E> extends TreeSet<E> implements ActivatableSet<E> {

	private transient Activator _activator;
	
	public ActivatableTreeSet(){
		super();
	}
	
    public ActivatableTreeSet(Comparator<? super E> comparator) {
    	super(comparator);
    }

	public ActivatableTreeSet(Collection<? extends E> c) {
		super(c);
	}
	
	public ActivatableTreeSet(SortedSet<E> s) {
		super(s);
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
	public Object clone() {
		activate(ActivationPurpose.READ);
		ActivatableTreeSet<E> cloned = (ActivatableTreeSet<E>) super.clone();
		cloned._activator = null;
		return cloned;
	}
	
	@Override
	public boolean contains(Object o) {
		activate(ActivationPurpose.READ);
		return super.contains(o);
	}
	
	@Override
	public E first() {
		activate(ActivationPurpose.READ);
		return super.first();
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
	public E last() {
		activate(ActivationPurpose.READ);
		return super.last();
	}
	
	@Override
	public boolean remove(Object o) {
		activate(ActivationPurpose.WRITE);
		return super.remove(o);
	}
	
	@Override
	public int size() {
		activate(ActivationPurpose.READ);
		return super.size();
	}
	
	public java.util.SortedSet<E> subSet(E fromElement, E toElement) {
		activate(ActivationPurpose.READ);
		return super.subSet(fromElement, toElement);
	};
	
	public java.util.SortedSet<E> headSet(E toElement) {
		activate(ActivationPurpose.READ);
		return super.headSet(toElement);
	};
	
	public java.util.SortedSet<E> tailSet(E fromElement) {
		activate(ActivationPurpose.READ);
		return super.tailSet(fromElement);
	};

}

