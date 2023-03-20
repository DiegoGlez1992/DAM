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
 * The of <tt>InstructionStack</tt> keeps track of which instructions pushed a
 * certain element of the stack. It is a stack of sets of instructions. You can
 * think of it like this: (1, {4,6}, 8) means that instruction 1 pushed the item
 * on the bottom of the stack, instructions 4 and 6 both push the second element
 * on the stack (depending on control flow), and instruction 8 pushed the
 * element on top of the stack. We use this information at a call site to
 * determine what instruction(s) pushed the receiver object onto the stack.
 * Special thanks to Jan Vitek for helping me come up with this algorithm.
 * 
 * <p>
 * 
 * This class is an <tt>InstructionVisitor</tt> that updates the instruction
 * stack representation appropriately. When there is a merge in control flow,
 * two <tt>InstructionStack</tt>s are merged using the <tt>merge</tt>
 * method.
 * 
 * <p>
 * 
 * This class is also used to determine whether the object at a given stack
 * depth is "preexistent". An object preexists if we can guarantee that it was
 * created outside of the method in which it is used. While it is possible to
 * determine which fields are preexistent (see "Inlining of Virtual Methods" by
 * Detlefs and Ageson in ECOOP99), we only keep track of local variables that
 * preexist.
 * 
 * <p>
 * 
 * We determine which local variables preexist as follows. Initially, only the
 * local variables for method parameters preexist. When a store is encoutered,
 * we determine if the set of instructions on top of the stack consist of loads
 * preexistent variables. If so, then the variable being stored into is
 * preexistent. However, objects that are the result of an allocation
 * (constructor call) in the method are preexist. Thus, we maintain the preexist
 * information as a set. If the set is null, then the object does not preexist.
 * If the set is empty, then it preexists and came from at least one argument.
 * If the set is non-empty, then it contains the type(s) of the constructor(s)
 * from which it originated. Pretty neat, huh?
 */
public class InstructionStack extends InstructionAdapter {

	MethodEditor method; // Method we're dealing with

	HashMap stacks; // Maps Labels to their stacks

	LinkedList currStack; // The current stack

	HashMap preexists; // Maps Labels to their preexists

	HashMap currPreexists; // The current preexist (var -> Set)

	private static void pre(final String s) {
		// Debug preexistence
		if (false) {
			System.out.println(s);
		}
	}

	/**
	 * Constructor. Creates an empty <tt>InstructionStack</tt>.
	 */
	public InstructionStack(final MethodEditor method) {
		this.method = method;
		this.stacks = new HashMap();
		this.preexists = new HashMap();

		// Initially only the parameters to the method prexist
		final Type[] paramTypes = method.paramTypes();
		this.currPreexists = new HashMap();
		for (int i = 0; i < paramTypes.length; i++) {
			// We only care about the preexistence of objects (not arrays)
			if ((paramTypes[i] != null) && paramTypes[i].isObject()) {
				this.currPreexists.put(method.paramAt(i), new HashSet());
			}
		}
	}

	/**
	 * Deals with a Label.
	 */
	public void handle(final Label label) {
		final LinkedList stack = (LinkedList) stacks.get(label);

		if (stack == null) {
			// If this label starts an exception handler, account for the
			// exception being pushed on the stack
			final Iterator tryCatches = method.tryCatches().iterator();
			while (tryCatches.hasNext()) {
				final TryCatch tc = (TryCatch) tryCatches.next();

				if (tc.handler().equals(label)) {
					// Kluge to push the exception object on the stack. I don't
					// think it matters much.
					final Instruction aload = new Instruction(Opcode.opcx_ldc,
							"Exception");
					currStack = new LinkedList();
					aload.visit(this);
					stacks.put(label, stack);
					label.setStartsBlock(true);

					// We have no idea from where the exception will be thrown,
					// so we can't make any assumptions about the preexistence
					// of any variables.
					currPreexists = new HashMap();
					return;
				}
			}

			if (currStack == null) {
				// Make a new stack
				currStack = new LinkedList();
				stacks.put(label, currStack);

				// I don't think we need to worry about the currPreexists. It
				// was taken care of in the constructor.

			} else {
				// Otherwise, keep the current stack
				currStack = (LinkedList) currStack.clone();
				stacks.put(label, currStack);

				// And the current preexists
				currPreexists = InstructionStack.clonePreexists(currPreexists);
				this.preexists.put(label, currPreexists);
			}

		} else {
			// Merge the old stack with the current one
			currStack = InstructionStack.merge(currStack, stack);
			stacks.put(label, currStack);

			final HashMap oldPreexists = (HashMap) this.preexists.get(label);
			currPreexists = InstructionStack.merge(oldPreexists, currPreexists);
			this.preexists.put(label, currPreexists);
		}
	}

