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
package com.db4o.db4ounit.common.ta.nonta;

import com.db4o.db4ounit.common.ta.LinkedList;

import db4ounit.Assert;

/**
 * @exclude
 */
public class NonTALinkedListTestCase extends NonTAItemTestCaseBase {
    
	private static final LinkedList LIST = LinkedList.newList(10);

	public static void main(String[] args) {
		new NonTALinkedListTestCase().runAll();
	}

    protected void assertItemValue(Object obj) {
        Assert.areEqual(LIST, ((LinkedListItem)obj).list);
    }

    protected Object createItem() {
        LinkedListItem item = new LinkedListItem();
        item.list = LIST;
        return item;
    }
    
    public void testDeactivateDepth() throws Exception {
    	final LinkedListItem item = queryItem();
    	final LinkedList level1 = item.list;
    	final LinkedList level2 = level1.nextN(1);
    	final LinkedList level3 = level1.nextN(2);
    	final LinkedList level4 = level1.nextN(3);
    	final LinkedList level5 = level1.nextN(4);
    	
    	Assert.isNotNull(level1.next);
    	Assert.isNotNull(level2.next);
    	Assert.isNotNull(level3.next);
    	Assert.isNotNull(level4.next);
    	Assert.isNotNull(level5.next);
    	
    	db().deactivate(level1, 4);
    	
    	assertDeactivated(level1);
    	assertDeactivated(level2);
    	assertDeactivated(level3);
    	assertDeactivated(level4);
    	Assert.isNotNull(level5.next);
    }

	private void assertDeactivated(final LinkedList list) {
		Assert.isNull(list.next);
    	Assert.areEqual(0, list.value);
	}

	private LinkedListItem queryItem() {
		return (LinkedListItem) retrieveOnlyInstance();
	}

}
