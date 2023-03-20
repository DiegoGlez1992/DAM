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
package com.db4o.consistency;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;

class OverlapMap {

	private Set<Pair<SlotDetail, SlotDetail>> _dupes = new HashSet<Pair<SlotDetail,SlotDetail>>();
	private TreeIntObject _slots = null;
	private final BlockConverter _blockConverter;
	
	public OverlapMap(BlockConverter blockConverter) {
		_blockConverter = blockConverter;
	}

	public void add(SlotDetail slot) {
		if(TreeIntObject.find(_slots, new TreeIntObject(slot._slot.address())) != null) {
			_dupes.add(new Pair<SlotDetail, SlotDetail>(byAddress(slot._slot.address()), slot));
		}
		_slots = (TreeIntObject) TreeIntObject.add(_slots, new TreeIntObject(slot._slot.address(), slot));
	}
	
	public Set<Pair<SlotDetail, SlotDetail>> overlaps() {
		final Set<Pair<SlotDetail, SlotDetail>> overlaps = new HashSet<Pair<SlotDetail, SlotDetail>>();
		final ByRef<SlotDetail> prevSlot = ByRef.newInstance();
		TreeIntObject.traverse(_slots, new Visitor4<TreeIntObject>() {
			public void visit(TreeIntObject tree) {
				SlotDetail curSlot = (SlotDetail) tree._object;
				if(isOverlap(prevSlot.value, curSlot)) {
					overlaps.add(new Pair<SlotDetail, SlotDetail>(prevSlot.value, curSlot));
				}
				prevSlot.value = curSlot;
			}

			private boolean isOverlap(SlotDetail prevSlot, SlotDetail curSlot) {
				if(prevSlot == null){
					return false;
				}
				return prevSlot._slot.address() + _blockConverter.bytesToBlocks(prevSlot._slot.length()) > curSlot._slot.address();
			}
		});
		return overlaps;
	}

	public Set<Pair<SlotDetail, SlotDetail>> dupes() {
		return _dupes;
	}
	
	private SlotDetail byAddress(int address) {
		TreeIntObject tree = (TreeIntObject) TreeIntObject.find(_slots, new TreeIntObject(address, null));
		return tree == null ? null : (SlotDetail)tree._object;
	}
}
