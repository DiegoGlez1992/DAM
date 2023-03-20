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

import static db4ounit.mocking.MethodCall.Conditions.*;

import com.db4o.config.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.mocking.*;

public class FixtureConfigurationTestCase implements TestCase {

	static final class MockFixtureConfiguration
		extends MethodCallRecorder
		implements FixtureConfiguration {
		
		public void configure(Db4oTestCase testCase, Configuration config) {
			record(new MethodCall("configure", testCase, config));
		}

		public String getLabel() {
			return "MOCK";
		}
	}
	
	public static final class TestCase1 extends AbstractDb4oTestCase {
		public void test() {
		}
	}
	
	public static final class TestCase2 extends AbstractDb4oTestCase {
		public void test() {
		}
	}                                              
	
	public void testSolo() {
		assertFixtureConfiguration(new Db4oSolo());
	}
	
	public void testClientServer() {
		assertFixtureConfiguration(
			Db4oFixtures.newNetworkingCS());
	}
	
	public void testInMemory() {
		assertFixtureConfiguration(new Db4oInMemory());
	}

	private void assertFixtureConfiguration(Db4oFixture fixture) {
		
		final MockFixtureConfiguration configuration = new MockFixtureConfiguration();
		fixture.fixtureConfiguration(configuration);
		
		Assert.isTrue(
			fixture.label().endsWith(" - " + configuration.getLabel()),
			"FixtureConfiguration label must be part of Fixture label.");
		
		new TestRunner(
				new Db4oTestSuiteBuilder(fixture, new Class[] {
					TestCase1.class,
					TestCase2.class,
				})).run(new TestResult());
		
		configuration.verify(
			new MethodCall("configure", isA(TestCase1.class), MethodCall.IGNORED_ARGUMENT),
			new MethodCall("configure", isA(TestCase1.class), MethodCall.IGNORED_ARGUMENT),
			new MethodCall("configure", isA(TestCase2.class), MethodCall.IGNORED_ARGUMENT),
			new MethodCall("configure", isA(TestCase2.class), MethodCall.IGNORED_ARGUMENT)
		);
	}
}
