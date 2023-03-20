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
package com.db4o.cs.internal;

import java.util.*;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;

public class ClassInfoHelper {

	private Hashtable4 _classMetaTable = new Hashtable4();

	private Hashtable4 _genericClassTable = new Hashtable4();

	private Config4Impl _config;

	public ClassInfoHelper(Config4Impl config) {
		_config = config;
	}

	public ClassInfo getClassMeta(ReflectClass claxx) {

		if (isObjectClass(claxx)) {
			return ClassInfo.newSystemClass(claxx.getName());
		}

		ClassInfo existing = lookupClassMeta(claxx.getName());
		if (existing != null) {
			return existing;
		}

		return newUserClassMeta(claxx);
	}

	private ClassInfo newUserClassMeta(ReflectClass claxx) {

		ClassInfo classMeta = ClassInfo.newUserClass(claxx.getName());
		classMeta.setSuperClass(mapSuperclass(claxx));

		registerClassMeta(claxx.getName(), classMeta);		
		
		classMeta.setFields(
				mapFields(claxx.getDeclaredFields(), shouldStoreTransientFields(claxx)));
		
		return classMeta;
	}

	private boolean shouldStoreTransientFields(ReflectClass claxx) {		
		Config4Class configClass = _config.configClass(claxx.getName());
		return configClass == null 
							? false 
							: configClass.storeTransientFields();
	}

	private ClassInfo mapSuperclass(ReflectClass claxx) {
		ReflectClass superClass = claxx.getSuperclass();
		if (superClass != null) {
			return getClassMeta(superClass);
		}
		return null;
	}

	private FieldInfo[] mapFields(ReflectField[] fields, boolean shouldStoreTransientFields) {
		
		if (!shouldStoreTransientFields) {
			fields = filterTransientFields(fields);
		}
		
		FieldInfo[] fieldsMeta = new FieldInfo[fields.length];
		for (int i = 0; i < fields.length; ++i) {
			final ReflectField field = fields[i];
			boolean isArray = field.getFieldType().isArray();
			ReflectClass fieldClass = isArray ? field.getFieldType().getComponentType() : field.getFieldType();
			boolean isPrimitive = fieldClass.isPrimitive();
			// TODO: need to handle NArray, currently it ignores NArray and alway sets NArray flag false.
			fieldsMeta[i] = new FieldInfo(field.getName(), getClassMeta(fieldClass), isPrimitive, isArray, false);
		}
		
		return fieldsMeta;
	}

	private ReflectField[] filterTransientFields(ReflectField[] fields) {		
		List<ReflectField> filteredFields = new ArrayList<ReflectField>();
		
		for(ReflectField field : fields) {
			if (!field.isTransient()) {
				filteredFields.add(field);
			}
		}
		
		return filteredFields.toArray(new ReflectField[filteredFields.size()]);
	}

	private static boolean isObjectClass(ReflectClass claxx) {
		// TODO: We should send the whole class meta if we'd like to support
		// java and .net communication (We have this request in our user forum
		// http://developer.db4o.com/forums/thread/31504.aspx). If we only want
		// to support java & .net platform separately, then this method should
		// be moved to Platform4.
		//return className.startsWith("java.lang.Object") || className.startsWith("System.Object");
		return claxx.reflector().forClass(Const4.CLASS_OBJECT) == claxx;
	}

	private ClassInfo lookupClassMeta(String className) {
		return (ClassInfo) _classMetaTable.get(className);
	}

	private void registerClassMeta(String className, ClassInfo classMeta) {
		_classMetaTable.put(className, classMeta);
	}

	public GenericClass classMetaToGenericClass(GenericReflector reflector,
			ClassInfo classMeta) {
		if (classMeta.isSystemClass()) {
			return (GenericClass) reflector.forName(classMeta.getClassName());
		}

		String className = classMeta.getClassName();
		// look up from generic class table.
		GenericClass genericClass = lookupGenericClass(className);
		if (genericClass != null) {
			return genericClass;
		}

		ReflectClass reflectClass = reflector.forName(className);
		if(reflectClass != null) {
			return (GenericClass) reflectClass;
		}
		
		GenericClass genericSuperClass = null;
		ClassInfo superClassMeta = classMeta.getSuperClass();
		if (superClassMeta != null) {
			genericSuperClass = classMetaToGenericClass(reflector,
					superClassMeta);
		}

		genericClass = new GenericClass(reflector, null, className,
				genericSuperClass);
		registerGenericClass(className, genericClass);

		FieldInfo[] fields = classMeta.getFields();
		GenericField[] genericFields = new GenericField[fields.length];

		for (int i = 0; i < fields.length; ++i) {
			ClassInfo fieldClassMeta = fields[i].getFieldClass();
			String fieldName = fields[i].getFieldName();
			GenericClass genericFieldClass = classMetaToGenericClass(reflector,
					fieldClassMeta);
			genericFields[i] = new GenericField(fieldName, genericFieldClass,
					fields[i]._isPrimitive);
		}

		genericClass.initFields(genericFields);
		return genericClass;
	}

	private GenericClass lookupGenericClass(String className) {
		return (GenericClass) _genericClassTable.get(className);
	}

	private void registerGenericClass(String className, GenericClass classMeta) {
		_genericClassTable.put(className, classMeta);
		((GenericReflector)classMeta.reflector()).register(classMeta);
	}

}
