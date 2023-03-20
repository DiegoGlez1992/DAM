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

/**
 * @exclude
 */
public class Algorithms4 {

	private static final int QSORT_LENGTH_THRESHOLD = 7;

	public static void sort(Sortable4 sortable) {
		sort(sortable, 0, sortable.size());
	}

	public static void sort(Sortable4 sortable, int start, int end) {
		int length = end - start;
		if (length < QSORT_LENGTH_THRESHOLD) {
			insertionSort(sortable, start, end);
			return;
		}
		qsort(sortable, start, end);
	}

	public static void qsort(Sortable4 sortable, int start, int end) {
		int length = end - start;
		int middle = start + length / 2;
		if (length > 7) {
			int bottom = start;
			int top = end - 1;
			if (length > 40) {
				length /= 8;
				bottom = middleValueIndex(sortable, bottom, bottom + length, bottom
						+ (2 * length));
				middle = middleValueIndex(sortable, middle - length, middle, middle
						+ length);
				top = middleValueIndex(sortable, top - (2 * length), top - length, top);
			}
			middle = middleValueIndex(sortable, bottom, middle, top);
		}
		int a, b, c, d;
		a = b = start;
		c = d = end - 1;
		while (true) {
			while (b <= c && sortable.compare(b, middle) <= 0) {
				if (sortable.compare(b, middle) == 0) {
					middle = newPartionIndex(middle, a, b);
					swap(sortable, a++, b);
				}
				b++;
			}
			while (c >= b && sortable.compare(c, middle) >= 0) {
				if (sortable.compare(c, middle) == 0) {
					middle = newPartionIndex(middle, c, d);
					swap(sortable, c, d--);
				}
				c--;
			}
			if (b > c) {
				break;
			}
			middle = newPartionIndex(middle, b, c);
			swap(sortable, b++, c--);
		}
		length = Math.min(a - start,b - a); 
		
		swap(sortable, start, b - length, length);
		length = Math.min(d - c, end - 1 - d);

		swap(sortable, b, end - length, length);
		length = b - a;
		if (length > 0) {
			sort(sortable, start, start + length);
		}
		length = d - c;
		if (length > 0) {
			sort(sortable, end - length, end);
		}
	}

	public static void insertionSort(Sortable4 sortable, int start,
			int end) {
		for (int i = start + 1; i < end; i++) {
			for (int j = i; j > start && sortable.compare(j - 1, j) > 0; j--) {
				swap(sortable, j - 1, j);
			}
		}
	}

	private static int newPartionIndex(int oldPartionIndex, int leftSwapIndex, int rightSwapIndex) {
		if(leftSwapIndex == oldPartionIndex) {
			return rightSwapIndex;
		} else if (rightSwapIndex == oldPartionIndex) {
			return leftSwapIndex;
		}
		return oldPartionIndex;
	}

	private static int middleValueIndex(Sortable4 sortable, int a, int b, int c) {
		if (sortable.compare(a, b) < 0) {
			if (sortable.compare(b, c) < 0) {
				return b;
			} else {
				if (sortable.compare(a, c) < 0) {
					return c;
				} else {
					return a;
				}
			}
		} else {
			if (sortable.compare(b, c) > 0) {
				return b;
			} else {
				if (sortable.compare(a, c) > 0) {
					return c;
				} else {
					return a;
				}
			}
		}
	}

	private static void swap(Sortable4 sortable, int left, int right) {
		if (left == right) {
			return;
		}
		sortable.swap(left, right);
	}
	
	private static void swap(Sortable4 sortable, int from, int to, int length) {
		while (length-- > 0) {
			swap(sortable, from++, to++);
		}
	}
	
}
