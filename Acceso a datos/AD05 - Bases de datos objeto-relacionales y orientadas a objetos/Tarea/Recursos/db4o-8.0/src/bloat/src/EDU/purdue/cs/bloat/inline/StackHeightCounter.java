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

/**
 * Used to keep track of the height of the stack. As instructions are visited,
 * the height of the stack is adjusted accordingly.
 */
public class StackHeightCounter extends InstructionAdapter {
	public static boolean DEBUG = false;

	private int height; // Current stack height

	private HashMap labelHeights; // Maps labels to their heights as Integers

	private MethodEditor method; // Method whose height we're computing

	Set tryCatches; // TryCatches active at current instruction

	private static void db(final String s) {
		if (StackHeightCounter.DEBUG) {
			System.out.println(s);
		}
	}

	public StackHeightCounter(final MethodEditor method) {
		this.method = method;
		this.height = 0;
		this.labelHeights = new HashMap();
		this.tryCatches = new HashSet();
	}

	/**
	 * Returns the current height of the stack.
	 */
	public int height() {
		return (this.height);
	}

	/**
	 * Handles a Label. Special provisions must be made for labels that catch
	 * exceptions.
	 */
	public void handle(final Label label) {
		final Integer labelHeight = (Integer) labelHeights.get(label);
		if (labelHeight != null) {
			height = labelHeight.intValue();
		}

		// If this label begins an exception handler, then start it off
		// with a new stack with one element (the exception object) on it.
		final Iterator tryCatches = method.tryCatches().iterator();
		while (tryCatches.hasNext()) {
			final TryCatch tc = (TryCatch) tryCatches.next();
			if (tc.handler().equals(label)) {
				label.setStartsBlock(true);
				height = 1;
				break;
			}

			if (tc.start().equals(label)) {
				// If this block starts a protected region make note of the
				// TryCatch block
				this.tryCatches.add(tc);
			}

			if (tc.end().equals(label)) {
				// If this block ends a protected region, remove it from the
				// tryCatches list
				this.tryCatches.remove(tc);
			}
		}
	}

	/**
	 * Handles an instruction. Special provisions must be made to handle jumps,
	 * switches, throws, and returns.
	 */
	public void handle(final Instruction inst) {
		inst.visit(this);

		if (inst.isJump()) {
			final Label target = (Label) inst.operand();
			target.setStartsBlock(true);
			final Integer targetHeight = (Integer) labelHeights.get(target);
			if (targetHeight != null) {
				if (targetHeight.intValue() != height) {
					// Make sure stack heights match
					StackHeightCounter.db("Stack height mismatch ("
							+ targetHeight.intValue() + " != " + height
							+ ") at " + inst);
				}

			} else {
				labelHeights.put(target, new Integer(height));
			}

		} else if (inst.isSwitch()) {
			// Propagate height to all targets
			final Switch sw = (Switch) inst.operand();
			final Label defaultTarget = sw.defaultTarget();
			defaultTarget.setStartsBlock(true);
			final Integer dTargetHeight = (Integer) labelHeights
					.get(defaultTarget);
			if (dTargetHeight != null) {
				if (dTargetHeight.intValue() != height) {
					// Make sure stack heights match
					StackHeightCounter.db("Stack height mismatch ("
							+ dTargetHeight.intValue() + " != " + height
							+ ") at " + inst);
				}
			} else {
				labelHeights.put(defaultTarget, new Integer(height));
			}

			final Label[] targets = sw.targets();
			for (int t = 0; t < targets.length; t++) {
				final Label target = targets[t];
				target.setStartsBlock(true);
				final Integer targetHeight = (Integer) labelHeights.get(target);
				if (targetHeight != null) {
					if (targetHeight.intValue() != height) {
						// Make sure stack heights match
						StackHeightCounter.db("Stack height mismatch ("
								+ targetHeight.intValue() + " != " + height
								+ ") at " + inst);
					}
				} else {
					labelHeights.put(target, new Integer(height));
				}
			}

		} else if (inst.isJsr()) {
			// We have to account for the return address being pushed on the
			// stack. Let's ignore the fact that someday in the future
			// subroutines may push stuff on the stack. M'kay?
			final Label subroutine = (Label) inst.operand();
			subroutine.setStartsBlock(true);
			final Integer subHeight = (Integer) labelHeights.get(subroutine);
			if (subHeight != null) {
				if (subHeight.intValue() != height + 1) {
					StackHeightCounter
							.db("Stack height mismatch at subroutine ("
									+ subHeight.intValue() + " != "
									+ (height + 1) + ") at " + inst);
				}

			} else {
				labelHeights.put(subroutine, new Integer(height + 1));
			}

		} else if (inst.isThrow() || inst.isReturn()) {
			// Clear the stack
			height = 0;
		}
	}

