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

import java.lang.reflect.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.reflect.FieldInfo;

import com.db4o.reflect.self.*;
import com.db4o.reflect.self.ClassInfo;

public class RegistryEnhancer {
	private static final String COMPONENTTYPE_METHODNAME = "componentType";

	private static final String ARRAYFOR_METHODNAME = "arrayFor";

	private static final String INFOFOR_METHODNAME = "infoFor";

	private static final String CLASSINFO_CONSTNAME = "CLASSINFO";

	private ClassEditor _ce;

	private Class[] _clazzes;

	private BloatJ2MEContext _context;

	public RegistryEnhancer(BloatJ2MEContext context, ClassEditor ce, Class clazz) {
		this._ce = ce;
		this._clazzes = createClasses(clazz);
		this._context = context;
	}

	private static Class[] createClasses(Class concrete) {
		List list = new ArrayList();
		Class cur = concrete;
		while (cur != Object.class) {
			list.add(cur);
			cur = cur.getSuperclass();
		}
		return (Class[]) list.toArray(new Class[list.size()]);
	}

	public void generate() {
		_context.addNoArgConstructor(_ce);
		generateCLASSINFOField();
		generateInfoForMethod();
		generateArrayForMethod();
		generateComponentTypeMethod();
	}

	private void generateCLASSINFOField() {
		FieldEditor fe = _context.createField(_ce, 26, Type
				.getType(Hashtable.class), CLASSINFO_CONSTNAME);

		MethodBuilder builder = new MethodBuilder(_context, _ce,
				Modifiers.STATIC, void.class, "<clinit>", new Class[0],
				new Class[0]);
		builder.newRef(Hashtable.class);
		builder.dup();
		builder.invokeSpecial(_context.getType(Hashtable.class), BloatJ2MEContext.INIT_METHODNAME,
				new Type[0], Type.VOID);
		builder.putstatic(_ce.type(), Hashtable.class, CLASSINFO_CONSTNAME);
		for (int classIdx = 0; classIdx < _clazzes.length; classIdx++) {
			builder.getstatic(_ce.type(), Hashtable.class, CLASSINFO_CONSTNAME);
			generateInfoForClass(builder, _clazzes[classIdx]);
		}

		builder.returnInstruction();
		builder.commit();
		fe.commit();

	}

	private void generateInfoForClass(MethodBuilder builder, Class clazz) {
		builder.invokeLoadClassConstMethod(clazz);
		builder.newRef(com.db4o.reflect.self.ClassInfo.class);
		builder.dup();
		FieldInfo[] fieldsInf = collectFieldsOfClass(clazz);
		builder.ldc(isAbstractClass(clazz));
		builder.invokeLoadClassConstMethod(clazz
				.getSuperclass());
		builder.ldc(fieldsInf.length);
		builder.anewarray(com.db4o.reflect.self.FieldInfo.class);
		for (int i = 0; i < fieldsInf.length; i++) {
			generateInfoForField(builder, _context.fieldEditor(clazz, fieldsInf[i]), i);
		}
		builder.invokeSpecial(_context.getType(ClassInfo.class), BloatJ2MEContext.INIT_METHODNAME,
				new Type[] { Type.BOOLEAN, Type.CLASS,
						_context.getType(com.db4o.reflect.self.FieldInfo[].class) },
				Type.VOID);
		builder.invokeVirtual(_context.getType(Hashtable.class), "put",
				new Type[] { Type.OBJECT, Type.OBJECT }, Type.OBJECT);
	}

