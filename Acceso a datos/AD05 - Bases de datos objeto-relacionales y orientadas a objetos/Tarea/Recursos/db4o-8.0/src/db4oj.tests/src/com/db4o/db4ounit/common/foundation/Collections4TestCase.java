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
public class Collections4TestCase implements TestCase {
	
	public void testSequenceSort() {
		assertSequenceSort(3, 2, 1 );
	}

	private void assertSequenceSort(Object... elements) {
		final Collection4 sequence = new Collection4(elements);
		Collections4.sort(sequence, new Comparison4() {
			public int compare(Object x, Object y) {
				return ((Comparable)x).compareTo(y);
			}
		});
		Arrays.sort(elements);
		Iterator4Assert.areEqual(Iterators.iterate(elements), sequence.iterator());
	}

}
