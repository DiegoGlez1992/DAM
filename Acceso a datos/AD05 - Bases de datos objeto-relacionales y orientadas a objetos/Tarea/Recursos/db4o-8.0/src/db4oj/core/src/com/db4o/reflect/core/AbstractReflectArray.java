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
package com.db4o.reflect.core;

import java.lang.reflect.*;

import com.db4o.reflect.*;

/**
 * @exclude
 */
public abstract class AbstractReflectArray implements ReflectArray {
	
	protected final Reflector _reflector;

	public AbstractReflectArray(Reflector reflector) {
		_reflector = reflector;
	}

	public abstract Object newInstance(ReflectClass componentType, int[] dimensions);

	public abstract Object newInstance(ReflectClass componentType, int length);

	public int[] dimensions(Object arr) {
	    int count = 0;
	    ReflectClass claxx = _reflector.forObject(arr);
	    while (claxx.isArray()) {
	        count++;
	        claxx = claxx.getComponentType();
	    }
	    int dim[] = new int[count];
	    for (int i = 0; i < count; i++) {
	        try {
	            dim[i] = getLength(arr);
	            arr = get(arr, 0);
	        } catch (Exception e) {
	            return dim;
	        }
	    }
	    return dim;
	}

	public int flatten(Object a_shaped, int[] a_dimensions, int a_currentDimension, Object[] a_flat,
			int a_flatElement) {
			    if (a_currentDimension == (a_dimensions.length - 1)) {
			        for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
			            a_flat[a_flatElement++] = getNoExceptions(a_shaped, i);
			        }
			    } else {
			        for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
			            a_flatElement =
			                flatten(
			                    getNoExceptions(a_shaped, i),
			                    a_dimensions,
			                    a_currentDimension + 1,
			                    a_flat,
			                    a_flatElement);
			        }
			    }
			    return a_flatElement;
			}

	public Object get(Object onArray, int index) {
	    return Array.get(onArray, index);
	}

	public ReflectClass getComponentType(ReflectClass a_class) {
	    while (a_class.isArray()) {
	        a_class = a_class.getComponentType();
	    }
	    return a_class;
	}

	public int getLength(Object array) {
	    return Array.getLength(array);
	}

	private final Object getNoExceptions(Object onArray, int index) {
	    try {
	        return get(onArray, index);
	    } catch (Exception e) {
	        return null;
	    }
	}

	public boolean isNDimensional(ReflectClass a_class) {
	    return a_class.getComponentType().isArray();
	}

	public void set(Object onArray, int index, Object element) {
	    if(element == null){
	        try{
	            Array.set(onArray, index, element);
	        }catch(Exception e){
	            // This can happen on primitive arrays
	            // and we are fine with ignoring it.
	        	// TODO: check if it's a primitive array first and don't ignore exceptions
	        }
	        
	    }else{
	        Array.set(onArray, index, element);
	    }
	}

	public int shape(Object[] a_flat, int a_flatElement, Object a_shaped, int[] a_dimensions,
			int a_currentDimension) {
			    if (a_currentDimension == (a_dimensions.length - 1)) {
			        for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
			            set(a_shaped, i, a_flat[a_flatElement++]);
			        }
			    } else {
			        for (int i = 0; i < a_dimensions[a_currentDimension]; i++) {
			            a_flatElement =
			                shape(
			                    a_flat,
			                    a_flatElement,
			                    get(a_shaped, i),
			                    a_dimensions,
			                    a_currentDimension + 1);
			        }
			    }
			    return a_flatElement;
			}

}
