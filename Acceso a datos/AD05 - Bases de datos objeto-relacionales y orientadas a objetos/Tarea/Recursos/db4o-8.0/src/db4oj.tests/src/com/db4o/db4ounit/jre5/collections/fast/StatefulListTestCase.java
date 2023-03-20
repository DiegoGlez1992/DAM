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
package com.db4o.db4ounit.jre5.collections.fast;

import java.util.*;

import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


/**
 */
@decaf.Ignore
public class StatefulListTestCase extends AbstractDb4oTestCase implements OptOutMultiSession{
	
	public static final Object[] DATA = new Object[]{
		new Integer(0),
		"one",
		new Double(2),
	};
	
	protected void configure(Configuration config) throws Exception {
		config.registerTypeHandler(new SingleClassTypeHandlerPredicate(StatefulList.class), new StatefulListTypeHandler());
	}
	
	protected void store() throws Exception {
		List list = createList();
		for (int i = 0; i < DATA.length; i++) {
			list.add(DATA[i]);
		}
		store(list);
	}
	
	private List createList(){
		return new StatefulList();
	}
	
	public void testListContent(){
		List template = createList();
		List list = (List) retrieveOnlyInstance(template.getClass());
		Object[] listContent = toArray(list);
		ArrayAssert.areEqual(DATA, listContent);
	}
	
	private Object[] toArray(List list) {
		Object[] res = new Object[list.size()];
		int index = 0;
		for(Object obj: list){
			res[index++] = obj;
		}
		return res;
	}

	public void testNoNewSlotRequiredIfNotChanged(){
		List template = createList();
		List list = (List) retrieveOnlyInstance(template.getClass());
		int slotAddress = slotAddress(list);
		db().store(list);
		Assert.areEqual(slotAddress, slotAddress(list));
	}

	private int slotAddress(List list) {
		int id = (int) db().getID(list);
		LocalTransaction localTransaction = (LocalTransaction) trans();
		Slot slot = localTransaction.idSystem().currentSlot(id);
		return slot.address();
	}
	
	public void testSlotAddres(){
		List template = createList();
		List list = (List) retrieveOnlyInstance(template.getClass());
		list.add("newString");
		int slotAddress = slotAddress(list);
		db().store(list);
		Assert.areNotEqual(slotAddress, slotAddress(list));
	}

	
	

}
