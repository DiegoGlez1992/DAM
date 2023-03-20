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
package com.db4o.cs.internal.messages;

import com.db4o.internal.*;
import com.db4o.internal.slots.*;

public final class MWriteNew extends MsgObject implements ServerSideMessage {
	
	public final void processAtServer() {
        int classMetadataId = _payLoad.readInt();
        unmarshall(_payLoad._offset);
        synchronized (containerLock()) {
            ClassMetadata classMetadata = classMetadataId == 0
            					? null
            					: localContainer().classMetadataForID(classMetadataId);
            
            int id = _payLoad.getID();
            
            transaction().idSystem().prefetchedIDConsumed(id);            
            
            Slot slot = localContainer().allocateSlotForNewUserObject(transaction(), id, _payLoad.length());
            
            _payLoad.address(slot.address());
            
            if(classMetadata != null){
                classMetadata.addFieldIndices(_payLoad);
            }
            localContainer().writeNew(transaction(), _payLoad.pointer(), classMetadata, _payLoad);
            
        }
    }
}