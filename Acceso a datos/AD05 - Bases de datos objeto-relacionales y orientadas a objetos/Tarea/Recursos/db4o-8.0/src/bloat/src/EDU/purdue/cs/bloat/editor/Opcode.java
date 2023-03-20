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
 * Opcode is an interface containing constants defining the opcodes of
 * instructions and related constants.
 * <ul>
 * <li> opc_XXX are the opcodes.
 * 
 * <li> opcx_XXX are the opcode classes. These are used externally by
 * Instruction.
 * 
 * <li> opcNames is an array of opcode names, indexed by the opcode.
 * 
 * <li> opcSize is an array of the bytecode instruction lengths, indexed by the
 * opcode.
 * 
 * <li> opcXMap is an array, indexed by the opcode, mapping opcodes to opcode
 * classes.
 * </ul>
 * 
 * @see Instruction
 * 
 * @author Nate Nystrom (<a
 *         href="mailto:nystrom@cs.purdue.edu">nystrom@cs.purdue.edu</a>)
 */
public interface Opcode {
	// Extended opcodes
	public static final int opc_nop = 0;

	public static final int opc_aconst_null = 1;

	public static final int opc_iconst_m1 = 2;

	public static final int opc_iconst_0 = 3;

	public static final int opc_iconst_1 = 4;

	public static final int opc_iconst_2 = 5;

	public static final int opc_iconst_3 = 6;

	public static final int opc_iconst_4 = 7;

	public static final int opc_iconst_5 = 8;

	public static final int opc_lconst_0 = 9;

	public static final int opc_lconst_1 = 10;

	public static final int opc_fconst_0 = 11;

	public static final int opc_fconst_1 = 12;

	public static final int opc_fconst_2 = 13;

	public static final int opc_dconst_0 = 14;

	public static final int opc_dconst_1 = 15;

	public static final int opc_bipush = 16;

	public static final int opc_sipush = 17;

	public static final int opc_ldc = 18;

	public static final int opc_ldc_w = 19;

	public static final int opc_ldc2_w = 20;

	public static final int opc_iload = 21;

	public static final int opc_lload = 22;

	public static final int opc_fload = 23;

	public static final int opc_dload = 24;

	public static final int opc_aload = 25;

	public static final int opc_iload_0 = 26;

	public static final int opc_iload_1 = 27;

	public static final int opc_iload_2 = 28;

	public static final int opc_iload_3 = 29;

	public static final int opc_lload_0 = 30;

	public static final int opc_lload_1 = 31;

	public static final int opc_lload_2 = 32;

	public static final int opc_lload_3 = 33;

	public static final int opc_fload_0 = 34;

	public static final int opc_fload_1 = 35;

	public static final int opc_fload_2 = 36;

	public static final int opc_fload_3 = 37;

	public static final int opc_dload_0 = 38;

	public static final int opc_dload_1 = 39;

	public static final int opc_dload_2 = 40;

	public static final int opc_dload_3 = 41;

	public static final int opc_aload_0 = 42;

	public static final int opc_aload_1 = 43;

	public static final int opc_aload_2 = 44;

	public static final int opc_aload_3 = 45;

	public static final int opc_iaload = 46;

	public static final int opc_laload = 47;

	public static final int opc_faload = 48;

	public static final int opc_daload = 49;

	public static final int opc_aaload = 50;

	public static final int opc_baload = 51;

	public static final int opc_caload = 52;

	public static final int opc_saload = 53;

	public static final int opc_istore = 54;

	public static final int opc_lstore = 55;

	public static final int opc_fstore = 56;

	public static final int opc_dstore = 57;

	public static final int opc_astore = 58;

	public static final int opc_istore_0 = 59;

	public static final int opc_istore_1 = 60;

	public static final int opc_istore_2 = 61;

	public static final int opc_istore_3 = 62;

	public static final int opc_lstore_0 = 63;

	public static final int opc_lstore_1 = 64;

	public static final int opc_lstore_2 = 65;

	public static final int opc_lstore_3 = 66;

	public static final int opc_fstore_0 = 67;

	public static final int opc_fstore_1 = 68;

	public static final int opc_fstore_2 = 69;

	public static final int opc_fstore_3 = 70;

	public static final int opc_dstore_0 = 71;

	public static final int opc_dstore_1 = 72;

	public static final int opc_dstore_2 = 73;

	public static final int opc_dstore_3 = 74;

	public static final int opc_astore_0 = 75;

	public static final int opc_astore_1 = 76;

	public static final int opc_astore_2 = 77;

	public static final int opc_astore_3 = 78;

	public static final int opc_iastore = 79;

	public static final int opc_lastore = 80;

	public static final int opc_fastore = 81;

	public static final int opc_dastore = 82;

	public static final int opc_aastore = 83;

	public static final int opc_bastore = 84;

	public static final int opc_castore = 85;

	public static final int opc_sastore = 86;

	public static final int opc_pop = 87;

	public static final int opc_pop2 = 88;

	public static final int opc_dup = 89;

	public static final int opc_dup_x1 = 90;

	public static final int opc_dup_x2 = 91;

	public static final int opc_dup2 = 92;

	public static final int opc_dup2_x1 = 93;

	public static final int opc_dup2_x2 = 94;

	public static final int opc_swap = 95;

	public static final int opc_iadd = 96;

	public static final int opc_ladd = 97;

	public static final int opc_fadd = 98;

	public static final int opc_dadd = 99;

	public static final int opc_isub = 100;

	public static final int opc_lsub = 101;

	public static final int opc_fsub = 102;

	public static final int opc_dsub = 103;

	public static final int opc_imul = 104;

	public static final int opc_lmul = 105;

	public static final int opc_fmul = 106;

	public static final int opc_dmul = 107;

	public static final int opc_idiv = 108;

	public static final int opc_ldiv = 109;

	public static final int opc_fdiv = 110;

	public static final int opc_ddiv = 111;

	public static final int opc_irem = 112;

	public static final int opc_lrem = 113;

	public static final int opc_frem = 114;

	public static final int opc_drem = 115;

	public static final int opc_ineg = 116;

	public static final int opc_lneg = 117;

	public static final int opc_fneg = 118;

	public static final int opc_dneg = 119;

	public static final int opc_ishl = 120;

	public static final int opc_lshl = 121;

	public static final int opc_ishr = 122;

	public static final int opc_lshr = 123;

