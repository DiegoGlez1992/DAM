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
package com.db4o.db4ounit.jre12.tp;

import java.util.*;

import com.db4o.activation.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Remove(decaf.Platform.JDK11)
public class CollectionUpdateTPTestCase extends AbstractDb4oTestCase {

	private final static int ID1 = 1;
	private final static int ID2 = 2;
	
	public static class Item implements Activatable {
		
		public transient Activator _activator;
		public int _id;
		public Child _child;
		
		public Item(int id) {
			this(id, null);
		}

		public Item(int id, Child child) {
			_id = id;
			_child = child;
		}

		public void id(int id) {
			_activator.activate(ActivationPurpose.WRITE);
			_id = id;
		}
		
		public int id() {
			_activator.activate(ActivationPurpose.READ);
			return _id;
		}
		
		public Child child() {
			_activator.activate(ActivationPurpose.READ);
			return _child;
		}
		
		public void activate(ActivationPurpose purpose) {
			_activator.activate(purpose);
		}

		public void bind(Activator activator) {
			_activator = activator;
		}
		
		@Override
		public String toString() {
			_activator.activate(ActivationPurpose.READ);
			return "Item #" + _id;
		}
	}
	
	public static class Child {
		public int _id;

		public Child(int id) {
			_id = id;
		}
		
		public void id(int id) {
			_id = id;
		}
		
		@Override
		public String toString() {
			return "Child #" + _id;
		}
	}
	
	public static class Holder {
		
		public List<Item> _list;
		
		public Holder(Item... items) {
			_list = new ArrayList<Item>();
			if (items != null) {
				_list.addAll(Arrays.asList(items));
			}
		}
		
		public Item item(int idx) {
			return _list.get(idx);
		}
		
		public void add(Item item) {
			_list.add(item);
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
	}

	@Override
	protected void store() throws Exception {
		Holder holder = new Holder(new Item(1), new Item(2, new Child(7)));
		store(holder);
	}
	
	public void testStructureUpdate() throws Exception {
		assertUpdates(0, 0, new Procedure4<Holder>() {
			public void apply(Holder holder) {
				Item item = new Item(3);
				store(item);
				holder.add(item);
				db().store(holder, Integer.MAX_VALUE);
			}
		});
		reopen();
		assertHolderContent(ID1, ID2, 3);
	}

	public void testElementUpdate() throws Exception {
		assertUpdates(1, 0, new Procedure4<Holder>() {
			public void apply(Holder holder) {
				holder.item(0).id(42);
				db().store(holder, Integer.MAX_VALUE);
			}
		});
		reopen();
		assertHolderContent(42, ID2);
	}

	public void testElementUpdateAndActivation() throws Exception {
		assertUpdates(1, 1, new Procedure4<Holder>() {
			public void apply(Holder holder) {
				holder.item(0).id(42);
				holder.item(1).id();
				db().store(holder, Integer.MAX_VALUE);
			}
		});
		reopen();
		assertHolderContent(42, ID2);
	}

	public void testChildUpdate() throws Exception {
		assertUpdates(0, 1, new Procedure4<Holder>() {
			public void apply(Holder holder) {
				holder.item(1).child().id(100);
				db().store(holder, Integer.MAX_VALUE);
			}
		});
		reopen();
		assertHolderContent(ID1, ID2);
	}

	private void assertUpdates(int expectedItemCount, int expectedChildCount, Procedure4<Holder> block) {
		final IntByRef itemCount = new IntByRef(0);
		final IntByRef childCount = new IntByRef(0);
		eventRegistry().updated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				if(args.object() instanceof Item) {
					itemCount.value = itemCount.value + 1;
				}
				if(args.object() instanceof Child) {
					childCount.value = childCount.value + 1;
				}
			}
		});
		Holder holder = retrieveOnlyInstance(Holder.class);
		block.apply(holder);
		commit();
		Assert.areEqual(expectedItemCount, itemCount.value);
		Assert.areEqual(expectedChildCount, childCount.value);
	}
	
	private void assertHolderContent(int... ids) {
		Holder holder = retrieveOnlyInstance(Holder.class);
		for (int idx = 0; idx < ids.length; idx++) {
			Assert.areEqual(ids[idx], holder.item(idx).id());
		}
	}

}
