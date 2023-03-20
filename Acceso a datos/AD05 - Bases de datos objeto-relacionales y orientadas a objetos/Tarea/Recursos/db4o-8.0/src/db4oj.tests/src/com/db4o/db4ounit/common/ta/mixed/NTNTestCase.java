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
package com.db4o.db4ounit.common.ta.mixed;

import com.db4o.db4ounit.common.ta.ItemTestCaseBase;
import com.db4o.db4ounit.common.ta.LinkedList;

import db4ounit.Assert;

/**
 * @exclude
 */
public class NTNTestCase extends ItemTestCaseBase {
	
	public static void main(String[] args) {
		new NTNTestCase().runAll();
	}
	
	protected Object createItem() throws Exception {
		return new NTNItem(42);
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		NTNItem item = (NTNItem) obj;
		Assert.isNotNull(item.tnItem);
		Assert.isNull(item.tnItem.list);
	}
		
	protected void assertItemValue(Object obj) throws Exception {
		NTNItem item = (NTNItem) obj;
		Assert.areEqual(LinkedList.newList(42), item.tnItem.value());
	}
	
	public void testDeactivateDepth() throws Exception {
		NTNItem item = (NTNItem) retrieveOnlyInstance();
		TNItem tnItem = item.tnItem;
		tnItem.value();
		Assert.isNotNull(tnItem.list);
		// item.tnItem.list
		db().deactivate(item, 2);
		// FIXME: failure 
		// Assert.isNull(tnItem.list);
		
		db().activate(item, 42);
		db().deactivate(item, 10);
		Assert.isNull(tnItem.list);
	}

}
