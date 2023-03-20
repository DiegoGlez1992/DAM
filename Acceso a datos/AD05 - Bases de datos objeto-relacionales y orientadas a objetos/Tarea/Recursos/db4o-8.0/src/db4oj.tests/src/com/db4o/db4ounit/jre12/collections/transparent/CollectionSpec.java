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
package com.db4o.db4ounit.jre12.collections.transparent;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.fixtures.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class CollectionSpec<L extends Collection<CollectionElement>> implements Labeled {

	private static String[] NAMES = new String[] {"one", "two", "three"};
	
	private final Closure4<L> _activatableCollectionFactory;
	private final Closure4<L> _plainCollectionFactory;
	private final Class<? super L> _collectionClazz;
	
	public CollectionSpec(
			Class<? super L> collectionClazz, 
			Closure4<L> activatableCollectionFactory,
			Closure4<L> plainCollectionFactory) {
		_activatableCollectionFactory = activatableCollectionFactory;
		_plainCollectionFactory = plainCollectionFactory;
		_collectionClazz = collectionClazz;
	}

	public L newActivatableCollection() {
		L collection = createActivatableCollection();
		for (CollectionElement element: newPlainCollection()) {
			collection.add(element);
		}
		return collection;
	}
	
	public L newPlainCollection(){
		L elements = _plainCollectionFactory.run();
		for (String name  : NAMES) {
			elements.add(new Element(name));
		}
		for (String name  : NAMES) {
			elements.add(new ActivatableElement(name));
		}
		return elements;
	}
	
	public static String firstName() {
		return NAMES[0];
	}
	
	private L createActivatableCollection() {
		return _activatableCollectionFactory.run();
	}

	public String label() {
		return ReflectPlatform.simpleName(_collectionClazz);
	}
}
