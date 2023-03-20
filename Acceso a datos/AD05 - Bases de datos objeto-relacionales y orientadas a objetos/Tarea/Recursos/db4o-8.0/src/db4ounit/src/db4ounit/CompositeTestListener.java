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

public class CompositeTestListener implements TestListener {

	private final TestListener _listener1;
	private final TestListener _listener2;

	public CompositeTestListener(TestListener listener1, TestListener listener2) {
		_listener1 = listener1;
		_listener2 = listener2;
	}

	public void runFinished() {
		_listener1.runFinished();
		_listener2.runFinished();
	}

	public void runStarted() {
		_listener1.runStarted();
		_listener2.runStarted();
	}

	public void testFailed(Test test, Throwable failure) {
		_listener1.testFailed(test, failure);
		_listener2.testFailed(test, failure);
	}

	public void testStarted(Test test) {
		_listener1.testStarted(test);
		_listener2.testStarted(test);
	}

	public void failure(String msg, Throwable failure) {
		_listener1.failure(msg, failure);
		_listener2.failure(msg, failure);
	}

}
