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
package com.db4o.instrumentation.core;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.instrumentation.bloat.*;
import com.db4o.instrumentation.util.*;

/**
 * @exclude
 */
public class BloatLoaderContext {
	private EditorContext context;
	
	private final BloatReferenceProvider references;
	
	public BloatLoaderContext(ClassInfoLoader loader) {
		this(new CachingBloatContext(loader,new LinkedList(),false));
	}

	public BloatLoaderContext(EditorContext context) {
		this.context=context;
		this.references = new BloatReferenceProvider();
	}
	
	public BloatReferenceProvider references() {
		return references;
	}

	public FlowGraph flowGraph(String className, String methodName) throws ClassNotFoundException {
		return flowGraph(className, methodName, null);
	}
	
	public FlowGraph flowGraph(String className, String methodName,Type[] argTypes) throws ClassNotFoundException {
		ClassEditor classEdit = classEditor(className);
		return flowGraph(classEdit, methodName, argTypes);
	}

	public FlowGraph flowGraph(ClassEditor classEdit, String methodName,Type[] argTypes) throws ClassNotFoundException {
		MethodEditor method = method(classEdit, methodName, argTypes);
		return method == null ? null : new FlowGraph(method);
	}

	public MethodEditor method(ClassEditor classEdit, String methodName,Type[] argTypes) throws ClassNotFoundException {
		ClassEditor clazz = classEdit;
		while(clazz != null) {
			MethodInfo[] methods = clazz.methods();
			for (int methodIdx = 0; methodIdx < methods.length; methodIdx++) {
				MethodEditor methodEdit = context.editMethod(methods[methodIdx]);
				if (methodEdit.name().equals(methodName)&&signatureMatchesTypes(argTypes, methodEdit)) {
					return methodEdit;
				}
			}
			clazz = classEditor(clazz.superclass());
		}
		return null;
	}

	public FieldEditor field(ClassEditor classEdit, String fieldName,Type fieldType) throws ClassNotFoundException {
		ClassEditor clazz = classEdit;
		while(clazz != null) {
			FieldInfo[] fields = clazz.fields();
			for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
				FieldInfo fieldInfo=fields[fieldIdx];
				FieldEditor fieldEdit = context.editField(fieldInfo);
				if (fieldEdit.name().equals(fieldName)&&fieldType.equals(fieldEdit.type())) {
					return fieldEdit;
				}
			}
			clazz = classEditor(clazz.superclass());
		}
		return null;
	}

	public ClassEditor classEditor(Type type) throws ClassNotFoundException {
		return type == null ? null : classEditor(BloatUtil.normalizeClassName(type));
	}

	private boolean signatureMatchesTypes(Type[] argTypes,
			MethodEditor methodEdit) {
		if(argTypes==null) {
			return true;
		}
		Type[] sigTypes=methodEdit.paramTypes();
		int sigOffset=(methodEdit.isStatic()||methodEdit.isConstructor() ? 0 : 1);
		if(argTypes.length!=(sigTypes.length-sigOffset)) {
			return false;
		}
		for (int idx = 0; idx < argTypes.length; idx++) {
			if(!argTypes[idx].className().equals(sigTypes[idx+sigOffset].className())) {
				return false;
			}
		}
		return true;
	}

	public ClassEditor classEditor(String className) throws ClassNotFoundException {
		return context.editClass(className);
	}

	public ClassEditor classEditor(int modifiers, String className, Type superClass, Type[] interfaces) {
		return context.newClass(modifiers, className, superClass, interfaces);
	}
	
	public Type superType(Type type) throws ClassNotFoundException {
		return context.editClass(type).superclass();
	}
	
	public void commit() {
		try {
			context.commit();
		}
		catch(ConcurrentModificationException exc) {
			exc.printStackTrace();
			throw exc;
		}
	}
}