	public static final int opc_iushr = 124;

	public static final int opc_lushr = 125;

	public static final int opc_iand = 126;

	public static final int opc_land = 127;

	public static final int opc_ior = 128;

	public static final int opc_lor = 129;

	public static final int opc_ixor = 130;

	public static final int opc_lxor = 131;

	public static final int opc_iinc = 132;

	public static final int opc_i2l = 133;

	public static final int opc_i2f = 134;

	public static final int opc_i2d = 135;

	public static final int opc_l2i = 136;

	public static final int opc_l2f = 137;

	public static final int opc_l2d = 138;

	public static final int opc_f2i = 139;

	public static final int opc_f2l = 140;

	public static final int opc_f2d = 141;

	public static final int opc_d2i = 142;

	public static final int opc_d2l = 143;

	public static final int opc_d2f = 144;

	public static final int opc_i2b = 145;

	public static final int opc_i2c = 146;

	public static final int opc_i2s = 147;

	public static final int opc_lcmp = 148;

	public static final int opc_fcmpl = 149;

	public static final int opc_fcmpg = 150;

	public static final int opc_dcmpl = 151;

	public static final int opc_dcmpg = 152;

	public static final int opc_ifeq = 153;

	public static final int opc_ifne = 154;

	public static final int opc_iflt = 155;

	public static final int opc_ifge = 156;

	public static final int opc_ifgt = 157;

	public static final int opc_ifle = 158;

	public static final int opc_if_icmpeq = 159;

	public static final int opc_if_icmpne = 160;

	public static final int opc_if_icmplt = 161;

	public static final int opc_if_icmpge = 162;

	public static final int opc_if_icmpgt = 163;

	public static final int opc_if_icmple = 164;

	public static final int opc_if_acmpeq = 165;

	public static final int opc_if_acmpne = 166;

	public static final int opc_goto = 167;

	public static final int opc_jsr = 168;

	public static final int opc_ret = 169;

	public static final int opc_tableswitch = 170;

	public static final int opc_lookupswitch = 171;

	public static final int opc_ireturn = 172;

	public static final int opc_lreturn = 173;

	public static final int opc_freturn = 174;

	public static final int opc_dreturn = 175;

	public static final int opc_areturn = 176;

	public static final int opc_return = 177;

	public static final int opc_getstatic = 178;

	public static final int opc_putstatic = 179;

	public static final int opc_getfield = 180;

	public static final int opc_putfield = 181;

	public static final int opc_invokevirtual = 182;

	public static final int opc_invokespecial = 183;

	public static final int opc_invokestatic = 184;

	public static final int opc_invokeinterface = 185;

	public static final int opc_xxxunusedxxx = 186;

	public static final int opc_new = 187;

	public static final int opc_newarray = 188;

	public static final int opc_anewarray = 189;

	public static final int opc_arraylength = 190;

	public static final int opc_athrow = 191;

	public static final int opc_checkcast = 192;

	public static final int opc_instanceof = 193;

	public static final int opc_monitorenter = 194;

	public static final int opc_monitorexit = 195;

	public static final int opc_wide = 196;

	public static final int opc_multianewarray = 197;

	public static final int opc_ifnull = 198;

	public static final int opc_ifnonnull = 199;

	public static final int opc_goto_w = 200;

	public static final int opc_jsr_w = 201;

	public static final int opc_breakpoint = 202;

	// Opcodes for persistence
	public static final int opc_rc = 237; // residency check

	public static final int opc_aupdate = 238; // pointer update check

	public static final int opc_supdate = 239; // scalar update check

	public static final int opc_aswizzle = 240; // array swizzle check

	public static final int opc_aswrange = 241; // array range swizzle

	public static final int opc_putfield_nowb = 204;

	public static final int opc_putstatic_nowb = 205;

	// Opcode classes (similar opcodes are squeezed together).

	/**
	 * Opcode class for nop, xxxunusedxxx, wide, breakpoint, and opcodes
	 * 203-254.
	 */
	public static final int opcx_nop = Opcode.opc_nop;

	/**
	 * Opcode class for aconst_null, iconst_m1, iconst_0, iconst_1, iconst_2,
	 * iconst_3, iconst_4, iconst_5, lconst_0, lconst_1, fconst_0, fconst_1,
	 * fconst_2, dconst_0, dconst_1, bipush, sipush, ldc, ldc_w, ldc2_w.
	 */
	public static final int opcx_ldc = Opcode.opc_ldc;

	/**
	 * Opcode class for iload, iload_0, iload_1, iload_2, iload_3.
	 */
	public static final int opcx_iload = Opcode.opc_iload;

	/**
	 * Opcode class for lload, lload_0, lload_1, lload_2, lload_3.
	 */
	public static final int opcx_lload = Opcode.opc_lload;

	/**
	 * Opcode class for fload, fload_0, fload_1, fload_2, fload_3.
	 */
	public static final int opcx_fload = Opcode.opc_fload;

	/**
	 * Opcode class for dload, dload_0, dload_1, dload_2, dload_3.
	 */
	public static final int opcx_dload = Opcode.opc_dload;

	/**
	 * Opcode class for aload, aload_0, aload_1, aload_2, aload_3.
	 */
	public static final int opcx_aload = Opcode.opc_aload;

	/**
	 * Opcode class for iaload.
	 */
	public static final int opcx_iaload = Opcode.opc_iaload;

	/**
	 * Opcode class for laload.
	 */
	public static final int opcx_laload = Opcode.opc_laload;

	/**
	 * Opcode class for faload.
	 */
	public static final int opcx_faload = Opcode.opc_faload;

	/**
	 * Opcode class for daload.
	 */
	public static final int opcx_daload = Opcode.opc_daload;

	/**
	 * Opcode class for aaload.
	 */
	public static final int opcx_aaload = Opcode.opc_aaload;

	/**
	 * Opcode class for baload.
	 */
	public static final int opcx_baload = Opcode.opc_baload;

	/**
	 * Opcode class for caload.
	 */
	public static final int opcx_caload = Opcode.opc_caload;

	/**
	 * Opcode class for saload.
	 */
	public static final int opcx_saload = Opcode.opc_saload;

	/**
	 * Opcode class for istore, istore_0, istore_1, istore_2, istore_3.
	 */
	public static final int opcx_istore = Opcode.opc_istore;