	/**
	 * Simulates the effect of "backing up" over an instruction.
	 */
	public void unhandle(final Instruction inst) {
		// Temporarily negate the stack height, perform the normal handle,
		// and then negate the stack height again.
		this.height = -this.height;
		this.handle(inst);
		this.height = -this.height;
	}

	/**
	 * Returns the set of <tt>TryCatch</tt> objects for the protected region
	 * that the current instruction may be in.
	 */
	public Set tryCatches() {
		return (this.tryCatches);
	}

	public void visit_ldc(final Instruction inst) {
		final Object operand = inst.operand();

		if ((operand instanceof Long) || (operand instanceof Double)) {
			height += 2;

		} else {
			height += 1;
		}
	}

	public void visit_iload(final Instruction inst) {
		height += 1;
	}

	public void visit_lload(final Instruction inst) {
		height += 2;
	}

	public void visit_fload(final Instruction inst) {
		height += 1;
	}

	public void visit_dload(final Instruction inst) {
		height += 2;
	}

	public void visit_aload(final Instruction inst) {
		height += 1;
	}

	public void visit_iaload(final Instruction inst) {
		height -= 1;
	}

	public void visit_laload(final Instruction inst) {
		height -= 0;
	}

	public void visit_faload(final Instruction inst) {
		height -= 1;
	}

	public void visit_daload(final Instruction inst) {
		height -= 0;
	}

	public void visit_aaload(final Instruction inst) {
		height -= 1;
	}

	public void visit_baload(final Instruction inst) {
		height -= 1;
	}

	public void visit_caload(final Instruction inst) {
		height -= 1;
	}

	public void visit_saload(final Instruction inst) {
		height -= 1;
	}

	public void visit_istore(final Instruction inst) {
		height -= 1;
	}

	public void visit_lstore(final Instruction inst) {
		height -= 2;
	}

	public void visit_fstore(final Instruction inst) {
		height -= 1;
	}

	public void visit_dstore(final Instruction inst) {
		height -= 2;
	}

	public void visit_astore(final Instruction inst) {
		height -= 1;
	}

	public void visit_iastore(final Instruction inst) {
		height -= 3;
	}

	public void visit_lastore(final Instruction inst) {
		height -= 4;
	}

	public void visit_fastore(final Instruction inst) {
		height -= 3;
	}

	public void visit_dastore(final Instruction inst) {
		height -= 4;
	}

	public void visit_aastore(final Instruction inst) {
		height -= 3;
	}

	public void visit_bastore(final Instruction inst) {
		height -= 3;
	}

	public void visit_castore(final Instruction inst) {
		height -= 3;
	}

	public void visit_sastore(final Instruction inst) {
		height -= 3;
	}

	public void visit_pop(final Instruction inst) {
		height -= 1;
	}

	public void visit_pop2(final Instruction inst) {
		height -= 2;
	}

	public void visit_dup(final Instruction inst) {
		height += 1;
	}

	public void visit_dup_x1(final Instruction inst) {
		height += 1;
	}

	public void visit_dup_x2(final Instruction inst) {
		height += 1;
	}

