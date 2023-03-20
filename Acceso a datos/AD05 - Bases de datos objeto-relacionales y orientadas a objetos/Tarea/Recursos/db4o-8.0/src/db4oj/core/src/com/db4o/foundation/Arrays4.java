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
package com.db4o.foundation;

import java.lang.reflect.*;
import java.util.*;



/**
 * @exclude
 */
public class Arrays4 {
	
	public static int[] copyOf(int[] src, int newLength) {
		final int[] copy = new int[newLength];
		System.arraycopy(src, 0, copy, 0, Math.min(src.length, newLength));
		return copy;
	}

	public static int indexOfIdentity(Object[] array, Object element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfEquals(Object[] array, Object expected) {
	    for (int i = 0; i < array.length; ++i) {                
	        if (expected.equals(array[i])) {
	            return i;
	        }
	    }
	    return -1;
	}

	public static int indexOf(int[] array, int element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	
	public static boolean equals(final byte[] x, final byte[] y) {
		if (x == y) {
			return true;
		}
	    if (x == null) {
	    	return false;
	    }
	    if (x.length != y.length) {
	    	return false;
	    }
	    for (int i = 0; i < x.length; i++) {
			if (y[i] != x[i]) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean equals(final Object[] x, final Object[] y) {
		if (x == y) {
			return true;
		}
	    if (x == null) {
	    	return false;
	    }
	    if (x.length != y.length) {
	    	return false;
	    }
	    for (int i = 0; i < x.length; i++) {
			if (!objectsAreEqual(y[i], x[i])) {
				return false;
			}
		}
		return true;
	}

	private static boolean objectsAreEqual(Object x, Object y) {
		if (x == y) return true;
		if (x == null || y == null) return false;
		return x.equals(y);
	}

	public static boolean containsInstanceOf(Object[] array, Class klass) {
		if (array == null) {
			return false;
		}
		for (int i=0; i<array.length; ++i) {
			if (klass.isInstance(array[i])) {
				return true;
			}
		}
		return false;
	}

	public static <T> void fill(T[] array, T value) {
		for (int i=0; i<array.length; ++i) {
			array[i] = value;
		}
    }

	public static Collection4 asList(Object[] arr) {
		Collection4 coll = new Collection4();
		for (int arrIdx = 0; arrIdx < arr.length; arrIdx++) {
			coll.add(arr[arrIdx]);
		}
		return coll;
	}
	
	/**
	 * @sharpen.ignore
	 */
	public static Object[] merge(Object[] a, Object[] b, Class arrayType) {
		Object[] merged = (Object[])Array.newInstance(arrayType, a.length + b.length);
		System.arraycopy(a, 0, merged, 0, a.length);
		System.arraycopy(b, 0, merged, a.length, b.length);
		return merged;
	}

	@decaf.Ignore(except=decaf.Platform.JDK11)
	public static void sort(final Object[] array, final Comparison4 comparator) {
		Algorithms4.sort(new Sortable4() {
			public void swap(int leftIndex, int rightIndex) {
				final Object leftValue = array[leftIndex];
				array[leftIndex] = array[rightIndex];
				array[rightIndex] = leftValue;
			}
			
			public int size() {
				return array.length;
			}
			
			public int compare(int leftIndex, int rightIndex) {
				return comparator.compare(array[leftIndex], array[rightIndex]);
			}
		});
	}
	
	public static long[] toLongArray(List<Long> list) {
		long[] arr = new long[list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = list.get(i);
		}
		return arr;
	}

}
