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
package com.db4o.db4ounit.jre11.events;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.btree.*;
import com.db4o.internal.classindex.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class SelectiveCascadingDeleteTestCase extends AbstractDb4oTestCase {
	
	protected void configure(Configuration config) {
		enableCascadeOnDelete(config);
	}
	
	protected void store() {
		Item c = new Item("C", null);
		Item b = new Item("B", c);
		Item a = new Item("A", b);
		db().store(a);
	}
	
	public void testPreventMiddleObjectDeletion() throws Exception {
		
		Assert.areEqual(3, queryItems().size());
		
		serverEventRegistry().deleting().addListener(new EventListener4() {
			public void onEvent(Event4 e, EventArgs args) {
				CancellableObjectEventArgs a = (CancellableObjectEventArgs)args;

                // TODO: Not really nice. Internal abstract class Transaction 
				//       is exposed. We should have an outside Transaction interface. 
				Transaction trans = (Transaction) a.transaction();
				
				ObjectContainer container = trans.objectContainer();
				
				Item item = ((Item)a.object());
				container.activate(item, 1);
                if (item.id.equals("B")) {
					// cancel deletion of this item
					a.cancel();
					
					// restart from the child
					container.delete(item.child);
				}
			}
		});

        Item a = queryItem("A");
		Assert.isNotNull(a);		

		assertIndexCount(3);

		db().delete(a);
		
		db().commit();

		reopen();
		
        a = queryItem("A");
		Assert.isNull(a);		
		assertIndexCount(1);
		
		ObjectSet found = queryItems();
		Assert.areEqual(1, found.size());
		final Item remainingItem = ((Item)found.next());
		Assert.areEqual("B", remainingItem.id);
		Assert.isNull(remainingItem.child);
	}

	private ObjectSet queryItems() {
		return createItemQuery().execute();
	}

	private Query createItemQuery() {
		Query q = db().query();
		q.constrain(Item.class);
		return q;
	}

	private void enableCascadeOnDelete(Configuration config) {
		config.objectClass(Item.class).cascadeOnDelete(true);
	}

	private Item queryItem(final String id) {
		Query q = createItemQuery();
		q.descend("id").constrain(id);
		ObjectSet result = q.execute();
		return result.hasNext() ? (Item)result.next() : null;
	}
	
	private void assertIndexCount(int expectedCount) {
		final int[] sum={0};
		Visitor4 visitor = new Visitor4() {
			public void visit(Object obj) {
				sum[0]++;
			}
		};
		btree().traverseKeys(fileSession().systemTransaction(),visitor);
		Assert.areEqual(expectedCount,sum[0]);
	}

	private BTree btree() {
		return BTreeClassIndexStrategy.btree(classMetadata());
	}

	private ClassMetadata classMetadata() {
		return fileSession().classMetadataForReflectClass(reflectClass(Item.class));
	}

	public static void main(String[] args) {
		new SelectiveCascadingDeleteTestCase().runNetworking();
	}
}
