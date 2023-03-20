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

import com.db4o.reflect.*;


/**
 * @exclude
 */
public class GenericArrayClass extends GenericClass {
    
    public GenericArrayClass(GenericReflector reflector, ReflectClass delegateClass, String name, GenericClass superclass) {
        super(reflector, delegateClass, name, superclass);
    }
    
    public ReflectClass getComponentType() {
        return getDelegate();
    }
    
    public boolean isArray() {
        return true;
    }
    
    public boolean isInstance(Object candidate) {
        if (!(candidate instanceof GenericArray)) {
            return false;
        }
        return isAssignableFrom(((GenericArray)candidate)._clazz);
    }
    
    public String toString(Object obj) {
    	if(_converter == null) {
    		return "(GA) " + getName();
    	}
    	return _converter.toString((GenericArray) obj);
    }
    
}
