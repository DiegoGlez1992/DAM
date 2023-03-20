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
import com.db4o.internal.*;

public class ClassLevelFixtureTestSuite extends OpaqueTestSuiteBase {

	public static final String TEARDOWN_METHOD_NAME = "classTearDown";
	public static final String SETUP_METHOD_NAME = "classSetUp";
	
	private final Class<?> _clazz;
	
	public ClassLevelFixtureTestSuite(Class<?> clazz, Closure4<Iterator4<Test>> tests) {
		super(tests);
		_clazz = clazz;
	}

	@Override
	protected void suiteSetUp() throws Exception {
		Reflection4.invokeStatic(_clazz, SETUP_METHOD_NAME);
	}

	@Override
	protected void suiteTearDown() throws Exception {
		Reflection4.invokeStatic(_clazz, TEARDOWN_METHOD_NAME);
	}

	public String label() {
		return _clazz.getName();
	}
	
	protected OpaqueTestSuiteBase transmogrified(Closure4<Iterator4<Test>> tests) {
		return new ClassLevelFixtureTestSuite(_clazz, tests);
	}
}
