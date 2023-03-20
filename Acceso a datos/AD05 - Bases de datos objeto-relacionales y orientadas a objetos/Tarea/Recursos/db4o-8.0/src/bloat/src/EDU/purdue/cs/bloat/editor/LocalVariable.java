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

/**
 * LocalVariable represents a local variable index operand to various
 * instructions.
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class LocalVariable {
	private String name;

	private Type type;

	private int index;

	/**
	 * Constructor.
	 * 
	 * @param index
	 *            The index of the local variable in the method's local variable
	 *            array.
	 */
	public LocalVariable(final int index) {
		this.name = null;
		this.type = null;
		this.index = index;
	}

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            The name of the local variable.
	 * @param type
	 *            The descriptor (or index into the constant pool) representing
	 *            the variable type.
	 * @param index
	 *            The index of the local variable in the method's local variable
	 *            array.
	 */
	public LocalVariable(final String name, final Type type, final int index) {
		this.name = name;
		this.type = type;
		this.index = index;
	}

	/**
	 * Hash the local variable.
	 * 
	 * A stricter hashing than using the index will break Hashtable lookups
	 * since a variable could have a name assigned to it after its first use.
	 * 
	 * @return The hash code.
	 */
	public int hashCode() {
		return index;
	}

	/**
	 * Check if an object is equal to this variable.
	 * 
	 * A stricter comparison than comparing indices will break Hashtable lookups
	 * since a variable could have a name assigned to it after its first use.
	 * 
	 * @param obj
	 *            The object to compare against.
	 * @return true if equal, false if not.
	 */
	public boolean equals(final Object obj) {
		return (obj != null) && (obj instanceof LocalVariable)
				&& (((LocalVariable) obj).index == index);
	}

	/**
	 * Get the name of the local variable.
	 * 
	 * @return The name of the local variable.
	 */
	public String name() {
		return name;
	}

	/**
	 * Get the type of the local variable.
	 * 
	 * @return The type of the local variable.
	 */
	public Type type() {
		return type;
	}

	/**
	 * Get the index into the local variable array.
	 * 
	 * @return The index into the local variable array.
	 */
	public int index() {
		return index;
	}

	/**
	 * Convert the variable to a string.
	 * 
	 * @return A string representation of the variable.
	 */
	public String toString() {
		if (name == null) {
			return "Local$" + index;
		}

		return name + "$" + index;
	}
}
