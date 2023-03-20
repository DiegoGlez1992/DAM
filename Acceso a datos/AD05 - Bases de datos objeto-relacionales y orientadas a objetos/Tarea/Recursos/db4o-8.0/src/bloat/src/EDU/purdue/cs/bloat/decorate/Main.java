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
package EDU.purdue.cs.bloat.decorate;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.trans.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Inserts residency, update, or swizzle checks into the methods of the classes
 * specified on the command line.
 * 
 * Usage: java EDU.purdue.cs.bloat.decorate.Main [-options] classes output_dir
 * 
 * where options include: -help print out this message -v -verbose turn on
 * verbose mode (can be given multiple times) -classpath <directories separated
 * by colons list directories in which to look for classes -f decorate files
 * even if up-to-date -closure recursively decorate referenced classes
 * -relax-loading don't report errors if a class is not found -skip
 * <class|package.*> skip the given class or package (this option can be given
 * more than once) -only <class|package.*> skip all but the given class or
 * package (this option can be given more than once) -rc insert residency checks
 * (default) -norc don't insert residency checks -uc insert update checks
 * (default) -sc insert array swizzle checks (default) -nosc don't insert array
 * swizzle checkso
 * 
 */
public class Main implements Opcode {
	private static int VERBOSE = 0; // The level of verbosity

	private static boolean FORCE = false;

	private static boolean CLOSURE = false;

	private static boolean RC = true; // Insert residency checks?

	private static boolean UC = true; // Insert update checks?

	private static boolean SC = true; // Insert swizzle checks?

	private static final List SKIP = new ArrayList();

	private static final List ONLY = new ArrayList();

	private static final int NONE = 0;

	private static final int POINTER = 1;

	private static final int SCALAR = 2;

