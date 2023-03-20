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
package com.db4o.ta.instrumentation;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;

import EDU.purdue.cs.bloat.editor.ClassEditor;
import EDU.purdue.cs.bloat.editor.EditorVisitor;
import EDU.purdue.cs.bloat.editor.FieldEditor;
import EDU.purdue.cs.bloat.editor.Instruction;
import EDU.purdue.cs.bloat.editor.LocalVariable;
import EDU.purdue.cs.bloat.editor.MemberRef;
import EDU.purdue.cs.bloat.editor.MethodEditor;
import EDU.purdue.cs.bloat.editor.NameAndType;
import EDU.purdue.cs.bloat.editor.Opcode;
import EDU.purdue.cs.bloat.editor.Type;

import com.db4o.activation.*;
import com.db4o.foundation.*;
import com.db4o.instrumentation.core.*;
import com.db4o.instrumentation.util.*;
import com.db4o.ta.*;

/**
 * @exclude
 */
class InstrumentFieldAccessEdit implements BloatClassEdit {

	private ClassFilter _filter;
	
	public InstrumentFieldAccessEdit(ClassFilter filter) {
		_filter = filter;
	}
	public InstrumentationStatus enhance(ClassEditor ce, ClassLoader origLoader, BloatLoaderContext loaderContext) {
		if(isAlreadyInstrumented(ce)) {
			return InstrumentationStatus.FAILED;
		}
		return instrumentAllMethods(ce, origLoader, loaderContext);
	}
	
	private boolean isAlreadyInstrumented(ClassEditor ce) {
		return BloatUtil.implementsDirectly(ce, ActivatableInstrumented.class);
	}

