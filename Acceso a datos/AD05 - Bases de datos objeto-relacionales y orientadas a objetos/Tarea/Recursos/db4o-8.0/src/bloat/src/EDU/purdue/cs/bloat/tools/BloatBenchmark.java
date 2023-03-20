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
package EDU.purdue.cs.bloat.tools;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.benchmark.*;
import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.codegen.*;
import EDU.purdue.cs.bloat.context.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.inline.*;
import EDU.purdue.cs.bloat.optimize.*;
import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.trans.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * This program is used to BLOAT Java programs. In particular, we use it to
 * BLOAT the programs used to benchmark BLOAT. This program is intended to be
 * run multiple times to generate multiple BLOATed programs.
 */
public class BloatBenchmark {
	public static boolean TRACE = false;

	private static boolean INLINE = false;

	private static boolean INTRA = false;

	private static boolean PEEPHOLE = false;

	private static boolean VERIFY = true;

	private static boolean SPECIALIZE = false;

	private static boolean SUN = false;

	private static boolean USE1_1 = false;

	private static boolean CHECK = true;

	private static boolean TIMES = false;

	private static final PrintWriter err = new PrintWriter(System.err, true);

	private static final Set CLASSES = new HashSet();

	private static String statsFile = null;

	private static String timesFile = null;

	private static int DEPTH = 2; // No. calls deep

	private static int SIZE = 1000; // Max size of methods

	private static int MORPH = -1; // Max "morphosity" of virtual calls

	private static int CALLEE_SIZE = -1;

	private static final List SKIP = new ArrayList(); // Classes that are
														// specifically not
														// optimized

	private static void tr(final String s) {
		if (BloatBenchmark.TRACE) {
			System.out.println(s);
		}
	}

	private static void usage() {
		BloatBenchmark.err
				.println("java TestSpecialize [options] classNames outputDir");
		BloatBenchmark.err.println("where [options] are:");
		BloatBenchmark.err
				.println("  -calleeSize size   Max method size to inline");
		BloatBenchmark.err
				.println("  -classpath path    Classpath is always prepended");
		BloatBenchmark.err.println("  -depth depth       Max inline depth");
		BloatBenchmark.err
				.println("  -inline            Inline calls to static methods");
		BloatBenchmark.err
				.println("  -intra             Intraprocedural BLOAT");
		BloatBenchmark.err
				.println("  -lookIn dir        Look for classes here");
		BloatBenchmark.err
				.println("  -morph morph       Max morphosity of call sites");
		BloatBenchmark.err.println("  -no-verify         Don't verify CFG");
		BloatBenchmark.err
				.println("  -no-opt-stack      Don't optimize stack usage");
		BloatBenchmark.err
				.println("  -no-stack-vars     Don't use stack vars in CFG");
		BloatBenchmark.err
				.println("  -no-stack-alloc    Don't try to push locals onto the operand stack");
		BloatBenchmark.err
				.println("  -peel-loops <n|all>"
						+ "\n                   Peel innermost loops to enable code hoisting"
						+ "\n                   (n >= 0 is the maximum loop level to peel)");
		BloatBenchmark.err
				.println("  -no-pre            Don't perform partial redundency elimination");
		BloatBenchmark.err
				.println("  -no-dce            Don't perform dead code elimination");
		BloatBenchmark.err
				.println("  -no-prop           Don't perform copy and constant propagation");
		BloatBenchmark.err
				.println("  -no-color          Don't do graph coloring");
		BloatBenchmark.err
				.println("  -peephole          Perform peephole after inter");
		BloatBenchmark.err.println("  -size size         Max method size");
		BloatBenchmark.err
				.println("  -specialize        Specialize virtual method calls");
		BloatBenchmark.err.println("  -stats statsFile   Generate stats");
		BloatBenchmark.err.println("  -sun               Include sun packages");
		BloatBenchmark.err.println("  -times timesFile   Print timings");
		BloatBenchmark.err
				.println("  -trace             Print trace information");
		BloatBenchmark.err
				.println("  -no-check          Don't check that my options 'make sense'");
		BloatBenchmark.err.println("  -skip <class|package.*>"
				+ "\n                   Skip the given class or package");
		BloatBenchmark.err.println("  -1.1               BLOAT for JDK1.1");
		BloatBenchmark.err.println("  -1.2               BLOAT for JDK1.2");
		BloatBenchmark.err.println("");
		System.exit(1);
	}