	/**
	 * Deals with an <tt>Instruction</tt> handles branches, jsrs, and the
	 * like.
	 */
	public void handle(final Instruction inst) {
		// Visit first
		inst.visit(this);

		if (inst.isJump()) {
			final Label target = (Label) inst.operand();
			target.setStartsBlock(true);

			// Merge the target's stack with any other stacks at that
			// label
			final LinkedList targetStack = (LinkedList) stacks.get(target);
			if (targetStack != null) {
				// Don't change currStack, but do the merge
				stacks.put(target, InstructionStack.merge(currStack,
						targetStack));

				final HashMap oldPreexists = (HashMap) this.preexists
						.get(target);
				this.preexists.put(target, InstructionStack.merge(
						currPreexists, oldPreexists));

			} else {
				// Put a new stack at the target
				stacks.put(target, currStack.clone());
				this.preexists.put(target, InstructionStack
						.clonePreexists(currPreexists));
			}

			if (!inst.isConditionalJump()) {
				// The next instruction should be a Label. But since it is
				// not the next instruction executed, we don't want to merge
				// the contents of the label's stack and the current stack.
				// So, null out the current stack.
				currStack = new LinkedList();
			}

		} else if (inst.isSwitch()) {
			// Propagate the current stack to all targets
			final Switch sw = (Switch) inst.operand();
			final Label defaultTarget = sw.defaultTarget();
			defaultTarget.setStartsBlock(true);

			final LinkedList defaultStack = (LinkedList) stacks
					.get(defaultTarget);
			if (defaultStack != null) {
				Assert.isTrue(defaultStack.size() == currStack.size(),
						"Stack height mismatch (" + defaultStack.size()
								+ " != " + currStack.size() + ") at " + inst);
				// Merge stacks for good measure
				stacks.put(defaultTarget, InstructionStack.merge(currStack,
						defaultStack));

				final HashMap defaultPreexists = (HashMap) this.preexists
						.get(defaultTarget);
				this.preexists.put(defaultTarget, InstructionStack.merge(
						currPreexists, defaultPreexists));

			} else {
				// Put copy of stack at target
				stacks.put(defaultTarget, currStack.clone());
				this.preexists.put(defaultTarget, InstructionStack
						.clonePreexists(currPreexists));
			}

			final Label[] targets = sw.targets();
			for (int t = 0; t < targets.length; t++) {
				final Label target = targets[t];
				target.setStartsBlock(true);
				final LinkedList targetStack = (LinkedList) stacks.get(target);
				if (targetStack != null) {
					Assert.isTrue(targetStack.size() == currStack.size(),
							"Stack height mismatch (" + targetStack.size()
									+ " != " + currStack.size() + ") at "
									+ inst);
					// Merge stacks for good measure
					stacks.put(target, InstructionStack.merge(currStack,
							targetStack));

					final HashMap oldPreexists = (HashMap) this.preexists
							.get(target);
					this.preexists.put(target, InstructionStack.merge(
							oldPreexists, currPreexists));

				} else {
					stacks.put(target, currStack.clone());
					this.preexists.put(target, InstructionStack
							.clonePreexists(currPreexists));
				}
			}

		} else if (inst.isJsr()) {
			// Someday we might have to deal with subroutines that push
			// stuff on the stack. That complicates things. I'm going to
			// pretend it doesn't exist. It was good enough for Nate.

			// In the meantime, we have to propagate the fact that the jsr
			// pushes the return address to the subroutine. We use an empty
			// stack because it is possible that a subroutine could be
			// called with different stack heights. Here is another thing
			// that needs to be fixed someday.
			final LinkedList subStack = new LinkedList();
			final LinkedList oldStack = currStack;

			// Push the return address on stack
			currStack = subStack;
			inst.visit(this);

			currStack = oldStack; // Should be okay unless sub effects stack

			// Propagate subStack to subroutine
			final Label subroutine = (Label) inst.operand();
			subroutine.setStartsBlock(true);
			stacks.put(subroutine, subStack);
			this.preexists.put(subroutine, new HashMap());

		} else if (inst.isReturn() || inst.isThrow()) {
			// We don't what comes next, but we don't want to merge with the
			// current stack.
			currStack = new LinkedList();
		}
	}

