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
package db4ounit.extensions.concurrency;

import java.lang.reflect.*;

import com.db4o.ext.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class Db4oConcurrencyTestSuiteBuilder extends Db4oTestSuiteBuilder {	

	public Db4oConcurrencyTestSuiteBuilder(Db4oFixture fixture, Class clazz) {
		super(fixture, clazz);
	}

	public Db4oConcurrencyTestSuiteBuilder(Db4oFixture fixture, Class[] classes) {
		super(fixture, classes);
	}

	protected Test createTest(Object instance, Method method) {
		return new ConcurrencyTestMethod(instance, method);
	}

	protected boolean isTestMethod(Method method) {
		String name = method.getName();
		return startsWithIgnoreCase(name, ConcurrencyConventions.testPrefix())
				&& TestPlatform.isPublic(method)
				&& !TestPlatform.isStatic(method) && hasValidParameter(method);
	}

	static boolean hasValidParameter(Method method) {
		Class[] parameters = method.getParameterTypes();
		if (parameters.length == 1 && parameters[0] == ExtObjectContainer.class)
			return true;

		if (parameters.length == 2 && parameters[0] == ExtObjectContainer.class
				&& parameters[1] == Integer.TYPE)
			return true;

		return false;
	}
}
