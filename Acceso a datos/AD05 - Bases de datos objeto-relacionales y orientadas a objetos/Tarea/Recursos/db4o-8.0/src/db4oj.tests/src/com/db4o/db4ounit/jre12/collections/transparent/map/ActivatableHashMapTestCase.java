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

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableHashMapTestCase extends ActivatableMapTestCaseBase{

	public static class Item<K,V> {
		public Map<K,V> _map;
	}
	
	public void testCreation() {
		new ActivatableHashMap<CollectionElement, CollectionElement>();
		new ActivatableHashMap<CollectionElement, CollectionElement>(42);
		new ActivatableHashMap<CollectionElement, CollectionElement>(42, (float)0.5);
		HashMap<CollectionElement,CollectionElement> origMap = new HashMap<CollectionElement,CollectionElement>();
		origMap.put(new Element("a"), new Element("b"));
		ActivatableHashMap<CollectionElement, CollectionElement> fromMap = 
			new ActivatableHashMap<CollectionElement, CollectionElement>(origMap);
		assertEqualContent(origMap, fromMap);
	}

	public void testClone() throws Exception{
		ActivatableHashMap cloned = (ActivatableHashMap) ((HashMap)singleMap()).clone();
		// assert that activator is null - should throw IllegalStateException if it isn't
		cloned.bind(new Activator() {
			public void activate(ActivationPurpose purpose) {
			}
		});
		assertEqualContent(newFilledMap(), cloned);
	}
	

	@Override
	protected Map<CollectionElement, CollectionElement> createMap() {
		return new ActivatableHashMap<CollectionElement, CollectionElement>();
	}
	
}
