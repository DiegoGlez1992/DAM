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
import com.db4o.foundation.io.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.extensions.util.*;
import db4ounit.mocking.*;
import db4ounit.tests.*;

public class FixtureTestCase implements TestCase {
	private final class ExcludingInMemoryFixture extends Db4oInMemory {
		public boolean accept(Class clazz) {
			return !OptOutFromTestFixture.class.isAssignableFrom(clazz);
		}
	}

	public void testSingleTestWithDifferentFixtures() {
		assertSimpleDb4o(new Db4oInMemory());
		assertSimpleDb4o(new Db4oSolo());
	}
	
	public void testMultipleTestsSingleFixture() {
		MultipleDb4oTestCase.resetConfigureCalls();
		FrameworkTestCase.runTestAndExpect(new Db4oTestSuiteBuilder(new Db4oInMemory(), MultipleDb4oTestCase.class), 2, false);
		Assert.areEqual(2,MultipleDb4oTestCase.configureCalls());
	}

	public void testSelectiveFixture() {
		final Db4oFixture fixture=new ExcludingInMemoryFixture();
		final Iterator4 tests = new Db4oTestSuiteBuilder(fixture, new Class[]{AcceptedTestCase.class,NotAcceptedTestCase.class}).iterator();
		final Test test = nextTest(tests);
		Assert.isFalse(tests.moveNext());
		FrameworkTestCase.runTestAndExpect(test,0);
	}

	private void assertSimpleDb4o(Db4oFixture fixture) {
		final Iterator4 tests = new Db4oTestSuiteBuilder(fixture, SimpleDb4oTestCase.class).iterator();
		final Test test = nextTest(tests);
		
		final MethodCallRecorder recorder = new MethodCallRecorder();
		SimpleDb4oTestCase.RECORDER_VARIABLE.with(recorder, new Runnable() {
			public void run() {
				FrameworkTestCase.runTestAndExpect(test, 0);
			}
		});
		recorder.verify(
			new MethodCall("fixture", fixture), // synthetic method call
			new MethodCall("configure", MethodCall.IGNORED_ARGUMENT),
			new MethodCall("store"),
			new MethodCall("testResultSize")
		);
	}

	private Test nextTest(Iterator4 tests) {
		return (Test) Iterators.next(tests);
	}

	public void testInterfaceIsAvailable() {
		Assert.isTrue(Db4oTestCase.class.isAssignableFrom(AbstractDb4oTestCase.class));
	}
	
	public void testDeleteDir() throws Exception {
		File4.mkdirs("a/b/c");
		Assert.isTrue(File4.exists("a"));
		IOUtil.deleteDir("a");
		Assert.isFalse(File4.exists("a"));
	}
}
