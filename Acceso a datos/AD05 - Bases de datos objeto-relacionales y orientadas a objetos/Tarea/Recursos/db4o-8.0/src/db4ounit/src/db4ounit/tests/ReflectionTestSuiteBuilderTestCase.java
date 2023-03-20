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

public class ReflectionTestSuiteBuilderTestCase implements TestCase {
	
	private final static class ExcludingReflectionTestSuiteBuilder extends
			ReflectionTestSuiteBuilder {
		public ExcludingReflectionTestSuiteBuilder(Class[] classes) {
			super(classes);
		}

		protected boolean isApplicable(Class clazz) {
			return clazz!=NotAccepted.class;
		}
	}

	public static class NonTestFixture {
	}
	
	public void testUnmarkedTestFixture() {
		
		final ReflectionTestSuiteBuilder builder = new ReflectionTestSuiteBuilder(NonTestFixture.class);
		assertFailingTestCase(IllegalArgumentException.class, builder);
	}
	
	public static class Accepted implements TestCase {
		public void test() {
		}
	}

	public static class NotAccepted implements TestCase {
		public void test() {
		}
	}

	public void testNotAcceptedFixture() {
		ReflectionTestSuiteBuilder builder = new ExcludingReflectionTestSuiteBuilder(new Class[]{Accepted.class,NotAccepted.class});
		Assert.areEqual(1, Iterators.size(builder.iterator()));
	}
	
	public static class ConstructorThrows implements TestCase {
		
		public static final RuntimeException ERROR = new RuntimeException("no way");
		
		public ConstructorThrows() {
			throw ERROR;
		}
		
		public void test1() {
		}
		
		public void test2() {
		}
	}
	
	public void testConstructorFailuresAppearAsFailedTestCases() {
		
		final ReflectionTestSuiteBuilder builder = new ReflectionTestSuiteBuilder(ConstructorThrows.class);
		Assert.areEqual(2, Iterators.toArray(builder.iterator()).length);
	}

	private Throwable assertFailingTestCase(final Class expectedError,
			final ReflectionTestSuiteBuilder builder) {
		final Iterator4 tests = builder.iterator();
		FailingTest test = (FailingTest) Iterators.next(tests);
		Assert.areSame(expectedError, test.error().getClass());
		return test.error();
	}
}
