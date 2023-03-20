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
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public abstract class StringIndexTestCaseBase extends AbstractDb4oTestCase {

	public static class Item {        
	    public String name;
	    
	    public Item() {            
	    }
	
	    public Item(String name_) {
	        name = name_;
	    }
	}

	public StringIndexTestCaseBase() {
		super();
	}

	protected void configure(Configuration config) {
	    indexField(config, Item.class, "name");
	}

	protected void assertItems(final String[] expected, final ObjectSet result) {
		final ExpectingVisitor expectingVisitor = new ExpectingVisitor(toObjectArray(expected));
		while (result.hasNext()) {
			expectingVisitor.visit(((Item)result.next()).name);
		}
		expectingVisitor.assertExpectations();
	}

	protected Object[] toObjectArray(String[] source) {
		Object[] array = new Object[source.length];
		System.arraycopy(source, 0, array, 0, source.length);
		return array;
	}

	protected void grafittiFreeSpace() {
		if (!(db() instanceof IoAdaptedObjectContainer)) {
			return;
		}
		final IoAdaptedObjectContainer file = ((IoAdaptedObjectContainer)db());
		final FreespaceManager fm = file.freespaceManager();
		fm.traverse(new Visitor4() {
			public void visit(Object obj) {
				Slot slot = (Slot) obj;
				file.overwriteDeletedBlockedSlot(slot);
			}
		});
	}

	protected void assertExists(String itemName) {
		assertExists(trans(), itemName);
	}

	protected void add(final String itemName) {
		add(trans(), itemName);
	}

	protected void add(Transaction transaction, String itemName) {
		container().store(transaction, new Item(itemName));
	}

	protected void assertExists(Transaction transaction, String itemName) {
		Assert.isNotNull(query(transaction, itemName));
	}

	protected void rename(Transaction transaction, String from, String to) {
		final Item item = query(transaction, from);
		Assert.isNotNull(item);
		item.name = to;
		container().store(transaction, item);
	}

	protected void rename(String from, String to) {
		rename(trans(), from, to);
	}

	protected Item query(String name) {
		return query(trans(), name);
	}

	protected Item query(Transaction transaction, String name) {
		ObjectSet objectSet = newQuery(transaction, name).execute();
	    if (!objectSet.hasNext()) {
	    	return null;
	    }
	    return (Item) objectSet.next();
	}

	protected Query newQuery(Transaction transaction, String itemName) {
		final Query query = container().query(transaction);
		query.constrain(Item.class);
		query.descend("name").constrain(itemName);
		return query;
	}

}