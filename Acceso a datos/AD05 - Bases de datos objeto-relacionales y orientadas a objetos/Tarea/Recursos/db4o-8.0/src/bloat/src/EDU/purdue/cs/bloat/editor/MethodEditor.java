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
package EDU.purdue.cs.bloat.editor;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * <tt>MethodEditor</tt> provides a means to edit a method of a class. A
 * <tt>MethodEditor</tt> gathers information from a <tt>MethodInfo</tt>
 * object. It then goes through the bytecodes of the method and extracts
 * information about the method. Along the way it creates an array of
 * <tt>Instruction</tt> and <tt>Label</tt> objects that represent the code.
 * Additionally, it models the try-catch blocks in the method and their
 * associated exception handlers.
 * 
 * @see EDU.purdue.cs.bloat.reflect.MethodInfo
 * @see Label
 * @see Instruction
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class MethodEditor implements Opcode {
	public static boolean PRESERVE_DEBUG = true;

	public static boolean UNIQUE_HANDLERS = false;

	public static boolean OPT_STACK_2 = false; // byte-code level stack opt

	private ClassEditor editor; // The editor that "owns" this MethodEditor

	private MethodInfo methodInfo; // Representation of this method

	private String name; // The name of this method

	private Type type; // Type variable representing the class's

	// descriptor
	private LinkedList code; // Label and Instruction objects representing
								// this

	// method's bytecode
	private LinkedList tryCatches; // Info about the try-catch blocks in this
									// method

	private LinkedList lineNumbers;

	private LocalVariable[] params; // The parameters to this method

	private int maxStack; // Max size of stack while running this method

	private int maxLabel; // Label pointing to the end of the code

	private int maxLocals; // Maximum number of local variables

	private boolean isDirty; // Has the method been modified?

	private Map locals; // Maps indices to that LocalVariable

	private Type[] paramTypes; // Types of parameters (accounts for wides)

	public UseMap uMap; // Structure for remembering use/def info

	private boolean isDeleted = false;

	public MethodEditor(final ClassEditor editor, final int modifiers,
			final Class returnType, final String methodName,
			final Class[] paramTypes, final Class[] exceptionTypes) {

		this(editor, modifiers, (returnType == null ? null : Type
				.getType(returnType)), methodName, MethodEditor
				.convertTypes(paramTypes), MethodEditor
				.convertTypes(exceptionTypes));
	}

	private static Type[] convertTypes(final Class[] classes) {
		if (classes == null) {
			return (null);
		}

		final Type[] types = new Type[classes.length];
		for (int i = 0; i < types.length; i++) {
			types[i] = Type.getType(classes[i]);
		}
		return (types);
	}

	/**
	 * Creates a new <code>MethodEditor</code> for editing a method in a given
	 * class with the given modifiers, return type, name, parameter types, and
	 * exception types.
	 * 
	 * @param modifiers
	 *            The {@link EDU.purdue.cs.bloat.reflect.Modifiers modifiers}
	 *            for the new method
	 * @param returnType
	 *            The return type of the method. If, <code>returnType</code>
	 *            is null, the return type is assumed to be <code>void</code>.
	 * @param methodName
	 *            The name of the method
	 * @param paramTypes
	 *            The types of the parameters to the new method. If
	 *            <code>paramTypes</code> is <code>null</code>, then we
	 *            assume that there are no arguments.
	 * @param exceptionTypes
	 *            The types of exceptions that may be thrown by the new method.
	 *            If <code>exceptionTypes</code> is <code>null</code>, then
	 *            we assume that no exceptions are declared.
	 */
	public MethodEditor(final ClassEditor editor, final int modifiers,
			Type returnType, final String methodName, Type[] paramTypes,
			Type[] exceptionTypes) {

		// if(ClassEditor.DEBUG) {
		// System.out.println("Creating MethodEditor " +
		// System.identityHashCode(this));
		// Thread.dumpStack();
		// }

		this.editor = editor;
		this.name = methodName;

		if (returnType == null) {
			returnType = Type.VOID;
		}

		if (paramTypes == null) {
			paramTypes = new Type[0];
		}

		if (exceptionTypes == null) {
			exceptionTypes = new Type[0];
		}

		// Get the indices in the constant pool for all sorts of
		// interesting information
		final ConstantPool cp = editor.constants();
		final int nameIndex = cp.getUTF8Index(methodName);
		this.type = Type.getType(paramTypes, returnType);
		Assert.isTrue(this.type.isMethod(), "Method type not method: "
				+ this.type);
		final int typeIndex = cp.getTypeIndex(this.type);
		final int exceptionIndex = cp.getUTF8Index("Exceptions");

		final int[] exceptionTypeIndices = new int[exceptionTypes.length];
		for (int i = 0; i < exceptionTypes.length; i++) {
			final Type eType = exceptionTypes[i];
			exceptionTypeIndices[i] = cp.getTypeIndex(eType);
		}

		final int codeIndex = cp.getUTF8Index("Code");

		final ClassInfo classInfo = editor.classInfo();
		this.methodInfo = classInfo.addNewMethod(modifiers, typeIndex,
				nameIndex, exceptionIndex, exceptionTypeIndices, codeIndex);

		// Initialize other parts of this MethodEditor as best we can
		this.code = new LinkedList();
		this.tryCatches = new LinkedList();
		this.lineNumbers = new LinkedList();
		this.locals = new HashMap();

		// Be sure to include space for the this pointer.
		if (!isStatic()) {
			this.params = new LocalVariable[type.stackHeight() + 1];

		} else {
			this.params = new LocalVariable[type.stackHeight()];
		}

		// Initalize the params to hold LocalVariables representing the
		// parameters
		this.paramTypes = new Type[this.params.length];
		final Type[] indexedParams = this.type().indexedParamTypes();
		if (!isStatic()) {
			// First parameter is the this pointer
			this.paramTypes[0] = this.declaringClass().type();
			for (int q = 1; q < this.paramTypes.length; q++) {
				this.paramTypes[q] = indexedParams[q - 1];
			}

		} else {
			for (int q = 0; q < this.paramTypes.length; q++) {
				this.paramTypes[q] = indexedParams[q];
			}
		}

		for (int q = 0; q < this.params.length; q++) {
			this.params[q] = new LocalVariable(null, this.paramTypes[q], q);
		}

		this.maxLocals = this.paramTypes.length;

		this.isDirty = true;
	}

	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            The class containing the method.
	 * @param methodInfo
	 *            The method to edit.
	 * 
	 * @see ClassEditor
	 * @see EDU.purdue.cs.bloat.reflect.MethodInfo MethodInfo
	 */
	public MethodEditor(final ClassEditor editor, final MethodInfo methodInfo) {
		// if(ClassEditor.DEBUG) {
		// System.out.println("Creating MethodEditor " +
		// System.identityHashCode(this));
		// Thread.dumpStack();
		// }

		final ConstantPool cp = editor.constants();

		this.methodInfo = methodInfo;
		this.editor = editor;
		this.isDirty = false;

		maxLabel = 0;
		maxLocals = methodInfo.maxLocals();
		maxStack = methodInfo.maxStack();
		locals = new HashMap();

		int index;
		int i;
		int j;

		index = methodInfo.nameIndex();
		name = (String) cp.constantAt(index);

		index = methodInfo.typeIndex();
		final String typeName = (String) cp.constantAt(index);
		type = Type.getType(typeName);

		code = new LinkedList();
		tryCatches = new LinkedList();
		lineNumbers = new LinkedList();

		// Be sure to include space for the this pointer.
		if (!isStatic()) {
			params = new LocalVariable[type.stackHeight() + 1];
		} else {
			params = new LocalVariable[type.stackHeight()];
		}

		// Initalize the params to hold LocalVariables representing the
		// parameters
		paramTypes = new Type[params.length];
		final Type[] indexedParams = this.type().indexedParamTypes();
		if (!isStatic()) {
			// First parameter is the this pointer
			paramTypes[0] = this.declaringClass().type();
			for (int q = 1; q < paramTypes.length; q++) {
				paramTypes[q] = indexedParams[q - 1];
			}

		} else {
			for (int q = 0; q < paramTypes.length; q++) {
				paramTypes[q] = indexedParams[q];
			}
		}
		for (int q = 0; q < params.length; q++) {
			params[q] = new LocalVariable(null, paramTypes[q], q);
		}

		// Get the byte code for this method
		final byte[] array = methodInfo.code();

		if ((array == null) || (array.length == 0)) {
			return;
		}

		// Build the array of Instructions (and Labels).
		//
		// next[i] contains the index of the instruction following i.
		// targets[i] contains an array of the branch targets of i.
		// lookups[i] contains an array of the switch lookup values of i.
		// label[i] contains a label if a label should be inserted before i.
		// lines[i] contains the line number of instruction i (or 0).
		//
		final int[] next = new int[array.length];
		final int[][] targets = new int[array.length][];
		final int[][] lookups = new int[array.length][];
		final Label[] label = new Label[array.length + 1];
		LocalVariable[][] localVars;

		if (MethodEditor.PRESERVE_DEBUG && (array.length < 0x10000)) {
			// LocalDebugInfo maps a local variable in the generated code
			// back to the name of a local variable in the original Java
			// source file.
			final LocalDebugInfo[] locals = methodInfo.locals();
			int max = 0;

			// Find the maximum local variable index for the code.
			for (i = 0; i < locals.length; i++) {
				if (max <= locals[i].index()) {
					max = locals[i].index() + 1;
				}
			}

			// localVars[i][j] contains the a LocalVariable, j, for
			// instruction i
			localVars = new LocalVariable[array.length][max];

			// Create LocalVariables for those locals with debug info
			// and set the params array so the name and type will be returned
			// be paramAt.
			//
			for (i = 0; i < locals.length; i++) {
				final int start = locals[i].startPC();
				final int end = start + locals[i].length();

				final String localName = (String) cp.constantAt(locals[i]
						.nameIndex());
				final String localType = (String) cp.constantAt(locals[i]
						.typeIndex());

				final LocalVariable var = new LocalVariable(localName, Type
						.getType(localType), locals[i].index());

				for (int pc = start; pc <= end; pc++) {
					if (pc < localVars.length) {
						localVars[pc][locals[i].index()] = var;
					}
				}

				if ((start == 0) && (locals[i].index() < params.length)) {
					params[locals[i].index()] = var;
				}
			}

			// Create a list of line number entries and add a label at the
			// start PC for each entry.
			final LineNumberDebugInfo[] lineNumbers = methodInfo.lineNumbers();

			for (i = 0; i < lineNumbers.length; i++) {
				final int start = lineNumbers[i].startPC();

				if (label[start] == null) {
					label[start] = new Label(start, false);
				}

				addLineNumberEntry(label[start], lineNumbers[i].lineNumber());
			}
		} else {
			// We're not preserving debugging information. So, we don't
			// need to worry about which local variables are live at
			// which instructions.

			localVars = new LocalVariable[array.length][0];
		}

		// Create a label for the beginning of the code and for each
		// branch target. Also set next[i] for all instructions i.
		//
		label[0] = new Label(0, true);

		int numInst = 0;

		for (i = 0; i < array.length; i = next[i]) {
			// Examine an instruction and extract its target labels
			// and switch lookups
			next[i] = munchCode(array, i, targets, lookups);
			numInst++;

			// Generate Labels for all the targets local to the code
			if (targets[i] != null) {
				for (j = 0; j < targets[i].length; j++) {
					if (targets[i][j] < array.length) {
						label[targets[i][j]] = new Label(targets[i][j], true);
					}
				}
			}
		}

		// Create a label for the beginning and end of protected blocks and the
		// beginning of catch blocks. Add a TryCatch entry for each
		// exception handler in the method.
		//
		final Catch[] exc = methodInfo.exceptionHandlers();

		for (i = 0; i < exc.length; i++) {
			final int start = exc[i].startPC();
			final int end = exc[i].endPC();
			final int handler = exc[i].handlerPC();

			label[start] = new Label(start, true);
			label[end] = new Label(end, true);
			label[handler] = new Label(handler, true);

			final Type catchType = (Type) cp
					.constantAt(exc[i].catchTypeIndex());

			addTryCatch(new TryCatch(label[start], label[end], label[handler],
					catchType));
		}

		// Go through the bytecode and create Instructions and build the
		// code linked list.
		// Add a label for instructions following branches.
		for (i = 0; i < array.length; i = next[i]) {
			final Instruction inst = new Instruction(array, i, targets[i],
					lookups[i], localVars[i], cp);

			if (label[i] != null) {
				code.add(label[i]);
			}

			code.add(inst);

			if (inst.isJump() || inst.isReturn() || inst.isJsr()
					|| inst.isRet() || inst.isThrow() || inst.isSwitch()) {

				// Add a label for the next instruction after a branch.
				if (next[i] < array.length) {
					label[next[i]] = new Label(next[i], true);
				}
			}
		}

		// Add a label at the end. This label must start a block.
		label[array.length] = new Label(array.length, true);
		code.add(label[array.length]);

		maxLabel = array.length + 1;

		if (ClassEditor.DEBUG) {
			System.out.println("Editing method " + name + " " + type);
		}

		if (MethodEditor.OPT_STACK_2) {
			uMap = new UseMap(); // structure for remembering use/def info.
		}

		this.setDirty(false);
	}

	/**
	 * Returns the <tt>Type</tt>s of exceptions that this method may throw.
	 */
	public Type[] exceptions() {
		final ConstantPool cp = editor.constants();
		final int[] indices = methodInfo.exceptionTypes();
		final Type[] types = new Type[indices.length];

		for (int i = 0; i < indices.length; i++) {
			types[i] = (Type) cp.constantAt(indices[i]);
		}

		return (types);
	}

	/**
	 * Returns <tt>true</tt> if this method has been modified.
	 */
	public boolean isDirty() {
		return (this.isDirty);
	}

	/**
	 * Sets the dirty flag of this method. The dirty flag is <tt>true</tt> if
	 * the method has been modified.
	 */
	public void setDirty(final boolean dirty) {
		this.isDirty = dirty;
		if (isDirty == true) {
			this.editor.setDirty(true);
		}
	}

	/**
	 * Marks this method for deletion. Once a method has been marked for
	 * deletion all attempts to change it will throw an
	 * <code>IllegalStateException</code>.
	 */
	public void delete() {
		this.setDirty(true);
		this.isDeleted = true;
	}

	/**
	 * Returns an array of <tt>Type</tt>s representing the types of the
	 * parameters of this method. It's really used to figure out the type of the
	 * local variables that hold the parameters. So, wide data is succeeded by
	 * an empty slot. Also, for virtual methods, the first element in the array
	 * is the receiver.
	 */
	public Type[] paramTypes() {
		return (this.paramTypes);
	}

	/**
	 * Get the LocalVariable for the parameter at the given index.
	 * 
	 * @param index
	 *            The index into the params (0 is the this pointer or the first
	 *            argument, if static).
	 * @return The LocalVariable for the parameter at the given index.
	 * 
	 */
	public LocalVariable paramAt(final int index) {
		if ((index >= params.length) || (params[index] == null)) {
			final LocalVariable local = new LocalVariable(index);
			if (index < params.length) {
				params[index] = local;
			}
			return (local);
		}

		return params[index];
	}

	/**
	 * Returns the raw MethodInfo of the method being edited.
	 */
	public MethodInfo methodInfo() {
		return methodInfo;
	}

	/**
	 * Returns the class which declared the method.
	 */
	public ClassEditor declaringClass() {
		return editor;
	}

	/**
	 * Returns the maximum number of locals used by the method.
	 */
	public int maxLocals() {
		return maxLocals;
	}

	public boolean isPublic() {
		return (methodInfo.modifiers() & Modifiers.PUBLIC) != 0;
	}

	public boolean isPrivate() {
		return (methodInfo.modifiers() & Modifiers.PRIVATE) != 0;
	}

	public boolean isProtected() {
		return (methodInfo.modifiers() & Modifiers.PROTECTED) != 0;
	}

	/**
	 * Returns true is the method has package level visibility
	 */
	public boolean isPackage() {
		return (!isPublic() && !isPrivate() && !isProtected());
	}

	public boolean isStatic() {
		return (methodInfo.modifiers() & Modifiers.STATIC) != 0;
	}

	public boolean isFinal() {
		return (methodInfo.modifiers() & Modifiers.FINAL) != 0;
	}

	public boolean isSynchronized() {
		return (methodInfo.modifiers() & Modifiers.SYNCHRONIZED) != 0;
	}

	public boolean isNative() {
		return (methodInfo.modifiers() & Modifiers.NATIVE) != 0;
	}

	public boolean isAbstract() {
		return (methodInfo.modifiers() & Modifiers.ABSTRACT) != 0;
	}

	/**
	 * Returns <tt>true</tt> if this method's class is an interface.
	 */
	public boolean isInterface() {
		return (editor.isInterface());
	}

	// TODO: Only change the methodInfo at commit time.
	// TODO: Add similar methods to field and class editors.
	/**
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void setPublic(final boolean flag) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		int mod = methodInfo.modifiers();

		if (flag) {
			mod |= Modifiers.PUBLIC;
		} else {
			mod &= ~Modifiers.PUBLIC;
		}

		methodInfo.setModifiers(mod);
		this.setDirty(true);
	}

	/**
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void setPrivate(final boolean flag) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		int mod = methodInfo.modifiers();

		if (flag) {
			mod |= Modifiers.PRIVATE;
		} else {
			mod &= ~Modifiers.PRIVATE;
		}

		methodInfo.setModifiers(mod);
		this.setDirty(true);
	}

	/**
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void setProtected(final boolean flag) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		int mod = methodInfo.modifiers();

		if (flag) {
			mod |= Modifiers.PROTECTED;
		} else {
			mod &= ~Modifiers.PROTECTED;
		}

		methodInfo.setModifiers(mod);
		this.setDirty(true);
	}

	/**
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void setStatic(final boolean flag) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		int mod = methodInfo.modifiers();

		if (flag) {
			mod |= Modifiers.STATIC;
		} else {
			mod &= ~Modifiers.STATIC;
		}

		methodInfo.setModifiers(mod);
		this.setDirty(true);
	}

	/**
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void setFinal(final boolean flag) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		int mod = methodInfo.modifiers();

		if (flag) {
			mod |= Modifiers.FINAL;
		} else {
			mod &= ~Modifiers.FINAL;
		}

		methodInfo.setModifiers(mod);
		this.setDirty(true);
	}

	/**
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void setSynchronized(final boolean flag) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		int mod = methodInfo.modifiers();

		if (flag) {
			mod |= Modifiers.SYNCHRONIZED;
		} else {
			mod &= ~Modifiers.SYNCHRONIZED;
		}

		methodInfo.setModifiers(mod);
		this.setDirty(true);
	}

	/**
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void setNative(final boolean flag) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		int mod = methodInfo.modifiers();

		if (flag) {
			mod |= Modifiers.NATIVE;
		} else {
			mod &= ~Modifiers.NATIVE;
		}

		methodInfo.setModifiers(mod);
		this.setDirty(true);
	}

	public void setAbstract(final boolean flag) {
		int mod = methodInfo.modifiers();

		if (flag) {
			mod |= Modifiers.ABSTRACT;
		} else {
			mod &= ~Modifiers.ABSTRACT;
		}

		methodInfo.setModifiers(mod);
		this.setDirty(true);
	}

	/**
	 * Scan the raw bytes of a single instruction, saving the indices of branch
	 * targets and the values of switch lookups. That is, gather information
	 * needed for creating <tt>Instruction</tt> instances.
	 * 
	 * @param code
	 *            The byte code array.
	 * @param index
	 *            The index into the code array.
	 * @param targets
	 *            Branch targets for the instruction scanned. This is set by the
	 *            method.
	 * @param lookups
	 *            Switch lookups for the instruction scanned. This is set by the
	 *            method.
	 * @return The index of the next instruction in the code array.
	 */
	private int munchCode(final byte[] code, final int index,
			final int[][] targets, final int[][] lookups) {
		final int opcode = Instruction.toUByte(code[index]);
		int next = index + Opcode.opcSize[opcode];

		switch (opcode) {
		case opc_ifeq:
		case opc_ifne:
		case opc_iflt:
		case opc_ifge:
		case opc_ifgt:
		case opc_ifle:
		case opc_if_icmpeq:
		case opc_if_icmpne:
		case opc_if_icmplt:
		case opc_if_icmpge:
		case opc_if_icmpgt:
		case opc_if_icmple:
		case opc_if_acmpeq:
		case opc_if_acmpne:
		case opc_ifnull:
		case opc_ifnonnull: {
			// Branch target
			final int target = Instruction.toShort(code[index + 1],
					code[index + 2]);
			targets[index] = new int[1];
			targets[index][0] = index + target;
			break;
		}
		case opc_goto:
		case opc_jsr: {
			// Branch target
			final int target = Instruction.toShort(code[index + 1],
					code[index + 2]);
			targets[index] = new int[1];
			targets[index][0] = index + target;
			break;
		}
		case opc_goto_w:
		case opc_jsr_w: {
			// Branch target
			final int target = Instruction.toInt(code[index + 1],
					code[index + 2], code[index + 3], code[index + 4]);
			targets[index] = new int[1];
			targets[index][0] = index + target;
			break;
		}
		case opc_ret: {
			// Unconditional branch to the address in a local variable.
			// Work on finding branch targets later.
			break;
		}
		case opc_tableswitch: {
			int target;
			int lo;
			int hi;
			int j;

			// The targets and low and high values are aligned on
			// 4-byte boundaries.
			for (j = index + 1; j % 4 != 0; j++) {
				// Empty statement.
			}

			// Read the default target.
			target = Instruction.toInt(code[j], code[j + 1], code[j + 2],
					code[j + 3]);
			j += 4;

			lo = Instruction.toInt(code[j], code[j + 1], code[j + 2],
					code[j + 3]);
			j += 4;

			hi = Instruction.toInt(code[j], code[j + 1], code[j + 2],
					code[j + 3]);
			j += 4;

			lookups[index] = new int[2];
			lookups[index][0] = lo;
			lookups[index][1] = hi;

			targets[index] = new int[hi - lo + 2];

			int k = 0;
			targets[index][k++] = index + target;

			next = j + (hi - lo + 1) * 4;

			while (j < next) {
				target = Instruction.toInt(code[j], code[j + 1], code[j + 2],
						code[j + 3]);
				j += 4;

				targets[index][k++] = index + target;
			}

			break;
		}
		case opc_lookupswitch: {
			int target;
			int value;
			int npairs;
			int j;

			// The targets and pairs are aligned on 4-byte boundaries.
			for (j = index + 1; j % 4 != 0; j++) {
				// Empty statement.
			}

			// Read the default target.
			target = Instruction.toInt(code[j], code[j + 1], code[j + 2],
					code[j + 3]);
			j += 4;

			npairs = Instruction.toInt(code[j], code[j + 1], code[j + 2],
					code[j + 3]);
			j += 4;

			lookups[index] = new int[npairs];
			targets[index] = new int[npairs + 1];

			int k = 0;
			targets[index][k++] = index + target;

			next = j + npairs * 8;

			while (j < next) {
				value = Instruction.toInt(code[j], code[j + 1], code[j + 2],
						code[j + 3]);
				j += 4;

				target = Instruction.toInt(code[j], code[j + 1], code[j + 2],
						code[j + 3]);
				j += 4;

				lookups[index][k - 1] = value;
				targets[index][k++] = index + target;
			}

			break;
		}
		case opc_wide: {
			if (code[index + 1] == (byte) Opcode.opc_iinc) {
				next = index + 6;
			} else {
				next = index + 4;
			}
			break;
		}
		}

		return next;
	}

	/**
	 * Remove all the instructions in preparation for the instructions being
	 * added back after a control flow graph edit.
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void clearCode() {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		if (ClassEditor.DEBUG) {
			System.out.println("Clearing code");
			Thread.dumpStack();
		}

		code.clear();
		tryCatches.clear();
		maxLocals = 0;
		maxStack = 0;
		this.setDirty(true);
	}

	/**
	 * Like clear code, but doesn't reset the maxLocals. I'm not really sure why
	 * this works, but it stops certain parts of code that is generated and then
	 * re-cfg'd from being eliminated as dead
	 */

	public void clearCode2() {
		code.clear();
		tryCatches.clear();
		maxStack = 0;
		this.setDirty(true);
	}

	/**
	 * Returns the name of the method.
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns <tt>true</tt> if the method being edited is a constructor.
	 */
	public boolean isConstructor() {
		return (name.equals("<init>"));
	}

	/**
	 * Returns the type of the method.
	 */
	public Type type() {
		return type;
	}

	/**
	 * Returns the <Tt>NameAndType</tt> of the method.
	 */
	public NameAndType nameAndType() {
		return (new NameAndType(this.name(), this.type()));
	}

	/**
	 * Returns a <tt>MemberRef</tt> for the method.
	 */
	public MemberRef memberRef() {
		return (new MemberRef(this.declaringClass().type(), this.nameAndType()));
	}

	/**
	 * Get the length of the code array.
	 * 
	 * @return The length of the code array.
	 */
	public int codeLength() {
		return code.size();
	}

	/**
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void setCode(final List v) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		if (ClassEditor.DEBUG) {
			System.out.println("Setting code to " + v.size() + " instructions");
			Thread.dumpStack();
		}
		code = new LinkedList(v);
		this.setDirty(true);
	}

	/**
	 * Returns the code (<tt>Instruction</tt>s and <Tt>Label</tt>s) in
	 * the method.
	 */
	public List code() {
		return code;
	}

	/**
	 * Get the label of the first block.
	 */
	public Label firstBlock() {
		final Iterator iter = code.iterator();

		while (iter.hasNext()) {
			final Object obj = iter.next();

			if (obj instanceof Label) {
				final Label l = (Label) obj;
				if (l.startsBlock()) {
					return l;
				}
			}
		}

		return null;
	}

	/**
	 * Get the label of the next block after the parameter.
	 * 
	 * @param label
	 *            The label at which to begin.
	 * @return The label.
	 */
	public Label nextBlock(final Label label) {
		boolean seen = false;

		final Iterator iter = code.iterator();

		while (iter.hasNext()) {
			final Object obj = iter.next();

			if (obj instanceof Label) {
				if (seen) {
					final Label l = (Label) obj;
					if (l.startsBlock()) {
						return l;
					}
				} else if (label.equals(obj)) {
					seen = true;
				}
			}
		}

		return null;
	}

	/**
	 * Removes a Label or Instruction from the code array.
	 * 
	 * @param i
	 *            The index of the element to remove.
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void removeCodeAt(final int i) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		code.remove(i);
		this.setDirty(true);
	}

	/**
	 * Inserts a Label or Instruction into the code array.
	 * 
	 * @param i
	 *            The index of the element to insert before.
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void insertCodeAt(final Object obj, final int i) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		code.add(i, obj);
		this.setDirty(true);
	}

	/**
	 * Replace a Label or Instruction in the code array.
	 * 
	 * @param obj
	 *            The new element.
	 * @param i
	 *            The index of the element to replace
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void replaceCodeAt(final Object obj, final int i) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		code.set(i, obj);
		this.setDirty(true);
	}

	/**
	 * Returns a Label or Instruction in the code array.
	 * 
	 * @param i
	 *            The index into the code array.
	 * @return The element at the index.
	 */
	public Object codeElementAt(final int i) {
		return code.get(i);
	}

	/**
	 * Add a line number entry.
	 * 
	 * @param label
	 *            The label beginning the range of instructions for this line
	 *            number.
	 * @param lineNumber
	 *            The line number.
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void addLineNumberEntry(final Label label, final int lineNumber) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		lineNumbers.add(new LineNumberEntry(label, lineNumber));
		this.setDirty(true);
	}

	/**
	 * Returns the number of exception handlers in the method.
	 */
	public int numTryCatches() {
		return tryCatches.size();
	}

	/**
	 * Returns the exception handlers (<tt>TryCatch</tt>) in the method.
	 */
	public Collection tryCatches() {
		return tryCatches;
	}

	/**
	 * Add an exception handler.
	 * 
	 * @param tryCatch
	 *            An exception handler.
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void addTryCatch(final TryCatch tryCatch) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		if (ClassEditor.DEBUG) {
			System.out.println("add " + tryCatch);
		}

		tryCatches.add(tryCatch);
		this.setDirty(true);
	}

	class LineNumberEntry {
		Label label;

		int lineNumber;

		public LineNumberEntry(final Label label, final int lineNumber) {
			this.label = label;
			this.lineNumber = lineNumber;
		}
	}

	class LocalInfo {
		LocalVariable var;

		Type type;

		public LocalInfo(final LocalVariable var, final Type type) {
			this.var = var;
			this.type = type;
		}

		public boolean equals(final Object obj) {
			return (obj != null) && (obj instanceof LocalInfo)
					&& ((LocalInfo) obj).var.equals(var)
					&& ((LocalInfo) obj).type.equals(type);
		}

		public int hashCode() {
			return var.hashCode() ^ type.hashCode();
		}
	}

	/**
	 * Creates a new local variable.
	 */
	public LocalVariable newLocal(final Type type) {
		final int index = maxLocals;

		maxLocals += type.stackHeight();
		this.setDirty(true);

		final LocalVariable local = new LocalVariable(index);

		locals.put(new Integer(index), local);

		return (local);
	}

	/**
	 * Creates a new local variable of an undertermined type.
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public LocalVariable newLocal(final boolean isWide) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		final int index = maxLocals;

		maxLocals += (isWide ? 2 : 1);
		this.setDirty(true);

		final LocalVariable local = new LocalVariable(index);

		locals.put(new Integer(index), local);

		return (local);
	}

	/**
	 * Returns the <tt>LocalVariable</tt> with the given index. If there is no
	 * local variable at that index, a new one is created at that index. We
	 * assume that this variable is not wide.
	 */
	public LocalVariable localAt(int index) {
		LocalVariable local = (LocalVariable) locals.get(new Integer(index));

		if (local == null) {
			local = new LocalVariable(index);
			locals.put(new Integer(index), local);
			if (index >= maxLocals) {
				maxLocals = index++; // Dangerous?
			}
		}

		return (local);
	}

	/**
	 * Add an instruction.
	 * 
	 * @param opcodeClass
	 *            The instruction to add.
	 */
	public void addInstruction(final int opcodeClass) {
		addInstruction(new Instruction(opcodeClass));
	}

	/**
	 * Add an instruction.
	 * 
	 * @param opcodeClass
	 *            The instruction to add.
	 */
	public void addInstruction(final int opcodeClass, final Object operand) {
		addInstruction(new Instruction(opcodeClass, operand));
	}

	/**
	 * Add an instruction to the end of the code array.
	 * 
	 * @param inst
	 *            The instruction to add.
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void addInstruction(final Instruction inst) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		if (ClassEditor.DEBUG) {
			System.out.println("    " + inst + " to "
					+ System.identityHashCode(this) + ":"
					+ System.identityHashCode(this.code));
		}

		code.add(inst);
		this.setDirty(true);
	}

	/**
	 * Get the next available label. That is the Label after the final
	 * Instruction in the code array.
	 * 
	 * @return A new label.
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public Label newLabel() {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		this.setDirty(true);
		return new Label(maxLabel++);
	}

	public Label newLabelTrue() {
		return new Label(maxLabel++, true);
	}

	/**
	 * Add a label to the code array to the end of the code array.
	 * 
	 * @param label
	 *            The label to add.
	 * 
	 * @throws IllegalStateException This field has been marked for deletion
	 */
	public void addLabel(final Label label) {
		if (this.isDeleted) {
			final String s = "Cannot change a field once it has been marked "
					+ "for deletion";
			throw new IllegalStateException(s);
		}

		if (ClassEditor.DEBUG) {
			System.out.println("    " + label + " to "
					+ System.identityHashCode(this) + ":"
					+ System.identityHashCode(this.code));
		}

		if (label.index() >= maxLabel) {
			maxLabel = label.index() + 1;
		}

		code.add(label);
		this.setDirty(true);
	}

	class LocalVarEntry {
		LocalVariable var;

		Label start;

		Label end;

		public LocalVarEntry(final LocalVariable var, final Label start,
				final Label end) {
			this.var = var;
			this.start = start;
			this.end = end;
		}
	}

	/**
	 * Commits changes made to this MethodEditor back to the MethodInfo on which
	 * it is based. Note that committal will take place regardless of whether or
	 * not the method is dirty.
	 */
	public void commit() {
		if (ClassEditor.DEBUG) {
			System.out.println("Committing method " + this.name + " "
					+ this.type + ": " + this.code.size() + " insts "
					+ System.identityHashCode(this) + ":"
					+ System.identityHashCode(this.code));
		}

		final ConstantPool cp = editor.constants();

		if (this.isDeleted) {
			final int nameIndex = cp.getUTF8Index(this.name);
			final int typeIndex = cp.getTypeIndex(this.type);
			this.editor.classInfo().deleteMethod(nameIndex, typeIndex);

		} else {
			methodInfo.setNameIndex(cp.addConstant(Constant.UTF8, name));
			methodInfo.setTypeIndex(cp.addConstant(Constant.UTF8, type
					.descriptor()));

			if (isNative() || isAbstract()) {
				return;
			}

			final List vars = new ArrayList();
			final List copy = new LinkedList();

			Iterator iter = code.iterator();

			CODE: while (iter.hasNext()) {
				final Object ce = iter.next();

				if (ce instanceof Label) {
					copy.add(ce);
					continue CODE;
				}

				final Instruction inst = (Instruction) ce;

				LocalVariable var = null;

				if (inst.operand() instanceof LocalVariable) {
					var = (LocalVariable) inst.operand();
				} else if (inst.operand() instanceof IncOperand) {
					var = ((IncOperand) inst.operand()).var();
				}

				if ((var == null) || (var.name() == null)
						|| (var.type() == null)) {
					copy.add(ce);
					continue CODE;
				}

				for (int j = vars.size() - 1; j >= 0; j--) {
					final LocalVarEntry v = (LocalVarEntry) vars.get(j);

					// Same variable, extend the range of the variable and
					// go to the next instruction.
					if (v.var.equals(var)) {
						v.end = newLabel();

						// Add a label after the instruction.
						copy.add(ce);
						copy.add(v.end);
						continue CODE;
					}

					// Different variable, same index. We have to add an entry
					// for the variable since we know the live range for this
					// index starts here.
					if (v.var.index() == var.index()) {
						break;
					}
				}

				final Label start = newLabel();
				final Label end = newLabel();

				vars.add(new LocalVarEntry(var, start, end));

				// Add labels before and after the instruction.
				copy.add(start);
				copy.add(ce);
				copy.add(end);
			}

			final HashSet seen = new HashSet();
			final ArrayList dup = new ArrayList();

			iter = tryCatches.iterator();

			while (iter.hasNext()) {
				final TryCatch tc = (TryCatch) iter.next();

				if (!seen.contains(tc.handler())) {
					if (ClassEditor.DEBUG) {
						System.out.println("See " + tc.handler());
					}
					seen.add(tc.handler());
				} else {
					if (ClassEditor.DEBUG) {
						System.out.println("See " + tc.handler() + " again");
					}
					dup.add(tc);
				}
			}

			if (dup.size() != 0) {
				final ListIterator liter = copy.listIterator();

				while (liter.hasNext()) {
					final Object ce = liter.next();

					if (ce instanceof Label) {
						final Iterator d = dup.iterator();

						while (d.hasNext()) {
							final TryCatch tc = (TryCatch) d.next();

							if (tc.handler().equals(ce)) {
								// Split the exception handler.
								//
								// Handler:
								// nop <-- nop needed to prevent TowerJ
								// goto L2 from removing the goto
								// Handler2:
								// nop
								// goto L2
								// Code:
								// handler code

								Instruction jump;
								Instruction nop;

								final Label handler2 = newLabel();
								final Label code = newLabel();

								nop = new Instruction(Opcode.opcx_nop);
								liter.add(nop);

								jump = new Instruction(Opcode.opcx_goto, code);
								liter.add(jump);

								liter.add(handler2);

								nop = new Instruction(Opcode.opcx_nop);
								liter.add(nop);

								jump = new Instruction(Opcode.opcx_goto, code);
								liter.add(jump);

								liter.add(code);

								if (ClassEditor.DEBUG) {
									System.out.println("Insert " + jump);
									System.out.println("Insert " + handler2);
									System.out.println("Insert " + jump);
									System.out.println("Insert " + code);
								}

								tc.setHandler(handler2);

								d.remove();
							}
						}
					}
				}
			}

			final CodeArray array = new CodeArray(this, cp, copy);
			final byte[] arr = array.array();

			methodInfo.setCode(arr);

			methodInfo.setMaxLocals(array.maxLocals());
			methodInfo.setMaxStack(array.maxStack());

			if (MethodEditor.PRESERVE_DEBUG && (arr.length < 0x10000)) {
				final LocalDebugInfo[] locals = new LocalDebugInfo[vars.size()];

				for (int i = 0; i < vars.size(); i++) {
					final LocalVarEntry entry = (LocalVarEntry) vars.get(i);

					final int start = array.labelIndex(entry.start);
					final int end = array.labelIndex(entry.end);

					if (start < end) {
						locals[i] = new LocalDebugInfo(start, end - start, cp
								.addConstant(Constant.UTF8, entry.var.name()),
								cp.addConstant(Constant.UTF8, entry.var.type()
										.descriptor()), entry.var.index());
					}
				}

				methodInfo.setLocals(locals);

				final LineNumberDebugInfo[] lines = new LineNumberDebugInfo[lineNumbers
						.size()];
				int i = 0;

				iter = lineNumbers.iterator();

				while (iter.hasNext()) {
					final LineNumberEntry line = (LineNumberEntry) iter.next();
					lines[i++] = new LineNumberDebugInfo(array
							.labelIndex(line.label), line.lineNumber);
				}

				methodInfo.setLineNumbers(lines);
			} else {
				methodInfo.setLineNumbers(null);
				methodInfo.setLocals(null);
			}

			final List c = new LinkedList();

			iter = tryCatches.iterator();

			while (iter.hasNext()) {
				final TryCatch tc = (TryCatch) iter.next();

				final int start = array.labelIndex(tc.start());
				final int end = array.labelIndex(tc.end());

				if (start < end) {
					c.add(new Catch(start, end, array.labelIndex(tc.handler()),
							cp.addConstant(Constant.CLASS, tc.type())));
				}
			}

			final Object[] a = c.toArray();
			final Catch[] catches = new Catch[a.length];
			System.arraycopy(a, 0, catches, 0, a.length);

			methodInfo.setExceptionHandlers(catches);

		}

		if (ClassEditor.DEBUG) {
			System.out.println("MethodInfo after commit: " + methodInfo);
		}

		// Method is no longer dirty
		this.isDirty = false;
	}

	/**
	 * Print the method.
	 * 
	 * @param out
	 *            Stream to which to print.
	 */
	public void print(final PrintStream out) {
		out.println(name + "." + type + (isDirty ? " (dirty) " : "") + ":");

		Iterator iter;

		iter = code.iterator();

		while (iter.hasNext()) {
			out.println("    " + iter.next());
		}

		iter = tryCatches.iterator();

		while (iter.hasNext()) {
			out.println("    " + iter.next());
		}
	}

	/**
	 * Two <tt>MethodEditor</tt>s are equal if they edit the same method in
	 * the same class.
	 */
	public boolean equals(final Object o) {
		if (o instanceof MethodEditor) {
			final MethodEditor other = (MethodEditor) o;

			if (!other.declaringClass().equals(this.declaringClass())) {
				return (false);
			}
			if (!other.name().equals(this.name())) {
				return (false);
			}
			if (!other.type().equals(this.type())) {
				return (false);
			}

			return (true);
		}

		return (false);
	}

	/**
	 * A <tt>MethodEditor</tt>'s hash code is based on the hash codes for its
	 * class, name, and type.
	 */
	public int hashCode() {
		return (this.declaringClass().hashCode() + this.name().hashCode() + this
				.type().hashCode());
	}

	public String toString() {
		return (editor.type() + "." + name + type);
	}

	public UseMap uMap() {
		return uMap;
	}

	public void rememberDef(final LocalExpr e) {
		if (MethodEditor.OPT_STACK_2) {
			uMap.add(e, (Instruction) code.get(code.size() - 1));
		}
	}

}
