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
package com.db4o.osgi.test;

import org.osgi.framework.*;

import com.db4o.db4ounit.common.filelock.*;
import com.db4o.db4ounit.jre5.*;
import com.db4o.db4ounit.optional.monitoring.cs.*;
import com.db4o.test.nativequery.*;
import com.db4o.test.nativequery.analysis.*;
import com.db4o.test.nativequery.cats.*;
import com.db4o.test.nativequery.expr.*;
import com.db4o.test.nativequery.expr.build.*;

import db4ounit.*;
import db4ounit.extensions.*;

class Db4oTestServiceImpl implements Db4oTestService {
	
	private BundleContext _context;

	public Db4oTestServiceImpl(BundleContext context) {
		_context = context;
	}

	public int runTests(String databaseFilePath) throws Exception {
		final Db4oOSGiBundleFixture fixture = new Db4oOSGiBundleFixture(_context, databaseFilePath);
		final Db4oTestSuiteBuilder suite = new Db4oTestSuiteBuilder(fixture, 
				new Class[] {
				ExpressionBuilderTestCase.class,
				BloatExprBuilderVisitorTestCase.class,
				ExpressionTestCase.class,
				BooleanReturnValueTestCase.class,
				NQRegressionTestCase.class,
				NQCatConsistencyTestCase.class,
				AllTestsDb4oUnitJdk5.class
			});
		return new ConsoleTestRunner(suite).run();
	}

}
