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
package com.db4o.db4ounit.common.reflect;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class NoTestConstructorsTestCase extends AbstractDb4oTestCase {

	public static class Item {
		public static int constructorCalls = 0;
		
		public Item() {
			constructorCalls++;
		}
	}

	protected void db4oSetupBeforeStore() {
		Item.constructorCalls = 0;
	}
	
	protected void configure(Configuration config) {
		config.callConstructors(true);
		config.testConstructors(false);
	}
	
	protected void store() {
		store(new Item());
		Assert.areEqual(1, Item.constructorCalls);
	}

	public void test() {
		retrieveOnlyInstance(Item.class);
		Assert.areEqual(2, Item.constructorCalls);
	}
	
	public static void main(String[] args) {
		new NoTestConstructorsTestCase().runAll();
	}
}
