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

public class OpaqueTestSuiteTestCase implements TestCase {

	private static final int NUM_TESTS = 3;

	public void testAllSucceed() {
		assertTestRuns();
	}

	public void testSingleFailure() {
		assertTestRuns(NUM_TESTS / 2);
	}

	public void testAllFail() {
		int[] failingIndices = new int[NUM_TESTS];
		for (int i = 0; i < NUM_TESTS; i++) {
			failingIndices[i] = i;
		}
		assertTestRuns(failingIndices);
	}

	private void assertTestRuns(int... failingIndices) {
		IntByRef counter = new IntByRef();
		TestResult result = new TestResult() {
			@Override
			public void testStarted(Test test) {
				super.testStarted(test);
				Assert.isInstanceOf(CountingTest.class, test);
			}
		};
		new TestRunner(Iterators.iterable(new SimpleTestSuite(counter, NUM_TESTS, failingIndices))).run(result);
		Assert.areEqual(NUM_TESTS, result.testCount());
		Assert.areEqual(failingIndices.length, result.failures().size());
		Assert.areEqual(NUM_TESTS + 2, counter.value);
	}
	
	public static class SimpleTestSuite extends OpaqueTestSuiteBase {

		private IntByRef _counter;
		private int _numTests;
		
		public SimpleTestSuite(final IntByRef counter, final int numTests, final int[] failingIndices) {
			this(counter, numTests, new Closure4<Iterator4<Test>>() {
				public Iterator4<Test> run() {
					return Iterators.iterate(tests(counter, numTests, failingIndices));
				}
			});
		}

		private SimpleTestSuite(IntByRef counter, int numTests, Closure4<Iterator4<Test>> tests) {
			super(tests);
			_counter = counter;
			_numTests = numTests;
		}

		@Override
		protected void suiteSetUp() throws Exception {
			Assert.areEqual(0, _counter.value);
			_counter.value++;
		}

		@Override
		protected void suiteTearDown() throws Exception {
			Assert.areEqual(_numTests + 1, _counter.value);
			_counter.value++;
		}

		public String label() {
			return getClass().getName();
		}
		
		private static Test[] tests(IntByRef counter, int numTests, int... failingIndices) {
			Test[] tests = new Test[numTests];
			for (int i = 0; i < numTests; i++) {
				tests[i] = new CountingTest(counter, i + 1, Arrays4.indexOf(failingIndices, i) >= 0);
			}
			return tests;
		}

		@Override
		protected OpaqueTestSuiteBase transmogrified(Closure4<Iterator4<Test>> tests) {
			return new SimpleTestSuite(_counter, _numTests, tests);
		}
	}
	
	public static class CountingTest implements Test {

		private IntByRef _counter;
		private int _idx;
		private boolean _fail;
		
		public CountingTest(IntByRef counter, int idx, boolean fail) {
			_counter = counter;
			_idx = idx;
			_fail = fail;
		}
		
		public boolean isLeafTest() {
			return true;
		}

		public String label() {
			return getClass().getName();
		}

		public Test transmogrify(Function4<Test, Test> fun) {
			return fun.apply(this);
		}

		public void run() {
			Assert.areEqual(_idx, _counter.value);
			_counter.value++;
			if(_fail) {
				Assert.fail();
			}
		}
		
	}
	
}
