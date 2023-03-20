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
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;


public class FreespaceManagerForDebug implements FreespaceManager {

    private final SlotListener _listener;

    public FreespaceManagerForDebug(SlotListener listener) {
        _listener = listener;
    }
    
	public Slot allocateSafeSlot(int length) {
		return null;
	}

	public void freeSafeSlot(Slot slot) {
		
	}
	
    public void beginCommit() {

    }

	public void commit() {
		
	}
	
    public void endCommit() {

    }

    public int slotCount() {
        return 0;
    }

    public void free(Slot slot) {
        _listener.onFree(slot);
    }

    public void freeSelf() {

    }

	public Slot allocateSlot(int length) {
		return null;
	}

    public void start(int id) {

    }

    public byte systemType() {
        return AbstractFreespaceManager.FM_DEBUG;
    }

	public void traverse(Visitor4<Slot> visitor) {
		
	}
	
    public void write(LocalObjectContainer container) {
    	
    }

	public void listener(FreespaceListener listener) {
		
	}

	public void migrateTo(FreespaceManager fm) {
		// TODO Auto-generated method stub
		
	}

	public int totalFreespace() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void slotFreed(Slot slot) {
		// TODO Auto-generated method stub
		
	}

	public boolean isStarted() {
		return false;
	}

	public Slot allocateTransactionLogSlot(int length) {
		
		return null;
	}

	public void read(LocalObjectContainer container, Slot slot) {
		
	}

}
