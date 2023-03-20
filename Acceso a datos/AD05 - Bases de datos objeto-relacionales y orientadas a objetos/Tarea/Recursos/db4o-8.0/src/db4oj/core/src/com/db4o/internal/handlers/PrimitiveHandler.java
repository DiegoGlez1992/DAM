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
package com.db4o.internal.handlers;

import com.db4o.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;


/**
 * @exclude
 */
public abstract class PrimitiveHandler implements ValueTypeHandler, IndexableTypeHandler, BuiltinTypeHandler, QueryableTypeHandler {
    
    protected ReflectClass _classReflector;
    
    private ReflectClass _primitiveClassReflector;
    
    private Object _primitiveNull;
    
    public Object coerce(ReflectClass claxx, Object obj) {
        return isAssignableFrom(claxx) ? obj : No4.INSTANCE;
    }

	private boolean isAssignableFrom(ReflectClass claxx) {
	    return classReflector().isAssignableFrom(claxx)
        	|| primitiveClassReflector().isAssignableFrom(claxx);
    }
    
    public abstract Object defaultValue();

    public void delete(DeleteContext context) {
    	context.seek(context.offset() + linkLength());
    }
    
    public final Object indexEntryToObject(Context context, Object indexEntry){
        return indexEntry;
    }
    
    public abstract Class primitiveJavaClass();
    
    protected Class javaClass(){
        return Platform4.nullableTypeFor(primitiveJavaClass());
    }
    
    public boolean descendsIntoMembers() {
    	return false;
    }
    
    public Object primitiveNull() {
    	if(_primitiveNull == null) {
        	ReflectClass claxx = (_primitiveClassReflector == null ? _classReflector : _primitiveClassReflector);
        	_primitiveNull = claxx.nullValue();
    	}
		return _primitiveNull;
    }

    /**
     * 
     * @param mf
     * @param buffer
     * @param redirect
     */
    public Object read(
        
        /* FIXME: Work in progress here, this signature should not be used from the outside */
        MarshallerFamily mf,
        
        
        StatefulBuffer buffer, boolean redirect) throws CorruptionException {
    	return read1(buffer);
    }

    abstract Object read1(ByteArrayBuffer reader) throws CorruptionException;

    public Object readIndexEntry(Context context, ByteArrayBuffer buffer) {
        try {
            return read1(buffer);
        } catch (CorruptionException e) {
        }
        return null;
    }
    
    public final Object readIndexEntryFromObjectSlot(MarshallerFamily mf, StatefulBuffer statefulBuffer) throws CorruptionException{
        return read(mf, statefulBuffer, true);
    }
    
    public Object readIndexEntry(ObjectIdContext context) throws CorruptionException, Db4oIOException{
        return read(context);
    }
    
    public ReflectClass classReflector(){
    	return _classReflector;  
    }
    
    public ReflectClass primitiveClassReflector(){
    	return _primitiveClassReflector;  
    }
    
    public void registerReflector(Reflector reflector) {
        _classReflector = reflector.forClass(javaClass());
        Class clazz = primitiveJavaClass();
        if(clazz != null){
            _primitiveClassReflector = reflector.forClass(clazz);
        }
    }

    public abstract void write(Object a_object, ByteArrayBuffer a_bytes);
    
    public void writeIndexEntry(Context context, ByteArrayBuffer a_writer, Object a_object) {
        if (a_object == null) {
            a_object = primitiveNull();
        }
        write(a_object, a_writer);
    }
    
    // redundant, only added to make Sun JDK 1.2's java happy :(
    public abstract int linkLength();
    
    public final void defragment(DefragmentContext context) {
    	context.incrementOffset(linkLength());
    }
    
    public void defragIndexEntry(DefragmentContextImpl context) {
    	try {
			read1(context.sourceBuffer());
			read1(context.targetBuffer());
		} catch (CorruptionException exc) {
			Exceptions4.virtualException();
		}
    }

	protected PrimitiveMarshaller primitiveMarshaller() {
		return MarshallerFamily.current()._primitive;
	}
	
    public void write(WriteContext context, Object obj) {
        throw new NotImplementedException();
    }
    
    public Object read(ReadContext context) {
        throw new NotImplementedException();
    }
    
    public Object nullRepresentationInUntypedArrays(){
        return primitiveNull();
    }
    
	public PreparedComparison prepareComparison(Context context, final Object obj) {
		if(obj == null){
			return Null.INSTANCE;
		}
		return internalPrepareComparison(obj);
	}
	
	public abstract PreparedComparison internalPrepareComparison(final Object obj);


}