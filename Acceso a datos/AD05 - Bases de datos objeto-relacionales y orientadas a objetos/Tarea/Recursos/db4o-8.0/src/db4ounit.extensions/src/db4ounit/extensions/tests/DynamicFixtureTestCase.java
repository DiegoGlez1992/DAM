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
package db4ounit.extensions.tests;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;


public class DynamicFixtureTestCase implements TestSuiteBuilder {
	
	public Iterator4 iterator() {
		// The test case simply runs FooTestSuite
		// with a Db4oInMemory fixture to ensure the 
		// the db4o fixture can be successfully propagated
		// to FooTestUnit#test.
		return new Db4oTestSuiteBuilder(
					new Db4oInMemory(),
					FooTestSuite.class).iterator();
	}	
	
	/**
	 * One of the possibly many test units.
	 */
	public static class FooTestUnit extends AbstractDb4oTestCase {
		
		private final Object[] values = MultiValueFixtureProvider.value();
		
		public void test() {
			Assert.isNotNull(db());
			Assert.isNotNull(values);
		}
	}
	
	/**
	 * The test suite which binds together fixture providers and test units.
	 */
	public static class FooTestSuite extends FixtureTestSuiteDescription {{

		fixtureProviders(
			new MultiValueFixtureProvider(new Object[][] {
				new Object[] { "foo", "bar" },
				new Object[] { 1, 42 },
			})
		);
	
		testUnits(FooTestUnit.class);
	}}
}
