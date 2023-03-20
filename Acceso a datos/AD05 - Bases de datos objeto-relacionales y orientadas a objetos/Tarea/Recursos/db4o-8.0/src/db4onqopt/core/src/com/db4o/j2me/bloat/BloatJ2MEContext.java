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
package com.db4o.j2me.bloat;

import java.io.*;

import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.instrumentation.util.*;

// TODO extract generic functionality, move to db4otools, delete
public class BloatJ2MEContext {
	public static final String INIT_METHODNAME = "<init>";
	public static final String EQUALS_METHODNAME = "equals";
	private static final String LOADCLASSCONSTMETHODNAME = "db4o$class$";
	private ClassFileLoader _loader;

	public BloatJ2MEContext(ClassFileLoader loader, String outputDirPath) {
		_loader = loader;
		_loader.setOutputDir(new File(outputDirPath));
	}

	public ClassFileLoader getLoader() {
		return _loader;
	}

	public ClassEditor loadClass(String classPath, String className) {
		_loader.appendClassPath(classPath);
		try {
			ClassInfo info = _loader.loadClass(className);
			EditorContext context = new PersistentBloatContext(info.loader());
			return context.editClass(info);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ClassEditor createClass(int modifiers, String className,
			Type superType, Type[] Interfaces) {
		EditorContext context = new PersistentBloatContext(_loader);
		return context.newClass(modifiers, className, superType, Interfaces);
	}

	public MethodEditor createMethod(ClassEditor ce, int modiefiers,
			Class type, String methodName, Class[] params, Class[] exeptions) {
		return new MethodEditor(ce, modiefiers, type, methodName, params,
				exeptions);
	}

	public FieldEditor createField(ClassEditor ce, int modifiers, Type type,
			String fieldName) {
		FieldEditor fe = new FieldEditor(ce, modifiers, type, fieldName);
		fe.commit();
		return fe;
	}

	public MemberRef fieldRef(Class parent, Class fieldClass, String name) {
		return fieldRef(getType(parent), fieldClass, name);
	}

	public MemberRef fieldRef(Type parent, Class fieldClass, String name) {
		return fieldRef(parent, getType(fieldClass), name);
	}

	public MemberRef fieldRef(Type parent, Type type, String name) {
		return new MemberRef(parent, new NameAndType(name, type));
	}

	public MemberRef fieldRef(String parent, Class fieldClass, String name) {
		Type type = Type.getType(Type.classDescriptor(parent));
		return fieldRef(type, fieldClass, name);
	}

	public MemberRef methodRef(Type parent, String name, Type[] param, Type ret) {
		NameAndType nat = new NameAndType(name, Type.getType(param, ret));
		return new MemberRef(parent, nat);
	}

	public MemberRef methodRef(Type parent, String name, Class[] param,
			Class ret) {
		Type[] paramTypes = new Type[param.length];
		for (int i = 0; i < paramTypes.length; i++) {
			paramTypes[i] = getType(param[i]);
		}
		return methodRef(parent, name, paramTypes, getType(ret));
	}

	public MemberRef methodRef(Class parent, String name, Class[] param,
			Class ret) {
		return methodRef(getType(parent), name, param, ret);
	}

	public Type getType(Class clazz) {
		return Type.getType(clazz);
	}

	public Type getType(String desc) {
		return Type.getType(desc);
	}

	public LocalVariable[] createLocalVariables(int num) {
		LocalVariable[] localVars = new LocalVariable[num + 1];
		for (int i = 0; i <= num; i++) {
			localVars[i] = new LocalVariable(i);
		}
		return localVars;
	}

	// TODO: Why is an empty 'throws' generated according to javap?
	public void createLoadClassConstMethod(ClassEditor ce) {
		MethodBuilder builder = new MethodBuilder(this, ce, Modifiers.PROTECTED
				| Modifiers.STATIC, Class.class, LOADCLASSCONSTMETHODNAME,
				new Class[] { String.class }, null);
		builder.aload(0);
		builder.invokeStatic(Type.CLASS, "forName",
				new Type[] { Type.STRING }, Type.CLASS);
		builder.label(1);
		builder.areturn();
		builder.label(2);
		builder.astore(1);
		builder.newRef(NoClassDefFoundError.class);
		builder.dup();
		builder.aload(1);
		builder.invokeVirtual(getType(ClassNotFoundException.class),
				"getMessage", new Type[] {}, Type.STRING);
		builder.invokeSpecial(getType(NoClassDefFoundError.class),
				INIT_METHODNAME, new Type[] { Type.STRING }, Type.VOID);
		builder.athrow();
		builder.addTryCatch(0, 1, 2, ClassNotFoundException.class);
		builder.commit();
	}

	public void invokeLoadClassConstMethod(MethodBuilder builder,
			String clazzName) {
		builder.ldc(normalizeClassName(clazzName));
		builder.invokeStatic(builder.parentType(),
				LOADCLASSCONSTMETHODNAME, new Type[] { Type.STRING }, Type.CLASS);
	}

	public String normalizeClassName(String name) {
		return name.replace('/', '.');
	}

	public MemberRef[] collectDeclaredFields(ClassEditor ce) {
		FieldInfo[] fields = ce.fields();
		MemberRef[] refs = new MemberRef[fields.length];
		for (int i = 0; i < fields.length; i++) {
			refs[i] = new FieldEditor(ce, fields[i]).memberRef();
		}
		return refs;
	}
	
	public void addNoArgConstructor(ClassEditor ce) {
		MethodEditor init = new MethodEditor(ce, Modifiers.PUBLIC, Type.VOID,
				INIT_METHODNAME, new Type[0], new Type[0]);
		MemberRef mr = methodRef(ce.superclass(), INIT_METHODNAME,
				new Class[0], void.class);
		LabelGenerator labelGen = new LabelGenerator();
		init.addLabel(labelGen.createLabel(true));
		init.addInstruction(Opcode.opcx_aload, init.paramAt(0));
		init.addInstruction(Opcode.opcx_invokespecial, mr);
		init.addInstruction(Opcode.opcx_return);
		init.commit();
	}
	
	public FieldEditor fieldEditor(Class clazz, FieldInfo fieldInfo) {
		FieldEditor f = null;

		try {
			f = new FieldEditor(new ClassEditor(null, new ClassFileLoader()
					.loadClass(clazz.getName())), fieldInfo);
		} catch (ClassFormatException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return f;
	}
}
