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

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Performs call site specialization for virtual method invocations. At each
 * virtual method invocation, the call graph is consulted to determine which
 * methods the call site could resolve to. The virtual call site is converted
 * into a "switch" on the method receiver. Each "case" corresponds to a possible
 * type that the receiver may take on. Inside the "case" branch, the receiver is
 * cast to the expected type. Finally, a static version of the virtual method is
 * called. In the "default" case, the virtual method is invoked.
 */
public class Specialize implements Opcode {
	public static boolean DEBUG = false;

	public static boolean DB_STATIC = false;

	public static boolean PRINTCODE = false;

	public static boolean STATS = false;

	public static boolean NOSTATIC = false;

	public static boolean USE_PREEXISTS = true;

	private final Map staticMethods = new HashMap();

	private final Set specialized = new HashSet(); // calls that have
													// specialized

	private InlineStats stats;

	/**
	 * Maximum number of specializations per call site
	 */
	public static int MAX_MORPH = 5;

	/**
	 * Do we specialize monomorphic call sites? By default, <tt>false</tt>.
	 */
	public static boolean MONO = false;

	private InlineContext context;

	private static void db(final String s) {
		if (Specialize.DEBUG) {
			System.out.println(s);
		}
	}

	/**
	 * Constructor.
	 */
	public Specialize(final InlineContext context) {
		this.context = context;

		stats = context.getInlineStats();
	}

