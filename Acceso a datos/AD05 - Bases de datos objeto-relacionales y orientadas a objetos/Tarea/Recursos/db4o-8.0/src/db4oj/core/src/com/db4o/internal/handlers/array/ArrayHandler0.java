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
package com.db4o.internal.handlers.array;

import java.io.*;

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;
import com.db4o.marshall.*;


/**
 * @exclude
 */
public class ArrayHandler0 extends ArrayHandler1 {
    
    protected ArrayVersionHelper createVersionHelper() {
        return new ArrayVersionHelper0();
    }
    
    protected void withContent(AbstractBufferContext context, Runnable runnable){
        int address = context.readInt();
        int length = context.readInt();
        if(address == 0){
            return;
        }
        ReadBuffer temp = context.buffer();
        ByteArrayBuffer indirectedBuffer = container(context).decryptedBufferByAddress(address, length);
        context.buffer(indirectedBuffer);
        runnable.run();
        context.buffer(temp);
    }

    public void delete(DeleteContext context) throws Db4oIOException {
    	context.readSlot();
    	context.defragmentRecommended();
    }

    public Object read(ReadContext readContext) {
        
        InternalReadContext context = (InternalReadContext) readContext;
        
        ByteArrayBuffer buffer = (ByteArrayBuffer) context.readIndirectedBuffer(); 
        if (buffer == null) {
            return null;
        }
        
        // With the following line we ask the context to work with 
        // a different buffer. Should this logic ever be needed by
        // a user handler, it should be implemented by using a Queue
        // in the UnmarshallingContext.
        
        // The buffer has to be set back from the outside!  See below
        ReadBuffer contextBuffer = context.buffer(buffer);
        
        Object array = super.read(context);
        
        // The context buffer has to be set back.
        context.buffer(contextBuffer);
        
        return array;
    }
    
    public static void defragment(DefragmentContext context, ArrayHandler handler) {
        int sourceAddress = context.sourceBuffer().readInt();
        int length = context.sourceBuffer().readInt();
        if(sourceAddress == 0 && length == 0) {
            context.targetBuffer().writeInt(0);
            context.targetBuffer().writeInt(0);
            return;
        }
        Slot slot = context.allocateMappedTargetSlot(sourceAddress, length);
        ByteArrayBuffer sourceBuffer = null;
        try {
            sourceBuffer = context.sourceBufferByAddress(sourceAddress, length);
        }
        catch (IOException exc) {
            throw new Db4oIOException(exc);
        }
        DefragmentContextImpl payloadContext = new DefragmentContextImpl(sourceBuffer, (DefragmentContextImpl) context);
        handler.defragmentSlot(payloadContext);
        payloadContext.writeToTarget(slot.address());
        context.targetBuffer().writeInt(slot.address());
        context.targetBuffer().writeInt(length);
    }
    
    public void defragment(DefragmentContext context) {
        defragment(context, this);
    }

}