	/**
	 * Pushes an instruction onto the stack
	 */
	private void push(final Instruction inst) {
		// Create a new set for the top element of the stack
		final Set set = new HashSet();
		set.add(inst);
		currStack.add(set);
	}

	/**
	 * Pops the top of the stack.
	 */
	private void pop() {
		currStack.removeLast();
	}

	/**
	 * Pops a given number of elements off the stack.
	 */
	private void pop(final int n) {
		for (int i = 0; i < n; i++) {
			currStack.removeLast();
		}
	}

	/**
	 * Returns the number of elements in this instruction stack.
	 */
	public int height() {
		return (currStack.size());
	}

	/**
	 * Returns the set of <tt>Instruction</tt>s at depth <tt>n</tt> of the
	 * instruction stack. Depth 0 is the top of the stack. The bottom of the
	 * stack is at depth stackSize - 1.
	 */
	public Set atDepth(final int n) {
		final Set set = (Set) currStack.get(currStack.size() - 1 - n);
		return (set);
	}

	/**
	 * Returns a <tt>Set</tt> representing whether or not the instructions at
	 * a given depth push a preexistent object onto the stack. If the list is
	 * <tt>null</tt>, then the push is not preexistent. If the list is empty,
	 * then the push is preexistent. If the list is non-empty, it contains the
	 * <tt>Type</tt>(s) of objects that are known to be on the stack. These
	 * types are the results of calls to constructors.
	 */
	public HashSet preexistsAtDepth(final int n) {
		// How do we determine whether a set of instructions pushes a
		// preexist object? All of the instructions must be loads of
		// objects from preexistent variable or the result of an
		// object creation. Note that we can deal with arrays because
		// we'd have to keep track of indices.

		InstructionStack.pre("  Preexisting variables: "
				+ InstructionStack.db(currPreexists));

		HashSet atDepth = null;
		final Iterator insts = this.atDepth(n).iterator();
		Assert.isTrue(insts.hasNext(), "No instructions at depth " + n);
		while (insts.hasNext()) {
			final Instruction inst = (Instruction) insts.next();
			InstructionStack.pre("    Instruction at " + n + ": " + inst);
			if (inst.opcodeClass() == Opcode.opcx_aload) {
				final LocalVariable var = (LocalVariable) inst.operand();
				final Set set = (Set) this.currPreexists.get(var);
				if (set != null) {
					if (set.isEmpty()) {
						// If the set is empty, then this local variable came
						// from
						// a method argument.
						atDepth = new HashSet();

					} else {
						// The list contains types that are the result of a
						// constructor call. Add them to the preexists list.
						if (atDepth == null) {
							atDepth = new HashSet();
						}
						atDepth.addAll(set);
					}
					continue;
				}

				// Instruction loads a non-preexistent variable, fall through

			} else if (inst.opcodeClass() == Opcode.opcx_new) {
				// We look for the new instruction instead of the constructor
				// call because of the way we represent the stack.

				if ((atDepth != null) && atDepth.isEmpty()) {
					// We already know that the object pushed at this depth are
					// one of the arguments. We don't the exact type of the
					// argument, so we can't safely add the type being
					// instantiated to the preexist list.
					continue;
				}

				// Figure out the type being created and add it to the
				// preexists list.
				final Type type = (Type) inst.operand();
				InstructionStack.pre("      Constructing "
						+ Type.truncatedName(type));
				if (atDepth == null) {
					atDepth = new HashSet();
				}
				atDepth.add(type);
				continue;

				// A non-constructor invokespecial was called, fall through

			} else if (inst.opcodeClass() == Opcode.opcx_dup) {
				final Set set = this.preexistsAtDepth(n - 1);
				if (set != null) {
					if (set.isEmpty()) {
						// If list is empty, then this preexist must also be
						// empty
						atDepth = new HashSet();

					} else {
						// Add the classes instantiated to the list
						atDepth.addAll(set);
					}
					continue;
				}
			}

			InstructionStack.pre("  Doesn't preexist");
			return (null);
		}

		// If we got down here every instruction was preexistent
		InstructionStack.pre("  Preexists");
		return (atDepth);

	}