	/**
	 * Examines the virtual method call sites in a method and specializes them
	 * based on the resolves-to information found in the call graph.
	 * 
	 * @return <tt>true</tt>, if the method was modified (that is, it
	 *         requires committing)
	 */
	public boolean specialize(final MethodEditor method) {
		if (method.isNative()) {
			// Can't do anything with native methods
			return (false);
		}

		// Do we ignore this method
		if (context.ignoreMethod(method.memberRef())) {
			return (false);
		}

		Specialize.db("\nSpecializing " + method.declaringClass().name() + "."
				+ method.name() + method.type());

		// Okay, what are we doing here? The first task is to find
		// virtual call sites. That's easy. Finding the code that pushes
		// the call's arguments is not so easy. We use the following
		// method. At each instruction we use a InstructionStack to keep
		// track of what the stack looks like. That is, we want to know
		// which instructions push which stack elements. Thus, at a call
		// site we can easily determine which instruction(s) (yes, there
		// might be more than one) push the receiver object on the stack.
		// We insert a dup and a store into a local variable after these
		// instructions. Back at the call site we load from the local
		// variable and specialize away. This method only adds a couple
		// of instructions and can handle all sort of bizzare control
		// flow.

		final CallGraph cg = context.getCallGraph();
		boolean changed = false;
		final InstructionStack stack = new InstructionStack(method);

		// Go through the code looking for call sites. Along the way keep
		// track of the stack.
		final List code = method.code();

		for (int i = 0; i < code.size(); i++) {
			final Object o = code.get(i);
			if (o instanceof Instruction) {
				final Instruction inst = (Instruction) o;

				if ((inst.opcodeClass() == Opcode.opcx_invokevirtual)
						|| (inst.opcodeClass() == Opcode.opcx_invokeinterface)) {
					// Found a virtual method call
					Specialize.db("  Call: " + inst);

					final MemberRef callee = (MemberRef) inst.operand();

					// Do we ignore the callee?
					if (context.ignoreMethod(callee)) {
						Specialize.db("    Explicitly ignoring this method");
						stack.handle(inst);
						continue;
					}

					MethodEditor me = null;
					try {
						me = context.editMethod(callee);

					} catch (final NoSuchMethodException ex0) {
						System.err.println("** Cannot find method " + callee);
						ex0.printStackTrace(System.err);
						System.exit(1);
					}

					if (me.isFinal() && !me.isNative()) {
						// Ah, we have a final method. Just convert it to a
						// static method.
						Specialize.db("  Converting final method " + callee
								+ " to static");

						final int index = code.indexOf(inst);
						final Instruction newInst = new Instruction(
								Opcode.opcx_invokespecial, me.memberRef());
						code.add(index, newInst);
						code.remove(inst);
						stack.handle(newInst);

						// Make note of call
						continue;
					}

					// Calculate the number of stack slots needed to hold the
					// callee's arguments.
					final int nArgs = callee.nameAndType().type().paramTypes().length;

					// Determine the instructions that push the receiver on the
					// stack. Make note of these.
					final Set pushes = stack.atDepth(nArgs);
					if (Specialize.DEBUG) {
						Specialize
								.db("    Receiver on stack after: "
										+ (stack.preexistsAtDepth(nArgs) != null ? " (preexists)"
												: ""));
						final Iterator pushies = pushes.iterator();
						while (pushies.hasNext()) {
							final Instruction push = (Instruction) pushies
									.next();
							Specialize.db("      " + code.indexOf(push) + ") "
									+ push);
						}
					}

					Set set = null; // Method call could resolve to
					if (Specialize.USE_PREEXISTS) {
						// If the receiver is not preexistent, then we can't
						// safely inline the method. Don't bother converting it
						// into a static method.
						final Set preexists = stack.preexistsAtDepth(nArgs);
						if (preexists == null) {
							stack.handle(inst);
							if (Specialize.STATS) {
								stats.noteNoPreexist();
							}
							Specialize.db("    Receiver does not preexist");
							continue;
						}

						// If the preexists is non-empty, then it contains the
						// type(s) that we know the receiver can take on. Remove
						// all others from the set of possible receiver types.
						// Wicked awesome!
						if (!preexists.isEmpty()) {
							set = cg.resolvesTo(callee, preexists);
							;

							if (Specialize.DEBUG) {
								final StringBuffer sb = new StringBuffer(
										"  retaining: ");
								final Iterator iter = preexists.iterator();
								while (iter.hasNext()) {
									final Type type = (Type) iter.next();
									sb.append(Type.truncatedName(type));
									if (iter.hasNext()) {
										sb.append(", ");
									}
								}
								Specialize.db(sb.toString());
							}
						}
					}

					if (set == null) {
						set = cg.resolvesTo(callee);
					}

					if ((set == null) || set.isEmpty()
							|| specialized.contains(inst)) {
						// What does it mean if a call has no call sites? Well,
						// it means that no class that implements the method is
						// created. So, either we have a null pointer exception
						// waiting to happen or the class is instantiated in
						// someplace that we don't analyze (e.g. native
						// methods).
						Specialize
								.db("    "
										+ (specialized.contains(inst) ? "Call already handled"
												: "Resolves to no methods")
										+ ".  Ignoring.");

						// Don't forget to update the stack
						stack.handle(inst);
						continue;
					}

					specialized.add(inst);

					if (Specialize.STATS) {
						// Make note of the number of methods a call site
						// resolves
						// to
						stats.noteMorphicity(set.size());
					}

					if (set.size() > Specialize.MAX_MORPH) {
						// If we only care about monomorphic calls sites, then
						// go
						// home.
						stack.handle(inst);
						continue;
					}

					// Don't update the stack if the call is specialized. This
					// will all get taken care of later.

					// Okay, it's showtime. Originally, I specialized all of
					// the call sites after I had visited the entire method.
					// However, this was incorrect because specialized code may
					// push the receiver on the stack. Bummer.

					// We're making changes
					changed = true;

					// Add a dup and store right after the instructions that
					// pushes the receiver.
					final LocalVariable var = method.newLocal(callee
							.declaringClass());

					Specialize.db("  New local: " + var + " " + var.type()
							+ " (from method " + method.name() + method.type()
							+ ", max " + method.maxLocals() + ")");

					final Iterator iter = pushes.iterator();
					Assert.isTrue(iter.hasNext(),
							"No instruction pushes receiver for " + inst);
					Instruction newInst = null;

					boolean mono = (set.size() == 1);

					while (!mono && iter.hasNext()) {
						// Since we've already examined the code that pushes the
						// receiver, we don't need to worry about keeping track
						// of
						// the stack height. Besides, this code doesn't effect
						// the stack. I hope.

						// There is no need to dup the receiver object if the
						// call
						// is monomorphic. This avoids extra dups being
						// executed.

						final Instruction push = (Instruction) iter.next();
						final int index = code.indexOf(push);
						Assert.isTrue(index != -1, push + " not found in code");
						newInst = new Instruction(Opcode.opcx_dup);
						code.add(index + 1, newInst);

						final Instruction store = new Instruction(
								Opcode.opcx_astore, var);
						code.add(index + 2, store);
					}

					// We've added instructions before the call. This will
					// mess up our code index, i. Update accordingly.
					i = code.indexOf(inst) - 1;

					// Now specialize the call site. Examine each method the
					// call could resolve to in an order such that overriding
					// methods come before overriden methods. We don't need to
					// keep track of the stack because these instructions will
					// be iterated over in a minute. I hope.

					Specialize.db("    Specializing call site...");

					Label nextLabel = null;
					final Label endLabel = method.newLabel();
					endLabel.setStartsBlock(true);
					endLabel.setComment("End Specialization");

					int index = code.indexOf(inst);
					Assert.isTrue(index != -1, "Call " + inst
							+ " not found in code");

					index--; // Trust me

					Assert.isTrue(set != null, "Call to " + callee
							+ " should resolve to something");
					final Object[] sortedSites = set.toArray();

					if (sortedSites.length == 1) {
						// If the call site only resolve to one method, then we
						// don't need to do all of the specialization stuff.
						// Just
						// call the static-ized method.
						Specialize.db("    Monomorphic call site");
						final MemberRef resolvesTo = (MemberRef) sortedSites[0];
						MethodEditor resolvesToMethod = null;
						try {
							resolvesToMethod = this.context
									.editMethod(resolvesTo);

						} catch (final NoSuchMethodException ex) {
							System.err.println("** No such method "
									+ resolvesTo);
							System.exit(1);
						}

						if (resolvesToMethod.isNative()) {
							// Can't specialize native methods. Oh well.
							// Remember
							// that it is possible for an overriding method to
							// be
							// native.
							newInst = new Instruction(
									Opcode.opcx_invokespecial, resolvesTo);
							code.add(++index, newInst);

						} else {
							// Make a static version of the virtual method being
							// invoked. Call it.
							if (Specialize.NOSTATIC) {
								// For testing the specialization stuff, call
								// the
								// virtual method instead of the static method.
								newInst = new Instruction(inst.opcodeClass(),
										inst.operand());
								specialized.add(newInst);

							} else {
								newInst = new Instruction(
										Opcode.opcx_invokespecial,
										resolvesToMethod.memberRef());
							}

							code.add(++index, newInst);
						}

						// Remove the call to the virtual method
						code.remove(inst);
						continue;
					}

					// If we get to here we have a polymorphic call site

					for (int s = 0; (s < sortedSites.length)
							&& (s <= Specialize.MAX_MORPH); s++) {
						final MemberRef resolvesTo = (MemberRef) sortedSites[s];

						// Do we ignore this method?
						if (context.ignoreMethod(resolvesTo)) {
							continue;
						}

						Specialize.db("    Resolves to " + resolvesTo + ")");

						final Type rType = resolvesTo.declaringClass();

						// This may be the target of a branch
						if (nextLabel != null) {
							nextLabel.setComment("Type " + rType);
							code.add(++index, nextLabel);
						}

						// Push the receiver object
						newInst = new Instruction(Opcode.opcx_aload, var);
						code.add(++index, newInst);

						// Check to see if it is this type
						newInst = new Instruction(Opcode.opcx_instanceof, rType);
						code.add(++index, newInst);

						// If its not, try something else
						nextLabel = method.newLabel();
						nextLabel.setStartsBlock(true);
						newInst = new Instruction(Opcode.opcx_ifeq, nextLabel);
						code.add(++index, newInst);

						// We have to add a label here because all branches must
						// be followed by a label. If we don't BLOAT will barf
						// during CFG construction. And that's messy.
						final Label grumble = method.newLabel();
						grumble.setStartsBlock(true);
						code.add(++index, grumble);

						// Otherwise, call the static-ified method. We can't
						// cast
						// the receiver to the expected type. Oh well, we
						// weren't
						// planning on verifying anyway.
						MethodEditor resolvesToMethod = null;
						try {
							resolvesToMethod = this.context
									.editMethod(resolvesTo);

						} catch (final NoSuchMethodException ex) {
							System.err.println("** No such method "
									+ resolvesTo);
							System.exit(1);
						}

						if (resolvesToMethod.isNative()) {
							// Can't specialize native methods. Oh well.
							// Remember
							// that it is possible for an overriding method to
							// be
							// native.
							newInst = new Instruction(
									Opcode.opcx_invokespecial, resolvesTo);
							code.add(++index, newInst);

						} else {
							// Make a static version of the virtual method being
							// invoked. Call it.
							if (Specialize.NOSTATIC) {
								// For testing the specialization stuff, call
								// the
								// virtual method instead of the static method.
								newInst = new Instruction(inst.opcodeClass(),
										inst.operand());
								specialized.add(newInst);

							} else {
								newInst = new Instruction(
										Opcode.opcx_invokespecial,
										resolvesToMethod.memberRef());
							}

							code.add(++index, newInst);
						}

						// Jump to the end
						newInst = new Instruction(Opcode.opcx_goto, endLabel);
						code.add(++index, newInst);
					}

					// Default code still invokes virtual method
					if (nextLabel != null) {
						nextLabel.setComment("Default invocation");
						code.add(++index, nextLabel);
					}

					// Call virtual method. Should be next in line.
					index++;

					// Jump to end.
					newInst = new Instruction(Opcode.opcx_goto, endLabel);
					code.add(++index, newInst);

					// We're all done
					code.add(++index, endLabel);

					if (Specialize.DEBUG) {
						// Print code up to and including end label
						System.out.println("  Code after specializing "
								+ callee.name() + callee.type());
						for (int j = 0; (j <= (index + 2)) && (j < code.size()); j++) {
							final Object q = code.get(j);
							if (q instanceof Label) {
								final Label label = (Label) q;
								System.out
										.println("      "
												+ j
												+ ") "
												+ label
												+ (label.startsBlock() ? " (starts block)"
														: ""));

							} else {
								System.out.println("      " + j + ") " + q);
							}
						}
					}

					// Don't print the stack at the call instruction
					continue;

				} else if (inst.opcodeClass() == Opcode.opcx_invokespecial) {
					// To make our lives easier, convert some invokespecial to
					// invoke static. Basically, if the callee is non-native
					// and is a method of the caller's class or one of its
					// superclasses, we can convert it.
					final MemberRef callee = (MemberRef) inst.operand();

					MethodEditor me = null;
					try {
						me = context.editMethod(callee);
						if (me.isNative() || me.isSynchronized()
								|| me.isConstructor()) {
							// Forget about these guys
							stack.handle(inst);

						} else {
							final Type calleeType = callee.declaringClass();
							final Type callerType = method.declaringClass()
									.type();
							final ClassHierarchy hier = context.getHierarchy();

							if (calleeType.equals(callerType)
									|| hier.subclassOf(callerType, calleeType)) {
								Specialize.db("  Making special " + inst
										+ " static");

								final int index = code.indexOf(inst);
								final Instruction newInst = new Instruction(
										Opcode.opcx_invokespecial, me
												.memberRef());
								code.add(index, newInst);
								code.remove(inst);
								stack.handle(newInst);

							} else {
								// Don't forget about the stack
								stack.handle(inst);
							}
						}

					} catch (final NoSuchMethodException ex2) {
						System.err.println("** Couldn't find method " + callee);
						ex2.printStackTrace(System.err);
						System.exit(1);
					}

				} else {
					// Just update stack
					stack.handle(inst);
				}

				if (Specialize.DEBUG) {
					Specialize.db("    " + code.indexOf(inst) + ") " + inst);
					for (int q = 0; q < stack.height(); q++) {
						final Iterator iter = stack.atDepth(q).iterator();
						if (iter.hasNext()) {
							System.out.print("      ");
						}
						while (iter.hasNext()) {
							System.out.print(code.indexOf(iter.next()));
							if (iter.hasNext()) {
								System.out.print(", ");
							}
						}

						if (stack.preexistsAtDepth(q) != null) {
							System.out.print(" (preexists)");

						} else {
							System.out.print(" (does not preexist)");
						}
						System.out.println("");
					}
				}

			} else if (o instanceof Label) {
				// We've reached a label.
				final Label label = (Label) o;
				stack.handle(label);

				Specialize.db("    " + o);

				if (Specialize.DEBUG) {
					for (int q = 0; q < stack.height(); q++) {
						final Iterator iter = stack.atDepth(q).iterator();
						if (iter.hasNext()) {
							System.out.print("      ");
						}
						while (iter.hasNext()) {
							System.out.print(code.indexOf(iter.next()));
							if (iter.hasNext()) {
								System.out.print(", ");
							}
						}
						System.out.println("");
					}
				}

			} else {
				Assert.isTrue(false, "What is " + o
						+ " doing in the instruction stream?");
			}
		}

		if (Specialize.DEBUG || Specialize.PRINTCODE) {
			// Print out the code
			System.out.println("Specialized code for "
					+ method.declaringClass().name() + "." + method.name()
					+ method.type());

			final Iterator iter2 = code.iterator();
			while (iter2.hasNext()) {
				final Object o = iter2.next();

				if (o instanceof Label) {
					System.out.println("");
				}

				System.out.println("  " + o);
			}
		}

		if (changed) {
			method.setDirty(true);
		}

		return (changed);
	}

}
