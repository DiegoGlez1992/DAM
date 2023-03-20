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

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;

/**
 * @exclude
 */
public class NNTTestCase extends ItemTestCaseBase {
	
	public static void main(String[] args) {
		new NNTTestCase().runAll();
	}
	
	protected Object createItem() throws Exception {
		return new NNTItem(42);
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		NNTItem item = (NNTItem) obj;
		Assert.isNotNull(item.ntItem);
		Assert.isNotNull(item.ntItem.tItem);
		Assert.areEqual(0, item.ntItem.tItem.value);
	}
		
	protected void assertItemValue(Object obj) throws Exception {
		NNTItem item = (NNTItem) obj;
		Assert.areEqual(42, item.ntItem.tItem.value());
	}

	public void testDeactivateDepth() throws Exception {
		NNTItem item = (NNTItem) retrieveOnlyInstance();
		NTItem ntItem = item.ntItem;
		TItem tItem = ntItem.tItem;
		tItem.value();
		// item.ntItem.tItem.value
		Assert.isNotNull(ntItem.tItem);
		db().deactivate(item, 2);
		// FIXME: failure 
//		Assert.isNull(ntItem.tItem);
		
		db().activate(item, 42);
		db().deactivate(item, 3);
		Assert.isNull(ntItem.tItem);
	}
}
