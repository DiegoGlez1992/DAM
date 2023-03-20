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

import java.util.*;

/**
 * AscendVisitor is the superclass of Type0Visitor and Type1Visitor,
 * conveniently containing the common code. It makes an upward traversal of the
 * tree as if it were a binary tree (nodes with more than two children, such as
 * a method call, are considered in a form similar to curried form).
 * 
 * @author Thomas VanDrunen
 */

public abstract class AscendVisitor extends TreeVisitor {

	Hashtable defInfoMap; /* the same as the fields of Stack Optimizer */

	Hashtable useInfoMap; /* of the same name */

	LocalExpr start; /* where we start the search from */

	Node previous;

	Vector visited;

	public AscendVisitor(final Hashtable defInfoMap, final Hashtable useInfoMap) {
		this.defInfoMap = defInfoMap;
		this.useInfoMap = useInfoMap;

		visited = new Vector();
	}

	abstract public void check(Node node);

	public void visitTree(final Tree tree) {

		final ListIterator iter = tree.stmts().listIterator(
				tree.stmts().lastIndexOf(previous));

		if (iter.hasPrevious()) {
			final Stmt p = (Stmt) iter.previous();
			check(p);
		}
		/*
		 * Object prev = iter.previous(); if (prev instanceof LocalExpr)
		 * check(prev);
		 */
	}

	public void visitExprStmt(final ExprStmt stmt) {

		previous = stmt;
		stmt.parent().visit(this);
	}

	public void visitIfCmpStmt(final IfCmpStmt stmt) {

		if (stmt.right() == previous) {
			check(stmt.left());
		} else if (stmt.left() == previous) {
			previous = stmt;
			stmt.parent().visit(this);
		}
	}

	public void visitIfZeroStmt(final IfZeroStmt stmt) {

		previous = stmt;
		stmt.parent.visit(this);
	}

