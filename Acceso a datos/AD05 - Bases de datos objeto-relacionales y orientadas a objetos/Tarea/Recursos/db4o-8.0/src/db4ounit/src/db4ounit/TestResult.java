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

import java.io.IOException;
import java.io.Writer;

import db4ounit.util.StopWatch;

public class TestResult extends Printable implements TestListener {

	private TestFailureCollection _failures = new TestFailureCollection();
	
	private int _testCount = 0;
	
	private final StopWatch _watch = new StopWatch();
	
	public TestResult() {
	}

	public void testStarted(Test test) {	
		++_testCount;
	}	
	
	public void testFailed(Test test, Throwable failure) {
		_failures.add(new TestFailure(test.label(), failure));
	}
	
	public void failure(String msg, Throwable failure) {
	}
	
	/**
	 * @sharpen.property
	 */
	public int testCount() {
		return _testCount;
	}

	/**
	 * @sharpen.property
	 */
	public boolean green() {
		return _failures.size() == 0;
	}

	/**
	 * @sharpen.property
	 */
	public TestFailureCollection failures() {
		return _failures;
	}
	
	public void print(Writer writer) throws IOException {		
		if (green()) {
			writer.write("GREEN (" + _testCount + " tests) - " + elapsedString() + TestPlatform.NEW_LINE);
			return;
		}
		writer.write("RED (" + _failures.size() +" out of " + _testCount + " tests failed) - " + elapsedString() + TestPlatform.NEW_LINE);				
		_failures.print(writer);
	}
	
	private String elapsedString() {
		return _watch.toString();
	}

	public void runStarted() {
		_watch.start();
	}

	public void runFinished() {
		_watch.stop();
	}

}
