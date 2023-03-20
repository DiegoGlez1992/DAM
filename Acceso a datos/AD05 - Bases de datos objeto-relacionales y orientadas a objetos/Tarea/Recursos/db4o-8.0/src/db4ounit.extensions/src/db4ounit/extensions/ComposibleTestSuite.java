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
package db4ounit.extensions;

public abstract class ComposibleTestSuite extends Db4oTestSuite {

	protected final Class[] composeTests(Class[] testCases) {
		return concat(testCases, composeWith());
	}

	
	protected Class[] composeWith() {
		return new Class[0];
	}

	public static Class[] concat(Class[] testCases, Class[] otherTests) {		
		Class[] result = new Class[otherTests.length + testCases.length];
		System.arraycopy(testCases, 0, result, 0, testCases.length);
		System.arraycopy(otherTests, 0, result, testCases.length, otherTests.length);
		
		return result;
	}
}
