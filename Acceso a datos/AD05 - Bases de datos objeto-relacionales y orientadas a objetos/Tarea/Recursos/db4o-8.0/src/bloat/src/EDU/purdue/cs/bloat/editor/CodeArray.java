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

import java.util.*;

import EDU.purdue.cs.bloat.reflect.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * CodeArray converts an array of Instructions into an array of bytes suitable
 * for saving to a <tt>MethodInfo</tt> with <tt>setCode</tt>.
 * 
 * <p>
 * 
 * The byte array is returned by calling the <tt>array</tt> method.
 * 
 * <p>
 * 
 * This code assumes no branch will be longer than 65536 bytes.
 * 
 * @see Instruction
 * @see MethodInfo
 * @see MethodInfo#setCode
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class CodeArray implements InstructionVisitor, Opcode {
	public static boolean DEBUG = Boolean.getBoolean("CodeArray.DEBUG");

	private ByteCell codeTail; // Linked list of ByteCells representing code

	private int codeLength; // Number of bytes in method

	private Map branches;

	private Map longBranches;

	private Map branchInsts;

	private Map labels; // Labels mapped to their offsets

	private int lastInst; // Offset of last (most recent) instrucion

	private int maxStack; // Max stack height

	private int stackHeight; // Current stack height

	private int maxLocals; // Max number of local variables

	private ConstantPool constants;

	private MethodEditor method;

	private boolean longBranch; // Do we use long (wide) jumps?

	private List insts;

	/**
	 * Create the byte array for a method.
	 * 
	 * @param method
	 *            The method being edited.
	 * @param constants
	 *            The constant pool of the class.
	 * @param insts
	 *            A List of Instructions and Labels to convert to a byte array.
	 * @see MethodEditor
	 * @see ConstantPool
	 * @see Instruction
	 * @see Label
	 */
	public CodeArray(final MethodEditor method, final ConstantPool constants,
			final List insts) {
		this.constants = constants;
		this.method = method;
		this.insts = insts;
		this.maxStack = 0;
		this.maxLocals = 0;

		longBranch = false;
		buildCode();
	}

	/**
	 * Examine the method's Labels and Instructions. Keep track of such things
	 * as the height of the stack at each instruction and to where subroutines
	 * return. The ultimate goal is to compute the max stack height of this
	 * method. This computation is complicated by subroutines that may be
	 * invoked at a variety of stack heights.
	 */
	private void buildCode() {
		codeTail = null;
		codeLength = 0;
		branches = new HashMap();
		longBranches = new HashMap();
		branchInsts = new HashMap();
		labels = new HashMap();

		// We need at least enought locals to store the parameters
		maxLocals = method.type().stackHeight();

		if (!method.isStatic()) {
			// One more for the this pointer
			maxLocals++;
		}

		stackHeight = 0;

		final Map labelPos = new HashMap(); // Maps Labels to their code offsets
		final int[] heights = new int[insts.size()]; // Stack height at each
														// inst

		// Maps Labels that begin jsrs to their return targets. Maps ret
		// instructions to the subroutine from which they return.
		final Map retTargets = new HashMap();
		final Map retInsts = new HashMap();

		// Print the code we're dealing with
		if (CodeArray.DEBUG) {
			System.out.println("Building code for "
					+ method.declaringClass().name() + "." + method.name());
			final Iterator iter = insts.iterator();
			while (iter.hasNext()) {
				final Object o = iter.next();
				System.out.println("  " + o);
			}
		}

		// Build the bytecode array, assuming each basic block begins with
		// stack height 0. We'll fix up the heights later.
		final Iterator iter = insts.iterator();
		int i = 0; // Which instruction are we at?
		while (iter.hasNext()) {
			final Object ce = iter.next();

			if (ce instanceof Label) {
				final Label label = (Label) ce;

				// A Label starts a new basic block. Reset the stack height.

				stackHeight = 0;
				labelPos.put(label, new Integer(i));

				addLabel(label);
				heights[i++] = stackHeight;

				// If this label starts a subroutine (i.e. is the target of
				// jsr instruction), then make not of it.
				if (retTargets.containsKey(label)) {
				}

			} else if (ce instanceof Instruction) {
				final Instruction inst = (Instruction) ce;

				// Visit this instruction to compute the current stack height
				inst.visit(this);

				if (inst.isJsr()) {
					// Make sure that the jsr is not the last instruction in the
					// method. If it was, where would we return to? Make note
					// of the return target (the Label following the jsr).

					heights[i++] = stackHeight;

					Assert.isTrue(iter.hasNext(), inst
							+ " found at end of method");

					final Object x = iter.next();

					Assert.isTrue(x instanceof Label, inst
							+ " not followed by label");

					final Label sub = (Label) inst.operand();
					final Label target = (Label) x;

					// Maintain a mapping between a subroutine (the Label that
					// begins it) and all return targets.
					Set targets = (Set) retTargets.get(sub);
					if (targets == null) {
						targets = new HashSet();
						retTargets.put(sub, targets);
					}
					targets.add(target);

					stackHeight = 0;
					labelPos.put(target, new Integer(i));

					addLabel(target);
					heights[i++] = stackHeight;

				} else {
					heights[i++] = stackHeight;
				}

			} else {
				// Something bad in instruction list
				throw new IllegalArgumentException();
			}
		}

		// Sorry, but we have to make another forward pass over some of
		// the code to determine the subroutine from which a given ret
		// instruction returns.
		final Iterator subLabels = retTargets.keySet().iterator();
		while (subLabels.hasNext()) {
			final Label subLabel = (Label) subLabels.next();
			final int pos = insts.indexOf(subLabel);
			Assert.isTrue(pos != -1, "Label " + subLabel + " not found");
			boolean foundRet = false;
			final ListIterator liter = insts.listIterator(pos);
			while (liter.hasNext()) {
				final Object o = liter.next();
				if (o instanceof Instruction) {
					final Instruction inst = (Instruction) o;
					if (inst.isRet()) {
						retInsts.put(inst, subLabel);
						foundRet = true;
						break;
					}
				}
			}
			Assert.isTrue(foundRet, "No ret for subroutine " + subLabel);
		}

		if (CodeArray.DEBUG) {
			// Print subroutine to return target mapping
			System.out.println("Subroutines and return targets:");
			final Iterator subs = retTargets.keySet().iterator();
			while (subs.hasNext()) {
				final Label sub = (Label) subs.next();
				System.out.print("  " + sub + ": ");
				final Set s = (Set) retTargets.get(sub);
				Assert.isTrue(s != null, "No return targets for " + sub);
				final Iterator rets = s.iterator();
				while (rets.hasNext()) {
					final Label ret = (Label) rets.next();
					System.out.print(ret.toString());
					if (rets.hasNext()) {
						System.out.print(", ");
					}
				}
				System.out.println("");
			}
		}

		// Fix up the stack heights by propagating the heights at each catch
		// and each branch to their targets. Visit the blocks
		// depth-first. Remember that the classfile requires the maximum
		// stack height. I would assume that is why we do all of this
		// stack height calculation stuff.

		final Set visited = new HashSet(); // Labels that we've seen
		final Stack stack = new Stack(); // Stack of HeightRecords
		Label label;

		// Start with the first Label
		if (insts.size() > 0) {
			Assert.isTrue((insts.get(0) instanceof Label),
					"A method must begin with a Label, not " + insts.get(0));
			label = (Label) insts.get(0);
			visited.add(label);
			stack.push(new HeightRecord(label, 0));
		}

		// Also examine each exception handler. Recall that the exception
		// object is implicitly pushed on the stack. So, the HeightRecord
		// initially has height 1.
		final Iterator e = method.tryCatches().iterator();
		while (e.hasNext()) {
			final TryCatch tc = (TryCatch) e.next();
			visited.add(tc.handler());
			stack.push(new HeightRecord(tc.handler(), 1));
		}

		// Examine the HeightRecords on the stack. Make sure that the
		// stack height has not exceeded 256. If the height at a given
		// label has changed since we last visited it, then propagate this
		// change to labels following the block begun by the label in
		// question.
		while (!stack.isEmpty()) {
			final HeightRecord h = (HeightRecord) stack.pop();

			Assert.isTrue(h.height < 256, "Stack height of " + h.height
					+ " reached. " + h.label + " (" + labelPos.get(h.label)
					+ ")");

			if (ClassEditor.DEBUG || CodeArray.DEBUG) {
				System.out.println(h.label + " has height " + h.height);
			}

			Integer labelIndex = (Integer) labelPos.get(h.label);
			Assert.isTrue(labelIndex != null, "Index of " + h.label
					+ " not found");

			final int start = labelIndex.intValue();
			int diff = h.height - heights[start];

			// Propagate the change in height to the next branch.
			// Then push the branch targets.
			if (ClassEditor.DEBUG) {
				/*
				 * System.out.println(" " + h.label + ": change " +
				 * heights[start] + " to " + h.height);
				 */
			}

			heights[start] = h.height;

			final ListIterator blockIter = insts.listIterator(start + 1);
			i = start;

			// Examine the instructions following the label
			while (blockIter.hasNext()) {
				final Object ce = blockIter.next();

				i++;

				if (ce instanceof Instruction) {
					final Instruction inst = (Instruction) ce;

					if (inst.isReturn() || inst.isThrow()) {
						// The method terminates. The stack is popped empty.
						heights[i] = 0;

						if (ClassEditor.DEBUG || CodeArray.DEBUG) {
							System.out.println("  " + heights[i] + ") " + inst);
						}

						// Consider next HeightRecord on stack.
						break;

					} else if (inst.isConditionalJump()) {
						// If the stack height at this Label has changed since
						// we
						// last saw it or if we have not processed the target of
						// the jump, add a new HeightRecord for the target
						// Label.

						heights[i] += diff;

						if (ClassEditor.DEBUG || CodeArray.DEBUG) {
							System.out.println("  " + heights[i] + ") " + inst);
						}

						label = (Label) inst.operand();

						if ((diff > 0) || !visited.contains(label)) {
							visited.add(label);
							stack.push(new HeightRecord(label, heights[i]));
						}

						// Fall through. That is, process the instruction after
						// the conditional jump. Remember that the code is in
						// trace order so the false block (which is the next
						// block
						// in a depth first traversal) follows. The height of
						// the
						// stack won't change when we fall through.

					} else if (inst.isGoto() || inst.isJsr()) {
						// Once again, if we have already visited the target
						// block, add a HeightRecord to the stack.

						heights[i] += diff;

						if (ClassEditor.DEBUG || CodeArray.DEBUG) {
							System.out.println("  " + heights[i] + ") " + inst);
						}

						label = (Label) inst.operand();

						if ((diff > 0) || !visited.contains(label)) {
							visited.add(label);
							stack.push(new HeightRecord(label, heights[i]));
						}

						// Deal with the next HeightRecord on the stack.
						break;

					} else if (inst.isRet()) {
						// Process any unvisited return targets (of the current
						// jsr) or those whose current height is less than the
						// height at this return instruction.

						heights[i] += diff;

						if (ClassEditor.DEBUG || CodeArray.DEBUG) {
							System.out.println("  " + heights[i] + ") " + inst);
						}

						final Label subLabel = (Label) retInsts.get(inst);
						Assert.isTrue(subLabel != null,
								"Not inside a subroutine at " + inst);

						final Set targets = (Set) retTargets.get(subLabel);
						Assert.isTrue(targets != null, "Subroutine " + subLabel
								+ " has no return targets");

						if (ClassEditor.DEBUG || CodeArray.DEBUG) {
							System.out.println("    Returning from: "
									+ subLabel);
						}

						final Iterator retIter = targets.iterator();

						while (retIter.hasNext()) {
							label = (Label) retIter.next();

							labelIndex = (Integer) labelPos.get(label);
							Assert.isTrue(labelIndex != null, "Index of "
									+ label + " not found");

							final int idx = labelIndex.intValue();

							if ((heights[idx] < heights[i])
									|| !visited.contains(label)) {
								visited.add(label);
								stack.push(new HeightRecord(label, heights[i]));
							}
						}

						break;

					} else if (inst.isSwitch()) {
						// Visit each unvisited switch target if it increases
						// the
						// stack height

						// If the height at this Label has changed since it was
						// last visited, process each target Label. Otherwise,
						// only process unvisited Labels.

						heights[i] += diff;

						if (ClassEditor.DEBUG || CodeArray.DEBUG) {
							System.out.println("  " + heights[i] + ") " + inst);
						}

						// A switch.
						final Switch sw = (Switch) inst.operand();

						label = sw.defaultTarget();

						if ((diff > 0) || !visited.contains(label)) {
							visited.add(label);
							stack.push(new HeightRecord(label, heights[i]));
						}

						final Label[] targets = sw.targets();

						for (int j = 0; j < targets.length; j++) {
							label = targets[j];
							if ((diff > 0) || !visited.contains(label)) {
								visited.add(label);
								stack.push(new HeightRecord(label, heights[i]));
							}
						}

						break;

					} else {
						// No other blocks to visit. Just adjust the height.

						heights[i] += diff;

						if (ClassEditor.DEBUG || CodeArray.DEBUG) {
							System.out.println("  " + heights[i] + ") " + inst);
						}
					}

				} else if (ce instanceof Label) {
					// We've hit the next block. Update the stack height.
					// Process this next block if has not been visited or its
					// current height is different from the previous
					// instruction.

					label = (Label) ce;

					diff = heights[i - 1] - heights[i];

					if ((diff > 0) || !visited.contains(label)) {
						visited.add(label);
						heights[i] = heights[i - 1];
					}

					if (ClassEditor.DEBUG || CodeArray.DEBUG) {
						System.out.println("  " + heights[i] + ") " + label);
					}
				}
			}
		}

		// Find the maximum stack height.
		maxStack = 0;

		for (i = 0; i < heights.length; i++) {
			final int h = heights[i];

			if (h > maxStack) {
				maxStack = h;
			}
		}
	}

	/**
	 * Returns the maximum number of local variables used by this method.
	 */
	public int maxLocals() {
		return maxLocals;
	}

	/**
	 * Returns the maximum height of the stack at any point in this method.
	 */
	public int maxStack() {
		return maxStack;
	}

	/**
	 * Returns the index in the byte array of the given label.
	 */
	public int labelIndex(final Label label) {
		final Integer i = (Integer) labels.get(label);

		if (i != null) {
			return i.intValue();
		}

		throw new IllegalArgumentException("Label " + label + " not found");
	}

	/**
	 * Returns the byte array after resolving branches.
	 */
	public byte[] array() {
		if (branches.size() > 0) {
			if (!longBranch && (codeLength >= 0x10000)) {
				longBranch = true;
				buildCode();
			}
		}

		final byte[] c = new byte[codeLength];
		int i = codeLength;

		for (ByteCell p = codeTail; p != null; p = p.prev) {
			c[--i] = p.value;
		}

		Iterator e;

		e = branches.keySet().iterator();

		while (e.hasNext()) {
			final Integer branch = (Integer) e.next();
			final int branchIndex = branch.intValue();

			final Integer inst = (Integer) branchInsts.get(branch);
			final int instIndex = inst.intValue();

			final Label label = (Label) branches.get(branch);
			final Integer target = (Integer) labels.get(label);

			Assert.isTrue(target != null, "Index of " + label + " not found");

			int diff = target.intValue() - instIndex;

			Assert.isTrue((-diff < 0x10000) && (diff < 0x10000),
					"Branch offset too large: " + diff);

			c[branchIndex] = (byte) ((diff >>> 8) & 0xff);
			c[branchIndex + 1] = (byte) (diff & 0xff);
		}

		e = longBranches.keySet().iterator();

		while (e.hasNext()) {
			final Integer branch = (Integer) e.next();
			final int branchIndex = branch.intValue();

			final Integer inst = (Integer) branchInsts.get(branch);
			final int instIndex = inst.intValue();

			final Label label = (Label) longBranches.get(branch);
			final Integer target = (Integer) labels.get(label);

			final int diff = target.intValue() - instIndex;

			c[branchIndex] = (byte) ((diff >>> 24) & 0xff);
			c[branchIndex + 1] = (byte) ((diff >>> 16) & 0xff);
			c[branchIndex + 2] = (byte) ((diff >>> 8) & 0xff);
			c[branchIndex + 3] = (byte) (diff & 0xff);
		}

		return c;
	}

	/**
	 * Makes note of a label.
	 */
	public void addLabel(final Label label) {
		if (ClassEditor.DEBUG || CodeArray.DEBUG) {
			System.out.println("    " + codeLength + ": " + "label " + label);
		}

		labels.put(label, new Integer(codeLength));
	}

	/**
	 * Adds a 4-byte branch to a given label. The branch is from the index of
	 * the last opcode added.
	 */
	public void addLongBranch(final Label label) {
		if (ClassEditor.DEBUG || CodeArray.DEBUG) {
			System.out.println("    " + codeLength + ": " + "long branch to "
					+ label);
		}

		branchInsts.put(new Integer(codeLength), new Integer(lastInst));
		longBranches.put(new Integer(codeLength), label);
		addByte(0);
		addByte(0);
		addByte(0);
		addByte(0);
	}

	/**
	 * Adds a 2-byte branch to a given label. The branch is from the index of
	 * the last opcode added.
	 */
	public void addBranch(final Label label) {
		if (ClassEditor.DEBUG || CodeArray.DEBUG) {
			System.out.println("    " + codeLength + ": " + "branch to "
					+ label);
		}

		branchInsts.put(new Integer(codeLength), new Integer(lastInst));
		branches.put(new Integer(codeLength), label);
		addByte(0);
		addByte(0);
	}

	/**
	 * Add an opcode to the byte array, adjusting for 4-byte alignment for
	 * switch instructions and saving the index for calculating branches.
	 * 
	 * @param opcode
	 *            The opcode.
	 * @see Opcode
	 */
	public void addOpcode(final int opcode) {
		if (ClassEditor.DEBUG || CodeArray.DEBUG) {
			System.out.println("    " + codeLength + ": " + "opcode "
					+ Opcode.opcNames[opcode]);
		}

		lastInst = codeLength;

		addByte(opcode);

		if ((opcode == Opcode.opc_tableswitch)
				|| (opcode == Opcode.opc_lookupswitch)) {
			// Switch instructions are followed by padding so that table
			// starts on a 4-byte boundary.
			while (codeLength % 4 != 0) {
				addByte(0);
			}
		}
	}

	/**
	 * Adds a single byte to the array.
	 */
	public void addByte(final int i) {
		if (ClassEditor.DEBUG) {
			System.out.println("    " + codeLength + ": " + "byte " + i);
		}

		// The bytecode array is represented as a linked list of
		// ByteCells. This method creates a new ByteCell and appends it
		// to the linked list.

		final ByteCell p = new ByteCell();
		p.value = (byte) (i & 0xff);
		p.prev = codeTail;
		codeTail = p;
		codeLength++;
	}

	/**
	 * Adds a 2-byte short to the array, high byte first.
	 */
	public void addShort(final int i) {
		if (ClassEditor.DEBUG) {
			System.out.println("    " + codeLength + ": " + "short " + i);
		}

		addByte(i >>> 8);
		addByte(i);
	}

	/**
	 * Adds a 4-byte int to the array, high byte first.
	 */
	public void addInt(final int i) {
		if (ClassEditor.DEBUG) {
			System.out.println("    " + codeLength + ": " + "int " + i);
		}

		addByte(i >>> 24);
		addByte(i >>> 16);
		addByte(i >>> 8);
		addByte(i);
	}

	public void visit_nop(final Instruction inst) {
		// If it must have been put there for a reason.
		addOpcode(Opcode.opc_nop);
		stackHeight += 0;
	}

	/*
	 * Does pretty much what you'd expect. Examines the instruction's operand to
	 * determine if one of the special constant opcodes (e.g. iconst_1) can be
	 * used. Adds the most appropriate instruction.
	 */
	public void visit_ldc(final Instruction inst) {
		final Object operand = inst.operand();

		if (operand == null) {
			addOpcode(Opcode.opc_aconst_null);
			stackHeight++;

		} else if (operand instanceof Integer) {
			final int v = ((Integer) operand).intValue();

			switch (v) {
			case -1:
				addOpcode(Opcode.opc_iconst_m1);
				break;
			case 0:
				addOpcode(Opcode.opc_iconst_0);
				break;
			case 1:
				addOpcode(Opcode.opc_iconst_1);
				break;
			case 2:
				addOpcode(Opcode.opc_iconst_2);
				break;
			case 3:
				addOpcode(Opcode.opc_iconst_3);
				break;
			case 4:
				addOpcode(Opcode.opc_iconst_4);
				break;
			case 5:
				addOpcode(Opcode.opc_iconst_5);
				break;
			default: {
				if ((byte) v == v) {
					addOpcode(Opcode.opc_bipush);
					addByte(v);
				} else if ((short) v == v) {
					addOpcode(Opcode.opc_sipush);
					addShort(v);
				} else {
					final int index = constants.addConstant(Constant.INTEGER,
							operand);
					if (index < 256) {
						addOpcode(Opcode.opc_ldc);
						addByte(index);
					} else {
						addOpcode(Opcode.opc_ldc_w);
						addShort(index);
					}
				}
				break;
			}
			}

			stackHeight++;

		} else if (operand instanceof Float) {
			final float v = ((Float) operand).floatValue();

			if (v == 0.0F) {
				addOpcode(Opcode.opc_fconst_0);
			} else if (v == 1.0F) {
				addOpcode(Opcode.opc_fconst_1);
			} else if (v == 2.0F) {
				addOpcode(Opcode.opc_fconst_2);
			} else {
				final int index = constants
						.addConstant(Constant.FLOAT, operand);
				if (index < 256) {
					addOpcode(Opcode.opc_ldc);
					addByte(index);
				} else {
					addOpcode(Opcode.opc_ldc_w);
					addShort(index);
				}
			}

			stackHeight++;

		} else if (operand instanceof Long) {
			final long v = ((Long) operand).longValue();

			if (v == 0) {
				addOpcode(Opcode.opc_lconst_0);
			} else if (v == 1) {
				addOpcode(Opcode.opc_lconst_1);
			} else {
				final int index = constants.addConstant(Constant.LONG, operand);
				addOpcode(Opcode.opc_ldc2_w);
				addShort(index);
			}

			stackHeight += 2;

		} else if (operand instanceof Double) {
			final double v = ((Double) operand).doubleValue();

			if (v == 0.0) {
				addOpcode(Opcode.opc_dconst_0);
			} else if (v == 1.0) {
				addOpcode(Opcode.opc_dconst_1);
			} else {
				final int index = constants.addConstant(Constant.DOUBLE,
						operand);
				addOpcode(Opcode.opc_ldc2_w);
				addShort(index);
			}

			stackHeight += 2;

		} else if (operand instanceof String) {
			int index = constants.addConstant(Constant.STRING, operand);
			createLDC(index);
		} else if (operand instanceof Type) {
			// JDK5 class literal
			int index = constants.addConstant(Constant.CLASS, operand);
			createLDC(index);
		} else {
			throw new RuntimeException();
		}
	}

	private void createLDC(int index) {

		if (index < 256) {
			addOpcode(Opcode.opc_ldc);
			addByte(index);
		} else {
			addOpcode(Opcode.opc_ldc_w);
			addShort(index);
		}

		stackHeight++;
	}

	/*
	 * Tries to use the shorter iload_x instructions.
	 */
	public void visit_iload(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 1 > maxLocals) {
			maxLocals = index + 1;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_iload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_iload);
				addShort(index);
			}
			stackHeight++;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_iload_0);
			break;
		case 1:
			addOpcode(Opcode.opc_iload_1);
			break;
		case 2:
			addOpcode(Opcode.opc_iload_2);
			break;
		case 3:
			addOpcode(Opcode.opc_iload_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_iload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_iload);
				addShort(index);
			}
			break;
		}

		stackHeight++;
	}

	public void visit_lload(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 2 > maxLocals) {
			maxLocals = index + 2;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_lload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_lload);
				addShort(index);
			}
			stackHeight++;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_lload_0);
			break;
		case 1:
			addOpcode(Opcode.opc_lload_1);
			break;
		case 2:
			addOpcode(Opcode.opc_lload_2);
			break;
		case 3:
			addOpcode(Opcode.opc_lload_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_lload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_lload);
				addShort(index);
			}
			break;
		}

		stackHeight += 2;
	}

	public void visit_fload(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 1 > maxLocals) {
			maxLocals = index + 1;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_fload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_fload);
				addShort(index);
			}

			stackHeight++;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_fload_0);
			break;
		case 1:
			addOpcode(Opcode.opc_fload_1);
			break;
		case 2:
			addOpcode(Opcode.opc_fload_2);
			break;
		case 3:
			addOpcode(Opcode.opc_fload_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_fload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_fload);
				addShort(index);
			}
			break;
		}

		stackHeight++;
	}

	public void visit_dload(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 2 > maxLocals) {
			maxLocals = index + 2;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_dload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_dload);
				addShort(index);
			}
			stackHeight += 2;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_dload_0);
			break;
		case 1:
			addOpcode(Opcode.opc_dload_1);
			break;
		case 2:
			addOpcode(Opcode.opc_dload_2);
			break;
		case 3:
			addOpcode(Opcode.opc_dload_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_dload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_dload);
				addShort(index);
			}
			break;
		}

		stackHeight += 2;
	}

	public void visit_aload(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 1 > maxLocals) {
			maxLocals = index + 1;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_aload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_aload);
				addShort(index);
			}
			stackHeight++;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_aload_0);
			break;
		case 1:
			addOpcode(Opcode.opc_aload_1);
			break;
		case 2:
			addOpcode(Opcode.opc_aload_2);
			break;
		case 3:
			addOpcode(Opcode.opc_aload_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_aload);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_aload);
				addShort(index);
			}
			break;
		}

		stackHeight++;
	}

	/**
	 * Pops an item off the stack.
	 */
	public void visit_iaload(final Instruction inst) {
		addOpcode(Opcode.opc_iaload);
		stackHeight--;
	}

	public void visit_laload(final Instruction inst) {
		addOpcode(Opcode.opc_laload);
		stackHeight += 0;
	}

	public void visit_faload(final Instruction inst) {
		addOpcode(Opcode.opc_faload);
		stackHeight--;
	}

	public void visit_daload(final Instruction inst) {
		addOpcode(Opcode.opc_daload);
		stackHeight += 0;
	}

	public void visit_aaload(final Instruction inst) {
		addOpcode(Opcode.opc_aaload);
		stackHeight--;
	}

	public void visit_baload(final Instruction inst) {
		addOpcode(Opcode.opc_baload);
		stackHeight--;
	}

	public void visit_caload(final Instruction inst) {
		addOpcode(Opcode.opc_caload);
		stackHeight--;
	}

	public void visit_saload(final Instruction inst) {
		addOpcode(Opcode.opc_saload);
		stackHeight--;
	}

	/*
	 * Try to take advantage of smaller opcodes (e.g. istore_1).
	 */
	public void visit_istore(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 1 > maxLocals) {
			maxLocals = index + 1;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_istore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_istore);
				addShort(index);
			}
			stackHeight--;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_istore_0);
			break;
		case 1:
			addOpcode(Opcode.opc_istore_1);
			break;
		case 2:
			addOpcode(Opcode.opc_istore_2);
			break;
		case 3:
			addOpcode(Opcode.opc_istore_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_istore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_istore);
				addShort(index);
			}
			break;
		}

		stackHeight--;
	}

	public void visit_lstore(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 2 > maxLocals) {
			maxLocals = index + 2;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_lstore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_lstore);
				addShort(index);
			}
			stackHeight -= 2;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_lstore_0);
			break;
		case 1:
			addOpcode(Opcode.opc_lstore_1);
			break;
		case 2:
			addOpcode(Opcode.opc_lstore_2);
			break;
		case 3:
			addOpcode(Opcode.opc_lstore_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_lstore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_lstore);
				addShort(index);
			}
			break;
		}

		stackHeight -= 2;
	}

	public void visit_fstore(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 1 > maxLocals) {
			maxLocals = index + 1;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_fstore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_fstore);
				addShort(index);
			}
			stackHeight--;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_fstore_0);
			break;
		case 1:
			addOpcode(Opcode.opc_fstore_1);
			break;
		case 2:
			addOpcode(Opcode.opc_fstore_2);
			break;
		case 3:
			addOpcode(Opcode.opc_fstore_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_fstore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_fstore);
				addShort(index);
			}
			break;
		}

		stackHeight--;
	}

	public void visit_dstore(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 2 > maxLocals) {
			maxLocals = index + 2;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_dstore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_dstore);
				addShort(index);
			}
			stackHeight -= 2;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_dstore_0);
			break;
		case 1:
			addOpcode(Opcode.opc_dstore_1);
			break;
		case 2:
			addOpcode(Opcode.opc_dstore_2);
			break;
		case 3:
			addOpcode(Opcode.opc_dstore_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_dstore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_dstore);
				addShort(index);
			}
			break;
		}

		stackHeight -= 2;
	}

	public void visit_astore(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 1 > maxLocals) {
			maxLocals = index + 1;
		}

		if (inst.useSlow()) {
			if (index < 256) {
				addOpcode(Opcode.opc_astore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_astore);
				addShort(index);
			}
			stackHeight--;
			return;
		}

		switch (index) {
		case 0:
			addOpcode(Opcode.opc_astore_0);
			break;
		case 1:
			addOpcode(Opcode.opc_astore_1);
			break;
		case 2:
			addOpcode(Opcode.opc_astore_2);
			break;
		case 3:
			addOpcode(Opcode.opc_astore_3);
			break;
		default:
			if (index < 256) {
				addOpcode(Opcode.opc_astore);
				addByte(index);
			} else {
				addOpcode(Opcode.opc_wide);
				addByte(Opcode.opc_astore);
				addShort(index);
			}
			break;
		}

		stackHeight--;
	}

	/*
	 * Store into an array. Pop 3+ items off the stack.
	 */
	public void visit_iastore(final Instruction inst) {
		addOpcode(Opcode.opc_iastore);
		stackHeight -= 3;
	}

	public void visit_lastore(final Instruction inst) {
		addOpcode(Opcode.opc_lastore);
		stackHeight -= 4;
	}

	public void visit_fastore(final Instruction inst) {
		addOpcode(Opcode.opc_fastore);
		stackHeight -= 3;
	}

	public void visit_dastore(final Instruction inst) {
		addOpcode(Opcode.opc_dastore);
		stackHeight -= 4;
	}

	public void visit_aastore(final Instruction inst) {
		addOpcode(Opcode.opc_aastore);
		stackHeight -= 3;
	}

	public void visit_bastore(final Instruction inst) {
		addOpcode(Opcode.opc_bastore);
		stackHeight -= 3;
	}

	public void visit_castore(final Instruction inst) {
		addOpcode(Opcode.opc_castore);
		stackHeight -= 3;
	}

	public void visit_sastore(final Instruction inst) {
		addOpcode(Opcode.opc_sastore);
		stackHeight -= 3;
	}

	public void visit_pop(final Instruction inst) {
		addOpcode(Opcode.opc_pop);
		stackHeight--;
	}

	public void visit_pop2(final Instruction inst) {
		addOpcode(Opcode.opc_pop2);
		stackHeight -= 2;
	}

	public void visit_dup(final Instruction inst) {
		addOpcode(Opcode.opc_dup);
		stackHeight++;
	}

	public void visit_dup_x1(final Instruction inst) {
		addOpcode(Opcode.opc_dup_x1);
		stackHeight++;
	}

	public void visit_dup_x2(final Instruction inst) {
		addOpcode(Opcode.opc_dup_x2);
		stackHeight++;
	}

	public void visit_dup2(final Instruction inst) {
		addOpcode(Opcode.opc_dup2);
		stackHeight += 2;
	}

	public void visit_dup2_x1(final Instruction inst) {
		addOpcode(Opcode.opc_dup2_x1);
		stackHeight += 2;
	}

	public void visit_dup2_x2(final Instruction inst) {
		addOpcode(Opcode.opc_dup2_x2);
		stackHeight += 2;
	}

	public void visit_swap(final Instruction inst) {
		addOpcode(Opcode.opc_swap);
	}

	public void visit_iadd(final Instruction inst) {
		addOpcode(Opcode.opc_iadd);
		stackHeight--;
	}

	public void visit_ladd(final Instruction inst) {
		addOpcode(Opcode.opc_ladd);
		stackHeight -= 2;
	}

	public void visit_fadd(final Instruction inst) {
		addOpcode(Opcode.opc_fadd);
		stackHeight--;
	}

	public void visit_dadd(final Instruction inst) {
		addOpcode(Opcode.opc_dadd);
		stackHeight -= 2;
	}

	public void visit_isub(final Instruction inst) {
		addOpcode(Opcode.opc_isub);
		stackHeight--;
	}

	public void visit_lsub(final Instruction inst) {
		addOpcode(Opcode.opc_lsub);
		stackHeight -= 2;
	}

	public void visit_fsub(final Instruction inst) {
		addOpcode(Opcode.opc_fsub);
		stackHeight--;
	}

	public void visit_dsub(final Instruction inst) {
		addOpcode(Opcode.opc_dsub);
		stackHeight -= 2;
	}

	public void visit_imul(final Instruction inst) {
		addOpcode(Opcode.opc_imul);
		stackHeight--;
	}

	public void visit_lmul(final Instruction inst) {
		addOpcode(Opcode.opc_lmul);
		stackHeight -= 2;
	}

	public void visit_fmul(final Instruction inst) {
		addOpcode(Opcode.opc_fmul);
		stackHeight--;
	}

	public void visit_dmul(final Instruction inst) {
		addOpcode(Opcode.opc_dmul);
		stackHeight -= 2;
	}

	public void visit_idiv(final Instruction inst) {
		addOpcode(Opcode.opc_idiv);
		stackHeight--;
	}

	public void visit_ldiv(final Instruction inst) {
		addOpcode(Opcode.opc_ldiv);
		stackHeight -= 2;
	}

	public void visit_fdiv(final Instruction inst) {
		addOpcode(Opcode.opc_fdiv);
		stackHeight--;
	}

	public void visit_ddiv(final Instruction inst) {
		addOpcode(Opcode.opc_ddiv);
		stackHeight -= 2;
	}

	public void visit_irem(final Instruction inst) {
		addOpcode(Opcode.opc_irem);
		stackHeight--;
	}

	public void visit_lrem(final Instruction inst) {
		addOpcode(Opcode.opc_lrem);
		stackHeight -= 2;
	}

	public void visit_frem(final Instruction inst) {
		addOpcode(Opcode.opc_frem);
		stackHeight--;
	}

	public void visit_drem(final Instruction inst) {
		addOpcode(Opcode.opc_drem);
		stackHeight -= 2;
	}

	public void visit_ineg(final Instruction inst) {
		addOpcode(Opcode.opc_ineg);
		stackHeight += 0;
	}

	public void visit_lneg(final Instruction inst) {
		addOpcode(Opcode.opc_lneg);
		stackHeight += 0;
	}

	public void visit_fneg(final Instruction inst) {
		addOpcode(Opcode.opc_fneg);
		stackHeight += 0;
	}

	public void visit_dneg(final Instruction inst) {
		addOpcode(Opcode.opc_dneg);
		stackHeight += 0;
	}

	public void visit_ishl(final Instruction inst) {
		addOpcode(Opcode.opc_ishl);
		stackHeight--;
	}

	public void visit_lshl(final Instruction inst) {
		addOpcode(Opcode.opc_lshl);
		stackHeight--;
	}

	public void visit_ishr(final Instruction inst) {
		addOpcode(Opcode.opc_ishr);
		stackHeight--;
	}

	public void visit_lshr(final Instruction inst) {
		addOpcode(Opcode.opc_lshr);
		stackHeight--;
	}

	public void visit_iushr(final Instruction inst) {
		addOpcode(Opcode.opc_iushr);
		stackHeight--;
	}

	public void visit_lushr(final Instruction inst) {
		addOpcode(Opcode.opc_lushr);
		stackHeight--;
	}

	public void visit_iand(final Instruction inst) {
		addOpcode(Opcode.opc_iand);
		stackHeight--;
	}

	public void visit_land(final Instruction inst) {
		addOpcode(Opcode.opc_land);
		stackHeight -= 2;
	}

	public void visit_ior(final Instruction inst) {
		addOpcode(Opcode.opc_ior);
		stackHeight--;
	}

	public void visit_lor(final Instruction inst) {
		addOpcode(Opcode.opc_lor);
		stackHeight -= 2;
	}

	public void visit_ixor(final Instruction inst) {
		addOpcode(Opcode.opc_ixor);
		stackHeight--;
	}

	public void visit_lxor(final Instruction inst) {
		addOpcode(Opcode.opc_lxor);
		stackHeight -= 2;
	}

	public void visit_iinc(final Instruction inst) {
		final IncOperand operand = (IncOperand) inst.operand();

		final int index = operand.var().index();

		if (index + 1 > maxLocals) {
			maxLocals = index + 1;
		}

		final int incr = operand.incr();

		if ((index < 256) && ((byte) incr == incr)) {
			addOpcode(Opcode.opc_iinc);
			addByte(index);
			addByte(incr);
		} else {
			addOpcode(Opcode.opc_wide);
			addByte(Opcode.opc_iinc);
			addShort(index);
			addShort(incr);
		}

		stackHeight += 0;
	}

	public void visit_i2l(final Instruction inst) {
		addOpcode(Opcode.opc_i2l);
		stackHeight++;
	}

	public void visit_i2f(final Instruction inst) {
		addOpcode(Opcode.opc_i2f);
		stackHeight += 0;
	}

	public void visit_i2d(final Instruction inst) {
		addOpcode(Opcode.opc_i2d);
		stackHeight++;
	}

	public void visit_l2i(final Instruction inst) {
		addOpcode(Opcode.opc_l2i);
		stackHeight--;
	}

	public void visit_l2f(final Instruction inst) {
		addOpcode(Opcode.opc_l2f);
		stackHeight--;
	}

	public void visit_l2d(final Instruction inst) {
		addOpcode(Opcode.opc_l2d);
		stackHeight += 0;
	}

	public void visit_f2i(final Instruction inst) {
		addOpcode(Opcode.opc_f2i);
		stackHeight += 0;
	}

	public void visit_f2l(final Instruction inst) {
		addOpcode(Opcode.opc_f2l);
		stackHeight++;
	}

	public void visit_f2d(final Instruction inst) {
		addOpcode(Opcode.opc_f2d);
		stackHeight++;
	}

	public void visit_d2i(final Instruction inst) {
		addOpcode(Opcode.opc_d2i);
		stackHeight--;
	}

	public void visit_d2l(final Instruction inst) {
		addOpcode(Opcode.opc_d2l);
		stackHeight += 0;
	}

	public void visit_d2f(final Instruction inst) {
		addOpcode(Opcode.opc_d2f);
		stackHeight--;
	}

	public void visit_i2b(final Instruction inst) {
		addOpcode(Opcode.opc_i2b);
		stackHeight += 0;
	}

	public void visit_i2c(final Instruction inst) {
		addOpcode(Opcode.opc_i2c);
		stackHeight += 0;
	}

	public void visit_i2s(final Instruction inst) {
		addOpcode(Opcode.opc_i2s);
		stackHeight += 0;
	}

	public void visit_lcmp(final Instruction inst) {
		addOpcode(Opcode.opc_lcmp);
		stackHeight -= 3;
	}

	public void visit_fcmpl(final Instruction inst) {
		addOpcode(Opcode.opc_fcmpl);
		stackHeight--;
	}

	public void visit_fcmpg(final Instruction inst) {
		addOpcode(Opcode.opc_fcmpg);
		stackHeight--;
	}

	public void visit_dcmpl(final Instruction inst) {
		addOpcode(Opcode.opc_dcmpl);
		stackHeight -= 3;
	}

	public void visit_dcmpg(final Instruction inst) {
		addOpcode(Opcode.opc_dcmpg);
		stackHeight -= 3;
	}

	/*
	 * Handle long branches.
	 */
	public void visit_ifeq(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_ifne);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_ifeq);
			addBranch((Label) inst.operand());
		}

		stackHeight--;
	}

	public void visit_ifne(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_ifeq);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_ifne);
			addBranch((Label) inst.operand());
		}

		stackHeight--;
	}

	public void visit_iflt(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_ifge);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_iflt);
			addBranch((Label) inst.operand());
		}

		stackHeight--;
	}

	public void visit_ifge(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_iflt);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_ifge);
			addBranch((Label) inst.operand());
		}

		stackHeight--;
	}

	public void visit_ifgt(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_ifle);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_ifgt);
			addBranch((Label) inst.operand());
		}

		stackHeight--;
	}

	public void visit_ifle(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_ifgt);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_ifle);
			addBranch((Label) inst.operand());
		}

		stackHeight--;
	}

	public void visit_if_icmpeq(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_if_icmpne);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_if_icmpeq);
			addBranch((Label) inst.operand());
		}

		stackHeight -= 2;
	}

	public void visit_if_icmpne(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_if_icmpeq);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_if_icmpne);
			addBranch((Label) inst.operand());
		}

		stackHeight -= 2;
	}

	public void visit_if_icmplt(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_if_icmpge);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_if_icmplt);
			addBranch((Label) inst.operand());
		}

		stackHeight -= 2;
	}

	public void visit_if_icmpge(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_if_icmplt);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_if_icmpge);
			addBranch((Label) inst.operand());
		}

		stackHeight -= 2;
	}

	public void visit_if_icmpgt(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_if_icmple);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_if_icmpgt);
			addBranch((Label) inst.operand());
		}

		stackHeight -= 2;
	}

	public void visit_if_icmple(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_if_icmpgt);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_if_icmple);
			addBranch((Label) inst.operand());
		}

		stackHeight -= 2;
	}

	public void visit_if_acmpeq(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_if_acmpne);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_if_acmpeq);
			addBranch((Label) inst.operand());
		}

		stackHeight -= 2;
	}

	public void visit_if_acmpne(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_if_acmpeq);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_if_acmpne);
			addBranch((Label) inst.operand());
		}

		stackHeight -= 2;
	}

	public void visit_goto(final Instruction inst) {
		if (longBranch) {
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
		} else {
			addOpcode(Opcode.opc_goto);
			addBranch((Label) inst.operand());
		}

		stackHeight += 0;
	}

	public void visit_jsr(final Instruction inst) {
		if (longBranch) {
			addOpcode(Opcode.opc_jsr_w);
			addLongBranch((Label) inst.operand());
		} else {
			addOpcode(Opcode.opc_jsr);
			addBranch((Label) inst.operand());
		}

		stackHeight++;
	}

	public void visit_ret(final Instruction inst) {
		final int index = ((LocalVariable) inst.operand()).index();

		if (index + 1 > maxLocals) {
			maxLocals = index + 1;
		}

		if (index < 256) {
			addOpcode(Opcode.opc_ret);
			addByte(index);
		} else {
			addOpcode(Opcode.opc_wide);
			addByte(Opcode.opc_ret);
			addShort(index);
		}

		stackHeight += 0;
	}

	public void visit_switch(final Instruction inst) {
		final Switch sw = (Switch) inst.operand();

		final int[] values = sw.values();
		final Label[] targets = sw.targets();

		if (values.length == 0) {
			if (longBranch) {
				addOpcode(Opcode.opc_pop); // Pop switch "index" off stack
				addOpcode(Opcode.opc_goto_w);
				addLongBranch(sw.defaultTarget());
			} else {
				addOpcode(Opcode.opc_pop); // Pop switch "index" off stack
				addOpcode(Opcode.opc_goto);
				addBranch(sw.defaultTarget());
			}
		} else if (sw.hasContiguousValues()) {
			addOpcode(Opcode.opc_tableswitch);
			addLongBranch(sw.defaultTarget());

			addInt(values[0]);
			addInt(values[values.length - 1]);

			for (int i = 0; i < targets.length; i++) {
				addLongBranch(targets[i]);
			}
		} else {
			addOpcode(Opcode.opc_lookupswitch);
			addLongBranch(sw.defaultTarget());

			addInt(values.length);

			for (int i = 0; i < targets.length; i++) {
				addInt(values[i]);
				addLongBranch(targets[i]);
			}
		}

		stackHeight--;
	}

	public void visit_ireturn(final Instruction inst) {
		addOpcode(Opcode.opc_ireturn);
		stackHeight = 0;
	}

	public void visit_lreturn(final Instruction inst) {
		addOpcode(Opcode.opc_lreturn);
		stackHeight = 0;
	}

	public void visit_freturn(final Instruction inst) {
		addOpcode(Opcode.opc_freturn);
		stackHeight = 0;
	}

	public void visit_dreturn(final Instruction inst) {
		addOpcode(Opcode.opc_dreturn);
		stackHeight = 0;
	}

	public void visit_areturn(final Instruction inst) {
		addOpcode(Opcode.opc_areturn);
		stackHeight = 0;
	}

	public void visit_return(final Instruction inst) {
		addOpcode(Opcode.opc_return);
		stackHeight = 0;
	}

	public void visit_getstatic(final Instruction inst) {
		final int index = constants.addConstant(Constant.FIELD_REF, inst
				.operand());
		addOpcode(Opcode.opc_getstatic);
		addShort(index);

		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight += type.stackHeight();
	}

	public void visit_putstatic(final Instruction inst) {
		final int index = constants.addConstant(Constant.FIELD_REF, inst
				.operand());
		addOpcode(Opcode.opc_putstatic);
		addShort(index);

		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight -= type.stackHeight();
	}

	public void visit_putstatic_nowb(final Instruction inst) {
		final int index = constants.addConstant(Constant.FIELD_REF, inst
				.operand());
		addOpcode(Opcode.opc_putstatic_nowb);
		addShort(index);

		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight -= type.stackHeight();
	}

	public void visit_getfield(final Instruction inst) {
		final int index = constants.addConstant(Constant.FIELD_REF, inst
				.operand());
		addOpcode(Opcode.opc_getfield);
		addShort(index);

		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight += type.stackHeight() - 1;
	}

	public void visit_putfield(final Instruction inst) {
		final int index = constants.addConstant(Constant.FIELD_REF, inst
				.operand());
		addOpcode(Opcode.opc_putfield);
		addShort(index);

		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight -= type.stackHeight() + 1;
	}

	public void visit_putfield_nowb(final Instruction inst) {
		final int index = constants.addConstant(Constant.FIELD_REF, inst
				.operand());
		addOpcode(Opcode.opc_putfield_nowb);
		addShort(index);

		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight -= type.stackHeight() + 1;
	}

	public void visit_invokevirtual(final Instruction inst) {
		final int index = constants.addConstant(Constant.METHOD_REF, inst
				.operand());
		addOpcode(Opcode.opc_invokevirtual);
		addShort(index);

		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		stackHeight += type.returnType().stackHeight() - type.stackHeight() - 1;
	}

	public void visit_invokespecial(final Instruction inst) {
		final int index = constants.addConstant(Constant.METHOD_REF, inst
				.operand());
		addOpcode(Opcode.opc_invokespecial);
		addShort(index);

		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		stackHeight += type.returnType().stackHeight() - type.stackHeight() - 1;
	}

	public void visit_invokestatic(final Instruction inst) {
		final int index = constants.addConstant(Constant.METHOD_REF, inst
				.operand());
		addOpcode(Opcode.opc_invokestatic);
		addShort(index);

		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		Assert.isTrue(type.isMethod(),
				"Trying to invoke a type that is not a method: " + method);

		stackHeight += type.returnType().stackHeight() - type.stackHeight();
	}

	public void visit_invokeinterface(final Instruction inst) {
		final int index = constants.addConstant(Constant.INTERFACE_METHOD_REF,
				inst.operand());
		final MemberRef method = (MemberRef) constants.constantAt(index);
		final Type type = method.nameAndType().type();

		addOpcode(Opcode.opc_invokeinterface);
		addShort(index);
		addByte(type.stackHeight() + 1);
		addByte(0);

		stackHeight += type.returnType().stackHeight() - type.stackHeight() - 1;
	}

	public void visit_new(final Instruction inst) {
		final int index = constants.addConstant(Constant.CLASS, inst.operand());
		addOpcode(Opcode.opc_new);
		addShort(index);

		stackHeight++;
	}

	public void visit_newarray(final Instruction inst) {
		final Type type = (Type) inst.operand();

		if (type.isReference()) {
			final int index = constants.addConstant(Constant.CLASS, type);
			addOpcode(Opcode.opc_anewarray);
			addShort(index);
		} else {
			addOpcode(Opcode.opc_newarray);
			addByte(type.typeCode());
		}

		stackHeight += 0;
	}

	public void visit_arraylength(final Instruction inst) {
		addOpcode(Opcode.opc_arraylength);
		stackHeight += 0;
	}

	public void visit_athrow(final Instruction inst) {
		addOpcode(Opcode.opc_athrow);
		stackHeight = 0;
	}

	public void visit_checkcast(final Instruction inst) {
		final int index = constants.addConstant(Constant.CLASS, inst.operand());
		addOpcode(Opcode.opc_checkcast);
		addShort(index);
		stackHeight += 0;
	}

	public void visit_instanceof(final Instruction inst) {
		final int index = constants.addConstant(Constant.CLASS, inst.operand());
		addOpcode(Opcode.opc_instanceof);
		addShort(index);
		stackHeight += 0;
	}

	public void visit_monitorenter(final Instruction inst) {
		addOpcode(Opcode.opc_monitorenter);
		stackHeight--;
	}

	public void visit_monitorexit(final Instruction inst) {
		addOpcode(Opcode.opc_monitorexit);
		stackHeight--;
	}

	public void visit_multianewarray(final Instruction inst) {
		final MultiArrayOperand operand = (MultiArrayOperand) inst.operand();
		final Type type = operand.type();
		final int dim = operand.dimensions();
		final int index = constants.addConstant(Constant.CLASS, type);
		addOpcode(Opcode.opc_multianewarray);
		addShort(index);
		addByte(dim);

		stackHeight += 1 - dim;
	}

	public void visit_ifnull(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_ifnonnull);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_ifnull);
			addBranch((Label) inst.operand());
		}

		stackHeight--;
	}

	public void visit_ifnonnull(final Instruction inst) {
		if (longBranch) {
			final Label tmp = method.newLabel();
			addOpcode(Opcode.opc_ifnull);
			addBranch(tmp);
			addOpcode(Opcode.opc_goto_w);
			addLongBranch((Label) inst.operand());
			addLabel(tmp);
		} else {
			addOpcode(Opcode.opc_ifnonnull);
			addBranch((Label) inst.operand());
		}

		stackHeight--;
	}

	public void visit_rc(final Instruction inst) {
		final Integer operand = (Integer) inst.operand();
		addOpcode(Opcode.opc_rc);
		addByte(operand.intValue());
		stackHeight += 0;
	}

	public void visit_aswizzle(final Instruction inst) {
		addOpcode(Opcode.opc_aswizzle);
		stackHeight -= 2;
	}

	public void visit_aswrange(final Instruction inst) {
		addOpcode(Opcode.opc_aswrange);
		stackHeight -= 3;
	}

	public void visit_aupdate(final Instruction inst) {
		final Integer operand = (Integer) inst.operand();
		addOpcode(Opcode.opc_aupdate);
		addByte(operand.intValue());
		stackHeight += 0;
	}

	public void visit_supdate(final Instruction inst) {
		final Integer operand = (Integer) inst.operand();
		addOpcode(Opcode.opc_supdate);
		addByte(operand.intValue());
		stackHeight += 0;
	}

	/**
	 * Represents the height of the stack at given Label.
	 */
	class HeightRecord {
		Label label;

		int height;

		public HeightRecord(final Label label, final int height) {
			if (ClassEditor.DEBUG || CodeArray.DEBUG) {
				System.out.println("    push " + label + " at " + height);
			}

			this.label = label;
			this.height = height;
		}
	}

	/**
	 * Used to represent the byte array.
	 */
	class ByteCell {
		byte value;

		ByteCell prev;
	}
}
