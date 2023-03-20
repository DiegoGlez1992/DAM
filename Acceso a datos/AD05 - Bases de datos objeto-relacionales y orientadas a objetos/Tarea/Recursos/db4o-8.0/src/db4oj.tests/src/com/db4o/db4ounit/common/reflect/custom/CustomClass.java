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

import com.db4o.foundation.*;
import com.db4o.reflect.*;

public class CustomClass implements ReflectClass {

	// fields must be public so test works on less capable runtimes
	public CustomClassRepository _repository;
	public String _name;
	public ReflectField[] _fields;

	public CustomClass() {
	}

	public CustomClass(CustomClassRepository repository, String name, String[] fieldNames,
			Class[] fieldTypes) {
		_repository = repository;
		_name = name;
		_fields = createFields(fieldNames, fieldTypes);
	}

	private ReflectField[] createFields(String[] fieldNames, Class[] fieldTypes) {
		ReflectField[] fields = new ReflectField[fieldNames.length + 1];
		for (int i=0; i<fieldNames.length; ++i) {
			fields[i] = new CustomField(_repository, i, fieldNames[i], fieldTypes[i]);
		}
		fields[fields.length-1] = new CustomUidField(_repository);
		return fields;
	}

	public ReflectClass getComponentType() {
		throw new NotImplementedException();
	}

	public CustomField customField(String name) {
		return (CustomField)getDeclaredField(name);
	}

	public ReflectField getDeclaredField(String name) {
		for (int i=0; i<_fields.length; ++i) {
			ReflectField field = _fields[i];
			if (field.getName().equals(name)) {
				return field;
			}
		}
		return null;
	}

	public ReflectField[] getDeclaredFields() {
		return _fields;
	}

	public ReflectClass getDelegate() {
		return this;
	}

	public ReflectMethod getMethod(String methodName,
			ReflectClass[] paramClasses) {
		return null;
	}

	public String getName() {
		return _name;
	}

	public ReflectClass getSuperclass() {
		return null;
//		return _repository.reflectClass(java.lang.Object.class);
	}

	public boolean isAbstract() {
		return false;
	}

	public boolean isArray() {
		return false;
	}

	public boolean isAssignableFrom(ReflectClass type) {
		return equals(type);
	}

	public boolean isCollection() {
		return false;
	}

	public boolean isInstance(Object obj) {
		throw new NotImplementedException();
	}

	public boolean isInterface() {
		return false;
	}

	public boolean isPrimitive() {
		return false;
	}

	public Object newInstance() {
		return new PersistentEntry(_name, null, new Object[_fields.length-1 /* uid field is kept explicitly */]);
	}

	public Reflector reflector() {
		return _repository._reflector;
	}

	public Iterator4 customFields() {
		return Iterators.filter(_fields, new Predicate4() {
			public boolean match(Object candidate) {
				return candidate instanceof CustomField;
			}
		});
	}
	
	public Object nullValue() {
		return null;
	}
	
	public boolean ensureCanBeInstantiated() {
		return true;
	}

	public boolean isImmutable() {
		return false;
	}
	
}
