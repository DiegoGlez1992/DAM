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

/**
 * Utility class to enable the reuse of object comparison and checking
 * methods without asserting.
 */
public class Check {

	public static boolean objectsAreEqual(Object expected, Object actual) {
		return expected == actual
			|| (expected != null
				&& actual != null
				&& expected.equals(actual));
	}
	
	public static boolean arraysAreEqual(Object[] expected, Object[] actual) {
		if (expected == actual) return true;
		if (expected == null || actual == null) return false;
		if (expected.length != actual.length) return false;
	    for (int i = 0; i < expected.length; i++) {
	        if (!objectsAreEqual(expected[i], actual[i])) return false;
	    }
	    return true;
	}

}
