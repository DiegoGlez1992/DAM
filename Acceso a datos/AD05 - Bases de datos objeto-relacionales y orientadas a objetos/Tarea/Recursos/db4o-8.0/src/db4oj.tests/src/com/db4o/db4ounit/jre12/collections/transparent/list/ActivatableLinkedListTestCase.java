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
package com.db4o.db4ounit.jre12.collections.transparent.list;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableLinkedListTestCase extends ActivatableCollectionTestCaseBase<LinkedList<CollectionElement>> {
	
	private CollectionSpec<LinkedList<CollectionElement>> _spec = 
			new CollectionSpec<LinkedList<CollectionElement>>(
					LinkedList.class,
					CollectionFactories.activatableLinkedListFactory(),
					CollectionFactories.plainLinkedListFactory()
			);
	
	public LinkedList<CollectionElement> newActivatableCollection() {
		return _spec.newActivatableCollection();
	}
	
	private LinkedList<CollectionElement> newPlainList(){
		return _spec.newPlainCollection();
	}
	
	public void testCreation() {
		new ActivatableLinkedList<Object>();
		new ActivatableLinkedList<String>((Collection<String>)new ActivatableLinkedList<String>());
	}
	
	public void testClone() throws Exception{
		ActivatableLinkedList cloned = (ActivatableLinkedList) singleLinkedList().clone();
		// assert that activator is null - should throw IllegalStateException if it isn't
		cloned.bind(new Activator() {
			public void activate(ActivationPurpose purpose) {
			}
		});
		IteratorAssert.areEqual(newPlainList().iterator(), cloned.iterator());
	}

	public void testToString(){
		Assert.areEqual(newPlainList().toString(), singleCollection().toString());
	}
	
	public void testAddFirst() throws Exception{
		Element element = new Element("first");
		singleLinkedList().addFirst(element);
		reopen();
		Assert.isTrue(singleLinkedList().contains(element));
	}
	
	public void testAddLast() throws Exception{
		Element element = new Element("last");
		singleLinkedList().addLast(element);
		reopen();
		Assert.isTrue(singleLinkedList().contains(element));
	}
	
	private LinkedList singleLinkedList(){
		return (ActivatableLinkedList) singleCollection();
	}

}
