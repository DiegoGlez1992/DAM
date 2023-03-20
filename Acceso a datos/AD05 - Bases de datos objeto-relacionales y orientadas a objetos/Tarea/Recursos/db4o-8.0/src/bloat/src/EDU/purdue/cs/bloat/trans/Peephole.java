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
package EDU.purdue.cs.bloat.trans;

import java.util.*;

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * Performs some peephole optimizations such as loads and stores and removes
 * unreachable instructions.
 */
public class Peephole implements Opcode {
	public static boolean DEBUG = false;

	/**
	 * Perform peephole optimizations on the bytecodes in a method. Peephole
	 * optimizations look at two consecutive instructions and try to perform
	 * some simple optimization. For instance, a push followed by a pop is
	 * uninteresting, so both of those instructions can be removed.
	 * 
	 * <p>
	 * 
	 * After the peephole optimizations are performed a final phase removes
	 * instructions that are not reachable. These instructions reside in basic
	 * blocks whose starting label is never jumped to.
	 */
	public static void transform(final MethodEditor method) {
		if (Peephole.DEBUG) {
			System.out.println("Peephole optimizing " + method);
		}

		// Map between labels and the instruction that they label
		final Map targets = new HashMap();

		final LinkedList jumps = new LinkedList(); // Jump instructions

		Instruction next = null;
		Instruction nextInst = null;

		final List code = method.code();

		// Go backwards so we can eliminate redundant loads and stores
		// in one pass. During the pass collect the locations of labels.

		CODE: for (int i = code.size() - 1; i >= 0; i--) {
			final Object ce = code.get(i);

			if (ce instanceof Label) {
				if (nextInst != null) {
					targets.put(ce, nextInst);
				}

				next = null;

			} else if (ce instanceof Instruction) {
				final Instruction inst = (Instruction) ce;

				Filter peep = null;

				// Have we seen a label that starts a block? (i.e. is a target)
				boolean seenLabel = false;

				if (inst.isGoto()) {
					// Look at the instructions following the goto. If an
					// instruction follows and no label (that starts a block)
					// has been seen, the instruction is dead and can be
					// removed. If the target of the goto follows the goto
					// instruction, the goto is useless and is removed.

					final Label target = (Label) inst.operand();

					for (int j = i + 1; j < code.size(); j++) {
						final Object t = code.get(j);

						// Replace
						// goto L
						// L: inst
						// with
						// L: inst
						//
						if (t instanceof Label) {
							if (((Label) t).startsBlock()) {
								seenLabel = true;
							}

							if (target.equals(t)) {
								code.remove(i);
								next = null;
								nextInst = null;
								continue CODE;
							}

							continue;
						}

						// Replace
						// goto L
						// this is unreachable
						// M: inst (M is a different label from L!)
						// with
						// goto L
						// M: inst
						//
						if (t instanceof Instruction) {
							if (seenLabel) {
								break;
							}

							code.remove(j);
							j--;
						}
					}
				}

				if (inst.isGoto() || inst.isSwitch()) {
					jumps.add(inst);
				}

				// Performs some peephole optimizations using the filter
				// method that returns an instance of the Filter class. The
				// filter method looks at two consecutive instructions and
				// determines whether or not something about them can be
				// changed. For instance, if a push is followed by a pop,
				// both instructions are useless and can be eliminated. The
				// contents of the Filter object represents the effects of the
				// peephole optimization.
				if (next != null) {
					peep = Peephole.filter(inst, next);
				}

				if (peep != null) {
					if (ClassEditor.DEBUG) {
						if (peep.replace.length == 0) {
							System.out.println("eliminate " + code.get(i) + "-"
									+ code.get(i + 1));

						} else {
							System.out.println("replace " + code.get(i) + "-"
									+ code.get(i + 1));
							System.out.println("   with");
							for (int j = 0; j < peep.replace.length; j++) {
								System.out.println("   " + peep.replace[j]);
							}
						}
					}

					// Remove old instructions
					code.remove(i + 1);
					code.remove(i);

					// Add new instructions resulting from peephole
					// optimizations
					for (int j = peep.replace.length - 1; j >= 0; j--) {
						code.add(i, peep.replace[j]);
					}

					if ((i < code.size())
							&& (code.get(i) instanceof Instruction)) {
						next = (Instruction) code.get(i);

					} else {
						// No more instructions, or next thing is a label
						next = null;
					}

				} else {
					// Filter didn't find any peephole optimizations, skip to
					// next pair of instructions.
					next = inst;
				}

				nextInst = next;
			}
		}

		// Replace the target of jumps to gotos with the goto target.
		// Replace gotos to unconditional jumps with the unconditional jump.
		while (!jumps.isEmpty()) {
			final Instruction inst = (Instruction) jumps.removeFirst();

			Instruction target;

			if (inst.isGoto()) {
				target = (Instruction) targets.get(inst.operand());

				if (target != null) {
					if (target.isGoto()
							&& !target.operand().equals(inst.operand())) {

						if (ClassEditor.DEBUG) {
							System.out.println("replace " + inst);
						}

						inst.setOperand(target.operand());

						if (ClassEditor.DEBUG) {
							System.out.println("   with " + inst);
						}

						jumps.add(inst);

					} else if (target.isSwitch() || target.isReturn()
							|| target.isThrow()) {

						if (ClassEditor.DEBUG) {
							System.out.println("replace " + inst);
						}

						inst.setOpcodeClass(target.opcodeClass());
						inst.setOperand(target.operand());

						if (ClassEditor.DEBUG) {
							System.out.println("   with " + inst);
						}
					}
				}
			}
		}

		// Remove unreachable code.
		Peephole.removeUnreachable(method, code);

		if (ClassEditor.DEBUG) {
			System.out.println("END PEEPHOLE---------------------------------");
		}
	}

