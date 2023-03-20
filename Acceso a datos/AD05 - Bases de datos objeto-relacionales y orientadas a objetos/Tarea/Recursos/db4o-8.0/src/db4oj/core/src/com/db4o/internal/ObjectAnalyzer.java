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

import com.db4o.reflect.*;


/**
 * @exclude
 */
class ObjectAnalyzer {
	
    private final ObjectContainerBase _container;
    
    private final Object _obj;
    
    private ClassMetadata _classMetadata;
    
    private ObjectReference _ref;
    
    private boolean _notStorable;
    
    ObjectAnalyzer(ObjectContainerBase container, Object obj){
        _container = container;
        _obj = obj;
    }
    
    void analyze(Transaction trans){
        _ref = trans.referenceForObject(_obj);
        if (_ref != null) {
        	_classMetadata = _ref.classMetadata();
        	return;
        }
        
        ReflectClass claxx = _container.reflector().forObject(_obj);
        if(claxx == null){
            notStorable(_obj, claxx);
            return;
        }
        if(!detectClassMetadata(trans, claxx)){
            return;
        }
        if (isValueType(_classMetadata) ) {
        	notStorable(_obj, _classMetadata.classReflector());
        }
    }

    private boolean detectClassMetadata(Transaction trans, ReflectClass claxx) {
        _classMetadata = _container.getActiveClassMetadata(claxx);
        if (_classMetadata != null) {
        	if (!_classMetadata.isStorable()) {
        		notStorable(_obj, claxx);
        		return false;
        	}
        	return true;
        }
        	
        _classMetadata = _container.produceClassMetadata(claxx);
        if ( _classMetadata == null
        	|| !_classMetadata.isStorable()){
            notStorable(_obj, claxx);
            return false;
        }
        
        // The following may return a reference if the object is held
        // in a static variable somewhere ( often: Enums) that gets
        // stored or associated on initialization of the ClassMetadata.
        
        _ref = trans.referenceForObject(_obj);
        
        return true;
    }

    private void notStorable(Object obj, ReflectClass claxx) {
        _container.notStorable(claxx, obj);
        _notStorable = true;
    }
    
    boolean notStorable(){
        return _notStorable;
    }
    
    private final boolean isValueType(ClassMetadata classMetadata) {
        return classMetadata.isValueType();
    }

    ObjectReference objectReference() {
        return _ref;
    }

    public ClassMetadata classMetadata() {
        return _classMetadata;
    }

}
