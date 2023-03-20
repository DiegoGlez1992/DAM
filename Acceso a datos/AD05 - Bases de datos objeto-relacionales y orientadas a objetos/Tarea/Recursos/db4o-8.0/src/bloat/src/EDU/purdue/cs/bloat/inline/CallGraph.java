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
package EDU.purdue.cs.bloat.inline;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Grants access to certain information about a Java program. At least one root
 * method must be specified. From these root methods, the call graph and
 * information such as the classes that are instantiated during the Java program
 * is computed.
 * 
 * <p>
 * 
 * The construction of the call graph is in the spirit of the "Program
 * Virtual-call Graph" presented in [Bacon97]. However, certain changes have
 * been made to tailor it to BLOAT and Java and to make the overall
 * representation smaller.
 * 
 * <p>
 * 
 * Rapid type analysis is integrated into the construction of the call graph. A
 * virtual method is not examined until we know that its declaring class has
 * been instantiated.
 * 
 * <p>
 * 
 * Some classes are created internally by the VM and are missed by our analysis.
 * So, we maintain a set of "pre-live" classes. We consider all of their
 * constructors to be live.
 */
public class CallGraph {
	public static boolean DEBUG = false;

	private static Set preLive; // "Pre-live" classes

	public static boolean USEPRELIVE = true;

	public static boolean USE1_2 = true;

	private Set roots; // Root methods (MethodRefs)

	private Map calls; // Maps methods to the methods they

	// call (virtual calls are not resolved)
	private Set liveClasses; // Classes (Types) that have been instantiated

	private Map resolvesTo; // Maps methods to the methods they resolve to

	private Map blocked; // Maps types to methods blocked on those types

	List worklist; // Methods to process

	Set liveMethods; // Methods that may be executed

	InlineContext context;

	private ClassHierarchy hier;

	static void db(final String s) {
		if (CallGraph.DEBUG) {
			System.out.println(s);
		}
	}

	/**
	 * Initialize the set of classes that are "pre-live"
	 */
	private static void init() {
		// We can't do this in the static initializer because USE1_2 might
		// not have the desired value.

		CallGraph.preLive = new HashSet();

		CallGraph.preLive.add("java.lang.Boolean");
		CallGraph.preLive.add("java.lang.Class");
		CallGraph.preLive.add("java.lang.ClassLoader");
		CallGraph.preLive.add("java.lang.Compiler");
		CallGraph.preLive.add("java.lang.Integer");
		CallGraph.preLive.add("java.lang.SecurityManager");
		CallGraph.preLive.add("java.lang.String");
		CallGraph.preLive.add("java.lang.StringBuffer");
		CallGraph.preLive.add("java.lang.System");
		CallGraph.preLive.add("java.lang.StackOverflowError");
		CallGraph.preLive.add("java.lang.Thread");
		CallGraph.preLive.add("java.lang.ThreadGroup");

		CallGraph.preLive.add("java.io.BufferedInputStream");
		CallGraph.preLive.add("java.io.BufferedReader");
		CallGraph.preLive.add("java.io.BufferedOutputStream");
		CallGraph.preLive.add("java.io.BufferedWriter");
		CallGraph.preLive.add("java.io.File");
		CallGraph.preLive.add("java.io.FileDescriptor");
		CallGraph.preLive.add("java.io.InputStreamReader");
		CallGraph.preLive.add("java.io.ObjectStreamClass");
		CallGraph.preLive.add("java.io.OutputStreamWriter");
		CallGraph.preLive.add("java.io.PrintStream");
		CallGraph.preLive.add("java.io.PrintWriter");

		CallGraph.preLive.add("java.net.URL");

		CallGraph.preLive.add("java.security.Provider");
		CallGraph.preLive.add("java.security.Security");

		CallGraph.preLive.add("java.util.Hashtable");
		CallGraph.preLive.add("java.util.ListResourceBundle");
		CallGraph.preLive.add("java.util.Locale");
		CallGraph.preLive.add("java.util.Properties");
		CallGraph.preLive.add("java.util.Stack");
		CallGraph.preLive.add("java.util.Vector");

		CallGraph.preLive.add("java.util.zip.ZipFile");

		// Some pre-live classes are only available on JDK1.2.
		if (CallGraph.USE1_2) {
			CallGraph.preLive.add("java.lang.Package");

			CallGraph.preLive.add("java.lang.ref.Finalizer");
			CallGraph.preLive.add("java.lang.ref.ReferenceQueue");

			CallGraph.preLive.add("java.io.FilePermission");
			CallGraph.preLive.add("java.io.UnixFileSystem");

			CallGraph.preLive.add("java.net.URLClassLoader");

			CallGraph.preLive.add("java.security.SecureClassLoader");
			CallGraph.preLive.add("java.security.AccessController");

			CallGraph.preLive.add("java.text.resources.LocaleElements");
			CallGraph.preLive.add("java.text.resources.LocaleElements_en");

			CallGraph.preLive.add("java.util.HashMap");

			CallGraph.preLive.add("java.util.jar.JarFile");
		}
	}

