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
import db4ounit.extensions.fixtures.*;
import db4ounit.fixtures.*;

public class ConcurrencyTestMethod extends TestMethod {

	private Thread[] threads;
	
	Exception[] failures;

	public ConcurrencyTestMethod(Object instance, Method method) {
		super(instance, method);
	}

	/*
	 * Override invoke method to support concurrency test
	 * 
	 * @see db4ounit.TestMethod#invoke()
	 */
	protected void invoke() throws Exception {
		AbstractDb4oTestCase toTest = subject();
		Method method = getMethod();
		invokeConcurrencyMethod(toTest, method);
	}

	private AbstractDb4oTestCase subject() {
		return (AbstractDb4oTestCase) getSubject();
	}

	private void invokeConcurrencyMethod(AbstractDb4oTestCase toTest, Method method)
			throws Exception {
		Class[] parameters = method.getParameterTypes();
		boolean hasSequenceParameter = false;

		if (parameters.length == 2) // ExtObjectContainer, seq
			hasSequenceParameter = true;

		int threadCount = toTest.threadCount();
		
		threads = new Thread[threadCount];
		failures = new Exception[threadCount];
		
		for (int i = 0; i < threadCount; ++i) {
			threads[i] = new Thread(new RunnableTestMethod(toTest, method, i,hasSequenceParameter), "ConcurrencyTestMethod.invokeConcurrencyMethod Thread["+i+"]");
		}
		// start threads simultaneously
		for (int i = 0; i < threadCount; ++i) {
			threads[i].start();
		}
		// wait for the threads to end
		for (int i = 0; i < threadCount; ++i) {
			threads[i].join();
		}
		// check if any of the threads ended abnormally
		for (int i = 0; i < threadCount; ++i) {
			if (failures[i] != null) {
				// TODO: show all failures by throwing another kind of exception.
				throw failures[i];
			}
		}
		// check test result
		checkConcurrencyMethod(toTest, method.getName());
	}

	private void checkConcurrencyMethod(AbstractDb4oTestCase toTest, String testMethodName) throws Exception {
		Method checkMethod = checkMethodFor(toTest.getClass(), testMethodName);
		if (null == checkMethod) {
			return;
		}
		// pass ExtObjectContainer as a param to check method
		ExtObjectContainer oc = fixture().db();
		try {
			checkMethod.invoke(toTest, new Object[] { oc });
		} finally {
			oc.close();
		}
	}

	private Method checkMethodFor(final Class testClass, String testMethodName) {
		try {
			Class[] types = { ExtObjectContainer.class };
			return testClass.getDeclaredMethod(ConcurrencyConventions.checkMethodNameFor(testMethodName), types);
		} catch (Exception e) {
			// if checkMethod is not availble, return as success
			return null;
		}
	}

	class RunnableTestMethod extends Contextful implements Runnable {
		private AbstractDb4oTestCase toTest;

		private Method method;

		private int seq;

		private boolean showSeq;

		RunnableTestMethod(AbstractDb4oTestCase toTest, Method method, int seq, boolean showSeq) {
			this.toTest = toTest;
			this.method = method;
			this.seq = seq;
			this.showSeq = showSeq;
		}

		public void run() {
			run(new Runnable() {
				public void run() {
					runMethod();
				}
			});
		}
		
		void runMethod() {
			ExtObjectContainer oc = null;
			try {
				oc = fixture().openNewSession(toTest);
				Object[] args;
				if (showSeq) {
					args = new Object[2];
					args[0] = oc;
					args[1] = new Integer(seq);
				} else {
					args = new Object[1];
					args[0] = oc;
				}
				method.invoke(toTest, args);
			} catch (Exception e) {
				failures[seq] = e;
			} finally {
				if (oc != null)
					oc.close();
			}
		}
	}

	MultiSessionFixture fixture() {
		return ((MultiSessionFixture)AbstractDb4oTestCase.fixture());
	}

}