	/**
	 * Opcode class for lstore, lstore_0, lstore_1, lstore_2, lstore_3.
	 */
	public static final int opcx_lstore = Opcode.opc_lstore;

	/**
	 * Opcode class for fstore, fstore_0, fstore_1, fstore_2, fstore_3.
	 */
	public static final int opcx_fstore = Opcode.opc_fstore;

	/**
	 * Opcode class for dstore, dstore_0, dstore_1, dstore_2, dstore_3.
	 */
	public static final int opcx_dstore = Opcode.opc_dstore;

	/**
	 * Opcode class for astore, astore_0, astore_1, astore_2, astore_3.
	 */
	public static final int opcx_astore = Opcode.opc_astore;

	/**
	 * Opcode class for iastore.
	 */
	public static final int opcx_iastore = Opcode.opc_iastore;

	/**
	 * Opcode class for lastore.
	 */
	public static final int opcx_lastore = Opcode.opc_lastore;

	/**
	 * Opcode class for fastore.
	 */
	public static final int opcx_fastore = Opcode.opc_fastore;

	/**
	 * Opcode class for dastore.
	 */
	public static final int opcx_dastore = Opcode.opc_dastore;

	/**
	 * Opcode class for aastore.
	 */
	public static final int opcx_aastore = Opcode.opc_aastore;

	/**
	 * Opcode class for bastore.
	 */
	public static final int opcx_bastore = Opcode.opc_bastore;

	/**
	 * Opcode class for castore.
	 */
	public static final int opcx_castore = Opcode.opc_castore;

	/**
	 * Opcode class for sastore.
	 */
	public static final int opcx_sastore = Opcode.opc_sastore;

	/**
	 * Opcode class for pop.
	 */
	public static final int opcx_pop = Opcode.opc_pop;

	/**
	 * Opcode class for pop2.
	 */
	public static final int opcx_pop2 = Opcode.opc_pop2;

	/**
	 * Opcode class for dup.
	 */
	public static final int opcx_dup = Opcode.opc_dup;

	/**
	 * Opcode class for dup_x1.
	 */
	public static final int opcx_dup_x1 = Opcode.opc_dup_x1;

	/**
	 * Opcode class for dup_x2.
	 */
	public static final int opcx_dup_x2 = Opcode.opc_dup_x2;

	/**
	 * Opcode class for dup2.
	 */
	public static final int opcx_dup2 = Opcode.opc_dup2;

	/**
	 * Opcode class for dup2_x1.
	 */
	public static final int opcx_dup2_x1 = Opcode.opc_dup2_x1;

	/**
	 * Opcode class for dup2_x2.
	 */
	public static final int opcx_dup2_x2 = Opcode.opc_dup2_x2;

	/**
	 * Opcode class for swap.
	 */
	public static final int opcx_swap = Opcode.opc_swap;

	/**
	 * Opcode class for iadd.
	 */
	public static final int opcx_iadd = Opcode.opc_iadd;

	/**
	 * Opcode class for ladd.
	 */
	public static final int opcx_ladd = Opcode.opc_ladd;

	/**
	 * Opcode class for fadd.
	 */
	public static final int opcx_fadd = Opcode.opc_fadd;

	/**
	 * Opcode class for dadd.
	 */
	public static final int opcx_dadd = Opcode.opc_dadd;

	/**
	 * Opcode class for isub.
	 */
	public static final int opcx_isub = Opcode.opc_isub;

	/**
	 * Opcode class for lsub.
	 */
	public static final int opcx_lsub = Opcode.opc_lsub;

	/**
	 * Opcode class for fsub.
	 */
	public static final int opcx_fsub = Opcode.opc_fsub;

	/**
	 * Opcode class for dsub.
	 */
	public static final int opcx_dsub = Opcode.opc_dsub;

	/**
	 * Opcode class for imul.
	 */
	public static final int opcx_imul = Opcode.opc_imul;

	/**
	 * Opcode class for lmul.
	 */
	public static final int opcx_lmul = Opcode.opc_lmul;

	/**
	 * Opcode class for fmul.
	 */
	public static final int opcx_fmul = Opcode.opc_fmul;

	/**
	 * Opcode class for dmul.
	 */
	public static final int opcx_dmul = Opcode.opc_dmul;

	/**
	 * Opcode class for idiv.
	 */
	public static final int opcx_idiv = Opcode.opc_idiv;

	/**
	 * Opcode class for ldiv.
	 */
	public static final int opcx_ldiv = Opcode.opc_ldiv;

	/**
	 * Opcode class for fdiv.
	 */
	public static final int opcx_fdiv = Opcode.opc_fdiv;

	/**
	 * Opcode class for ddiv.
	 */
	public static final int opcx_ddiv = Opcode.opc_ddiv;

	/**
	 * Opcode class for irem.
	 */
	public static final int opcx_irem = Opcode.opc_irem;

	/**
	 * Opcode class for lrem.
	 */
	public static final int opcx_lrem = Opcode.opc_lrem;

	/**
	 * Opcode class for frem.
	 */
	public static final int opcx_frem = Opcode.opc_frem;

	/**
	 * Opcode class for drem.
	 */
	public static final int opcx_drem = Opcode.opc_drem;

	/**
	 * Opcode class for ineg.
	 */
	public static final int opcx_ineg = Opcode.opc_ineg;

	/**
	 * Opcode class for lneg.
	 */
	public static final int opcx_lneg = Opcode.opc_lneg;

	/**
	 * Opcode class for fneg.
	 */
	public static final int opcx_fneg = Opcode.opc_fneg;

	/**
	 * Opcode class for dneg.
	 */
	public static final int opcx_dneg = Opcode.opc_dneg;

	/**
	 * Opcode class for ishl.
	 */
	public static final int opcx_ishl = Opcode.opc_ishl;

	/**
	 * Opcode class for lshl.
	 */
	public static final int opcx_lshl = Opcode.opc_lshl;

	/**
	 * Opcode class for ishr.
	 */
	public static final int opcx_ishr = Opcode.opc_ishr;

	/**
	 * Opcode class for lshr.
	 */
	public static final int opcx_lshr = Opcode.opc_lshr;

	/**
	 * Opcode class for iushr.
	 */
	public static final int opcx_iushr = Opcode.opc_iushr;

	/**
	 * Opcode class for lushr.
	 */
	public static final int opcx_lushr = Opcode.opc_lushr;