	/**
	 * Parse the command line. Inserts residency, update, and swizzle checks
	 * into the bytecode of the methods of the specified classes.
	 */
	public static void main(final String[] args) {
		final ClassFileLoader loader = new ClassFileLoader();
		List classes = new ArrayList(); // Names of classes from command line
		boolean gotdir = false; // Did user specify an output dir?

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-v") || args[i].equals("-verbose")) {
				Main.VERBOSE++;
			} else if (args[i].equals("-help")) {
				Main.usage();
			} else if (args[i].equals("-classpath")) {
				if (++i >= args.length) {
					Main.usage();
				}

				final String classpath = args[i];
				loader.setClassPath(classpath);
			} else if (args[i].equals("-skip")) {
				if (++i >= args.length) {
					Main.usage();
				}

				final String pkg = args[i].replace('.', '/');
				Main.SKIP.add(pkg);
			} else if (args[i].equals("-only")) {
				if (++i >= args.length) {
					Main.usage();
				}

				final String pkg = args[i].replace('.', '/');
				Main.ONLY.add(pkg);
			} else if (args[i].equals("-closure")) {
				Main.CLOSURE = true;
			} else if (args[i].equals("-relax-loading")) {
				ClassHierarchy.RELAX = true;
			} else if (args[i].equals("-f")) {
				Main.FORCE = true;
			} else if (args[i].equals("-norc")) {
				Main.RC = false;
			} else if (args[i].equals("-rc")) {
				Main.RC = true;
			} else if (args[i].equals("-nouc")) {
				Main.UC = false;
			} else if (args[i].equals("-uc")) {
				Main.UC = true;
			} else if (args[i].equals("-nosc")) {
				Main.SC = false;
			} else if (args[i].equals("-sc")) {
				Main.SC = true;
			} else if (args[i].startsWith("-")) {
				Main.usage();
			} else if (i == args.length - 1) {
				// Last argument is the name of the outpu directory
				final File f = new File(args[i]);

				if (f.exists() && !f.isDirectory()) {
					System.err.println("No such directory: " + f.getPath());
					System.exit(2);
				}

				loader.setOutputDir(f);
				gotdir = true;
			} else {
				classes.add(args[i]);
			}
		}

		if (!gotdir) {
			Main.usage();
		}

		if (classes.size() == 0) {
			Main.usage();
		}

		if (Main.VERBOSE > 3) {
			ClassFileLoader.DEBUG = true;
			ClassEditor.DEBUG = true;
		}

		boolean errors = false;

		final Iterator iter = classes.iterator();

		// Load each class specified on the command line
		while (iter.hasNext()) {
			final String name = (String) iter.next();

			try {
				loader.loadClass(name);
			} catch (final ClassNotFoundException ex) {
				System.err.println("Couldn't find class: " + ex.getMessage());
				errors = true;
			}
		}

		if (errors) {
			System.exit(1);
		}

		final BloatContext context = new CachingBloatContext(loader, classes,
				Main.CLOSURE);

		if (!Main.CLOSURE) {
			final Iterator e = classes.iterator();

			while (e.hasNext()) {
				final String name = (String) e.next();
				try {
					final ClassInfo info = loader.loadClass(name);
					Main.decorateClass(context, info);
				} catch (final ClassNotFoundException ex) {
					System.err.println("Couldn't find class: "
							+ ex.getMessage());
					System.exit(1);
				}
			}
		} else {
			classes = null;

			final ClassHierarchy hier = context.getHierarchy();

			final Iterator e = hier.classes().iterator();

			while (e.hasNext()) {
				final Type t = (Type) e.next();

				if (t.isObject()) {
					try {
						final ClassInfo info = loader.loadClass(t.className());
						Main.decorateClass(context, info);
					} catch (final ClassNotFoundException ex) {
						System.err.println("Couldn't find class: "
								+ ex.getMessage());
						System.exit(1);
					}
				}
			}
		}
	}

	private static void usage() {
		System.err
				.println("Usage: java EDU.purdue.cs.bloat.decorate.Main "
						+ "\n            [-options] classes output_dir"
						+ "\n"
						+ "\nwhere options include:"
						+ "\n    -help             print out this message"
						+ "\n    -v -verbose       turn on verbose mode "
						+ "(can be given multiple times)"
						+ "\n    -classpath <directories separated by colons>"
						+ "\n                      list directories in which to look for classes"
						+ "\n    -f                decorate files even if up-to-date"
						+ "\n    -closure          recursively decorate referenced classes"
						+ "\n    -relax-loading    don't report errors if a class is not found"
						+ "\n    -skip <class|package.*>"
						+ "\n                      skip the given class or package"
						+ "\n                      (this option can be given more than once)"
						+ "\n    -only <class|package.*>"
						+ "\n                      skip all but the given class or package"
						+ "\n                      (this option can be given more than once)"
						+ "\n    -rc               insert residency checks (default)"
						+ "\n    -norc             don't insert residency checks"
						+ "\n    -uc               insert update checks (default)"
						+ "\n    -sc               insert array swizzle checks (default)"
						+ "\n    -nosc             don't insert array swizzle checks");
		System.exit(0);
	}

	/**
	 * Adds residency/update/swizzle checks to all of the methods in a given
	 * class.
	 * 
	 * @param context
	 *            Information about all the classes we're dealing with
	 * @param info
	 *            Information about the class we're decorating
	 */
	private static void decorateClass(final EditorContext context,
			final ClassInfo info) {
		final ClassFile classFile = (ClassFile) info;

		// Check to see if the class file is up-to-date
		if (!Main.FORCE) {
			final File source = classFile.file();
			final File target = classFile.outputFile();

			if ((source != null) && (target != null) && source.exists()
					&& target.exists()
					&& (source.lastModified() < target.lastModified())) {

				if (Main.VERBOSE > 1) {
					System.out.println(classFile.name() + " is up to date");
				}

				return;
			}
		}

		if (Main.VERBOSE > 2) {
			classFile.print(System.out);
		}

		final ClassEditor c = context.editClass(info);

		boolean skip = false;

		final String name = c.type().className();
		final String qual = c.type().qualifier() + "/*";

		// Edit only classes explicitly mentioned.
		if (Main.ONLY.size() > 0) {
			skip = true;

			// Only edit classes we explicitly don't name.
			for (int i = 0; i < Main.ONLY.size(); i++) {
				final String pkg = (String) Main.ONLY.get(i);

				if (name.equals(pkg) || qual.equals(pkg)) {
					skip = false;
					break;
				}
			}
		}

		// Don't edit classes we explicitly skip.
		if (!skip) {
			for (int i = 0; i < Main.SKIP.size(); i++) {
				final String pkg = (String) Main.SKIP.get(i);

				if (name.equals(pkg) || qual.equals(pkg)) {
					skip = true;
					break;
				}
			}
		}

		if (skip) {
			if (Main.VERBOSE > 0) {
				System.out.println("Skipping " + c.type().className());
			}

			context.release(info);
			return;
		}

		if (Main.VERBOSE > 0) {
			System.out.println("Decorating class " + c.type().className());
		}

		if (Main.VERBOSE > 2) {
			((ClassFile) info).print(System.out);
		}

		final MethodInfo[] methods = c.methods();

		// Add residency checks (via transform()) to each method in the class
		for (int j = 0; j < methods.length; j++) {
			MethodEditor m;

			try {
				m = context.editMethod(methods[j]);
			} catch (final ClassFormatException ex) {
				System.err.println(ex.getMessage());
				continue;
			}

			Main.transform(m);
			context.commit(methods[j]);
		}

		context.commit(info);
	}

	/**
	 * Inserts residency/update/swizzle checks into a method. Iterates over the
	 * bytecodes in the method and inserts the appropriate residency opcode.
	 * 
	 * @param method
	 *            The method to which to add checks.
	 * 
	 * @see MethodEditor#code
	 */
	private static void transform(final MethodEditor method) {
		if (Main.VERBOSE > 1) {
			System.out.println("Decorating method " + method);
		}

		// Optimize initialization of arrays to speed things up.
		CompactArrayInitializer.transform(method);

		final ListIterator iter = method.code().listIterator();

		// Go through the code (Instructions and Labels) in the method
		INST: while (iter.hasNext()) {
			final Object ce = iter.next();

			if (Main.VERBOSE > 2) {
				System.out.println("Examining " + ce);
			}

			if (ce instanceof Instruction) {
				final Instruction inst = (Instruction) ce;

				int uctype = Main.NONE; // Type of update check (POINTER or
										// SCALAR)
				boolean insert_sc = false; // Insert swizzle check (aaload
											// only)?

				final int opc = inst.opcodeClass();
				int depth;

				switch (opc) {
				case opcx_arraylength:
				case opcx_athrow:
				case opcx_getfield:
				case opcx_instanceof: {
					depth = 0;
					break;
				}
				case opcx_iaload:
				case opcx_laload:
				case opcx_faload:
				case opcx_daload:
				case opcx_baload:
				case opcx_caload:
				case opcx_saload: {
					depth = 1;
					break;
				}
				case opcx_aaload: {
					depth = 1;
					insert_sc = true;
					break;
				}
				case opcx_iastore:
				case opcx_fastore:
				case opcx_aastore:
				case opcx_bastore:
				case opcx_castore:
				case opcx_sastore: {
					depth = 2;
					break;
				}
				case opcx_lastore:
				case opcx_dastore: {
					depth = 3;
					break;
				}
				case opcx_putfield: {
					final MemberRef ref = (MemberRef) inst.operand();
					depth = ref.type().stackHeight();
					if (ref.type().isReference()) {
						uctype = Main.POINTER;
					} else {
						uctype = Main.SCALAR;
					}
					break;
				}
				case opcx_invokevirtual:
				case opcx_invokespecial:
				case opcx_invokeinterface: {
					final MemberRef ref = (MemberRef) inst.operand();
					depth = ref.type().stackHeight();
					break;
				}
				case opcx_rc: {
					// Skip any existing residency checks.
					iter.remove();
					continue INST;
				}
				case opcx_aupdate: {
					// Skip any existing update checks.
					iter.remove();
					continue INST;
				}
				case opcx_supdate: {
					// Skip any existing update checks.
					iter.remove();
					continue INST;
				}
				default: {
					continue INST;
				}
				}

				Instruction addInst;

				// Insert a residency check...
				if (Main.RC) {
					Object t;

					// //////////////////////////////////
					// Before...
					// +-----+------+-----------+
					// | ... | inst | afterInst |
					// +-----+------+-----------+
					// ^prev ^next
					//
					// After...
					// +-----+----+------+-----------+
					// | ... | RC | inst | afterInst |
					// +-----+----+------+-----------+
					// ^prev ^next
					// //////////////////////////////////

					// +-----+------+-----------+
					// | ... | inst | afterInst |
					// +-----+------+-----------+
					// ^prev ^next

					t = iter.previous();
					Assert.isTrue(t == inst, t + " != " + inst);

					// +-----+------+-----------+
					// | ... | inst | afterInst |
					// +-----+------+-----------+
					// ^prev ^next

					addInst = new Instruction(Opcode.opcx_rc,
							new Integer(depth));
					iter.add(addInst);

					// +-----+----+------+-----------+
					// | ... | RC | inst | afterInst |
					// +-----+----+------+-----------+
					// ^prev ^next

					t = iter.previous();
					Assert.isTrue(t == addInst, t + " != " + addInst);

					// +-----+----+------+-----------+
					// | ... | RC | inst | afterInst |
					// +-----+----+------+-----------+
					// ^prev ^next

					t = iter.next();
					Assert.isTrue(t == addInst, t + " != " + addInst);

					// +-----+----+------+-----------+
					// | ... | RC | inst | afterInst |
					// +-----+----+------+-----------+
					// ^prev ^next

					t = iter.next();
					Assert.isTrue(t == inst, t + " != " + inst);

					// +-----+----+------+-----------+
					// | ... | RC | inst | afterInst |
					// +-----+----+------+-----------+
					// ^prev ^next

					if (Main.VERBOSE > 2) {
						System.out.println("Inserting " + addInst + " before "
								+ inst);
					}
				} else {
					if (Main.VERBOSE > 2) {
						System.out.println("Not inserting rc before " + inst);
					}
				}

				// Insert a swizzle check...
				if (insert_sc) {
					if (Main.SC) {
						Object t;

						// ////////////////////////////////////////////
						// Before...
						// +-----+------+-----------+
						// | ... | inst | afterInst |
						// +-----+------+-----------+
						// ^prev ^next
						//
						// After...
						// +-----+------+----------+------+-----------+
						// | ... | dup2 | aswizzle | inst | afterInst |
						// +-----+------+----------+------+-----------+
						// ^prev ^next
						// /////////////////////////////////////////////

						// +-----+------+-----------+
						// | ... | inst | afterInst |
						// +-----+------+-----------+
						// ^prev ^next

						t = iter.previous();
						Assert.isTrue(t == inst, t + " != " + inst);

						// +-----+------+-----------+
						// | ... | inst | afterInst |
						// +-----+------+-----------+
						// ^prev ^next

						addInst = new Instruction(Opcode.opcx_dup2);
						iter.add(addInst);

						// +-----+------+------+-----------+
						// | ... | dup2 | inst | afterInst |
						// +-----+------+------+-----------+
						// ^prev ^next

						t = iter.previous();
						Assert.isTrue(t == addInst, t + " != " + addInst);

						// +-----+------+------+-----------+
						// | ... | dup2 | inst | afterInst |
						// +-----+------+------+-----------+
						// ^prev ^next

						t = iter.next();
						Assert.isTrue(t == addInst, t + " != " + addInst);

						// +-----+------+------+-----------+
						// | ... | dup2 | inst | afterInst |
						// +-----+------+------+-----------+
						// ^prev ^next

						addInst = new Instruction(Opcode.opcx_aswizzle);
						iter.add(addInst);

						// +-----+------+----------+------+-----------+
						// | ... | dup2 | aswizzle | inst | afterInst |
						// +-----+------+----------+------+-----------+
						// ^prev ^next

						t = iter.previous();
						Assert.isTrue(t == addInst, t + " != " + addInst);

						// +-----+------+----------+------+-----------+
						// | ... | dup2 | aswizzle | inst | afterInst |
						// +-----+------+----------+------+-----------+
						// ^prev ^next

						t = iter.next();
						Assert.isTrue(t == addInst, t + " != " + addInst);

						// +-----+------+----------+------+-----------+
						// | ... | dup2 | aswizzle | inst | afterInst |
						// +-----+------+----------+------+-----------+
						// ^prev ^next

						t = iter.next();
						Assert.isTrue(t == inst, t + " != " + inst);

						// +-----+------+----------+------+-----------+
						// | ... | dup2 | aswizzle | inst | afterInst |
						// +-----+------+----------+------+-----------+
						// ^prev ^next

						if (Main.VERBOSE > 2) {
							System.out
									.println("Inserting dup2,aswizzle before "
											+ inst);
						}
					}

					else {
						if (Main.VERBOSE > 2) {
							System.out.println("Not inserting aswizzle before "
									+ inst);
						}
					}
				}

				// Insert an update check...
				if (uctype != Main.NONE) {
					if (Main.UC) {
						Object t;

						// ////////////////////////////////////////////
						// Before...
						// +-----+------+-----------+
						// | ... | inst | afterInst |
						// +-----+------+-----------+
						// ^prev ^next
						//
						// After...
						// +-----+---------+------+-----------+
						// | ... | aupdate | inst | afterInst |
						// +-----+---------+------+-----------+
						// ^prev ^next
						// /////////////////////////////////////////////

						// +-----+------+-----------+
						// | ... | inst | afterInst |
						// +-----+------+-----------+
						// ^prev ^next

						t = iter.previous();
						Assert.isTrue(t == inst, t + " != " + inst);

						// +-----+------+-----------+
						// | ... | inst | afterInst |
						// +-----+------+-----------+
						// ^prev ^next

						addInst = new Instruction(Opcode.opcx_aupdate,
								new Integer(depth));
						/*
						 * if (uctype == POINTER) { addInst = new
						 * Instruction(opcx_aupdate, new Integer(depth)); } else {
						 * addInst = new Instruction(opcx_supdate, new
						 * Integer(depth)); }
						 */

						iter.add(addInst);

						// +-----+---------+------+-----------+
						// | ... | aupdate | inst | afterInst |
						// +-----+---------+------+-----------+
						// ^prev ^next

						t = iter.previous();
						Assert.isTrue(t == addInst, t + " != " + addInst);

						// +-----+---------+------+-----------+
						// | ... | aupdate | inst | afterInst |
						// +-----+---------+------+-----------+
						// ^prev ^next

						t = iter.next();
						Assert.isTrue(t == addInst, t + " != " + addInst);

						// +-----+---------+------+-----------+
						// | ... | aupdate | inst | afterInst |
						// +-----+---------+------+-----------+
						// ^prev ^next

						t = iter.next();
						Assert.isTrue(t == inst, t + " != " + inst);

						// +-----+---------+------+-----------+
						// | ... | aupdate | inst | afterInst |
						// +-----+---------+------+-----------+
						// ^prev ^next

						if (Main.VERBOSE > 2) {
							System.out.println("Inserting " + addInst
									+ " before " + inst);
						}
					} else if (Main.VERBOSE > 2) {
						System.out.println("Not inserting uc before " + inst);
					}
				}
			}
		}
	}
}
