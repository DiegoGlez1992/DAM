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
package com.db4o.instrumentation.bloat;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.editor.Type;
import EDU.purdue.cs.bloat.reflect.*;

import com.db4o.instrumentation.api.*;
import com.db4o.instrumentation.util.*;

class BloatMethodBuilder implements MethodBuilder {
	
	private final MethodEditor methodEditor;
	private final LabelGenerator _labelGen;
	private final BloatReferenceProvider _references;
	private final Map _conversions = new HashMap();

	BloatMethodBuilder(BloatReferenceProvider references, ClassEditor classEditor, String methodName, TypeRef returnType, TypeRef[] parameterTypes) {
		_references = references;
		methodEditor = new MethodEditor(classEditor, Modifiers.PUBLIC, BloatTypeRef.bloatType(returnType), methodName, BloatTypeRef.bloatTypes(parameterTypes), new Type[]{});
		_labelGen = new LabelGenerator();
		methodEditor.addLabel(_labelGen.createLabel(true));
		setUpConversions();
	}

	public void invoke(final MethodRef method, CallingConvention convention) {
		if (convention == CallingConvention.INTERFACE) {
			invokeInterface(method);
		} else if (convention == CallingConvention.STATIC) {
			invokeStatic(method);
		} else {
			invokeVirtual(method);
		}
	}

	private void invokeInterface(final MethodRef method) {
		addInstruction(Opcode.opc_invokeinterface, memberRef(method));
	}
	
	private void invokeVirtual(final MethodRef methodRef) {
		addInstruction(Opcode.opc_invokevirtual, memberRef(methodRef));
	}
	
	private void invokeStatic(final MethodRef methodRef) {
		addInstruction(Opcode.opc_invokestatic, memberRef(methodRef));
	}

	public void ldc(Object value) {
		addInstruction(Opcode.opc_ldc, coerce(value));
	}
	
	public void loadArgument(final int index) {
		addInstruction(Opcode.opc_aload, new LocalVariable(index));
	}
	
	public void pop() {
		addInstruction(Opcode.opc_pop);
	}
	
	private MemberRef memberRef(Object ref) {
		return ((BloatMemberRef)ref).member();
	}

	public void endMethod() {
		addLabel(false);
		addInstruction(Opcode.opc_return);
		addLabel(true);
		methodEditor.commit();
	}

	private void addLabel(final boolean startsBlock) {
		methodEditor.addLabel(_labelGen.createLabel(startsBlock));
	}

	public void addInstruction(final int opcode) {
		methodEditor.addInstruction(opcode);
	}

	public void print(PrintStream out) {
		methodEditor.print(out);
	}

	public void loadArrayElement(TypeRef elementType) {
		addInstruction(arrayElementOpcode(elementType));
	}

	private int arrayElementOpcode(TypeRef elementType) {
		if(elementType==integerType()) {
			return Opcode.opc_iaload;
		}
		if(elementType==longType()) {
			return Opcode.opc_laload;
		}
		if(elementType==floatType()) {
			return Opcode.opc_faload;
		}
		if(elementType==doubleType()) {
			return Opcode.opc_daload;
		}
		return Opcode.opc_aaload;
	}

	private TypeRef doubleType() {
		return type(Double.TYPE);
	}

	private TypeRef floatType() {
		return type(Float.TYPE);
	}

	private TypeRef longType() {
		return type(Long.TYPE);
	}

	private TypeRef integerType() {
		return type(Integer.TYPE);
	}

	private TypeRef type(Class type) {
		return _references.forType(type);
	}

	public void addInstruction(Instruction instruction) {
		methodEditor.addInstruction(instruction);
	}
	
	public void addInstruction(int opcode, Object operand) {
		methodEditor.addInstruction(opcode, operand);
	}

	public void add(TypeRef operandType) {
		addInstruction(addOpcode(operandType));
	}

	private int addOpcode(TypeRef operandType) {
		if(operandType==doubleType()) {
			return Opcode.opc_dadd;
		}
		if(operandType==floatType()) {
			return Opcode.opc_fadd;
		}
		if(operandType==longType()) {
			return Opcode.opc_ladd;
		}
		return Opcode.opc_iadd;
	}

	public void subtract(TypeRef operandType) {
		addInstruction(subOpcode(operandType));
	}

	private int subOpcode(TypeRef operandType) {
		if(operandType==doubleType()) {
			return Opcode.opc_dsub;
		}
		if(operandType==floatType()) {
			return Opcode.opc_fsub;
		}
		if(operandType==longType()) {
			return Opcode.opc_lsub;
		}
		return Opcode.opc_isub;
	}

