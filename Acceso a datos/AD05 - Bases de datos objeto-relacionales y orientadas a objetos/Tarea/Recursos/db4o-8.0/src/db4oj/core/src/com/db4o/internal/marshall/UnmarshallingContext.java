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

import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.internal.activation.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;


/**
 * Wraps the low-level details of reading a Buffer, which in turn is a glorified byte array.
 * 
 * @exclude
 */
public class UnmarshallingContext extends ObjectReferenceContext implements HandlerVersionContext, ReferenceActivationContext {
    
    private Object _object;
    
    private int _addToIDTree;
    
    private boolean _checkIDTree;
    
    public UnmarshallingContext(Transaction transaction, ByteArrayBuffer buffer, ObjectReference ref, int addToIDTree, boolean checkIDTree) {
        super(transaction, buffer, null, ref);
        _addToIDTree = addToIDTree;
        _checkIDTree = checkIDTree;
    }
    
    public UnmarshallingContext(Transaction transaction, ObjectReference ref, int addToIDTree, boolean checkIDTree) {
        this(transaction, null, ref, addToIDTree, checkIDTree);
    }
    
    public Object read(){
        if(! beginProcessing()){
            return _object;
        }
        
        readBuffer(objectId());
        
        if(buffer() == null){
            endProcessing();
            return _object;
        }
        
        ClassMetadata classMetadata = readObjectHeader();
        if(classMetadata == null){
        	invalidSlot();
            endProcessing();
            return _object;
        }
        
        _reference.classMetadata(classMetadata);
        
        adjustActivationDepth();
        
        if(_checkIDTree){
            Object objectInCacheFromClassCreation = transaction().objectForIdFromCache(objectId());
            if(objectInCacheFromClassCreation != null){
                _object = objectInCacheFromClassCreation;
                endProcessing();
                return _object;
            }
        }
        
        if(peekPersisted()){
            _object = classMetadata().instantiateTransient(this);
        }else{
            _object = classMetadata().instantiate(this);
        }
        
        endProcessing();
        return _object;
    }
    
    private void invalidSlot() {
		if(container().config().recoveryMode()){
			return;
		}
		throw new InvalidSlotException("id: " + objectId());
	}

	private void adjustActivationDepth() {
		if (UnknownActivationDepth.INSTANCE == _activationDepth) {
			_activationDepth = container().defaultActivationDepth(classMetadata());
        }
	}
    
    private ActivationDepthProvider activationDepthProvider() {
    	return container().activationDepthProvider();
	}
    
	public Object readFullyActivatedObjectForKeys(TypeHandler4 handler) {
		Object obj = readObject(handler);
		if(obj == null){
			return obj;
		}
		ActivationDepth activationDepth = activationDepthProvider().activationDepth(Integer.MAX_VALUE, ActivationMode.ACTIVATE);
		container().activate(transaction(), obj, activationDepth);
		return obj;
	}

	public Object readFieldValue (FieldMetadata field){
        readBuffer(objectId());
        if(buffer() == null){
            return null;
        }
        ClassMetadata classMetadata = readObjectHeader(); 
        if(classMetadata == null){
            return null;
        }
        return readFieldValue(classMetadata, field);
    }
	
	private Object readFieldValue(ClassMetadata classMetadata, FieldMetadata field) {
		if(! classMetadata.seekToField(this, field)){
	        return null;
	    }
	   	return field.read(this);
	}

	private ClassMetadata readObjectHeader() {
        _objectHeader = new ObjectHeader(container(), byteArrayBuffer());
        ClassMetadata classMetadata = _objectHeader.classMetadata();
        if(classMetadata == null){
            return null;
        }
        return classMetadata;
    }

    private void readBuffer(int id) {
        if (buffer() == null && id > 0) {
            buffer(container().readBufferById(transaction(), id)); 
        }
    }
    
    private boolean beginProcessing() {
        return _reference.beginProcessing();
    }
    
    private void endProcessing() {
        _reference.endProcessing();
    }

    public void setStateClean() {
        _reference.setStateClean();
    }

    public Object persistentObject() {
        return _object;
    }

    public void setObjectWeak(Object obj) {
        _reference.setObjectWeak(container(), obj);
    }

    protected boolean peekPersisted() {
        return _addToIDTree == Const4.TRANSIENT;
    }
    
    public Config4Class classConfig() {
        return classMetadata().config();
    }

    public void persistentObject(Object obj) {
        _object = obj;
    }

    
}

