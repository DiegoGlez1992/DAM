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
package com.db4o.db4ounit.common.querying;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeOnDeleteTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		public String item;
	}
	
	public static class Holder {
		public Item[] items;
	}
	
	public void testNoAccidentalDeletes() throws Exception {
		assertNoAccidentalDeletes(true, true);
		assertNoAccidentalDeletes(true, false);
		assertNoAccidentalDeletes(false, true);
		assertNoAccidentalDeletes(false, false);
	}
	
	private void assertNoAccidentalDeletes(boolean cascadeOnUpdate, boolean cascadeOnDelete) throws Exception {
		deleteAll(Holder.class);
		deleteAll(Item.class);
		
		ObjectClass oc = fixture().config().objectClass(Holder.class);
		oc.cascadeOnDelete(cascadeOnDelete);
		oc.cascadeOnUpdate(cascadeOnUpdate);
		
		reopen();
		
		Item item = new Item();
		Holder holder = new Holder();
		holder.items = new Item[]{ item };
		db().store(holder);
		db().commit();
		
		holder.items[0].item = "abrakadabra";
		db().store(holder);
		if(! cascadeOnDelete && ! cascadeOnUpdate){
			// the only case, where we don't cascade
			db().store(holder.items[0]);
		}
		
		Assert.areEqual(1, countOccurences(Item.class));
		db().commit();
		Assert.areEqual(1, countOccurences(Item.class));
	}
}
