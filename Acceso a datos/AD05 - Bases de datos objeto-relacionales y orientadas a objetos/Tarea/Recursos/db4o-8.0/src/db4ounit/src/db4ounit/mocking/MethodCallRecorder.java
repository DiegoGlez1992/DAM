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
package db4ounit.mocking;

import com.db4o.foundation.*;

import db4ounit.*;

public class MethodCallRecorder implements Iterable4 {
	
	private final Collection4 _calls = new Collection4();
	
	public Iterator4 iterator() {
		return _calls.iterator();
	}
	
	public void record(MethodCall call) {
		_calls.add(call);
	}
	
	public void reset() {
		_calls.clear();
	}
	
	/**
	 * Asserts that the method calls were the same as expectedCalls.
	 * 
	 * Unfortunately we cannot call this method 'assert' because
	 * it's a keyword starting with java 1.5.
	 * 
	 * @param expectedCalls
	 */
	public void verify(MethodCall... expectedCalls) {
		Iterator4Assert.areEqual(expectedCalls, iterator());
	}

	public void verifyUnordered(MethodCall... expectedCalls) {
		Iterator4Assert.sameContent(expectedCalls, iterator());
    }
}
