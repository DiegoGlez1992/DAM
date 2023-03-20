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
package com.db4o.db4ounit.common.util;

public class Db4oUnitTestUtil {

	public static Class[] mergeClasses(Class[] classesLeft, Class[] classesRight) {
		if(classesLeft == null || classesLeft.length == 0) {
			return classesRight;
		}
		if(classesRight == null || classesRight.length == 0) {
			return classesLeft;
		}
		Class[] merged = new Class[classesLeft.length + classesRight.length];
		System.arraycopy(classesLeft, 0, merged, 0, classesLeft.length);
		System.arraycopy(classesRight, 0, merged, classesLeft.length, classesRight.length);
		return merged;
	}
	
	private Db4oUnitTestUtil() {
	}
}