	public static void main(final String[] args) {
		String CLASSPATH = null;
		String outputDirName = null;
		String lookIn = null;

		// Parse the command line
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-trace")) {
				BloatBenchmark.TRACE = true;
				PersistentBloatContext.DB_COMMIT = true;

			} else if (args[i].equals("-calleeSize")) {
				if (++i >= args.length) {
					BloatBenchmark.err.println("** No callee size specified");
					BloatBenchmark.usage();
				}

				try {
					BloatBenchmark.CALLEE_SIZE = Integer.parseInt(args[i]);

				} catch (final NumberFormatException ex33) {
					BloatBenchmark.err.println("** Bad number: " + args[i]);
					BloatBenchmark.usage();
				}

			} else if (args[i].startsWith("-classpath")) {
				if (++i >= args.length) {
					BloatBenchmark.err.println("** No classpath specified");
					BloatBenchmark.usage();
				}

				// If there is more than one -classpath append it to the
				// current one. That way the CLASSPATH reflects the order in
				// which the options came on the command line.
				if (CLASSPATH == null) {
					CLASSPATH = args[i];

				} else {
					CLASSPATH += File.pathSeparator + args[i];
				}

			} else if (args[i].equals("-no-stack-alloc")) {
				Main.STACK_ALLOC = false;

			} else if (args[i].equals("-peel-loops")) {
				if (++i >= args.length) {
					BloatBenchmark.usage();
				}

				final String n = args[i];

				if (n.equals("all")) {
					FlowGraph.PEEL_LOOPS_LEVEL = FlowGraph.PEEL_ALL_LOOPS;

				} else {
					try {
						FlowGraph.PEEL_LOOPS_LEVEL = Integer.parseInt(n);

						if (FlowGraph.PEEL_LOOPS_LEVEL < 0) {
							BloatBenchmark.usage();
						}
					} catch (final NumberFormatException ex) {
						BloatBenchmark.usage();
					}
				}
			} else if (args[i].equals("-no-color")) {
				Liveness.UNIQUE = true;

			} else if (args[i].equals("-no-dce")) {
				Main.DCE = false;

			} else if (args[i].equals("-no-prop")) {
				Main.PROP = false;

			} else if (args[i].equals("-no-pre")) {
				Main.PRE = false;

			} else if (args[i].equals("-no-check")) {
				BloatBenchmark.CHECK = false;

			} else if (args[i].equals("-depth")) {
				if (++i >= args.length) {
					BloatBenchmark.err.println("** No depth specified");
					BloatBenchmark.usage();
				}

				try {
					BloatBenchmark.DEPTH = Integer.parseInt(args[i]);

				} catch (final NumberFormatException ex33) {
					BloatBenchmark.err.println("** Bad number: " + args[i]);
					BloatBenchmark.usage();
				}

			} else if (args[i].equals("-inline")) {
				// Inline calls to static methods
				BloatBenchmark.INLINE = true;

			} else if (args[i].equals("-intra")) {
				BloatBenchmark.INTRA = true;

			} else if (args[i].equals("-lookIn")) {
				if (++i >= args.length) {
					BloatBenchmark.err.println("** No directory specified");
					BloatBenchmark.usage();
				}

				if (lookIn != null) {
					lookIn += File.pathSeparator + args[i];

				} else {
					lookIn = args[i];
				}

			} else if (args[i].equals("-morph")) {
				if (++i >= args.length) {
					BloatBenchmark.err.println("** No morphosity specified");
					BloatBenchmark.usage();
				}

				try {
					BloatBenchmark.MORPH = Integer.parseInt(args[i]);

				} catch (final NumberFormatException ex33) {
					BloatBenchmark.err.println("** Bad number: " + args[i]);
					BloatBenchmark.usage();
				}

			} else if (args[i].equals("-noinline")) {
				// Don't perform inlining, just specialize
				BloatBenchmark.INLINE = false;

			} else if (args[i].equals("-peephole")) {
				// Perform peephole optimizations when doing interprocedural
				// stuff
				BloatBenchmark.PEEPHOLE = true;

			} else if (args[i].equals("-size")) {
				if (++i >= args.length) {
					BloatBenchmark.err.println("** No size specified");
					BloatBenchmark.usage();
				}

				try {
					BloatBenchmark.SIZE = Integer.parseInt(args[i]);

				} catch (final NumberFormatException ex33) {
					BloatBenchmark.err.println("** Bad number: " + args[i]);
					BloatBenchmark.usage();
				}

			} else if (args[i].equals("-specialize")) {
				// Specialize virtual method call sites
				BloatBenchmark.SPECIALIZE = true;

			} else if (args[i].equals("-stats")) {
				if (++i >= args.length) {
					BloatBenchmark.err.println("** No stats file specified");
					BloatBenchmark.usage();
				}

				BloatBenchmark.statsFile = args[i];

			} else if (args[i].equals("-sun")) {
				// Optimize sun packages
				BloatBenchmark.SUN = true;

			} else if (args[i].equals("-times")) {
				BloatBenchmark.TIMES = true;

				if (++i >= args.length) {
					BloatBenchmark.err.println("** No times file specified");
					BloatBenchmark.usage();
				}

				BloatBenchmark.timesFile = args[i];

			} else if (args[i].equals("-no-verify")) {
				BloatBenchmark.VERIFY = false;

			} else if (args[i].equals("-no-opt-stack")) {
				CodeGenerator.OPT_STACK = false;

			} else if (args[i].equals("-no-stack-vars")) {
				Tree.USE_STACK = false;

			} else if (args[i].equals("-skip")) {
				if (++i >= args.length) {
					BloatBenchmark.usage();
				}

				String pkg = args[i];

				// Account for class file name on command line
				if (pkg.endsWith(".class")) {
					pkg = pkg.substring(0, pkg.lastIndexOf('.'));
				}

				BloatBenchmark.SKIP.add(pkg.replace('.', '/'));

			} else if (args[i].equals("-1.1")) {
				// There are some classes that we don't want to be pre-live.
				// They don't exist in JDK1.1.
				BloatBenchmark.USE1_1 = true;
				CallGraph.USE1_2 = false;

			} else if (args[i].equals("-1.2")) {
				CallGraph.USE1_2 = true;

				if (lookIn != null) {
					lookIn += File.separator + "1.2";
				}

			} else if (args[i].startsWith("-")) {
				BloatBenchmark.err
						.println("** Unrecognized option: " + args[i]);
				BloatBenchmark.usage();

			} else if (i == args.length - 1) {
				outputDirName = args[i];

			} else {
				BloatBenchmark.CLASSES.add(args[i]);
			}
		}

		if (BloatBenchmark.CLASSES.isEmpty()) {
			BloatBenchmark.err.println("** No classes specified");
			BloatBenchmark.usage();
		}

		if (outputDirName == null) {
			BloatBenchmark.err.println("** No output directory specified");
			BloatBenchmark.usage();
		}

		// Make sure the options the user entered make sense
		if (BloatBenchmark.CHECK) {
			BloatBenchmark.checkOptions();
		}

		if (BloatBenchmark.USE1_1) {
			// Don't generate stats for 1.1
			BloatBenchmark.statsFile = null;
		}

		if (lookIn != null) {
			CLASSPATH = lookIn + File.pathSeparator + CLASSPATH;
		}

		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i] + " ");
		}
		BloatBenchmark.tr("BLOATing with command line: " + sb);

		BloatContext context = null;

		float systemStart = 0.0F;
		float systemDelta = 0.0F;
		float systemEnd = 0.0F;
		float systemTotal = 0.0F;

		float userStart = 0.0F;
		float userDelta = 0.0F;
		float userEnd = 0.0F;
		float userTotal = 0.0F;

		PrintWriter times = null;

		if (BloatBenchmark.TIMES) {
			try {
				times = new PrintWriter(
						new FileWriter(BloatBenchmark.timesFile), true);

			} catch (final IOException ex) {
				times = new PrintWriter(System.out, true);
			}
		}

		if (BloatBenchmark.INTRA) {
			BloatBenchmark.tr("Intraprocedural BLOAT");

			// First compute the roots of the call graph. Figure out which
			// methods are live.
			context = BloatBenchmark.makeContext(CLASSPATH, null);
			final Collection liveMethods = BloatBenchmark.liveMethods(
					BloatBenchmark.CLASSES, context);

			// Run intraprocedural BLOAT on the live methods.
			BloatBenchmark.tr(liveMethods.size() + " live methods");
			context = BloatBenchmark.makeContext(CLASSPATH, outputDirName);
			BloatBenchmark.intraBloat(liveMethods, context);

		} else {
			BloatBenchmark.tr("Interprocedural BLOAT");

			if (BloatBenchmark.TIMES) {
				Times.snapshot();
				systemStart = Times.systemTime();
				userStart = Times.userTime();
			}

			// Do the interprocedural BLOATing
			context = BloatBenchmark.makeContext(CLASSPATH, outputDirName);
			BloatBenchmark.liveMethods(BloatBenchmark.CLASSES, context);

			if (BloatBenchmark.TIMES) {
				// Take a measurement
				Times.snapshot();

				systemEnd = Times.systemTime();
				userEnd = Times.userTime();

				systemDelta = systemEnd - systemStart;
				userDelta = userEnd - userStart;

				systemStart = systemEnd;
				userStart = userEnd;

				systemTotal += systemDelta;
				userTotal += userDelta;

				times.println("Call graph construction");
				times.println("  User: " + userDelta);
				times.println("  System: " + systemDelta);
			}

			if (BloatBenchmark.SPECIALIZE) {
				BloatBenchmark.specialize(context);
			}

			if (BloatBenchmark.TIMES) {
				// Take a measurement
				Times.snapshot();

				systemEnd = Times.systemTime();
				userEnd = Times.userTime();

				systemDelta = systemEnd - systemStart;
				userDelta = userEnd - userStart;

				systemStart = systemEnd;
				userStart = userEnd;

				systemTotal += systemDelta;
				userTotal += userDelta;

				times.println("Call site specialization");
				times.println("  User: " + userDelta);
				times.println("  System: " + systemDelta);
			}

			if (BloatBenchmark.INLINE) {
				BloatBenchmark.inline(context);
			}

			if (BloatBenchmark.TIMES) {
				// Take a measurement
				Times.snapshot();

				systemEnd = Times.systemTime();
				userEnd = Times.userTime();

				systemDelta = systemEnd - systemStart;
				userDelta = userEnd - userStart;

				systemStart = systemEnd;
				userStart = userEnd;

				systemTotal += systemDelta;
				userTotal += userDelta;

				times.println("Method inlining");
				times.println("  User: " + userDelta);
				times.println("  System: " + systemDelta);
			}

			if (BloatBenchmark.PEEPHOLE) {
				BloatBenchmark.peephole(context);
			}
		}

		// Commit dirty data
		BloatBenchmark.tr("Committing dirty methods");
		context.commitDirty();

		if (BloatBenchmark.TIMES) {
			// Take a measurement
			Times.snapshot();

			systemEnd = Times.systemTime();
			userEnd = Times.userTime();

			systemDelta = systemEnd - systemStart;
			userDelta = userEnd - userStart;

			systemStart = systemEnd;
			userStart = userEnd;

			systemTotal += systemDelta;
			userTotal += userDelta;

			times.println("Committal");
			times.println("  User: " + userDelta);
			times.println("  System: " + systemDelta);
		}

		if (BloatBenchmark.TIMES) {
			times.println("Total");
			times.println("  User: " + userTotal);
			times.println("  System: " + systemTotal);
		}

		if (BloatBenchmark.statsFile != null) {
			final InlineStats stats = context.getInlineStats();
			PrintWriter statsOut = null;
			try {
				statsOut = new PrintWriter(new FileWriter(
						BloatBenchmark.statsFile), true);

			} catch (final IOException ex) {
				statsOut = new PrintWriter(System.out, true);
			}

			stats.printSummary(statsOut);
		}

		BloatBenchmark.tr("Finished");
	}

	/**
	 * Creates a <tt>BloatContext</tt> that loads classes from a given
	 * CLASSPATH.
	 */
	static BloatContext makeContext(final String classpath,
			final String outputDirName) {
		final ClassFileLoader loader = new ClassFileLoader();
		if (classpath != null) {
			loader.prependClassPath(classpath);
		}

		// if(TRACE) {
		// loader.setVerbose(true);
		// }

		BloatBenchmark.tr("  Creating a BloatContext for CLASSPATH: "
				+ loader.getClassPath());

		if (outputDirName != null) {
			loader.setOutputDir(new File(outputDirName));
		}
		final BloatContext context = new CachingBloatContext(loader,
				BloatBenchmark.CLASSES, true);

		// Always ignore the sun packages and the opj stuff for
		// interprocedural stuff
		if (!BloatBenchmark.SUN) {
			context.addIgnorePackage("sun");
		}

		context.addIgnorePackage("java.lang.ref");
		context.addIgnorePackage("org.opj.system");

		if (BloatBenchmark.USE1_1) {
			// Toba can't deal with java.lang.Character
			context.addIgnoreClass(Type.getType("Ljava/lang/Character;"));
		}

		return (context);
	}

	/**
	 * Returns the live methods of a program whose root methods are the
	 * <tt>main</tt> method of a set of classes.
	 * 
	 * @param classes
	 *            Names of classes containing root methods
	 * @param context
	 *            Repository for accessing BLOAT stuff
	 * @return The <tt>MemberRef</tt>s of the live methods
	 */
	private static Collection liveMethods(final Collection classes,
			final BloatContext context) {

		// Determine the roots of the call graph
		final Set roots = new HashSet();
		Iterator iter = classes.iterator();
		while (iter.hasNext()) {
			final String className = (String) iter.next();
			try {
				final ClassEditor ce = context.editClass(className);
				final MethodInfo[] methods = ce.methods();

				for (int i = 0; i < methods.length; i++) {
					final MethodEditor me = context.editMethod(methods[i]);

					if (!me.name().equals("main")) {
						continue;
					}

					BloatBenchmark.tr("  Root " + ce.name() + "." + me.name()
							+ me.type());
					roots.add(me.memberRef());
				}

			} catch (final ClassNotFoundException ex1) {
				BloatBenchmark.err.println("** Could not find class: "
						+ ex1.getMessage());
				System.exit(1);
			}
		}

		if (roots.isEmpty()) {
			BloatBenchmark.err.print("** No main method found in classes: ");
			iter = classes.iterator();
			while (iter.hasNext()) {
				final String name = (String) iter.next();
				BloatBenchmark.err.print(name);
				if (iter.hasNext()) {
					BloatBenchmark.err.print(", ");
				}
			}
			BloatBenchmark.err.println("");
		}

		context.setRootMethods(roots);
		final CallGraph cg = context.getCallGraph();

		final Set liveMethods = new TreeSet(new MemberRefComparator());
		liveMethods.addAll(cg.liveMethods());

		return (liveMethods);
	}

	/**
	 * Specializes the live methods in a program.
	 */
	private static void specialize(final BloatContext context) {

		final CallGraph cg = context.getCallGraph();

		final Set liveMethods = new TreeSet(new MemberRefComparator());
		liveMethods.addAll(cg.liveMethods());

		// Specialize all possible methods
		final InlineStats stats = context.getInlineStats();

		if (BloatBenchmark.statsFile != null) {
			Specialize.STATS = true;
			stats.setConfigName("BloatBenchmark");
		}

		if (BloatBenchmark.MORPH != -1) {
			Specialize.MAX_MORPH = BloatBenchmark.MORPH;
		}
		final Specialize spec = new Specialize(context);

		if (Specialize.STATS) {
			stats.noteLiveMethods(liveMethods.size());
			stats.noteLiveClasses(cg.liveClasses().size());
		}

		BloatBenchmark.tr("Specializing live methods");
		final Iterator iter = liveMethods.iterator();

		for (int count = 0; iter.hasNext(); count++) {
			try {
				final MethodEditor live = context.editMethod((MemberRef) iter
						.next());

				if (context.ignoreMethod(live.memberRef())) {
					// Don't display ignored methods, it's misleading.
					continue;
				}

				BloatBenchmark.tr("  " + count + ") "
						+ live.declaringClass().name() + "." + live.name()
						+ live.type());

				spec.specialize(live);

			} catch (final NoSuchMethodException ex2) {
				BloatBenchmark.err.println("** Could not find method "
						+ ex2.getMessage());
				System.exit(1);
			}
		}
	}

	/**
	 * Inlines calls to static methods in the live methods of a given program.
	 */
	private static void inline(final BloatContext context) {

		final Set liveMethods = new TreeSet(new MemberRefComparator());
		final CallGraph cg = context.getCallGraph();
		liveMethods.addAll(cg.liveMethods());

		BloatBenchmark.tr("Inlining " + liveMethods.size() + " live methods");

		if (BloatBenchmark.CALLEE_SIZE != -1) {
			Inline.CALLEE_SIZE = BloatBenchmark.CALLEE_SIZE;
		}

		final Iterator iter = liveMethods.iterator();
		for (int count = 0; BloatBenchmark.INLINE && iter.hasNext(); count++) {
			try {
				final MethodEditor live = context.editMethod((MemberRef) iter
						.next());

				if (context.ignoreMethod(live.memberRef())) {
					// Don't display ignored methods, it's misleading.
					continue;
				}

				BloatBenchmark.tr("  " + count + ") "
						+ live.declaringClass().name() + "." + live.name()
						+ live.type());

				final Inline inline = new Inline(context, BloatBenchmark.SIZE);
				inline.setMaxCallDepth(BloatBenchmark.DEPTH);
				inline.inline(live);

				// Commit here in an attempt to conserve memory
				context.commit(live.methodInfo());
				context.release(live.methodInfo());

			} catch (final NoSuchMethodException ex3) {
				BloatBenchmark.err.println("** Could not find method "
						+ ex3.getMessage());
				System.exit(1);
			}
		}
	}

	/**
	 * Performs peephole optimizations on a program's live methods.
	 */
	private static void peephole(final BloatContext context) {

		final Set liveMethods = new TreeSet(new MemberRefComparator());
		final CallGraph cg = context.getCallGraph();
		liveMethods.addAll(cg.liveMethods());

		// Perform peephole optimizations. We do this separately because
		// some peephole optimizations do things to the stack that
		// inlining doesn't like. For instance, a peephole optimizations
		// might make it so that a method has a non-empty stack upon
		// return. Inlining will barf at the sight of this.
		BloatBenchmark.tr("Performing peephole optimizations");

		final Iterator iter = liveMethods.iterator();
		while (BloatBenchmark.PEEPHOLE && iter.hasNext()) {
			try {
				final MethodEditor live = context.editMethod((MemberRef) iter
						.next());
				Peephole.transform(live);
				context.commit(live.methodInfo());
				context.release(live.methodInfo());

			} catch (final NoSuchMethodException ex314) {
				BloatBenchmark.err.println("** Could not find method "
						+ ex314.getMessage());
				ex314.printStackTrace(System.err);
				System.exit(1);
			}
		}
	}

	/**
	 * Performs intraprocedural BLOAT on a program's live methods.
	 * 
	 * @param liveMethods
	 *            Should be alphabetized. This way we can commit a class once
	 *            we've BLOATed all of its methods.
	 */
	private static void intraBloat(final Collection liveMethods,
			final BloatContext context) {

		ClassEditor prevClass = null;
		final Iterator iter = liveMethods.iterator();
		for (int count = 0; iter.hasNext(); count++) {
			MethodEditor live = null;
			ClassEditor ce = null; // Hack to make sure commit happens
			try {
				live = context.editMethod((MemberRef) iter.next());
				ce = context.editClass(live.declaringClass().classInfo());

			} catch (final NoSuchMethodException ex3) {
				BloatBenchmark.err.println("** Could not find method "
						+ ex3.getMessage());
				System.exit(1);
			}

			/* So we can skip classes or packages */
			final String name = ce.type().className();
			final String qual = ce.type().qualifier() + "/*";
			boolean skip = false;
			for (int i = 0; i < BloatBenchmark.SKIP.size(); i++) {
				final String pkg = (String) BloatBenchmark.SKIP.get(i);

				if (name.equals(pkg) || qual.equals(pkg)) {
					skip = true;
					break;
				}
			}

			if (context.ignoreMethod(live.memberRef()) || skip) {
				// Don't display ignored methods, it's misleading.
				context.release(live.methodInfo());
				continue;
			}

			final Runtime runtime = Runtime.getRuntime();
			runtime.gc();

			final Date start = new Date();
			BloatBenchmark.tr("  " + count + ") "
					+ live.declaringClass().name() + "." + live.name()
					+ live.type());
			BloatBenchmark.tr("    Start: " + start);

			try {
				EDU.purdue.cs.bloat.optimize.Main.TRACE = BloatBenchmark.TRACE;
				if (!BloatBenchmark.VERIFY) {
					EDU.purdue.cs.bloat.optimize.Main.VERIFY = false;
				}
				EDU.purdue.cs.bloat.optimize.Main.bloatMethod(live, context);

			} catch (final Exception oops) {
				BloatBenchmark.err
						.println("******************************************");
				BloatBenchmark.err.println("Exception while BLOATing "
						+ live.declaringClass().name() + "." + live.name()
						+ live.type());
				BloatBenchmark.err.println(oops.getMessage());
				oops.printStackTrace(System.err);
				BloatBenchmark.err
						.println("******************************************");
			}

			// Commit here in an attempt to conserve memory
			context.commit(live.methodInfo());
			context.release(live.methodInfo());

			if (prevClass == null) {
				prevClass = ce;

			} else if (!prevClass.equals(ce)) {
				// We've finished BLOATed the methods for prevClass, commit
				// prevClass and move on
				BloatBenchmark.tr(prevClass.type() + " != " + ce.type());
				context.commit(prevClass.classInfo());
				context.release(prevClass.classInfo());
				// context.commitDirty();
				// tr(context.toString());
				prevClass = ce;

			} else {
				context.release(ce.classInfo());
			}

			final Date end = new Date();
			BloatBenchmark.tr("    Ellapsed time: "
					+ (end.getTime() - start.getTime()) + " ms");
		}

		context.commitDirty();
	}

	/**
	 * Checks to make sure that the chosen options make sense.
	 */
	private static void checkOptions() {
		if (!BloatBenchmark.INTRA && !BloatBenchmark.SPECIALIZE
				&& !BloatBenchmark.INLINE) {
			BloatBenchmark.err.println("** There is nothing to do!");
			BloatBenchmark.usage();

		} else if ((BloatBenchmark.MORPH != -1) && !BloatBenchmark.SPECIALIZE) {
			BloatBenchmark.err
					.println("** Must specialize when setting morphosity");
			BloatBenchmark.usage();
		}
	}

	private static class MemberRefComparator implements Comparator {
		public int compare(final Object o1, final Object o2) {
			Assert.isTrue(o1 instanceof MemberRef, o1 + " is not a MemberRef!");
			Assert.isTrue(o2 instanceof MemberRef, o2 + " is not a MemberRef!");

			final MemberRef me1 = (MemberRef) o1;
			final MemberRef me2 = (MemberRef) o2;

			final String s1 = me1.declaringClass() + "." + me1.name()
					+ me1.type();
			final String s2 = me2.declaringClass() + "." + me2.name()
					+ me2.type();

			return (s1.compareTo(s2));
		}

		public boolean equals(final Object other) {
			return (true);
		}
	}
}