	/**
	 * Iterate over the code in the method and determine which labels begin
	 * blocks that are reachable. The code in blocks that are not reachable is
	 * removed.
	 */
	// TODO: Currently, ALL ret targets are marked reachable from a
	// single ret. Correct this by looking at the local variables.
	private static void removeUnreachable(final MethodEditor method,
			final List code) {
		// Maps Labels to their instruction position
		final Map labelPos = new HashMap();

		// Collect all the ret targets.
		Iterator iter = code.iterator();
		int i = 0;

		while (iter.hasNext()) {
			final Object ce = iter.next();

			if (ce instanceof Label) {
				labelPos.put(ce, new Integer(i));
			}

			i++;
		}

		// Visit the blocks depth-first.

		// Stack of Labels that begin blocks that have been visited
		final Set visited = new HashSet();

		// Stack of Labels that begin blocks that have not been visited
		final Stack stack = new Stack();

		Label label; // Current label

		if (code.size() > 0) {
			// Start with the label of the first block
			label = (Label) code.get(0);
			visited.add(label);
			stack.push(label);
		}

		final Iterator e = method.tryCatches().iterator();

		while (e.hasNext()) {
			// All exception handlers are considered to be live
			final TryCatch tc = (TryCatch) e.next();
			visited.add(tc.handler());
			stack.push(tc.handler());
		}

		while (!stack.isEmpty()) {
			label = (Label) stack.pop();

			final Integer labelIndex = (Integer) labelPos.get(label);
			Assert.isTrue(labelIndex != null, "Index of " + label
					+ " not found");

			i = labelIndex.intValue();
			final ListIterator blockIter = code.listIterator(i + 1);

			while (blockIter.hasNext()) {
				// Iterate over the code in the block. If we encounter
				// instructions that change execution (i.e. go to another
				// block), add the Label of the target of the jump to the
				// stack if it is not already present.

				final Object ce = blockIter.next();
				i++;

				if (ce instanceof Instruction) {
					final Instruction inst = (Instruction) ce;

					if (inst.isReturn() || inst.isThrow()) {
						// We've reached the end of the block, but we don't know
						// which block will be executed next.
						break;

					} else if (inst.isConditionalJump() || inst.isJsr()) {
						// We've reached the end of the block, add the Label of
						// the next block to be executed to the list. It's a
						// conditional jump, so don't break. The rest of the
						// code
						// in the block is not necessarily dead.

						label = (Label) inst.operand();

						if (!visited.contains(label)) {
							visited.add(label);
							stack.push(label);
						}

						// Fall through.

					} else if (inst.isGoto()) {
						// Add next block to work list.

						label = (Label) inst.operand();

						if (!visited.contains(label)) {
							visited.add(label);
							stack.push(label);
						}

						break;

					} else if (inst.isRet()) {
						// The ret targets were handled by the jsr.
						break;

					} else if (inst.isSwitch()) {
						// A switch. Add all possible targets of the switch to
						// the worklist.

						final Switch sw = (Switch) inst.operand();

						label = sw.defaultTarget();

						if (!visited.contains(label)) {
							visited.add(label);
							stack.push(label);
						}

						final Label[] targets = sw.targets();

						for (int j = 0; j < targets.length; j++) {
							label = targets[j];

							if (!visited.contains(label)) {
								visited.add(label);
								stack.push(label);
							}
						}

						break;
					}

				} else if (ce instanceof Label) {
					label = (Label) ce;
					visited.add(label);
				}
			}
		}

		boolean reachable = false;

		iter = code.iterator();

		// Remove unreachable instructions
		while (iter.hasNext()) {
			final Object ce = iter.next();

			if (ce instanceof Label) {
				reachable = visited.contains(ce);
				// Don't remove unreachable labels, only instructions.

			} else if (!reachable) {
				if (ClassEditor.DEBUG) {
					System.out.println("Removing unreachable " + ce);
				}

				iter.remove();
			}
		}
	}

