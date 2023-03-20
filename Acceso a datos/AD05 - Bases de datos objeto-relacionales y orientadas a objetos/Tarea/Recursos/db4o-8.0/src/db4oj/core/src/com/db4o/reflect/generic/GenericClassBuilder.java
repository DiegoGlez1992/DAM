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
public class GenericClassBuilder implements ReflectClassBuilder {

	private GenericReflector _reflector;
	private Reflector _delegate;
	
	public GenericClassBuilder(GenericReflector reflector, Reflector delegate_) {
		super();
		_reflector = reflector;
		_delegate = delegate_;
	}

	public ReflectClass createClass(String name, ReflectClass superClass, int fieldCount) {
		ReflectClass nativeClass = _delegate.forName(name);
		GenericClass clazz=new GenericClass(_reflector, nativeClass,name, (GenericClass)superClass);
		clazz.setDeclaredFieldCount(fieldCount);
		return clazz;
	}

	public ReflectField createField(
			ReflectClass parentType, 
			String fieldName,
			ReflectClass fieldType, 
			boolean isVirtual, 
			boolean isPrimitive,
			boolean isArray, boolean isNArray) {
        if (isVirtual) {
            return new GenericVirtualField(fieldName);
        }   
		return new GenericField(fieldName, fieldType, isPrimitive);
	}

	public void initFields(ReflectClass clazz, ReflectField[] fields) {
        ((GenericClass)clazz).initFields((GenericField[])fields);
	}

	public ReflectField[] fieldArray(int length) {
		return new GenericField[length];
	}
}
