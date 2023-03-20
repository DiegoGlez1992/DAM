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
public class ActivatableHashSetTestCase extends ActivatableCollectionTestCaseBase<HashSet<CollectionElement>> {
	
	private CollectionSpec<HashSet<CollectionElement>> _spec = 
			new CollectionSpec<HashSet<CollectionElement>>(
					HashSet.class,
					CollectionFactories.activatableHashSetFactory(),
					CollectionFactories.plainHashSetFactory()
			);
	
	public HashSet<CollectionElement> newActivatableCollection() {
		return _spec.newActivatableCollection();
	}
	
	private HashSet<CollectionElement> newPlainSet(){
		return _spec.newPlainCollection();
	}
	
	public void testCreation() {
		new ActivatableHashSet<Object>();
		new ActivatableHashSet<String>(42);
		new ActivatableHashSet<String>(42, 0.001f);
		new ActivatableHashSet<String>(new ArrayList<String>());
	}
	
	public void testClone() throws Exception{
		ActivatableHashSet cloned = (ActivatableHashSet) singleCollection().clone();
		// assert that activator is null - should throw IllegalStateException if it isn't
		cloned.bind(new Activator() {
			public void activate(ActivationPurpose purpose) {
			}
		});
		IteratorAssert.sameContent(newPlainSet().iterator(), cloned.iterator());
	}

	public void testToString(){
		Assert.areEqual(newPlainSet().toString(), singleCollection().toString());
	}

}
