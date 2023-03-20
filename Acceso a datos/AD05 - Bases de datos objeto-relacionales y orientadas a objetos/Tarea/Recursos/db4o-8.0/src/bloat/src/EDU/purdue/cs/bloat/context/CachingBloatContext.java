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

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;

/**
 * Does a lot of the same stuff as <tt>PersistentBloatContext</tt> except that
 * it manages the chaches of BLOAT objects. For example, when a
 * <tt>MethodEditor</tt> is no longer needed, it is removed from the cache if
 * it is not dirty. This context is meant to used in volatile memory.
 */
public class CachingBloatContext extends PersistentBloatContext {

	// Keep track of reference counts in a manner reminiscent of the old
	// Editor class.
	protected Map classRC;

	protected Map methodRC;

	protected Map fieldRC;

	/**
	 * Constructor.
	 * 
	 * @param loader
	 *            Used to load classes
	 * @param classes
	 *            Some initial classes in the context
	 * @param closure
	 *            Do we look for the maximum number of classes?
	 */
	public CachingBloatContext(final ClassInfoLoader loader,
			final Collection classes, final boolean closure) {
		super(loader, closure);

		classRC = new HashMap();
		methodRC = new HashMap();
		fieldRC = new HashMap();

		addClasses(classes);
	}

	public ClassEditor newClass(final int modifiers, final String className,
			final Type superType, final Type[] interfaces) {

		final ClassEditor ce = super.newClass(modifiers, className, superType,
				interfaces);
		final ClassInfo info = ce.classInfo();
		classRC.put(info, new Integer(1));

		return ce;
	}

	public ClassEditor editClass(final ClassInfo info) {
		// Check the cache
		ClassEditor ce = (ClassEditor) classEditors.get(info);

		if (ce == null) {
			ce = new ClassEditor(this, info);
			classEditors.put(info, ce);
			classRC.put(info, new Integer(1));

			if (!classInfos.containsValue(info)) {
				final String className = ce.name().intern();
				BloatContext.db("editClass(ClassInfo): " + className + " -> "
						+ info);
				classInfos.put(className, info);
			}

		} else {
			final Integer rc = (Integer) classRC.get(info);
			classRC.put(info, new Integer(rc.intValue() + 1));
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
				}

				release(ce.classInfo());

			} catch (final ClassNotFoundException ex1) {
				throw new NoSuchMethodException(method.toString() + "("
						+ ex1.getMessage() + ")");

			} catch (final ClassFormatException ex2) {
				throw new NoSuchMethodException(method.toString() + "("
						+ ex2.getMessage() + ")");

			}

