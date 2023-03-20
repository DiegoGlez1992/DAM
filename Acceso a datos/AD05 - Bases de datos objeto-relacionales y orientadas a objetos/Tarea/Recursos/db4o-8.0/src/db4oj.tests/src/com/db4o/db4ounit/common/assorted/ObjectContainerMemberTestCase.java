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
package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.events.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ObjectContainerMemberTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		
		public ObjectContainer _typedObjectContainer;
		
		public Object _untypedObjectContainer;

	}

	public void test() throws Exception{
		EventRegistry eventRegistryFactory = EventRegistryFactory.forObjectContainer(db());
		eventRegistryFactory.creating().addListener(new EventListener4<CancellableObjectEventArgs>(){
			public void onEvent(Event4<CancellableObjectEventArgs> e,
					CancellableObjectEventArgs args) {
				Object obj = args.object();
				Assert.isFalse(obj instanceof ObjectContainer);
			}
		});
		Item item = new Item();
		item._typedObjectContainer = db();
		item._untypedObjectContainer = db();
		store(item);

		// Special case: Cascades activation to existing ObjectContainer member
		db().queryByExample(Item.class).next();
		
	}

}