	public void visit_dup2(final Instruction inst) {
		height += 2;
	}

	public void visit_dup2_x1(final Instruction inst) {
		height += 2;
	}

	public void visit_dup2_x2(final Instruction inst) {
		height += 2;
	}

	public void visit_iadd(final Instruction inst) {
		height -= 1;
	}

	public void visit_ladd(final Instruction inst) {
		height -= 2;
	}

	public void visit_fadd(final Instruction inst) {
		height -= 1;
	}

	public void visit_dadd(final Instruction inst) {
		height -= 2;
	}

	public void visit_isub(final Instruction inst) {
		height -= 1;
	}

	public void visit_lsub(final Instruction inst) {
		height -= 2;
	}

	public void visit_fsub(final Instruction inst) {
		height -= 1;
	}

	public void visit_dsub(final Instruction inst) {
		height -= 2;
	}

	public void visit_imul(final Instruction inst) {
		height -= 1;
	}

	public void visit_lmul(final Instruction inst) {
		height -= 2;
	}

	public void visit_fmul(final Instruction inst) {
		height -= 1;
	}

	public void visit_dmul(final Instruction inst) {
		height -= 2;
	}

	public void visit_idiv(final Instruction inst) {
		height -= 1;
	}

	public void visit_ldiv(final Instruction inst) {
		height -= 2;
	}

	public void visit_fdiv(final Instruction inst) {
		height -= 1;
	}

	public void visit_ddiv(final Instruction inst) {
		height -= 2;
	}

	public void visit_irem(final Instruction inst) {
		height -= 1;
	}

	public void visit_lrem(final Instruction inst) {
		height -= 2;
	}

	public void visit_frem(final Instruction inst) {
		height -= 1;
	}

	public void visit_drem(final Instruction inst) {
		height -= 2;
	}

	public void visit_ishl(final Instruction inst) {
		height -= 1;
	}

	public void visit_lshl(final Instruction inst) {
		height -= 1;
	}

	public void visit_ishr(final Instruction inst) {
		height -= 1;
	}

	public void visit_lshr(final Instruction inst) {
		height -= 1;
	}

	public void visit_iushr(final Instruction inst) {
		height -= 1;
	}

	public void visit_lushr(final Instruction inst) {
		// Yes, it's only -1. The long and the int index are popped off
		// and the shifted value is pushed. Net loss of 1.
		height -= 1;
	}

	public void visit_iand(final Instruction inst) {
		height -= 1;
	}

	public void visit_land(final Instruction inst) {
		height -= 2;
	}

	public void visit_ior(final Instruction inst) {
		height -= 1;
	}

	public void visit_lor(final Instruction inst) {
		height -= 2;
	}

	public void visit_ixor(final Instruction inst) {
		height -= 1;
	}

	public void visit_lxor(final Instruction inst) {
		height -= 2;
	}

	public void visit_i2l(final Instruction inst) {
		height += 1;
	}

	public void visit_i2d(final Instruction inst) {
		height += 1;
	}

	public void visit_l2i(final Instruction inst) {
		height -= 1;
	}

	public void visit_l2f(final Instruction inst) {
		height -= 1;
	}

	public void visit_f2l(final Instruction inst) {
		height += 1;
	}

	public void visit_f2d(final Instruction inst) {
		height += 1;
	}

	public void visit_d2i(final Instruction inst) {
		height -= 1;
	}

	public void visit_d2f(final Instruction inst) {
		height -= 1;
	}

	public void visit_lcmp(final Instruction inst) {
		height -= 3;
	}

	public void visit_fcmpl(final Instruction inst) {
		height -= 1;
	}

	public void visit_fcmpg(final Instruction inst) {
		height -= 1;
	}

	public void visit_dcmpl(final Instruction inst) {
		height -= 3;
	}

	public void visit_dcmpg(final Instruction inst) {
		height -= 3;
	}

	public void visit_ifeq(final Instruction inst) {
		height -= 1;
	}

	public void visit_ifne(final Instruction inst) {
		height -= 1;
	}

