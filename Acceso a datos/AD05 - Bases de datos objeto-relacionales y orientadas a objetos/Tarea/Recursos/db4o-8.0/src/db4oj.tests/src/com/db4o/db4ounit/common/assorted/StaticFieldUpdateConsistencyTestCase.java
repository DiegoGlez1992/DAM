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
package com.db4o.db4ounit.common.assorted;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.consistency.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class StaticFieldUpdateConsistencyTestCase extends AbstractDb4oTestCase {

	public static class SimpleEnum {
		public String _name;
		
		public SimpleEnum(String name) {
			_name = name;
		}
		
		public static SimpleEnum A = new SimpleEnum("A");
		public static SimpleEnum B = new SimpleEnum("B");
	}
	
	public static class Item {
		public SimpleEnum _value;
		
		public Item(SimpleEnum value) {
			_value = value;
		}
	}

	
	@Override
	protected void configure(Configuration config) throws Exception {
		config.updateDepth(5);
		config.objectClass(SimpleEnum.class).persistStaticFieldValues();
	}

	@Override
	protected void store() throws Exception {
		store(new Item(SimpleEnum.A));
	}
	
	public void test() throws Exception {
		updateAll();
		ConsistencyReport consistencyReport = new ConsistencyChecker(fileSession()).checkSlotConsistency();
		Assert.isTrue(consistencyReport.consistent(), consistencyReport.toString());
	}

	private void updateAll() {
		ObjectSet<Item> result = newQuery(Item.class).execute();
		while(result.hasNext()) {
			Item item = result.next();
			item._value = (item._value == SimpleEnum.A) ? SimpleEnum.B : SimpleEnum.A;
			store(item);
		}
		commit();
	}
	
}