	private InstrumentationStatus instrumentAllMethods(final ClassEditor ce, final ClassLoader origLoader, final BloatLoaderContext loaderContext) {
		final MemberRef activateMethod = createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Type[]{}, Type.VOID);
		final MemberRef bindMethod = createMethodReference(ce.type(), TransparentActivationInstrumentationConstants.BIND_METHOD_NAME, new Type[]{ Type.getType(Activator.class) }, Type.VOID);
		final ObjectByRef instrumented = new ObjectByRef(InstrumentationStatus.NOT_INSTRUMENTED);
		ce.visit(new EditorVisitor() {

			public void visitClassEditor(ClassEditor editor) {
				editor.addInterface(ActivatableInstrumented.class);
			}

			public void visitFieldEditor(FieldEditor editor) {
			}

			public void visitMethodEditor(MethodEditor editor) {
				if(editor.isAbstract()) {
					return;
				}
				MemberRef methodRef = editor.memberRef();
				if(methodRef.equals(activateMethod) || methodRef.equals(bindMethod)) {
					return;
				}

				TreeMap fieldAccessIndexes = new TreeMap(new Comparator() {
					public int compare(Object o1, Object o2) {
						return -((Comparable)o1).compareTo(o2);
					}
				});
				for(int codeIdx = 0; codeIdx < editor.codeLength(); codeIdx++) {
					Object curCode = editor.codeElementAt(codeIdx);
					MemberRef fieldRef = fieldRef(curCode);
					if(fieldRef == null || !accept(fieldRef)) {
						continue;
					}
					boolean writeAccess = ((Instruction)curCode).origOpcode() == Opcode.opc_putfield;						
					fieldAccessIndexes.put(
							new Integer(codeIdx),
							new FieldAccess(
									fieldRef,
									writeAccess
										? ActivationPurpose.WRITE
										: ActivationPurpose.READ));
				}
				if(fieldAccessIndexes.isEmpty()) {
					return;
				}
				try {					
					int modifiedCount = 0;
					for (Iterator idxIter = fieldAccessIndexes.keySet().iterator(); idxIter.hasNext();) {
						Integer idx = ((Integer) idxIter.next());
						FieldAccess fieldAccess = (FieldAccess)fieldAccessIndexes.get(idx);
						if (instrumentFieldAccess(loaderContext, editor, idx, fieldAccess)) {
							modifiedCount++;
						}
					}
					editor.commit();
					if (modifiedCount > 0) {
						instrumented.value = InstrumentationStatus.INSTRUMENTED;
					}
					
				} catch (ClassNotFoundException e) {
					instrumented.value = InstrumentationStatus.FAILED;
					return;
				}
			}

			private boolean instrumentFieldAccess(
					final BloatLoaderContext loaderContext,
					MethodEditor editor, Integer idx,
					FieldAccess fieldAccess) throws ClassNotFoundException {
				
				MemberRef fieldRef = fieldAccess.fieldRef;
				ClassEditor fieldParentClassEditor = loaderContext.classEditor(fieldRef.declaringClass());
				if (!isPersistentField(loaderContext, fieldParentClassEditor, fieldRef)) {
					return false;
				}
				if(editor.isConstructor()) {
					if(editor.declaringClass().name().equals(fieldParentClassEditor.name())) {
						return false;
					}
				}
				
				final MemberRef targetActivateMethod = createMethodReference(fieldRef.declaringClass(),  TransparentActivationInstrumentationConstants.ACTIVATE_METHOD_NAME, new Type[]{ Type.getType(ActivationPurpose.class)}, Type.VOID);
				if(targetActivateMethod == null) {
					return false;
				}
				
				int insertionPoint = idx.intValue();				
				if (ActivationPurpose.WRITE == fieldAccess.purpose) {
					LoadStoreInstructions instructions = BloatUtil.loadStoreInstructionsFor(fieldRef.type());
					LocalVariable temp = editor.newLocal(fieldRef.type());
					editor.insertCodeAt(new Instruction(instructions.store, temp), insertionPoint++);					
					insertionPoint = insertActivateCall(editor, targetActivateMethod, insertionPoint, ActivationPurpose.WRITE);					
					editor.insertCodeAt(new Instruction(instructions.load, temp), insertionPoint);
					
				} else {				
					insertActivateCall(editor, targetActivateMethod, insertionPoint, ActivationPurpose.READ);
				}
				
				return true;
			}			

			private int insertActivateCall(MethodEditor editor,
					final MemberRef targetActivateMethod, int insertionPoint, ActivationPurpose purpose) {
				
				editor.insertCodeAt(new Instruction(Opcode.opc_dup), insertionPoint);
				editor.insertCodeAt(new Instruction(Opcode.opc_getstatic, purposeFieldFor(purpose)), ++insertionPoint);
				editor.insertCodeAt(new Instruction(Opcode.opc_invokevirtual, targetActivateMethod), ++insertionPoint);
				return ++insertionPoint;
			}

			private MemberRef purposeFieldFor(ActivationPurpose purpose) {
				String fieldName = ActivationPurpose.READ == purpose ? "READ" : "WRITE";
				return createMemberRef(Type.getType(ActivationPurpose.class), fieldName, Type.getType(ActivationPurpose.class));
			}

			private boolean isPersistentField(
					final BloatLoaderContext loaderContext,
					final ClassEditor ce, MemberRef fieldRef)
					throws ClassNotFoundException {
				FieldEditor fieldEdit = loaderContext.field(ce, fieldRef.name(), fieldRef.type());
				return !fieldEdit.isTransient() && !fieldEdit.isStatic();
			}

			private boolean accept(MemberRef fieldRef) {
				String className = fieldRef.declaringClass().className();
				String normalizedClassName = BloatUtil.normalizeClassName(className);
				try {
					final Class<?> clazz = origLoader.loadClass(normalizedClassName);
					if (clazz.isEnum()) {
						return false;
					}
					return _filter.accept(clazz);
				} catch (ClassNotFoundException e) {
					// TODO: sensible error notification.
					e.printStackTrace();
					return false;
				}
			}

			private MemberRef fieldRef(Object code) {
				if(!(code instanceof Instruction)) {
					return null;
				}
				Instruction curInstr = (Instruction)code;
				if(curInstr.origOpcode() == Opcode.opc_getfield
					|| curInstr.origOpcode() == Opcode.opc_putfield) {
					return (MemberRef) curInstr.operand();
				}
				return null;
			}

		});
		if(((InstrumentationStatus)instrumented.value).isInstrumented()) {
			ce.commit();
		}
		return (InstrumentationStatus) instrumented.value;
	}
	private MemberRef createMethodReference(Type parent, String name, Type[] args, Type ret) {
		return createMemberRef(parent, name, Type.getType(args, ret));
	}
	private MemberRef createMemberRef(Type parent, String name, Type type) {
		NameAndType nameAndType = new NameAndType(name, type);
		return new MemberRef(parent, nameAndType);
	}
}
