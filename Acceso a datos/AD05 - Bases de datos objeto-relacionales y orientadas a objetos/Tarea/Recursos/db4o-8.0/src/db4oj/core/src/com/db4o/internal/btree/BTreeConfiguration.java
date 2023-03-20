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
package com.db4o.internal.btree;

import com.db4o.internal.ids.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public class BTreeConfiguration {
	
	public static final BTreeConfiguration DEFAULT = new BTreeConfiguration(null, 20, true);
	
	public final TransactionalIdSystem _idSystem;

	public final SlotChangeFactory _slotChangeFactory;
	
	public final boolean _canEnlistWithTransaction;

	public final int _cacheSize;

	public BTreeConfiguration(TransactionalIdSystem idSystem, SlotChangeFactory slotChangeFactory, int cacheSize, boolean canEnlistWithTransaction) {
		_idSystem = idSystem;
		_slotChangeFactory = slotChangeFactory;
		_canEnlistWithTransaction = canEnlistWithTransaction;
		_cacheSize = cacheSize;
	}

	public BTreeConfiguration(TransactionalIdSystem idSystem, int cacheSize, boolean canEnlistWithTransaction){
		this(idSystem, SlotChangeFactory.SYSTEM_OBJECTS, cacheSize, canEnlistWithTransaction);
	}

	
}
