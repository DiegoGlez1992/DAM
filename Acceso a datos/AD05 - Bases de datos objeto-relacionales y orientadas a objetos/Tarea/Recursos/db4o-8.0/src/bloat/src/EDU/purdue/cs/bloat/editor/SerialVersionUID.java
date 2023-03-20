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
import java.lang.reflect.*;
import java.security.*;
import java.util.*;

import EDU.purdue.cs.bloat.reflect.*;

/**
 * <P>
 * This class computes the serial version UID of a class modeled by a
 * <code>ClassEditor</code>. Otherwise, we would have to load the class in
 * order to compute its serial version UID. That would suck.
 * </P>
 * 
 * <P>
 * The algorithm for computing the serial version UID can be found in the <A
 * href="http://java.sun.com/j2se/1.3/docs/guide/serialization/spec">serialization
 * spec</A>
 * </P>
 */
public class SerialVersionUID {

	/**
	 * Returns <code>true</code> if the class modeled by the given
	 * <code>ClassEditor</code> implements {@link java.io.Serializable
	 * Serializable}. It checks superclasses.
	 */
	public static boolean implementsSerializable(final ClassEditor ce) {
		if (ce.type().equals(Type.OBJECT)) {
			// Stop the recursion!
			return (false);
		}

		final Type serializable = Type.getType("Ljava/io/Serializable;");
		final Type[] interfaces = ce.interfaces();
		for (int i = 0; i < interfaces.length; i++) {
			if (interfaces[i].equals(serializable)) {
				return (true);
			}
		}

		// Does its superclass implement Serializable?

		final Type superclass = ce.superclass();
		final ClassInfoLoader loader = ce.classInfo().loader();
		try {
			final ClassInfo ci = loader.loadClass(superclass.className());
			final ClassEditor sce = new ClassEditor(ce.context(), ci);
			return (SerialVersionUID.implementsSerializable(sce));

		} catch (final ClassNotFoundException ex) {
			System.err.println("Could not load class: " + superclass
					+ ", superclass of " + ce.name());
			System.exit(1);
		}
		return (false);
	}

	/**
	 * Returns the serial version UID of the class modeled by the given
	 * <code>ClassEditor</code>.
	 * 
	 * @param ce
	 *            The class must implement {@link java.io.Serializable
	 *            Serializable}
	 */
	public static long serialVersionUID(final ClassEditor ce) {
		// Make sure the class implements Serializable
		if (!SerialVersionUID.implementsSerializable(ce)) {
			final String s = "Class " + ce.name()
					+ " does not implement java.io.Serializable";
			throw new IllegalArgumentException(s);
		}

		// If the class already has a serialVersionUID, return that
		final FieldInfo[] fields = ce.fields();
		for (int i = 0; i < fields.length; i++) {
			final FieldEditor fe = new FieldEditor(ce, fields[i]);
			if (fe.name().equals("serialVersionUID")) {
				final Object value = fe.constantValue();
				if (value != null) {
					if (value instanceof Long) {
						return (((Long) value).longValue());
					}
				}
			}
		}

		// Now, compute the digest of the bytes using SHA
		MessageDigest algorithm = null;
		try {
			algorithm = MessageDigest.getInstance("SHA");

		} catch (final NoSuchAlgorithmException ex) {
			final String s = "Can't use SHA-1 message digest algorith!";
			throw new IllegalArgumentException(s);
		}

		final DataOutputStream dos = new DataOutputStream(
				new DigestOutputStream(new ByteArrayOutputStream(), algorithm));

		try {
			// Write a bunch of information about the class to the output
			// stream
			SerialVersionUID.writeClassName(ce, dos);
			SerialVersionUID.writeClassModifiers(ce, dos);
			SerialVersionUID.writeInterfaceNames(ce, dos);
			SerialVersionUID.writeFields(ce, dos);
			SerialVersionUID.writeStaticInitializer(ce, dos);
			SerialVersionUID.writeConstructors(ce, dos);
			SerialVersionUID.writeMethods(ce, dos);

			dos.flush();
			dos.close();

		} catch (final IOException ex) {
			final String s = ("While computing serial version UID: " + ex);
			throw new IllegalArgumentException(s);
		}

		// Compute the hash value from the first 64 bites of the digest
		final byte[] digest = algorithm.digest();
		long uid = 0;
		for (int i = 0; i < Math.min(8, digest.length); i++) {
			uid += (long) (digest[i] & 255) << (i * 8);
		}
		return (uid);

	}