	/**
	 * Opcode class for iand.
	 */
	public static final int opcx_iand = Opcode.opc_iand;

	/**
	 * Opcode class for land.
	 */
	public static final int opcx_land = Opcode.opc_land;

	/**
	 * Opcode class for ior.
	 */
	public static final int opcx_ior = Opcode.opc_ior;

	/**
	 * Opcode class for lor.
	 */
	public static final int opcx_lor = Opcode.opc_lor;

	/**
	 * Opcode class for ixor.
	 */
	public static final int opcx_ixor = Opcode.opc_ixor;

	/**
	 * Opcode class for lxor.
	 */
	public static final int opcx_lxor = Opcode.opc_lxor;

	/**
	 * Opcode class for iinc.
	 */
	public static final int opcx_iinc = Opcode.opc_iinc;

	/**
	 * Opcode class for i2l.
	 */
	public static final int opcx_i2l = Opcode.opc_i2l;

	/**
	 * Opcode class for i2f.
	 */
	public static final int opcx_i2f = Opcode.opc_i2f;

	/**
	 * Opcode class for i2d.
	 */
	public static final int opcx_i2d = Opcode.opc_i2d;

	/**
	 * Opcode class for l2i.
	 */
	public static final int opcx_l2i = Opcode.opc_l2i;

	/**
	 * Opcode class for l2f.
	 */
	public static final int opcx_l2f = Opcode.opc_l2f;

	/**
	 * Opcode class for l2d.
	 */
	public static final int opcx_l2d = Opcode.opc_l2d;

	/**
	 * Opcode class for f2i.
	 */
	public static final int opcx_f2i = Opcode.opc_f2i;

	/**
	 * Opcode class for f2l.
	 */
	public static final int opcx_f2l = Opcode.opc_f2l;

	/**
	 * Opcode class for f2d.
	 */
	public static final int opcx_f2d = Opcode.opc_f2d;

	/**
	 * Opcode class for d2i.
	 */
	public static final int opcx_d2i = Opcode.opc_d2i;

	/**
	 * Opcode class for d2l.
	 */
	public static final int opcx_d2l = Opcode.opc_d2l;

	/**
	 * Opcode class for d2f.
	 */
	public static final int opcx_d2f = Opcode.opc_d2f;

	/**
	 * Opcode class for i2b.
	 */
	public static final int opcx_i2b = Opcode.opc_i2b;

	/**
	 * Opcode class for i2c.
	 */
	public static final int opcx_i2c = Opcode.opc_i2c;

	/**
	 * Opcode class for i2s.
	 */
	public static final int opcx_i2s = Opcode.opc_i2s;

	/**
	 * Opcode class for lcmp.
	 */
	public static final int opcx_lcmp = Opcode.opc_lcmp;

	/**
	 * Opcode class for fcmpl.
	 */
	public static final int opcx_fcmpl = Opcode.opc_fcmpl;

	/**
	 * Opcode class for fcmpg.
	 */
	public static final int opcx_fcmpg = Opcode.opc_fcmpg;

	/**
	 * Opcode class for dcmpl.
	 */
	public static final int opcx_dcmpl = Opcode.opc_dcmpl;

	/**
	 * Opcode class for dcmpg.
	 */
	public static final int opcx_dcmpg = Opcode.opc_dcmpg;

	/**
	 * Opcode class for ifeq.
	 */
	public static final int opcx_ifeq = Opcode.opc_ifeq;

	/**
	 * Opcode class for ifne.
	 */
	public static final int opcx_ifne = Opcode.opc_ifne;

	/**
	 * Opcode class for iflt.
	 */
	public static final int opcx_iflt = Opcode.opc_iflt;

	/**
	 * Opcode class for ifge.
	 */
	public static final int opcx_ifge = Opcode.opc_ifge;

	/**
	 * Opcode class for ifgt.
	 */
	public static final int opcx_ifgt = Opcode.opc_ifgt;

	/**
	 * Opcode class for ifle.
	 */
	public static final int opcx_ifle = Opcode.opc_ifle;

	/**
	 * Opcode class for if_icmpeq.
	 */
	public static final int opcx_if_icmpeq = Opcode.opc_if_icmpeq;

	/**
	 * Opcode class for if_icmpne.
	 */
	public static final int opcx_if_icmpne = Opcode.opc_if_icmpne;

	/**
	 * Opcode class for if_icmplt.
	 */
	public static final int opcx_if_icmplt = Opcode.opc_if_icmplt;

	/**
	 * Opcode class for if_icmpge.
	 */
	public static final int opcx_if_icmpge = Opcode.opc_if_icmpge;

	/**
	 * Opcode class for if_icmpgt.
	 */
	public static final int opcx_if_icmpgt = Opcode.opc_if_icmpgt;

	/**
	 * Opcode class for if_icmple.
	 */
	public static final int opcx_if_icmple = Opcode.opc_if_icmple;

	/**
	 * Opcode class for if_acmpeq.
	 */
	public static final int opcx_if_acmpeq = Opcode.opc_if_acmpeq;

	/**
	 * Opcode class for if_acmpne.
	 */
	public static final int opcx_if_acmpne = Opcode.opc_if_acmpne;

	/**
	 * Opcode class for goto, goto_w.
	 */
	public static final int opcx_goto = Opcode.opc_goto;

	/**
	 * Opcode class for jsr, jsr_w.
	 */
	public static final int opcx_jsr = Opcode.opc_jsr;

	/**
	 * Opcode class for ret.
	 */
	public static final int opcx_ret = Opcode.opc_ret;

	/**
	 * Opcode class for tableswitch, lookupswitch.
	 */
	public static final int opcx_switch = Opcode.opc_tableswitch;

	/**
	 * Opcode class for ireturn.
	 */
	public static final int opcx_ireturn = Opcode.opc_ireturn;

	/**
	 * Opcode class for lreturn.
	 */
	public static final int opcx_lreturn = Opcode.opc_lreturn;

	/**
	 * Opcode class for freturn.
	 */
	public static final int opcx_freturn = Opcode.opc_freturn;

	/**
	 * Opcode class for dreturn.
	 */
	public static final int opcx_dreturn = Opcode.opc_dreturn;

	/**
	 * Opcode class for areturn.
	 */
	public static final int opcx_areturn = Opcode.opc_areturn;

	/**
	 * Opcode class for return.
	 */
	public static final int opcx_return = Opcode.opc_return;

