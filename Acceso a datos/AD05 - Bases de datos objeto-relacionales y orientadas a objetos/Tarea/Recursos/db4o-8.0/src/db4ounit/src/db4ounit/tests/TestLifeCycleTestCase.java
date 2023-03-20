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

public class TestLifeCycleTestCase implements TestCase {
	public void testLifeCycle() {
		
		final ByRef<Boolean> tearDownCalled = ByRef.newInstance(false);
		RunsLifeCycle._tearDownCalled.with(tearDownCalled, new Runnable() {
			public void run() {
				final Iterator4 tests = new ReflectionTestSuiteBuilder(RunsLifeCycle.class).iterator();
				final Test test = (Test)Iterators.next(tests);
				FrameworkTestCase.runTestAndExpect(test, 1);
			}
		});
		Assert.isTrue(tearDownCalled.value);
	}
}
