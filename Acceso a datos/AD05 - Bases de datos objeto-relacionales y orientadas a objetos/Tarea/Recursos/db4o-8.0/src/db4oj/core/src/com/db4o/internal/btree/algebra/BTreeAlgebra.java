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
package com.db4o.internal.btree.algebra;

import com.db4o.foundation.*;
import com.db4o.internal.btree.*;


/**
 * @exclude
 */
class BTreeAlgebra {

	public static BTreeRange intersect(BTreeRangeUnion union, BTreeRangeSingle single) {		
		final SortedCollection4 collection = newBTreeRangeSingleCollection();		
		collectIntersections(collection, union, single);
		return toRange(collection);
	}

	public static BTreeRange intersect(BTreeRangeUnion union1, BTreeRangeUnion union2) {
		final SortedCollection4 collection = newBTreeRangeSingleCollection();
		final Iterator4 ranges = union1.ranges();
		while (ranges.moveNext()) {
			final BTreeRangeSingle current = (BTreeRangeSingle) ranges.current();
			collectIntersections(collection, union2, current);
		}
		return toRange(collection);
	}
	
	private static void collectIntersections(final SortedCollection4 collection, BTreeRangeUnion union, BTreeRangeSingle single) {
		final Iterator4 ranges = union.ranges();
		while (ranges.moveNext()) {
			final BTreeRangeSingle current = (BTreeRangeSingle) ranges.current();
			if (single.overlaps(current)) {
				collection.add(single.intersect(current));
			}
		}
	}

	public static BTreeRange intersect(BTreeRangeSingle single1, BTreeRangeSingle single2) {
		BTreePointer first = BTreePointer.max(single1.first(), single2.first());
		BTreePointer end = BTreePointer.min(single1.end(), single2.end());
		return single1.newBTreeRangeSingle(first, end);
	}

	public static BTreeRange union(final BTreeRangeUnion union1, final BTreeRangeUnion union2) {
		final Iterator4 ranges = union1.ranges();
		BTreeRange merged = union2;
		while (ranges.moveNext()) {
			merged = merged.union((BTreeRange) ranges.current());
		}
		return merged;
	}

	public static BTreeRange union(final BTreeRangeUnion union, final BTreeRangeSingle single) {
		if (single.isEmpty()) {
			return union;
		}
		
		SortedCollection4 sorted = newBTreeRangeSingleCollection();
		sorted.add(single);		
		
		BTreeRangeSingle range = single;
		Iterator4 ranges = union.ranges();
		while (ranges.moveNext()) {
			BTreeRangeSingle current = (BTreeRangeSingle) ranges.current();
			if (canBeMerged(current, range)) {
				sorted.remove(range);
				range = merge(current, range);
				sorted.add(range);
			} else {
				sorted.add(current);
			}
		}
		
		return toRange(sorted);
	}

	private static BTreeRange toRange(SortedCollection4 sorted) {
		if (1 == sorted.size()) {
			return (BTreeRange)sorted.singleElement();
		}
		return new BTreeRangeUnion(sorted);
	}

	private static SortedCollection4 newBTreeRangeSingleCollection() {
		return new SortedCollection4(BTreeRangeSingle.COMPARISON);
	}
	
	public static BTreeRange union(final BTreeRangeSingle single1, final BTreeRangeSingle single2) {
		if (single1.isEmpty()) {
			return single2;
		}
		if (single2.isEmpty()) {
			return single1;
		}
		if (canBeMerged(single1, single2)) {
			return merge(single1, single2);
		}
		return new BTreeRangeUnion(new BTreeRangeSingle[] { single1, single2 });
	}
	
	private static BTreeRangeSingle merge(BTreeRangeSingle range1, BTreeRangeSingle range2) {
		return range1.newBTreeRangeSingle(
					BTreePointer.min(range1.first(), range2.first()),
					BTreePointer.max(range1.end(), range2.end()));
	}

	private static boolean canBeMerged(BTreeRangeSingle range1, BTreeRangeSingle range2) {
		return range1.overlaps(range2)
				|| range1.adjacent(range2);
	}
}
