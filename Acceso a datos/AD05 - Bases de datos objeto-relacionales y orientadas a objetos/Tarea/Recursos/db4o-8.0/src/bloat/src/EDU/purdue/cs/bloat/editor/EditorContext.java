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

import EDU.purdue.cs.bloat.reflect.*;

/**
 * An <tt>EditorContext</tt> supplies a means of loading and editing classes.
 * Note that a number of these methods are identical to methods in
 * <tt>Editor</tt>. It is expected that an <tt>EditorContext</tt> will have
 * a different caching (of <tt>ClassEditor</tt>s, etc.) policy than
 * <tt>Editor</tt> does. Hence, the methods in <tt>EditorContext</tt> should
 * be used to edit classes, etc.
 */
public interface EditorContext {

	/**
	 * Loads a class into BLOAT
	 */
	public ClassInfo loadClass(String className) throws ClassNotFoundException;

	/**
	 * Creates a new <code>ClassInfo</code>
	 * 
	 * @param modifiers
	 *            The modifiers describing the newly-created class
	 * @param classIndex
	 *            The index of the name of the newly-created class in its
	 *            constant pool
	 * @param superClassIndex
	 *            The index of the name of the newly-created class's superclass
	 *            in its constant pool
	 * @param interfaceIndexes
	 *            The indexes of the names of the interfaces that the
	 *            newly-created class implements
	 * @param constants
	 *            The constant pool for the newly created class (a list of
	 *            {@link Constant}s).
	 */
	public ClassInfo newClassInfo(int modifiers, int classIndex,
			int superClassIndex, int[] interfaceIndexes,
			java.util.List constants);

	/**
	 * Returns the <tt>ClassHierarchy</tt> of all classes and interfaces known
	 * to BLOAT.
	 */
	public ClassHierarchy getHierarchy();

	/**
	 * Returns a <code>ClassEditor</code> for editing a new class with the
	 * given name. It will override any class with the given name that is
	 * already being edited.
	 */
	public ClassEditor newClass(int modifiers, String className,
			Type superType, Type[] interfaces);

	/**
	 * Returns a <tt>ClassEditor</tt> used to edit a class of a given name.
	 */
	public ClassEditor editClass(String className)
			throws ClassNotFoundException, ClassFormatException;

	/**
	 * Returns a <tt>ClassEditor</tt> used to edit a class described by a
	 * given <tt>Type</tt>.
	 */
	public ClassEditor editClass(Type classType) throws ClassNotFoundException,
			ClassFormatException;

	/**
	 * Returns a <tt>ClassEditor</tt> used to edit a class described by a
	 * given <tt>ClassInfo</tt>.
	 */
	public ClassEditor editClass(ClassInfo info);

	/**
	 * Returns a <tt>FieldEditor</tt> for editing a <tt>FieldInfo</tt>.
	 */
	public FieldEditor editField(FieldInfo info);

	/**
	 * Returns a <tt>FieldEditor</tt> for editing a field.
	 */
	public FieldEditor editField(MemberRef field) throws NoSuchFieldException;

	/**
	 * Returns a <tt>MethodEditor</tt> for editing a method.
	 */
	public MethodEditor editMethod(MethodInfo info);

	/**
	 * Returns a <tt>MethodEditor</tt> for editing a method.
	 */
	public MethodEditor editMethod(MemberRef method)
			throws NoSuchMethodException;

	/**
	 * Signals that we are done editing a method. The object used to model it
	 * may be reclaimed.
	 */
	public void release(MethodInfo info);

	/**
	 * Signals that we are done editing a field. The object used to model it may
	 * be reclaimed.
	 */
	public void release(FieldInfo info);

	/**
	 * Signals that we are done editing a class. The object used to model it may
	 * be reclaimed.
	 */
	public void release(ClassInfo info);

	/**
	 * Commits the changes made to a class.
	 */
	public void commit(ClassInfo info);

	/**
	 * Commits the changes made to a method.
	 */
	public void commit(MethodInfo info);

	/**
	 * Commits the changes made to a field.
	 */
	public void commit(FieldInfo info);

	/**
	 * Commits all changes made to classes, methods, and fields.
	 */
	public void commit();
}
