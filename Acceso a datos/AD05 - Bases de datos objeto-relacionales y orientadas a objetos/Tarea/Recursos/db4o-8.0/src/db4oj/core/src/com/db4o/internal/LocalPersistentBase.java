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
package com.db4o.internal;

import com.db4o.*;
import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public abstract class LocalPersistentBase extends PersistentBase {
	
	private final TransactionalIdSystem _idSystem;
	
	public LocalPersistentBase(TransactionalIdSystem idSystem){
		_idSystem = idSystem;
	}
	
	public LocalPersistentBase(){
		this(null);
	}
	
	public TransactionalIdSystem idSystem(Transaction trans) {
		if(_idSystem != null){
			return _idSystem;
		}
		return super.idSystem(trans);
	}
	
	@Override
	protected ByteArrayBuffer readBufferById(Transaction trans) {
		Slot slot = idSystem(trans).currentSlot(getID());
		if(DTrace.enabled){
			DTrace.SLOT_READ.logLength(getID(), slot);
		}
		return ((LocalObjectContainer)trans.container()).readBufferBySlot(slot);
	}

}
