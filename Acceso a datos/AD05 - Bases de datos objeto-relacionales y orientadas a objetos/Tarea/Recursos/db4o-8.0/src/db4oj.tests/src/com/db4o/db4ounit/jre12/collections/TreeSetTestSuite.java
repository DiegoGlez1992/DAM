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
package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;
import com.db4o.typehandlers.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * @exclude
 */
@decaf.Remove(decaf.Platform.JDK11)
public class TreeSetTestSuite extends FixtureTestSuiteDescription implements Db4oTestCase {
	
	static final Comparator<String> comparator = new Comparator<String>() {
		public int compare(String x, String y) {
			return y.compareTo(x);
        }
	};
	
	{
		fixtureProviders(
			new Db4oFixtureProvider(),
			new SubjectFixtureProvider(
				new Deferred4<TreeSet>() { public TreeSet value() {
					
					return new TreeSet();
					
				}},
				new Deferred4<TreeSet>() { public TreeSet value() {
					
					return new TreeSet(comparator);
					
				}}
			)
		);
		
		testUnits(TestUnit.class);
	}
	
	public static class TestUnit extends AbstractDb4oTestCase {

		private static final String[] ELEMENTS = new String[] { "one", "two" };

		public static class Item {

			public Item(TreeSet treeSet) {
				this.treeSet = treeSet;
			}

			public TreeSet treeSet;

		}

		@Override
		protected void store() {
			final TreeSet treeSet = subject();
			treeSet.addAll(Arrays.asList(ELEMENTS));
			
			store(new Item(treeSet));
		}

		private TreeSet subject() {
	        return SubjectFixtureProvider.value();
        }
		
		public void testIdentity() throws Exception {
			final TreeSet existingTreeSet = retrieveOnlyInstance(Item.class).treeSet;
			store(new Item(existingTreeSet));
			reopen();
			
			final ObjectSet<Item> items = db().query(Item.class);
			Assert.areEqual(2, items.size());
			assertTreeSetContent(items.get(0).treeSet);
			Assert.areSame(items.get(0).treeSet, items.get(1).treeSet);
		}

		public void testRoundtrip() {
			final Item item = retrieveOnlyInstance(Item.class);
			assertTreeSetContent(item.treeSet);
		}
		
		public void testTypeHandler() {
			Assert.isInstanceOf(
					TreeSetTypeHandler.class,
					configuredTypeHandlerFor(TreeSet.class));
		}

		private TypeHandler4 configuredTypeHandlerFor(final Class<?> clazz) {
	        return container().handlers().configuredTypeHandler(reflectClass(clazz));
        }

		private void assertTreeSetContent(final TreeSet treeSet) {
	        final Comparator comparator = subject().comparator();
			final Object[] expected = comparator == null
				? ELEMENTS
				: sortedBy(ELEMENTS, comparator);
			IteratorAssert.areEqual(expected, treeSet.iterator());
        }
		
		public void testTransparentDescendOnElement() {
			
			final Item item = retrieveOnlyInstance(Item.class);
			store(new Item(null));

			for (String element : ELEMENTS) {
				final ObjectSet<Item> found = itemByTreeSetElement(element);
				if (!found.hasNext()) {
					Assert.fail("Expecting " + item);
				}
				Assert.areSame(item, found.next());
			}
			
			final Item copy = new Item(new TreeSet(item.treeSet));
			store(copy);
			
			for (String element : ELEMENTS) {
				final ObjectSet<Item> found = itemByTreeSetElement(element);
				ObjectSetAssert.sameContent(found, item, copy);
			}
		}
		
		static class NamedItem implements Comparable<NamedItem>{

			public String name;

			public NamedItem(String name) {
				this.name = name;
			}

			public int compareTo(NamedItem o) {
				return name.compareTo(o.name);
			}
			
		}
		
		public void testTransparentDescendOnElementMember() {
			
			deleteAll(Item.class);
			
			final Item item1 = new Item(new TreeSet(Arrays.asList(
										new NamedItem("foo"))));
			final Item item2 = new Item(new TreeSet(Arrays.asList(
										new NamedItem("bar"))));
			
			final Item[] items = { item1, item2 };
			for (Item item : items)
				store(item);
			
			for (Item item : items) {
				final NamedItem firstNamedItem = (NamedItem)item.treeSet.first();
				final ObjectSet<Item> found = itemByNamedItem(firstNamedItem.name);
				ObjectSetAssert.sameContent(found, item);
			}
		}

		private ObjectSet<Item> itemByNamedItem(final String namedItemName) {
			final Query query = newQuery(Item.class);
			query.descend("treeSet").descend("name").constrain(namedItemName);
			return query.<Item>execute();
		}
		
		private ObjectSet<Item> itemByTreeSetElement(String element) {
	        final Query query = newQuery(Item.class);
	        query.descend("treeSet").constrain(element);
	        return query.<Item>execute();
        }

		private String[] sortedBy(String[] elements, Comparator comparator) {
			final String[] copy = new String[elements.length];
			System.arraycopy(elements, 0, copy, 0, elements.length);
			Arrays.sort(copy, comparator);
			return copy;
        }

	}
}