	public void visitInitStmt(final InitStmt stmt) {

		final LocalExpr[] targets = stmt.targets();
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == previous) {
				if (i > 0) {
					check(targets[i - 1]);
				} else {
					break;
				}
			}
		}
	}

	public void visitGotoStmt(final GotoStmt stmt) {

	}

	public void visitLabelStmt(final LabelStmt stmt) {

	}

	public void visitMonitorStmt(final MonitorStmt stmt) {

		previous = stmt;
		stmt.parent().visit(this);
	}

	public void visitPhiStmt(final PhiStmt stmt) {

		if (stmt instanceof PhiCatchStmt) {
			visitPhiCatchStmt((PhiCatchStmt) stmt);
		} else if (stmt instanceof PhiJoinStmt) {
			visitPhiJoinStmt((PhiJoinStmt) stmt);
			/*
			 * else if (stmt instanceof PhiReturnStmt)
			 * visitPhiReturnStmt((PhiReturnStmt) stmt);
			 */
		}
	}

	public void visitCatchExpr(final CatchExpr expr) {

	}

	public void visitDefExpr(final DefExpr expr) {
		if (expr instanceof MemExpr) {
			visitMemExpr((MemExpr) expr);
		}
	}

	public void visitStackManipStmt(final StackManipStmt stmt) {

	}

	public void visitPhiCatchStmt(final PhiCatchStmt stmt) {

	}

	public void visitPhiJoinStmt(final PhiJoinStmt stmt) {

	}

	public void visitRetStmt(final RetStmt stmt) {

	}

	public void visitReturnExprStmt(final ReturnExprStmt stmt) {

		previous = stmt;
		stmt.parent.visit(this);
	}

	public void visitReturnStmt(final ReturnStmt stmt) {

	}

	public void visitAddressStoreStmt(final AddressStoreStmt stmt) {

	}

	public void visitStoreExpr(final StoreExpr expr) {

		if ((expr.target() instanceof LocalExpr)
				|| (expr.target() instanceof StaticFieldExpr)) {
			if (previous == expr.expr()) { // can't be target, because then
				// it would be a definition, for which
				// Type0Visitor is not called
				previous = expr;
				expr.parent.visit(this);
			}
		}

		else if (expr.target() instanceof ArrayRefExpr) {
			if (previous == expr.expr()) {
				check(((ArrayRefExpr) expr.target()).index());
			} else if (previous == expr.target()) {
				previous = expr;
				expr.parent.visit(this);
			}
		}

		else if (expr.target() instanceof FieldExpr) {
			if (previous == expr.expr()) {
				check(expr.target());
			} else if (previous == expr.target()) {
				previous = expr;
				expr.parent.visit(this);
			}
		}
	}

	public void visitJsrStmt(final JsrStmt stmt) {

	}

	public void visitSwitchStmt(final SwitchStmt stmt) {

		if (previous == stmt.index()) {
			previous = stmt;
			stmt.parent.visit(this);
		}
	}

	public void visitThrowStmt(final ThrowStmt stmt) {

	}

	public void visitStmt(final Stmt stmt) {

	}

	public void visitSCStmt(final SCStmt stmt) {

	}

	public void visitSRStmt(final SRStmt stmt) {

	}

	public void visitArithExpr(final ArithExpr expr) {

		if (previous == expr.left()) {
			previous = expr;
			expr.parent.visit(this);
		} else if (previous == expr.right()) {
			check(expr.left());
		}

	}

	public void visitArrayLengthExpr(final ArrayLengthExpr expr) {

	}

	public void visitMemExpr(final MemExpr expr) {

		if (expr instanceof MemRefExpr) {
			visitMemRefExpr((MemRefExpr) expr);
		} else if (expr instanceof VarExpr) {
			visitVarExpr((VarExpr) expr);
		}

	}

	public void visitMemRefExpr(final MemRefExpr expr) {
		if (expr instanceof FieldExpr) {
			visitFieldExpr((FieldExpr) expr);
		} else if (expr instanceof StaticFieldExpr) {
			visitStaticFieldExpr((StaticFieldExpr) expr);
		} else if (expr instanceof ArrayRefExpr) {
			visitArrayRefExpr((ArrayRefExpr) expr);
		}

	}

	public void visitArrayRefExpr(final ArrayRefExpr expr) {

		if (previous == expr.array()) { // the array reference is like the
			previous = expr; // left child
			expr.parent().visit(this);
		} else if (previous == expr.index()) {
			check(expr.array()); // right child
		}
	}

	public void visitCallExpr(final CallExpr expr) {
		if (expr instanceof CallMethodExpr) {
			visitCallMethodExpr((CallMethodExpr) expr);
		}
		if (expr instanceof CallStaticExpr) {
			visitCallStaticExpr((CallStaticExpr) expr);
		}

	}

	public void visitCallMethodExpr(final CallMethodExpr expr) {

		if (previous == expr.receiver()) {
			previous = expr;
			expr.parent.visit(this);
		}

		else {
			final Expr[] params = expr.params();
			for (int i = 0; i < params.length; i++) {
				if (params[i] == previous) {
					if (i > 0) {
						check(params[i - 1]);
					} else {
						check(expr.receiver());
					}
				}
			}
		}

	}

	public void visitCallStaticExpr(final CallStaticExpr expr) {

		final Expr[] params = expr.params();
		for (int i = 0; i < params.length; i++) {
			if (params[i] == previous) {
				if (i > 0) {
					check(params[i - 1]);
				} else {
					previous = expr;
					expr.parent().visit(this);
				}
				break;
			}
		}

	}

	public void visitCastExpr(final CastExpr expr) {

		previous = expr;
		expr.parent.visit(this);
	}

	public void visitConstantExpr(final ConstantExpr expr) {

	}

	public void visitFieldExpr(final FieldExpr expr) {

		if (previous == expr.object()) {
			previous = expr;
			expr.parent.visit(this);
		}
	}

	public void visitInstanceOfExpr(final InstanceOfExpr expr) {

		if (previous == expr.expr()) {
			previous = expr;
			expr.parent.visit(this);
		}
	}

	public void visitLocalExpr(final LocalExpr expr) {

	}

	public void visitNegExpr(final NegExpr expr) {

		if (previous == expr.expr()) {
			previous = expr;
			expr.parent.visit(this);
		}
	}

	public void visitNewArrayExpr(final NewArrayExpr expr) {

		if (previous == expr.size()) {
			previous = expr;
			expr.parent.visit(this);
		}
	}

	public void visitNewExpr(final NewExpr expr) {

	}

	public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {

		final Expr[] dims = expr.dimensions;
		for (int i = 0; i < dims.length; i++) {
			if (dims[i] == previous) {
				if (i > 0) {
					check(dims[i - 1]);
				} else {
					previous = expr;
					expr.parent().visit(this);
				}
			}
		}

	}

	public void visitCheckExpr(final CheckExpr expr) {
		if (expr instanceof ZeroCheckExpr) {
			visitZeroCheckExpr((ZeroCheckExpr) expr);
		} else if (expr instanceof RCExpr) {
			visitRCExpr((RCExpr) expr);
		} else if (expr instanceof UCExpr) {
			visitUCExpr((UCExpr) expr);
		}
	}

	public void visitZeroCheckExpr(final ZeroCheckExpr expr) {
		/*
		 * if (previous == expr.expr()) { previous = expr;
		 * expr.parent.visit(this); }
		 */
	}

	public void visitRCExpr(final RCExpr expr) {

	}

	public void visitUCExpr(final UCExpr expr) {

	}

	public void visitReturnAddressExpr(final ReturnAddressExpr expr) {

	}

	public void visitShiftExpr(final ShiftExpr expr) {

		if (previous == expr.expr()) { // the expression to be shifted is like
			previous = expr; // the left child
			expr.parent().visit(this);
		} else if (previous == expr.bits()) {
			check(expr.expr()); // the right child
		}
	}

	public void visitStackExpr(final StackExpr expr) {

	}

	public void visitVarExpr(final VarExpr expr) {
		if (expr instanceof LocalExpr) {
			visitLocalExpr((LocalExpr) expr);
		}
		if (expr instanceof StackExpr) {
			visitStackExpr((StackExpr) expr);
		}
	}

	public void visitStaticFieldExpr(final StaticFieldExpr expr) {

	}

	public void visitExpr(final Expr expr) {

	}

	public void visitNode(final Node node) {

	}
}