	/**
	 * Opcode class for getstatic.
	 */
	public static final int opcx_getstatic = Opcode.opc_getstatic;

	/**
	 * Opcode class for putstatic.
	 */
	public static final int opcx_putstatic = Opcode.opc_putstatic;

	/**
	 * Opcode class for getfield.
	 */
	public static final int opcx_getfield = Opcode.opc_getfield;

	/**
	 * Opcode class for putfield.
	 */
	public static final int opcx_putfield = Opcode.opc_putfield;

	/**
	 * Opcode class for invokevirtual.
	 */
	public static final int opcx_invokevirtual = Opcode.opc_invokevirtual;

	/**
	 * Opcode class for invokespecial.
	 */
	public static final int opcx_invokespecial = Opcode.opc_invokespecial;

	/**
	 * Opcode class for invokestatic.
	 */
	public static final int opcx_invokestatic = Opcode.opc_invokestatic;

	/**
	 * Opcode class for invokeinterface.
	 */
	public static final int opcx_invokeinterface = Opcode.opc_invokeinterface;

	/**
	 * Opcode class for new.
	 */
	public static final int opcx_new = Opcode.opc_new;

	/**
	 * Opcode class for newarray, anewarray.
	 */
	public static final int opcx_newarray = Opcode.opc_newarray;

	/**
	 * Opcode class for arraylength.
	 */
	public static final int opcx_arraylength = Opcode.opc_arraylength;

	/**
	 * Opcode class for athrow.
	 */
	public static final int opcx_athrow = Opcode.opc_athrow;

	/**
	 * Opcode class for checkcast.
	 */
	public static final int opcx_checkcast = Opcode.opc_checkcast;

	/**
	 * Opcode class for instanceof.
	 */
	public static final int opcx_instanceof = Opcode.opc_instanceof;

	/**
	 * Opcode class for monitorenter.
	 */
	public static final int opcx_monitorenter = Opcode.opc_monitorenter;

	/**
	 * Opcode class for monitorexit.
	 */
	public static final int opcx_monitorexit = Opcode.opc_monitorexit;

	/**
	 * Opcode class for multianewarray.
	 */
	public static final int opcx_multianewarray = Opcode.opc_multianewarray;

	/**
	 * Opcode class for ifnull.
	 */
	public static final int opcx_ifnull = Opcode.opc_ifnull;

	/**
	 * Opcode class for ifnonnull.
	 */
	public static final int opcx_ifnonnull = Opcode.opc_ifnonnull;

	/**
	 * Opcode class for supdate.
	 */
	public static final int opcx_aupdate = Opcode.opc_aupdate;

	/**
	 * Opcode class for supdate.
	 */
	public static final int opcx_supdate = Opcode.opc_supdate;

	/**
	 * Opcode class for rc.
	 */
	public static final int opcx_rc = Opcode.opc_rc;

	/**
	 * Opcode class for aswizzle.
	 */
	public static final int opcx_aswizzle = Opcode.opc_aswizzle;

	/**
	 * Opcode class for aswrange.
	 */
	public static final int opcx_aswrange = Opcode.opc_aswrange;

	/**
	 * Opcode class for putfield_nowb.
	 */
	public static final int opcx_putfield_nowb = Opcode.opc_putfield_nowb;

	/**
	 * Opcode class for putstatic_nowb.
	 */
	public static final int opcx_putstatic_nowb = Opcode.opc_putstatic_nowb;

	/**
	 * An array of opcode names, indexed by the opcode.
	 */
	public static final String[] opcNames = { "nop", "aconst_null",
			"iconst_m1", "iconst_0", "iconst_1", "iconst_2", "iconst_3",
			"iconst_4", "iconst_5", "lconst_0", "lconst_1", "fconst_0",
			"fconst_1", "fconst_2", "dconst_0", "dconst_1", "bipush", "sipush",
			"ldc", "ldc_w", "ldc2_w", "iload", "lload", "fload", "dload",
			"aload", "iload_0", "iload_1", "iload_2", "iload_3", "lload_0",
			"lload_1", "lload_2", "lload_3", "fload_0", "fload_1", "fload_2",
			"fload_3", "dload_0", "dload_1", "dload_2", "dload_3", "aload_0",
			"aload_1", "aload_2", "aload_3", "iaload", "laload", "faload",
			"daload", "aaload", "baload", "caload", "saload", "istore",
			"lstore", "fstore", "dstore", "astore", "istore_0", "istore_1",
			"istore_2", "istore_3", "lstore_0", "lstore_1", "lstore_2",
			"lstore_3", "fstore_0", "fstore_1", "fstore_2", "fstore_3",
			"dstore_0", "dstore_1", "dstore_2", "dstore_3", "astore_0",
			"astore_1", "astore_2", "astore_3", "iastore", "lastore",
			"fastore", "dastore", "aastore", "bastore", "castore", "sastore",
			"pop", "pop2", "dup", "dup_x1", "dup_x2", "dup2", "dup2_x1",
			"dup2_x2", "swap", "iadd", "ladd", "fadd", "dadd", "isub", "lsub",
			"fsub", "dsub", "imul", "lmul", "fmul", "dmul", "idiv", "ldiv",
			"fdiv", "ddiv", "irem", "lrem", "frem", "drem", "ineg", "lneg",
			"fneg", "dneg", "ishl", "lshl", "ishr", "lshr", "iushr", "lushr",
			"iand", "land", "ior", "lor", "ixor", "lxor", "iinc", "i2l", "i2f",
			"i2d", "l2i", "l2f", "l2d", "f2i", "f2l", "f2d", "d2i", "d2l",
			"d2f", "i2b", "i2c", "i2s", "lcmp", "fcmpl", "fcmpg", "dcmpl",
			"dcmpg", "ifeq", "ifne", "iflt", "ifge", "ifgt", "ifle",
			"if_icmpeq", "if_icmpne", "if_icmplt", "if_icmpge", "if_icmpgt",
			"if_icmple", "if_acmpeq", "if_acmpne", "goto", "jsr", "ret",
			"tableswitch", "lookupswitch", "ireturn", "lreturn", "freturn",
			"dreturn", "areturn", "return", "getstatic", "putstatic",
			"getfield", "putfield", "invokevirtual", "invokespecial",
			"invokestatic", "invokeinterface", "xxxunusedxxx", "new",
			"newarray", "anewarray", "arraylength", "athrow", "checkcast",
			"instanceof", "monitorenter", "monitorexit", "wide",
			"multianewarray", "ifnull", "ifnonnull", "goto_w", "jsr_w",
			"breakpoint", "203", "putfield_nowb", "putstatic_nowb", "206",
			"207", "208", "209", "210", "211", "212", "213", "214", "215",
			"216", "217", "218", "219", "220", "221", "222", "223", "224",
			"225", "226", "227", "228", "229", "230", "231", "232", "233",
			"234", "235", "236", "rc", "aupdate", "supdate", "aswizzle",
			"aswrange", "242", "243", "244", "245", "246", "247", "248", "249",
			"250", "251", "252", "253", "254", "255", };

