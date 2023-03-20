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

import com.db4o.db4ounit.common.ta.*;

import db4ounit.*;

/**
 * @exclude
 */
public class TANArrayTestCase extends TAItemTestCaseBase {
	
	private static final int[][] INTS1 = new int[][]{ {1,2,3}, {4,5,6}};
	
	private static final int[][] INTS2 = new int[][] { {4,5,6}, {7,8,9}};
	
	private static final LinkedList[][] LIST1 = new LinkedList[][] {{ LinkedList.newList(5)}, {LinkedList.newList(5)}};
	
	private static final LinkedList[][] LIST2 = new LinkedList[][] {{ LinkedList.newList(5)}, {LinkedList.newList(5)}};

	public static void main(String[] args) {
		new TANArrayTestCase().runAll();
	}

	protected Object createItem() throws Exception {
		TANArrayItem item = new TANArrayItem();
		item.value = INTS1;
		item.obj = INTS2;
		
		item.lists = LIST1;
		item.listsObject = LIST2;
		return item;
	}

	protected void assertItemValue(Object obj) throws Exception {
		TANArrayItem item = (TANArrayItem) obj;
		JaggedArrayAssert.areEqual(INTS1, item.value());
		JaggedArrayAssert.areEqual(INTS2, (int[][])item.object());
		JaggedArrayAssert.areEqual(LIST1, item.lists());
		JaggedArrayAssert.areEqual(LIST2, (LinkedList[][]) item.listsObject());
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		TANArrayItem item = (TANArrayItem) obj;
		Assert.isNull(item.value);
		Assert.isNull(item.obj);
		Assert.isNull(item.lists);
		Assert.isNull(item.listsObject);
	}

}
