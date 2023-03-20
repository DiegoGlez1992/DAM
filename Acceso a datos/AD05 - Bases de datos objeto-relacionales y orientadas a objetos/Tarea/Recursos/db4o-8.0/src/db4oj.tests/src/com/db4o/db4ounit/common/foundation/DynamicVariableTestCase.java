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
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

public class DynamicVariableTestCase implements TestCase {

	public static void main(String[] args) {
		new ConsoleTestRunner(DynamicVariableTestCase.class).run();
	}

	public void testSingleThread() {
		final DynamicVariable variable = new DynamicVariable();
		checkVariableBehavior(variable);
	}

	public void testMultiThread() {
		final DynamicVariable variable = new DynamicVariable();
		final Collection4 failures = new Collection4();
		variable.with("mine", new Runnable() {
			public void run() {
				final Thread[] threads = createThreads(variable, failures);
				startAll(threads);
				for (int i=0; i<10; ++i) {
					Assert.areEqual("mine", variable.value());
				}
				joinAll(threads);
			}
		});
		Assert.isNull(variable.value());
		Assert.isTrue(failures.isEmpty(), failures.toString());
	}

	private void joinAll(final Thread[] threads) {
		for (int i = 0; i < threads.length; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void startAll(final Thread[] threads) {
		for (int i = 0; i < threads.length; i++) {
			threads[i].start();
		}
	}

	private Thread[] createThreads(final DynamicVariable variable, final Collection4 failures) {
		final Thread[] threads = new Thread[5];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Runnable() {
				public void run() {
					try {
						for (int i=0; i<10; ++i) {
							checkVariableBehavior(variable);
						}
					} catch (Exception failure) {
						synchronized (failures) {
							failures.add(failure);
						}
					}
				}
			}, "DynamicVariableTestCase.checkVariableBehavior Thread["+i+"]");
		}
		return threads;
	}

	private void checkVariableBehavior(final DynamicVariable variable) {
		Assert.isNull(variable.value());
		variable.with("foo", new Runnable() {
			public void run() {
				Assert.areEqual("foo", variable.value());
				variable.with("bar", new Runnable() {
					public void run() {
						Assert.areEqual("bar", variable.value());
					}
				});
				Assert.areEqual("foo", variable.value());
			}
		});
		Assert.isNull(variable.value());
	}

}
