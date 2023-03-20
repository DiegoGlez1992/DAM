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
package com.db4o.internal.ids;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public interface TransactionalIdSystem {

	public void collectCallBackInfo(CallbackInfoCollector collector);

	public boolean isDirty();

	public void commit(FreespaceCommitter freespaceCommitter);

	public Slot committedSlot(int id);

	public Slot currentSlot(int id);
	
	public void accumulateFreeSlots(FreespaceCommitter freespaceCommitter, boolean forFreespace);

	public void rollback();

	public void clear();

	public boolean isDeleted(int id);

	public void notifySlotUpdated(int id, Slot slot, SlotChangeFactory slotChangeFactory);
	
	public void notifySlotCreated(int id, Slot slot, SlotChangeFactory slotChangeFactory);
	
	public void notifySlotDeleted(int id, SlotChangeFactory slotChangeFactory);

	public int newId(SlotChangeFactory slotChangeFactory);

	public int prefetchID();

	public void prefetchedIDConsumed(int id);

	public void close();

}