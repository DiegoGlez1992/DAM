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
import EDU.purdue.cs.bloat.file.*;
import EDU.purdue.cs.bloat.inline.*;
import EDU.purdue.cs.bloat.reflect.*;

/**
 * This abstract class is a central repository for all things that are necessary
 * for a BLOATing sessions. Its subclasses implement certain schemes for
 * managing BLOAT data structures such as editors and control flow graphs.
 */
public abstract class BloatContext implements InlineContext {
	public static boolean DEBUG = Boolean.getBoolean("BloatContext.DEBUG");

	protected InlineStats inlineStats;

	// Ignore stuff for inlining
	protected Set ignorePackages = new HashSet();

	protected Set ignoreClasses = new HashSet();

	protected Set ignoreMethods = new HashSet();

	protected Set ignoreFields = new HashSet();

	protected boolean ignoreSystem = false;

	protected CallGraph callGraph;

	protected Set roots; // Root methods of call graph

	protected static void db(final String s) {
		if (BloatContext.DEBUG) {
			System.out.println(s);
		}
	}

	protected ClassInfoLoader loader;

	/**
	 * Constructor. Each <tt>BloatContext</tt> needs to know about a
	 * <tt>ClassInfoLoader</tt>.
	 */
	public BloatContext(final ClassInfoLoader loader) {
		this.loader = loader;
	}

	private static ClassLoader systemCL;
	static {
		final String s = "";
		BloatContext.systemCL = s.getClass().getClassLoader();
	}

	/**
	 * Returns <tt>true</tt> if the give type is a system class (that is, has
	 * the same class loader as java.lang.String).
	 */
	public static boolean isSystem(final Type type) {
		Class c = null;
		try {
			c = Class.forName(type.className().replace('/', '.'));

		} catch (final ClassNotFoundException ex) {
			System.err.println("** Could not find class " + type.className());
			ex.printStackTrace(System.err);
			System.exit(1);
		}

		// Have to use == because class loader might be null
		return (c.getClassLoader() == BloatContext.systemCL);
	}

	public void setRootMethods(final Set roots) {
		if (this.callGraph != null) {
			// Can't set the call graph roots after its been created
			throw new IllegalStateException("Cannot set roots after "
					+ "call graph has been created");
		}

		this.roots = roots;
	}

	public CallGraph getCallGraph() {
		if (this.callGraph == null) {
			// Create a new CallGraph
			this.callGraph = new CallGraph(this, this.roots);
		}
		return (this.callGraph);
	}

	public InlineStats getInlineStats() {
		if (inlineStats == null) {
			inlineStats = new InlineStats();
		}
		return (inlineStats);
	}

	public void addIgnorePackage(String name) {
		name = name.replace('.', '/');
		ignorePackages.add(name);
	}

	public void addIgnoreClass(final Type type) {
		ignoreClasses.add(type);
	}

	public void addIgnoreMethod(final MemberRef method) {
		ignoreMethods.add(method);
	}

	public void addIgnoreField(final MemberRef field) {
		ignoreFields.add(field);
	}

	public void setIgnoreSystem(final boolean ignore) {
		this.ignoreSystem = ignore;
	}

	public boolean ignoreClass(final Type type) {
		// First, check to see if we explicitly ignore it. If not, check
		// to see if we ignore its package. The ladies always seem to
		// ignore my package.
		if (ignoreClasses.contains(type)) {
			return (true);

		} else if (type.isPrimitive()) {
			addIgnoreClass(type);
			return (true);

		} else {
			if (this.ignoreSystem) {
				if (BloatContext.isSystem(type)) {
					addIgnoreClass(type);
					return (true);
				}
			}

			String packageName = type.className();
			final int lastSlash = packageName.lastIndexOf('/');

			if (lastSlash == -1) {
				return (false);
			}

			packageName = packageName.substring(0, lastSlash);

			// If any ignore package is a prefix of the class's package,
			// then ignore it. This makes our lives easier.
			final Iterator packages = ignorePackages.iterator();
			while (packages.hasNext()) {
				final String s = (String) packages.next();
				if (type.className().startsWith(s)) {
					addIgnoreClass(type);
					return (true);
				}
			}

			return (false);
		}
	}

	public boolean ignoreMethod(final MemberRef method) {
		if (ignoreMethods.contains(method)) {
			return (true);

		} else if (ignoreClass(method.declaringClass())) {
			addIgnoreMethod(method);
			return (true);
		}
		return (false);
	}

	public boolean ignoreField(final MemberRef field) {
		if (ignoreMethods.contains(field)) {
			return (true);

		} else if (ignoreClass(field.declaringClass())) {
			addIgnoreField(field);
			return (true);
		}
		return (false);
	}

	/**
	 * Commits all classes, methods, and fields, that have been modified.
	 */
	public abstract void commitDirty();

	/**
	 * Test the ignore stuff.
	 */
	public static void main(final String[] args) {
		final PrintWriter out = new PrintWriter(System.out, true);
		final PrintWriter err = new PrintWriter(System.err, true);

		final BloatContext context = new CachingBloatContext(
				new ClassFileLoader(), new ArrayList(), false);

		final List types = new ArrayList();

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-ip")) {
				if (++i >= args.length) {
					err.println("** Missing package name");
					System.exit(1);
				}

				out.println("Ignoring package " + args[i]);
				context.addIgnorePackage(args[i]);

			} else if (args[i].equals("-ic")) {
				if (++i >= args.length) {
					err.println("** Missing class name");
					System.exit(1);
				}

				out.println("Ignoring class " + args[i]);
				final String type = args[i].replace('.', '/');
				context.addIgnoreClass(Type.getType("L" + type + ";"));

			} else {
				// A type
				final String type = args[i].replace('.', '/');
				types.add(Type.getType("L" + type + ";"));
			}
		}

		out.println("");

		final Iterator iter = types.iterator();
		while (iter.hasNext()) {
			final Type type = (Type) iter.next();
			out.println("Ignore " + type + "? " + context.ignoreClass(type));
		}
	}

}
