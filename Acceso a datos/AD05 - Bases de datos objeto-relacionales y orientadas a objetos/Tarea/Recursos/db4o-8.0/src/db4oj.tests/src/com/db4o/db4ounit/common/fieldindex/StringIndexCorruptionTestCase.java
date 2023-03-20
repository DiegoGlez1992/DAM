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
package com.db4o.db4ounit.common.fieldindex;

import com.db4o.config.*;
import com.db4o.ext.*;

import db4ounit.*;

/**
 * Jira ticket: COR-373
 * 
 * @exclude
 */
public class StringIndexCorruptionTestCase extends StringIndexTestCaseBase {
	
	public static void main(String[] arguments) {
		new StringIndexCorruptionTestCase().runSolo();
	}
	
	protected void configure(Configuration config) {
		super.configure(config);
		config.bTreeNodeSize(4);
	}
	
	public void testStressSet() {		
    	final ExtObjectContainer container = db();
    	
    	final int itemCount = 300;
		for (int i=0; i<itemCount; ++i) {
    		Item item = new Item(itemName(i));
    		container.store(item);
    		container.store(item);
    		container.commit();
    		container.store(item);
    		container.store(item);
    		container.commit();
    	}    	
    	for (int i=0; i<itemCount; ++i) {
    		String itemName = itemName(i);
    		final Item found = query(itemName);
    		Assert.isNotNull(found, "'" + itemName + "' not found");
			Assert.areEqual(itemName, found.name);
    	}
    }
	
	private String itemName(int i) {
		return "item " + i;
	}

}
