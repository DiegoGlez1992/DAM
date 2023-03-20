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
 * representation for java.lang.reflect.Field.
 * <br><br>See the respective documentation in the JDK API.
 * @see Reflector
 */
public interface ReflectField {
	
	public Object get(Object onObject);
	
	public String getName();
	
	/**
	 * The ReflectClass returned by this method should have been
	 * provided by the parent reflector.
	 * 
	 * @return the ReflectClass representing the field type as provided by the parent reflector
	 */
	public ReflectClass getFieldType();
	
	public boolean isPublic();
	
	public boolean isStatic();
	
	public boolean isTransient();
	
	public void set(Object onObject, Object value);
	
	/**
	 * The ReflectClass returned by this method should have been
	 * provided by the parent reflector.
	 * 
	 * @return the ReflectClass representing the index type as provided by the parent reflector
	 */
	public ReflectClass indexType();
	
	public Object indexEntry(Object orig);
}
