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

import com.db4o.foundation.*;

import db4ounit.*;

public class FrameworkTestCase implements TestCase {
	public final static RuntimeException EXCEPTION = new RuntimeException();
	
	public void testRunsGreen() {
		TestResult result = new TestResult();
		new TestRunner(Iterators.singletonIterable(new RunsGreen())).run(result);
		Assert.isTrue(result.failures().size() == 0, "not green");
	}
	
	public void testRunsRed() {
		TestResult result = new TestResult();
		new TestRunner(Iterators.singletonIterable(new RunsRed(EXCEPTION))).run(result);
		Assert.isTrue(result.failures().size() == 1, "not red");
	}
	
	public static void runTestAndExpect(Test test,int expFailures) {
		runTestAndExpect(test,expFailures,true);
	}
	
	public static void runTestAndExpect(Test test,int expFailures, boolean checkException) {
		runTestAndExpect(Iterators.singletonIterable(test), expFailures, checkException);
	}

	public static void runTestAndExpect(final Iterable4 tests, int expFailures, boolean checkException) {
		final TestResult result = new TestResult();
		new TestRunner(tests).run(result);
		if (expFailures != result.failures().size()) {
			Assert.fail(result.failures().toString());
		}
		if (checkException) {
			for(Iterator4 iter=result.failures().iterator(); iter.moveNext();) {
				TestFailure failure = (TestFailure) iter.current();
				Assert.areEqual(EXCEPTION, failure.reason());
			}
		}
	}
}