	/**
	 * VARIABLE represent either variable instruction length or a variable
	 * effect on the operand stack, depending on the context.
	 */
	public static final byte VARIABLE = -1;

	/**
	 * An array of the bytecode instruction lengths, indexed by the opcode.
	 */
	public static final byte[] opcSize = { 1, // nop
			1, // aconst_null
			1, // iconst_m1
			1, // iconst_0
			1, // iconst_1
			1, // iconst_2
			1, // iconst_3
			1, // iconst_4
			1, // iconst_5
			1, // lconst_0
			1, // lconst_1
			1, // fconst_0
			1, // fconst_1
			1, // fconst_2
			1, // dconst_0
			1, // dconst_1
			2, // bipush
			3, // sipush
			2, // ldc
			3, // ldc_w
			3, // ldc2_w
			2, // iload
			2, // lload
			2, // fload
			2, // dload
			2, // aload
			1, // iload_0
			1, // iload_1
			1, // iload_2
			1, // iload_3
			1, // lload_0
			1, // lload_1
			1, // lload_2
			1, // lload_3
			1, // fload_0
			1, // fload_1
			1, // fload_2
			1, // fload_3
			1, // dload_0
			1, // dload_1
			1, // dload_2
			1, // dload_3
			1, // aload_0
			1, // aload_1
			1, // aload_2
			1, // aload_3
			1, // iaload
			1, // laload
			1, // faload
			1, // daload
			1, // aaload
			1, // baload
			1, // caload
			1, // saload
			2, // istore
			2, // lstore
			2, // fstore
			2, // dstore
			2, // astore
			1, // istore_0
			1, // istore_1
			1, // istore_2
			1, // istore_3
			1, // lstore_0
			1, // lstore_1
			1, // lstore_2
			1, // lstore_3
			1, // fstore_0
			1, // fstore_1
			1, // fstore_2
			1, // fstore_3
			1, // dstore_0
			1, // dstore_1
			1, // dstore_2
			1, // dstore_3
			1, // astore_0
			1, // astore_1
			1, // astore_2
			1, // astore_3
			1, // iastore
			1, // lastore
			1, // fastore
			1, // dastore
			1, // aastore
			1, // bastore
			1, // castore
			1, // sastore
			1, // pop
			1, // pop2
			1, // dup
			1, // dup_x1
			1, // dup_x2
			1, // dup2
			1, // dup2_x1
			1, // dup2_x2
			1, // swap
			1, // iadd
			1, // ladd
			1, // fadd
			1, // dadd
			1, // isub
			1, // lsub
			1, // fsub
			1, // dsub
			1, // imul
			1, // lmul
			1, // fmul
			1, // dmul
			1, // idiv
			1, // ldiv
			1, // fdiv
			1, // ddiv
			1, // irem
			1, // lrem
			1, // frem
			1, // drem
			1, // ineg
			1, // lneg
			1, // fneg
			1, // dneg
			1, // ishl
			1, // lshl
			1, // ishr
			1, // lshr
			1, // iushr
			1, // lushr
			1, // iand
			1, // land
			1, // ior
			1, // lor
			1, // ixor
			1, // lxor
			3, // iinc
			1, // i2l
			1, // i2f
			1, // i2d
			1, // l2i
			1, // l2f
			1, // l2d
			1, // f2i
			1, // f2l
			1, // f2d
			1, // d2i
			1, // d2l
			1, // d2f
			1, // i2b
			1, // i2c
			1, // i2s
			1, // lcmp
			1, // fcmpl
			1, // fcmpg
			1, // dcmpl
			1, // dcmpg
			3, // ifeq
			3, // ifne
			3, // iflt
			3, // ifge
			3, // ifgt
			3, // ifle
			3, // if_icmpeq
			3, // if_icmpne
			3, // if_icmplt
			3, // if_icmpge
			3, // if_icmpgt
			3, // if_icmple
			3, // if_acmpeq
			3, // if_acmpne
			3, // goto
			3, // jsr
			2, // ret
			Opcode.VARIABLE, // tableswitch
			Opcode.VARIABLE, // lookupswitch
			1, // ireturn
			1, // lreturn
			1, // freturn
			1, // dreturn
			1, // areturn
			1, // return
			3, // getstatic
			3, // putstatic
			3, // getfield
			3, // putfield
			3, // invokevirtual
			3, // invokespecial
			3, // invokestatic
			5, // invokeinterface
			1, // xxxunusedxxx
			3, // new
			2, // newarray
			3, // anewarray
			1, // arraylength
			1, // athrow
			3, // checkcast
			3, // instanceof
			1, // monitorenter
			1, // monitorexit
			Opcode.VARIABLE, // wide
			4, // multianewarray
			3, // ifnull
			3, // ifnonnull
			5, // goto_w
			5, // jsr_w
			1, // breakpoint
			1, // 203
			3, // putfield_nowb
			3, // putstatic_nowb
			1, // 206
			1, // 207
			1, // 208
			1, // 209
			1, // 210
			1, // 211
			1, // 212
			1, // 213
			1, // 214
			1, // 215
			1, // 216
			1, // 217
			1, // 218
			1, // 219
			1, // 220
			1, // 221
			1, // 222
			1, // 223
			1, // 224
			1, // 225
			1, // 226
			1, // 227
			1, // 228
			1, // 229
			1, // 230
			1, // 231
			1, // 232
			1, // 233
			1, // 234
			1, // 235
			1, // 236
			2, // rc
			2, // aupdate
			2, // supdate
			1, // aswizzle
			1, // aswrange
			1, // 242
			1, // 243
			1, // 244
			1, // 245
			1, // 246
			1, // 247
			1, // 248
			1, // 249
			1, // 250
			1, // 251
			1, // 252
			1, // 253
			1, // 254
			1, // 255
	};