	public void multiply(TypeRef operandType) {
		addInstruction(multOpcode(operandType));
	}

	private int multOpcode(TypeRef operandType) {
		if(operandType==doubleType()) {
			return Opcode.opc_dmul;
		}
		if(operandType==floatType()) {
			return Opcode.opc_fmul;
		}
		if(operandType==longType()) {
			return Opcode.opc_lmul;
		}
		return Opcode.opc_imul;
	}

	public void divide(TypeRef operandType) {
		addInstruction(divOpcode(operandType));
	}

	public void modulo(TypeRef operandType) {
		addInstruction(modOpcode(operandType));
	}

	private int divOpcode(TypeRef operandType) {
		if(operandType==doubleType()) {
			return Opcode.opc_ddiv;
		}
		if(operandType==floatType()) {
			return Opcode.opc_fdiv;
		}
		if(operandType==longType()) {
			return Opcode.opc_ldiv;
		}
		return Opcode.opc_idiv;
	}

	private int modOpcode(TypeRef operandType) {
		if(operandType==doubleType()) {
			return Opcode.opc_drem;
		}
		if(operandType==floatType()) {
			return Opcode.opc_frem;
		}
		if(operandType==longType()) {
			return Opcode.opc_lrem;
		}
		return Opcode.opc_irem;
	}

	public void invoke(Method method) {
		final MethodRef methodRef = _references.forMethod(method);
		if (isStatic(method)) {
			invokeStatic(methodRef); 
		} else {
			invokeVirtual(methodRef);
		}
	}	
	
	private boolean isStatic(Method method) {
		return (method.getModifiers()&Modifier.STATIC)!=0;
	}

	public ReferenceProvider references() {
		return _references;
	}

	public void loadField(FieldRef fieldRef) {
		addInstruction(Opcode.opc_getfield, memberRef(fieldRef));
	}

	public void loadStaticField(FieldRef fieldRef) {
		addInstruction(Opcode.opc_getstatic, memberRef(fieldRef));
	}
	
	public void box(TypeRef boxedType) {
		Class[] convSpec=(Class[])_conversions.get(boxedType);
		if (null == convSpec) {
			return;
		}
		
		final Class wrapperType = convSpec[0];
		final Class primitiveType = convSpec[1];
		
		final LocalVariable local = methodEditor.newLocal(bloatType(primitiveType));
		addInstruction(storeOpcode(primitiveType), local);
		addInstruction(Opcode.opc_new, bloatType(wrapperType));
		addInstruction(Opcode.opc_dup);
		addInstruction(loadOpcode(primitiveType), local);
		addInstruction(Opcode.opc_invokespecial, memberRef(_references.forMethod(type(convSpec[0]),"<init>",new TypeRef[]{type(primitiveType)},type(Void.TYPE))));
	}
	
	private int loadOpcode(Class type) {
		if(type==Long.TYPE) {
			return Opcode.opc_lload;
		}
		if(type==Float.TYPE) {
			return Opcode.opc_fload;
		}
		if(type==Double.TYPE) {
			return Opcode.opc_dload;
		}
		return Opcode.opc_iload;
	}

	private int storeOpcode(Class type) {
		if(type==Long.TYPE) {
			return Opcode.opc_lstore;
		}
		if(type==Float.TYPE) {
			return Opcode.opc_fstore;
		}
		if(type==Double.TYPE) {
			return Opcode.opc_dstore;
		}
		return Opcode.opc_istore;
	}

	private Type bloatType(final Class type) {
		return _references.bloatType(type);
	}
	
	private void setUpConversions() {
		setUpConversion(new Class[]{Integer.class,Integer.TYPE});
		setUpConversion(new Class[]{Long.class,Long.TYPE});
		setUpConversion(new Class[]{Short.class,Short.TYPE});
		setUpConversion(new Class[]{Byte.class,Byte.TYPE});
		setUpConversion(new Class[]{Double.class,Double.TYPE});
		setUpConversion(new Class[]{Float.class,Float.TYPE});
		setUpConversion(new Class[]{Boolean.class,Boolean.TYPE});
	}
	
	private void setUpConversion(Class[] classes) {
		for (int i = 0; i < classes.length; i++) {
			_conversions.put(type(classes[i]), classes);
		}
	}

	private Object coerce(Object value) {
		if(value instanceof Boolean) {
			return ((Boolean)value).booleanValue() ? new Integer(1) : new Integer(0);
		}
		if(value instanceof Character) {
			return new Integer(((Character)value).charValue());
		}
		if(value instanceof Byte || value instanceof Short) {
			return new Integer(((Number)value).intValue());
		}
		return value;
	}
}
