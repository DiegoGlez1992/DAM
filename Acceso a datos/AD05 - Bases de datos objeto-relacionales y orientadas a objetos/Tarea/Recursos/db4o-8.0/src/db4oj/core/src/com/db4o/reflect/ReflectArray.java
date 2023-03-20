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
package com.db4o.reflect;


/** 
 * representation for java.lang.reflect.Array.
 * <br><br>See the respective documentation in the JDK API.
 * @see Reflector
 */
public interface ReflectArray {
    
    public void analyze(Object obj, ArrayInfo info);
    
    public int[] dimensions(Object arr);
    
    public int flatten(
        Object a_shaped,
        int[] a_dimensions,
        int a_currentDimension,
        Object[] a_flat,
        int a_flatElement);
	
	public Object get(Object onArray, int index);
	
    public ReflectClass getComponentType(ReflectClass a_class);
	
	public int getLength(Object array);
	
	public boolean isNDimensional(ReflectClass a_class);
	
	public Object newInstance(ReflectClass componentType, ArrayInfo info);
	
	public Object newInstance(ReflectClass componentType, int length);
	
	public Object newInstance(ReflectClass componentType, int[] dimensions);
	
	public void set(Object onArray, int index, Object element);
    
    public int shape(
        Object[] a_flat,
        int a_flatElement,
        Object a_shaped,
        int[] a_dimensions,
        int a_currentDimension);

	
}