	/**
	 * Filter represents a set of instructions that result from a peephole
	 * optimizations. For instance, when uninteresting instructions are removed,
	 * a Filter object with an empty "replace" array will be returned by the
	 * below filter method.
	 */
	static class Filter {
		Instruction[] replace;

		Filter() {
			this.replace = new Instruction[0];
		}

		Filter(final Instruction replace) {
			this.replace = new Instruction[] { replace };
		}

		Filter(final Instruction replace1, final Instruction replace2) {
			this.replace = new Instruction[] { replace1, replace2 };
		}
	}

	/**
	 * Filter a pair of instructions. That is, do a peephole optimization on two
	 * consecutive instructions. For instance, if a push is followed by a pop,
	 * both instructions can be eliminated. The <tt>Filter</tt> object that is
	 * returned specifies what instruction(s), if any, should replace the two
	 * instructions that are the parameters to this method.
	 * 
	 * @param first
	 *            The first instruction.
	 * @param second
	 *            The second instruction.
	 * @return A list of instructions to replace the two instructions with, or
	 *         null, if the instructions should be left as is.
	 */
	private static Filter filter(final Instruction first,
			final Instruction second) {
		switch (second.opcodeClass()) {

		// swap means nothing if it's after a dup.
		// (goodbye means nothing when it's all for show
		// so stop pretending you've somewhere else to go.)

		case opcx_swap:
			// Elminate swap-swap
			if (first.opcodeClass() == Opcode.opcx_swap) {
				return new Filter();
			}
			// swap means nothing if it's after a dup.
			// (goodbye means nothing when it's all for show
			// so stop pretending you've somewhere else to go.)
			if (first.opcodeClass() == Opcode.opcx_dup) {
				return new Filter(first);
			}
			break;

		// Eliminate push-pop.
		case opcx_pop:
			// Eliminate push-pop.

			switch (first.opcodeClass()) {
			case opcx_ldc:
				// Make sure things being popped off is not wide (we're
				// dealing with a pop not a pop2).
				Assert.isTrue(!(first.operand() instanceof Long)
						&& !(first.operand() instanceof Double),
						"Cannot pop a 2-word operand");
				// Fall through.

			case opcx_iload:
			case opcx_fload:
			case opcx_aload:
			case opcx_dup:
				// Eliminate the load and the pop.
				return new Filter();

			case opcx_dup_x1:
				// Replace dup_x1-pop with swap
				// (As if this is really likely to happen ;) <-- Nate made a
				// joke!
				return new Filter(new Instruction(Opcode.opcx_swap));
			}
			break;

		case opcx_pop2:
			switch (first.opcodeClass()) {
			case opcx_ldc:
				Assert.isTrue((first.operand() instanceof Long)
						|| (first.operand() instanceof Double),
						"Cannot pop2 a 1-word operand");
				// Fall through.

			case opcx_lload:
			case opcx_dload:
			case opcx_dup2:
				// Eliminate push and pop
				return new Filter();
			}
			break;

		case opcx_istore:
			// Eliminate load-store to same location.

			if (first.opcodeClass() == Opcode.opcx_iload) {
				if (first.operand().equals(second.operand())) {
					return new Filter();
				}
			}
			break;

		case opcx_fstore:
			if (first.opcodeClass() == Opcode.opcx_fload) {
				if (first.operand().equals(second.operand())) {
					return new Filter();
				}
			}
			break;

		case opcx_astore:
			if (first.opcodeClass() == Opcode.opcx_aload) {
				if (first.operand().equals(second.operand())) {
					return new Filter();
				}
			}
			break;

		case opcx_lstore:
			if (first.opcodeClass() == Opcode.opcx_lload) {
				if (first.operand().equals(second.operand())) {
					return new Filter();
				}
			}
			break;

		case opcx_dstore:
			if (first.opcodeClass() == Opcode.opcx_dload) {
				if (first.operand().equals(second.operand())) {
					return new Filter();
				}
			}
			break;

		case opcx_ireturn:
		case opcx_freturn:
		case opcx_areturn:
		case opcx_lreturn:
		case opcx_dreturn:
			// Replace store-return with return. Remember that upon return
			// all local variables revert to their pre-call values, so any
			// stores are destroyed.

			switch (first.opcodeClass()) {
			case opcx_istore:
			case opcx_fstore:
			case opcx_astore:
			case opcx_lstore:
			case opcx_dstore:
				return new Filter(second);
			}
			break;

		case opcx_iadd:
			// Replace ineg-iadd with isub

			if (first.opcodeClass() == Opcode.opcx_ineg) {
				return new Filter(new Instruction(Opcode.opcx_isub));
			}
			break;

		case opcx_isub:
			// Replace ineg-isub with iadd

			if (first.opcodeClass() == Opcode.opcx_ineg) {
				return new Filter(new Instruction(Opcode.opcx_iadd));
			}
			break;

		case opcx_ladd:
			// Replace lneg-ladd with lsub

			if (first.opcodeClass() == Opcode.opcx_lneg) {
				return new Filter(new Instruction(Opcode.opcx_lsub));
			}
			break;

		case opcx_lsub:
			// Replace lneg-lsub with ladd

			if (first.opcodeClass() == Opcode.opcx_lneg) {
				return new Filter(new Instruction(Opcode.opcx_ladd));
			}
			break;

		case opcx_if_icmpeq:
			// Replace ldc 0-if_icmpeq with ifeq

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() == 0) {
						return new Filter(new Instruction(Opcode.opcx_ifeq,
								second.operand()));
					}
				}
			}
			break;

		case opcx_if_icmpne:
			// Replace ldc 0-if_icmpne with ifne

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() == 0) {
						return new Filter(new Instruction(Opcode.opcx_ifne,
								second.operand()));
					}
				}
			}
			break;

		case opcx_if_icmplt:
			// Replace ldc 0-if_icmplt with iflt

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() == 0) {
						return new Filter(new Instruction(Opcode.opcx_iflt,
								second.operand()));
					}
				}
			}
			break;

		case opcx_if_icmpge:
			// Replace ldc 0-if_icmpge with ifge

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() == 0) {
						return new Filter(new Instruction(Opcode.opcx_ifge,
								second.operand()));
					}
				}
			}
			break;

		case opcx_if_icmpgt:
			// Replace ldc 0-if_icmpgt with ifgt

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() == 0) {
						return new Filter(new Instruction(Opcode.opcx_ifgt,
								second.operand()));
					}
				}
			}
			break;

		case opcx_if_icmple:
			// Replace ldc 0-if_icmple with ifle

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() == 0) {
						return new Filter(new Instruction(Opcode.opcx_ifle,
								second.operand()));
					}
				}
			}
			break;

		case opcx_if_acmpeq:
			// Replace ldc null-if_acmpeq with ifnull

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				if (first.operand() == null) {
					return new Filter(new Instruction(Opcode.opcx_ifnull,
							second.operand()));
				}
			}
			break;

		case opcx_if_acmpne:
			// Replace ldc null-if_acmpne with ifnonnull

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				if (first.operand() == null) {
					return new Filter(new Instruction(Opcode.opcx_ifnonnull,
							second.operand()));
				}
			}
			break;

		case opcx_ifeq:
			// Replace ldc 0-ifeq with goto and eliminate ldc !0-ifeq

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() == 0) {
						return new Filter(new Instruction(Opcode.opcx_goto,
								second.operand()));
					} else {
						return new Filter();
					}
				}
			}
			break;

		case opcx_ifne:
			// Replace ldc !0-ifne with goto and eliminate ldc 0-ifne

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() != 0) {
						return new Filter(new Instruction(Opcode.opcx_goto,
								second.operand()));
					} else {
						return new Filter();
					}
				}
			}
			break;

		case opcx_iflt:
			// Replace ldc <0-iflt with goto and eliminate ldc >=0-iflt

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() < 0) {
						return new Filter(new Instruction(Opcode.opcx_goto,
								second.operand()));
					} else {
						return new Filter();
					}
				}
			}
			break;

		case opcx_ifge:
			// Replace ldc >=0-ifge with goto and eliminate ldc <0-ifge

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() >= 0) {
						return new Filter(new Instruction(Opcode.opcx_goto,
								second.operand()));
					} else {
						return new Filter();
					}
				}
			}
			break;

		case opcx_ifgt:
			// Replace ldc >0-ifgt with goto and eliminate ldc <=0-ifgt

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() > 0) {
						return new Filter(new Instruction(Opcode.opcx_goto,
								second.operand()));
					} else {
						return new Filter();
					}
				}
			}
			break;

		case opcx_ifle:
			// Replace ldc <=0-ifle with goto and eliminate ldc >0-ifle

			if (first.opcodeClass() == Opcode.opcx_ldc) {
				final Object op = first.operand();
				if (op instanceof Integer) {
					if (((Integer) op).intValue() <= 0) {
						return new Filter(new Instruction(Opcode.opcx_goto,
								second.operand()));
					} else {
						return new Filter();
					}
				}
			}
			break;
		}

		switch (second.opcodeClass()) {
		// Replace store-store to same location with pop-store.

		case opcx_istore:
		case opcx_fstore:
		case opcx_astore:
		case opcx_lstore:
		case opcx_dstore:
			switch (first.opcodeClass()) {
			case opcx_istore:
			case opcx_fstore:
			case opcx_astore:
				if (first.operand().equals(second.operand())) {
					return new Filter(new Instruction(Opcode.opcx_pop), first);
				}
				break;
			case opcx_lstore:
			case opcx_dstore:
				if (first.operand().equals(second.operand())) {
					return new Filter(new Instruction(Opcode.opcx_pop2), first);
				}
				break;
			}
			break;
		}

		switch (second.opcodeClass()) {
		// Replace store-load with dup-store.
		// Replace load-load with load-dup.
		case opcx_iload:
			if (first.opcodeClass() == Opcode.opcx_istore) {
				if (first.operand().equals(second.operand())) {
					return new Filter(new Instruction(Opcode.opcx_dup), first);
				}
			}
			if (first.opcodeClass() == Opcode.opcx_iload) {
				if (first.operand().equals(second.operand())) {
					return new Filter(first, new Instruction(Opcode.opcx_dup));
				}
			}
			break;

		case opcx_fload:
			if (first.opcodeClass() == Opcode.opcx_fstore) {
				if (first.operand().equals(second.operand())) {
					return new Filter(new Instruction(Opcode.opcx_dup), first);
				}
			}
			if (first.opcodeClass() == Opcode.opcx_fload) {
				if (first.operand().equals(second.operand())) {
					return new Filter(first, new Instruction(Opcode.opcx_dup));
				}
			}
			break;

		case opcx_aload:
			if (first.opcodeClass() == Opcode.opcx_astore) {
				if (first.operand().equals(second.operand())) {
					return new Filter(new Instruction(Opcode.opcx_dup), first);
				}
			}
			if (first.opcodeClass() == Opcode.opcx_aload) {
				if (first.operand().equals(second.operand())) {
					return new Filter(first, new Instruction(Opcode.opcx_dup));
				}
			}
			break;

		case opcx_lload:
			if (first.opcodeClass() == Opcode.opcx_lstore) {
				if (first.operand().equals(second.operand())) {
					return new Filter(new Instruction(Opcode.opcx_dup2), first);
				}
			}
			if (first.opcodeClass() == Opcode.opcx_lload) {
				if (first.operand().equals(second.operand())) {
					return new Filter(first, new Instruction(Opcode.opcx_dup2));
				}
			}
			break;

		case opcx_dload:
			if (first.opcodeClass() == Opcode.opcx_dstore) {
				if (first.operand().equals(second.operand())) {
					return new Filter(new Instruction(Opcode.opcx_dup2), first);
				}
			}
			if (first.opcodeClass() == Opcode.opcx_dload) {
				if (first.operand().equals(second.operand())) {
					return new Filter(first, new Instruction(Opcode.opcx_dup2));
				}
			}
			break;
		}

		return null;
	}
}
