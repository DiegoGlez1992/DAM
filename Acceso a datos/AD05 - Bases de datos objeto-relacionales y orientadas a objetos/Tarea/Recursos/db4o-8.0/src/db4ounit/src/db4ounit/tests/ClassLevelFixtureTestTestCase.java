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
package db4ounit.tests;

import db4ounit.*;

public class ClassLevelFixtureTestTestCase implements TestCase {

	private static int _count;
	
	public void test() {
		_count = 0;
		TestResult result = new TestResult();
		new TestRunner(new ReflectionTestSuiteBuilder(SimpleTestSuite.class)).run(result);
		Assert.areEqual(3, _count);
		Assert.areEqual(1, result.testCount());
		Assert.areEqual(0, result.failures().size());
	}
	
	public static class SimpleTestSuite implements ClassLevelFixtureTest {
		public static void classSetUp() {
			ClassLevelFixtureTestTestCase._count++;
			
		}
		
		public static void classTearDown() {
			ClassLevelFixtureTestTestCase._count++;
		}

		public void test() {
			ClassLevelFixtureTestTestCase._count++;
		}
	}
	
}
