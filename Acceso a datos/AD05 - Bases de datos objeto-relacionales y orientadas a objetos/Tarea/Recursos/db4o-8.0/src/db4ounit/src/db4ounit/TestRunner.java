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
package db4ounit;

import com.db4o.foundation.*;

public class TestRunner {
	
	public static DynamicVariable<TestExecutor> EXECUTOR = DynamicVariable.newInstance();
	
	private final Iterable4 _tests;

	public TestRunner(Iterable4 tests) {
		_tests = tests;
	}

	public void run(final TestListener listener) {
		listener.runStarted();
		TestExecutor executor = new TestExecutor() {
			public void execute(Test test) {
				runTest(test, listener);
			}

			public void fail(Test test, Throwable failure) {
				listener.testFailed(test, failure);
			}
		};
		Environments.runWith(Environments.newClosedEnvironment(executor), new Runnable() {
			public void run() {
				final Iterator4 iterator = _tests.iterator();
				while (iterator.moveNext()) {
					runTest((Test)iterator.current(), listener);
				}
			}
		});
		listener.runFinished();
	}

	private void runTest(final Test test, TestListener listener) {
		if(test.isLeafTest()) {
			listener.testStarted(test);
		}
		try {
			test.run();
		} catch (TestException x) {
		    Throwable reason = x.getReason();
			listener.testFailed(test, reason == null ? x : reason);
		} catch (Exception failure) {
			listener.testFailed(test, failure);
		}
	}

}
