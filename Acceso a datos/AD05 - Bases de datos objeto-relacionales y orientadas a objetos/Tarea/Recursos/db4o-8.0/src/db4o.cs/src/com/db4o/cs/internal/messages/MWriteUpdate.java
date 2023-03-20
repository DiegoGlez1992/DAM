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
import com.db4o.internal.activation.*;
import com.db4o.internal.slots.*;

public final class MWriteUpdate extends MsgObject implements ServerSideMessage {
	
	public final void processAtServer() {
	    int classMetadataID = _payLoad.readInt();
	    int arrayTypeValue = _payLoad.readInt();
	    ArrayType arrayType = ArrayType.forValue(arrayTypeValue);
	    unmarshall(_payLoad._offset);
	    synchronized(containerLock()){
	        ClassMetadata classMetadata = localContainer().classMetadataForID(classMetadataID);
			int id = _payLoad.getID();
			transaction().dontDelete(id);
			
			Slot clientSlot = _payLoad.slot();
			Slot newSlot = null;
			
			if(clientSlot.isUpdate()){
				transaction().writeUpdateAdjustIndexes(id, classMetadata, arrayType);
	            newSlot = localContainer().allocateSlotForUserObjectUpdate(_payLoad.transaction(), _payLoad.getID(), _payLoad.length());
			} else if(clientSlot.isNew()){
				// Just one known usecase for this one: For updating plain objects from old versions, since
				// they didnt't have own slots that could be freed.
				// Logic that got us here in OpenTypeHandler7#addReference()#writeUpdate()
				newSlot = localContainer().allocateSlotForNewUserObject(_payLoad.transaction(), _payLoad.getID(), _payLoad.length());
			} else {
				throw new IllegalStateException();
			}
            
			_payLoad.address(newSlot.address());
			classMetadata.addFieldIndices(_payLoad);
            _payLoad.writeEncrypt();
            deactivateCacheFor(id);            
		}
	}

	private void deactivateCacheFor(int id) {
		ObjectReference reference = transaction().referenceForId(id);
		if (null == reference) {
			return;
		}
		reference.deactivate(transaction(), new FixedActivationDepth(1));
	}
}