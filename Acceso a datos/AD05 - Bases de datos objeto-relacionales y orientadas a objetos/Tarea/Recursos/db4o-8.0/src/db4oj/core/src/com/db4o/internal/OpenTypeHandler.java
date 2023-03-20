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

import com.db4o.ext.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.handlers.versions.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


public class OpenTypeHandler implements ReferenceTypeHandler, ValueTypeHandler, BuiltinTypeHandler, CascadingTypeHandler, LinkLengthAware {
    
    private static final int HASHCODE = 1003303143;
	private ObjectContainerBase _container;
    
	public OpenTypeHandler(ObjectContainerBase container){
		_container = container;
	}
	
	ObjectContainerBase container() {
		return _container;
	}
	
	public ReflectClass classReflector() {
		return container().handlers().ICLASS_OBJECT;
	}

	public void cascadeActivation(ActivationContext context){
	    Object targetObject = context.targetObject();
	    if(isPlainObject(targetObject)){
	    	return;
	    }
		TypeHandler4 typeHandler = typeHandlerForObject(targetObject);
	    Handlers4.cascadeActivation(context, typeHandler);
	}
    
	public void delete(DeleteContext context) throws Db4oIOException {
        int payLoadOffset = context.readInt();
        if(context.isLegacyHandlerVersion()){
        	context.defragmentRecommended();
        	return;
        }
        if (payLoadOffset <= 0) {
        	return;
        }
        int linkOffset = context.offset();
        context.seek(payLoadOffset);
        int classMetadataID = context.readInt();
        TypeHandler4 typeHandler = container().classMetadataForID(classMetadataID).typeHandler();
        if(typeHandler != null){
        	if(! isPlainObject(typeHandler)){
        		context.delete(typeHandler);
        	}
        }
        context.seek(linkOffset);
	}
	
	public int getID() {
		return Handlers4.UNTYPED_ID;
	}

	public boolean hasField(ObjectContainerBase a_stream, String a_path) {
		return a_stream.classCollection().fieldExists(a_path);
	}
    
