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
import com.db4o.foundation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.array.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.metadata.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public class PrimitiveTypeMetadata extends ClassMetadata {
    
    private static final int HASHCODE_FOR_NULL = 283636383;
    
    public PrimitiveTypeMetadata(ObjectContainerBase container, TypeHandler4 handler, int id, ReflectClass classReflector) {
    	super(container, classReflector);
        _aspects = FieldMetadata.EMPTY_ARRAY;
        _typeHandler = handler;
        _id = id;
    }
    
    public PrimitiveTypeMetadata(ObjectContainerBase container){
        super(container);
        _typeHandler = null;
    }

    @Override
    public void cascadeActivation(ActivationContext context) {
        // Override
        // do nothing
    }

    @Override
    final void addToIndex(Transaction trans, int id) {
        // Override
        // Primitive Indices will be created later.
    }

    @Override
    boolean allowsQueries() {
        return false;
    }

    @Override
    void cacheDirty(Collection4 col) {
        // do nothing
    }
    
    @Override
    public boolean descendOnCascadingActivation() {
        return false;
    }

    @Override
    public void delete(DeleteContext context) throws Db4oIOException {
    	if(context.isLegacyHandlerVersion()){
    		context.readInt();
    		context.defragmentRecommended();
    	}
    }
    
    @Override
    void deleteMembers(DeleteContextImpl context, ArrayType arrayType, boolean isUpdate) {
        if (arrayType == ArrayType.PLAIN_ARRAY) {
            new ArrayHandler(typeHandler(), true).deletePrimitiveEmbedded((StatefulBuffer) context.buffer(), this);
        } else if (arrayType == ArrayType.MULTIDIMENSIONAL_ARRAY) {
            new MultidimensionalArrayHandler(typeHandler(), true).deletePrimitiveEmbedded((StatefulBuffer) context.buffer(), this);
        }
    }
    
	@Override
	public boolean hasClassIndex() {
	    return false;
	}

	@Override
    public Object instantiate(UnmarshallingContext context) {
        Object obj = context.persistentObject();
        if (obj == null) {
            obj = context.read(typeHandler());
            context.setObjectWeak(obj);
        }
        context.setStateClean();
        return obj;
    }
    
	@Override
    public Object instantiateTransient(UnmarshallingContext context) {
        return Handlers4.readValueType(context, correctHandlerVersion(context));
    }

	@Override
    void instantiateFields(UnmarshallingContext context) {
       throw new NotImplementedException();
    }

	@Override
    public boolean isArray() {
        return _id == Handlers4.ANY_ARRAY_ID || _id == Handlers4.ANY_ARRAY_N_ID;
    }
    
	@Override
    public boolean hasIdentity(){
        return false;
    }
    
	@Override
	public boolean isStronglyTyped(){
		return false;
	}
    
	@Override
    public PreparedComparison prepareComparison(Context context, Object source) {
    	return Handlers4.prepareComparisonFor(typeHandler(), context, source);
    }
	
	@Override
    public TypeHandler4 readCandidateHandler(QueryingReadContext context) {
        if (isArray()) {
            return typeHandler();
        }
        return null;
    }
    
//	@Override
//    public ObjectID readObjectID(InternalReadContext context){
//        if(_handler instanceof ClassMetadata){
//            return ((ClassMetadata)_handler).readObjectID(context);
//        }
//        if(Handlers4.handlesArray(_handler)){
//            // TODO: Here we should theoretically read through the array and collect candidates.
//            // The respective construct is wild: "Contains query through an array in an array."
//            // Ignore for now.
//            return ObjectID.IGNORE;
//        }
//        return ObjectID.NOT_POSSIBLE;
//    }
    
	@Override
    void removeFromIndex(Transaction ta, int id) {
        // do nothing
    }
    
	@Override
    public final boolean writeObjectBegin() {
        return false;
    }
    
	@Override
    public String toString(){
        return getClass().getName() + "(" + typeHandler() + ")";
    }

	@Override
    public void defragment(DefragmentContext context) {
    	correctHandlerVersion(context).defragment(context);
    }
    
	@Override
    public Object wrapWithTransactionContext(Transaction transaction, Object value) {
        return value;
    }
    
    @Override
    public TypeHandler4 delegateTypeHandler(Context context){
        return typeHandler();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof PrimitiveTypeMetadata)){
            return false;
        }
        PrimitiveTypeMetadata other = (PrimitiveTypeMetadata) obj;
        if(typeHandler() == null){
            return other.typeHandler() == null;
        }
        return typeHandler().equals(other.typeHandler());
    }
    
    @Override
    public int hashCode() {
        if(typeHandler() == null){
            return HASHCODE_FOR_NULL;
        }
        return typeHandler().hashCode();
    }
    
    public Object deepClone(Object context) {
    	throw new IllegalStateException();
    }
    
    protected AspectTraversalStrategy detectAspectTraversalStrategy() {
    	return new AspectTraversalStrategy() {
			public void traverseAllAspects(TraverseAspectCommand command) {
				// do nothing
			}
		};
    }
}
