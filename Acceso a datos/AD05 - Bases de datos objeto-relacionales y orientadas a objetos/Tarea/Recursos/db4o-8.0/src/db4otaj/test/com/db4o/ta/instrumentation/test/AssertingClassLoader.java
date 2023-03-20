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
package com.db4o.ta.instrumentation.test;

import java.io.*;
import java.net.*;

import db4ounit.*;
import db4ounit.extensions.util.*;

/**
 * Creates a separate environment to load classes ({@link ExcludingClassLoader}
 * so they can be asserted after instrumentation.
 * 
 * @sharpen.ignore
 */
public class AssertingClassLoader {

	private final URLClassLoader _loader;

	public AssertingClassLoader(File classPath, Class[] excludedClasses) throws MalformedURLException {
		this(classPath, excludedClasses, new Class[0]);
	}

	public AssertingClassLoader(File classPath, Class[] excludedClasses, Class[] delegatedClasses) throws MalformedURLException {
		ExcludingClassLoader excludingLoader = new ExcludingClassLoader(getClass().getClassLoader(), excludedClasses, delegatedClasses);		
		_loader = new URLClassLoader(new URL[] { toURL(classPath) }, excludingLoader);
	}

	/**
	 * @deprecated
	 */
	private URL toURL(File classPath) throws MalformedURLException {
		return classPath.toURL();
	}

	public void assertAssignableFrom(Class expected, Class actual) throws ClassNotFoundException {
		if (isAssignableFrom(expected, actual)) {
			return;
		}
		
		fail(expected, actual, "not assignable from");
	}

	public void assertNotAssignableFrom(Class expected, Class actual) throws ClassNotFoundException {
		if (!isAssignableFrom(expected, actual)) {
			return;
		}
		
		fail(expected, actual, "assignable from");
	}
	
	private void fail(Class expected, Class actual, String reason) {
		Assert.fail("'" + actual + "' " + reason + " '" + expected + "'");
	}

	private boolean isAssignableFrom(Class expected, Class actual) throws ClassNotFoundException {
		Class loaded = loadClass(actual);
		return expected.isAssignableFrom(loaded);
	}

	public Class loadClass(Class actual) throws ClassNotFoundException {
		return _loader.loadClass(actual.getName());
	}

	public Object newInstance(Class clazz) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return loadClass(clazz).newInstance();
	}
}
