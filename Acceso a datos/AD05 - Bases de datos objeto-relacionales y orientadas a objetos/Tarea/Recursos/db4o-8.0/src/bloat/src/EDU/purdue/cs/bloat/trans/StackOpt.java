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

import EDU.purdue.cs.bloat.editor.*;

public class StackOpt extends InstructionAdapter implements Opcode {

	int stackHeight;

	int minStackHeight;

	UseMap uMap;

	static final boolean DEBUG = false;

	public void transform(final MethodEditor method) {

		uMap = method.uMap();

		for (int i = method.codeLength() - 1; i > 0; i--) {

			Instruction inst;
			boolean isWide;
			final Object codeEl = method.codeElementAt(i);

			if ((codeEl instanceof Instruction)
					&& ((Instruction) codeEl).isLoad()) {
				inst = (Instruction) codeEl;
			} else {
				continue;
			}

			switch (inst.opcodeClass()) {

			case opcx_iload:
			case opcx_fload:
			case opcx_aload:
			case opcx_iaload:
			case opcx_faload:
			case opcx_aaload:
			case opcx_baload:
			case opcx_caload:
			case opcx_saload:
				isWide = false;
				break;
			case opcx_lload:
			case opcx_dload:
			case opcx_laload:
			case opcx_daload:
			default:
				isWide = true;
			}

			stackHeight = 0;

			for (int j = i - 1;; j--) {

				// stop at the begining of the code or a basic block.
				if ((j <= 0) || // this should be redundant, but to be safe
						(method.codeElementAt(j) instanceof Label)) {
					break;
				}

				if ((stackHeight == -1)
						&& ((uMap.hasSameDef(inst, ((Instruction) method
								.codeElementAt(j))) && ((Instruction) method
								.codeElementAt(j)).isLoad()) || dupRun(method,
								j, inst))) {

					if (forwardCountCheck(method, j, i, -1)) {
						// found a type 0 relation with a load
						if (StackOpt.DEBUG) {
							System.err
									.println("load type 0: "
											+ ((Instruction) method
													.codeElementAt(j))
													.toString() + " "
											+ inst.toString());
						}

						if (isWide) {
							method.insertCodeAt(
									new Instruction(Opcode.opc_dup2), j + 1);
						} else {
							method.insertCodeAt(
									new Instruction(Opcode.opc_dup), j + 1); // add
																				// dup
						}
						i++; // code has changed; why don't method editors
						// have iterators?
						method.removeCodeAt(i); // remove load
					}
					break; // done, even if final check failed.
				}

				else if ((stackHeight == 0)
						&& uMap.hasSameDef(inst, ((Instruction) method
								.codeElementAt(j)))) {
					if (((Instruction) method.codeElementAt(j)).isStore()) {

						if (forwardCountCheck(method, j, i, 0)) {
							// found a type 0 with a store
							if (StackOpt.DEBUG) {
								System.err.println("store type 0: "
										+ ((Instruction) method
												.codeElementAt(j)).toString()
										+ " " + inst.toString());
							}

							if (isWide) {
								method.insertCodeAt(new Instruction(
										Opcode.opc_dup2), j);
							} else {
								method.insertCodeAt(new Instruction(
										Opcode.opc_dup), j);
							}
							i++;
							method.removeCodeAt(i);
						}
						break;
					} else if (((Instruction) method.codeElementAt(j)).isLoad()
							&& !isWide) { // can't do type 1s with wides.

						if (forwardCountCheck(method, j, i, -1)) {
							// found a type 1 with a load
							if (StackOpt.DEBUG) {
								System.err.println("load type 1: "
										+ ((Instruction) method
												.codeElementAt(j)).toString()
										+ " " + inst.toString());
							}

							method.insertCodeAt(
									new Instruction(Opcode.opc_dup), j + 1);
							i++;
							method.replaceCodeAt(new Instruction(
									Opcode.opc_swap), i);
						}
						break;
					}
				}

				else if ((stackHeight == 1)
						&& uMap.hasSameDef(inst, ((Instruction) method
								.codeElementAt(j)))) {
					if (((Instruction) method.codeElementAt(j)).isStore()
							&& !isWide) { // can't do type 1 with wides

						if (forwardCountCheck(method, j, i, 0)) {
							// type 1 for stores
							if (StackOpt.DEBUG) {
								System.err.println("store type 1: "
										+ ((Instruction) method
												.codeElementAt(j)).toString()
										+ " " + inst.toString());
							}

							method.insertCodeAt(
									new Instruction(Opcode.opc_dup), j);
							i++;
							method.replaceCodeAt(new Instruction(
									Opcode.opc_swap), i);
						}
						break;
					}
				}

				heightChange(method.codeElementAt(j));
				// System.err.print(stackHeight + ";");
			}
		}

	}

	boolean forwardCountCheck(final MethodEditor m, final int j, final int i,
			final int bound) {

		stackHeight = 0;
		minStackHeight = 0;

		for (int k = j + 1; k < i; k++) {
			heightChange(m.codeElementAt(k));
			if (minStackHeight < bound) {
				return false;
			}
		}

		return true;
	}

	boolean dupRun(final MethodEditor m, final int j, final Instruction inst) {

		if (((Instruction) m.codeElementAt(j)).opcodeClass() == Opcode.opcx_dup) {
			for (int k = j - 1;; k--) {
				if (m.codeElementAt(k) instanceof Instruction) {
					if (((Instruction) m.codeElementAt(k)).opcodeClass() == Opcode.opcx_dup) {
						continue;
					} else if (((Instruction) m.codeElementAt(k)).isLoad()
							&& uMap.hasSameDef(inst, ((Instruction) m
									.codeElementAt(k)))) {
						return true;
					}
				}
				break;
			}
		}

		return false;
	}

