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

import com.db4o.*;
import com.db4o.internal.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

import db4ounit.*;
import db4ounit.extensions.fixtures.*;


public abstract class FreespaceManagerTestCaseBase extends FileSizeTestCaseBase implements OptOutMultiSession{
	
	protected FreespaceManager[] fm;
	
	protected void db4oSetupAfterStore() throws Exception {
		LocalObjectContainer container = (LocalObjectContainer) db();
		
		BTreeFreespaceManager btreeFm = new BTreeFreespaceManager(container, null, container.configImpl().discardFreeSpace(), AbstractFreespaceManager.REMAINDER_SIZE_LIMIT);
		btreeFm.start(0);
		
		fm = new FreespaceManager[]{
			new InMemoryFreespaceManager(null, container.configImpl().discardFreeSpace(), AbstractFreespaceManager.REMAINDER_SIZE_LIMIT),
			btreeFm,
		};
	}
	
	protected void clear(FreespaceManager freespaceManager){
		Slot slot = null;
		do{
			slot = freespaceManager.allocateSlot(1);
		}while(slot != null);
		Assert.areEqual(0, freespaceManager.slotCount());
		Assert.areEqual(0, freespaceManager.totalFreespace());
	}
	
	protected void assertSame(FreespaceManager fm1, FreespaceManager fm2 ){
		Assert.areEqual(fm1.slotCount(), fm2.slotCount());
		Assert.areEqual(fm1.totalFreespace(), fm2.totalFreespace());
	}

	protected void assertSlot(Slot expected, Slot actual){
		Assert.areEqual(expected, actual);
	}
    
    protected void produceSomeFreeSpace() {
        int length = 300;
        Slot slot = localContainer().allocateSlot(length);
        ByteArrayBuffer buffer = new ByteArrayBuffer(length);
        if (Debug4.xbytes) {
        	buffer.checkXBytes(false);
        }
        localContainer().writeBytes(buffer, slot.address(), 0);
        localContainer().free(slot);
    }

    protected FreespaceManager currentFreespaceManager() {
        return localContainer().freespaceManager();
    }
    
    public static class Item{
        public int _int; 
    }

     protected void storeSomeItems() {
        for (int i = 0; i < 3; i++) {
            store(new Item());
        }
        db().commit();
    }
    
    protected LocalObjectContainer localContainer() {
        return fixture().fileSession();
    }

}
