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
package com.db4o.internal.slots;

import com.db4o.foundation.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.ids.*;

/**
 * @exclude
 */
public class IdSystemSlotChange extends SystemSlotChange {
	
	private Collection4 _freed;

	public IdSystemSlotChange(int id) {
		super(id);
	}
	
	@Override
	protected void free(FreespaceManager freespaceManager, Slot slot) {
		if(slot.isNull()){
			return;
		}
		if(_freed == null){
			_freed = new Collection4();
		}
		_freed.add(slot);
	}
	
	@Override
	public void accumulateFreeSlot(TransactionalIdSystemImpl idSystem, FreespaceCommitter freespaceCommitter, boolean forFreespace) {
        if( forFreespace() != forFreespace){
        	return;
        }
		super.accumulateFreeSlot(idSystem, freespaceCommitter, forFreespace);
		if(_freed == null){
			return;
		}
		Iterator4 iterator = _freed.iterator();
		while(iterator.moveNext()){
			freespaceCommitter.delayedFree((Slot) iterator.current(), freeToSystemFreespaceSystem());
		}
	}
	
	protected boolean freeToSystemFreespaceSystem(){
		return true;
	}

}