	void heightChange(final Object inst) {

		if (inst instanceof Instruction) {
			((Instruction) inst).visit(this);
		}

	}

	public void visit_nop(final Instruction inst) {
		stackHeight += 0;
	}

	public void visit_ldc(final Instruction inst) {
		final Object operand = inst.operand();

		if ((operand instanceof Long) || (operand instanceof Double)) {
			stackHeight += 2;

		} else {
			stackHeight += 1;
		}
	}

	public void visit_iload(final Instruction inst) {
		stackHeight += 1;
	}

	public void visit_lload(final Instruction inst) {
		stackHeight += 2;
	}

	public void visit_fload(final Instruction inst) {
		stackHeight += 1;
	}

	public void visit_dload(final Instruction inst) {
		stackHeight += 2;
	}

	public void visit_aload(final Instruction inst) {
		stackHeight += 1;
	}

	public void visit_iaload(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_laload(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_faload(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_daload(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_aaload(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_baload(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_caload(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_saload(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_istore(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_lstore(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_fstore(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_dstore(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_astore(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_iastore(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_lastore(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_fastore(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_dastore(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_aastore(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_bastore(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_castore(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_sastore(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_pop(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_pop2(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_dup(final Instruction inst) {
		stackHeight += 1;
	}

	public void visit_dup_x1(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += 3;
	}

	public void visit_dup_x2(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += 4;
	}

	public void visit_dup2(final Instruction inst) {
		stackHeight += 2;
	}

	public void visit_dup2_x1(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += 5;
	}

	public void visit_dup2_x2(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += 6;
	}

	public void visit_swap(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_iadd(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;

	}

	public void visit_ladd(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_fadd(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_dadd(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_isub(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lsub(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_fsub(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_dsub(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_imul(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lmul(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_fmul(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_dmul(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_idiv(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_ldiv(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_fdiv(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_ddiv(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_irem(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lrem(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_frem(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_drem(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_ineg(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lneg(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_fneg(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_dneg(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;

	}

	public void visit_ishl(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lshl(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_ishr(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lshr(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_iushr(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lushr(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_iand(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_land(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_ior(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lor(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_ixor(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lxor(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_iinc(final Instruction inst) {

	}

	public void visit_i2l(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += 2;
	}

	public void visit_i2f(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_i2d(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += 2;
	}

	public void visit_l2i(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_l2f(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_l2d(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_f2i(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_f2l(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += 2;
	}

	public void visit_f2d(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += 2;
	}

	public void visit_d2i(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_d2l(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 2;
	}

	public void visit_d2f(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_i2b(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_i2c(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_i2s(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_lcmp(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_fcmpl(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_fcmpg(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_dcmpl(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_dcmpg(final Instruction inst) {
		stackHeight -= 4;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_ifeq(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_ifne(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_iflt(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_ifge(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_ifgt(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_ifle(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_if_icmpeq(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_if_icmpne(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_if_icmplt(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_if_icmpge(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_if_icmpgt(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_if_icmple(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_if_acmpeq(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_if_acmpne(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_goto(final Instruction inst) {

	}

	public void visit_jsr(final Instruction inst) {
		stackHeight += 1;
	}

	public void visit_ret(final Instruction inst) {
	}

	public void visit_switch(final Instruction inst) {
		stackHeight -= 1;
	}

	public void visit_ireturn(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_lreturn(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_freturn(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_dreturn(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_areturn(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_return(final Instruction inst) {
	}

	public void visit_getstatic(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight += type.stackHeight();
	}

	public void visit_putstatic(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight -= type.stackHeight();
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_putstatic_nowb(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight -= type.stackHeight();
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_getfield(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();

		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += type.stackHeight();
	}

	public void visit_putfield(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight -= type.stackHeight() + 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_putfield_nowb(final Instruction inst) {
		final Type type = ((MemberRef) inst.operand()).nameAndType().type();
		stackHeight -= type.stackHeight() + 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_invokevirtual(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		stackHeight -= type.stackHeight() + 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += type.returnType().stackHeight();
	}

	public void visit_invokespecial(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		stackHeight -= type.stackHeight() + 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += type.returnType().stackHeight();

	}

	public void visit_invokestatic(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		stackHeight -= type.stackHeight();
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += type.returnType().stackHeight();
	}

	public void visit_invokeinterface(final Instruction inst) {
		final MemberRef method = (MemberRef) inst.operand();
		final Type type = method.nameAndType().type();

		stackHeight -= type.stackHeight() + 1;

		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += type.returnType().stackHeight();
	}

	public void visit_new(final Instruction inst) {
		stackHeight += 1;
	}

	public void visit_newarray(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;

	}

	public void visit_arraylength(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;
	}

	public void visit_athrow(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_checkcast(final Instruction inst) {

	}

	public void visit_instanceof(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
		stackHeight += 1;

	}

	public void visit_monitorenter(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_monitorexit(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

	}

	public void visit_multianewarray(final Instruction inst) {
		final MultiArrayOperand operand = (MultiArrayOperand) inst.operand();
		final int dim = operand.dimensions();

		stackHeight -= dim;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}

		stackHeight += 1;
	}

	public void visit_ifnull(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_ifnonnull(final Instruction inst) {
		stackHeight -= 1;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_rc(final Instruction inst) {
	}

	public void visit_aupdate(final Instruction inst) {
	}

	public void visit_supdate(final Instruction inst) {
	}

	public void visit_aswizzle(final Instruction inst) {
		stackHeight -= 2;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

	public void visit_aswrange(final Instruction inst) {
		stackHeight -= 3;
		if (stackHeight < minStackHeight) {
			minStackHeight = stackHeight;
		}
	}

}