	private void generateInfoForField(MethodBuilder builder, FieldEditor fieldEditor, int arrIdx) {
		builder.dup();
		builder.ldc(arrIdx);
		builder.newRef(com.db4o.reflect.self.FieldInfo.class);
		builder.dup();
		builder.ldc(fieldEditor.name());
		Class wrapper = PrimitiveUtil.wrapper(fieldEditor.type());
		if (wrapper != null) {
			builder.getstatic(wrapper, Class.class, "TYPE");
		} else {
			builder.invokeLoadClassConstMethod(fieldEditor.type().className());
		}
		builder.ldc(fieldEditor.isPublic());
		builder.ldc(fieldEditor.isStatic());
		builder.ldc(fieldEditor.isTransient());
		builder.invokeSpecial(
				_context.getType(com.db4o.reflect.self.FieldInfo.class), BloatJ2MEContext.INIT_METHODNAME,
				new Type[] { Type.STRING, Type.CLASS, Type.BOOLEAN,
						Type.BOOLEAN, Type.BOOLEAN }, Type.VOID);
		builder.aastore();
	}


	private FieldInfo[] collectFieldsOfClass(Class clazz) {
		ClassEditor ce = null;
		FieldInfo[] fields = null;
		try {
			ce = new ClassEditor(null, new ClassFileLoader().loadClass(clazz
					.getName()));
			fields = ce.fields();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return fields;
	}

	private boolean isAbstractClass(Class clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	private void generateInfoForMethod() {
		MethodBuilder builder = new MethodBuilder(_context, _ce,
				Modifiers.PUBLIC, com.db4o.reflect.self.ClassInfo.class,
				INFOFOR_METHODNAME, new Class[] { Class.class }, null);
		builder.getstatic(_ce.type(), Hashtable.class, CLASSINFO_CONSTNAME);
		builder.aload(1);
		builder.invokeVirtual(_context.getType(Hashtable.class), "get",
				new Type[] { Type.OBJECT }, Type.OBJECT);
		builder.checkcast(ClassInfo.class);
		builder.areturn();
		builder.commit();

	}

	private void generateArrayForMethod() {
		MethodBuilder builder = new MethodBuilder(_context, _ce,
				Modifiers.PUBLIC, Object.class, ARRAYFOR_METHODNAME, new Class[] {
						Class.class, Integer.TYPE }, null);
		int labelIdx = 1;
		for (int classIdx = 0; classIdx < _clazzes.length; classIdx++) {
			builder.invokeLoadClassConstMethod(_clazzes[classIdx]);
			builder.aload(1);
			builder.invokeVirtual(Type.CLASS,
					"isAssignableFrom", new Type[] { Type.CLASS },
					Type.BOOLEAN);
			builder.ifeq(labelIdx);
			builder.iload(2);
			builder.newarray(_clazzes[classIdx]);
			builder.areturn();
			builder.label(labelIdx);
			labelIdx++;
		}
		builder.aload(0);
		builder.aload(1);
		builder.iload(2);
		builder.invokeSpecial(_context.getType(SelfReflectionRegistry.class),
				ARRAYFOR_METHODNAME, new Type[] { Type.CLASS, Type.INTEGER },
				Type.OBJECT);
		builder.areturn();
		builder.commit();
	}

	private void generateComponentTypeMethod() {
		MethodBuilder builder = new MethodBuilder(_context, _ce,
				Modifiers.PUBLIC, Class.class, COMPONENTTYPE_METHODNAME,
				new Class[] { Class.class }, new Class[0]);
		int labelId = 1;
		for (int classIdx = 0; classIdx < _clazzes.length; classIdx++) {
			builder
					.invokeLoadClassConstMethod(convertToArray(_clazzes[classIdx]));
			builder.aload(1);
			builder.invokeVirtual(Type.CLASS,
					"isAssignableFrom", new Type[] { Type.CLASS },
					Type.BOOLEAN);
			builder.ifeq(labelId);
			builder.invokeLoadClassConstMethod(_clazzes[classIdx]);
			builder.areturn();
			builder.label(labelId);
			labelId++;
		}
		builder.aload(0);
		builder.aload(1);
		builder.invokeSpecial(
				_context.getType(com.db4o.reflect.self.SelfReflectionRegistry.class),
				COMPONENTTYPE_METHODNAME, new Type[] { Type.CLASS }, Type.CLASS);
		builder.areturn();
		builder.commit();

	}

	private Class convertToArray(Class clazz) {
		return Array.newInstance(clazz, new int[1]).getClass();
	}

	
	
}
