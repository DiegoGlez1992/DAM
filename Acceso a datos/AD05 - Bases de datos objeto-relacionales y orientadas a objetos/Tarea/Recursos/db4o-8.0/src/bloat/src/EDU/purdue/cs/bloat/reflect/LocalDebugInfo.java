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
package EDU.purdue.cs.bloat.reflect;

/**
 * LocalDebugInfo is used to map a local variable index in a range of
 * instructions to a local in the original Java source file. In addition,
 * LocalDebugInfo keeps track of a local variable's name and type (as indices
 * into the constant pool) and which number local variable is being represented.
 * Instances of <tt>file.LocalVariableTable</tt> consist of an array of
 * LocalDebugInfo.
 * 
 * @see EDU.purdue.cs.bloat.file.LocalVariableTable
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class LocalDebugInfo {
	private int startPC;

	private int length;

	private int nameIndex;

	private int typeIndex;

	private int index;

	/**
	 * Constructor.
	 * 
	 * @param startPC
	 *            The start PC of the live range of the variable.
	 * @param length
	 *            The length of the live range of the variable.
	 * @param nameIndex
	 *            The index into the constant pool of the name of the variable.
	 * @param typeIndex
	 *            The index into the constant pool of the type descriptor of the
	 *            variable.
	 * @param index
	 *            The index of this variable into the local variable array for
	 *            the method.
	 */
	public LocalDebugInfo(final int startPC, final int length,
			final int nameIndex, final int typeIndex, final int index) {
		this.startPC = startPC;
		this.length = length;
		this.nameIndex = nameIndex;
		this.typeIndex = typeIndex;
		this.index = index;
	}

	/**
	 * Get the start PC of the live range of the variable.
	 * 
	 * @return The start PC of the live range of the variable.
	 */
	public int startPC() {
		return startPC;
	}

	/**
	 * Get the length of the live range of the variable.
	 * 
	 * @return The length of the live range of the variable.
	 */
	public int length() {
		return length;
	}

	/**
	 * Get the index into the constant pool of the name of the variable.
	 * 
	 * @return The index into the constant pool of the name of the variable.
	 */
	public int nameIndex() {
		return nameIndex;
	}

	/**
	 * Get the index into the constant pool of the type descriptor of the
	 * variable.
	 * 
	 * @return The index into the constant pool of the type descriptor of the
	 *         variable.
	 */
	public int typeIndex() {
		return typeIndex;
	}

	/**
	 * Get the index of this variable into the local variable array for the
	 * method.
	 * 
	 * @return The index of this variable into the local variable array for the
	 *         method.
	 */
	public int index() {
		return index;
	}

	public Object clone() {
		return (new LocalDebugInfo(this.startPC, this.length, this.nameIndex,
				this.typeIndex, this.index));
	}

	/**
	 * Returns a string representation of the attribute.
	 */
	public String toString() {
		return "(local #" + index + " pc=" + startPC + ".."
				+ (startPC + length) + " name=" + nameIndex + " desc="
				+ typeIndex + ")";
	}
}