	/**
	 * Merges two stacks together and returns their union. Note that stacks of
	 * unequal height cannot be merged.
	 */
	private static LinkedList merge(final LinkedList stack1,
			final LinkedList stack2) {

		Assert.isFalse((stack1 == null) && (stack2 == null),
				"Cannot merge two null stacks");

		final LinkedList merge = new LinkedList();

		// If either stack is null or empty, just use the other one
		if ((stack1 == null) || (stack1.size() == 0)) {
			merge.addAll(stack2);
			return (merge);
		}

		if ((stack2 == null) || (stack2.size() == 0)) {
			merge.addAll(stack1);
			return (merge);
		}

		Assert.isTrue(stack1.size() == stack2.size(),
				"Stacks of unequal height cannot be merged (" + stack1.size()
						+ " != " + stack2.size() + ")");

		for (int i = 0; i < stack1.size(); i++) {
			final Set mergeSet = new HashSet();
			mergeSet.addAll((Set) stack1.get(i));
			mergeSet.addAll((Set) stack2.get(i));
			merge.add(i, mergeSet);
		}

		return (merge);
	}

	/**
	 * Merges two preexists lists. For a given variable, if either of the two
	 * input indices is <tt>null</tt>, then the result is <tt>null</tt>.
	 * If either of the two input indices is empty, then the result is empty.
	 * Otherwise, the result is the union of the two input sets.
	 */
	private static HashMap merge(final HashMap one, final HashMap two) {
		Assert.isFalse((one == null) && (two == null),
				"Can't merge null preexists");

		if (one == null) {
			return (InstructionStack.clonePreexists(two));

		} else if (two == null) {
			return (InstructionStack.clonePreexists(one));
		}

		// Go through all of the variables in both sets. If one is not
		// contained in the other, then the set (or null) from the other
		// is used. If one is mapped to null, then the result is null.
		// If one has an empty set, then the result has an empty set.
		// Otherwise, the two non-empty sets are merge.
		final HashMap result = new HashMap();
		final Set allVars = new HashSet();
		allVars.addAll(one.keySet());
		allVars.addAll(two.keySet());
		final Iterator iter = allVars.iterator();
		while (iter.hasNext()) {
			final LocalVariable var = (LocalVariable) iter.next();
			if (!one.containsKey(var)) {
				HashSet set = (HashSet) two.get(var);
				if (set != null) {
					set = (HashSet) set.clone();
				}

				result.put(var, set);

			} else if (!two.containsKey(var)) {
				HashSet set = (HashSet) one.get(var);
				if (set != null) {
					set = (HashSet) set.clone();
				}

				result.put(var, set);

			} else {
				final HashSet oneSet = (HashSet) one.get(var);
				final HashSet twoSet = (HashSet) two.get(var);
				if ((oneSet == null) || (twoSet == null)) {
					result.put(var, null);

				} else if (oneSet.isEmpty() || twoSet.isEmpty()) {
					result.put(var, new HashSet());

				} else {
					final Set set = new HashSet();
					set.addAll(oneSet);
					set.addAll(twoSet);
					result.put(var, set);
				}
			}
		}

		InstructionStack.pre("Merge of " + InstructionStack.db(one) + " and "
				+ InstructionStack.db(two) + " is "
				+ InstructionStack.db(result));

		return (result);
	}

