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

import EDU.purdue.cs.bloat.cfg.*;

/**
 * ReplaceVisitor traverses a tree and replaces each occurrence of one Node with
 * another Node.
 */
public class ReplaceVisitor extends TreeVisitor {
	Node from;

	Node to;

	/**
	 * Constructor.
	 * 
	 * @param from
	 *            The "old" Node.
	 * @param to
	 *            The "new" Node.
	 */
	public ReplaceVisitor(final Node from, final Node to) {
		this.from = from;
		this.to = to;

		if (Tree.DEBUG) {
			System.out.println("replace " + from + " VN=" + from.valueNumber()
					+ " in " + from.parent + " with " + to);
		}
	}

	public void visitTree(final Tree tree) {
		if (to instanceof Stmt) {
			((Stmt) to).setParent(tree);

			// The most common statement replacement is the last statement.
			// so search from the end of the statement list.
			final ListIterator iter = tree.stmts
					.listIterator(tree.stmts.size());

			while (iter.hasPrevious()) {
				final Stmt s = (Stmt) iter.previous();
				if (s == from) {
					iter.set(to);
					break;
				}
			}
		} else {
			tree.visitChildren(this);
		}
	}

	public void visitExprStmt(final ExprStmt stmt) {
		if (stmt.expr == from) {
			stmt.expr = (Expr) to;
			((Expr) to).setParent(stmt);
		} else {
			stmt.visitChildren(this);
		}
	}

	public void visitInitStmt(final InitStmt stmt) {
		for (int i = 0; i < stmt.targets.length; i++) {
			if (stmt.targets[i] == from) {
				stmt.targets[i] = (LocalExpr) to;
				((LocalExpr) to).setParent(stmt);
				return;
			}
		}

		stmt.visitChildren(this);
	}

	public void visitGotoStmt(final GotoStmt stmt) {
		stmt.visitChildren(this);
	}

	public void visitMonitorStmt(final MonitorStmt stmt) {
		if (stmt.object == from) {
			stmt.object = (Expr) to;
			((Expr) to).setParent(stmt);
		} else {
			stmt.visitChildren(this);
		}
	}

	public void visitStackManipStmt(final StackManipStmt stmt) {
		for (int i = 0; i < stmt.target.length; i++) {
			if (stmt.target[i] == from) {
				stmt.target[i] = (StackExpr) to;
				((Expr) to).setParent(stmt);
				return;
			}
		}

		for (int i = 0; i < stmt.source.length; i++) {
			if (stmt.source[i] == from) {
				stmt.source[i] = (StackExpr) to;
				((Expr) to).setParent(stmt);
				return;
			}
		}

		stmt.visitChildren(this);
	}

	public void visitCatchExpr(final CatchExpr expr) {
		expr.visitChildren(this);
	}

