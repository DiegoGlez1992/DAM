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

import com.db4o.collections.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.activation.*;
import com.db4o.ta.*;

import db4ounit.*;
import db4ounit.extensions.*;

// COR-1909
/**
 * @sharpen.ignore
 */
@decaf.Remove(decaf.Platform.JDK11)
public class ActivatableListUpdateTestCase extends AbstractDb4oTestCase {
	
	
	public static void main(String[] args) {
		new ActivatableListUpdateTestCase().runSolo();
	}

	public static class Item extends ActivatableBase {

		public int _id;
		
		public Item(int id) {
			_id = id;
		}

		public int id() {
			activateForRead();
			return _id;
		}

		public void id(int id) {
			activateForWrite();
			_id = id;
		}
	}

	public static class Holder extends ActivatableBase {

		public List<Item> _list;
		
		public Holder(Item... items) {
			if (items != null) {
				_list = new ActivatableArrayList<Item>(items.length);
				for (Item item : items) {
					_list.add(item);
				}
			}
			else {
				_list = new ActivatableArrayList<ActivatableListUpdateTestCase.Item>();
			}
		}
		
		public void add(Item item) {
			activateForRead();
			_list.add(item);
		}
		
		public Item item(int idx) {
			activateForRead();
			return _list.get(idx);
		}
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.add(new TransparentPersistenceSupport());
	}

	@Override
	protected void store() throws Exception {
		store(new Holder(new Item(42)));
	}

	public void testItemUpdatesOnStructureChange() {
		final IntByRef itemUpdateCount = new IntByRef(0);
		eventRegistry().updated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				if(args.object() instanceof Item) {
					itemUpdateCount.value++;
				}
			}
		});
		Holder holder = retrieveOnlyInstance(Holder.class);
		Assert.areEqual(42, holder.item(0).id());
		holder.add(new Item(43));
		commit();
		Assert.areEqual(0, itemUpdateCount.value);
	}
}
