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

/**
 * A test that always fails with a specific exception.
 */
public class FailingTest implements Test {

	private final Throwable _error;
	private final String _label;

	public FailingTest(String label, Throwable error) {
		_label = label;
		_error = error;
	}

	public String label() {
		return _label;
	}
	
	public Throwable error() {
		return _error;
	}

	public void run() {
		throw new TestException(_error);
	}

	public boolean isLeafTest() {
		return true;
	}
	
	public Test transmogrify(Function4<Test, Test> fun) {
		return fun.apply(this);
	}

}