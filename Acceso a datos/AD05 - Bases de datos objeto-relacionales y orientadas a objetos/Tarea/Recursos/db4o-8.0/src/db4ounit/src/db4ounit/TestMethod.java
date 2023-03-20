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

import java.lang.reflect.*;

import com.db4o.foundation.*;

/**
 * Reflection based db4ounit.Test implementation.
 */
public class TestMethod implements Test {
	
	private final Object _subject;
	private final Method _method;
	
	public TestMethod(Object instance, Method method) {
		if (null == instance) throw new IllegalArgumentException("instance");
		if (null == method) throw new IllegalArgumentException("method");	
		_subject = instance;
		_method = method;
	}
	
	public Object getSubject() {
		return _subject;
	}
	
	public Method getMethod() {
		return _method;
	}

	public String label() {
		return _subject.getClass().getName() + "." + _method.getName();
	}
	
	public String toString() {
		return "TestMethod(" + _method + ")";
	}

	public void run() {
		boolean exceptionInTest = false;
		try {
			try {
				setUp();
				invoke();
			} catch (InvocationTargetException x) {
				exceptionInTest = true;
				throw new TestException(x.getTargetException());
			} catch (Exception x) {
				exceptionInTest = true;
				throw new TestException(x);
			}
		} finally {
			try {
				tearDown();
			}
			catch(RuntimeException exc) {
				if(!exceptionInTest) {
					throw exc;
				}
				exc.printStackTrace();
			}
		}
	}

	protected void invoke() throws Exception {
		_method.invoke(_subject, new Object[0]);
	}

	protected void tearDown() {
		if (_subject instanceof TestLifeCycle) {
			try {
				((TestLifeCycle)_subject).tearDown();
			} catch (Exception e) {
				throw new TearDownFailureException(e);
			}
		}
	}

	protected void setUp() {
		if (_subject instanceof TestLifeCycle) {
			try {
				((TestLifeCycle)_subject).setUp();
			} catch (Exception e) {
				throw new SetupFailureException(e);
			}
		}
	}

	public boolean isLeafTest() {
		return true;
	}

	public Test transmogrify(Function4<Test, Test> fun) {
		return fun.apply(this);
	}
}