	/**
	 * Adds (the name of) a class to the set of classes that are considered to
	 * be "pre-live"
	 */
	public static void addPreLive(final String name) {
		if (CallGraph.preLive == null) {
			CallGraph.init();
		}
		CallGraph.preLive.add(name);
	}

	/**
	 * Removes a class from the set of "pre-live" classes
	 * 
	 * @return <tt>true</tt> if the class was "pre-live"
	 */
	public static boolean removePreLive(final String name) {
		if (CallGraph.preLive == null) {
			CallGraph.init();
		}
		return (CallGraph.preLive.remove(name));
	}

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            <Tt>InlineContext</tt> used to examine classes and methods.
	 * 
	 * @param roots
	 *            The methods (represented as <tt>MemberRef</tt>s) considered
	 *            to the roots (that is, the "main" methods) of the call graph.
	 *            Presumably, only static methods or constructors can be root
	 *            methods.
	 */
	public CallGraph(final InlineContext context, final Set roots) {
		Assert.isTrue(roots != null, "A call graph must have roots");
		Assert.isTrue(roots.size() > 0, "A call graph must have roots");

		if (CallGraph.preLive == null) {
			CallGraph.init();
		}

		this.context = context;
		this.hier = context.getHierarchy();
		this.roots = roots;

		this.liveClasses = new HashSet();
		this.resolvesTo = new HashMap();
		this.calls = new HashMap();
		this.blocked = new HashMap();
		this.worklist = new LinkedList(this.roots);
		this.liveMethods = new HashSet();

		// To save space, make one InstructionVisitor and use it on every
		// Instruction.
		final CallVisitor visitor = new CallVisitor(this);

		CallGraph.db("Adding pre-live classes");
		doPreLive();

		CallGraph.db("Constructing call graph");

		// Examine each method in the worklist. At each constructor
		// invocation make note of the type that was created. At each
		// method call determine all possible methods that it can resolve
		// to. Add the methods of classes that have been instantiated to
		// the worklist.
		while (!worklist.isEmpty()) {
			final MemberRef caller = (MemberRef) worklist.remove(0);

			if (liveMethods.contains(caller)) {
				// We've already handled this method
				continue;
			}

			MethodEditor callerMethod = null;
			try {
				callerMethod = context.editMethod(caller);

			} catch (final NoSuchMethodException ex1) {
				System.err.println("** Could not find method: " + caller);
				ex1.printStackTrace(System.err);
				System.exit(1);
			}

			// If the method is abstract or native, we can't do anything
			// with it.
			if (callerMethod.isAbstract()) {
				continue;
			}

			liveMethods.add(caller);

			if (callerMethod.isNative()) {
				// We still want native methods to be live
				continue;
			}

			CallGraph.db("\n  Examining method " + caller);

			final Set callees = new HashSet(); // Methods called by caller
			calls.put(caller, callees);

			// If the method is static or is a constructor, the classes
			// static initializer method must have been called. Make note
			// of this.
			if (callerMethod.isStatic() || callerMethod.isConstructor()) {
				addClinit(callerMethod.declaringClass().type());
			}

			// Examine the instructions in the caller method.
			final Iterator code = callerMethod.code().iterator();
			visitor.setCaller(callerMethod);
			while (code.hasNext()) {
				final Object o = code.next();
				if (o instanceof Instruction) {
					final Instruction inst = (Instruction) o;
					inst.visit(visitor);
				}
			}
		}

		// We're done constructing the call graph. Try to free up some
		// memory.
		blocked = null;
	}

