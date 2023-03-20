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
package com.db4o.reflect.generic;

import com.db4o.foundation.*;
import com.db4o.reflect.*;

/**
 * @exclude
 */
public class GenericClass implements ReflectClass, DeepClone {

    private static final GenericField[] NO_FIELDS = new GenericField[0];
    
    private final GenericReflector _reflector;
    private final ReflectClass _delegate;
    
    private final String _name;
    private GenericClass _superclass;
    
    private GenericClass _array;
    
    private boolean _isPrimitive;
    
    private int _isCollection;
    
    protected GenericConverter _converter;
    
    private GenericField[] _fields = NO_FIELDS;
    
    private int _declaredFieldCount = -1;
    private int _fieldCount = -1;
    
    private final int _hashCode;
    

    public GenericClass(GenericReflector reflector, ReflectClass delegateClass, String name, GenericClass superclass) {
        _reflector = reflector;
        _delegate = delegateClass;
        _name = name;
        _superclass = superclass;
        _hashCode = _name.hashCode();
    }
    
    public GenericClass arrayClass(){
        if(_array != null){
            return _array;
        }
        _array = new GenericArrayClass(_reflector, this, _name, _superclass);
        return _array;
    }

    public Object deepClone(Object obj) {
        GenericReflector reflector = (GenericReflector)obj;
        GenericClass superClass = null;
        if(_superclass != null){
            _superclass = (GenericClass)reflector.forName(_superclass.getName());
        }
        GenericClass ret = new GenericClass(reflector, _delegate, _name, superClass);
        GenericField[] fields = new GenericField[_fields.length];
        for (int i = 0; i < fields.length; i++) {
            fields[i] = (GenericField)_fields[i].deepClone(reflector);
        }
        ret.initFields(fields);
        return ret;
    }
    
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(obj == null || getClass() != obj.getClass() ){
            return false;
        }
        GenericClass otherGC = (GenericClass)obj;
        if(_hashCode != otherGC.hashCode()){
            return false;
        }
        return _name.equals(otherGC._name);
    }
    
    public ReflectClass getComponentType() {
        if(_delegate != null){
            return _delegate.getComponentType();
        }
        return null;
    }

    // TODO: consider that classes may have two fields of
    // the same name after refactoring.

    public ReflectField getDeclaredField(String name) {
        if(_delegate != null){
            return _delegate.getDeclaredField(name);
        }
        for (int i = 0; i < _fields.length; i++) {
            if (_fields[i].getName().equals(name)) {
                return _fields[i];
            }
        }
        return null;
    }

    public ReflectField[] getDeclaredFields() {
        if(_delegate != null){
            return _delegate.getDeclaredFields();
        }
        return _fields;
    }
    
    public ReflectClass getDelegate(){
    	if(_delegate != null){
    		return _delegate;
    	}
        return this;
    }
    
    int getFieldCount() {
    	if(_fieldCount != -1) {
    		return _fieldCount;
    	}
    	_fieldCount = 0;
    	if(_superclass != null) {
    		_fieldCount = _superclass.getFieldCount();
    	}
    	if(_declaredFieldCount == -1) {
    		_declaredFieldCount = getDeclaredFields().length; 
    	}
    	_fieldCount += _declaredFieldCount;
    	return _fieldCount;
    }
    
    public ReflectMethod getMethod(String methodName, ReflectClass[] paramClasses) {
        if(_delegate != null){
            return _delegate.getMethod(methodName, paramClasses);
        }
        return null;
    }

    public String getName() {
        return _name;
    }

    public ReflectClass getSuperclass() {
        if(_superclass != null){
            return _superclass;
        }
        if(_delegate == null){
            return _reflector.forClass(Object.class);
        }
        ReflectClass delegateSuperclass = _delegate.getSuperclass();
        if(delegateSuperclass != null){
            _superclass = _reflector.ensureDelegate(delegateSuperclass);
        }
        return _superclass;
    }
    
    public int hashCode() {
        return _hashCode;
    }

	public void initFields(GenericField[] fields) {
		int startIndex = 0;
		if(_superclass != null) {
			startIndex = _superclass.getFieldCount();
		}
		_fields = fields;
		for (int i = 0; i < _fields.length; i++) {
		    _fields[i].setIndex(startIndex + i);
		}
	}

	 // TODO: Consider: Will this method still be necessary 
	// once constructor logic is pushed into the reflectors?
    public boolean isAbstract() { 
        if(_delegate != null){
            return _delegate.isAbstract();
        }
        return false;
    }

    public boolean isArray() {
        if(_delegate != null){
            return _delegate.isArray();
        }
        return false;
    }

    public boolean isAssignableFrom(ReflectClass subclassCandidate) {
    	if(subclassCandidate == null){
    		return false;
    	}
        if (equals(subclassCandidate)) {
            return true;
        }
        if(_delegate != null){
        	if( subclassCandidate instanceof GenericClass){
        		subclassCandidate = ((GenericClass)subclassCandidate).getDelegate();
        	}
            return _delegate.isAssignableFrom(subclassCandidate);
        }
        if (!(subclassCandidate instanceof GenericClass)) {
        	return false;
        }
        return isAssignableFrom(subclassCandidate.getSuperclass());
    }

	public boolean isCollection() {
        if(_isCollection == 1){
            return true;
        }
        if(_isCollection == -1){
            return false;
        }
        _isCollection = _reflector.isCollection(this) ? 1 : -1;
		return isCollection();
	}
	
    public boolean isInstance(Object candidate) {
        if(_delegate != null){
            return _delegate.isInstance(candidate);
        }
        if (!(candidate instanceof GenericObject)) {
        	return false;
        }
        return isAssignableFrom(((GenericObject)candidate)._class);
    }

    public boolean isInterface() {
        if(_delegate != null){
            return _delegate.isInterface();
        }
        return false;
    }

    public boolean isPrimitive() {
        if(_delegate != null){
            return _delegate.isPrimitive();
        }
        return _isPrimitive;
    }
    
    public Object newInstance() {
        if(_delegate != null){
            return _delegate.newInstance();
        }
        return new GenericObject(this);
    }

    public Reflector reflector() {
        if(_delegate != null){
            return _delegate.reflector();
        }
        return _reflector;
    }
    
    void setConverter (GenericConverter converter) {
    	_converter = converter;
    }
    
    void setDeclaredFieldCount(int count) {
    	_declaredFieldCount = count;
    }
    
    void setPrimitive() {
    	_isPrimitive = true;
    }
    
    public String toString(){
        return "GenericClass " + _name; 
    }
    
    public String toString(Object obj) {
    	if(_converter == null) {
    		return "(G) " + getName();
    	}
    	return _converter.toString((GenericObject) obj);
    }

	public boolean ensureCanBeInstantiated() {
		if(_delegate != null) {
			return _delegate.ensureCanBeInstantiated();
		}
		return true;
	}

	public Object nullValue() {
		if(_delegate == null) {
			return null;
		}
		return _delegate.nullValue();
	}

	public boolean isImmutable() {
        if(_delegate != null){
            return _delegate.isImmutable();
        }
		return isPrimitive();
	}

}
