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
package EDU.purdue.cs.bloat.tree;

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;

/**
 * PrintVistor traverses a Tree and prints some information about each visited
 * Node to a stream.
 */
public class PrintVisitor extends TreeVisitor {

	protected PrintWriter out; // The stream to which we are printing

	/**
	 * Constructor. Prints to System.out.
	 */
	public PrintVisitor() {
		this(System.out);
	}

	public PrintVisitor(final Writer out) {
		this.out = new PrintWriter(out);
	}

	public PrintVisitor(final PrintStream out) {
		this.out = new PrintWriter(out);
	}

	protected void println() {
		out.println();
	}

	protected void println(final Object s) {
		out.println(s);
	}

	protected void print(final Object s) {
		out.print(s);
	}

	public void visitFlowGraph(final FlowGraph cfg) {
		cfg.source().visit(this);

		final Iterator e = cfg.trace().iterator();

		while (e.hasNext()) {
			final Block block = (Block) e.next();
			block.visit(this);
		}

		cfg.sink().visit(this);

		this.out.flush();
	}

	public void visitBlock(final Block block) {
		println();
		println(block);

		final Handler handler = (Handler) block.graph().handlersMap()
				.get(block);

		if (handler != null) {
			println("catches " + handler.catchType());
			println("protects " + handler.protectedBlocks());
		}

		block.visitChildren(this);
	}

	public void visitExprStmt(final ExprStmt stmt) {
		print("eval ");
		stmt.expr().visit(this);
		println();
	}

	public void visitIfZeroStmt(final IfZeroStmt stmt) {
		print("if0 (");
		stmt.expr().visit(this);
		print(" ");

		switch (stmt.comparison()) {
		case IfStmt.EQ:
			print("==");
			break;
		case IfStmt.NE:
			print("!=");
			break;
		case IfStmt.GT:
			print(">");
			break;
		case IfStmt.GE:
			print(">=");
			break;
		case IfStmt.LT:
			print("<");
			break;
		case IfStmt.LE:
			print("<=");
			break;
		}

		if (stmt.expr().type().isReference()) {
			print(" null");
		} else {
			print(" 0");
		}

		print(") then " + stmt.trueTarget() + " else " + stmt.falseTarget());
		println(" caught by " + stmt.catchTargets());
	}

	public void visitIfCmpStmt(final IfCmpStmt stmt) {
		print("if (");
		stmt.left().visit(this);
		print(" ");

		switch (stmt.comparison()) {
		case IfStmt.EQ:
			print("==");
			break;
		case IfStmt.NE:
			print("!=");
			break;
		case IfStmt.GT:
			print(">");
			break;
		case IfStmt.GE:
			print(">=");
			break;
		case IfStmt.LT:
			print("<");
			break;
		case IfStmt.LE:
			print("<=");
			break;
		}

		print(" ");

		if (stmt.right() != null) {
			stmt.right().visit(this);
		}

		print(") then " + stmt.trueTarget() + " else " + stmt.falseTarget());
		println(" caught by " + stmt.catchTargets());
	}

	public void visitInitStmt(final InitStmt stmt) {
		print("INIT");

		final LocalExpr[] t = stmt.targets();

		if (t != null) {
			for (int i = 0; i < t.length; i++) {
				if (t[i] != null) {
					print(" ");
					t[i].visit(this);
				}
			}
		}

		println();
	}

	public void visitGotoStmt(final GotoStmt stmt) {
		print("goto " + stmt.target().label());
		println(" caught by " + stmt.catchTargets());
	}

	public void visitLabelStmt(final LabelStmt stmt) {
		if (stmt.label() != null) {
			println(stmt.label());
		}
	}

	public void visitMonitorStmt(final MonitorStmt stmt) {
		if (stmt.kind() == MonitorStmt.ENTER) {
			print("enter ");
		} else {
			print("exit ");
		}

		print("monitor (");

		if (stmt.object() != null) {
			stmt.object().visit(this);
		}

		println(")");
	}

	public void visitCatchExpr(final CatchExpr expr) {
		print("Catch(" + expr.catchType() + ")");
	}

	public void visitStackManipStmt(final StackManipStmt stmt) {
		print("(");

		final StackExpr[] target = stmt.target();

		if (target != null) {
			for (int i = 0; i < target.length; i++) {
				target[i].visit(this);
				if (i != target.length - 1) {
					print(", ");
				}
			}
		}

		final String[] str = new String[] { "swap", "dup", "dup_x1", "dup_x2",
				"dup2", "dup2_x1", "dup2_x2" };

		print(") := " + str[stmt.kind()] + "(");

		final StackExpr[] source = stmt.source();

		if (source != null) {
			for (int i = 0; i < source.length; i++) {
				source[i].visit(this);
				if (i != source.length - 1) {
					print(", ");
				}
			}
		}

		println(")");
	}

	public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
		if (stmt.target() != null) {
			stmt.target().visit(this);
		}

		print(" := Phi(");

