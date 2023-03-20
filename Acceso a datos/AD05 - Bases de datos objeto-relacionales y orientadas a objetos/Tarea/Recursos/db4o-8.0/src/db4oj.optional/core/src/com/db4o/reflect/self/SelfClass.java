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
package com.db4o.reflect.self;

import com.db4o.internal.*;
import com.db4o.reflect.*;

public class SelfClass implements ReflectClass {
	private static final SelfField[] EMPTY_FIELDS = new SelfField[0];

	private boolean _isAbstract;

	private SelfField[] _fields;

	private Reflector _parentReflector;

	private SelfReflectionRegistry _registry;

	private Class _class;

	private Class _superClass;

	// public SelfClass() {
	// super();
	// }

	public SelfClass(Reflector parentReflector,
			SelfReflectionRegistry registry, Class clazz) {
		_parentReflector = parentReflector;
		_registry = registry;
		_class = clazz;
	}

	// TODO: Is this needed at all?
	public Class getJavaClass() {
		return _class;
	}

	public Reflector reflector() {
		return _parentReflector;
	}

	public ReflectClass getComponentType() {
		if (!isArray()) {
			return null;
		}
		return _parentReflector.forClass(_registry.componentType(_class));
	}

	public ReflectField[] getDeclaredFields() {
		ensureClassInfoLoaded();
		return _fields;
	}

	private void ensureClassInfoLoaded() {
		if (_fields == null) {
			ClassInfo classInfo = _registry.infoFor(_class);
			if (classInfo == null) {
				_fields = EMPTY_FIELDS;
				return;
			}
			_superClass = classInfo.superClass();
			_isAbstract = classInfo.isAbstract();
			FieldInfo[] fieldInfo = classInfo.fieldInfo();
			if (fieldInfo == null) {
				_fields = EMPTY_FIELDS;
				return;
			}
			_fields = new SelfField[fieldInfo.length];
			for (int idx = 0; idx < fieldInfo.length; idx++) {
				_fields[idx] = selfFieldFor(fieldInfo[idx]);
			}
		}
	}

	public ReflectField getDeclaredField(String name) {
		ensureClassInfoLoaded();
		for (int idx = 0; idx < _fields.length; idx++) {
			if (_fields[idx].getName().equals(name)) {
				return _fields[idx];
			}
		}
		return null;
	}

	private SelfField selfFieldFor(FieldInfo fieldInfo) {
		return new SelfField(fieldInfo.name(), _parentReflector
				.forClass(fieldInfo.type()), this, _registry);
	}

	public ReflectClass getDelegate() {
		return this;
	}

	public ReflectMethod getMethod(String methodName,
			ReflectClass[] paramClasses) {
		// TODO !!!!
		return null;
	}

	public String getName() {
		return _class.getName();
	}

	public ReflectClass getSuperclass() {
		ensureClassInfoLoaded();
		if (_superClass == null) {
			return null;
		}
		return _parentReflector.forClass(_superClass);
	}

	public boolean isAbstract() {
		ensureClassInfoLoaded();
		return _isAbstract || isInterface();
	}

	public boolean isArray() {
		return _class.isArray();
	}

	public boolean isAssignableFrom(ReflectClass type) {
		if (!(type instanceof SelfClass)) {
			return false;
		}
		return _class.isAssignableFrom(((SelfClass) type).getJavaClass());
	}

	public boolean isCollection() {
		return _parentReflector.isCollection(this);
	}

	public boolean isInstance(Object obj) {
		return _class.isInstance(obj);
	}

	public boolean isInterface() {
		return _class.isInterface();
	}

	public boolean isPrimitive() {
		return _registry.isPrimitive(_class);
	}

	public Object newInstance() {
		try {
			return _class.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Specialized exceptions break conversion to .NET

		//           
		//        
		//            
		// } catch (InstantiationException e) {
		// e.printStackTrace();
		// } catch (IllegalAccessException e) {
		// e.printStackTrace();
		// }

		return null;
	}

	public Object nullValue() {
		return null;
	}
	
	public boolean ensureCanBeInstantiated() {
		return true;
	}

	public boolean isImmutable() {
		return isPrimitive() || Platform4.isSimple(_class);
	}
	
}