			throw new NoSuchMethodException(method.toString());
		}

		return (editMethod(info));
	}

	public MethodEditor editMethod(final MethodInfo info) {
		// Check methodEditors cache
		MethodEditor me = (MethodEditor) methodEditors.get(info);

		if (me == null) {
			final ClassInfo classInfo = info.declaringClass();
			me = new MethodEditor(editClass(classInfo), info);
			release(classInfo);

			methodEditors.put(info, me);
			methodRC.put(info, new Integer(1));
			BloatContext
					.db("Creating a new MethodEditor for " + me.memberRef());

		} else {
			final Integer rc = (Integer) methodRC.get(info);
			methodRC.put(info, new Integer(rc.intValue() + 1));
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

				release(ce.classInfo());
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

		BloatContext.db("Editing " + info);

		if (fe == null) {
			final ClassInfo classInfo = info.declaringClass();
			fe = new FieldEditor(editClass(classInfo), info);
			release(classInfo);

			fieldEditors.put(info, fe);
			fieldRC.put(info, new Integer(0));
			BloatContext.db("Creating a new FieldEditor for "
					+ fe.nameAndType());

		} else {
			final Integer rc = (Integer) fieldRC.get(info);
			fieldRC.put(info, new Integer(rc.intValue() + 1));

		}

		return (fe);
	}

	public void release(final ClassInfo info) {
		final Integer rc = (Integer) classRC.get(info);

		if ((rc != null) && (rc.intValue() > 1)) {
			// Not done yet;
			classRC.put(info, new Integer(rc.intValue() - 1));
			return;

		}

		ClassEditor ce = (ClassEditor) classEditors.get(info);
		if ((ce != null) && ce.isDirty()) {
			return;
		}

		// We're done with this class, remove all traces of it
		ce = (ClassEditor) classEditors.remove(info);
		classRC.remove(info);
		classEditors.remove(info);

		final Iterator iter = classInfos.keySet().iterator();
		while (iter.hasNext()) {
			final String name = (String) iter.next();
			final ClassInfo info2 = (ClassInfo) classInfos.get(name);
			if (info2 == info) {
				BloatContext.db("Removing ClassInfo: " + name + " -> " + info2);
				classInfos.remove(name);
				break;
			}
		}

		if (ce != null) {
			// Remove all of the class's fields and methods also
			final MethodInfo[] methods = ce.methods();
			for (int i = 0; i < methods.length; i++) {
				release(methods[i]);
			}

			final FieldInfo[] fields = ce.fields();
			for (int i = 0; i < fields.length; i++) {
				release(fields[i]);
			}
		}

	}

	public void release(final MethodInfo info) {
		final Integer rc = (Integer) classRC.get(info);

		if ((rc != null) && (rc.intValue() > 1)) {
			methodRC.put(info, new Integer(rc.intValue() - 1));
			return;
		}

		final MethodEditor me = (MethodEditor) methodEditors.get(info);

		// We should keep dirty methods around. My original thought was
		// that if we committed dirty methods when they were released, we
		// risk having MethodEditors editing different versions of the
		// same method. So, if we don't release dirty methods, we'll only
		// have ONE MethodEditor.
		if ((me != null) && me.isDirty()) {
			return;
		}

		// We're done with this method, remove all traces of it
		methodRC.remove(info);
		methodEditors.remove(info);

		final Iterator iter = methodInfos.keySet().iterator();
		while (iter.hasNext()) {
			final MemberRef ref = (MemberRef) iter.next();
			final MethodInfo info2 = (MethodInfo) methodInfos.get(ref);
			if (info2 == info) {
				methodInfos.remove(ref);
				break;
			}
		}
	}

	public void release(final FieldInfo info) {
		final Integer rc = (Integer) fieldRC.get(info);

		BloatContext.db("Releasing " + info);

		if ((rc != null) && (rc.intValue() > 1)) {
			fieldRC.put(info, new Integer(rc.intValue() - 1));
			return;
		}

		final FieldEditor fe = (FieldEditor) fieldEditors.get(info);
		if ((fe != null) && fe.isDirty()) {
			return;
		}

		// We're done with this field, remove all traces of it
		fieldRC.remove(info);
		fieldEditors.remove(info);

		final Iterator iter = fieldInfos.keySet().iterator();
		while (iter.hasNext()) {
			final MemberRef ref = (MemberRef) iter.next();
			final FieldInfo info2 = (FieldInfo) fieldInfos.get(ref);
			if (info2 == info) {
				fieldInfos.remove(ref);
				break;
			}
		}
	}

	public void commit(final ClassInfo info) {
		super.commit(info);

		classEditors.remove(info);
		classRC.remove(info);
	}

	public void commit(final MethodInfo info) {
		super.commit(info);

		methodEditors.remove(info);
		methodRC.remove(info);
	}

	public void commit(final FieldInfo info) {
		super.commit(info);

		fieldEditors.remove(info);
		fieldRC.remove(info);
	}

	public void commit() {
		Collection fieldValues = fieldEditors.values();
		FieldEditor[] fieldArray = (FieldEditor[]) fieldValues.toArray(new FieldEditor[fieldValues.size()]);
		for (int i = 0; i < fieldArray.length; i++) {
			final FieldEditor fe = fieldArray[i];
			commit(fe.fieldInfo());
		}

		Collection methodValues = methodEditors.values();
		MethodEditor[] methodArray = (MethodEditor[]) methodValues.toArray(new MethodEditor[methodValues.size()]);
		for (int i = 0; i < methodArray.length; i++) {
			final MethodEditor me = methodArray[i];
			commit(me.methodInfo());
		}

		Collection classValues = classEditors.values();
		ClassEditor[] classArray = (ClassEditor[]) classValues.toArray(new ClassEditor[classValues.size()]);
		for (int i = 0; i < classArray.length; i++) {
			final ClassEditor ce = classArray[i];
			commit(ce.classInfo());
		}
	}

	/**
	 * Return a textual description of all of the caches. Useful if we run out
	 * of memory.
	 */
	public String toString() {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);

		pw.println("Context of caches in CachingBloatContext...");

		pw.println("  Class Infos");
		Iterator iter = classInfos.keySet().iterator();
		while (iter.hasNext()) {
			final Object key = iter.next();
			pw.println("    " + key + " -> " + classInfos.get(key));
		}

		pw.println("  Class Editors");
		iter = classEditors.keySet().iterator();
		while (iter.hasNext()) {
			final Object key = iter.next();
			pw.println("    " + key + " -> " + classEditors.get(key));
		}

		pw.println("  Class RC");
		iter = classRC.keySet().iterator();
		while (iter.hasNext()) {
			final Object key = iter.next();
			pw.println("    " + key + " -> " + classRC.get(key));
		}

		pw.println("  Method Infos");
		iter = methodInfos.keySet().iterator();
		while (iter.hasNext()) {
			final Object key = iter.next();
			pw.println("    " + key + " -> " + methodInfos.get(key));
		}

		pw.println("  Method Editors");
		iter = methodEditors.keySet().iterator();
		while (iter.hasNext()) {
			final Object key = iter.next();
			pw.println("    " + key + " -> " + methodEditors.get(key));
		}

		pw.println("  Method RC");
		iter = methodRC.keySet().iterator();
		while (iter.hasNext()) {
			final Object key = iter.next();
			pw.println("    " + key + " -> " + methodRC.get(key));
		}

		pw.println("  Field Infos");
		iter = fieldInfos.keySet().iterator();
		while (iter.hasNext()) {
			final Object key = iter.next();
			pw.println("    " + key + " -> " + fieldInfos.get(key));
		}

		pw.println("  Field Editors");
		iter = fieldEditors.keySet().iterator();
		while (iter.hasNext()) {
			final Object key = iter.next();
			pw.println("    " + key + " -> " + fieldEditors.get(key));
		}

		pw.println("  Field RC");
		iter = fieldRC.keySet().iterator();
		while (iter.hasNext()) {
			final Object key = iter.next();
			pw.println("    " + key + " -> " + fieldRC.get(key));
		}

		return (sw.toString());
	}
}
