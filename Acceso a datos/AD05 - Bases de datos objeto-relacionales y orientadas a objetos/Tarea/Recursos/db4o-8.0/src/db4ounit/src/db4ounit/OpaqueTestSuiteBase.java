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

public abstract class OpaqueTestSuiteBase implements Test {

	private Closure4<Iterator4<Test>> _tests;
	
	public OpaqueTestSuiteBase(Closure4<Iterator4<Test>> tests) {
		_tests = tests;
	}

	public void run() {
		TestExecutor executor = Environments.my(TestExecutor.class);
		Iterator4<Test> tests = _tests.run();
		try {
			suiteSetUp();
			while(tests.moveNext()) {
				executor.execute(tests.current());
			}
			suiteTearDown();
		}
		catch(Exception exc) {
			executor.fail(this, exc);
		}
	}
	
	public boolean isLeafTest() {
		return false;
	}
	
	protected Closure4<Iterator4<Test>> tests() {
		return _tests;
	}
	
	public Test transmogrify(final Function4<Test, Test> fun) {
		return transmogrified(
			new Closure4<Iterator4<Test>>() {
				public Iterator4<Test> run() {
					return Iterators.map(tests().run(), new Function4<Test, Test>() {
						public Test apply(Test test) {
							return fun.apply(test);
						}
					});
				}
			});
	}

	protected abstract OpaqueTestSuiteBase transmogrified(Closure4<Iterator4<Test>> tests);
	
	protected abstract void suiteSetUp() throws Exception;
	protected abstract void suiteTearDown() throws Exception;

}