	/**
	 * Helper method to add the static initializers and all constructors of the
	 * pre-live classes to the worklist, etc.
	 */
	private void doPreLive() {
		if (!CallGraph.USEPRELIVE) {
			return;
		}

		CallGraph.db("Making constructors of pre-live classes live");

		final Iterator iter = CallGraph.preLive.iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			CallGraph.db("  " + name + " is pre-live");
			name = name.replace('.', '/');

			ClassEditor ce = null;
			try {
				ce = context.editClass(name);

			} catch (final ClassNotFoundException ex1) {
				System.err.println("** Cannot find pre-live class: " + name);
				ex1.printStackTrace(System.err);
				System.exit(1);
			}

			// Make class and static initializer live
			liveClasses.add(ce.type());
			addClinit(ce.type());

			// Make all constructors live
			final MethodInfo[] methods = ce.methods();
			for (int i = 0; i < methods.length; i++) {
				final MethodEditor method = context.editMethod(methods[i]);
				if (method.name().equals("<init>")) {
					CallGraph.db("  " + method);
					worklist.add(method.memberRef());
				}
			}
		}
	}

	/**
	 * Adds the static initializer for a given <tt>Type</tt> to the worklist.
	 */
	void addClinit(final Type type) {
		try {
			final ClassEditor ce = context.editClass(type);

			final MethodInfo[] methods = ce.methods();
			for (int i = 0; i < methods.length; i++) {
				final MethodEditor clinit = context.editMethod(methods[i]);
				if (clinit.name().equals("<clinit>")) {
					worklist.add(clinit.memberRef());
					context.release(clinit.methodInfo());
					break;
				}
				context.release(clinit.methodInfo());
			}
			context.release(ce.classInfo());

		} catch (final ClassNotFoundException ex1) {
			System.err.println("** Could not find class for " + type);
			ex1.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/**
	 * Handles a virtual call. Determines all possible methods the call could
	 * resolve to. Adds the method whose declaring classes are live to the
	 * worklist. Blocks the rest on their declaring types.
	 */
	void doVirtual(final MethodEditor caller, final MemberRef callee) {
		// Figure out which methods the callee can resolve to.
		final Iterator resolvesToWith = hier.resolvesToWith(callee).iterator();

		while (resolvesToWith.hasNext()) {
			final ClassHierarchy.ResolvesToWith rtw = (ClassHierarchy.ResolvesToWith) resolvesToWith
					.next();
			CallGraph.db("      resolves to " + rtw.method);

			// Add all possible non-abstract methods to the call graph.
			// This way, when a blocked method becomes unblocked, it will
			// still be in the call graph.
			addCall(caller, rtw.method);

			Iterator rTypes = rtw.rTypes.iterator();
			boolean isLive = false; // Is one of the rTypes live?
			while (rTypes.hasNext()) {
				final Type rType = (Type) rTypes.next();
				if (liveClasses.contains(rType)) {
					isLive = true;
					CallGraph.db("      Method " + rtw.method + " is live");
					worklist.add(rtw.method);
					break;
				}
			}

			if (!isLive) {
				// If none of the receiver types is live, then the method is
				// blocked on all possible receiver types.
				rTypes = rtw.rTypes.iterator();
				final StringBuffer sb = new StringBuffer();
				while (rTypes.hasNext()) {
					final Type rType = (Type) rTypes.next();
					Set blockedMethods = (Set) blocked.get(rType);
					if (blockedMethods == null) {
						blockedMethods = new HashSet();
						blocked.put(rType, blockedMethods);
					}
					blockedMethods.add(rtw.method);
					sb.append(rType.toString());
					if (rTypes.hasNext()) {
						sb.append(',');
					}
				}
				CallGraph.db("      Blocked " + rtw.method + " on " + sb);
			}
		}
	}

	/**
	 * Makes note of one method calling another. This does not make the method
	 * live.
	 */
	void addCall(final MethodEditor callerMethod, final MemberRef callee) {
		// Just maintain the calls mapping
		final MemberRef caller = callerMethod.memberRef();
		Set callees = (Set) this.calls.get(caller);
		if (callees == null) {
			callees = new HashSet();
			this.calls.put(caller, callees);
		}
		callees.add(callee);
	}

	/**
	 * Marks a <tt>Type</tt> as being lives. It also unblocks any methods that
	 * were blocked on the type.
	 */
	void makeLive(final Type type) {
		if (this.liveClasses.contains(type)) {
			return;
		}

		// Make type live and unblock all methods blocked on it
		CallGraph.db("    Making " + type + " live");
		liveClasses.add(type);
		final Set blockedMethods = (Set) blocked.remove(type);
		if (blockedMethods != null) {
			final Iterator iter = blockedMethods.iterator();
			while (iter.hasNext()) {
				final MemberRef method = (MemberRef) iter.next();
				CallGraph.db("      Unblocking " + method);
				worklist.add(method);
			}
		}
	}

	/**
	 * Returns the methods (<tt>MemberRef</tt>s) to which a given method
	 * could resolve. Only live methods are taken into account. The methods are
	 * sorted such that overriding methods appear before overriden methods.
	 */
	public Set resolvesTo(final MemberRef method) {
		TreeSet resolvesTo = (TreeSet) this.resolvesTo.get(method);

		if (resolvesTo == null) {
			resolvesTo = new TreeSet(new MemberRefComparator(context));
			this.resolvesTo.put(method, resolvesTo);

			final Set liveMethods = this.liveMethods();
			final Iterator rtws = hier.resolvesToWith(method).iterator();
			while (rtws.hasNext()) {
				final ClassHierarchy.ResolvesToWith rtw = (ClassHierarchy.ResolvesToWith) rtws
						.next();
				if (liveMethods.contains(rtw.method)) {
					resolvesTo.add(rtw.method);
				}
			}
		}

		// Return a clone so that the set may safely be modified
		return ((Set) resolvesTo.clone());
	}

	/**
	 * Returns the methods (<tt>MemberRef</tt>s) to which a given method
	 * could resolve given that the receiver is in a certain set of types. Only
	 * live methods are taken into account. The methods are sorted such that
	 * overriding methods appear before overriden methods.
	 */
	public Set resolvesTo(final MemberRef method, final Set rTypes) {
		if (rTypes.isEmpty()) {
			return (resolvesTo(method));
		}

		// Since we're only dealing with a subset of types, don't bother
		// with the caching stuff.
		final TreeSet resolvesTo = new TreeSet(new MemberRefComparator(context));

		final Set liveMethods = this.liveMethods();
		final Iterator rtws = hier.resolvesToWith(method).iterator();
		while (rtws.hasNext()) {
			final ClassHierarchy.ResolvesToWith rtw = (ClassHierarchy.ResolvesToWith) rtws
					.next();
			if (liveMethods.contains(rtw.method)) {
				final HashSet clone = (HashSet) rtw.rTypes.clone();

				clone.retainAll(rTypes);
				if (!clone.isEmpty()) {
					// Only keep method that have at least one possible
					// receiver type in rTypes
					resolvesTo.add(rtw.method);
				}
			}
		}

		// Return a clone so that the set may safely be modified
		return ((Set) resolvesTo.clone());
	}

	/**
	 * Returns the set of methods (<tt>MemberRef</tt>s) that the
	 * construction algorithm has deemed to be live.
	 */
	public Set liveMethods() {
		// Not all of the methods in the calls mapping are necessarily
		// live. So, we have to maintain a separate set.
		return (this.liveMethods);
	}

	/**
	 * Returns the root methods (<tt>MemberRef</tt>s) of the call graph.
	 */
	public Set roots() {
		return (this.roots);
	}

	/**
	 * Returns the set of classes (<tt>Type</tt>s) that are instantiated in
	 * the program.
	 */
	public Set liveClasses() {
		return (this.liveClasses);
	}

	/**
	 * Prints a textual prepresentation of the <tt>CallGraph</tt> to a
	 * <tt>PrintWriter</tt>.
	 * 
	 * @param out
	 *            To where we print
	 * @param printLeaves
	 *            If <tt>true</tt>, leaf methods (methods that do not call
	 *            any other methods) are printed
	 */
	public void print(final PrintWriter out, boolean printLeaves) {

		final Iterator callers = calls.keySet().iterator();
		while (callers.hasNext()) {
			final MemberRef caller = (MemberRef) callers.next();

			final Iterator callees = ((Set) calls.get(caller)).iterator();

			if (!printLeaves && !callees.hasNext()) {
				continue;
			}

			out.print(caller.declaringClass() + "." + caller.name()
					+ caller.type());
			if (roots.contains(caller)) {
				out.print(" (root)");
			}
			out.println("");

			while (callees.hasNext()) {
				final MemberRef callee = (MemberRef) callees.next();

				// Only print live methods
				if (!calls.containsKey(callee)) {
					continue;
				}

				out.println("  " + callee.declaringClass() + "."
						+ callee.name() + callee.type());
			}

			out.println("");
		}
	}

	/**
	 * Prints a summary of the call graph. Including the classes that are live
	 * and which methods are blocked.
	 */
	public void printSummary(final PrintWriter out) {
		out.println("Instantiated classes:");
		final Iterator instantiated = this.liveClasses.iterator();
		while (instantiated.hasNext()) {
			final Type type = (Type) instantiated.next();
			out.println("  " + type.toString());
		}

		out.println("\nBlocked methods:");
		if (blocked != null) {
			final Iterator types = blocked.keySet().iterator();
			while (types.hasNext()) {
				final Type type = (Type) types.next();
				out.println("  " + type);

				final Set set = (Set) blocked.get(type);
				if (set != null) {
					final Iterator methods = set.iterator();
					while (methods.hasNext()) {
						final MemberRef method = (MemberRef) methods.next();
						out.println("    " + method);
					}
				}
			}
		}

		out.println("\nCall graph:");
		this.print(out, false);
	}

}

/**
 * <tt>CallVisitor</tt> examines the instructions in a method and notices what
 * methods are called and which classes are created.
 */
class CallVisitor extends InstructionAdapter {
	MethodEditor caller;

	CallGraph cg;

	boolean firstSpecial; // Are we dealing with the first invokespecial?

	private static void db(final String s) {
		CallGraph.db(s);
	}

	public CallVisitor(final CallGraph cg) {
		this.cg = cg;
	}

	public void setCaller(final MethodEditor caller) {
		this.caller = caller;
		if (caller.isConstructor()) {
			this.firstSpecial = true;
		} else {
			this.firstSpecial = false;
		}
	}

	public void visit_invokevirtual(final Instruction inst) {
		CallVisitor.db("\n    Visiting Call: " + inst);

		this.firstSpecial = false;

		// Call doVirtual to determine which methods this call may resolve
		// to are live.
		final MemberRef callee = (MemberRef) inst.operand();
		cg.doVirtual(caller, callee);
	}

	public void visit_invokeinterface(final Instruction inst) {
		CallVisitor.db("\n    Visiting Call: " + inst);

		this.firstSpecial = false;

		// Pretty much the same as invokevirtual
		final MemberRef callee = (MemberRef) inst.operand();
		cg.doVirtual(caller, callee);
	}

	public void visit_invokestatic(final Instruction inst) {
		CallVisitor.db("\n    Visiting call: " + inst);

		this.firstSpecial = false;

		// There's not a lot to do with static methods since there is no
		// dynamic dispatch.
		final MemberRef callee = (MemberRef) inst.operand();
		cg.addCall(caller, callee);
		cg.worklist.add(callee);
	}

	public void visit_invokespecial(final Instruction inst) {
		CallVisitor.db("\n    Visiting call: " + inst);

		// Recall that invokespecial is used to call constructors, private
		// methods, and "super" methods. There is no dynamic dispatch for
		// special methods.
		final MemberRef callee = (MemberRef) inst.operand();

		MethodEditor calleeMethod = null;

		try {
			calleeMethod = cg.context.editMethod(callee);

		} catch (final NoSuchMethodException ex1) {
			System.err.println("** Couldn't find method: " + callee);
			System.exit(1);
		}

		if (calleeMethod.isSynchronized() || calleeMethod.isNative()) {
			// Calls to synchronized and native methods are virtual
			cg.doVirtual(caller, callee);

		} else {
			// Calls to everything else (superclass methods, private
			// methods, etc.) do not involve a dynamic dispatch and can be
			// treated like a static method.
			cg.addCall(caller, callee);
			cg.worklist.add(callee);
		}

		cg.context.release(calleeMethod.methodInfo());
	}

	public void visit_getstatic(final Instruction inst) {
		// Referencing a static fields implies that its class's static
		// initializer has been invoked.
		CallVisitor.db("\n    Referencing static field " + inst);
		final MemberRef field = (MemberRef) inst.operand();
		cg.addClinit(field.declaringClass());
	}

	public void visit_putstatic(final Instruction inst) {
		// Referencing a static field implies that its class's static
		// initializer has been invoked.
		CallVisitor.db("\n    Referencing static field " + inst);
		final MemberRef field = (MemberRef) inst.operand();
		cg.addClinit(field.declaringClass());
	}

	public void visit_new(final Instruction inst) {
		// The new instruction instantiates a type and thus makes it live.
		final Type type = (Type) inst.operand();
		cg.makeLive(type);
	}
}

/**
 * Compares <tt>MemberRef</tt>s such that overriding methods are less than
 * overridden methods.
 */
class MemberRefComparator implements Comparator {
	TypeComparator c;

	public MemberRefComparator(final InlineContext context) {
		c = new TypeComparator(context);
	}

	public int compare(final Object o1, final Object o2) {
		Assert.isTrue(o1 instanceof MemberRef, o1 + " is not a MemberRef");
		Assert.isTrue(o2 instanceof MemberRef, o2 + " is not a MemberRef");

		final MemberRef ref1 = (MemberRef) o1;
		final MemberRef ref2 = (MemberRef) o2;

		final Type type1 = ref1.declaringClass();
		final Type type2 = ref2.declaringClass();

		return (c.compare(type1, type2));
	}

	public boolean compareTo(final Object other) {
		return (other instanceof MemberRefComparator);
	}
}
