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

import EDU.purdue.cs.bloat.util.*;

/**
 * <tt>Instruction</tt> represents a single instruction in the JVM. All
 * <tt>Instruction</tt>s known their opcode. Some instructions have an
 * operand. Operands are integers, floats, or one of the special classes that
 * represent multiple operands such as <tt>IncOperand</tt> and <tt>Switch</tt>.
 * 
 * @see IncOperand
 * @see Switch
 * @see MultiArrayOperand
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public class Instruction implements Opcode {
	private Object operand;

	private int opcode; // Mapped opcode

	private int origOpcode; // Original (non-mapped) opcode

	private boolean useSlow = false;

	// Do we use a slow version when generating code?

	/**
	 * Constructor.
	 * 
	 * @param opcode
	 *            The opcode class of the instruction.
	 */
	public Instruction(final int opcode) {
		this(opcode, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param opcode
	 *            The opcode class of the instruction.
	 * @param operand
	 *            The operand of the instruction, or null.
	 */
	public Instruction(final int opcode, final Object operand) {
		this.opcode = opcode;
		this.origOpcode = opcode;
		this.operand = operand;
		Assert.isTrue(opcode == Opcode.opcXMap[opcode], "Illegal instruction: "
				+ this);
	}

	/**
	 * Constructor.
	 * 
	 * @param code
	 *            The raw byte code array of the method.
	 * @param index
	 *            This index into the byte array of the instruction.
	 * @param targets
	 *            Indices of the branch targets of the instruction.
	 * @param lookups
	 *            Values of the switch lookups of the instruction.
	 * @param locals
	 *            The local variables defined at this index.
	 * @param constants
	 *            The constant pool for the class.
	 */
	public Instruction(final byte[] code, final int index, final int[] targets,
			final int[] lookups, final LocalVariable[] locals,
			final ConstantPool constants) {
		int i, j;
		int incr;
		int dim;
		int atype;
		Label[] t;
		int[] v;

		int opc = Instruction.toUByte(code[index]);

		switch (opc) {
		case opc_aconst_null:
			operand = null;
			break;
		case opc_iconst_m1:
			operand = new Integer(-1);
			break;
		case opc_iconst_0:
			operand = new Integer(0);
			break;
		case opc_iconst_1:
			operand = new Integer(1);
			break;
		case opc_iconst_2:
			operand = new Integer(2);
			break;
		case opc_iconst_3:
			operand = new Integer(3);
			break;
		case opc_iconst_4:
			operand = new Integer(4);
			break;
		case opc_iconst_5:
			operand = new Integer(5);
			break;
		case opc_lconst_0:
			operand = new Long(0);
			break;
		case opc_lconst_1:
			operand = new Long(1);
			break;
		case opc_fconst_0:
			operand = new Float(0.0F);
			break;
		case opc_fconst_1:
			operand = new Float(1.0F);
			break;
		case opc_fconst_2:
			operand = new Float(2.0F);
			break;
		case opc_dconst_0:
			operand = new Double(0.0);
			break;
		case opc_dconst_1:
			operand = new Double(1.0);
			break;
		case opc_bipush:
			operand = new Integer(code[index + 1]);
			break;
		case opc_sipush:
			operand = new Integer(Instruction.toShort(code[index + 1],
					code[index + 2]));
			break;
		case opc_ldc:
			i = Instruction.toUByte(code[index + 1]);
			operand = constants.constantAt(i);
			break;
		case opc_ldc_w:
		case opc_ldc2_w:
			i = Instruction.toUShort(code[index + 1], code[index + 2]);
			operand = constants.constantAt(i);
			break;
		case opc_iload:
		case opc_lload:
		case opc_fload:
		case opc_dload:
		case opc_aload:
			i = Instruction.toUByte(code[index + 1]);
			operand = (i < locals.length) && (locals[i] != null) ? locals[i]
					: new LocalVariable(i);
			break;
		case opc_iload_0:
		case opc_lload_0:
		case opc_fload_0:
		case opc_dload_0:
		case opc_aload_0:
			operand = (0 < locals.length) && (locals[0] != null) ? locals[0]
					: new LocalVariable(0);
			break;
		case opc_iload_1:
		case opc_lload_1:
		case opc_fload_1:
		case opc_dload_1:
		case opc_aload_1:
			operand = (1 < locals.length) && (locals[1] != null) ? locals[1]
					: new LocalVariable(1);
			break;
		case opc_iload_2:
		case opc_lload_2:
		case opc_fload_2:
		case opc_dload_2:
		case opc_aload_2:
			operand = (2 < locals.length) && (locals[2] != null) ? locals[2]
					: new LocalVariable(2);
			break;
		case opc_iload_3:
		case opc_lload_3:
		case opc_fload_3:
		case opc_dload_3:
		case opc_aload_3:
			operand = (3 < locals.length) && (locals[3] != null) ? locals[3]
					: new LocalVariable(3);
			break;
		case opc_istore:
		case opc_lstore:
		case opc_fstore:
		case opc_dstore:
		case opc_astore:
			i = Instruction.toUByte(code[index + 1]);
			operand = (i < locals.length) && (locals[i] != null) ? locals[i]
					: new LocalVariable(i);
			break;
		case opc_istore_0:
		case opc_lstore_0:
		case opc_fstore_0:
		case opc_dstore_0:
		case opc_astore_0:
			operand = (0 < locals.length) && (locals[0] != null) ? locals[0]
					: new LocalVariable(0);
			break;
		case opc_istore_1:
		case opc_lstore_1:
		case opc_fstore_1:
		case opc_dstore_1:
		case opc_astore_1:
			operand = (1 < locals.length) && (locals[1] != null) ? locals[1]
					: new LocalVariable(1);
			break;
		case opc_istore_2:
		case opc_lstore_2:
		case opc_fstore_2:
		case opc_dstore_2:
		case opc_astore_2:
			operand = (2 < locals.length) && (locals[2] != null) ? locals[2]
					: new LocalVariable(2);
			break;
		case opc_istore_3:
		case opc_lstore_3:
		case opc_fstore_3:
		case opc_dstore_3:
		case opc_astore_3:
			operand = (3 < locals.length) && (locals[3] != null) ? locals[3]
					: new LocalVariable(3);
			break;
		case opc_iinc:
			i = Instruction.toUByte(code[index + 1]);
			incr = code[index + 2];
			operand = new IncOperand(
					(i < locals.length) && (locals[i] != null) ? locals[i]
							: new LocalVariable(i), incr);
			break;
		case opc_ifeq:
		case opc_ifne:
		case opc_iflt:
		case opc_ifge:
		case opc_ifgt:
		case opc_ifle:
		case opc_if_icmpeq:
		case opc_if_icmpne:
		case opc_if_icmplt:
		case opc_if_icmpge:
		case opc_if_icmpgt:
		case opc_if_icmple:
		case opc_if_acmpeq:
		case opc_if_acmpne:
			Assert.isTrue(targets.length == 1, "Illegal instruction: "
					+ Opcode.opcNames[opc]);
			operand = new Label(targets[0]);
			break;
		case opc_goto:
		case opc_jsr:
		case opc_ifnull:
		case opc_ifnonnull:
		case opc_goto_w:
		case opc_jsr_w:
			Assert.isTrue(targets.length == 1, "Illegal instruction: "
					+ Opcode.opcNames[opc]);
			operand = new Label(targets[0]);
			break;
		case opc_ret:
			i = Instruction.toUByte(code[index + 1]);
			operand = (i < locals.length) && (locals[i] != null) ? locals[i]
					: new LocalVariable(i);
			break;
		case opc_tableswitch:
			// The first target is the default.
			t = new Label[targets.length - 1];
			v = new int[targets.length - 1];
			for (i = 1, j = lookups[0]; i < targets.length; i++, j++) {
				t[i - 1] = new Label(targets[i]);
				v[i - 1] = j;
			}
			operand = new Switch(new Label(targets[0]), t, v);
			break;
		case opc_lookupswitch:
			// The first target is the default.
			t = new Label[targets.length - 1];
			v = new int[targets.length - 1];
			for (i = 1; i < targets.length; i++) {
				t[i - 1] = new Label(targets[i]);
				v[i - 1] = lookups[i - 1];
			}
			operand = new Switch(new Label(targets[0]), t, v);
			break;
		case opc_getstatic:
		case opc_putstatic:
		case opc_putstatic_nowb:
		case opc_getfield:
		case opc_putfield:
		case opc_putfield_nowb:
		case opc_invokevirtual:
		case opc_invokespecial:
		case opc_invokestatic:
		case opc_invokeinterface:
		case opc_new:
		case opc_anewarray:
		case opc_checkcast:
		case opc_instanceof:
			i = Instruction.toUShort(code[index + 1], code[index + 2]);
			operand = constants.constantAt(i);
			break;
		case opc_newarray:
			atype = code[index + 1];
			operand = Type.getType(atype);
			break;
		case opc_wide:
			opc = Instruction.toUByte(code[index + 1]);
			switch (opc) {
			case opc_iload:
			case opc_fload:
			case opc_aload:
			case opc_lload:
			case opc_dload:
			case opc_istore:
			case opc_fstore:
			case opc_astore:
			case opc_lstore:
			case opc_dstore:
			case opc_ret:
				i = Instruction.toUShort(code[index + 2], code[index + 3]);
				operand = (i < locals.length) && (locals[i] != null) ? locals[i]
						: new LocalVariable(i);
				break;
			case opc_iinc:
				i = Instruction.toUShort(code[index + 2], code[index + 3]);
				incr = Instruction.toShort(code[index + 4], code[index + 5]);
				operand = new IncOperand((i < locals.length)
						&& (locals[i] != null) ? locals[i] : new LocalVariable(
						i), incr);
				break;
			}
			break;
		case opc_multianewarray:
			i = Instruction.toUShort(code[index + 1], code[index + 2]);
			dim = Instruction.toUByte(code[index + 3]);
			operand = new MultiArrayOperand((Type) constants.constantAt(i), dim);
			break;
		case opc_rc:
			i = Instruction.toUByte(code[index + 1]);
			operand = new Integer(i);
			break;
		case opc_aupdate:
			i = Instruction.toUByte(code[index + 1]);
			operand = new Integer(i);
			break;
		case opc_supdate:
			i = Instruction.toUByte(code[index + 1]);
			operand = new Integer(i);
			break;
		default:
			break;
		}

		origOpcode = opc;
		opcode = Opcode.opcXMap[opc];
	}

	/**
	 * Returns the original (non-mapped) opcode used to create this Instruction.
	 */
	public int origOpcode() {
		return (this.origOpcode);
	}

	/**
	 * Sets a flag that determines whether or not the "slow" version of the
	 * instruction should be generated. For example, if useSlow is true, "iload
	 * 2" is generated instead of "iload_2".
	 */
	public void setUseSlow(final boolean useSlow) {
		this.useSlow = useSlow;
	}

	/**
	 * Returns whether or not the "slow" version of this instruction is
	 * generated.
	 */
	public boolean useSlow() {
		return (this.useSlow);
	}

	/**
	 * Check if the instruction is a load.
	 * 
	 * @return true if the instruction is a load, false if not.
	 */
	public boolean isLoad() {
		switch (opcode) {
		case opc_iload:
		case opc_lload:
		case opc_fload:
		case opc_dload:
		case opc_aload:
		case opc_iload_0:
		case opc_lload_0:
		case opc_fload_0:
		case opc_dload_0:
		case opc_aload_0:
		case opc_iload_1:
		case opc_lload_1:
		case opc_fload_1:
		case opc_dload_1:
		case opc_aload_1:
		case opc_iload_2:
		case opc_lload_2:
		case opc_fload_2:
		case opc_dload_2:
		case opc_aload_2:
		case opc_iload_3:
		case opc_lload_3:
		case opc_fload_3:
		case opc_dload_3:
		case opc_aload_3:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Check if the instruction is a store.
	 * 
	 * @return true if the instruction is a store, false if not.
	 */
	public boolean isStore() {
		switch (opcode) {
		case opc_istore:
		case opc_lstore:
		case opc_fstore:
		case opc_dstore:
		case opc_astore:
		case opc_istore_0:
		case opc_lstore_0:
		case opc_fstore_0:
		case opc_dstore_0:
		case opc_astore_0:
		case opc_istore_1:
		case opc_lstore_1:
		case opc_fstore_1:
		case opc_dstore_1:
		case opc_astore_1:
		case opc_istore_2:
		case opc_lstore_2:
		case opc_fstore_2:
		case opc_dstore_2:
		case opc_astore_2:
		case opc_istore_3:
		case opc_lstore_3:
		case opc_fstore_3:
		case opc_dstore_3:
		case opc_astore_3:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Check if the instruction is an increment.
	 * 
	 * @return true if the instruction is an increment, false if not.
	 */
	public boolean isInc() {
		return opcode == Opcode.opc_iinc;
	}

	/**
	 * Check if the instruction is an exception throw instruction.
	 * 
	 * @return true if the instruction is a throw, false if not.
	 */
	public boolean isThrow() {
		return opcode == Opcode.opc_athrow;
	}

	/**
	 * Returns <tt>true</tt> if this instruction invokes a method.
	 */
	public boolean isInvoke() {
		switch (opcode) {
		case opc_invokevirtual:
		case opc_invokespecial:
		case opc_invokestatic:
		case opc_invokeinterface:
			return (true);
		default:
			return (false);
		}
	}

	/**
	 * Check if the instruction is a subroutine return instruction.
	 * 
	 * @return true if the instruction is a ret, false if not.
	 */
	public boolean isRet() {
		return opcode == Opcode.opc_ret;
	}

	/**
	 * Returns <tt>true</tt> if the instruction returns from a method.
	 */
	public boolean isReturn() {
		switch (opcode) {
		case opc_areturn:
		case opc_ireturn:
		case opc_lreturn:
		case opc_freturn:
		case opc_dreturn:
		case opc_return:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Check if the instruction is a switch.
	 * 
	 * @return true if the instruction is a switch, false if not.
	 */
	public boolean isSwitch() {
		return opcodeClass() == Opcode.opcx_switch;
	}

	/**
	 * Check if the instruction is a jump.
	 * 
	 * @return true if the instruction is a jump, false if not.
	 */
	public boolean isJump() {
		return isConditionalJump() || isGoto();
	}

	/**
	 * Check if the instruction is a jsr.
	 * 
	 * @return true if the instruction is a jsr, false if not.
	 */
	public boolean isJsr() {
		return opcodeClass() == Opcode.opcx_jsr;
	}

	/**
	 * Check if the instruction is a goto.
	 * 
	 * @return true if the instruction is a goto, false if not.
	 */
	public boolean isGoto() {
		return opcodeClass() == Opcode.opcx_goto;
	}

	/**
	 * Check if the instruction is a conditional jump.
	 * 
	 * @return true if the instruction is a conditional jump, false if not.
	 */
	public boolean isConditionalJump() {
		switch (opcode) {
		case opc_if_icmpeq:
		case opc_if_icmpne:
		case opc_if_icmplt:
		case opc_if_icmpge:
		case opc_if_icmpgt:
		case opc_if_icmple:
		case opc_if_acmpeq:
		case opc_if_acmpne:
		case opc_ifeq:
		case opc_ifne:
		case opc_iflt:
		case opc_ifge:
		case opc_ifgt:
		case opc_ifle:
		case opc_ifnull:
		case opc_ifnonnull:
			return true;
		default:
			return false;
		}
	}

	/**
	 * Get the opcode class of the instruction.
	 * 
	 * @return The opcode class of the instruction.
	 */
	public int opcodeClass() {
		return opcode;
	}

	/**
	 * Set the opcode class of the instruction.
	 * 
	 * @param opcode
	 *            The opcode class of the instruction.
	 */
	public void setOpcodeClass(final int opcode) {
		this.opcode = opcode;
	}

	/**
	 * Set the operand of the instruction.
	 * 
	 * @param operand
	 *            The operand of the instruction.
	 */
	public void setOperand(final Object operand) {
		this.operand = operand;
	}

	/**
	 * Get the operand of the instruction.
	 * 
	 * @return The operand of the instruction.
	 */
	public Object operand() {
		return operand;
	}

	/**
	 * Convert the instruction to a string.
	 * 
	 * @return A string representation of the instruction.
	 */
	public String toString() {
		if ((operand == null) && (opcodeClass() != Opcode.opcx_ldc)) {
			return Opcode.opcNames[opcode];
		} else if (operand instanceof Float) {
			return Opcode.opcNames[opcode] + " " + operand + "F";
		} else if (operand instanceof Long) {
			return Opcode.opcNames[opcode] + " " + operand + "L";
		} else if (operand instanceof String) {
			final StringBuffer sb = new StringBuffer();

			final String s = (String) operand;

			for (int i = 0; i < s.length(); i++) {
				final char c = s.charAt(i);
				if (Character.isWhitespace(c) || ((0x20 <= c) && (c <= 0x7e))) {
					sb.append(c);
				} else {
					sb.append("\\u");
					sb.append(Integer.toHexString(c));
				}

				if (sb.length() > 50) {
					sb.append("...");
					break;
				}
			}

			return Opcode.opcNames[opcode] + " \"" + sb.toString() + "\"";

		} else {

			return Opcode.opcNames[opcode] + " " + operand;
		}
	}

	/**
	 * Utility function to join 2 bytes into an unsigned short.
	 * 
	 * @param b1
	 *            The upper byte.
	 * @param b2
	 *            The lower byte.
	 * @return The unsigned short.
	 */
	protected static int toUShort(final byte b1, final byte b2) {
		int x = (short) (Instruction.toUByte(b1) << 8)
				| Instruction.toUByte(b2);
		if (x < 0) {
			x += 0x10000;
		}
		return x;
	}

	/**
	 * Utility function to join 2 bytes into an signed short.
	 * 
	 * @param b1
	 *            The upper byte.
	 * @param b2
	 *            The lower byte.
	 * @return The signed short.
	 */
	protected static short toShort(final byte b1, final byte b2) {
		return (short) ((Instruction.toUByte(b1) << 8) | Instruction
				.toUByte(b2));
	}

	/**
	 * Utility function to join 4 bytes into an signed int.
	 * 
	 * @param b1
	 *            The upper byte.
	 * @param b2
	 *            The next-to-upper byte.
	 * @param b3
	 *            The next-to-lower byte.
	 * @param b4
	 *            The lower byte.
	 * @return The signed int.
	 */
	protected static int toInt(final byte b1, final byte b2, final byte b3,
			final byte b4) {
		return (Instruction.toUByte(b1) << 24)
				| (Instruction.toUByte(b2) << 16)
				| (Instruction.toUByte(b3) << 8) | Instruction.toUByte(b4);
	}

	/**
	 * Utility function to convert a byte into an unsigned byte.
	 * 
	 * @param b
	 *            The byte.
	 * @return The unsigned byte.
	 */
	protected static int toUByte(final byte b) {
		return b < 0 ? b + 0x100 : b;
	}

	/**
	 * Returns the category of this instruction. An instruction's category is
	 * basically the width of the value the instruction places on the stack.
	 * Types <tt>long</tt> and <tt>double</tt> are Category 2. All other
	 * types are Category 1.
	 */
	public int category() {
		switch (this.opcode) {
		case opcx_lload:
		case opcx_dload:
		case opcx_lstore:
		case opcx_dstore:
		case opcx_laload:
		case opcx_daload:
		case opcx_lastore:
		case opcx_dastore:
		case opcx_ladd:
		case opcx_dadd:
		case opcx_lsub:
		case opcx_dsub:
		case opcx_lmul:
		case opcx_dmul:
		case opcx_ldiv:
		case opcx_ddiv:
		case opcx_lrem:
		case opcx_drem:
		case opcx_lneg:
		case opcx_dneg:
		case opcx_i2l:
		case opcx_i2d:
		case opcx_l2d:
		case opcx_f2l:
		case opcx_f2d:
		case opcx_d2l:
		case opcx_land:
		case opcx_lor:
		case opcx_lxor:
		case opcx_lshl:
		case opcx_lshr:
		case opcx_lushr:
			return (2);

		case opcx_ldc:
			// If we're loading a Long or Double, the category is 2.
			if ((this.operand instanceof Long)
					|| (this.operand instanceof Double)) {
				return (2);

			} else {
				return (1);
			}

		case opcx_invokevirtual:
		case opcx_invokespecial:
		case opcx_invokeinterface:
		case opcx_invokestatic:
			// If the return type is wide, then the category is 2.
			final MemberRef callee = (MemberRef) this.operand;
			if (callee.nameAndType().type().returnType().isWide()) {
				return (2);

			} else {
				return (1);
			}

		default:
			return (1);
		}
	}

	/**
	 * Big switch statement to call the appropriate method of an instruction
	 * visitor.
	 * 
	 * @param visitor
	 *            The instruction visitor.
	 */
	public void visit(final InstructionVisitor visitor) {
		switch (opcodeClass()) {
		case opcx_nop:
			visitor.visit_nop(this);
			break;
		case opcx_ldc:
			visitor.visit_ldc(this);
			break;
		case opcx_iload:
			visitor.visit_iload(this);
			break;
		case opcx_lload:
			visitor.visit_lload(this);
			break;
		case opcx_fload:
			visitor.visit_fload(this);
			break;
		case opcx_dload:
			visitor.visit_dload(this);
			break;
		case opcx_aload:
			visitor.visit_aload(this);
			break;
		case opcx_iaload:
			visitor.visit_iaload(this);
			break;
		case opcx_laload:
			visitor.visit_laload(this);
			break;
		case opcx_faload:
			visitor.visit_faload(this);
			break;
		case opcx_daload:
			visitor.visit_daload(this);
			break;
		case opcx_aaload:
			visitor.visit_aaload(this);
			break;
		case opcx_baload:
			visitor.visit_baload(this);
			break;
		case opcx_caload:
			visitor.visit_caload(this);
			break;
		case opcx_saload:
			visitor.visit_saload(this);
			break;
		case opcx_istore:
			visitor.visit_istore(this);
			break;
		case opcx_lstore:
			visitor.visit_lstore(this);
			break;
		case opcx_fstore:
			visitor.visit_fstore(this);
			break;
		case opcx_dstore:
			visitor.visit_dstore(this);
			break;
		case opcx_astore:
			visitor.visit_astore(this);
			break;
		case opcx_iastore:
			visitor.visit_iastore(this);
			break;
		case opcx_lastore:
			visitor.visit_lastore(this);
			break;
		case opcx_fastore:
			visitor.visit_fastore(this);
			break;
		case opcx_dastore:
			visitor.visit_dastore(this);
			break;
		case opcx_aastore:
			visitor.visit_aastore(this);
			break;
		case opcx_bastore:
			visitor.visit_bastore(this);
			break;
		case opcx_castore:
			visitor.visit_castore(this);
			break;
		case opcx_sastore:
			visitor.visit_sastore(this);
			break;
		case opcx_pop:
			visitor.visit_pop(this);
			break;
		case opcx_pop2:
			visitor.visit_pop2(this);
			break;
		case opcx_dup:
			visitor.visit_dup(this);
			break;
		case opcx_dup_x1:
			visitor.visit_dup_x1(this);
			break;
		case opcx_dup_x2:
			visitor.visit_dup_x2(this);
			break;
		case opcx_dup2:
			visitor.visit_dup2(this);
			break;
		case opcx_dup2_x1:
			visitor.visit_dup2_x1(this);
			break;
		case opcx_dup2_x2:
			visitor.visit_dup2_x2(this);
			break;
		case opcx_swap:
			visitor.visit_swap(this);
			break;
		case opcx_iadd:
			visitor.visit_iadd(this);
			break;
		case opcx_ladd:
			visitor.visit_ladd(this);
			break;
		case opcx_fadd:
			visitor.visit_fadd(this);
			break;
		case opcx_dadd:
			visitor.visit_dadd(this);
			break;
		case opcx_isub:
			visitor.visit_isub(this);
			break;
		case opcx_lsub:
			visitor.visit_lsub(this);
			break;
		case opcx_fsub:
			visitor.visit_fsub(this);
			break;
		case opcx_dsub:
			visitor.visit_dsub(this);
			break;
		case opcx_imul:
			visitor.visit_imul(this);
			break;
		case opcx_lmul:
			visitor.visit_lmul(this);
			break;
		case opcx_fmul:
			visitor.visit_fmul(this);
			break;
		case opcx_dmul:
			visitor.visit_dmul(this);
			break;
		case opcx_idiv:
			visitor.visit_idiv(this);
			break;
		case opcx_ldiv:
			visitor.visit_ldiv(this);
			break;
		case opcx_fdiv:
			visitor.visit_fdiv(this);
			break;
		case opcx_ddiv:
			visitor.visit_ddiv(this);
			break;
		case opcx_irem:
			visitor.visit_irem(this);
			break;
		case opcx_lrem:
			visitor.visit_lrem(this);
			break;
		case opcx_frem:
			visitor.visit_frem(this);
			break;
		case opcx_drem:
			visitor.visit_drem(this);
			break;
		case opcx_ineg:
			visitor.visit_ineg(this);
			break;
		case opcx_lneg:
			visitor.visit_lneg(this);
			break;
		case opcx_fneg:
			visitor.visit_fneg(this);
			break;
		case opcx_dneg:
			visitor.visit_dneg(this);
			break;
		case opcx_ishl:
			visitor.visit_ishl(this);
			break;
		case opcx_lshl:
			visitor.visit_lshl(this);
			break;
		case opcx_ishr:
			visitor.visit_ishr(this);
			break;
		case opcx_lshr:
			visitor.visit_lshr(this);
			break;
		case opcx_iushr:
			visitor.visit_iushr(this);
			break;
		case opcx_lushr:
			visitor.visit_lushr(this);
			break;
		case opcx_iand:
			visitor.visit_iand(this);
			break;
		case opcx_land:
			visitor.visit_land(this);
			break;
		case opcx_ior:
			visitor.visit_ior(this);
			break;
		case opcx_lor:
			visitor.visit_lor(this);
			break;
		case opcx_ixor:
			visitor.visit_ixor(this);
			break;
		case opcx_lxor:
			visitor.visit_lxor(this);
			break;
		case opcx_iinc:
			visitor.visit_iinc(this);
			break;
		case opcx_i2l:
			visitor.visit_i2l(this);
			break;
		case opcx_i2f:
			visitor.visit_i2f(this);
			break;
		case opcx_i2d:
			visitor.visit_i2d(this);
			break;
		case opcx_l2i:
			visitor.visit_l2i(this);
			break;
		case opcx_l2f:
			visitor.visit_l2f(this);
			break;
		case opcx_l2d:
			visitor.visit_l2d(this);
			break;
		case opcx_f2i:
			visitor.visit_f2i(this);
			break;
		case opcx_f2l:
			visitor.visit_f2l(this);
			break;
		case opcx_f2d:
			visitor.visit_f2d(this);
			break;
		case opcx_d2i:
			visitor.visit_d2i(this);
			break;
		case opcx_d2l:
			visitor.visit_d2l(this);
			break;
		case opcx_d2f:
			visitor.visit_d2f(this);
			break;
		case opcx_i2b:
			visitor.visit_i2b(this);
			break;
		case opcx_i2c:
			visitor.visit_i2c(this);
			break;
		case opcx_i2s:
			visitor.visit_i2s(this);
			break;
		case opcx_lcmp:
			visitor.visit_lcmp(this);
			break;
		case opcx_fcmpl:
			visitor.visit_fcmpl(this);
			break;
		case opcx_fcmpg:
			visitor.visit_fcmpg(this);
			break;
		case opcx_dcmpl:
			visitor.visit_dcmpl(this);
			break;
		case opcx_dcmpg:
			visitor.visit_dcmpg(this);
			break;
		case opcx_ifeq:
			visitor.visit_ifeq(this);
			break;
		case opcx_ifne:
			visitor.visit_ifne(this);
			break;
		case opcx_iflt:
			visitor.visit_iflt(this);
			break;
		case opcx_ifge:
			visitor.visit_ifge(this);
			break;
		case opcx_ifgt:
			visitor.visit_ifgt(this);
			break;
		case opcx_ifle:
			visitor.visit_ifle(this);
			break;
		case opcx_if_icmpeq:
			visitor.visit_if_icmpeq(this);
			break;
		case opcx_if_icmpne:
			visitor.visit_if_icmpne(this);
			break;
		case opcx_if_icmplt:
			visitor.visit_if_icmplt(this);
			break;
		case opcx_if_icmpge:
			visitor.visit_if_icmpge(this);
			break;
		case opcx_if_icmpgt:
			visitor.visit_if_icmpgt(this);
			break;
		case opcx_if_icmple:
			visitor.visit_if_icmple(this);
			break;
		case opcx_if_acmpeq:
			visitor.visit_if_acmpeq(this);
			break;
		case opcx_if_acmpne:
			visitor.visit_if_acmpne(this);
			break;
		case opcx_goto:
			visitor.visit_goto(this);
			break;
		case opcx_jsr:
			visitor.visit_jsr(this);
			break;
		case opcx_ret:
			visitor.visit_ret(this);
			break;
		case opcx_switch:
			visitor.visit_switch(this);
			break;
		case opcx_ireturn:
			visitor.visit_ireturn(this);
			break;
		case opcx_lreturn:
			visitor.visit_lreturn(this);
			break;
		case opcx_freturn:
			visitor.visit_freturn(this);
			break;
		case opcx_dreturn:
			visitor.visit_dreturn(this);
			break;
		case opcx_areturn:
			visitor.visit_areturn(this);
			break;
		case opcx_return:
			visitor.visit_return(this);
			break;
		case opcx_getstatic:
			visitor.visit_getstatic(this);
			break;
		case opcx_putstatic:
			visitor.visit_putstatic(this);
			break;
		case opcx_putstatic_nowb:
			visitor.visit_putstatic_nowb(this);
			break;
		case opcx_getfield:
			visitor.visit_getfield(this);
			break;
		case opcx_putfield:
			visitor.visit_putfield(this);
			break;
		case opcx_putfield_nowb:
			visitor.visit_putfield_nowb(this);
			break;
		case opcx_invokevirtual:
			visitor.visit_invokevirtual(this);
			break;
		case opcx_invokespecial:
			visitor.visit_invokespecial(this);
			break;
		case opcx_invokestatic:
			visitor.visit_invokestatic(this);
			break;
		case opcx_invokeinterface:
			visitor.visit_invokeinterface(this);
			break;
		case opcx_new:
			visitor.visit_new(this);
			break;
		case opcx_newarray:
			visitor.visit_newarray(this);
			break;
		case opcx_arraylength:
			visitor.visit_arraylength(this);
			break;
		case opcx_athrow:
			visitor.visit_athrow(this);
			break;
		case opcx_checkcast:
			visitor.visit_checkcast(this);
			break;
		case opcx_instanceof:
			visitor.visit_instanceof(this);
			break;
		case opcx_monitorenter:
			visitor.visit_monitorenter(this);
			break;
		case opcx_monitorexit:
			visitor.visit_monitorexit(this);
			break;
		case opcx_multianewarray:
			visitor.visit_multianewarray(this);
			break;
		case opcx_ifnull:
			visitor.visit_ifnull(this);
			break;
		case opcx_ifnonnull:
			visitor.visit_ifnonnull(this);
			break;
		case opcx_rc:
			visitor.visit_rc(this);
			break;
		case opcx_aupdate:
			visitor.visit_aupdate(this);
			break;
		case opcx_supdate:
			visitor.visit_supdate(this);
			break;
		case opcx_aswizzle:
			visitor.visit_aswizzle(this);
			break;
		case opcx_aswrange:
			visitor.visit_aswrange(this);
			break;
		}
	}
}
