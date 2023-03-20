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

import com.db4o.db4ounit.jre12.collections.transparent.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * @sharpen.remove
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableListAPITestSuite extends FixtureBasedTestSuite implements Db4oTestCase {

	private static FixtureVariable<CollectionSpec<List<CollectionElement>>> LIST_SPEC =
		new FixtureVariable<CollectionSpec<List<CollectionElement>>>("list");

	@Override
	public FixtureProvider[] fixtureProviders() {
		return new FixtureProvider[] {
				new Db4oFixtureProvider(),
				new SimpleFixtureProvider(LIST_SPEC,
						new CollectionSpec<ArrayList<CollectionElement>>(
								ArrayList.class, 
								CollectionFactories.activatableArrayListFactory(),
								CollectionFactories.plainArrayListFactory()),
						new CollectionSpec<LinkedList<CollectionElement>>(
								LinkedList.class, 
								CollectionFactories.activatableLinkedListFactory(),
								CollectionFactories.plainLinkedListFactory()),
						new CollectionSpec<Stack<CollectionElement>>(
								Stack.class, 
								CollectionFactories.activatableStackFactory(),
								CollectionFactories.plainStackFactory())
				),
		};
	}

	@Override
	public Class[] testUnits() {
		return new Class[] {
				ActivatableListAPITestUnit.class
		};
	}
	
	public static class ActivatableListAPITestUnit extends ActivatableCollectionAPITestUnit<List<CollectionElement>> {
		
		public void testActivatableElementsAreNotActivated(){
			// trigger activation of Collection and
			// all elements that are not Activatable
			singleCollection().iterator();
			
			long id = ActivatableCollectionTestUtil.anyActivatableElementId(db());
			Object element = db().getByID(id);
			Assert.isFalse(db().isActive(element));
		}

		public void testAddAtIndex() throws Exception{
			singleCollection().add(0, new Element("four"));
			reopen();
			List<CollectionElement> elements = newPlainCollection();
			elements.add(0, new Element("four"));
			IteratorAssert.areEqual(elements.iterator(), singleCollection().iterator());		
		}
		
		public void testAddAllAtIndex() throws Exception{
			singleCollection().addAll(2,newPlainCollection());
			reopen();
			List<CollectionElement> elements = newPlainCollection();
			elements.addAll(2, newPlainCollection());
			IteratorAssert.areEqual(elements.iterator(), singleCollection().iterator());		
		}
		
		public void testGet() {
			Assert.areEqual(newPlainCollection().get(0), singleCollection().get(0));
		}
		
		public void testIndexOf(){
			Assert.areEqual(1, singleCollection().indexOf(new Element("two")));
		}
		
		public void testLastIndexOf() throws Exception{
			List<CollectionElement> list = singleCollection();
			list.add(2, new Element("one"));
			reopen();
			List<CollectionElement> retrieved = singleCollection();
			Assert.areEqual(2, retrieved.lastIndexOf(new Element("one")));
		}
		
		public void testListIterator(){
			IteratorAssert.areEqual(newPlainCollection().listIterator(), singleCollection().listIterator());
		}
		
		public void testListIteratorAtIndex(){
			IteratorAssert.areEqual(newPlainCollection().listIterator(1), singleCollection().listIterator(1));
		}
		
		public void testRemoveAtIndex() throws Exception{
			singleCollection().remove(0);
			reopen();
			List<CollectionElement> list = newPlainCollection();
			list.remove(0);
			IteratorAssert.areEqual(list.iterator(), singleCollection().iterator());
		}
		
		public void testSet(){
			List<CollectionElement> singleList = singleCollection();
			Element element = new Element("four");
			singleList.set(1, element);
			List<CollectionElement> elements = newPlainCollection();
			elements.set(1, element);
			assertAreEqual(elements, singleList);
		}
		
		public void testSubList(){
			IteratorAssert.areEqual(newPlainCollection().subList(0,1).iterator(), singleCollection().subList(0, 1).iterator());
		}
		
		public void testModifiedSubList() throws Exception{
			singleCollection().subList(0, 1).clear();
			reopen();
			List<CollectionElement> expectedCollection = newPlainCollection();
			expectedCollection.subList(0,1).clear();
			IteratorAssert.areEqual(expectedCollection.iterator(), singleCollection().iterator());
		}
		
		public void testListIteratorAdd() throws Exception {
			Element added = new Element("added");
			List<CollectionElement> list = singleCollection();
			for (ListIterator iter = list.listIterator(); iter.hasNext();) {
				iter.next();
				if(!iter.hasNext()) {
					iter.add(added);
				}
			}
			reopen();
			List<CollectionElement> retrieved = singleCollection();
			Assert.areEqual(newPlainCollection().size() + 1, retrieved.size());
			Assert.isTrue(retrieved.contains(added));
			Assert.areEqual(added, retrieved.get(retrieved.size() - 1));
		}

		public void testListIteratorSet() throws Exception {
			Element replaced = new Element("replaced");
			List<CollectionElement> list = singleCollection();
			for (ListIterator iter = list.listIterator(); iter.hasNext();) {
				iter.next();
				if(iter.previousIndex() == 0) {
					iter.set(replaced);
				}
			}
			reopen();
			List<CollectionElement> retrieved = singleCollection();
			Assert.areEqual(newPlainCollection().size(), retrieved.size());
			Assert.isTrue(retrieved.contains(replaced));
			Assert.isFalse(retrieved.contains(new Element(CollectionSpec.firstName())));
			Assert.areEqual(replaced, retrieved.get(0));
		}

		@Override
		protected CollectionSpec<List<CollectionElement>> currentCollectionSpec() {
			return LIST_SPEC.value();
		}
	}
}
