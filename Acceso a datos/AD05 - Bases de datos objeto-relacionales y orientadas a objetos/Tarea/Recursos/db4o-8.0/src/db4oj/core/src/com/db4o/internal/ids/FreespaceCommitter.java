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

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.freespace.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class FreespaceCommitter {
	
	public static final FreespaceCommitter DO_NOTHING = new NullFreespaceCommitter();
	
	private final List<Slot> _freeToUserFreespaceSystem = new ArrayList<Slot>();
	
	private final List<Slot> _freeToSystemFreespaceSystem = new ArrayList<Slot>();

	private final FreespaceManager _freespaceManager;
	
	private TransactionalIdSystem _transactionalIdSystem;
	
	public FreespaceCommitter(FreespaceManager freespaceManager) {
		_freespaceManager = freespaceManager == null ? NullFreespaceManager.INSTANCE : freespaceManager;
	}
	
	public void commit() {
		apply(_freeToUserFreespaceSystem);
		_freespaceManager.beginCommit();
		
		_freespaceManager.commit();
		
		_transactionalIdSystem.accumulateFreeSlots(this, true);
		
		apply(_freeToSystemFreespaceSystem);
		_freespaceManager.endCommit();
	}

	private void apply(List<Slot> toFree) {
		for(Slot slot : toFree){
			_freespaceManager.free(slot);
		}
		toFree.clear();
	}

	public void transactionalIdSystem(TransactionalIdSystem transactionalIdSystem) {
		_transactionalIdSystem = transactionalIdSystem;
	}
	
	private static class NullFreespaceCommitter extends FreespaceCommitter {

		public NullFreespaceCommitter() {
			super(NullFreespaceManager.INSTANCE);
		}
		
		@Override
		public void commit() {
			// do nothing
		}
		
	}

	public void delayedFree(Slot slot, boolean freeToSystemFreeSpaceSystem) {
		if(freeToSystemFreeSpaceSystem){
			_freeToSystemFreespaceSystem.add(slot);
		}else {
			_freeToUserFreespaceSystem.add(slot);
		}
	}

}
