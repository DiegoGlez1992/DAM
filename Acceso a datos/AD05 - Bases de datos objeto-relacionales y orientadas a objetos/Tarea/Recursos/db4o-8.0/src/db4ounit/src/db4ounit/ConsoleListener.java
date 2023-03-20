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

import javax.swing.JTable.*;

public class ConsoleListener implements TestListener {
	
	private final Writer _writer;
	
	public ConsoleListener(Writer writer) {
		_writer = writer;
	}

	public void runFinished() {
	}

	public void runStarted() {
	}

	public void testFailed(Test test, Throwable failure) {
		printFailure(failure);
	}

	public void testStarted(Test test) {
		print(test.label());
	}
	
	private void printFailure(Throwable failure) {
		if (failure == null) {
			print("\t!");
		} else {
			print("\t! " + failure);
		}
	}
	
	private void print(String message) {
		try {
			_writer.write(message + TestPlatform.NEW_LINE);
			_writer.flush();
		} catch (IOException x) {
			TestPlatform.printStackTrace(_writer, x);
		}
	}

	public void failure(String msg, Throwable failure) {
		print("\t ! " + msg);
		printFailure(failure);
	}
}