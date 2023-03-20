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
package com.db4o.internal.freespace;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public interface FreespaceManager {
	
	public void beginCommit();

	public void endCommit();
	
	public int slotCount();

	public void free(Slot slot);
	
    public void freeSelf();

	public int totalFreespace();
	
	public Slot allocateTransactionLogSlot(int length);

	public Slot allocateSlot(int length);

	public void migrateTo(FreespaceManager fm);

	public void read(LocalObjectContainer container, Slot slot);

	public void start(int id);

	public byte systemType();
	
	public void traverse(Visitor4<Slot> visitor);

	public void write(LocalObjectContainer container);

	public void commit();

	public Slot allocateSafeSlot(int length);

	public void freeSafeSlot(Slot slot);
	
	public void listener(FreespaceListener listener);
	
	public void slotFreed(Slot slot);

	public boolean isStarted();
	
}