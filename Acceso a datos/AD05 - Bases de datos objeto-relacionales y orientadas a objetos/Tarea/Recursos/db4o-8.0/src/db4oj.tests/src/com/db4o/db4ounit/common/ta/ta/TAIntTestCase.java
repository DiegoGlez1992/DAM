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
package com.db4o.db4ounit.common.ta.ta;

import db4ounit.*;

/**
 * @exclude
 */
public class TAIntTestCase extends TAItemTestCaseBase {

	public static void main(String[] args) {
		new TAIntTestCase().runAll();
	}

	protected Object createItem() throws Exception {
		TAIntItem item = new TAIntItem();
		item.value = 42;
		item.i = new Integer(1);
		item.obj = new Integer(2);
		return item;
	}

	protected void assertItemValue(Object obj) throws Exception {
		TAIntItem item = (TAIntItem) obj;
		Assert.areEqual(42, item.value());
		Assert.areEqual(new Integer(1), item.integerValue());
		Assert.areEqual(new Integer(2), item.object());
	}

}
