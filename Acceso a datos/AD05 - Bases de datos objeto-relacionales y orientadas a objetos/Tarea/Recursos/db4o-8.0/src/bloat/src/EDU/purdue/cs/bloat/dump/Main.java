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
package EDU.purdue.cs.bloat.dump;

import java.util.*;

import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;

/**
 * Prints the contents of a Java classfile to the console.
 */
public class Main implements Opcode {
	public static void main(final String[] args) {
		final ClassFileLoader loader = new ClassFileLoader();
		String className = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-help")) {
				Main.usage();
			} else if (args[i].equals("-classpath")) {
				if (++i >= args.length) {
					Main.usage();
				}

				final String classpath = args[i];
				loader.setClassPath(classpath);
			} else if (args[i].startsWith("-")) {
				Main.usage();
			} else {
				if (className != null) {
					Main.usage();
				}
				className = args[i];
			}
		}

		if (className == null) {
			Main.usage();
		}

		ClassInfo info = null;

		try {
			info = loader.loadClass(className);
		} catch (final ClassNotFoundException ex) {
			System.err.println("Couldn't find class: " + ex.getMessage());
			System.exit(1);
		}

		final Collection classes = new ArrayList(1);
		classes.add(className);

		final BloatContext context = new CachingBloatContext(loader, classes,
				true);

		if (info != null) {
			Main.printClass(context, info);
		}
	}

	private static void usage() {
		System.err
				.println("Usage: java EDU.purdue.cs.bloat.dump.Main "
						+ "\n            [-options] class"
						+ "\n"
						+ "\nwhere options include:"
						+ "\n    -help             print out this message"
						+ "\n    -classpath <directories separated by colons>"
						+ "\n                      list directories in which to look for classes");
		System.exit(0);
	}

	private static void printClass(final EditorContext context,
			final ClassInfo info) {
		final ClassEditor c = context.editClass(info);

		if (c.isPublic()) {
			System.out.print("public ");
		} else if (c.isPrivate()) {
			System.out.print("private ");
		} else if (c.isProtected()) {
			System.out.print("protected ");
		}

		if (c.isStatic()) {
			System.out.print("static ");
		}

		if (c.isFinal()) {
			System.out.print("final ");
		}

		if (c.isInterface()) {
			System.out.print("interface ");
		} else if (c.isAbstract()) {
			System.out.print("abstract class ");
		} else {
			System.out.print("class ");
		}

		System.out.print(c.type().className());

		if (c.superclass() != null) {
			System.out.print(" extends " + c.superclass().className());
		}

		final Type[] interfaces = c.interfaces();

		for (int i = 0; i < interfaces.length; i++) {
			if (i == 0) {
				System.out.print(" implements");
			} else {
				System.out.print(",");
			}

			System.out.print(" " + interfaces[i].className());
		}

		System.out.println();
		System.out.println("{");

		final FieldInfo[] fields = c.fields();

		for (int i = 0; i < fields.length; i++) {
			FieldEditor f = null;

			try {
				f = context.editField(fields[i]);
			} catch (final ClassFormatException ex) {
				System.err.println(ex.getMessage());
				System.exit(1);
			}

			System.out.print("    ");

			if (f.isPublic()) {
				System.out.print("public ");
			} else if (f.isPrivate()) {
				System.out.print("private ");
			} else if (f.isProtected()) {
				System.out.print("protected ");
			}

			if (f.isTransient()) {
				System.out.print("transient ");
			}

			if (f.isVolatile()) {
				System.out.print("volatile ");
			}

			if (f.isStatic()) {
				System.out.print("static ");
			}

			if (f.isFinal()) {
				System.out.print("final ");
			}

			System.out.println(f.type() + " " + f.name());

			context.release(fields[i]);
		}

		if (fields.length != 0) {
			System.out.println();
		}

		final MethodInfo[] methods = c.methods();

		for (int i = 0; i < methods.length; i++) {
			MethodEditor m = null;

			try {
				m = context.editMethod(methods[i]);
			} catch (final ClassFormatException ex) {
				System.err.println(ex.getMessage());
				System.exit(1);
			}

			if (i != 0) {
				System.out.println();
			}

			System.out.print("    ");

			if (m.isPublic()) {
				System.out.print("public ");
			} else if (m.isPrivate()) {
				System.out.print("private ");
			} else if (m.isProtected()) {
				System.out.print("protected ");
			}

			if (m.isNative()) {
				System.out.print("native ");
			}

			if (m.isSynchronized()) {
				System.out.print("synchronized ");
			}

			if (m.isAbstract()) {
				System.out.print("abstract ");
			}

			if (m.isStatic()) {
				System.out.print("static ");
			}

			if (m.isFinal()) {
				System.out.print("final ");
			}

			System.out.println(m.type() + " " + m.name());

			final Iterator iter = m.code().iterator();

			while (iter.hasNext()) {
				final Object obj = iter.next();
				System.out.println("        " + obj);
			}

			context.release(methods[i]);
		}

		System.out.println("}");

		context.release(info);
	}
}
