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
import com.db4o.ext.*;
import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.fixtures.*;

public class ClassConfigOverridesGlobalConfigTestSuite extends FixtureTestSuiteDescription {

	private static final FixtureVariable<ConfigScope> GLOBAL_CONFIG = FixtureVariable.newInstance("global");
	private static final FixtureVariable<TernaryBool> CLASS_CONFIG = FixtureVariable.newInstance("class");

	{
		testUnits(ClassConfigOverridesGlobalConfigTestUnit.class);
		fixtureProviders(
				new SimpleFixtureProvider(GLOBAL_CONFIG, ConfigScope.GLOBALLY, ConfigScope.INDIVIDUALLY, ConfigScope.DISABLED),
				new SimpleFixtureProvider(CLASS_CONFIG, TernaryBool.YES, TernaryBool.NO, TernaryBool.UNSPECIFIED)
		);
	}
	
	public static class ClassConfigOverridesGlobalConfigTestUnit extends AbstractDb4oTestCase {
	
		public static class Item {
		}
	
		@Override
		protected void configure(Configuration config) {
			config.generateUUIDs(GLOBAL_CONFIG.value());
			if(!CLASS_CONFIG.value().isUnspecified()) {
				config.objectClass(Item.class).generateUUIDs(CLASS_CONFIG.value().booleanValue(true));
			}
		}
	
		@Override
		protected void store() throws Exception {
			store(new Item());
		}
		
		public void testNoUUIDIsGenerated() {
			Item item = retrieveOnlyInstance(Item.class);
			ObjectInfo objectInfo = db().ext().getObjectInfo(item);
			if(!CLASS_CONFIG.value().isUnspecified()) {
				assertGeneration(objectInfo, CLASS_CONFIG.value().booleanValue(true) && GLOBAL_CONFIG.value() != ConfigScope.DISABLED);
			}
			else {
				assertGeneration(objectInfo, GLOBAL_CONFIG.value() == ConfigScope.GLOBALLY);
			}
		}
		
		private void assertGeneration(ObjectInfo objectInfo, boolean expectGeneration) {
			if(expectGeneration) {
				Assert.isNotNull(objectInfo.getUUID());
			}
			else {
				Assert.isNull(objectInfo.getUUID());
				Assert.areEqual(0L, objectInfo.getCommitTimestamp());
			}
		}
	}

}
