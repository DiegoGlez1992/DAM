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
package com.db4o.internal.marshall;

import com.db4o.internal.*;
import com.db4o.marshall.*;

/**
 * @exclude
 */
public class ObjectHeaderContext extends AbstractReadContext implements MarshallingInfo, HandlerVersionContext{
    
    protected ObjectHeader _objectHeader;
    
	private int _declaredAspectCount;
    
    public ObjectHeaderContext(Transaction transaction, ReadBuffer buffer, ObjectHeader objectHeader) {
        super(transaction, buffer);
        _objectHeader = objectHeader;
    }
    
    public final ObjectHeaderAttributes headerAttributes(){
        return _objectHeader._headerAttributes;
    }

    public final boolean isNull(int fieldIndex) {
        return headerAttributes().isNull(fieldIndex);
    }

    public int handlerVersion() {
        return _objectHeader.handlerVersion();
    }
    
    public void beginSlot() {
        // do nothing
    }
    
    public ContextState saveState(){
        return new ContextState(offset());
    }
    
    public void restoreState(ContextState state){
        seek(state._offset);
    }

	public ClassMetadata classMetadata(){
	    return _objectHeader.classMetadata(); 
	}
	
	public int declaredAspectCount() {
		return _declaredAspectCount;
	}

	public void declaredAspectCount(int count) {
		_declaredAspectCount = count;
	}

}
