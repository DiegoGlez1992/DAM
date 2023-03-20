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
package com.db4o.internal.fileheader;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.internal.slots.*;

/**
 * @exclude
 */
public abstract class FileHeaderVariablePart {
	
    protected final LocalObjectContainer _container;

	public abstract Runnable commit(boolean shuttingDown);

	public abstract void read(int variablePartAddress, int variablePartLength);
	
	protected FileHeaderVariablePart(LocalObjectContainer container){
		_container = container;
	}
	
    public final byte getIdentifier() {
        return Const4.HEADER;
    }
    
	protected final SystemData systemData() {
		return _container.systemData();
	}
	
	protected final Slot allocateSlot(int length) {
		Slot reusedSlot = _container.freespaceManager().allocateSafeSlot(length);
		if(reusedSlot != null){
			return reusedSlot;
		}
		return _container.appendBytes(length);
	}
	
    public void readIdentity(LocalTransaction trans) {
        LocalObjectContainer file = trans.localContainer();
        Db4oDatabase identity = Debug4.staticIdentity ? 
        		Db4oDatabase.STATIC_IDENTITY : 
        		(Db4oDatabase) file.getByID(trans, systemData().identityId());
        if (null != identity) {
        	file.activate(trans, identity, new FixedActivationDepth(2));
        	systemData().identity(identity);
        } else{
        	// TODO: What now?
        	// Apparently we get this state after defragment
        	// and defragment then sets the identity.
        	// If we blindly generate a new identity here,
        	// ObjectUpdateFileSizeTestCase reports trouble.
        }
    }
    
    public abstract int marshalledLength();

}
