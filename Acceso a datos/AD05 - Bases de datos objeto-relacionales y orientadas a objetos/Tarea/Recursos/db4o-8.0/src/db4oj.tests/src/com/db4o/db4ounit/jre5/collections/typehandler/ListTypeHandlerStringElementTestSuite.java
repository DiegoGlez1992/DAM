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
public class ListTypeHandlerStringElementTestSuite extends FixtureBasedTestSuite implements Db4oTestCase {
	
	
	public FixtureProvider[] fixtureProviders() {
		ListTypeHandlerTestElementsSpec[] elementSpecs = {
				ListTypeHandlerTestVariables.STRING_ELEMENTS_SPEC,
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
			ListTypeHandlerStringElementTestUnit.class,
		};
	}

	public static class ListTypeHandlerStringElementTestUnit extends ListTypeHandlerTestUnitBase {
		
		public void testSuccessfulEndsWithQuery() throws Exception {
	    	Query q = newQuery(itemFactory().itemClass());
	    	q.descend(AbstractItemFactory.LIST_FIELD_NAME).constrain(successfulEndChar()).endsWith(false);
	    	assertQueryResult(q, true);
		}
		
		public void testFailingEndsWithQuery() throws Exception {
	    	Query q = newQuery(itemFactory().itemClass());
	    	q.descend(AbstractItemFactory.LIST_FIELD_NAME).constrain(failingEndChar()).endsWith(false);
	    	assertQueryResult(q, false);
		}

		private String successfulEndChar() {
			return String.valueOf(endChar());
		}

		private String failingEndChar() {
			return String.valueOf(endChar() + 1);
		}

		private char endChar() {
			String str = (String)elements()[0];
			return str.charAt(str.length()-1);
		}
	}

}
