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
package com.db4o.ext;

import com.db4o.foundation.*;
import com.db4o.reflect.*;


/**
 * the internal representation of a field on a stored class.
 */
public interface StoredField {
    
    /**
     * creates an index on this field at runtime.
     */
    public void createIndex();
    
    
    /**
     * drops an existing index on this field at runtime.
     */
    public void dropIndex();
    
	/**
	 * returns the field value on the passed object.
	 * <br><br>This method will also work, if the field is not present in the current
	 * version of the class.
	 * <br><br>It is recommended to use this method for refactoring purposes, if fields
	 * are removed and the field values need to be copied to other fields. 
	 */
	public Object get(Object onObject);
	
	
	/**
	 * returns the name of the field.
	 */
	public String getName();
	
	
	/**
	 * returns the Class (Java) / Type (.NET) of the field.
	 * <br><br>For array fields this method will return the type of the array.
	 * Use {@link #isArray()} to detect arrays.  
	 */
	public ReflectClass getStoredType();
	
	
	/**
	 * returns true if the field is an array.
	 */
	public boolean isArray();
	
	/**
	 * modifies the name of this stored field.
	 * <br><br>After renaming one or multiple fields the ObjectContainer has
	 * to be closed and reopened to allow internal caches to be refreshed.<br><br>
	 * @param name the new name
	 */
	public void rename(String name);
    
    
    /**
     * specialized highspeed API to collect all values of a field for all instances
     * of a class, if the field is indexed. 
     * <br><br>The field values will be taken directly from the index without the
     * detour through class indexes or object instantiation.
     * <br><br>
     * If this method is used to get the values of a first class object index,
     * deactivated objects will be passed to the visitor. 
     * 
     * @param visitor the visitor to be called with each index value.
     */
    public void traverseValues(Visitor4 visitor);
    
    /**
     * Returns whether this field has an index or not.
     * @return true if this field has an index.
     */
	public boolean hasIndex();


//  will need for replication. Requested for 3.0 
//	
//	/**
//	 * sets the field value on the passed object.
//	 * @param onObject
//	 * @param fieldValue
//	 */
//	public void set(Object onObject, Object fieldValue);
}
