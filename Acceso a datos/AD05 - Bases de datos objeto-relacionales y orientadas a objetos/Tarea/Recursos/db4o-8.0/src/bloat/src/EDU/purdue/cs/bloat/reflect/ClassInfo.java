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

import java.util.*;

/**
 * ClassInfo allows a class to be accessed and modified at a very low level.
 * ClassInfo is implemented by <tt>file.ClassFile</tt>
 * 
 * @see EDU.purdue.cs.bloat.file.ClassFile
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public interface ClassInfo {
	/**
	 * Get the class info loader for the class.
	 * 
	 * @return The class info loader.
	 */
	public ClassInfoLoader loader();

	/**
	 * Get the name of the class.
	 * 
	 * @return The name of the class.
	 */
	public String name();

	/**
	 * Get the index into the constant pool of class.
	 * 
	 * @return The index of the class.
	 */
	public int classIndex();

	/**
	 * Get the index into the constant pool of class's superclass.
	 * 
	 * @return The index of the superclass.
	 */
	public int superclassIndex();

	/**
	 * Get the indices into the constant pool of class's interfaces.
	 * 
	 * @return The indices of the interfaces.
	 */
	public int[] interfaceIndices();

	/**
	 * Set the index into the constant pool of class.
	 * 
	 * @param index
	 *            The index of the class.
	 */
	public void setClassIndex(int index);

	/**
	 * Set the index into the constant pool of class's superclass.
	 * 
	 * @param index
	 *            The index of the superclass.
	 */
	public void setSuperclassIndex(int index);

	/**
	 * Set the indices into the constant pool of class's interfaces.
	 * 
	 * @param indices
	 *            The indices of the interfaces.
	 */
	public void setInterfaceIndices(int[] indices);

	/**
	 * Set the modifiers of the class. The values correspond to the constants in
	 * the Modifiers class.
	 * 
	 * @param modifiers
	 *            A bit vector of modifier flags for the class.
	 * @see Modifiers
	 */
	public void setModifiers(int modifiers);

	/**
	 * Get the modifiers of the class. The values correspond to the constants in
	 * the Modifiers class.
	 * 
	 * @return A bit vector of modifier flags for the class.
	 * @see Modifiers
	 */
	public int modifiers();

	/**
	 * Get an array of FieldInfo structures for each field in the class.
	 * 
	 * @return An array of FieldInfo structures.
	 * @see FieldInfo
	 */
	public FieldInfo[] fields();

	/**
	 * Returns an array of MethodInfo structures for each method in the class.
	 */
	public MethodInfo[] methods();

	/**
	 * Sets the methods in this class.
	 */
	public void setMethods(MethodInfo[] methods);

	/**
	 * Returns an array of the constants in the constant pool.
	 */
	public Constant[] constants();

	/**
	 * Set all the constants in the constant pool.
	 * 
	 * @param constants
	 *            The array of Constants.
	 * @see Constant
	 */
	public void setConstants(Constant[] constants);

	/**
	 * Commit any changes to the file or to the virtual machine.
	 */
	public void commit();

	/**
	 * Commits only certain methods and fields.
	 * 
	 * @param methods
	 *            Methods (<tt>MethodInfo</tt>s) to commit. If <tt>null</tt>,
	 *            all methods are committed.
	 * @param fields
	 *            Fields (<tt>FieldInfo</tt>s) to commit. If <tt>null</tt>,
	 *            all fields are committed.
	 */
	public void commitOnly(Set methods, Set fields);

	/**
	 * Factory method that creates a new field in the class being modeled
	 */
	public FieldInfo addNewField(int modifiers, int typeIndex, int nameIndex);

	/**
	 * Factory method that creates a new field with a constant value in the
	 * class being modeled
	 * 
	 * @param cvNameIndex
	 *            The index in the class's constant pool for the UTF8 constant
	 *            "ConstantValue"
	 * @param constantValueIndex
	 *            The index in the class's constant pool for the constant value
	 */
	public FieldInfo addNewField(int modifiers, int typeIndex, int nameIndex,
			int cvNameIndex, int constantValueIndex);

	/**
	 * Deletes a field from this class
	 * 
	 * @param nameIndex
	 *            Index in the constant pool of the name of the field to be
	 *            deleted
	 * 
	 * @throws IllegalArgumentException The class modeled by this
	 *        <code>ClassInfo</code> does not contain a field whose name is at
	 *        the given index
	 */
	public void deleteField(int nameIndex);

	/**
	 * Deletes a method from this class
	 * 
	 * @param nameIndex
	 *            Index in the constant pool of the name of the method to be
	 *            deleted
	 * @param typeIndex
	 *            Index in the constant pool of the type of the method to be
	 *            deleted
	 * 
	 * @throws IllegalArgumentException The class modeled by this
	 *        <code>ClassInfo</code> does not contain a method whose name and
	 *        type are not at the given indices
	 */
	public void deleteMethod(int nameIndex, int typeIndex);

	/**
	 * Adds a new method to this class.
	 * 
	 * @param modifiers
	 *            The {@link EDU.purdue.cs.bloat.reflect.Modifiers modifiers}
	 *            for the new method
	 * @param typeIndex
	 *            The index of the type (conglomeration of the parameter types
	 *            and the return type) for this method in the class's constant
	 *            pool
	 * @param nameIndex
	 *            The index of the name of the method in the class's constant
	 *            pool
	 * @param exceptionIndex
	 *            The index of the UTF8 string "Exceptions" in the class's
	 *            constant pool
	 * @param exceptionTypeIndices
	 *            The indices in the class's constant pool of the type of the
	 *            exceptions thrown by this method
	 * @param codeIndex
	 *            The index of the UTF8 string "Code" in the class's constant
	 *            pool
	 */
	public MethodInfo addNewMethod(int modifiers, int typeIndex, int nameIndex,
			int exceptionIndex, int[] exceptionTypeIndices, int codeIndex);

	public void print(java.io.PrintStream out);

	public void print(java.io.PrintWriter out);

	public String toString();
}
