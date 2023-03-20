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
package com.db4o.db4ounit.common.internal.metadata;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.marshall.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ClassMetadataCollectIdsTestCase extends AbstractDb4oTestCase implements OptOutMultiSession {
	
	public static class Item {
		
		public String name;
		
		public Item typedRef;
		public Object untypedRef;
		public Object untypedArray;
		
		public Item(String name, Item ref1, Item ref2, Object... untypedArray) {
	        this.name = name;
	        this.typedRef = ref1;
	        this.untypedRef = ref2;
	        this.untypedArray = untypedArray;
        }

		public Object untypedElementAt(int index) {
			return ((Object[])untypedArray)[index];
        }
	}
	
	@Override
	protected void store() throws Exception {
		store(
			new Item("root",
				new Item("typed", null, null),
				new Item("untyped", null, null),
				new Item("array", null, null)));
	}
	
	public void testCollectIdsByFieldName() {
		
		Item root = queryRootItem();
		
		final CollectIdContext context = CollectIdContext.forID(trans(), (int) db().getID(root));
		context.classMetadata().collectIDs(context, "typedRef");
		
		assertCollectedIds(context, root.typedRef);
				
	}
	
	public void testCollectIds() {
		
		Item root = queryRootItem();
		
		final CollectIdContext context = CollectIdContext.forID(trans(), (int) db().getID(root));
		context.classMetadata().collectIDs(context);
		
		assertCollectedIds(context, root.typedRef, root.untypedRef, root.untypedElementAt(0));
				
	}

	private void assertCollectedIds(final CollectIdContext context, final Object... expectedReferences) {
	    Iterator4Assert.sameContent(
	    		Iterators.map(expectedReferences, new Function4() {
	    			public Object apply(Object reference) {
	    				return (int)db().getID(reference);
                    }}),
				new TreeKeyIterator(context.ids()));
    }

	private Item queryRootItem() {
		return queryItemByName("root").next();
    }

	private ObjectSet<Item> queryItemByName(final String itemName) {
	    final Query query = newQuery(Item.class);
		query.descend("name").constrain(itemName);
		return query.<Item>execute();
    }
}
