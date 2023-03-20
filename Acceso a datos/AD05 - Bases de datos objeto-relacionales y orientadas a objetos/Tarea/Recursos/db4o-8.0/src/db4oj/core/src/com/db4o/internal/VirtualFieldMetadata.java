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

import com.db4o.foundation.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.query.processor.*;
import com.db4o.internal.replication.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * TODO: refactor for symmetric inheritance - don't inherit from YapField and override,
 * instead extract an abstract superclass from YapField and let both YapField and this class implement
 * 
 * @exclude
 */
public abstract class VirtualFieldMetadata extends FieldMetadata {
    
    private static final Object ANY_OBJECT = new Object();
    
    private ReflectClass _classReflector;

	private BuiltinTypeHandler _handler;

    VirtualFieldMetadata(int fieldTypeID, BuiltinTypeHandler handler) {
        super(fieldTypeID);
        _handler = handler;
    }
    
    @Override
    public TypeHandler4 getHandler() {
    	return _handler;
    }
    
    public abstract void addFieldIndex(ObjectIdContextImpl context)  throws FieldIndexException ;
    
    public boolean alive() {
        return true;
    }
    
    boolean canAddToQuery(String fieldName){
        return fieldName.equals(getName()); 
    }
    
	public boolean canBeDisabled() {
		return false;
	}
    
    public boolean canUseNullBitmap(){
        return false;
    }
    
    public ReflectClass classReflector(Reflector reflector){
        if (_classReflector == null) {
            _classReflector = ((BuiltinTypeHandler)getHandler()).classReflector();
        }
        return _classReflector;
    }
    
    void collectConstraints(Transaction a_trans, QConObject a_parent,
        Object a_template, Visitor4 a_visitor) {
        
        // QBE constraint collection call
        // There isn't anything useful to do here, since virtual fields
        // are not on the actual object.
        
    }
    
    public void deactivate(ActivationContext context) {
        // do nothing
    }
    
    public abstract void delete(DeleteContextImpl context, boolean isUpdate);
    
    public Object getOrCreate(Transaction a_trans, Object a_OnObject) {
        // This is the first part of marshalling
        // Virtual fields do it all in #marshall(), the object is never used.
        // Returning any object here prevents triggering null handling.
        return ANY_OBJECT;
    }
    
    public boolean needsArrayAndPrimitiveInfo(){
        return false;
    }

    public void activate(UnmarshallingContext context) {
        context.objectReference().produceVirtualAttributes();
        instantiate1(context);
    }

    abstract void instantiate1(ObjectReferenceContext context);
    
    public void loadFieldTypeById(){
    	// do nothing
    }
    
    public void marshall(MarshallingContext context, Object obj){
        marshall(context.transaction(), context.reference(), context, context.isNew());
    }

    private final void marshall(
            Transaction trans,
            ObjectReference ref, 
            WriteBuffer buffer,
            boolean isNew) {
        
        if(! trans.supportsVirtualFields()){
            marshallIgnore(buffer);
            return;
        }
        
        ObjectContainerBase stream = trans.container();
        HandlerRegistry handlers = stream._handlers;
        boolean migrating = false;
        
        
        if (stream._replicationCallState == Const4.NEW) {                
            Db4oReplicationReferenceProvider provider = handlers._replicationReferenceProvider;
            Object parentObject = ref.getObject();
            Db4oReplicationReference replicationReference = provider.referenceFor(parentObject); 
            if(replicationReference != null){
                migrating = true;
                VirtualAttributes va = ref.produceVirtualAttributes();
                va.i_version = replicationReference.version();
                va.i_uuid = replicationReference.longPart();
                va.i_database = replicationReference.signaturePart();
            }
        }
        
        if (ref.virtualAttributes() == null) {
        	ref.produceVirtualAttributes();
            migrating = false;
        }
	    marshall(trans, ref, buffer, migrating, isNew);
    }
    
    abstract void marshall(Transaction trans, ObjectReference ref, WriteBuffer buffer, boolean migrating, boolean isNew);
    
    abstract void marshallIgnore(WriteBuffer writer);
    
    public void readVirtualAttribute(ObjectReferenceContext context) {
        if(! context.transaction().supportsVirtualFields()){
            incrementOffset(context, context);
            return;
        }
        instantiate1(context);
    }
    
    public boolean isVirtual() {
        return true;
    }

    protected Object indexEntryFor(Object indexEntry) {
    	return indexEntry;
    }
    
    protected Indexable4 indexHandler(ObjectContainerBase stream) {
    	return (Indexable4)getHandler();
    }
}