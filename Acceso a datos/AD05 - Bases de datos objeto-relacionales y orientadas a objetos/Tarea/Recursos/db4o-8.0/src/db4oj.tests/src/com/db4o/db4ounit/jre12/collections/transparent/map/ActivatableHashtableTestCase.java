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
package com.db4o.db4ounit.jre12.collections.transparent.map;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;

import db4ounit.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableHashtableTestCase extends ActivatableMapTestCaseBase{
	
	public void testCreation() {
		new ActivatableHashtable<CollectionElement, CollectionElement>();
		new ActivatableHashtable<CollectionElement, CollectionElement>(42);
		new ActivatableHashtable<CollectionElement, CollectionElement>(42, (float)0.5);
		Hashtable<CollectionElement,CollectionElement> origMap = new Hashtable<CollectionElement,CollectionElement>();
		origMap.put(new Element("a"), new Element("b"));
		ActivatableHashtable<CollectionElement, CollectionElement> fromMap = 
			new ActivatableHashtable<CollectionElement, CollectionElement>(origMap);
		assertEqualContent(origMap, fromMap);
	}

	public void testClone() throws Exception{
		ActivatableHashtable cloned = (ActivatableHashtable) singleHashtable().clone();
		// assert that activator is null - should throw IllegalStateException if it isn't
		cloned.bind(new Activator() {
			public void activate(ActivationPurpose purpose) {
			}
		});
		assertEqualContent(newFilledMap(), cloned);
	}
	
	public void testContains() {
		Hashtable<CollectionElement, CollectionElement> actual = singleHashtable();
		for (CollectionElement expectedValue : newFilledMap().values()) {
			Assert.isTrue(actual.contains(expectedValue));
		}
	}
	
	public void testElements(){
		Collection actual = elementsToCollection(singleHashtable());
		Collection expected = elementsToCollection((Hashtable) newFilledMap());
		IteratorAssert.sameContent(expected, actual);
	}
	
	

	private Collection elementsToCollection(Hashtable hashtable) {
		Collection temp = new ArrayList();
		Enumeration elements = hashtable.elements();
		while(elements.hasMoreElements()){
			temp.add(elements.nextElement());
		}
		return temp;
	}

	@Override
	protected Map<CollectionElement, CollectionElement> createMap() {
		return new ActivatableHashtable<CollectionElement, CollectionElement>();
	}
	
	private Hashtable singleHashtable(){
		return (Hashtable) singleMap();
	}

	

}
