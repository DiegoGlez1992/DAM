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
package db4ounit.tests;

import db4ounit.*;
import db4ounit.mocking.*;

public class CompositeTestListenerTestCase implements TestCase {
	
	static final class ListenerRecorder implements TestListener {

		private final MethodCallRecorder _recorder;
		private final String _label;

		public ListenerRecorder(String label, MethodCallRecorder recorder) {
			_label = label;
			_recorder = recorder;
		}

		public void runFinished() {
			record("runFinished");
		}

		public void runStarted() {
			record("runStarted");
		}

		public void testFailed(Test test, Throwable failure) {
			record("testFailed", new Object[] { test, failure });
		}

		public void testStarted(Test test) {
			record("testStarted", new Object[] { test });
		}

		public void failure(String msg, Throwable failure) {
			record("failure", new Object[] { msg, failure });
		}
		
		private void record(String name) {
			record(name, new Object[0]);
		}

		private void record(String name, Object[] args) {
			_recorder.record(new MethodCall(_label + "." + name, args));
		}
	}
	
	public void test() {
		MethodCallRecorder recorder = new MethodCallRecorder();
		CompositeTestListener listener = new CompositeTestListener(
				new ListenerRecorder("first", recorder),
				new ListenerRecorder("second", recorder));
		
		final RunsGreen test = new RunsGreen();
		final RuntimeException failure = new RuntimeException();
		listener.runStarted();
		listener.testStarted(test);
		listener.testFailed(test, failure);
		listener.runFinished();
		
		recorder.verify(new MethodCall[] {
			call("first.runStarted"),
			call("second.runStarted"),
			call("first.testStarted", test),
			call("second.testStarted", test),
			call("first.testFailed", test, failure),
			call("second.testFailed", test, failure),
			call("first.runFinished"),
			call("second.runFinished"),
		});
	}

	private MethodCall call(String method, Object arg0, RuntimeException arg1) {
		return new MethodCall(method, arg0, arg1);
	}

	private MethodCall call(String method, Object arg) {
		return new MethodCall(method, arg);
	}

	private MethodCall call(final String method) {
		return new MethodCall(method);
	}

}
