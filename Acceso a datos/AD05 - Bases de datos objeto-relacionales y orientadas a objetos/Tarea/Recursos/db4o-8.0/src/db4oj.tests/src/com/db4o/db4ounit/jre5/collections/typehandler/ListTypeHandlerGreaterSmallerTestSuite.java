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
package com.db4o.db4ounit.jre5.collections.typehandler;

import com.db4o.query.*;

import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 */
@decaf.Ignore
@SuppressWarnings("unchecked")
public class ListTypeHandlerGreaterSmallerTestSuite extends FixtureBasedTestSuite implements Db4oTestCase {
	
	public FixtureProvider[] fixtureProviders() {
		ListTypeHandlerTestElementsSpec[] elementSpecs = {
				ListTypeHandlerTestVariables.STRING_ELEMENTS_SPEC,
				ListTypeHandlerTestVariables.INT_ELEMENTS_SPEC,
		};
		return new FixtureProvider[] {
			new Db4oFixtureProvider(),
			ListTypeHandlerTestVariables.LIST_FIXTURE_PROVIDER,
			new SimpleFixtureProvider(
				ListTypeHandlerTestVariables.ELEMENTS_SPEC,
				(Object[])elementSpecs
			),
			ListTypeHandlerTestVariables.TYPEHANDLER_FIXTURE_PROVIDER,
		};
	}

	public Class[] testUnits() { 
		return new Class[] {
			ListTypeHandlerGreaterSmallerTestUnit.class,
		};
	}

	public static class ListTypeHandlerGreaterSmallerTestUnit extends ListTypeHandlerTestUnitBase {
		
		public void testSuccessfulSmallerQuery() throws Exception {
	    	Query q = newQuery(itemFactory().itemClass());
	    	q.descend(AbstractItemFactory.LIST_FIELD_NAME).constrain(largeElement()).smaller();
	    	assertQueryResult(q, true);
		}
		
		public void testFailingGreaterQuery() throws Exception {
	    	Query q = newQuery(itemFactory().itemClass());
	    	q.descend(AbstractItemFactory.LIST_FIELD_NAME).constrain(largeElement()).greater();
	    	assertQueryResult(q, false);
		}

	}

}
