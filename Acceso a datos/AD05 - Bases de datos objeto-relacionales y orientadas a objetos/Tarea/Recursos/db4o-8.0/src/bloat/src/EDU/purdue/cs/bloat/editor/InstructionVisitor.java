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

/**
 * The visitor pattern allows functionality to be added to a number of classes
 * (or in this case one class, <tt>Instruction</tt>, that can vary in
 * behavior) without modifying the classes themselves. Additionally, the visitor
 * pattern simulates double dispatching. For instance <tt>visit</tt> method of
 * <tt>Instruction</tt> calls a particular method of
 * <tt>InstructionVisitor</tt> based on the <tt>Instruction</tt>'s opcode.
 * <p>
 * <tt>InstructionVisitor</tt> provides an interface for performing actions
 * based on the instruction type. Classes implementing this interface should not
 * be able to miss any of the instruction types. This interface was created as
 * an alternative to having 138 different subtypes of Instruction.
 * 
 * @see Instruction#visit
 * @see EDU.purdue.cs.bloat.tree.Tree
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public interface InstructionVisitor {
	public void visit_nop(Instruction inst);

	public void visit_ldc(Instruction inst);

	public void visit_iload(Instruction inst);

	public void visit_lload(Instruction inst);

	public void visit_fload(Instruction inst);

	public void visit_dload(Instruction inst);

	public void visit_aload(Instruction inst);

	public void visit_iaload(Instruction inst);

	public void visit_laload(Instruction inst);

	public void visit_faload(Instruction inst);

	public void visit_daload(Instruction inst);

	public void visit_aaload(Instruction inst);

	public void visit_baload(Instruction inst);

	public void visit_caload(Instruction inst);

	public void visit_saload(Instruction inst);

	public void visit_istore(Instruction inst);

	public void visit_lstore(Instruction inst);

	public void visit_fstore(Instruction inst);

	public void visit_dstore(Instruction inst);

	public void visit_astore(Instruction inst);

	public void visit_iastore(Instruction inst);

	public void visit_lastore(Instruction inst);

	public void visit_fastore(Instruction inst);

	public void visit_dastore(Instruction inst);

	public void visit_aastore(Instruction inst);

	public void visit_bastore(Instruction inst);

	public void visit_castore(Instruction inst);

	public void visit_sastore(Instruction inst);

	public void visit_pop(Instruction inst);

	public void visit_pop2(Instruction inst);

	public void visit_dup(Instruction inst);

	public void visit_dup_x1(Instruction inst);

	public void visit_dup_x2(Instruction inst);

	public void visit_dup2(Instruction inst);

	public void visit_dup2_x1(Instruction inst);

	public void visit_dup2_x2(Instruction inst);

	public void visit_swap(Instruction inst);

	public void visit_iadd(Instruction inst);

	public void visit_ladd(Instruction inst);

	public void visit_fadd(Instruction inst);

	public void visit_dadd(Instruction inst);

	public void visit_isub(Instruction inst);

	public void visit_lsub(Instruction inst);

	public void visit_fsub(Instruction inst);

	public void visit_dsub(Instruction inst);

	public void visit_imul(Instruction inst);

	public void visit_lmul(Instruction inst);

	public void visit_fmul(Instruction inst);

	public void visit_dmul(Instruction inst);

	public void visit_idiv(Instruction inst);

	public void visit_ldiv(Instruction inst);

	public void visit_fdiv(Instruction inst);

	public void visit_ddiv(Instruction inst);

	public void visit_irem(Instruction inst);

	public void visit_lrem(Instruction inst);

	public void visit_frem(Instruction inst);

	public void visit_drem(Instruction inst);

	public void visit_ineg(Instruction inst);

	public void visit_lneg(Instruction inst);

	public void visit_fneg(Instruction inst);

	public void visit_dneg(Instruction inst);

	public void visit_ishl(Instruction inst);

	public void visit_lshl(Instruction inst);

	public void visit_ishr(Instruction inst);

	public void visit_lshr(Instruction inst);

	public void visit_iushr(Instruction inst);

	public void visit_lushr(Instruction inst);

	public void visit_iand(Instruction inst);

	public void visit_land(Instruction inst);

	public void visit_ior(Instruction inst);

	public void visit_lor(Instruction inst);

	public void visit_ixor(Instruction inst);

	public void visit_lxor(Instruction inst);

	public void visit_iinc(Instruction inst);

	public void visit_i2l(Instruction inst);

	public void visit_i2f(Instruction inst);

	public void visit_i2d(Instruction inst);

	public void visit_l2i(Instruction inst);

	public void visit_l2f(Instruction inst);

	public void visit_l2d(Instruction inst);

	public void visit_f2i(Instruction inst);

	public void visit_f2l(Instruction inst);

	public void visit_f2d(Instruction inst);

	public void visit_d2i(Instruction inst);

	public void visit_d2l(Instruction inst);

	public void visit_d2f(Instruction inst);

	public void visit_i2b(Instruction inst);

	public void visit_i2c(Instruction inst);

	public void visit_i2s(Instruction inst);

	public void visit_lcmp(Instruction inst);

	public void visit_fcmpl(Instruction inst);

	public void visit_fcmpg(Instruction inst);

	public void visit_dcmpl(Instruction inst);

	public void visit_dcmpg(Instruction inst);

	public void visit_ifeq(Instruction inst);

	public void visit_ifne(Instruction inst);

	public void visit_iflt(Instruction inst);

	public void visit_ifge(Instruction inst);

	public void visit_ifgt(Instruction inst);

	public void visit_ifle(Instruction inst);

	public void visit_if_icmpeq(Instruction inst);

	public void visit_if_icmpne(Instruction inst);

	public void visit_if_icmplt(Instruction inst);

	public void visit_if_icmpge(Instruction inst);

	public void visit_if_icmpgt(Instruction inst);

	public void visit_if_icmple(Instruction inst);

	public void visit_if_acmpeq(Instruction inst);

	public void visit_if_acmpne(Instruction inst);

	public void visit_goto(Instruction inst);

	public void visit_jsr(Instruction inst);

	public void visit_ret(Instruction inst);

	public void visit_switch(Instruction inst);

	public void visit_ireturn(Instruction inst);

	public void visit_lreturn(Instruction inst);

	public void visit_freturn(Instruction inst);

	public void visit_dreturn(Instruction inst);

	public void visit_areturn(Instruction inst);

	public void visit_return(Instruction inst);

	public void visit_getstatic(Instruction inst);

	public void visit_putstatic(Instruction inst);

	public void visit_putstatic_nowb(Instruction inst);

	public void visit_getfield(Instruction inst);

	public void visit_putfield(Instruction inst);

	public void visit_putfield_nowb(Instruction inst);

	public void visit_invokevirtual(Instruction inst);

	public void visit_invokespecial(Instruction inst);

	public void visit_invokestatic(Instruction inst);

	public void visit_invokeinterface(Instruction inst);

	public void visit_new(Instruction inst);

	public void visit_newarray(Instruction inst);

	public void visit_arraylength(Instruction inst);

	public void visit_athrow(Instruction inst);

	public void visit_checkcast(Instruction inst);

	public void visit_instanceof(Instruction inst);

	public void visit_monitorenter(Instruction inst);

	public void visit_monitorexit(Instruction inst);

	public void visit_multianewarray(Instruction inst);

	public void visit_ifnull(Instruction inst);

	public void visit_ifnonnull(Instruction inst);

	public void visit_rc(Instruction inst);

	public void visit_aupdate(Instruction inst);

	public void visit_supdate(Instruction inst);

	public void visit_aswizzle(Instruction inst);

	public void visit_aswrange(Instruction inst);
}