	/**
	 * Returns a textual representation of a preexists mapping.
	 */
	static String db(final HashMap preexists) {
		if (preexists == null) {
			return ("\n  null?\n");
		}

		final StringBuffer sb = new StringBuffer("\n");
		final Iterator vars = preexists.keySet().iterator();
		while (vars.hasNext()) {
			final LocalVariable var = (LocalVariable) vars.next();
			final Set set = (Set) preexists.get(var);
			if (set == null) {
				sb.append("  " + var + ": null\n");

			} else {
				sb.append("  " + var + ": ");
				final Iterator iter = set.iterator();
				while (iter.hasNext()) {
					final Type type = (Type) iter.next();
					sb.append(Type.truncatedName(type));
					if (iter.hasNext()) {
						sb.append(", ");
					}
				}
				sb.append("\n");
			}
		}

		return (sb.toString());
	}

	/**
	 * Makes a deep copy of a List containing preexists information.
	 */
	private static HashMap clonePreexists(final HashMap old) {
		final HashMap clone = new HashMap();
		final Iterator vars = old.keySet().iterator();
		while (vars.hasNext()) {
			final LocalVariable var = (LocalVariable) vars.next();
			final HashSet set = (HashSet) old.get(var);
			if (set == null) {
				clone.put(var, null);
			} else {
				clone.put(var, set.clone());
			}
		}
		return (clone);
	}

	public void visit_nop(final Instruction inst) {
	}

	public void visit_ldc(final Instruction inst) {
		push(inst);
	}

	public void visit_iload(final Instruction inst) {
		push(inst);
	}

	public void visit_lload(final Instruction inst) {
		push(inst);
	}

	public void visit_fload(final Instruction inst) {
		push(inst);
	}

	public void visit_dload(final Instruction inst) {
		push(inst);
	}

	public void visit_aload(final Instruction inst) {
		push(inst);
	}

