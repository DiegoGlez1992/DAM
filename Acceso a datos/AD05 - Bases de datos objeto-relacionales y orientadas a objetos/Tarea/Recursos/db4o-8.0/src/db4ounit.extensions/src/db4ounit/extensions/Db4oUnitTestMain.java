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
package db4ounit.extensions;

import com.db4o.foundation.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

/**
 * @sharpen.ignore
 */
public class Db4oUnitTestMain extends UnitTestMain {
	public static void main(String[] args) throws Exception {
		new Db4oUnitTestMain().runTests(args);
	}

	private final Db4oFixture _fixture = 
//		new Db4oSolo();
		new Db4oInMemory();
	
	@Override
	protected Iterable4 builder(Class clazz) {
		
		return Iterators.concat(new Iterable4[] {
				
				solo(clazz),
				embedded(clazz),
				networkingCS(clazz),
				
//				defragSolo(clazz),
				
			}
		);
	}

	@SuppressWarnings("unused")
	private Db4oTestSuiteBuilder defragSolo(Class clazz) {
		return new Db4oTestSuiteBuilder(new Db4oDefragSolo(), clazz);
	}

	private Db4oTestSuiteBuilder networkingCS(Class clazz) {
		return new Db4oTestSuiteBuilder(Db4oFixtures.newNetworkingCS(), clazz);
	}

	private Db4oTestSuiteBuilder embedded(Class clazz) {
		return new Db4oTestSuiteBuilder(Db4oFixtures.newEmbedded(), clazz);
	}

	private Db4oTestSuiteBuilder solo(Class clazz) {
		return new Db4oTestSuiteBuilder(_fixture, clazz);
	}
	
	@Override
	protected Test wrapTest(Test test) {
		return new TestWithFixture(test, Db4oFixtureVariable.FIXTURE_VARIABLE, _fixture);
	}
}
