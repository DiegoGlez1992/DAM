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

import java.io.*;

import com.db4o.foundation.*;

public class ConsoleTestRunner {
	
	private final Iterable4 _suite;
	private final boolean _reportToFile;
	
	public ConsoleTestRunner(Iterator4 suite) {
		this(suite, true);
	}

	public ConsoleTestRunner(Iterator4 suite, boolean reportToFile) {
		if (null == suite) throw new IllegalArgumentException("suite");
		_suite = Iterators.iterable(suite);
		_reportToFile = reportToFile;
	}

	public ConsoleTestRunner(Iterable4 suite) {
		this(suite, true);
	}

	public ConsoleTestRunner(Iterable4 suite, final boolean reportToFile) {
		if (null == suite) throw new IllegalArgumentException("suite");
		_suite = suite;
		_reportToFile = reportToFile;
	}
	
	public ConsoleTestRunner(Class clazz) {
		this(new ReflectionTestSuiteBuilder(clazz));
	}	

	public int run() {
		return run(TestPlatform.getStdErr());
	}
	
	protected TestResult createTestResult() {
		return new TestResult();
	}

	public int run(Writer writer) {		
		
		TestResult result = createTestResult();
		
		new TestRunner(_suite).run(new CompositeTestListener(new ConsoleListener(writer), result));
		
		reportResult(result, writer);
		return result.failures().size();
	}

	private void report(Exception x) {
		TestPlatform.printStackTrace(TestPlatform.getStdErr(), x);
	}

	private void reportResult(TestResult result, Writer writer) {
		if(_reportToFile) {
			reportToTextFile(result);
		}
		report(result, writer);
	}

	private void reportToTextFile(TestResult result) {
		try {
			java.io.Writer writer = TestPlatform.openTextFile("db4ounit.log");
			try {
				report(result, writer);
			} finally {
				writer.close();
			}
		} catch (IOException e) {
			report(e);
		}
	}

	private void report(TestResult result, Writer writer) {
		try {
			result.print(writer);
			writer.flush();
		} catch (IOException e) {
			report(e);
		}
	}
}
