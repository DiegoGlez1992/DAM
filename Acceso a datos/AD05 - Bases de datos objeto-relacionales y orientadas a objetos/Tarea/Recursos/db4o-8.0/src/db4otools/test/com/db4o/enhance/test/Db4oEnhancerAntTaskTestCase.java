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
package com.db4o.enhance.test;

import com.db4o.ta.*;

import db4ounit.*;

public class Db4oEnhancerAntTaskTestCase implements TestCase {

	private final static Class<ToBeInstrumented> INSTRUMENTED_CLAZZ = ToBeInstrumented.class;

	private final static Class<NotToBeInstrumented> NOT_INSTRUMENTED_CLAZZ = NotToBeInstrumented.class;

	public static void main(String[] args) {
		new ConsoleTestRunner(Db4oEnhancerAntTaskTestCase.class).run();
	}

	public void test() throws Exception {
		Assert.isTrue(Activatable.class.isAssignableFrom(INSTRUMENTED_CLAZZ));
		Assert.isFalse(Activatable.class
				.isAssignableFrom(NOT_INSTRUMENTED_CLAZZ));
	}
}
