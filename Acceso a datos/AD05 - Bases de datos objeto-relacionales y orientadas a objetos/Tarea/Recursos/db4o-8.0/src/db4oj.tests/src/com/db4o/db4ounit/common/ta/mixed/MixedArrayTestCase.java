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

public class MixedArrayTestCase extends ItemTestCaseBase {

	public static void main(String[] args) {
		new MixedArrayTestCase().runAll();
	}
	
	protected Object createItem() throws Exception {
		return new MixedArrayItem(depth());
	}

	/**
	 * @sharpen.if !CF
	 */
	private int depth() {
		return 42;
	}
	
	/**
	 * @sharpen.if CF
	 * @sharpen.rename Depth
	 */
	@SuppressWarnings("unused")
	private int depthCF() {
		return 10;
	}

	protected void assertItemValue(Object obj) throws Exception {
		MixedArrayItem item = (MixedArrayItem) obj;
		Object[] objects = item.objects;
		Assert.areEqual(depth(), ((TItem)objects[1]).value());
		Assert.areEqual(depth(), ((TItem)objects[3]).value());		
	}

	protected void assertRetrievedItem(Object obj) throws Exception {
		MixedArrayItem item = (MixedArrayItem) obj;
		Object[] objects = item.objects;
		Assert.isNotNull(objects);
		for (int i = 0; i < objects.length; ++i) {
			Assert.isNotNull(objects[i]);
		}
		Assert.areEqual(LinkedList.newList(depth()), objects[0]);
		Assert.areEqual(0, ((TItem)objects[1]).value);
		Assert.areEqual(LinkedList.newList(depth()), objects[2]);
		Assert.areEqual(0, ((TItem)objects[3]).value);
	}
	
}
