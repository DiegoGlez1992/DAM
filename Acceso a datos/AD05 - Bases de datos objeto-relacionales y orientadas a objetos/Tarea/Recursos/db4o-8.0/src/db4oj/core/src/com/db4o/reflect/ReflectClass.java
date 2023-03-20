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

import com.db4o.internal.*;


/** 
 * representation for java.lang.Class.
 * <br><br>See the respective documentation in the JDK API.
 * @see Reflector
 */
public interface ReflectClass {
	
    public ReflectClass getComponentType();
	
	public ReflectField[] getDeclaredFields();
	
	public ReflectField getDeclaredField(String name);
    
	/**
	 * Returns the ReflectClass instance being delegated to.
	 * 
	 * If there's no delegation it should return this. 
	 * 
	 * @return delegate or this
	 */
    public ReflectClass getDelegate();
	
	public ReflectMethod getMethod(String methodName, ReflectClass[] paramClasses);
	
	public String getName();
	
	public ReflectClass getSuperclass();
	
	public boolean isAbstract();
	
	public boolean isArray();
	
	public boolean isAssignableFrom(ReflectClass type);
	
	public boolean isCollection();
	
	public boolean isInstance(Object obj);
	
	public boolean isInterface();
	
	public boolean isPrimitive();
    
	public Object newInstance();
    
    public Reflector reflector();
    	
	public Object nullValue();

	/**
	 * Calling this method may change the internal state of the class, even if a usable
	 * constructor has been found on earlier invocations.
	 * 
	 * @return true, if instances of this class can be created, false otherwise
	 */
	public boolean ensureCanBeInstantiated();
	
	/**
	 * We need this for replication, to find out if a class needs to be traversed
	 * or if it simply can be copied across. For now we will simply return 
	 * the classes that are {@link #isPrimitive()} and {@link Platform4#isSimple(Class)}
	 * We can think about letting users add an Immutable annotation.  
	 */
	public boolean isImmutable();
	
}