	/**
	 * Writes the name of the class to the data output stream
	 */
	private static void writeClassName(final ClassEditor ce,
			final DataOutputStream dos) throws IOException {
		dos.writeUTF(ce.name().replace('/', '.'));
	}

	/**
	 * Returns the Java reflection modifiers for a given class
	 */
	static int getModifiers(final ClassEditor ce) {
		// Translate BLOAT's class modifiers into Java's reflection
		// modifiers
		int modifiers = 0;

		if (ce.isPublic()) {
			modifiers |= Modifier.PUBLIC;
		}

		if (ce.isPrivate()) {
			modifiers |= Modifier.PRIVATE;
		}

		if (ce.isProtected()) {
			modifiers |= Modifier.PROTECTED;
		}

		if (ce.isStatic()) {
			modifiers |= Modifier.STATIC;
		}

		if (ce.isFinal()) {
			modifiers |= Modifier.FINAL;
		}

		if (ce.isAbstract()) {
			modifiers |= Modifier.ABSTRACT;
		}

		if (ce.isInterface()) {
			modifiers |= Modifier.INTERFACE;
		}

		return (modifiers);
	}

	/**
	 * Writes the class's modifiers to the output stream
	 */
	private static void writeClassModifiers(final ClassEditor ce,
			final DataOutputStream dos) throws IOException {
		dos.writeInt(SerialVersionUID.getModifiers(ce));
	}

	/**
	 * Writes the names of the interfaces implemented by the class to the output
	 * stream
	 */
	private static void writeInterfaceNames(final ClassEditor ce,
			final DataOutputStream dos) throws IOException {

		// Sort interfaces by name
		final SortedSet sorted = new TreeSet();

		final Type[] interfaces = ce.interfaces();
		for (int i = 0; i < interfaces.length; i++) {
			sorted.add(interfaces[i].className().replace('/', '.'));
		}

		final Iterator iter = sorted.iterator();
		while (iter.hasNext()) {
			final String name = (String) iter.next();
			dos.writeUTF(name);
		}

	}

	/**
	 * Returns the Java reflection modifiers for a field
	 */
	static int getModifiers(final FieldEditor fe) {
		int modifiers = 0;

		if (fe.isPublic()) {
			modifiers |= Modifier.PUBLIC;
		}

		if (fe.isPrivate()) {
			modifiers |= Modifier.PRIVATE;
		}

		if (fe.isProtected()) {
			modifiers |= Modifier.PROTECTED;
		}

		if (fe.isPackage()) {
			// Nothing
		}

		if (fe.isStatic()) {
			modifiers |= Modifier.STATIC;
		}

		if (fe.isFinal()) {
			modifiers |= Modifier.FINAL;
		}

		if (fe.isVolatile()) {
			modifiers |= Modifier.VOLATILE;
		}

		if (fe.isTransient()) {
			// Kind of a moot point
			modifiers |= Modifier.TRANSIENT;
		}

		return (modifiers);
	}

