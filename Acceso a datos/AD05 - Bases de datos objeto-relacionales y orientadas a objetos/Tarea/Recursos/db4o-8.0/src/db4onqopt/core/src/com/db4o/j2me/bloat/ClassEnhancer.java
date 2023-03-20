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

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.reflect.self.*;

public class ClassEnhancer {

	private static final String SELFSET_METHODNAME = "self_set";

	private static final String SELFGET_METHODNAME = "self_get";

	private ClassEditor _ce;

	private BloatJ2MEContext _context;

	public ClassEnhancer(BloatJ2MEContext context, ClassEditor ce) {
		this._context = context;
		this._ce = ce;
	}

	public boolean inspectNoArgConstr(MethodInfo[] methods) {
		MethodEditor me;
		for (int i = 0; i < methods.length; i++) {
			me = new MethodEditor(_ce, methods[i]);
			if ((me.type().equals(Type.VOID))
					&& (me.name().equals(BloatJ2MEContext.INIT_METHODNAME))) {
				return true;
			}
		}
		return false;
	}

	protected void generateSelf_get(MemberRef[] fields) {
		MethodBuilder builder = new MethodBuilder(_context, _ce,
				Modifiers.PUBLIC, Object.class, SELFGET_METHODNAME,
				new Class[] { String.class },new Class[0]);

		for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
			generateSelfGetMethodCase(builder, fieldIdx, fields[fieldIdx]);
		}
		Type superType = _ce.superclass();
		if (instrumentedType(superType)) {
			builder.aload(0);
			builder.aload(1);
			builder.invokeSpecial(superType, SELFGET_METHODNAME,
					new Type[] { Type.STRING }, Type.OBJECT);
		} else {
			builder.ldc(null);
		}
		builder.areturn();
		builder.commit();
	}

	private void generateSelfGetMethodCase(MethodBuilder builder, int labelIdx, MemberRef field) {
		Class wrapper = null;
		if (field.type().isPrimitive()) {
			wrapper = PrimitiveUtil.wrapper(field.type());
		}
		builder.aload(1);
		builder.ldc(field.name());
		builder.invokeVirtual(Type.STRING, BloatJ2MEContext.EQUALS_METHODNAME,
				new Type[] { Type.OBJECT }, Type.BOOLEAN);
		builder.ifeq(labelIdx + 1);
		if (wrapper != null) {
			builder.newRef(wrapper);
			builder.dup();
		}
		builder.aload(0);
		builder.getfield(field);
		if (wrapper != null) {
			builder.invokeSpecial(_context
					.getType(wrapper), BloatJ2MEContext.INIT_METHODNAME,
					new Type[] { field.type() }, Type.VOID);
		}
		builder.areturn();
		builder.label(labelIdx + 1);
	}

	// TODO: Shouldn't this information be passed in by the calling class?
	// (It should know which classes it instruments anyway.)
	private boolean instrumentedType(Type type) {
		String typeName = _context.normalizeClassName(type.className());
		System.err.println(typeName);
		return !(typeName.startsWith("java.") || typeName.startsWith("javax.") || typeName
				.startsWith("sun."));

	}

	protected void generateSelf_set(MemberRef[] fields) {
		MethodBuilder builder = new MethodBuilder(_context, _ce,
				Modifiers.PUBLIC, Void.TYPE, SELFSET_METHODNAME, new Class[] {
						String.class, Object.class }, null);

		for (int fieldIdx = 0; fieldIdx < fields.length; fieldIdx++) {
			generateSelfSetFieldCase(builder, fieldIdx, fields[fieldIdx]);
		}

		Type superType = _ce.superclass();
		if (instrumentedType(superType)) {
			builder.aload(0);
			builder.aload(1);
			builder.aload(2);
			builder.invokeSpecial(superType, SELFSET_METHODNAME,
					new Type[] { Type.STRING, Type.OBJECT }, Type.VOID);

		} else {
			builder.ldc(null);
		}

		builder.returnInstruction();
		builder.commit();

	}

	private void generateSelfSetFieldCase(MethodBuilder builder, int labelIdx, MemberRef field) {
		Type fieldType = field.type();

		Class wrapper = PrimitiveUtil.wrapper(fieldType);
		builder.aload(1);
		builder.ldc(field.name());
		builder.invokeVirtual(Type.STRING, BloatJ2MEContext.EQUALS_METHODNAME,
				new Type[] { Type.OBJECT }, Type.BOOLEAN);
		builder.ifeq(labelIdx + 1);
		builder.aload(0);
		builder.aload(2);
		if (wrapper != null) {
			builder.checkcast(wrapper);
			builder.invokeVirtual(_context
					.getType(wrapper), PrimitiveUtil.conversionFunctionName(wrapper), new Type[0], fieldType);
		} else {
			builder.checkcast(fieldType);
		}
		builder.putfield(field);
		builder.returnInstruction();
		builder.label(labelIdx + 1);
	}

	public void generate() {
		addInterfaceIfNeeded();
		if (!(inspectNoArgConstr(_ce.methods()))) {
			_context.addNoArgConstructor(_ce);
		}
		MemberRef[] declaredFields = _context.collectDeclaredFields(_ce);
		generateSelf_get(declaredFields);
		generateSelf_set(declaredFields);
	}

	private void addInterfaceIfNeeded() {
		if(!instrumentedType(_ce.superclass())&&!implementsSelfReflectable(_ce)) {
			_ce.addInterface(SelfReflectable.class);
		}
	}

	private boolean implementsSelfReflectable(ClassEditor ce) {
		Type[] interfaces = ce.interfaces();

		for (int interfIdx = 0; interfIdx < interfaces.length; interfIdx++) {
			if (interfaces[interfIdx].getClass().equals(SelfReflectable.class)) {
				return true;
			}
		}
		return false;
	}

}
