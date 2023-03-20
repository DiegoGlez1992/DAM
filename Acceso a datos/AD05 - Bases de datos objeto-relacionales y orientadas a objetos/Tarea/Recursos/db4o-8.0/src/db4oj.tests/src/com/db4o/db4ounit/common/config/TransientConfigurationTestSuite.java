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
package com.db4o.db4ounit.common.config;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.fixtures.*;

public class TransientConfigurationTestSuite extends FixtureTestSuiteDescription {
	private static final FixtureVariable<Boolean> CLASS_CONFIG = FixtureVariable.newInstance("Class");

	{
		testUnits(TransientConfigurationTestUnit.class);
		fixtureProviders(new SimpleFixtureProvider(CLASS_CONFIG, new Object[] { true, false }));
	}
	
	public static class TransientConfigurationTestUnit extends AbstractDb4oTestCase {
		private static final int TRANSIENT_VALUE = 42;
		private static final int PERSISTENT_VALUE = 0xdb40;

		@Override
		protected void configure(Configuration config) throws Exception {
			config.objectClass(Item.class).storeTransientFields(CLASS_CONFIG.value());
		}
		
		@Override
		protected void store() throws Exception {
			store(new Item(TRANSIENT_VALUE, PERSISTENT_VALUE));
		}
		
		public void testRetrieval() {
			Item instance = retrieveOnlyInstance(Item.class);
			Assert.areEqual(PERSISTENT_VALUE, instance._persistentField);
			
			Assert.areEqual(expectedTransientValue(), instance._transientField);
		}

		private int expectedTransientValue() {
			return CLASS_CONFIG.value() ? TRANSIENT_VALUE : 0;
		}
	}
	
	public static class Item {
		public transient int _transientField;
		public int _persistentField;
		
		public Item(int transientValue, int persistentValue) {
			_transientField = transientValue;
			_persistentField = persistentValue;
		}
	}
}
