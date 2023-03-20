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
package EDU.purdue.cs.bloat.diva;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.codegen.*;
import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.ssa.*;
import EDU.purdue.cs.bloat.tbaa.*;
import EDU.purdue.cs.bloat.trans.*;
import EDU.purdue.cs.bloat.tree.*;

/**
 * Performs a number of analyses on the methods of some specified classes.
 * However, it does not perform some of the optimizations that optimize.Main
 * does.
 * 
 * @see EDU.purdue.cs.bloat.optimize.Main
 */
public class Main {
	static boolean DEBUG = false;

	static boolean VERBOSE = false;

	static boolean FORCE = false;

	static boolean CLOSURE = false;

	static boolean PRE = true;

	static boolean DCE = true;

	static boolean PROP = true;

	static boolean FOLD = true;

	static boolean STACK_ALLOC = false;

	static boolean COMPACT_ARRAY_INIT = true;

	static boolean ANNO = true;

	static String[] ARGS = null;

	static List SKIP = new ArrayList();

	static List ONLY = new ArrayList();

	static String METHOD = null;

	static BloatContext context = null;

	static ClassFileLoader loader = null;

	public static void main(final String[] args) {
		try {
			Main.loader = new ClassFileLoader();

			List classes = new ArrayList(args.length);
			boolean gotdir = false;

			Main.ARGS = args;

			for (int i = 0; i < args.length; i++) {
				if (args[i].equals("-v") || args[i].equals("-verbose")) {
					Main.VERBOSE = true;
					Main.loader.setVerbose(true);
				} else if (args[i].equals("-debug")) {
					Main.DEBUG = true;
					Main.loader.setVerbose(true);
					ClassFileLoader.DEBUG = true;
					CompactArrayInitializer.DEBUG = true;
					ClassEditor.DEBUG = true;
					FlowGraph.DEBUG = true;
					DominatorTree.DEBUG = true;
					Tree.DEBUG = true;
					CodeGenerator.DEBUG = true;
					Liveness.DEBUG = true;
					SSA.DEBUG = true;
					SSAGraph.DEBUG = true;
					PersistentCheckElimination.DEBUG = true;
					ValueNumbering.DEBUG = true;
					ValueFolding.DEBUG = true;
					ClassHierarchy.DEBUG = true;
					TypeInference.DEBUG = true;
					SSAPRE.DEBUG = true;
					StackPRE.DEBUG = true;
					ExprPropagation.DEBUG = true;
					DeadCodeElimination.DEBUG = true;
				} else if (args[i].equals("-help")) {
					Main.usage();
				} else if (args[i].equals("-noanno")) {
					Main.ANNO = false;
				} else if (args[i].equals("-anno")) {
					Main.ANNO = true;
				} else if (args[i].equals("-preserve-debug")) {
					MethodEditor.PRESERVE_DEBUG = true;
				} else if (args[i].equals("-nouse-stack-vars")) {
					Tree.USE_STACK = false;
				} else if (args[i].equals("-use-stack-vars")) {
					Tree.USE_STACK = true;
				} else if (args[i].equals("-nocompact-array-init")) {
					Main.COMPACT_ARRAY_INIT = false;
				} else if (args[i].equals("-compact-array-init")) {
					Main.COMPACT_ARRAY_INIT = true;
				} else if (args[i].equals("-nostack-alloc")) {
					Main.STACK_ALLOC = false;
				} else if (args[i].equals("-stack-alloc")) {
					Main.STACK_ALLOC = true;
				} else if (args[i].equals("-peel-loops")) {
					if (++i >= args.length) {
						Main.usage();
					}

					final String n = args[i];

					if (n.equals("all")) {
						FlowGraph.PEEL_LOOPS_LEVEL = FlowGraph.PEEL_ALL_LOOPS;
					} else {
						try {
							FlowGraph.PEEL_LOOPS_LEVEL = Integer.parseInt(n);

							if (FlowGraph.PEEL_LOOPS_LEVEL < 0) {
								Main.usage();
							}
						} catch (final NumberFormatException ex) {
							Main.usage();
						}
					}
				} else if (args[i].equals("-color")) {
					Liveness.UNIQUE = false;
				} else if (args[i].equals("-nocolor")) {
					Liveness.UNIQUE = true;
				} else if (args[i].equals("-only-method")) {
					if (++i >= args.length) {
						Main.usage();
					}

					Main.METHOD = args[i];
				} else if (args[i].equals("-print-flow-graph")) {
					FlowGraph.PRINT_GRAPH = true;
				} else if (args[i].equals("-classpath")) {
					if (++i >= args.length) {
						Main.usage();
					}

					final String classpath = args[i];
					Main.loader.setClassPath(classpath);
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
				} else if (args[i].equals("-nodce")) {
					Main.DCE = false;
				} else if (args[i].equals("-noprop")) {
					Main.PROP = false;
				} else if (args[i].equals("-nopre")) {
					Main.PRE = false;
				} else if (args[i].equals("-dce")) {
					Main.DCE = true;
				} else if (args[i].equals("-prop")) {
					Main.PROP = true;
				} else if (args[i].equals("-pre")) {
					Main.PRE = true;
				} else if (args[i].equals("-closure")) {
					Main.CLOSURE = true;
				} else if (args[i].equals("-relax-loading")) {
					ClassHierarchy.RELAX = true;
				} else if (args[i].equals("-f")) {
					Main.FORCE = true;
				} else if (args[i].startsWith("-")) {
					Main.usage();
				} else if (i == args.length - 1) {
					final File f = new File(args[i]);

					if (f.exists() && !f.isDirectory()) {
						System.err.println("No such directory: " + f.getPath());
						System.exit(2);
					}

					if (!f.exists()) {
						f.mkdirs();
					}

					if (!f.exists()) {
						System.err.println("Couldn't create directory: "
								+ f.getPath());
						System.exit(2);
					}

					Main.loader.setOutputDir(f);
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

			boolean errors = false;

			final Iterator iter = classes.iterator();

			while (iter.hasNext()) {
				final String name = (String) iter.next();

				try {
					Main.loader.loadClass(name);
				} catch (final ClassNotFoundException ex) {
					System.err.println("Couldn't find class: "
							+ ex.getMessage());
					errors = true;
				}
			}

			if (errors) {
				System.exit(1);
			}

			Main.context = new CachingBloatContext(Main.loader, classes,
					Main.CLOSURE);

			if (!Main.CLOSURE) {
				final Iterator e = classes.iterator();

				while (e.hasNext()) {
					final String name = (String) e.next();
					Main.editClass(name);
				}
			} else {
				classes = null;

				final Iterator e = Main.context.getHierarchy().classes()
						.iterator();

				while (e.hasNext()) {
					final Type t = (Type) e.next();

					if (t.isObject()) {
						Main.editClass(t.className());
					}
				}
			}
		} catch (final ExceptionInInitializerError ex) {
			ex.printStackTrace();
			System.out.println(ex.getException());
		}
	}

	private static void usage() {
		System.err
				.println("Usage: java EDU.purdue.cs.bloat.optimize.Main"
						+ "\n            [-options] classes dir"
						+ "\n"
						+ "\nwhere options include:"
						+ "\n    -help             print out this message"
						+ "\n    -v -verbose       turn on verbose mode"
						+ "\n    -debug            display a hideous amount of debug info"
						+ "\n    -classpath <directories separated by colons>"
						+ "\n                      list directories in which to look for classes"
						+ "\n    -f                optimize files even if up-to-date"
						+ "\n    -closure          recursively optimize referenced classes"
						+ "\n    -relax-loading    don't report errors if a class is not found"
						+ "\n    -skip <class|package.*>"
						+ "\n                      skip the given class or package"
						+ "\n    -only <class|package.*>"
						+ "\n                      skip all but the given class or package"
						+ "\n    -preserve-debug   try to preserve debug information"
						+ "\n    -[no]anno         insert an annotation in the contant pool"
						+ "\n    -[no]stack-alloc  try to push locals onto the operand stack"
						+ "\n    -peel-loops <n|all>"
						+ "\n                      peel innermost loops to enable code hoisting"
						+ "\n                      (n >= 0 is the maximum loop level to peel)"
						+ "\n    -[no]pre          perform partial redundency elimination"
						+ "\n    -[no]dce          perform dead code elimination"
						+ "\n    -[no]prop         perform copy and constant propagation"
						+ "");
		System.exit(0);
	}

	private static void editClass(final String className) {
		ClassFile classFile;

		try {
			classFile = (ClassFile) Main.loader.loadClass(className);
		} catch (final ClassNotFoundException ex) {
			System.err.println("Couldn't find class: " + ex.getMessage());
			return;
		}

		if (!Main.FORCE) {
			final File source = classFile.file();
			final File target = classFile.outputFile();

			if ((source != null) && (target != null) && source.exists()
					&& target.exists()
					&& (source.lastModified() < target.lastModified())) {

				if (Main.VERBOSE) {
					System.out.println(classFile.name() + " is up to date");
				}

				return;
			}
		}

		if (Main.DEBUG) {
			classFile.print(System.out);
		}

		final ClassEditor c = Main.context.editClass(classFile);

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
			if (Main.VERBOSE) {
				System.out.println("Skipping " + c.type().className());
			}

			Main.context.release(classFile);
			return;
		}

		if (Main.VERBOSE) {
			System.out.println("Optimizing " + c.type().className());
		}

		final MethodInfo[] methods = c.methods();

		for (int j = 0; j < methods.length; j++) {
			final MethodEditor m;

			try {
				m = Main.context.editMethod(methods[j]);
			} catch (final ClassFormatException ex) {
				System.err.println(ex.getMessage());
				continue;
			}

			if ((Main.METHOD != null) && !m.name().equals(Main.METHOD)) {
				Main.context.release(methods[j]);
				continue;
			}

			if (Main.DEBUG) {
				m.print(System.out);
			}

			if (m.isNative() || m.isAbstract()) {
				Main.context.release(methods[j]);
				continue;
			}

			if (Main.COMPACT_ARRAY_INIT) {
				CompactArrayInitializer.transform(m);

				if (Main.DEBUG) {
					System.out.println("---------- After compaction:");
					m.print(System.out);
					System.out.println("---------- end print");
				}
			}

			FlowGraph cfg;

			try {
				cfg = new FlowGraph(m);
			} catch (final ClassFormatException ex) {
				System.err.println(ex.getMessage());
				Main.context.release(methods[j]);
				continue;
			}

			SSA.transform(cfg);

			if (FlowGraph.DEBUG) {
				System.out.println("---------- After SSA:");
				cfg.print(System.out);
				System.out.println("---------- end print");
			}

			if (Main.DEBUG) {
				cfg.visit(new VerifyCFG(false));
			}

			// Do copy propagation first to get rid of all the extra copies
			// inserted for dups. If they're left it, it really slows down
			// value numbering.
			if (Main.PROP) {
				if (Main.DEBUG) {
					System.out.println("-------Before Copy Propagation-------");
				}

				final ExprPropagation copy = new ExprPropagation(cfg);
				copy.transform();

				if (Main.DEBUG) {
					cfg.visit(new VerifyCFG(false));
				}

				if (Main.DEBUG) {
					System.out.println("--------After Copy Propagation-------");
					cfg.print(System.out);
				}
			}

			if (Main.DCE) {
				if (Main.DEBUG) {
					System.out.println("-----Before Dead Code Elimination----");
				}

				final DeadCodeElimination dce = new DeadCodeElimination(cfg);
				dce.transform();

				if (Main.DEBUG) {
					cfg.visit(new VerifyCFG(false));
				}

				if (Main.DEBUG) {
					System.out.println("-----After Dead Code Elimination-----");
					cfg.print(System.out);
				}
			}

			if (Main.DEBUG) {
				System.out.println("---------Doing type inference--------");
			}

			TypeInference.transform(cfg, Main.context.getHierarchy());

			if (Main.DEBUG) {
				System.out.println("--------Doing value numbering--------");
			}

			(new ValueNumbering()).transform(cfg);

			if (Main.FOLD) {
				if (Main.DEBUG) {
					System.out.println("--------Before Value Folding---------");
				}

				(new ValueFolding()).transform(cfg);

				if (Main.DEBUG) {
					cfg.visit(new VerifyCFG());
				}

				if (Main.DEBUG) {
					System.out.println("---------After Value Folding---------");
					cfg.print(System.out);
				}
			}

			if (Main.PRE) {
				if (Main.DEBUG) {
					System.out.println("-------------Before SSAPRE-----------");
				}

				final SSAPRE pre = new SSAPRE(cfg, Main.context);
				pre.transform();

				if (Main.DEBUG) {
					cfg.visit(new VerifyCFG());
				}

				if (Main.DEBUG) {
					System.out.println("-------------After SSAPRE------------");
					cfg.print(System.out);
				}
			}

			if (Main.FOLD) {
				if (Main.DEBUG) {
					System.out.println("--------Before Value Folding---------");
				}

				(new ValueFolding()).transform(cfg);

				if (Main.DEBUG) {
					cfg.visit(new VerifyCFG());
				}

				if (Main.DEBUG) {
					System.out.println("---------After Value Folding---------");
					cfg.print(System.out);
				}
			}

			if (Main.PROP) {
				if (Main.DEBUG) {
					System.out.println("-------Before Copy Propagation-------");
				}

				final ExprPropagation copy = new ExprPropagation(cfg);
				copy.transform();

				if (Main.DEBUG) {
					cfg.visit(new VerifyCFG());
				}

				if (Main.DEBUG) {
					System.out.println("--------After Copy Propagation-------");
					cfg.print(System.out);
				}
			}

			if (Main.DCE) {
				if (Main.DEBUG) {
					System.out.println("-----Before Dead Code Elimination----");
				}

				final DeadCodeElimination dce = new DeadCodeElimination(cfg);
				dce.transform();

				if (Main.DEBUG) {
					cfg.visit(new VerifyCFG());
				}

				if (Main.DEBUG) {
					System.out.println("-----After Dead Code Elimination-----");
					cfg.print(System.out);
				}
			}

			(new PersistentCheckElimination()).transform(cfg);
			(new InductionVarAnalyzer()).transform(cfg);

			/*
			 * if (STACK_ALLOC) { if (DEBUG) {
			 * System.out.println("------------Before StackPRE----------"); }
			 * 
			 * StackPRE pre = new StackPRE(cfg); pre.transform();
			 * 
			 * if (DEBUG) { cfg.visit(new VerifyCFG()); }
			 * 
			 * if (DEBUG) { System.out.println("------------After
			 * StackPRE-----------"); cfg.print(System.out); } }
			 */

			cfg.commit();

			Peephole.transform(m);

			Main.context.commit(methods[j]);
		}

		if (Main.ANNO) {
			String s = "Optimized with: EDU.purdue.cs.bloat.diva.Main";

			for (int i = 0; i < Main.ARGS.length; i++) {
				if ((Main.ARGS[i].indexOf(' ') >= 0)
						|| (Main.ARGS[i].indexOf('\t') >= 0)
						|| (Main.ARGS[i].indexOf('\r') >= 0)
						|| (Main.ARGS[i].indexOf('\n') >= 0)) {
					s += " '" + Main.ARGS[i] + "'";
				} else {
					s += " " + Main.ARGS[i];
				}
			}

			System.out.println(s);
			// c.constants().addConstant(Constant.UTF8, s);
		}

		Main.context.commit(classFile);
	}
}
