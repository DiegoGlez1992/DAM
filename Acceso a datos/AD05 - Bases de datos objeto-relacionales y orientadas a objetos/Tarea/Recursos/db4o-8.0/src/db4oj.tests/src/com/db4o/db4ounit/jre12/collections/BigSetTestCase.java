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

import com.db4o.collections.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.collections.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

@decaf.Ignore(decaf.Platform.JDK11)
public class BigSetTestCase extends AbstractDb4oTestCase implements OptOutMultiSession{
	
	public static void main(String[] args) {
		new BigSetTestCase().runSolo("testBigSetAfterCommit");
	}
	
	private static final Item ITEM_ONE = new Item("one");
	
	private static final Item[] items = new Item[]{
		new Item("one"),
		new Item("two"),
		new Item("three"),
	};

	public static class Holder <E> {
		public Set<E> _set;
	}
	
	public static class Item {
		public String _name;
		
		public Item(String name){
			_name = name;
		}
		
		public boolean equals(Object obj) {
			if(! (obj instanceof Item)){
				return false;
			}
			Item other = (Item) obj;
			if(_name == null){
				return other._name == null;
			}
			return _name.equals(other._name);
		}
		
		@Override
		public String toString() {
		    return "Item(" + _name + ")";
		}
	}
	
	public void testRefreshBigSet() {
		final Holder<Item> holder = newHolderWithBigSet(new Item("1"), new Item("2"));
		storeAndCommit(holder);
		db().refresh(holder, Integer.MAX_VALUE);
		Assert.areEqual(2, holder._set.size());
	}
	
	public void testAddAfterCommit() {
		runTestAfterCommit(new Procedure4() {
			public void apply(Object set) {
				((Set<Item>)set).add(new Item("3"));
			}
		});
	}

	private void runTestAfterCommit(final Procedure4 setOperations) {
	    final Holder<Item> holder = newHolderWithBigSet(new Item("1"), new Item("2"));
		storeAndCommit(holder);
		
		final Set<Item> set = holder._set;
		Assert.areEqual(2, set.size());
		setOperations.apply(set);
		
		purgeAll(holder, holder._set);
		
		final Holder<Item> resurrected = (Holder<Item>)retrieveOnlyInstance(holder.getClass());
		IteratorAssert.sameContent(set.iterator(), resurrected._set.iterator());
    }
	
	public void testClearAfterCommit() {
		runTestAfterCommit(new Procedure4() {
			public void apply(Object set) {
				((Set<Item>)set).clear();
            }
		});
	}
	
	public void testRemoveAfterCommit() {
		runTestAfterCommit(new Procedure4() {
			public void apply(Object set) {
				((Set<Item>)set).remove(queryItem("1"));
            }
		});
	}

	protected Item queryItem(String name) {
		final Query query = newQuery(Item.class);
		query.descend("_name").constrain(name);
		return (Item) query.execute().get(0);
    }

	private void storeAndCommit(final Holder<Item> holder) {
	    store(holder);
		db().commit();
    }
	
	public void testPurgeBeforeCommit() {
		Holder<Item> holder = newHolderWithBigSet(new Item("foo"));
		store(holder);
		
		purgeAll(holder, holder._set);
		
		holder = (Holder<Item>)retrieveOnlyInstance(holder.getClass());
		Assert.areEqual(1, holder._set.size());
	}

	private Holder<Item> newHolderWithBigSet(final Item... item) {
	    Holder<Item> holder = new Holder<Item>();
		holder._set = newBigSet(item);
	    return holder;
    }
	
	private void purgeAll(Object... objects) {
		for (Object object : objects) {
			db().purge(object);
		}
	}

	public void testTypeHandlerInstalled(){
		TypeHandler4 typeHandler = container().handlers().configuredTypeHandler(reflector().forClass(newBigSet().getClass()));
		Assert.isInstanceOf(BigSetTypeHandler.class, typeHandler);
	}
	
	public void testEmptySet(){
		Set<Item> set = newBigSet();
		Assert.areEqual(0, set.size()); 
	}

	/**
	 * @sharpen.ignore
	 */
	public void testAdd(){
		Set<Item> set = newBigSet();
		Assert.isTrue(set.add(ITEM_ONE));
		Assert.isFalse(set.add(ITEM_ONE));
		Assert.areEqual(1, set.size());
	}
	
	public void testSize(){
		Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		Assert.areEqual(1, set.size());
		set.remove(ITEM_ONE);
		Assert.areEqual(0, set.size());
		Item itemTwo = new Item("two");
		set.add(itemTwo);
		set.add(new Item("three"));
		Assert.areEqual(2, set.size());
		set.remove(itemTwo);
		Assert.areEqual(1, set.size());
	}
	