	public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
		if (stmt.target == from) {
			stmt.target = (VarExpr) to;
			((VarExpr) to).setParent(stmt);
		} else {
			final Iterator e = stmt.operands.keySet().iterator();

			while (e.hasNext()) {
				final Block block = (Block) e.next();

				if (stmt.operandAt(block) == from) {
					stmt.setOperandAt(block, (Expr) to);
					((Expr) to).setParent(stmt);
					return;
				}
			}

			stmt.visitChildren(this);
		}
	}

	public void visitPhiCatchStmt(final PhiCatchStmt stmt) {
		if (stmt.target == from) {
			stmt.target = (LocalExpr) to;
			((LocalExpr) to).setParent(stmt);
		} else {
			final ListIterator e = stmt.operands.listIterator();

			while (e.hasNext()) {
				final LocalExpr expr = (LocalExpr) e.next();

				if (expr == from) {
					e.set(to);
					from.cleanup();
					((LocalExpr) to).setParent(stmt);
					return;
				}
			}

			stmt.visitChildren(this);
		}
	}

	public void visitRetStmt(final RetStmt stmt) {
		stmt.visitChildren(this);
	}

	public void visitReturnExprStmt(final ReturnExprStmt stmt) {
		if (stmt.expr == from) {
			stmt.expr = (Expr) to;
			((Expr) to).setParent(stmt);
		} else {
			stmt.visitChildren(this);
		}
	}

	public void visitReturnStmt(final ReturnStmt stmt) {
		stmt.visitChildren(this);
	}

	public void visitAddressStoreStmt(final AddressStoreStmt stmt) {
		stmt.visitChildren(this);
	}

	public void visitStoreExpr(final StoreExpr expr) {
		if (expr.target == from) {
			expr.target = (MemExpr) to;
			((MemExpr) to).setParent(expr);
		} else if (expr.expr == from) {
			expr.expr = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitSwitchStmt(final SwitchStmt stmt) {
		if (stmt.index == from) {
			stmt.index = (Expr) to;
			((Expr) to).setParent(stmt);
		} else {
			stmt.visitChildren(this);
		}
	}

	public void visitThrowStmt(final ThrowStmt stmt) {
		if (stmt.expr == from) {
			stmt.expr = (Expr) to;
			((Expr) to).setParent(stmt);
		} else {
			stmt.visitChildren(this);
		}
	}

	public void visitSCStmt(final SCStmt stmt) {
		if (stmt.array == from) {
			stmt.array = (Expr) to;
			((Expr) to).setParent(stmt);
		} else if (stmt.index == from) {
			stmt.index = (Expr) to;
			((Expr) to).setParent(stmt);
		} else {
			stmt.visitChildren(this);
		}
	}

	public void visitSRStmt(final SRStmt stmt) {
		if (stmt.array == from) {
			stmt.array = (Expr) to;
			((Expr) to).setParent(stmt);
		} else if (stmt.start == from) {
			stmt.start = (Expr) to;
			((Expr) to).setParent(stmt);
		} else if (stmt.end == from) {
			stmt.end = (Expr) to;
			((Expr) to).setParent(stmt);
		} else {
			stmt.visitChildren(this);
		}
	}

	public void visitDefExpr(final DefExpr expr) {
		expr.visitChildren(this);
	}

	public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
		if (expr.array == from) {
			expr.array = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitArithExpr(final ArithExpr expr) {
		if (expr.left == from) {
			expr.left = (Expr) to;
			((Expr) to).setParent(expr);
		} else if (expr.right == from) {
			expr.right = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitArrayRefExpr(final ArrayRefExpr expr) {
		if (expr.array == from) {
			expr.array = (Expr) to;
			((Expr) to).setParent(expr);
		} else if (expr.index == from) {
			expr.index = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitCallMethodExpr(final CallMethodExpr expr) {
		if (expr.receiver == from) {
			expr.receiver = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			for (int i = 0; i < expr.params.length; i++) {
				if (expr.params[i] == from) {
					expr.params[i] = (Expr) to;
					((Expr) to).setParent(expr);
					return;
				}
			}

			expr.visitChildren(this);
		}
	}

	public void visitCallStaticExpr(final CallStaticExpr expr) {
		for (int i = 0; i < expr.params.length; i++) {
			if (expr.params[i] == from) {
				expr.params[i] = (Expr) to;
				((Expr) to).setParent(expr);
				return;
			}
		}

		expr.visitChildren(this);
	}

	public void visitCastExpr(final CastExpr expr) {
		if (expr.expr == from) {
			expr.expr = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitConstantExpr(final ConstantExpr expr) {
		expr.visitChildren(this);
	}

	public void visitFieldExpr(final FieldExpr expr) {
		if (expr.object == from) {
			expr.object = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitInstanceOfExpr(final InstanceOfExpr expr) {
		if (expr.expr == from) {
			expr.expr = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitLocalExpr(final LocalExpr expr) {
		expr.visitChildren(this);
	}

	public void visitNegExpr(final NegExpr expr) {
		if (expr.expr == from) {
			expr.expr = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitNewArrayExpr(final NewArrayExpr expr) {
		if (expr.size == from) {
			expr.size = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitNewExpr(final NewExpr expr) {
		expr.visitChildren(this);
	}

	public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
		for (int i = 0; i < expr.dimensions.length; i++) {
			if (expr.dimensions[i] == from) {
				expr.dimensions[i] = (Expr) to;
				((Expr) to).setParent(expr);
				return;
			}
		}

		expr.visitChildren(this);
	}

	public void visitIfZeroStmt(final IfZeroStmt stmt) {
		if (stmt.expr == from) {
			stmt.expr = (Expr) to;
			((Expr) to).setParent(stmt);
		} else {
			stmt.visitChildren(this);
		}
	}

	public void visitIfCmpStmt(final IfCmpStmt stmt) {
		if (stmt.left == from) {
			stmt.left = (Expr) to;
			((Expr) to).setParent(stmt);
		} else if (stmt.right == from) {
			stmt.right = (Expr) to;
			((Expr) to).setParent(stmt);
		} else {
			stmt.visitChildren(this);
		}
	}

	public void visitReturnAddressExpr(final ReturnAddressExpr expr) {
		expr.visitChildren(this);
	}

	public void visitShiftExpr(final ShiftExpr expr) {
		if (expr.expr == from) {
			expr.expr = (Expr) to;
			((Expr) to).setParent(expr);
		} else if (expr.bits == from) {
			expr.bits = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitZeroCheckExpr(final ZeroCheckExpr expr) {
		if (expr.expr == from) {
			expr.expr = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitRCExpr(final RCExpr expr) {
		if (expr.expr == from) {
			expr.expr = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitUCExpr(final UCExpr expr) {
		if (expr.expr == from) {
			expr.expr = (Expr) to;
			((Expr) to).setParent(expr);
		} else {
			expr.visitChildren(this);
		}
	}

	public void visitStackExpr(final StackExpr expr) {
		expr.visitChildren(this);
	}

	public void visitStaticFieldExpr(final StaticFieldExpr expr) {
		expr.visitChildren(this);
	}
}
