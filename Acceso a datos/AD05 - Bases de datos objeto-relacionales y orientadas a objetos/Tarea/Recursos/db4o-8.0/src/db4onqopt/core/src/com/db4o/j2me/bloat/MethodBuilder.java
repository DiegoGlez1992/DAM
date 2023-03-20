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

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;

//TODO extract generic functionality and move to db4otools
public class MethodBuilder {
	private Map _labels;

	private Map _localVars;

	private MethodEditor _editor;

	private BloatJ2MEContext _context;

	public MethodBuilder(BloatJ2MEContext context, ClassEditor classEditor,
			int modifiers, Class type, String name, Class[] params,
			Class[] exceptions) {
		_context = context;
		_editor = new MethodEditor(classEditor, modifiers, type, name, params,
				exceptions);
		_labels = new HashMap();
		_localVars = new HashMap();
		label(0);
	}

	public void label(int id) {
		Label label = forceLabel(id);
		_editor.addLabel(label);
	}

	private Label forceLabel(int id) {
		Integer key = new Integer(id);
		Label label = (Label) _labels.get(key);
		if (label == null) {
			label = new Label(id);
			_labels.put(key, label);
		}
		return label;
	}

	private LocalVariable forceLocalVar(int id) {
		Integer key = new Integer(id);
		LocalVariable localVar = (LocalVariable) _localVars.get(key);
		if (localVar == null) {
			localVar = new LocalVariable(id);
			_localVars.put(key, localVar);
		}
		return localVar;
	}

	public void aload(int id) {
		_editor.addInstruction(Opcode.opc_aload, forceLocalVar(id));
	}

	public void iload(int id) {
		_editor.addInstruction(Opcode.opc_iload, forceLocalVar(id));
	}

	public void newarray(Class clazz) {
		_editor.addInstruction(Opcode.opc_newarray, _context.getType(clazz));
	}

	public void astore(int id) {
		_editor.addInstruction(Opcode.opc_astore, forceLocalVar(id));
	}

	public void areturn() {
		_editor.addInstruction(Opcode.opc_areturn);
	}

	public void returnInstruction() {
		_editor.addInstruction(Opcode.opc_return);
	}

	public void invokeSpecial(Type parent, String name, Type[] params,
			Type ret) {
		invoke(Opcode.opc_invokespecial,parent,name,params,ret);
	}

	public void invokeVirtual(Type parent, String name, Type[] params,
			Type ret) {
		invoke(Opcode.opc_invokevirtual,parent,name,params,ret);
	}

	public void invokeStatic(Type parent, String name, Type[] params,
			Type ret) {
		invoke(Opcode.opc_invokestatic,parent,name,params,ret);
	}

	public void invokeInterface(Type parent, String name, Type[] params,
			Type ret) {
		invoke(Opcode.opc_invokeinterface,parent,name,params,ret);
	}

	private void invoke(int mode, Type parent, String name, Type[] params,
			Type ret) {
		_editor.addInstruction(mode, _context.methodRef(parent, name, params,
				ret));
	}

	public void newRef(Class clazz) {
		_editor.addInstruction(Opcode.opc_new, _context.getType(clazz));
	}

	public void dup() {
		_editor.addInstruction(Opcode.opc_dup);
	}

	public void athrow() {
		_editor.addInstruction(Opcode.opc_athrow);
	}

	public void ldc(int constant) {
		ldc(new Integer(constant));
	}

	public void ldc(boolean constant) {
		ldc(constant ? 1 : 0);
	}

	public void ldc(Object constant) {
		_editor.addInstruction(Opcode.opc_ldc, constant);
	}

	public void ifeq(int labelId) {
		_editor.addInstruction(Opcode.opc_ifeq, forceLabel(labelId));
	}

	public void addTryCatch(int from, int to, int handler, Class thrown) {
		_editor.addTryCatch(new TryCatch(forceLabel(from), forceLabel(to),
				forceLabel(handler), _context.getType(thrown)));
	}

	public void getstatic(Class parent, Class type, String name) {
		getstatic(_context.getType(parent), type, name);
	}

	public void getstatic(Type parent, Class type, String name) {
		getstatic(parent, _context.getType(type), name);
	}

	public void getstatic(Type parent, Type type, String name) {
		_editor.addInstruction(Opcode.opc_getstatic, _context.fieldRef(parent,
				type, name));
	}

	public void putstatic(Type parent, Class type, String name) {
		_editor.addInstruction(Opcode.opc_putstatic, _context.fieldRef(parent,
				_context.getType(type), name));
	}

	public void checkcast(Class type) {
		checkcast(_context.getType(type));
	}

	public void checkcast(Type type) {
		_editor.addInstruction(Opcode.opc_checkcast, type);
	}

	public void commit() {
		_editor.commit();
	}

	public MemberRef memberRef() {
		return _editor.memberRef();
	}

	public Type parentType() {
		return _editor.declaringClass().type();
	}

	public void getfield(MemberRef field) {
		_editor.addInstruction(Opcode.opc_getfield, field);
	}

	public void putfield(MemberRef field) {
		_editor.addInstruction(Opcode.opc_putfield, field);
	}

	public void anewarray(Class clazz) {
		_editor.addInstruction(Opcode.opc_newarray, _context.getType(clazz));
	}

	public void aastore() {
		_editor.addInstruction(Opcode.opc_aastore);
	}

	public void pop() {
		_editor.addInstruction(Opcode.opc_pop);
	}

	public void invokeLoadClassConstMethod(Class clazz) {
		invokeLoadClassConstMethod(clazz.getName());
	}

	public void invokeLoadClassConstMethod(String clazzName) {
		_context.invokeLoadClassConstMethod(this, clazzName);
	}
}
