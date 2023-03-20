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
package EDU.purdue.cs.bloat.file;

import java.io.*;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Method represents a method in a Java classfile. A method's name and value
 * (the types of its parameters and its return type) are modeled as indices into
 * it class's constant pool. A method has modifiers that determine whether it is
 * public, private, static, final, etc. Methods have a number of attributes such
 * as their Code and any Exceptions they may throw.
 * 
 * @see Code
 * @see Exceptions
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class Method implements MethodInfo {
	private ClassInfo classInfo;

	private int modifiers;

	private int name;

	private int type;

	private Attribute[] attrs;

	private Code code;

	private Exceptions exceptions;

	public static boolean DEBUG = Boolean.getBoolean("Method.DEBUG");

	/**
	 * Constructor for creating a <code>Method</code> from scratch
	 * 
	 * @param attrs
	 *            Must include <code>code</code> and <code>exceptions</code>
	 *            which are themselves attributes of this method
	 */
	Method(final ClassInfo classInfo, final int modifiers, final int name,
			final int type, final Attribute[] attrs, final Code code,
			final Exceptions exceptions) {
		this.classInfo = classInfo;
		this.modifiers = modifiers;
		this.name = name;
		this.type = type;

		Assert.isNotNull(attrs, "Every method must have at least a Code "
				+ "attribute");
		this.attrs = attrs;
		this.code = code;
		this.exceptions = exceptions;
	}

	/**
	 * Constructor. Read a method from a class file.
	 * 
	 * @param in
	 *            The data stream of the class file.
	 * @param classInfo
	 *            The class file containing the method.
	 * @exception IOException
	 *                If an error occurs while reading.
	 */
	public Method(final DataInputStream in, final ClassInfo classInfo)
			throws IOException {
		this.classInfo = classInfo;

		modifiers = in.readUnsignedShort();

		name = in.readUnsignedShort();
		type = in.readUnsignedShort();

		final int numAttributes = in.readUnsignedShort();

		attrs = new Attribute[numAttributes];

		for (int i = 0; i < numAttributes; i++) {
			final int nameIndex = in.readUnsignedShort();
			final int length = in.readInt();

			final Constant name = classInfo.constants()[nameIndex];

			if (name != null) {
				if ("Code".equals(name.value())) {
					code = new Code(classInfo, in, nameIndex, length);
					attrs[i] = code;
				} else if ("Exceptions".equals(name.value())) {
					exceptions = new Exceptions(classInfo, in, nameIndex,
							length);
					attrs[i] = exceptions;
				}
			}

			if (attrs[i] == null) {
				attrs[i] = new GenericAttribute(in, nameIndex, length);
			}
		}
	}

	/**
	 * Get the class which declared the method.
	 * 
	 * @return The ClassInfo of the class which declared the method.
	 */
	public ClassInfo declaringClass() {
		return classInfo;
	}

	/**
	 * Set the index into the constant pool of the name of the method.
	 * 
	 * @param name
	 *            The index into the constant pool of the name of the method.
	 */
	public void setNameIndex(final int name) {
		this.name = name;
	}

	/**
	 * Set the index into the constant pool of the type of the method.
	 * 
	 * @param type
	 *            The index into the constant pool of the type of the method.
	 */
	public void setTypeIndex(final int type) {
		this.type = type;
	}

	/**
	 * Get the index into the constant pool of the name of the method.
	 * 
	 * @return The index into the constant pool of the name of the method.
	 */
	public int nameIndex() {
		return name;
	}

	/**
	 * Get the index into the constant pool of the type of the method.
	 * 
	 * @return The index into the constant pool of the type of the method.
	 */
	public int typeIndex() {
		return type;
	}

	/**
	 * Set the modifiers of the method. The values correspond to the constants
	 * in the Modifiers class.
	 * 
	 * @param modifiers
	 *            A bit vector of modifier flags for the method.
	 * @see Modifiers
	 */
	public void setModifiers(final int modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * Get the modifiers of the method. The values correspond to the constants
	 * in the Modifiers class.
	 * 
	 * @return A bit vector of modifier flags for the method.
	 * @see Modifiers
	 */
	public int modifiers() {
		return modifiers;
	}

	/**
	 * Get the maximum height of the operand stack.
	 * 
	 * @return The maximum height of the operand stack.
	 */
	public int maxStack() {
		if (code != null) {
			return code.maxStack();
		}
		return 0;
	}

	/**
	 * Set the maximum height of the operand stack.
	 * 
	 * @param maxStack
	 *            The maximum height of the operand stack.
	 */
	public void setMaxStack(final int maxStack) {
		if (code != null) {
			code.setMaxStack(maxStack);
		}
	}

	/**
	 * Get the maximum number of locals used in the method.
	 * 
	 * @return The maximum number of locals used in the method.
	 */
	public int maxLocals() {
		if (code != null) {
			return code.maxLocals();
		}
		return 0;
	}

	/**
	 * Set the maximum number of locals used in the method.
	 * 
	 * @param maxLocals
	 *            The maximum number of locals used in the method.
	 */
	public void setMaxLocals(final int maxLocals) {
		if (code != null) {
			code.setMaxLocals(maxLocals);
		}
	}

	/**
	 * Get the byte code array of the method.
	 * 
	 * @return The byte code array of the method.
	 */
	public byte[] code() {
		if (code != null) {
			return code.code();
		}
		return new byte[0];
	}

	/**
	 * Returns the length of the Code array.
	 */
	public int codeLength() {
		if (code != null) {
			return (code.codeLength());
		} else {
			return (0);
		}
	}

	/**
	 * Set the byte code array of the method.
	 * 
	 * @param bytes
	 *            The byte code array of the method.
	 */
	public void setCode(final byte[] bytes) {
		if (code != null) {
			code.setCode(bytes);
			if (Method.DEBUG) {
				System.out.println("Set code with " + bytes.length + " bytes");
				// Thread.dumpStack();
			}
		}
	}

	/**
	 * Get the indices into the constant pool of the types of the exceptions
	 * thrown by the method.
	 * 
	 * @return The indices into the constant pool of the types of the exceptions
	 *         thrown by the method.
	 */
	public int[] exceptionTypes() {
		if (exceptions != null) {
			return exceptions.exceptionTypes();
		}
		return new int[0];
	}

	/**
	 * Set the line numbers of the instructions in the method.
	 * 
	 * @param lineNumbers
	 *            The line numbers of the instructions in the method.
	 */
	public void setLineNumbers(final LineNumberDebugInfo[] lineNumbers) {
		if (code != null) {
			code.setLineNumbers(lineNumbers);
		}
	}

	/**
	 * Set the local variable debug info for the method.
	 * 
	 * @param locals
	 *            The local variable debug info for the method.
	 */
	public void setLocals(final LocalDebugInfo[] locals) {
		if (code != null) {
			code.setLocals(locals);
		}
	}

	/**
	 * Get the line numbers of the instructions in the method.
	 * 
	 * @return The line numbers of the instructions in the method.
	 */
	public LineNumberDebugInfo[] lineNumbers() {
		if (code != null) {
			return code.lineNumbers();
		}
		return new LineNumberDebugInfo[0];
	}

	/**
	 * Get the local variable debug info for the method.
	 * 
	 * @return The local variable debug info for the method.
	 */
	public LocalDebugInfo[] locals() {
		if (code != null) {
			return code.locals();
		}
		return new LocalDebugInfo[0];
	}

	/**
	 * Get the exception handlers in the method.
	 * 
	 * @return The exception handlers in the method.
	 */
	public Catch[] exceptionHandlers() {
		if (code != null) {
			return code.exceptionHandlers();
		}
		return new Catch[0];
	}

	/**
	 * Set the exception handlers in the method.
	 * 
	 * @param handlers
	 *            The exception handlers in the method.
	 */
	public void setExceptionHandlers(final Catch[] handlers) {
		if (code != null) {
			code.setExceptionHandlers(handlers);
		}
	}

	/**
	 * Write the method to a class file.
	 * 
	 * @param out
	 *            The data stream of the class file.
	 * @exception IOException
	 *                If an error occurs while writing.
	 */
	public void write(final DataOutputStream out) throws IOException {
		if (Method.DEBUG) {
			System.out.println("Writing method " + this);
			System.out.println("  Masked Modifiers: " + (modifiers & 0xf000));
		}

		out.writeShort(modifiers);

		out.writeShort(name);
		out.writeShort(type);

		out.writeShort(attrs.length);

		for (int i = 0; i < attrs.length; i++) {
			if (Method.DEBUG) {
				System.out.println("  " + attrs[i]);
			}
			out.writeShort(attrs[i].nameIndex());
			out.writeInt(attrs[i].length());
			attrs[i].writeData(out);
		}
	}

	/**
	 * Returns a string representation of the method.
	 */
	public String toString() {
		String x = "";

		x += " (modifiers";

		if ((modifiers & Modifiers.PUBLIC) != 0) {
			x += " PUBLIC";
		}
		if ((modifiers & Modifiers.PRIVATE) != 0) {
			x += " PRIVATE";
		}
		if ((modifiers & Modifiers.PROTECTED) != 0) {
			x += " PROTECTED";
		}
		if ((modifiers & Modifiers.STATIC) != 0) {
			x += " STATIC";
		}
		if ((modifiers & Modifiers.FINAL) != 0) {
			x += " FINAL";
		}
		if ((modifiers & Modifiers.SYNCHRONIZED) != 0) {
			x += " SYNCHRONIZED";
		}
		if ((modifiers & Modifiers.VOLATILE) != 0) {
			x += " VOLATILE";
		}
		if ((modifiers & Modifiers.TRANSIENT) != 0) {
			x += " TRANSIENT";
		}
		if ((modifiers & Modifiers.NATIVE) != 0) {
			x += " NATIVE";
		}
		if ((modifiers & Modifiers.INTERFACE) != 0) {
			x += " INTERFACE";
		}
		if ((modifiers & Modifiers.ABSTRACT) != 0) {
			x += " ABSTRACT";
		}
		x += ")";

		return "(method " + name + " " + type + x
				+ (code != null ? "\n      " + code : "")
				+ (exceptions != null ? "\n      " + exceptions : "") + ")";
	}

	/**
	 * Constructor used for cloning a <code>Method</code>
	 */
	private Method(final ClassInfo classInfo, final int modifiers,
			final int name, final int type, final Attribute[] attrs) {
		this.classInfo = classInfo;
		this.modifiers = modifiers;
		this.name = name;
		this.type = type;

		if (attrs != null) {
			this.attrs = new Attribute[attrs.length];
			for (int i = 0; i < attrs.length; i++) {
				final Attribute attr = (Attribute) attrs[i].clone();
				if (attr instanceof Code) {
					this.code = (Code) attr;

				} else if (attr instanceof Exceptions) {
					this.exceptions = (Exceptions) attr;
				}
				this.attrs[i] = attr;
			}
		}

		Assert.isTrue(code != null, "No Code in attributes");
		Assert.isTrue(exceptions != null, "No Exceptions in attributes");
	}

	/**
	 * Creates a clone of this <tt>MethodInfo</tt> except that its declaring
	 * class does not know about it.
	 */
	public Object clone() {
		return (new Method(this.classInfo, this.modifiers, this.name,
				this.type, this.attrs));
	}
}
