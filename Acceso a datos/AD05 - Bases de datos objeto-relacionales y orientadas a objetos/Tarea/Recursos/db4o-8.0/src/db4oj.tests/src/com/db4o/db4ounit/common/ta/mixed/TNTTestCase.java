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
public class TNTTestCase extends ItemTestCaseBase {
	
	public static void main(String[] args) {
		new TNTTestCase().runAll();
	}
	
	protected Object createItem() throws Exception {
		return new TNTItem(42);
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		TNTItem item = (TNTItem) obj;
		Assert.isNull(item.ntItem);
	}
		
	protected void assertItemValue(Object obj) throws Exception {
		TNTItem item = (TNTItem) obj;
		NTItem ntItem = item.value();
		Assert.isNotNull(ntItem);
		Assert.isNotNull(ntItem.tItem);
		Assert.areEqual(0, ntItem.tItem.value);
		Assert.areEqual(42, ntItem.tItem.value());
	}

}