	public void testContains(){
		Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		Assert.isTrue(set.contains(ITEM_ONE));
	}
	
	public void testPersistence() throws Exception{
		Holder<Item> holder = new Holder<Item>();
		holder._set = newBigSet();
		Set<Item> set = holder._set;
		set.add(ITEM_ONE);
		store(holder);
		reopen();
		holder = (Holder<Item>) retrieveOnlyInstance(holder.getClass());
		set = holder._set;
		assertSinglePersistentItem(set);
	}

	private void assertSinglePersistentItem(Set<Item> set) {
		Item expectedItem = (Item)retrieveOnlyInstance(Item.class);
		Assert.isNotNull(set);
		Assert.areEqual(1, set.size());
		Iterator setIterator = set.iterator();
		Assert.isNotNull(setIterator);
		Assert.isTrue(setIterator.hasNext());
		Item actualItem = (Item) setIterator.next();
		Assert.areSame(expectedItem, actualItem);
	}
	
	public void testAddAllContainsAll(){
		final Set<Item> set = newBigSet();
		final List<Item> collection = itemList();
		Assert.isTrue(set.addAll(collection));
		Assert.isTrue(set.containsAll(collection));
		
		Assert.isFalse(set.addAll(collection));
		Assert.areEqual(collection.size(), set.size());
	}
	
	public void testRemove(){
		Set<Item> set = newBigSet();
		List<Item> collection = itemList();
		set.addAll(collection);
		Item first = collection.get(0);
		set.remove(first);
		Assert.isTrue(collection.remove(first));
		Assert.isFalse(collection.remove(first));
		Assert.isTrue(set.containsAll(collection));
		Assert.isFalse(set.contains(first));
	}
	
	public void testRemoveAll(){
		Set<Item> set = newBigSet();
		List<Item> collection = itemList();
		set.addAll(collection);
		Assert.isTrue(set.removeAll(collection));
		Assert.areEqual(0, set.size());
		Assert.isFalse(set.removeAll(collection));
	}
	
	public void testIsEmpty(){
		Set<Item> set = newBigSet();
		Assert.isTrue(set.isEmpty());
		set.add(ITEM_ONE);
		Assert.isFalse(set.isEmpty());
		set.remove(ITEM_ONE);
		Assert.isTrue(set.isEmpty());
	}
	
	public void testIterator(){
		Set<Item> set = newBigSet();
		Collection<Item> collection = itemList();
		set.addAll(collection);
		
		Iterator i = set.iterator();
		Assert.isNotNull(i);
		IteratorAssert.sameContent(collection.iterator(), i);
	}
	
	public void testDelete() throws Throwable{
		final Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		db().store(set);
		db().commit();
		BTree bTree = bTree(set);
		BTreeAssert.assertAllSlotsFreed(fileTransaction(), bTree, new CodeBlock() {
			public void run() throws Throwable {
				db().delete(set);
				db().commit();
			}
		});
		Assert.expect(IllegalStateException.class, new CodeBlock() {
			public void run() throws Throwable {
				set.add(ITEM_ONE);
			}
		});
	}
	
	public void testDefragment() throws Exception{
		Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		db().store(set);
		db().commit();
		defragment();
		set = (Set<Item>) retrieveOnlyInstance(set.getClass());
		assertSinglePersistentItem(set);
	}
	
	public void testClear(){
		Set<Item> set = newBigSet();
		set.add(ITEM_ONE);
		set.clear();
		Assert.areEqual(0, set.size());
	}

	private List<Item> itemList() {
		List<Item> c = new ArrayList<Item>();
		for (int i = 0; i < items.length; i++) {
			c.add(items[i]);
		}
		return c;
	}
	
	public void testGetInternalImplementation() throws Exception{
		Set<Item> set = newBigSet();
		BTree bTree = bTree(set);
		Assert.isNotNull(bTree);
	}
	
	private Set<Item> newBigSet(Item... initialSet) {
		Set<Item> set = CollectionFactory.forObjectContainer(db()).<Item>newBigSet();
		set.addAll(Arrays.asList(initialSet));
		return set;
	}

	public static BTree bTree(Set<Item> set) throws IllegalAccessException{
		return (BTree)Reflection4.getFieldValue(set, "_bTree");
	}
	
	private LocalTransaction fileTransaction() {
		return ((LocalTransaction)trans());
	}
	
}
