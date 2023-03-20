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

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public abstract class AbstractReadContext extends AbstractBufferContext implements InternalReadContext {
    
    protected ActivationDepth _activationDepth = UnknownActivationDepth.INSTANCE;
    
    private boolean _lastReferenceReadWasReallyNull = false;
    
    protected AbstractReadContext(Transaction transaction, ReadBuffer buffer){
    	super(transaction, buffer);
    }
    
    protected AbstractReadContext(Transaction transaction){
    	this(transaction, null);
    }
    
    public final Object read(TypeHandler4 handlerType) {
        return readObject(handlerType);
    }
    
    public final Object readObject(TypeHandler4 handlerType) {
    	if (null == handlerType) {
    		throw new ArgumentNullException();
    	}
        final TypeHandler4 handler = HandlerRegistry.correctHandlerVersion(this, handlerType);
        return slotFormat().doWithSlotIndirection(this, handler, new Closure4() {
            public Object run() {
                return readAtCurrentSeekPosition(handler);
            }
        
        });
    }
    
    public Object readAtCurrentSeekPosition(TypeHandler4 handler){
        if(Handlers4.useDedicatedSlot(this, handler)){
            return readObject();
        }
        return Handlers4.readValueType(this, handler);
    }
    
	public final Object readObject() {
        int objectId = readInt();
        if (objectId == 0) {
        	_lastReferenceReadWasReallyNull = true;
        	return null;
        }
    	_lastReferenceReadWasReallyNull = false;
        
    	if(objectId == Const4.INVALID_OBJECT_ID) {
    		return null;
    	}
    	
        final ClassMetadata classMetadata = classMetadataForObjectId(objectId);
        if (null == classMetadata) {
        	// TODO: throw here
        	return null;
        }
        
		ActivationDepth depth = activationDepth().descend(classMetadata);
        if (peekPersisted()) {
            return container().peekPersisted(transaction(), objectId, depth, false);
        }

        Object obj = container().getByID2(transaction(), objectId);
        if (null == obj) {
        	return null;
        }

        // this is OK for boxed value types. They will not be added
        // to the list, since they will not be found in the ID tree.
        container().stillToActivate(container().activationContextFor(transaction(), obj, depth));

        return obj;
    }

    private ClassMetadata classMetadataForObjectId(int objectId) {
        
        // TODO: This method is *very* costly as is, since it reads
        //       the whole slot once and doesn't reuse it. Optimize.
        
    	HardObjectReference hardRef = container().getHardObjectReferenceById(transaction(), objectId);
    	if (null == hardRef || hardRef._reference == null) {
    		// com.db4o.db4ounit.common.querying.CascadeDeleteDeleted
    		return null;
    	}
		return hardRef._reference.classMetadata();
	}

	protected boolean peekPersisted() {
        return false;
    }
    
    public ActivationDepth activationDepth() {
        return _activationDepth;
    }
    
    public void activationDepth(ActivationDepth depth){
        _activationDepth = depth;
    }
    
    public ReadWriteBuffer readIndirectedBuffer() {
        int address = readInt();
        int length = readInt();
        if(address == 0){
            return null;
        }
        return container().decryptedBufferByAddress(address, length);
    }

    public boolean lastReferenceReadWasReallyNull() {
    	return _lastReferenceReadWasReallyNull;
    }
    
    public void notifyNullReferenceSkipped() {
    	_lastReferenceReadWasReallyNull = true;
    }
}
