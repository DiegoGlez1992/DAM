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
package com.db4o.db4ounit.jre12.collections.transparent.set;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableTreeSetTestCase extends ActivatableCollectionTestCaseBase<TreeSet<CollectionElement>> {
	
	private CollectionSpec<TreeSet<CollectionElement>> _spec = 
			new CollectionSpec<TreeSet<CollectionElement>>(
					TreeSet.class,
					CollectionFactories.activatableTreeSetFactory(),
					CollectionFactories.plainTreeSetFactory()
			);
	
	public TreeSet<CollectionElement> newActivatableCollection() {
		return _spec.newActivatableCollection();
	}
	
	private TreeSet<CollectionElement> newPlainSet(){
		return _spec.newPlainCollection();
	}
	
	public void testCreation() {
		new ActivatableTreeSet<Object>();
		new ActivatableTreeSet<String>(new Comparator() {
			public int compare(Object o1, Object o2) {
				return 0;
			}
		});
		new ActivatableTreeSet<String>(new TreeSet());
		new ActivatableTreeSet<String>(new ArrayList<String>());
	}
	
	public void testClone() throws Exception{
		ActivatableTreeSet cloned = (ActivatableTreeSet) singleCollection().clone();
		// assert that activator is null - should throw IllegalStateException if it isn't
		cloned.bind(new Activator() {
			public void activate(ActivationPurpose purpose) {
			}
		});
		IteratorAssert.sameContent(newPlainSet().iterator(), cloned.iterator());
	}
	
	public void testFirst(){
		Assert.areEqual(newPlainSet().first(), singleCollection().first());
	}
	
	public void testLast(){
		Assert.areEqual(newPlainSet().last(), singleCollection().last());
	}
	
	public void testSubSet(){
		CollectionElement firstElement = newPlainSet().first();
		CollectionElement lastElement = newPlainSet().last();
		SortedSet<CollectionElement> plainSubSet = newPlainSet().subSet(firstElement, lastElement);
		SortedSet<CollectionElement> treeSubSet = singleCollection().subSet(firstElement, lastElement);
		IteratorAssert.sameContent(plainSubSet.iterator(), treeSubSet.iterator());
	}
	
	public void testHeadSet(){
		CollectionElement lastElement = newPlainSet().last();
		SortedSet<CollectionElement> plainSubSet = newPlainSet().headSet(lastElement);
		SortedSet<CollectionElement> treeSubSet = singleCollection().headSet(lastElement);
		IteratorAssert.sameContent(plainSubSet.iterator(), treeSubSet.iterator());
	}
	
	public void testTailSet(){
		CollectionElement firstElement = newPlainSet().first();
		SortedSet<CollectionElement> plainSubSet = newPlainSet().tailSet(firstElement);
		SortedSet<CollectionElement> treeSubSet = singleCollection().tailSet(firstElement);
		IteratorAssert.sameContent(plainSubSet.iterator(), treeSubSet.iterator());
	}

	public void testToString(){
		Assert.areEqual(newPlainSet().toString(), singleCollection().toString());
	}

}
