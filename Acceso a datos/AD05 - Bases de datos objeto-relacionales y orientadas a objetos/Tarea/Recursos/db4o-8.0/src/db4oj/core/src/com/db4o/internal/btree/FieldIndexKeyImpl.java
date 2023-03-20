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
package com.db4o.internal.btree;


/**
 * Composite key for field indexes, first compares on the actual
 * indexed field _value and then on the _parentID (which is a
 * reference to the containing object). 
 * 
 * @exclude
 */
public class FieldIndexKeyImpl implements FieldIndexKey {
	
	private final Object _value;
    
    private final int _parentID;
    
    public FieldIndexKeyImpl(int parentID, Object value){
        _parentID = parentID;
        _value = value;
    }
    
    public int parentID(){
        return _parentID;
    }
    
    public Object value(){
        return _value;
    }
    
    public String toString() {
    	return "FieldIndexKey(" + _parentID + ", " + safeString(_value) + ")";
    }

	private String safeString(Object value) {
		if (null == value) {
			return "null";
		}
		return value.toString();
	}
}
