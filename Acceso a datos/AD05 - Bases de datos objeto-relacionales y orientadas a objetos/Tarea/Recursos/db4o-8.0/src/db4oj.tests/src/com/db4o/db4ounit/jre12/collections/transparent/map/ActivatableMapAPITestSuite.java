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

import com.db4o.collections.*;
import com.db4o.db4ounit.jre12.collections.transparent.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableMapAPITestSuite extends FixtureBasedTestSuite implements Db4oTestCase {
	
	private static FixtureVariable<MapSpec<ActivatableMap<CollectionElement, CollectionElement>>> MAP_SPEC =
		new FixtureVariable<MapSpec<ActivatableMap<CollectionElement, CollectionElement>>>("map");

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
		new Db4oFixtureProvider(),
		new SimpleFixtureProvider(MAP_SPEC,
				new MapSpec<ActivatableMap<CollectionElement, CollectionElement>>(activatableHashMapFactory()),
				new MapSpec<ActivatableMap<CollectionElement, CollectionElement>>(activatableHashtableFactory()))
				
		};
	}

	private Closure4<ActivatableMap<CollectionElement, CollectionElement>> activatableHashMapFactory() {
		return new Closure4<ActivatableMap<CollectionElement,CollectionElement>>() {
			public ActivatableMap<CollectionElement, CollectionElement> run() {
				return new ActivatableHashMap<CollectionElement, CollectionElement>();
			}
		};
	}
	
	private Closure4<ActivatableMap<CollectionElement, CollectionElement>> activatableHashtableFactory() {
		return new Closure4<ActivatableMap<CollectionElement,CollectionElement>>() {
			public ActivatableMap<CollectionElement, CollectionElement> run() {
				return new ActivatableHashtable<CollectionElement, CollectionElement>();
			}
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[]{ActivatableMapAPITestUnit.class};
	}
	
	/**
	 * @sharpen.remove
	 */
	@decaf.Remove(decaf.Platform.JDK11)
	public static class ActivatableMapAPITestUnit extends ActivatableMapTestCaseBase{
		
		public void testCorrectContent() {
			assertEqualContent(newFilledMap(), singleMap());
		}

		public void testMapIsNotActivated(){
			Assert.isFalse(db().isActive(singleMap()));
		}
		
		public void testActivatableElementsAreNotActivated() {
			long[] ids = ActivatableCollectionTestUtil.allActivatableElementIds(db());
			for (long id : ids) {
				Assert.isFalse(db().isActive(db().getByID(id)));
			}
		}

		public void testClear() throws Exception {
			singleMap().clear();
			reopen();
			assertEmpty();
		}

		public void testContainsKey() {
			Map<CollectionElement, CollectionElement> actual = singleMap();
			for (CollectionElement expectedKey : newFilledMap().keySet()) {
				Assert.isTrue(actual.containsKey(expectedKey));
			}
		}
		
		public void testContainsValue() {
			Map<CollectionElement, CollectionElement> actual = singleMap();
			for (CollectionElement expectedValue : newFilledMap().values()) {
				Assert.isTrue(actual.containsValue(expectedValue));
			}
		}
		
		public void testEntrySet() {
			IteratorAssert.sameContent(newFilledMap().entrySet().iterator(), singleMap().entrySet().iterator());
		}
		
		public void testGet() {
			assertEqualContent(newFilledMap(), singleMap());
		}
		
		public void testIsEmpty() {
			Assert.isFalse(singleMap().isEmpty());
		}
		
		public void testKeySet() {
			IteratorAssert.sameContent(newFilledMap().keySet().iterator(), singleMap().keySet().iterator());
		}
		
		public void testPut() throws Exception {
			Map<CollectionElement, CollectionElement> map = singleMap();
			Element value = new Element("added value");
			Element key = new Element("added key");
			map.put(key, value);
			reopen();
			Assert.areEqual(value, singleMap().get(key));
		}
		
		public void testPutAll() throws Exception {
			Map<CollectionElement, CollectionElement> map = singleMap();
			Map<CollectionElement, CollectionElement> added = new HashMap<CollectionElement, CollectionElement>();
			added.put(new Element("added key 1"), new Element("added value 1"));
			added.put(new Element("added key 2"), new Element("added value 2"));
			map.putAll(added);
			reopen();
			Map<CollectionElement, CollectionElement> expected = newFilledMap();
			expected.putAll(added);
			assertEqualContent(expected, singleMap());
		}
		
		public void testRemove() throws Exception {
			Map<CollectionElement, CollectionElement> map = singleMap();
			for (CollectionElement key : newFilledMap().keySet()) {
				map.remove(key);
			}
			reopen();
			assertEmpty();
		}
		
		public void testSize() {
			Assert.areEqual(newFilledMap().size(), singleMap().size());
		}
		
		public void testValues() {
			IteratorAssert.sameContent(newFilledMap().values().iterator(), singleMap().values().iterator());
		}
		
		public void testRemoveFromValues() throws Exception{
			Collection<CollectionElement> expectedValues = newFilledMap().values();
			Object removedElement = removeFirstFrom(expectedValues);
			singleMap().values().remove(removedElement);
			reopen();
			Collection<CollectionElement> actualValues = singleMap().values();
			IteratorAssert.sameContent(expectedValues.iterator(), actualValues.iterator());
		}

		private Object removeFirstFrom(
				Collection<CollectionElement> collection) {
			CollectionElement removed = collection.iterator().next();
			collection.remove(removed);
			return removed;
		}
		
		public void testEquals() {
			Map<CollectionElement, CollectionElement> expected = newFilledMap();
			Map<CollectionElement, CollectionElement> map = singleMap();
			Assert.isTrue(map.equals(expected));
		}
		
		public void testHashCode() {
			Map<CollectionElement, CollectionElement> expected = newFilledMap();
			Map<CollectionElement, CollectionElement> map = singleMap();
			Assert.areEqual(expected.hashCode(), map.hashCode());
		}

		public void testKeySetIteratorRemove() throws Exception {
			for (Iterator iter = singleMap().keySet().iterator(); iter.hasNext();) {
				iter.next();
				iter.remove();
			}
			reopen();
			Assert.isTrue(singleMap().isEmpty());
		}
		
		public void testRepeatedPut() throws Exception {
			Element key1 = new Element("added key 1");
			Element key2 = new Element("added key 2");
			singleMap().put(key1, new Element("added value 1"));
			db().purge();
			singleMap().put(key2, new Element("added value 2"));
			reopen();
			Map<CollectionElement,CollectionElement> retrieved = singleMap();
			Assert.isTrue(retrieved.containsKey(key1));
			Assert.isTrue(retrieved.containsKey(key2));
		}
		
		private MapSpec<ActivatableMap<CollectionElement, CollectionElement>> currentMapSpec() {
			return MAP_SPEC.value();
		}

		@Override
		protected Map<CollectionElement, CollectionElement> createMap() {
			return currentMapSpec().newActivatableMap();
		}
	
	}

}