	public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        int payLoadOffSet = context.readInt();
        if(payLoadOffSet == 0){
            return null;
        }
        context.seek(payLoadOffSet);
        int classMetadataID = context.readInt();
        ClassMetadata classMetadata = context.container().classMetadataForID(classMetadataID);
        if(classMetadata == null){
        	return null;
        }
    	return classMetadata.readCandidateHandler(context);
	}
    
    public ObjectID readObjectID(InternalReadContext context){
        int payloadOffset = context.readInt();
        if(payloadOffset == 0){
            return ObjectID.IS_NULL;
        }
        int savedOffset = context.offset();
        TypeHandler4 typeHandler = readTypeHandler(context, payloadOffset);
        if(typeHandler == null){
            context.seek(savedOffset);
            return ObjectID.IS_NULL;
        }
        seekSecondaryOffset(context, typeHandler);
        if(typeHandler instanceof ReadsObjectIds){
            ObjectID readObjectID = ((ReadsObjectIds)typeHandler).readObjectID(context);
            context.seek(savedOffset);
            return readObjectID;
        }
        context.seek(savedOffset);
        return ObjectID.NOT_POSSIBLE;
    }
    
    public void defragment(DefragmentContext context) {
        int payLoadOffSet = context.readInt();
        if(payLoadOffSet == 0){
            return;
        }
        int savedOffSet = context.offset();
        context.seek(payLoadOffSet);
        try{
	        int classMetadataId = context.copyIDReturnOriginalID();
			TypeHandler4 typeHandler = correctTypeHandlerVersionFor(context, classMetadataId);
			if(typeHandler == null){
				return;
			}
			seekSecondaryOffset(context, typeHandler);
			if(isPlainObject(typeHandler)){
				context.defragment(new PlainObjectHandler());
			}else{
				context.defragment(typeHandler);
			}
        }finally{
        	context.seek(savedOffSet);
        }
    }
    
	protected TypeHandler4 correctTypeHandlerVersionFor(DefragmentContext context, int classMetadataId) {
		TypeHandler4 typeHandler = context.typeHandlerForId(classMetadataId);
		if (null == typeHandler) {
			return null;
		}
		
		ClassMetadata classMetadata = container(context).classMetadataForID(classMetadataId);
		return HandlerRegistry.correctHandlerVersion(context, typeHandler, classMetadata);
	}

	protected ObjectContainerBase container(DefragmentContext context) {
		return context.transaction().container();
	}

    protected TypeHandler4 readTypeHandler(InternalReadContext context, int payloadOffset) {
        context.seek(payloadOffset);
        TypeHandler4 typeHandler = container().typeHandlerForClassMetadataID(context.readInt());
        return HandlerRegistry.correctHandlerVersion(context, typeHandler);
    }

    /**
     * @param buffer
     * @param typeHandler
     */
    protected void seekSecondaryOffset(ReadBuffer buffer, TypeHandler4 typeHandler) {
        // do nothing, no longer needed in current implementation.
    }

    public Object read(ReadContext readContext) {
        InternalReadContext context = (InternalReadContext) readContext;
        int payloadOffset = context.readInt();
        if(payloadOffset == 0){
			context.notifyNullReferenceSkipped();
            return null;
        }
        int savedOffSet = context.offset();
        try{
	        TypeHandler4 typeHandler = readTypeHandler(context, payloadOffset);
	        if(typeHandler == null){
	            return null;
	        }

	        seekSecondaryOffset(context, typeHandler);
	        if(isPlainObject(typeHandler)){
	        	return context.readAtCurrentSeekPosition(new PlainObjectHandler());
	        }
	        return context.readAtCurrentSeekPosition(typeHandler);
        } finally{
        	context.seek(savedOffSet);
        }
    }
    
    public void activate(ReferenceActivationContext context) {
//    	throw new IllegalStateException();
    }

    public void collectIDs(final QueryingReadContext readContext) {
        InternalReadContext context = (InternalReadContext) readContext;
        int payloadOffset = context.readInt();
        if(payloadOffset == 0){
            return;
        }
        int savedOffSet = context.offset();
        try {
	        TypeHandler4 typeHandler = readTypeHandler(context, payloadOffset);
	        if(typeHandler == null){
	            return;
	        }
	        seekSecondaryOffset(context, typeHandler);
	        if (isPlainObject(typeHandler)) {
	        	readContext.collector().addId(readContext.readInt());
	        	return;
	        }
	        
	        CollectIdContext collectIdContext = new CollectIdContext(readContext.transaction(), readContext.collector(), null, readContext.buffer()) {
	        	@Override
	        	public int handlerVersion() {
	        		return readContext.handlerVersion();
	        	}
	        	
	        	@Override
	        	public SlotFormat slotFormat() {
	        		return new SlotFormatCurrent() {
	        			@Override
	        			public boolean isIndirectedWithinSlot(TypeHandler4 handler) {
	        				return false;
	        			}
	        		};
	        	}
	        };
	        Handlers4.collectIdsInternal(collectIdContext, context.container().handlers().correctHandlerVersion(typeHandler, context.handlerVersion()), 0, false);
        } finally {
        	context.seek(savedOffSet);
        }
    }
    
    public TypeHandler4 readTypeHandlerRestoreOffset(InternalReadContext context){
        int savedOffset = context.offset();
        int payloadOffset = context.readInt();
        TypeHandler4 typeHandler = payloadOffset == 0 ? null : readTypeHandler(context, payloadOffset);  
        context.seek(savedOffset);
        return typeHandler;
    }
      
	public void write(WriteContext context, Object obj) {
	    if(obj == null) {
            context.writeInt(0);
            return;
        }
        
        MarshallingContext marshallingContext = (MarshallingContext) context;
        ClassMetadata classMetadata = classMetadataFor(obj);
        if (classMetadata == null) {
        	context.writeInt(0);
        	return;
        }
        
        MarshallingContextState state = marshallingContext.currentState();
        
        marshallingContext.createChildBuffer(false);
        
        context.writeInt(classMetadata.getID());
        writeObject(context, classMetadata.typeHandler(), obj);
        
        marshallingContext.restoreState(state);
    }

	private ClassMetadata classMetadataFor(Object obj) {
		return container().classMetadataForObject(obj);
	}

	private void writeObject(WriteContext context, TypeHandler4 typeHandler, Object obj) {
		if(isPlainObject(obj)){
			context.writeObject(new PlainObjectHandler(), obj);
			return;
		}
        if(Handlers4.useDedicatedSlot(context, typeHandler)){
            context.writeObject(obj);
        }else {
            typeHandler.write(context, obj);
        }
    }

    private boolean isPlainObject(Object obj) {
    	if(obj == null){
    		return false;
    	}
		return obj.getClass() == Const4.CLASS_OBJECT;
	}
    
    public static boolean isPlainObject(TypeHandler4 typeHandler) {
		return typeHandler.getClass() == OpenTypeHandler.class
			|| typeHandler.getClass() == OpenTypeHandler0.class
			|| typeHandler.getClass() == OpenTypeHandler2.class
			|| typeHandler.getClass() == OpenTypeHandler7.class;
	}

	public TypeHandler4 typeHandlerForObject(Object obj) {
        return classMetadataFor(obj).typeHandler();
    }
    
    public boolean equals(Object obj) {
    	return obj instanceof OpenTypeHandler
    		&& !(obj instanceof InterfaceTypeHandler);
    }
    
    public int hashCode() {
        return HASHCODE;
    }

	public void registerReflector(Reflector reflector) {
		// nothing to do
	}

	public int linkLength() {
		return Const4.ID_LENGTH;
	}
	
}
