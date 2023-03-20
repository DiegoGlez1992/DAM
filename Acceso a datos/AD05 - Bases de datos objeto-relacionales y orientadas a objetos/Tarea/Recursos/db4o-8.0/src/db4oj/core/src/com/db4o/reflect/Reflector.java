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

import com.db4o.foundation.*;

/**
 * root of the reflection implementation API.
 * <br><br>The open reflection interface is supplied to allow to implement
 * reflection functionality on JDKs that do not come with the
 * java.lang.reflect.* package.<br><br>
 * Use {@link com.db4o.config.CommonConfiguration#reflectWith configuration.commmon().reflectWith(IReflect reflector)}
 * to register the use of your implementation before opening database
 * files.
 */
public interface Reflector extends DeepClone{
	
	void configuration(ReflectorConfiguration config);
	
	/**
	 * returns an ReflectArray object, the equivalent to java.lang.reflect.Array.
	 */
	public ReflectArray array();
	
	/**
	 * returns an ReflectClass for a Class
	 */
	public ReflectClass forClass(Class clazz);
	
	/**
	 * returns an ReflectClass class reflector for a class name or null
	 * if no such class is found
	 */
	public ReflectClass forName(String className);
	
	/**
	 * returns an ReflectClass for an object or null if the passed object is null.
	 */
	public ReflectClass forObject(Object obj);
	
	public boolean isCollection(ReflectClass clazz);
    
    public void setParent(Reflector reflector);
	
}
