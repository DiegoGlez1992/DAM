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
package EDU.purdue.cs.bloat.context;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

/**
 * Maintains all BLOAT data structures as if they were meant to reside in a
 * persistent store. As a result, it keeps every piece of BLOAT data around
 * because it might be needed in the future. No fancing cache maintainence is
 * performed. Because we are going for maximum information we take the closure
 * of classes when working with the class hierarchy.
 */
public class PersistentBloatContext extends BloatContext {

	protected final ClassHierarchy hierarchy;

	protected Map classInfos; // Maps Strings to ClassInfos

	protected Map methodInfos; // Maps MemberRefs to MethodInfos

	protected Map fieldInfos; // Maps MemberRefs to FieldInfos

	protected Map classEditors; // Maps ClassInfos to ClassEditors

	protected Map methodEditors; // Maps MethodInfos to MethodEditors

	protected Map fieldEditors; // Maps MethodInfos to FieldEditors

	public static boolean DB_COMMIT = false;

	protected static void comm(final String s) {
		if (PersistentBloatContext.DB_COMMIT || BloatContext.DEBUG) {
			System.out.println(s);
		}
	}

	/**
	 * Constructor. Each <tt>BloatContext</tt> stems from a
	 * <tt>ClassInfoLoader</tt>. Using the loader it can create an
	 * <tt>Editor</tt> and such. Initially, no classes are loaded.
	 */
	public PersistentBloatContext(final ClassInfoLoader loader) {
		this(loader, true);
	}

	/**
	 * Constructor. It is the responsibility of the subclasses to add classes to
	 * the hierarchy by calling <tt>addClasses</tt>.
	 * 
	 * @param loader
	 *            Used to load classes
	 * @param closure
	 *            Do we look for the maximum number of classes?
	 */
	protected PersistentBloatContext(final ClassInfoLoader loader,
			final boolean closure) {
		super(loader);
		BloatContext.db("Creating a new BloatContext");

		// Create a bunch of the mappings we maintain. Make sure to do
		// this before anything else!
		classInfos = new HashMap();
		methodInfos = new HashMap();
		fieldInfos = new HashMap();

		classEditors = new HashMap();
		methodEditors = new HashMap();
		fieldEditors = new HashMap();

		// Have to create an empty class hierarchy then add the classes.
		// There is a strange circular dependence between the hierarchy
		// and the context.
		this.hierarchy = new ClassHierarchy(this, new ArrayList(), closure);

	}

	/**
	 * Adds a bunch of (names of) classes to the hierarchy.
	 */
	protected void addClasses(final Collection classes) {
		final Iterator iter = classes.iterator();
		while (iter.hasNext()) {
			final String className = (String) iter.next();
			this.hierarchy.addClassNamed(className);
		}
	}

	public ClassInfo loadClass(String className) throws ClassNotFoundException {
		// Lots of interesting stuff to do here. For the moment, just
		// load the class from the ClassInfoLoader and add it to the
		// hierarchy.

		className = className.replace('.', '/').intern();

		// Check the cache of ClassInfos
		ClassInfo info = (ClassInfo) classInfos.get(className);

		if (info == null) {
			BloatContext.db("BloatContext: Loading class " + className);
			info = loader.loadClass(className);
			hierarchy.addClassNamed(className);
			BloatContext.db("loadClass: " + className + " -> " + info);
			classInfos.put(className, info);
		}

		return (info);
	}

	public ClassInfo newClassInfo(final int modifiers, final int classIndex,
			final int superClassIndex, final int[] interfaceIndexes,
			final List constants) {

		return this.loader.newClass(modifiers, classIndex, superClassIndex,
				interfaceIndexes, constants);
	}

	public ClassHierarchy getHierarchy() {
		return (this.hierarchy);
	}

	public ClassEditor newClass(final int modifiers, String className,
			final Type superType, final Type[] interfaces) {

		final ClassEditor ce = new ClassEditor(this, modifiers, className,
				superType, interfaces);
		final ClassInfo info = ce.classInfo();

		className = ce.name().intern();

		BloatContext.db("editClass(ClassInfo): " + className + " -> " + info);

		classInfos.put(className, info);
		classEditors.put(info, ce);

		return ce;
	}

