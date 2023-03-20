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
package com.db4o.db4ounit.common.freespace;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.*;

@decaf.Remove(decaf.Platform.JDK11)
public class BlockAwareFreespaceManagerTestCase implements TestCase {
	
	private static final int BLOCK_SIZE = 7;
	
	private final BlockConverter _blockConverter = new BlockSizeBlockConverter(BLOCK_SIZE);
	
	private final InMemoryFreespaceManager _blocked = new InMemoryFreespaceManager(null, 0, 0);
	
	private final BlockAwareFreespaceManager _nonBlocked = new BlockAwareFreespaceManager(_blocked , new BlockSizeBlockConverter(BLOCK_SIZE));

	public void testFree(){
		Slot slot = new Slot(1, 15);
		_nonBlocked.free(slot);
		assertContains(_blockConverter.toBlockedLength(slot));
	}
	
	public void testAllocateSlot(){
		Slot slot = new Slot(1, 15);
		_nonBlocked.free(slot);
		
		Slot allocatedSlot = _nonBlocked.allocateSlot(8);
		int expectedAllocatedSlotLength = _blockConverter.blockAlignedBytes(8);
		Slot expectedSlot = new Slot(1, expectedAllocatedSlotLength);
		Assert.areEqual(expectedSlot, allocatedSlot);
	}
	
	public void testNoSlotAvailable(){
		Slot slot = _nonBlocked.allocateSlot(1);
		Assert.isNull(slot);
	}

	private void assertContains(Slot ...slots) {
		final List<Slot> foundSlots = new ArrayList<Slot>();
		_blocked.traverse(new Visitor4<Slot>() {
			public void visit(Slot slot) {
				foundSlots.add(slot);
				
			}
		});
		IteratorAssert.sameContent(slots, foundSlots);
	}

}
