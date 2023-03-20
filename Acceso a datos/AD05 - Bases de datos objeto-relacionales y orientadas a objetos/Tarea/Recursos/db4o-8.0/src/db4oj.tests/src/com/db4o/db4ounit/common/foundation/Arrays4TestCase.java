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
package com.db4o.db4ounit.common.foundation;

import com.db4o.foundation.*;

import db4ounit.*;

/**
 * @exclude
 */
public class Arrays4TestCase implements TestCase {

	public void testContainsInstanceOf() {
		Object[] array = new Object[] { "foo", new Integer(42) };
		Assert.isTrue(Arrays4.containsInstanceOf(array, String.class));
		Assert.isTrue(Arrays4.containsInstanceOf(array, Integer.class));
		Assert.isTrue(Arrays4.containsInstanceOf(array, Object.class));
		Assert.isFalse(Arrays4.containsInstanceOf(array, Float.class));
		
		Assert.isFalse(Arrays4.containsInstanceOf(new Object[0], Object.class));
		Assert.isFalse(Arrays4.containsInstanceOf(new Object[1], Object.class));
		Assert.isFalse(Arrays4.containsInstanceOf(null, Object.class));
	}
	
	public void testCopyOfInt() {
		assertCopyOf();
		assertCopyOf(42);
		assertCopyOf(42, 42);
		
		assertCopyOf(new int[] { 1, 2, 3 }, 2);
		assertCopyOf(new int[] { 1, 2 }, 3);
	}

	private void assertCopyOf(int[] array, int newLength) {
		assertCopyOf(expectationFor(array, newLength), array, newLength);
	}

	private int[] expectationFor(int[] array, int newLength) {
		final int[] expectation = new int[newLength];
		System.arraycopy(array, 0, expectation, 0, Math.min(array.length, newLength));
		return expectation;
	}

	private void assertCopyOf(final int... array) {
		assertCopyOf(array, array, array.length);
	}

	private void assertCopyOf(final int[] expected, final int[] array, final int newLength) {
		final int[] copy = Arrays4.copyOf(array, newLength);
		Assert.areNotSame(array, copy);
		ArrayAssert.areEqual(expected, copy);
	}

}