	/**
	 * An array, indexed by the opcode, mapping opcodes to opcode classes.
	 */
	public static final int[] opcXMap = { Opcode.opcx_nop, // nop
			Opcode.opcx_ldc, // aconst_null
			Opcode.opcx_ldc, // iconst_m1
			Opcode.opcx_ldc, // iconst_0
			Opcode.opcx_ldc, // iconst_1
			Opcode.opcx_ldc, // iconst_2
			Opcode.opcx_ldc, // iconst_3
			Opcode.opcx_ldc, // iconst_4
			Opcode.opcx_ldc, // iconst_5
			Opcode.opcx_ldc, // lconst_0
			Opcode.opcx_ldc, // lconst_1
			Opcode.opcx_ldc, // fconst_0
			Opcode.opcx_ldc, // fconst_1
			Opcode.opcx_ldc, // fconst_2
			Opcode.opcx_ldc, // dconst_0
			Opcode.opcx_ldc, // dconst_1
			Opcode.opcx_ldc, // bipush
			Opcode.opcx_ldc, // sipush
			Opcode.opcx_ldc, // ldc
			Opcode.opcx_ldc, // ldc_w
			Opcode.opcx_ldc, // ldc2_w
			Opcode.opcx_iload, // iload
			Opcode.opcx_lload, // lload
			Opcode.opcx_fload, // fload
			Opcode.opcx_dload, // dload
			Opcode.opcx_aload, // aload
			Opcode.opcx_iload, // iload_0
			Opcode.opcx_iload, // iload_1
			Opcode.opcx_iload, // iload_2
			Opcode.opcx_iload, // iload_3
			Opcode.opcx_lload, // lload_0
			Opcode.opcx_lload, // lload_1
			Opcode.opcx_lload, // lload_2
			Opcode.opcx_lload, // lload_3
			Opcode.opcx_fload, // fload_0
			Opcode.opcx_fload, // fload_1
			Opcode.opcx_fload, // fload_2
			Opcode.opcx_fload, // fload_3
			Opcode.opcx_dload, // dload_0
			Opcode.opcx_dload, // dload_1
			Opcode.opcx_dload, // dload_2
			Opcode.opcx_dload, // dload_3
			Opcode.opcx_aload, // aload_0
			Opcode.opcx_aload, // aload_1
			Opcode.opcx_aload, // aload_2
			Opcode.opcx_aload, // aload_3
			Opcode.opcx_iaload, // iaload
			Opcode.opcx_laload, // laload
			Opcode.opcx_faload, // faload
			Opcode.opcx_daload, // daload
			Opcode.opcx_aaload, // aaload
			Opcode.opcx_baload, // baload
			Opcode.opcx_caload, // caload
			Opcode.opcx_saload, // saload
			Opcode.opcx_istore, // istore
			Opcode.opcx_lstore, // lstore
			Opcode.opcx_fstore, // fstore
			Opcode.opcx_dstore, // dstore
			Opcode.opcx_astore, // astore
			Opcode.opcx_istore, // istore_0
			Opcode.opcx_istore, // istore_1
			Opcode.opcx_istore, // istore_2
			Opcode.opcx_istore, // istore_3
			Opcode.opcx_lstore, // lstore_0
			Opcode.opcx_lstore, // lstore_1
			Opcode.opcx_lstore, // lstore_2
			Opcode.opcx_lstore, // lstore_3
			Opcode.opcx_fstore, // fstore_0
			Opcode.opcx_fstore, // fstore_1
			Opcode.opcx_fstore, // fstore_2
			Opcode.opcx_fstore, // fstore_3
			Opcode.opcx_dstore, // dstore_0
			Opcode.opcx_dstore, // dstore_1
			Opcode.opcx_dstore, // dstore_2
			Opcode.opcx_dstore, // dstore_3
			Opcode.opcx_astore, // astore_0
			Opcode.opcx_astore, // astore_1
			Opcode.opcx_astore, // astore_2
			Opcode.opcx_astore, // astore_3
			Opcode.opcx_iastore, // iastore
			Opcode.opcx_lastore, // lastore
			Opcode.opcx_fastore, // fastore
			Opcode.opcx_dastore, // dastore
			Opcode.opcx_aastore, // aastore
			Opcode.opcx_bastore, // bastore
			Opcode.opcx_castore, // castore
			Opcode.opcx_sastore, // sastore
			Opcode.opcx_pop, // pop
			Opcode.opcx_pop2, // pop2
			Opcode.opcx_dup, // dup
			Opcode.opcx_dup_x1, // dup_x1
			Opcode.opcx_dup_x2, // dup_x2
			Opcode.opcx_dup2, // dup2
			Opcode.opcx_dup2_x1, // dup2_x1
			Opcode.opcx_dup2_x2, // dup2_x2
			Opcode.opcx_swap, // swap
			Opcode.opcx_iadd, // iadd
			Opcode.opcx_ladd, // ladd
			Opcode.opcx_fadd, // fadd
			Opcode.opcx_dadd, // dadd
			Opcode.opcx_isub, // isub
			Opcode.opcx_lsub, // lsub
			Opcode.opcx_fsub, // fsub
			Opcode.opcx_dsub, // dsub
			Opcode.opcx_imul, // imul
			Opcode.opcx_lmul, // lmul
			Opcode.opcx_fmul, // fmul
			Opcode.opcx_dmul, // dmul
			Opcode.opcx_idiv, // idiv
			Opcode.opcx_ldiv, // ldiv
			Opcode.opcx_fdiv, // fdiv
			Opcode.opcx_ddiv, // ddiv
			Opcode.opcx_irem, // irem
			Opcode.opcx_lrem, // lrem
			Opcode.opcx_frem, // frem
			Opcode.opcx_drem, // drem
			Opcode.opcx_ineg, // ineg
			Opcode.opcx_lneg, // lneg
			Opcode.opcx_fneg, // fneg
			Opcode.opcx_dneg, // dneg
			Opcode.opcx_ishl, // ishl
			Opcode.opcx_lshl, // lshl
			Opcode.opcx_ishr, // ishr
			Opcode.opcx_lshr, // lshr
			Opcode.opcx_iushr, // iushr
			Opcode.opcx_lushr, // lushr
			Opcode.opcx_iand, // iand
			Opcode.opcx_land, // land
			Opcode.opcx_ior, // ior
			Opcode.opcx_lor, // lor
			Opcode.opcx_ixor, // ixor
			Opcode.opcx_lxor, // lxor
			Opcode.opcx_iinc, // iinc
			Opcode.opcx_i2l, // i2l
			Opcode.opcx_i2f, // i2f
			Opcode.opcx_i2d, // i2d
			Opcode.opcx_l2i, // l2i
			Opcode.opcx_l2f, // l2f
			Opcode.opcx_l2d, // l2d
			Opcode.opcx_f2i, // f2i
			Opcode.opcx_f2l, // f2l
			Opcode.opcx_f2d, // f2d
			Opcode.opcx_d2i, // d2i
			Opcode.opcx_d2l, // d2l
			Opcode.opcx_d2f, // d2f
			Opcode.opcx_i2b, // i2b
			Opcode.opcx_i2c, // i2c
			Opcode.opcx_i2s, // i2s
			Opcode.opcx_lcmp, // lcmp
			Opcode.opcx_fcmpl, // fcmpl
			Opcode.opcx_fcmpg, // fcmpg
			Opcode.opcx_dcmpl, // dcmpl
			Opcode.opcx_dcmpg, // dcmpg
			Opcode.opcx_ifeq, // ifeq
			Opcode.opcx_ifne, // ifne
			Opcode.opcx_iflt, // iflt
			Opcode.opcx_ifge, // ifge
			Opcode.opcx_ifgt, // ifgt
			Opcode.opcx_ifle, // ifle
			Opcode.opcx_if_icmpeq, // if_icmpeq
			Opcode.opcx_if_icmpne, // if_icmpne
			Opcode.opcx_if_icmplt, // if_icmplt
			Opcode.opcx_if_icmpge, // if_icmpge
			Opcode.opcx_if_icmpgt, // if_icmpgt
			Opcode.opcx_if_icmple, // if_icmple
			Opcode.opcx_if_acmpeq, // if_acmpeq
			Opcode.opcx_if_acmpne, // if_acmpne
			Opcode.opcx_goto, // goto
			Opcode.opcx_jsr, // jsr
			Opcode.opcx_ret, // ret
			Opcode.opcx_switch, // tableswitch
			Opcode.opcx_switch, // lookupswitch
			Opcode.opcx_ireturn, // ireturn
			Opcode.opcx_lreturn, // lreturn
			Opcode.opcx_freturn, // freturn
			Opcode.opcx_dreturn, // dreturn
			Opcode.opcx_areturn, // areturn
			Opcode.opcx_return, // return
			Opcode.opcx_getstatic, // getstatic
			Opcode.opcx_putstatic, // putstatic
			Opcode.opcx_getfield, // getfield
			Opcode.opcx_putfield, // putfield
			Opcode.opcx_invokevirtual, // invokevirtual
			Opcode.opcx_invokespecial, // invokespecial
			Opcode.opcx_invokestatic, // invokestatic
			Opcode.opcx_invokeinterface, // invokeinterface
			Opcode.opcx_nop, // xxxunusedxxx
			Opcode.opcx_new, // new
			Opcode.opcx_newarray, // newarray
			Opcode.opcx_newarray, // anewarray
			Opcode.opcx_arraylength, // arraylength
			Opcode.opcx_athrow, // athrow
			Opcode.opcx_checkcast, // checkcast
			Opcode.opcx_instanceof, // instanceof
			Opcode.opcx_monitorenter, // monitorenter
			Opcode.opcx_monitorexit, // monitorexit
			Opcode.opcx_nop, // wide
			Opcode.opcx_multianewarray, // multianewarray
			Opcode.opcx_ifnull, // ifnull
			Opcode.opcx_ifnonnull, // ifnonnull
			Opcode.opcx_goto, // goto_w
			Opcode.opcx_jsr, // jsr_w
			Opcode.opcx_nop, // breakpoint
			Opcode.opcx_nop, // 203
			Opcode.opcx_putfield_nowb, // putfield_nowb
			Opcode.opcx_putstatic_nowb, // putstatic_nowb
			Opcode.opcx_nop, // 206
			Opcode.opcx_nop, // 207
			Opcode.opcx_nop, // 208
			Opcode.opcx_nop, // 209
			Opcode.opcx_nop, // 210
			Opcode.opcx_nop, // 211
			Opcode.opcx_nop, // 212
			Opcode.opcx_nop, // 213
			Opcode.opcx_nop, // 214
			Opcode.opcx_nop, // 215
			Opcode.opcx_nop, // 216
			Opcode.opcx_nop, // 217
			Opcode.opcx_nop, // 218
			Opcode.opcx_nop, // 219
			Opcode.opcx_nop, // 220
			Opcode.opcx_nop, // 221
			Opcode.opcx_nop, // 222
			Opcode.opcx_nop, // 223
			Opcode.opcx_nop, // 224
			Opcode.opcx_nop, // 225
			Opcode.opcx_nop, // 226
			Opcode.opcx_nop, // 227
			Opcode.opcx_nop, // 228
			Opcode.opcx_nop, // 229
			Opcode.opcx_nop, // 230
			Opcode.opcx_nop, // 231
			Opcode.opcx_nop, // 232
			Opcode.opcx_nop, // 233
			Opcode.opcx_nop, // 234
			Opcode.opcx_nop, // 235
			Opcode.opcx_nop, // 236
			Opcode.opcx_rc, // rc
			Opcode.opcx_aupdate, // aupdate
			Opcode.opcx_supdate, // supdate
			Opcode.opcx_aswizzle, // aswizzle
			Opcode.opcx_aswrange, // aswrange
			Opcode.opcx_nop, // 242
			Opcode.opcx_nop, // 243
			Opcode.opcx_nop, // 244
			Opcode.opcx_nop, // 245
			Opcode.opcx_nop, // 246
			Opcode.opcx_nop, // 247
			Opcode.opcx_nop, // 248
			Opcode.opcx_nop, // 249
			Opcode.opcx_nop, // 250
			Opcode.opcx_nop, // 251
			Opcode.opcx_nop, // 252
			Opcode.opcx_nop, // 253
			Opcode.opcx_nop, // 254
			Opcode.opcx_nop, // 255
	};
}