	public ClassEditor editClass(String className)
			throws ClassNotFoundException, ClassFormatException {
		// Only make the name -> classInfo mapping if we edit the class,
		// this way the mapping will be deleted when the ClassEditor is
		// released.

		className = className.intern();

		ClassInfo info = (ClassInfo) classInfos.get(className);

		if (info == null) {
			info = loadClass(className);
			// db("editClass(String): " + className + " -> " + info);
			// classInfos.put(className, info);
		}

		return (editClass(info));
	}

	public ClassEditor editClass(final Type classType)
			throws ClassNotFoundException, ClassFormatException {
		return (editClass(classType.className()));
	}

	public ClassEditor editClass(final ClassInfo info) {
		// Check the cache
		ClassEditor ce = (ClassEditor) classEditors.get(info);

		if (ce == null) {
			ce = new ClassEditor(this, info);
			classEditors.put(info, ce);

			if (!classInfos.containsValue(info)) {
				final String className = ce.name().intern();
				BloatContext.db("editClass(ClassInfo): " + className + " -> "
						+ info);
				classInfos.put(className, info);
			}
		}

		return (ce);
	}

	public MethodEditor editMethod(final MemberRef method)
			throws NoSuchMethodException {

		// Check the MethodInfo cache
		final MethodInfo info = (MethodInfo) methodInfos.get(method);

		if (info == null) {
			// Groan, we have to do this the HARD way.
			BloatContext.db("Creating a new MethodEditor for " + method);
			final NameAndType nat = method.nameAndType();
			final String name = nat.name();
			final Type type = nat.type();

			try {
				final ClassEditor ce = editClass(method.declaringClass());
				final MethodInfo[] methods = ce.methods();

				for (int i = 0; i < methods.length; i++) {
					final MethodEditor me = editMethod(methods[i]);

					if (me.name().equals(name) && me.type().equals(type)) {
						// The call to editMethod should have already handled
						// the
						// methodEditors mapping, but we still need to do
						// methodInfos.
						methodInfos.put(method, methods[i]);
						release(ce.classInfo());
						return (me);
					}

					release(methods[i]);
				}

			} catch (final ClassNotFoundException ex1) {
			} catch (final ClassFormatException ex2) {
			}

			throw new NoSuchMethodException(method.toString());
		}

		return (editMethod(info));
	}

	public MethodEditor editMethod(final MethodInfo info) {
		// Check methodEditors cache
		MethodEditor me = (MethodEditor) methodEditors.get(info);

		if (me == null) {
			me = new MethodEditor(editClass(info.declaringClass()), info);
			methodEditors.put(info, me);
			BloatContext
					.db("Creating a new MethodEditor for " + me.memberRef());
		}

		return (me);
	}

	public FieldEditor editField(final MemberRef field)
			throws NoSuchFieldException {

		// Just like we had to do with methods
		final FieldInfo info = (FieldInfo) fieldInfos.get(field);

		if (info == null) {
			final NameAndType nat = field.nameAndType();
			final String name = nat.name();
			final Type type = nat.type();

			try {
				final ClassEditor ce = editClass(field.declaringClass());
				final FieldInfo[] fields = ce.fields();

				for (int i = 0; i < fields.length; i++) {
					final FieldEditor fe = editField(fields[i]);

					if (fe.name().equals(name) && fe.type().equals(type)) {
						fieldInfos.put(field, fields[i]);
						release(ce.classInfo());
						return (fe);
					}

					release(fields[i]);
				}
			} catch (final ClassNotFoundException ex1) {
			} catch (final ClassFormatException ex2) {
			}

			throw new NoSuchFieldException(field.toString());
		}

		return (editField(info));
	}

