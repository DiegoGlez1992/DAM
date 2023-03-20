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


/**
 * @exclude
 */
@decaf.IgnoreImplements(decaf.Platform.JDK11)
public class GenericObject implements Comparable {

    final GenericClass _class;
    
    private Object[] _values;
    
    public GenericObject(GenericClass clazz) {
        _class = clazz;
    }
    
    private void ensureValuesInitialized() {
    	if(_values == null) {
    		_values = new Object[_class.getFieldCount()];
    	}
    }
    
    public void set(int index,Object value) {
    	ensureValuesInitialized();
    	_values[index]=value;
    }

	/**
	 *
	 * @param index
	 * @return the value of the field at index, based on the fields obtained GenericClass.getDeclaredFields
	 */
	public Object get(int index) {
    	ensureValuesInitialized();
    	return _values[index];
    }

    public String toString(){
        if(_class == null){
            return super.toString();    
        }
        return _class.toString(this);
    }

	public GenericClass getGenericClass(){
		return _class;
	}

	public int compareTo(Object o) {
		return 0;
    }
}
