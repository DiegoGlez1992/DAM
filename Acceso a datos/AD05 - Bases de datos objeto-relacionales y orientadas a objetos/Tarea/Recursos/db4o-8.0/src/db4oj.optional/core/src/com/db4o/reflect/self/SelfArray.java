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
package com.db4o.reflect.self;

import com.db4o.reflect.*;

public class SelfArray implements ReflectArray {
    
	private final SelfReflectionRegistry _registry;

    /** @param reflector */
	SelfArray(Reflector reflector,SelfReflectionRegistry registry) {
		_registry=registry;
	}
	
    public void analyze(Object obj, ArrayInfo info) {
        // do nothing
    }

	public int[] dimensions(Object arr) {
		return new int[]{getLength(arr)};
	}

	public int flatten(Object a_shaped, int[] a_dimensions,
			int a_currentDimension, Object[] a_flat, int a_flatElement) {
		if(a_shaped instanceof Object[]) {
			Object[] shaped=(Object[])a_shaped;
			System.arraycopy(shaped, 0, a_flat, 0, shaped.length);
			return shaped.length;
		}
		return _registry.flattenArray(a_shaped,a_flat);
	}

	public Object get(Object onArray, int index) {
		if(onArray instanceof Object[]) {
			return ((Object[])onArray)[index];
		}
		return _registry.getArray(onArray,index);
	}

	public ReflectClass getComponentType(ReflectClass a_class) {
		return ((SelfClass)a_class).getComponentType();
	}

	public int getLength(Object array) {
		if(array instanceof Object[]) {
			return ((Object[])array).length;
		}
		return _registry.arrayLength(array);
	}

	public boolean isNDimensional(ReflectClass a_class) {
		return false;
	}
	
    public Object newInstance(ReflectClass componentType, ArrayInfo info) {
        // TODO: implement multidimensional arrays.
        int length = info.elementCount();
        return newInstance(componentType, length);
    }

	public Object newInstance(ReflectClass componentType, int length) {
		return _registry.arrayFor(((SelfClass)componentType).getJavaClass(),length);
	}

	public Object newInstance(ReflectClass componentType, int[] dimensions) {
		return newInstance(componentType,dimensions[0]);
	}

	public void set(Object onArray, int index, Object element) {
		if(onArray instanceof Object[]) {
			((Object[])onArray)[index]=element;
			return;
		}
		_registry.setArray(onArray,index,element);
	}

	public int shape(Object[] a_flat, int a_flatElement, Object a_shaped,
			int[] a_dimensions, int a_currentDimension) {
		if(a_shaped instanceof Object[]) {
			Object[] shaped=(Object[])a_shaped;
			System.arraycopy(a_flat, 0, shaped, 0, a_flat.length);
			return a_flat.length;
		}
		return _registry.shapeArray(a_flat,a_shaped);
	}


}
