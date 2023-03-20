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

import java.util.*;

import com.db4o.foundation.*;

import db4ounit.*;

@decaf.Remove
public class Algorithms4TestCase implements TestCase {
	
	public static class QuickSortableIntArray implements Sortable4{
		
		private int[] ints;
		
		public QuickSortableIntArray(int[] ints) {
			this.ints = ints;
		}

		public int compare(int leftIndex, int rightIndex) {
			return ints[leftIndex] - ints[rightIndex]; 
		}

		public int size() {
			return ints.length;
		}

		public void swap(int leftIndex, int rightIndex) {
			int temp = ints[leftIndex];
			ints[leftIndex] = ints[rightIndex];
			ints[rightIndex] = temp;
		}
	}
	
	public void testUnsortedSmall(){
		assertQSort(3 , 5, 2 , 1, 4);
	}
	
	public void testUnsortedBig() {
		assertQSort(3, 5, 7, 1, 2, 4, 6, 9, 11, 8, 10, 12);
	}
	
	public void testSingleElement(){
		assertQSort(42);
	}
	
	public void testTwoElements() {
		assertQSort(1, 42);
		assertQSort(42, 1);
	}
	
	public void testDuplicates() {
		assertQSort(2, 2, 1, 1, 5, 5, 5, 5, 5, 3, 3, 3);
	}

	public void testStackUsage(){
		int[] ints = new int[50000];
		for(int i=0;i<ints.length;i++) {
			ints[i]=i+1;
		}
		assertQSort(ints);
	}

	private void assertQSort(int... ints) {
		final int[] copy = Arrays4.copyOf(ints, ints.length);
		QuickSortableIntArray sample = new QuickSortableIntArray(copy);
		Algorithms4.sort(sample);
		Arrays.sort(ints);
		ArrayAssert.areEqual(ints, copy);
	}


}
