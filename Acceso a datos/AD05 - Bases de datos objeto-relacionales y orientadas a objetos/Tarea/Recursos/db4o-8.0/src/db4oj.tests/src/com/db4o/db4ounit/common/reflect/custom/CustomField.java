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
package com.db4o.db4ounit.common.reflect.custom;

import com.db4o.reflect.*;

/**
 * One important thing to remember when implementing ReflectField
 * is that getFieldType and getIndexType must always return ReflectClass
 * instances given by the parent reflector.
 */
public class CustomField implements ReflectField {

	// fields must be public so test works on less capable runtimes
	public CustomClassRepository _repository;	
	public String _name;
	public Class _type;
	public int _index;
	public boolean _indexed;

	public CustomField() {
	}

	public CustomField(CustomClassRepository repository, int index, String name, Class type) {
		_repository = repository;
		_index = index;
		_name = name;
		_type = type;
	}

	public Object get(Object onObject) {
		logMethodCall("get", onObject);
		return fieldValues(onObject)[_index];
	}

	private Object[] fieldValues(Object onObject) {
		return ((PersistentEntry)onObject).fieldValues;
	}

	public ReflectClass getFieldType() {
		logMethodCall("getFieldType");
		return _repository.forFieldType(_type);
	}

	public String getName() {
		return _name;
	}

	public Object indexEntry(Object orig) {
		logMethodCall("indexEntry", orig);
		return orig;
	}

	public ReflectClass indexType() {
		logMethodCall("indexType");
		return getFieldType();
	}

	public boolean isPublic() {
		return true;
	}

	public boolean isStatic() {
		return false;
	}

	public boolean isTransient() {
		return false;
	}

	public void set(Object onObject, Object value) {
		logMethodCall("set", onObject, value);
		fieldValues(onObject)[_index] = value;
	}

	public void indexed(boolean value) {
		_indexed = value;
	}
	
	public boolean indexed() {
		return _indexed;
	}

	public String toString() {
		return "CustomField(" + _index + ", " + _name + ", " + _type.getName() + ")";
	}

	private void logMethodCall(String methodName) {
		Logger.logMethodCall(this, methodName);
	}

	private void logMethodCall(String methodName, Object arg) {
		Logger.logMethodCall(this, methodName, arg);
	}

	private void logMethodCall(String methodName, Object arg1, Object arg2) {
		Logger.logMethodCall(this, methodName, arg1, arg2);
	}
}