	/**
	 * Writes information about the class's fields to the output stream
	 */
	private static void writeFields(final ClassEditor ce,
			final DataOutputStream dos) throws IOException {

		// Sort the fields by their names
		final SortedSet sorted = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				FieldEditor fe1 = (FieldEditor) o1;
				FieldEditor fe2 = (FieldEditor) o2;
				return (fe1.name().compareTo(fe2.name()));
			}
		});

		final FieldInfo[] infos = ce.fields();
		for (int i = 0; i < infos.length; i++) {
			final FieldEditor fe = new FieldEditor(ce, infos[i]);
			// Ignore private static and private transient fields
			if (fe.isPrivate() && fe.isStatic()) {
				break;

			} else if (fe.isPrivate() && fe.isTransient()) {
				break;

			} else {
				sorted.add(fe);
			}
		}

		final Iterator iter = sorted.iterator();
		while (iter.hasNext()) {
			final FieldEditor fe = (FieldEditor) iter.next();
			dos.writeUTF(fe.name());
			dos.writeInt(SerialVersionUID.getModifiers(fe));
			dos.writeUTF(fe.type().descriptor());
		}
	}

	/**
	 * Returns the Java reflection descriptors for a method
	 */
	static int getModifiers(final MethodEditor me) {
		int modifiers = 0;

		if (me.isPublic()) {
			modifiers |= Modifier.PUBLIC;
		}

		if (me.isPrivate()) {
			modifiers |= Modifier.PRIVATE;
		}

		if (me.isProtected()) {
			modifiers |= Modifier.PROTECTED;
		}

		if (me.isPackage()) {
			// Nothing
		}

		if (me.isStatic()) {
			modifiers |= Modifier.STATIC;
		}

		if (me.isFinal()) {
			modifiers |= Modifier.FINAL;
		}

		if (me.isSynchronized()) {
			modifiers |= Modifier.SYNCHRONIZED;
		}

		if (me.isNative()) {
			modifiers |= Modifier.NATIVE;
		}

		if (me.isAbstract()) {
			modifiers |= Modifier.ABSTRACT;
		}

		if (me.isInterface()) {
			modifiers |= Modifier.INTERFACE;
		}

		return (modifiers);
	}

	/**
	 * Writes information about the classes static initializer if it has one
	 */
	private static void writeStaticInitializer(final ClassEditor ce,
			final DataOutputStream dos) throws IOException {

		MethodEditor clinit = null;

		final MethodInfo[] methods = ce.methods();
		for (int i = 0; i < methods.length; i++) {
			final MethodEditor me = new MethodEditor(ce, methods[i]);
			if (me.name().equals("<clinit>")) {
				clinit = me;
				break;
			}
		}

		if (clinit != null) {
			dos.writeUTF("<clinit>");
			dos.writeInt(Modifier.STATIC);
			dos.writeUTF("()V");
		}
	}

	/**
	 * Writes information about the class's constructors
	 */
	private static void writeConstructors(final ClassEditor ce,
			final DataOutputStream dos) throws IOException {

		// Sort constructors by their signatures
		final SortedSet sorted = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				MethodEditor me1 = (MethodEditor) o1;
				MethodEditor me2 = (MethodEditor) o2;
				return (me1.type().descriptor().compareTo(me2.type()
						.descriptor()));
			}
		});

		final MethodInfo[] methods = ce.methods();
		for (int i = 0; i < methods.length; i++) {
			final MethodEditor me = new MethodEditor(ce, methods[i]);
			if (me.name().equals("<init>")) {
				if (!me.isPrivate()) {
					// Ignore private constructors
					sorted.add(me);
				}
			}
		}

		final Iterator iter = sorted.iterator();
		while (iter.hasNext()) {
			final MethodEditor init = (MethodEditor) iter.next();
			dos.writeUTF("<init>");
			dos.writeInt(SerialVersionUID.getModifiers(init));
			dos.writeUTF(init.type().descriptor());
		}
	}

	/**
	 * Write information about the class's methods
	 */
	private static void writeMethods(final ClassEditor ce,
			final DataOutputStream dos) throws IOException {

		// Sort constructors by their names and signatures
		final SortedSet sorted = new TreeSet(new Comparator() {
			public int compare(Object o1, Object o2) {
				MethodEditor me1 = (MethodEditor) o1;
				MethodEditor me2 = (MethodEditor) o2;

				String d1 = me1.name() + me1.type().descriptor();
				String d2 = me2.name() + me2.type().descriptor();
				return (d1.compareTo(d2));
			}
		});

		final MethodInfo[] methods = ce.methods();
		for (int i = 0; i < methods.length; i++) {
			final MethodEditor me = new MethodEditor(ce, methods[i]);
			if (!me.isPrivate() && !me.isConstructor()
					&& !me.name().equals("<clinit>")) {
				// Ignore private methods
				sorted.add(me);
			}
		}

		final Iterator iter = sorted.iterator();
		while (iter.hasNext()) {
			final MethodEditor me = (MethodEditor) iter.next();
			dos.writeUTF(me.name());
			dos.writeInt(SerialVersionUID.getModifiers(me));
			dos.writeUTF(me.type().descriptor());
		}

	}

}
