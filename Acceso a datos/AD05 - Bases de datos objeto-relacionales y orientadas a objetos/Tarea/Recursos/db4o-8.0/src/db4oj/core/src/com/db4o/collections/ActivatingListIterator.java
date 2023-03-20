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
 * @exclude
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatingListIterator<E> extends ActivatingIterator<E> implements ListIterator<E> {

	public ActivatingListIterator(Activatable activatable, Iterator<E> iterator) {
		super(activatable, iterator);
	}

	public void add(E o) {
		activate(ActivationPurpose.WRITE);
		listIterator().add(o);
	}

	public boolean hasPrevious() {
		activate(ActivationPurpose.READ);
		return listIterator().hasPrevious();
	}

	public int nextIndex() {
		activate(ActivationPurpose.READ);
		return listIterator().nextIndex();
	}

	public E previous() {
		activate(ActivationPurpose.READ);
		return listIterator().previous();
	}

	public int previousIndex() {
		activate(ActivationPurpose.READ);
		return listIterator().previousIndex();
	}

	public void set(E o) {
		activate(ActivationPurpose.WRITE);
		listIterator().set(o);
	}
	
	private ListIterator<E> listIterator() {
		return (ListIterator<E>) _iterator;
	}
}