	public FieldEditor editField(final FieldInfo info) {
		// Check the cache
		FieldEditor fe = (FieldEditor) fieldEditors.get(info);

		if (fe == null) {
			fe = new FieldEditor(editClass(info.declaringClass()), info);
			fieldEditors.put(info, fe);
			BloatContext.db("Creating a new FieldEditor for "
					+ fe.nameAndType());
		}

		return (fe);
	}

	public void release(final ClassInfo info) {
		// Since we keep around all data, do nothing
	}

	public void release(final ClassEditor ce) {
		// Since we keep around all data, do nothing
	}

	public void release(final MethodInfo info) {
		// Since we keep around all data, do nothing
	}

	public void release(final FieldInfo info) {
		// Since we keep around all data, do nothing
	}

	/**
	 * Classes that are ignored are not committed.
	 * 
	 * @see #ignoreClass(Type)
	 */
	public void commit(final ClassInfo info) {
		final Type type = Type.getType("L" + info.name() + ";");
		if (ignoreClass(type)) {
			return;
		}

		final ClassEditor ce = editClass(info);

		// Commit all of the class's methods and fields
		final MethodInfo[] methods = ce.methods();
		for (int i = 0; i < methods.length; i++) {
			commit(methods[i]);
		}

		final FieldInfo[] fields = ce.fields();
		for (int i = 0; i < fields.length; i++) {
			commit(fields[i]);
		}

		ce.commit();

		ce.setDirty(false);
		release(info);
	}

	public void commit(final MethodInfo info) {
		final MethodEditor me = editMethod(info);
		me.commit();

		// We make the method's class dirty so it, too, will be committed
		me.declaringClass().setDirty(true);
		me.setDirty(false);
		release(info);
	}

	public void commit(final FieldInfo info) {
		final FieldEditor fe = editField(info);
		fe.commit();

		// We make the method's class dirty so it, too, will be committed
		fe.declaringClass().setDirty(true);
		fe.setDirty(false);
		release(info);
	}

	public void commit() {
		Object[] array = fieldEditors.values().toArray();
		for (int i = 0; i < array.length; i++) {
			final FieldEditor fe = (FieldEditor) array[i];
			if (!ignoreField(fe.memberRef())) {
				commit(fe.fieldInfo());
			}
		}

		array = methodEditors.values().toArray();
		for (int i = 0; i < array.length; i++) {
			final MethodEditor me = (MethodEditor) array[i];
			if (!ignoreMethod(me.memberRef())) {
				commit(me.methodInfo());
			}
		}

		array = classEditors.values().toArray();
		for (int i = 0; i < array.length; i++) {
			final ClassEditor ce = (ClassEditor) array[i];
			if (!ignoreClass(ce.type())) {
				commit(ce.classInfo());
			}
		}
	}

	public void commitDirty() {
		PersistentBloatContext.comm("Committing dirty data");

		// Commit all dirty fields
		Object[] array = this.fieldEditors.values().toArray();
		for (int i = 0; i < array.length; i++) {
			final FieldEditor fe = (FieldEditor) array[i];
			if (fe.isDirty() && !ignoreField(fe.memberRef())) {
				PersistentBloatContext.comm("  Committing field: "
						+ fe.declaringClass().name() + "." + fe.name());
				commit(fe.fieldInfo());
			}
		}

		// Commit all dirty methods
		array = this.methodEditors.values().toArray();
		for (int i = 0; i < array.length; i++) {
			final MethodEditor me = (MethodEditor) array[i];
			if (me.isDirty() && !ignoreMethod(me.memberRef())) {
				PersistentBloatContext.comm("  Committing method: "
						+ me.declaringClass().name() + "." + me.name()
						+ me.type());
				commit(me.methodInfo());
			}
		}

		// Commit all dirty classes
		array = this.classEditors.values().toArray();
		for (int i = 0; i < array.length; i++) {
			final ClassEditor ce = (ClassEditor) array[i];
			if (ce.isDirty() && !ignoreClass(ce.type())) {
				PersistentBloatContext.comm("  Committing class: " + ce.name());
				commit(ce.classInfo());
			}
		}
	}
}
