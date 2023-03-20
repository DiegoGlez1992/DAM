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
public class TAStringTestCase extends TAItemTestCaseBase {

	public static void main(String[] args) {
		new TAStringTestCase().runAll();
	}

	protected Object createItem() throws Exception {
		TAStringItem item = new TAStringItem();
		item.value = "42";
		item.obj = "hello";
		return item;
	}

	protected void assertItemValue(Object obj) throws Exception {
		TAStringItem item = (TAStringItem) obj;
		Assert.areEqual("42", item.value());
		Assert.areEqual("hello", item.object());
	}

	protected void assertRetrievedItem(Object obj) {
		TAStringItem item = (TAStringItem) obj;
		Assert.isNull(item.value);
		Assert.isNull(item.obj);
	}

}
