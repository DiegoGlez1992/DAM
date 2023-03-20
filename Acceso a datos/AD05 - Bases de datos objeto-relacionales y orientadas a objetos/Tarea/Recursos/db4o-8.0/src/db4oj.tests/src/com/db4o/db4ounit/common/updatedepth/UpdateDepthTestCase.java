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
package com.db4o.db4ounit.common.updatedepth;

import java.util.*;

import com.db4o.config.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

@decaf.Remove
public class UpdateDepthTestCase extends FixtureTestSuiteDescription implements Db4oTestCase {
	
	{
		fixtureProviders(
			new SubjectFixtureProvider(
					
				// (expectedDepth, storeDepth, globalConfigDepth, itemConfigDepth)
				new UpdateDepths(0, 0, Const4.UNSPECIFIED, 3),
				new UpdateDepths(1, 1, Const4.UNSPECIFIED, 3),
				new UpdateDepths(2, 2, Const4.UNSPECIFIED, 3),
				new UpdateDepths(3, 3, Const4.UNSPECIFIED, 3),
				
				new UpdateDepths(0, Const4.UNSPECIFIED, Const4.UNSPECIFIED, 0),
				
				new UpdateDepths(1, Const4.UNSPECIFIED, Const4.UNSPECIFIED, Const4.UNSPECIFIED),
				new UpdateDepths(2, Const4.UNSPECIFIED, Const4.UNSPECIFIED, 2),
				
				new UpdateDepths(1, Const4.UNSPECIFIED, 1, Const4.UNSPECIFIED)
				),
			new Db4oFixtureProvider());
		
		testUnits(TestUnit.class);
	}
	
	static class UpdateDepths implements Labeled {

		private final int expected;
		private final int store;
		private final int globalConfig;
		private final int itemConfig;

		public UpdateDepths(int expectedDepth, int storeDepth, int globalConfigurationDepth, int itemConfigurationDepth) {
			this.expected = expectedDepth;
			this.store = storeDepth;
			this.globalConfig = globalConfigurationDepth;
			this.itemConfig = itemConfigurationDepth;
		}

		public String label() {
			return val("eD", expected) + "," + val("sD", store) + "," + val("gC", globalConfig) + "," + val("iC", itemConfig);
		}

		private String val(String label, int value) {
			return label + ":" + (value == Const4.UNSPECIFIED ? "?" : String.valueOf(value));
		}
	}
	
	public static final class TestUnit extends AbstractDb4oTestCase {
	
		protected void store() throws Exception {
			store(new RootItem(newGraph()));
		}
		
		protected void configure(Configuration config) throws Exception {
			
			if (globalUpdateDepth() != Const4.UNSPECIFIED) {
				config.updateDepth(globalUpdateDepth());
			}
			
			final ObjectClass itemClass = config.objectClass(Item.class);
			if (itemUpdateDepth() != Const4.UNSPECIFIED) {
				itemClass.updateDepth(itemUpdateDepth());
			}
			
			itemClass.minimumActivationDepth(3);
	//		itemClass.cascadeOnDelete(true);
		}

		
		public void test() throws Exception {
			switch (expectedUpdateDepth()) {
			case 0:
				expectDepth0();
				break;
			case 1:
				expectDepth1();
				break;
			case 2:
				expectDepth2();
				break;
			case 3:
				expectDepth3();
				break;
			default:
				throw new IllegalStateException("Invalid expected update depth!");
			}
			
		}
		
		private int itemUpdateDepth() {
			return updateDepths().itemConfig;
		}

		private UpdateDepths updateDepths() {
			return SubjectFixtureProvider.<UpdateDepths>value();
		}

		private int globalUpdateDepth() {
			return updateDepths().globalConfig;
		}
		
		private int expectedUpdateDepth() {
			return updateDepths().expected;
		}
		
		private int storeUpdateDepth() {
			return updateDepths().store;
		}
		
		private void expectDepth0() throws Exception {
	
			final Item item = pokeName(queryRoot());
			storeItem(item);
			expect(newGraph());
		}

		private void storeItem(final Item item) {
			db().store(item, storeUpdateDepth());
		}

		private void expectDepth1() throws Exception {
			
			final Item item = pokeChild(pokeName(queryRoot()));
			storeItem(item);
			expect(pokeName(newGraph()));
		}
		
		private void expectDepth2() throws Exception {
			
			final Item root = pokeChild(pokeName(queryRoot()));
			pokeChild(root.child); // one level too many
			
			storeItem(root);
			
			expect(pokeChild(pokeName(newGraph())));
		}
		
		private void expectDepth3() throws Exception {
			final Item item = pokeChild(pokeName(queryRoot()));
			pokeChild(item.child);
			
			storeItem(item);
			
			expect(item);
		}
		
		private Item newGraph() {
			return new Item("Level 1",
				new Item("Level 2",
					new Item("Level 3"),
					new Item[] { new Item("Array Level 3") }),
				new Item[] { new Item("Array Level 2") });
		}
	
		private Item pokeChild(final Item item) {
			pokeName(item.child);
			if (item.childArray != null) {
				pokeName(item.childArray[0]);
				pokeName((Item) item.childVector.elementAt(0));
			}
			return item;
		}
		
		private Item pokeName(Item item) {
			item.name = item.name + "*";
			return item;
		}
	
		private void expect(Item expected) {
			try {
				reopen();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			assertEquals(expected, queryRoot());
		}
	
		private void assertEquals(Item expected, Item actual) {
			if (expected == null) {
				Assert.isNull(actual);
				return;
			}
			Assert.isNotNull(actual);
			Assert.areEqual(expected.name, actual.name);
			assertEquals(expected.child, actual.child);
			assertEquals(expected.childArray, actual.childArray);
			assertCollection(expected.childVector, actual.childVector);
		}
	
		private void assertCollection(Vector expected, Vector actual) {
			if (expected == null) {
				Assert.isNull(actual);
				return;
			}
			Assert.isNotNull(actual);
			Assert.areEqual(expected.size(), actual.size());
			for (int i=0; i<expected.size(); ++i) {
				assertEquals((Item)expected.elementAt(i), (Item)actual.elementAt(i));
			}
		}
	
		private void assertEquals(Item[] expected, Item[] actual) {
			if (expected == null) {
				Assert.isNull(actual);
				return;
			}
			Assert.isNotNull(actual);
			Assert.areEqual(expected.length, actual.length);
			for (int i=0; i<expected.length; ++i) {
				assertEquals(expected[i], actual[i]);
			}
		}
		
		private Item queryRoot() {
			return ((RootItem)newQuery(RootItem.class).execute().next()).root;
		}
	}
	

	public static final class Item {
		
		public String name;
		public Item child;
		public Item[] childArray;
		public Vector childVector;
		
		public Item() {
		}
		
		public Item(String name) {
			this.name = name;
		}
		
		public Item(String name, Item child) {
			this(name);
			this.child = child;
		}
		
		public Item(String name, Item child, Item[] childArray) {
			this(name, child);
			this.childArray = childArray;
			this.childVector = new Vector();
			for (int i=0; i<childArray.length; ++i) {
				childVector.addElement(childArray[i]);
			}
		}
	}
	
	public static final class RootItem {
		public Item root;
		
		public RootItem() {
		}
		
		public RootItem(Item root) {
			this.root = root;
		}
	}
		
}