		if (stmt.hasParent()) {
			final Tree tree = (Tree) stmt.parent();
			final Block block = tree.block();

			final Iterator e = block.graph().preds(block).iterator();

			while (e.hasNext()) {
				final Block pred = (Block) e.next();

				final Expr operand = stmt.operandAt(pred);
				print(pred.label() + "=");
				operand.visit(this);

				if (e.hasNext()) {
					print(", ");
				}
			}
		} else {
			final Iterator e = stmt.operands().iterator();

			while (e.hasNext()) {
				final Expr operand = (Expr) e.next();
				operand.visit(this);

				if (e.hasNext()) {
					print(", ");
				}
			}
		}

		println(")");
	}

	public void visitPhiCatchStmt(final PhiCatchStmt stmt) {
		if (stmt.target() != null) {
			stmt.target().visit(this);
		}

		print(" := Phi-Catch(");

		final Iterator e = stmt.operands().iterator();

		while (e.hasNext()) {
			final Expr operand = (Expr) e.next();
			operand.visit(this);

			if (e.hasNext()) {
				print(", ");
			}
		}

		println(")");
	}

	public void visitRetStmt(final RetStmt stmt) {
		print("ret from " + stmt.sub());
		println(" caught by " + stmt.catchTargets());
	}

	public void visitReturnExprStmt(final ReturnExprStmt stmt) {
		print("return ");

		if (stmt.expr() != null) {
			stmt.expr().visit(this);
		}

		println(" caught by " + stmt.catchTargets());
	}

	public void visitReturnStmt(final ReturnStmt stmt) {
		print("return");
		println(" caught by " + stmt.catchTargets());
	}

	public void visitStoreExpr(final StoreExpr expr) {
		print("(");

		if (expr.target() != null) {
			expr.target().visit(this);
		}

		print(" := ");

		if (expr.expr() != null) {
			expr.expr().visit(this);
		}

		print(")");
	}

	public void visitAddressStoreStmt(final AddressStoreStmt stmt) {
		print("La");

		if (stmt.sub() != null) {
			print(new Integer(stmt.sub().returnAddress().index()));
		} else {
			print("???");
		}

		println(" := returnAddress");
	}

	public void visitJsrStmt(final JsrStmt stmt) {
		print("jsr ");

		if (stmt.sub() != null) {
			print(stmt.sub().entry());
		}

		if (stmt.follow() != null) {
			print(" ret to " + stmt.follow());
		}

		println(" caught by " + stmt.catchTargets());
	}

	public void visitSwitchStmt(final SwitchStmt stmt) {
		print("switch (");

		if (stmt.index() != null) {
			stmt.index().visit(this);
		}

		print(")");
		println(" caught by " + stmt.catchTargets());

		if ((stmt.values() != null) && (stmt.targets() != null)) {
			for (int i = 0; i < stmt.values().length; i++) {
				println("    case " + stmt.values()[i] + ": "
						+ stmt.targets()[i]);
			}
		}

		println("    default: " + stmt.defaultTarget());
	}

	public void visitThrowStmt(final ThrowStmt stmt) {
		print("throw ");

		if (stmt.expr() != null) {
			stmt.expr().visit(this);
		}

		println(" caught by " + stmt.catchTargets());
	}

	public void visitSCStmt(final SCStmt stmt) {
		print("aswizzle ");
		if (stmt.array() != null) {
			stmt.array().visit(this);
		}
		if (stmt.index() != null) {
			stmt.index().visit(this);
		}
	}

	public void visitSRStmt(final SRStmt stmt) {
		print("aswrange array: ");
		if (stmt.array() != null) {
			stmt.array().visit(this);
		}
		print(" start: ");
		if (stmt.start() != null) {
			stmt.start().visit(this);
		}
		print(" end: ");
		if (stmt.end() != null) {
			stmt.end().visit(this);
		}
		println("");
	}

	public void visitArithExpr(final ArithExpr expr) {
		print("(");

		if (expr.left() != null) {
			expr.left().visit(this);
		}

		print(" ");

		switch (expr.operation()) {
		case ArithExpr.ADD:
			print("+");
			break;
		case ArithExpr.SUB:
			print("-");
			break;
		case ArithExpr.DIV:
			print("/");
			break;
		case ArithExpr.MUL:
			print("*");
			break;
		case ArithExpr.REM:
			print("%");
			break;
		case ArithExpr.AND:
			print("&");
			break;
		case ArithExpr.IOR:
			print("|");
			break;
		case ArithExpr.XOR:
			print("^");
			break;
		case ArithExpr.CMP:
			print("<=>");
			break;
		case ArithExpr.CMPL:
			print("<l=>");
			break;
		case ArithExpr.CMPG:
			print("<g=>");
			break;
		}

		print(" ");
		if (expr.right() != null) {
			expr.right().visit(this);
		}
		print(")");
	}

	public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
		if (expr.array() != null) {
			expr.array().visit(this);
		}
		print(".length");
	}

	public void visitArrayRefExpr(final ArrayRefExpr expr) {
		if (expr.array() != null) {
			expr.array().visit(this);
		}
		print("[");
		if (expr.index() != null) {
			expr.index().visit(this);
		}
		print("]");
	}

	public void visitCallMethodExpr(final CallMethodExpr expr) {
		if (expr.receiver() != null) {
			expr.receiver().visit(this);
		}

		print(".");
		if (expr.method() != null) {
			print(expr.method().nameAndType().name());
		}
		print("(");

		if (expr.params() != null) {
			for (int i = 0; i < expr.params().length; i++) {
				expr.params()[i].visit(this);
				if (i != expr.params().length - 1) {
					print(", ");
				}
			}
		}

		print(")");
	}

	public void visitCallStaticExpr(final CallStaticExpr expr) {
		if (expr.method() != null) {
			print(expr.method().declaringClass());
		}

		print(".");
		if (expr.method() != null) {
			print(expr.method().nameAndType().name());
		}
		print("(");

		if (expr.params() != null) {
			for (int i = 0; i < expr.params().length; i++) {
				expr.params()[i].visit(this);
				if (i != expr.params().length - 1) {
					print(", ");
				}
			}
		}

		print(")");
	}

	public void visitCastExpr(final CastExpr expr) {
		print("((" + expr.castType() + ") ");
		if (expr.expr() != null) {
			expr.expr().visit(this);
		}
		print(")");
	}

	public void visitConstantExpr(final ConstantExpr expr) {
		if (expr.value() instanceof String) {
			final StringBuffer sb = new StringBuffer();

			final String s = (String) expr.value();

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

			print("'" + sb.toString() + "'");
		} else if (expr.value() instanceof Float) {
			print(expr.value() + "F");
		} else if (expr.value() instanceof Long) {
			print(expr.value() + "L");
		} else {
			print(expr.value());
		}
	}

	public void visitFieldExpr(final FieldExpr expr) {
		if (expr.object() != null) {
			expr.object().visit(this);
		}
		print(".");
		if (expr.field() != null) {
			print(expr.field().nameAndType().name());
		}
	}

	public void visitInstanceOfExpr(final InstanceOfExpr expr) {
		if (expr.expr() != null) {
			expr.expr().visit(this);
		}
		print(" instanceof " + expr.checkType());
	}

	public void visitLocalExpr(final LocalExpr expr) {
		if (expr.fromStack()) {
			print("T");
		} else {
			print("L");
		}

		print(expr.type().shortName().toLowerCase());
		print(Integer.toString(expr.index()));

		final DefExpr def = expr.def();

		if ((def == null) || (def.version() == -1)) {
			print("_undef");

		} else {
			print("_" + def.version());
		}
	}

	public void visitNegExpr(final NegExpr expr) {
		print("-");
		if (expr.expr() != null) {
			expr.expr().visit(this);
		}
	}

	public void visitNewArrayExpr(final NewArrayExpr expr) {
		print("new " + expr.elementType() + "[");
		if (expr.size() != null) {
			expr.size().visit(this);
		}
		print("]");
	}

	public void visitNewExpr(final NewExpr expr) {
		print("new " + expr.objectType());
	}

	public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
		print("new " + expr.elementType());

		if (expr.dimensions() != null) {
			for (int i = 0; i < expr.dimensions().length; i++) {
				print("[" + expr.dimensions()[i] + "]");
			}
		}
	}

	public void visitZeroCheckExpr(final ZeroCheckExpr expr) {
		if (expr.expr().type().isReference()) {
			print("notNull(");
		} else {
			print("notZero(");
		}

		if (expr.expr() != null) {
			expr.expr().visit(this);
		}

		print(")");
	}

	public void visitRCExpr(final RCExpr expr) {
		print("rc(");
		if (expr.expr() != null) {
			expr.expr().visit(this);
		}
		print(")");
	}

	public void visitUCExpr(final UCExpr expr) {
		if (expr.kind() == UCExpr.POINTER) {
			print("aupdate(");
		} else {
			print("supdate(");
		}

		if (expr.expr() != null) {
			expr.expr().visit(this);
		}
		print(")");
	}

	public void visitReturnAddressExpr(final ReturnAddressExpr expr) {
		print("returnAddress");
	}

	public void visitShiftExpr(final ShiftExpr expr) {
		print("(");
		if (expr.expr() != null) {
			expr.expr().visit(this);
		}

		if (expr.dir() == ShiftExpr.LEFT) {
			print("<<");
		} else if (expr.dir() == ShiftExpr.RIGHT) {
			print(">>");
		} else if (expr.dir() == ShiftExpr.UNSIGNED_RIGHT) {
			print(">>>");
		}

		if (expr.bits() != null) {
			expr.bits().visit(this);
		}
		print(")");
	}

	public void visitStackExpr(final StackExpr expr) {
		print("S" + expr.type().shortName().toLowerCase() + expr.index());

		final DefExpr def = expr.def();

		if ((def == null) || (def.version() == -1)) {
			print("_undef");
		} else {
			print("_" + def.version());
		}
	}

	public void visitStaticFieldExpr(final StaticFieldExpr expr) {
		if (expr.field() != null) {
			print(expr.field().declaringClass() + "."
					+ expr.field().nameAndType().name());
		}
	}

	public void visitExpr(final Expr expr) {
		print("EXPR");
	}

	public void visitStmt(final Stmt stmt) {
		print("STMT");
	}
}
