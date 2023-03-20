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

import com.db4o.foundation.*;

public class IntArrays4 {

	public static int[] fill(int[] array, int value) {
		for (int i=0; i<array.length; ++i) {
			array[i] = value;
		}
		return array;
	}

	public static int[] concat(int[] a, int[] b) {
		int[] array = new int[a.length + b.length];
		System.arraycopy(a, 0, array, 0, a.length);
		System.arraycopy(b, 0, array, a.length, b.length);
		return array;
	}

	public static int occurences(int[] values, int value) {
	    int count = 0;
	    for (int i = 0; i < values.length; i++) {
	        if(values[i] == value){
	            count ++;
	        }
	    }
	    return count;
	}

	public static int[] clone(int[] bars) {
		int[] array = new int[bars.length];
		System.arraycopy(bars, 0, array, 0, bars.length);
		return array;
	}

	public static Object[] toObjectArray(int[] values) {
	    Object[] ret = new Object[values.length];
	    for (int i = 0; i < values.length; i++) {
	        ret[i] = new Integer(values[i]);
	    }
	    return ret;
	}

	public static Iterator4 newIterator(int[] values) {
		return new ArrayIterator4(toObjectArray(values));
	}

}
