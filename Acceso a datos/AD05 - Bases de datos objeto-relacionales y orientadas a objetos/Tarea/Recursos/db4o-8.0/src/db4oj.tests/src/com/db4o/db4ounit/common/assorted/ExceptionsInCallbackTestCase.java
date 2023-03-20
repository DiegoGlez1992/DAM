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

import java.util.*;

import com.db4o.config.*;
import com.db4o.consistency.*;
import com.db4o.consistency.ConsistencyChecker.*;
import com.db4o.events.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

/**
 * @exclude
 */
public class ExceptionsInCallbackTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new ExceptionsInCallbackTestCase().runSolo();
	}
	
	public static class Holder {
		public List list;
		public int i;
	}
	
	public static class Item {
		
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.objectClass(Holder.class).cascadeOnUpdate(true);
		config.objectClass(Holder.class).cascadeOnDelete(true);
	}
	
	public void testExceptionInUpdateCallback() throws Exception{
		final BooleanByRef doThrow = new BooleanByRef();
		EventRegistryFactory.forObjectContainer(db()).updated().addListener(new EventListener4<ObjectInfoEventArgs>() {
			public void onEvent(Event4<ObjectInfoEventArgs> e, ObjectInfoEventArgs args) {
				if(doThrow.value){
					if(args.info().getObject().getClass().equals(Item.class)){
						throw new RuntimeException();
					}
				}
			}
		});
		Holder holder = new Holder();
		Item item = new Item();
		store(holder);
		store(item);
		commit();
		doThrow.value = true;
		holder.list = new ArrayList();
		holder.list.add(item);
		try {
			db().store(holder, Integer.MAX_VALUE);
		} catch (RuntimeException rex) {
			// rex.printStackTrace();
		}
		checkdb();
		commit();
		checkdb();
		reopen();
		checkdb();
	}

	private void checkdb() {
		ConsistencyReport consistencyReport = new ConsistencyChecker((LocalObjectContainer) container()).checkSlotConsistency();
		Assert.isTrue(consistencyReport.consistent(), consistencyReport.toString());
	}

}