	public void visit_iaload(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_laload(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_faload(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_daload(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_aaload(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_baload(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_caload(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_saload(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_istore(final Instruction inst) {
		pop();
	}

	public void visit_lstore(final Instruction inst) {
		pop();
	}

	public void visit_fstore(final Instruction inst) {
		pop();
	}

	public void visit_dstore(final Instruction inst) {
		pop();
	}

	public void visit_astore(final Instruction inst) {
		// When we store an object to a local variable, we need to keep
		// track of whether or not the object being stored (and hence the
		// variable into which it is stored) is preexistent.
		final LocalVariable var = (LocalVariable) inst.operand();
		final HashSet set = preexistsAtDepth(0);

		if (set == null) {
			InstructionStack.pre("      " + var + " does not preexist");
			this.currPreexists.put(var, null);

		} else if (set.isEmpty()) {
			InstructionStack.pre("      " + var + " preexists");
			this.currPreexists.put(var, new HashSet());

		} else {
			// This store superceeds anything else that was already in this
			// local, so don't merge the sets.
			InstructionStack.pre("      " + var + " preexists with types");
			this.currPreexists.put(var, set.clone());
		}

		pop();
	}

	public void visit_iastore(final Instruction inst) {
		pop(3);
	}

	public void visit_lastore(final Instruction inst) {
		pop(3);
	}

	public void visit_fastore(final Instruction inst) {
		pop(3);
	}

	public void visit_dastore(final Instruction inst) {
		pop(3);
	}

	public void visit_aastore(final Instruction inst) {
		pop(3);
	}

	public void visit_bastore(final Instruction inst) {
		pop(3);
	}

	public void visit_castore(final Instruction inst) {
		pop(3);
	}

	public void visit_sastore(final Instruction inst) {
		pop(3);
	}

	/**
	 * Helper method for asserting that all of the instructions are of a certain
	 * category.
	 */
	private void checkCategory(final Set insts, final int category) {
		final Iterator iter = insts.iterator();
		while (iter.hasNext()) {
			final Instruction inst = (Instruction) iter.next();
			Assert.isTrue(inst.category() == category, "Category mismatch: "
					+ inst.category() + " != " + category);
		}
	}

	/**
	 * Helper method for asserting that all of the instructions have the same
	 * category. The category is returned.
	 */
	private int checkCategory(final Set insts) {
		int category = 0;
		final Iterator iter = insts.iterator();
		while (iter.hasNext()) {
			final Instruction inst = (Instruction) iter.next();
			if (category == 0) {
				category = inst.category();

			} else {
				Assert.isTrue(inst.category() == category,
						"Category mismatch in instruction set");
			}
		}

		Assert.isTrue(category != 0, "No instructions in set");
		return (category);
	}

	public void visit_pop(final Instruction inst) {
		final Set insts = atDepth(0);

		checkCategory(insts, 1);

		pop();
	}

	public void visit_pop2(final Instruction inst) {
		// Form 1 pops two category 1 values off the stack. Form 2 pops
		// one category 2 value off the stack.

		final Set top1 = (Set) currStack.removeLast();

		final int category = checkCategory(top1);

		if (category == 1) {
			// Pop another category 1 off
			final Set top2 = (Set) currStack.removeLast();
			checkCategory(top2, 1);
		}
	}

	public void visit_dup(final Instruction inst) {
		// Duplicate the category 1 value on the top of the stack
		final Set dup = atDepth(0);

		checkCategory(dup, 1);

		currStack.add(new HashSet(dup));
	}

	public void visit_dup_x1(final Instruction inst) {
		final Set dup = atDepth(0);

		checkCategory(dup, 1);

		currStack.add(currStack.size() - 2, new HashSet(dup));
	}

	public void visit_dup_x2(final Instruction inst) {
		// Top value on stack must be category 1.
		final Set top1 = atDepth(0);

		checkCategory(top1, 1);

		final Set top2 = atDepth(1);

		final int category = checkCategory(top2);

		if (category == 1) {
			final Set top3 = atDepth(2);
			checkCategory(top3, 1);

			// Form 1: Dup top value and put three down

			currStack.add(currStack.size() - 3, new HashSet(top1));

		} else {
			// Form 2: Dup top value and put two down

			currStack.add(currStack.size() - 2, new HashSet(top1));
		}
	}

	public void visit_dup2(final Instruction inst) {
		// If top two values are both category 1, dup them both.
		// Otherwise, dup the one category 2 value.
		final Set top = atDepth(0);

		final int category = checkCategory(top);

		if (category == 1) {
			final Set top1 = atDepth(1);
			checkCategory(top1, 1);

			// Form 1: Dup top two values

			currStack.add(new HashSet(top1));
			currStack.add(new HashSet(top));

		} else {
			// Form 2: Dup top value

			currStack.add(new HashSet(top));
		}
	}

	public void visit_dup2_x1(final Instruction inst) {
		// If the top two values are of category 1, then dup them and put
		// them three down. Otherwise, the top two values are of category
		// 2 and the top value is put two down.
		final Set top = atDepth(0);

		final int category = checkCategory(top);

		if (category == 1) {
			final Set top1 = atDepth(1);
			checkCategory(top1, 1);

			// Form 1: Dup top two values and put three down

			final int n = currStack.size() - 3;
			currStack.add(n, top1);
			currStack.add(n, top);

		} else {
			final Set top1 = atDepth(1);
			checkCategory(top1, 1);

			// Form 2: Dup top value and put two down

			currStack.add(currStack.size() - 2, new HashSet(top));
		}
	}

	public void visit_dup2_x2(final Instruction inst) {
		// If the two four values are all category 1, then duplicate the
		// top two values and put them four down. If the top value is of
		// category 2 and the next two are of type 1, then dup the top
		// value and put it three down. If the top two values are both
		// category 1 and the third value is type 2, then dup the top two
		// values and put them three down. If the top two values are both
		// category 2, then dup the top one and put it two down.

		final Set top = atDepth(0);
		final int category = checkCategory(top);

		if (category == 1) {
			final Set top1 = atDepth(1);
			final int category1 = checkCategory(top1);
			if (category1 == 1) {
				final Set top2 = atDepth(2);
				final int category2 = checkCategory(top2);
				if (category2 == 1) {
					checkCategory(atDepth(3), 1);

					// Form 1: Dup top two values and put four down
					final int n = currStack.size() - 4;
					currStack.add(n, new HashSet(top1));
					currStack.add(n, new HashSet(top));

				} else {
					// Form 3: Dup top two values and put three down
					final int n = currStack.size() - 3;
					currStack.add(n, new HashSet(top1));
					currStack.add(n, new HashSet(top));

				}

			} else {
				Assert.isTrue(false, "Impossible stack combination for "
						+ "dup2_x1: ... 2 1");
			}

		} else {
			final Set top1 = atDepth(1);
			final int category1 = checkCategory(top1);
			if (category1 == 1) {
				final int category2 = checkCategory(atDepth(2));
				if (category2 == 1) {
					// Form 2: Dup top value and put three down
					currStack.add(currStack.size() - 3, new HashSet(top));

				} else {
					Assert.isTrue(false, "Impossible stack combination for "
							+ "dup2_x1: ... 2 1 2");
				}

			} else {
				// Form 4: Dup top and put two down
				currStack.add(currStack.size() - 2, new HashSet(top));
			}
		}
	}

	public void visit_swap(final Instruction inst) {
		final Set top = (Set) currStack.removeLast();
		final Set next = (Set) currStack.removeLast();

		checkCategory(top, 1);
		checkCategory(next, 1);

		currStack.add(top);
		currStack.add(next);
	}

	public void visit_iadd(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_ladd(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_fadd(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_dadd(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_isub(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_lsub(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_fsub(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_dsub(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_imul(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_lmul(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_fmul(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_dmul(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_idiv(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_ldiv(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_fdiv(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_ddiv(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_irem(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_lrem(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_frem(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_drem(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_ineg(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_lneg(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_fneg(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_dneg(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_ishl(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_lshl(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_ishr(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_lshr(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_iushr(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_lushr(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_iand(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_land(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_ior(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_lor(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_ixor(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_lxor(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_iinc(final Instruction inst) {
		// Kind of a fine point here. The instruction doesn't change the
		// stack, iinc increments a local variable.
	}

	public void visit_i2l(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_i2f(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_i2d(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_l2i(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_l2f(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_l2d(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_f2i(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_f2l(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_f2d(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_d2i(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_d2l(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_d2f(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_i2b(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_i2c(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_i2s(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_lcmp(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_fcmpl(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_fcmpg(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_dcmpl(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_dcmpg(final Instruction inst) {
		pop(2);
		push(inst);
	}

	public void visit_ifeq(final Instruction inst) {
		pop();
	}

	public void visit_ifne(final Instruction inst) {
		pop();
	}

	public void visit_iflt(final Instruction inst) {
		pop();
	}

	public void visit_ifge(final Instruction inst) {
		pop();
	}

	public void visit_ifgt(final Instruction inst) {
		pop();
	}

	public void visit_ifle(final Instruction inst) {
		pop();
	}

	public void visit_if_icmpeq(final Instruction inst) {
		pop(2);
	}

	public void visit_if_icmpne(final Instruction inst) {
		pop(2);
	}

	public void visit_if_icmplt(final Instruction inst) {
		pop(2);
	}

	public void visit_if_icmpge(final Instruction inst) {
		pop(2);
	}

	public void visit_if_icmpgt(final Instruction inst) {
		pop(2);
	}

	public void visit_if_icmple(final Instruction inst) {
		pop(2);
	}

	public void visit_if_acmpeq(final Instruction inst) {
		pop(2);
	}

	public void visit_if_acmpne(final Instruction inst) {
		pop(2);
	}

	public void visit_goto(final Instruction inst) {
		// Nothing to do
	}

	public void visit_jsr(final Instruction inst) {
		push(inst);
	}

	public void visit_ret(final Instruction inst) {
		// Nothing to do
	}

	public void visit_switch(final Instruction inst) {
		pop();
	}

	// Return stuff performed by handle(Instruction)
	public void visit_ireturn(final Instruction inst) {

	}

	public void visit_lreturn(final Instruction inst) {

	}

	public void visit_freturn(final Instruction inst) {

	}

	public void visit_dreturn(final Instruction inst) {

	}

	public void visit_areturn(final Instruction inst) {

	}

	public void visit_return(final Instruction inst) {

	}

	public void visit_getstatic(final Instruction inst) {
		push(inst);
	}

	public void visit_putstatic(final Instruction inst) {
		pop();
	}

	public void visit_putstatic_nowb(final Instruction inst) {
		pop();
	}

	public void visit_getfield(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_putfield(final Instruction inst) {
		pop(2);
	}

	public void visit_putfield_nowb(final Instruction inst) {
		pop(2);
	}

	public void visit_invokevirtual(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		pop(type.paramTypes().length);
		pop(); // Pop receiver

		if (type.returnType() != Type.VOID) {
			push(inst);
		}
	}

	public void visit_invokespecial(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		pop(type.paramTypes().length);
		pop(); // Pop receiver

		if (type.returnType() != Type.VOID) {
			push(inst);
		}
	}

	public void visit_invokestatic(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		pop(type.paramTypes().length);

		if (type.returnType() != Type.VOID) {
			push(inst);
		}
	}

	public void visit_invokeinterface(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		pop(type.paramTypes().length);
		pop(); // Pop receiver

		if (type.returnType() != Type.VOID) {
			push(inst);
		}
	}

	public void visit_new(final Instruction inst) {
		push(inst);
	}

	public void visit_newarray(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_arraylength(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_athrow(final Instruction inst) {
		// I guess...
		pop();
		push(inst);
	}

	public void visit_checkcast(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_instanceof(final Instruction inst) {
		pop();
		push(inst);
	}

	public void visit_monitorenter(final Instruction inst) {
		pop();
	}

	public void visit_monitorexit(final Instruction inst) {
		pop();
	}

	public void visit_multianewarray(final Instruction inst) {
		final MultiArrayOperand operand = (MultiArrayOperand) inst.operand();
		final int dim = operand.dimensions();

		pop(dim);

		push(inst);
	}

	public void visit_ifnull(final Instruction inst) {
		pop();
	}

	public void visit_ifnonnull(final Instruction inst) {
		pop();
	}

	public void visit_rc(final Instruction inst) {
	}

	public void visit_aupdate(final Instruction inst) {
	}

	public void visit_supdate(final Instruction inst) {
	}

	public void visit_aswizzle(final Instruction inst) {
	}

	public void visit_aswrange(final Instruction inst) {
	}

}