	public void visit_iflt(final Instruction inst) {
		height -= 1;
	}

	public void visit_ifge(final Instruction inst) {
		height -= 1;
	}

	public void visit_ifgt(final Instruction inst) {
		height -= 1;
	}

	public void visit_ifle(final Instruction inst) {
		height -= 1;
	}

	public void visit_if_icmpeq(final Instruction inst) {
		height -= 2;
	}

	public void visit_if_icmpne(final Instruction inst) {
		height -= 2;
	}

	public void visit_if_icmplt(final Instruction inst) {
		height -= 2;
	}

	public void visit_if_icmpge(final Instruction inst) {
		height -= 2;
	}

	public void visit_if_icmpgt(final Instruction inst) {
		height -= 2;
	}

	public void visit_if_icmple(final Instruction inst) {
		height -= 2;
	}

	public void visit_if_acmpeq(final Instruction inst) {
		height -= 2;
	}

	public void visit_if_acmpne(final Instruction inst) {
		height -= 2;
	}

	public void visit_jsr(final Instruction inst) {
		// Even though the jsr instruction itself pushes the return
		// address onto the stack, we don't want to account for that
		// here. It is already taken care of in the handle method. This
		// way the label following the jsr (the return site) will have the
		// stack height it had before the call. Once again, we do not
		// account for the possibility of the jsr modifying the height of
		// the stack.
		height += 0;
	}

	public void visit_switch(final Instruction inst) {
		height -= 1;
	}

	public void visit_ireturn(final Instruction inst) {
		height = 0;
	}

	public void visit_lreturn(final Instruction inst) {
		height = 0;
	}

	public void visit_freturn(final Instruction inst) {
		height = 0;
	}

	public void visit_dreturn(final Instruction inst) {
		height = 0;
	}

	public void visit_areturn(final Instruction inst) {
		height = 0;
	}

	public void visit_return(final Instruction inst) {
		height = 0;
	}

	public void visit_getstatic(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		height += type.stackHeight();
	}

	public void visit_putstatic(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		height -= type.stackHeight();
	}

	public void visit_putstatic_nowb(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		height -= type.stackHeight();
	}

	public void visit_getfield(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		height += type.stackHeight() - 1;
	}

	public void visit_putfield(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		height -= type.stackHeight() + 1;
	}

	public void visit_putfield_nowb(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		height -= type.stackHeight() + 1;
	}

	public void visit_invokevirtual(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		height += type.returnType().stackHeight() - type.stackHeight() - 1;
	}

	public void visit_invokespecial(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		height += type.returnType().stackHeight() - type.stackHeight() - 1;
	}

	public void visit_invokestatic(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		height += type.returnType().stackHeight() - type.stackHeight();
	}

	public void visit_invokeinterface(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		height += type.returnType().stackHeight() - type.stackHeight() - 1;

	}

	public void visit_new(final Instruction inst) {
		height += 1;
	}

	public void visit_monitorenter(final Instruction inst) {
		height -= 1;
	}

	public void visit_monitorexit(final Instruction inst) {
		height -= 1;
	}

	public void visit_multianewarray(final Instruction inst) {
		final MultiArrayOperand operand = (MultiArrayOperand) inst.operand();
		final int dim = operand.dimensions();

		height += 1 - dim;
	}

	public void visit_ifnull(final Instruction inst) {
		height -= 1;
	}

	public void visit_ifnonnull(final Instruction inst) {
		height -= 1;
	}

	public void visit_aswizzle(final Instruction inst) {
		height -= 2;
	}

	public void visit_aswrange(final Instruction inst) {
		height -= 3;
	}

	/**
	 * Returns a clone of this <tt>StackHeightCounter</tt>
	 */
	public Object clone() {
		final StackHeightCounter clone = new StackHeightCounter(this.method);
		clone.height = this.height;
		clone.labelHeights = (HashMap) this.labelHeights.clone();
		return (clone);
	}
}
