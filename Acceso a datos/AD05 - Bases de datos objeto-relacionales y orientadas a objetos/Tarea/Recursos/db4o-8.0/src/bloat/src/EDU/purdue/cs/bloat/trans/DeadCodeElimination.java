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

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * DeadCodeElimination performs SSA-based dead code elimination as described in
 * [Cytron, et. al. 91]. The idea behind dead code elimination is that there are
 * some instructions that do not contribute anything useful to the result of the
 * program. Most dead code is introduced by other optimizations.
 * 
 * A program statement is live if one or more of the following holds:
 * 
 * <ol>
 * 
 * <li>The statement effects program output. In Java this is a lot more than
 * just I/O. We must be conservative and assume that exceptions and monitor
 * expression are always live.
 * 
 * <li>The statement is an assignment statement whose target is used in a live
 * statement.
 * 
 * <li>The statement is a conditional branch and there are live statements
 * whose execution depend on the conditional branch.
 * 
 * <ol>
 * 
 * Basically, the algorithm proceeds by marking a number of statements as being
 * pre-live and then uses a worklist to determine which statements must also be
 * live by the above three conditions.
 */
public class DeadCodeElimination {
	public static boolean DEBUG = false;

	private static final int DEAD = 0;

	private static final int LIVE = 1;

	FlowGraph cfg;

	/**
	 * Constructor.
	 */
	public DeadCodeElimination(final FlowGraph cfg) {
		this.cfg = cfg;
	}

	// Keep a work list of expressions that need to be made live.
	LinkedList worklist;

	/**
	 * Performs dead code elimination.
	 */
	public void transform() {
		// Mark all nodes in the tree as DEAD.
		cfg.visit(new TreeVisitor() {
			public void visitNode(final Node node) {
				node.visitChildren(this);
				node.setKey(DeadCodeElimination.DEAD);
			}
		});

		worklist = new LinkedList();

		// Visit the nodes in the tree and mark nodes that we know must be
		// LIVE.
		cfg.visit(new TreeVisitor() {
			public void visitMonitorStmt(final MonitorStmt stmt) {
				// NullPointerException, IllegalMonitorStateException
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitInitStmt(final InitStmt stmt) {
				// Needed to correctly initialize the formal parameters when
				// coloring
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitJsrStmt(final JsrStmt stmt) {
				// Branch
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitAddressStoreStmt(final AddressStoreStmt stmt) {
				// Branch
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitRetStmt(final RetStmt stmt) {
				// Branch
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitSRStmt(final SRStmt stmt) {
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitSCStmt(final SCStmt stmt) {
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
				// Memory allocation
				// NegativeArraySizeException
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitNewArrayExpr(final NewArrayExpr expr) {
				// Memory allocation
				// NegativeArraySizeException
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitNewExpr(final NewExpr expr) {
				// Memory allocation
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitStackExpr(final StackExpr expr) {
				if (expr.stmt() instanceof PhiStmt) {
					return;
				}

				// Stack change
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitZeroCheckExpr(final ZeroCheckExpr expr) {
				// NullPointerException or DivideByZeroException
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitRCExpr(final RCExpr expr) {
				// Residency check
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitUCExpr(final UCExpr expr) {
				// Update check
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitCastExpr(final CastExpr expr) {
				// ClassCastException
				if (expr.castType().isReference()) {
					if (DeadCodeElimination.DEBUG) {
						System.out.println(expr + " is prelive");
					}

					makeLive(expr);
				} else {
					expr.visitChildren(this);
				}
			}

			public void visitArithExpr(final ArithExpr expr) {
				// DivideByZeroException
				if ((expr.operation() == ArithExpr.DIV)
						|| (expr.operation() == ArithExpr.REM)) {

					if (expr.type().isIntegral()) {
						if (DeadCodeElimination.DEBUG) {
							System.out.println(expr + " is prelive");
						}

						makeLive(expr);
						return;
					}
				}

				expr.visitChildren(this);
			}

			public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
				// NullPointerException
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitArrayRefExpr(final ArrayRefExpr expr) {
				// NullPointerException, ArrayIndexOutOfBoundsException,
				// ArrayStoreException
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitFieldExpr(final FieldExpr expr) {
				// NullPointerException
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitCallStaticExpr(final CallStaticExpr expr) {
				// Call
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitCallMethodExpr(final CallMethodExpr expr) {
				// Call
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitCatchExpr(final CatchExpr expr) {
				// Stack change
				if (DeadCodeElimination.DEBUG) {
					System.out.println(expr + " is prelive");
				}

				makeLive(expr);
			}

			public void visitStackManipStmt(final StackManipStmt stmt) {
				// Stack change
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitThrowStmt(final ThrowStmt stmt) {
				// Branch
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitSwitchStmt(final SwitchStmt stmt) {
				// Branch
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitIfStmt(final IfStmt stmt) {
				// Branch
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitGotoStmt(final GotoStmt stmt) {
				// Branch
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitReturnStmt(final ReturnStmt stmt) {
				// Branch
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitReturnExprStmt(final ReturnExprStmt stmt) {
				// Branch
				if (DeadCodeElimination.DEBUG) {
					System.out.println(stmt + " is prelive");
				}

				makeLive(stmt);
			}

			public void visitStoreExpr(final StoreExpr expr) {
				// Can change a variable visible outside the method.
				if (!(expr.target() instanceof LocalExpr)) {
					if (DeadCodeElimination.DEBUG) {
						System.out.println(expr + " is prelive");
					}

					makeLive(expr);

				} else {
					expr.visitChildren(this);
				}
			}
		});

		// Go through the nodes in the worklist and make the nodes that
		// defined the VarExprs live.
		while (!worklist.isEmpty()) {
			final VarExpr expr = (VarExpr) worklist.removeFirst();

			final DefExpr def = expr.def();

			if (def != null) {
				if (DeadCodeElimination.DEBUG) {
					System.out.println("making live def of " + expr);
					System.out.println("    def = " + def);
				}

				makeLive(def.parent());
			}
		}

		// Remove dead stores.
		cfg.visit(new TreeVisitor() {
			public void visitStoreExpr(final StoreExpr expr) {
				final Expr lhs = expr.target();
				final Expr rhs = expr.expr();

				if ((lhs.key() == DeadCodeElimination.DEAD)
						&& (rhs.key() == DeadCodeElimination.LIVE)) {
					rhs.setParent(null);
					expr.replaceWith(rhs, false);

					lhs.cleanup();
					expr.cleanupOnly();

					lhs.setKey(DeadCodeElimination.DEAD);
					expr.setKey(DeadCodeElimination.DEAD);

					rhs.visit(this);

				} else {
					expr.visitChildren(this);
				}
			}
		});

		// Pull out live expressions from their dead parents. Gee, Nate,
		// what a lovely sentiment. I'll think I'll send that one to
		// Hallmark.
		cfg.visit(new TreeVisitor() {
			public void visitStmt(final Stmt stmt) {
				if (stmt.key() == DeadCodeElimination.DEAD) {
					stmt.visitChildren(this);
				}
			}

			public void visitExpr(final Expr expr) {
				if (expr.key() == DeadCodeElimination.DEAD) {
					expr.visitChildren(this);
					return;
				}

				final Node parent = expr.parent();

				if (parent.key() == DeadCodeElimination.LIVE) {
					// expr will removed later
					return;
				}

				if (parent instanceof ExprStmt) {
					// The expr and its parent are both dead, but expr resides
					// in an ExprStmt. We want the parent after all.
					parent.setKey(DeadCodeElimination.LIVE);
					return;
				}

				// We are going to remove the expr's parent, but keep the
				// expr. Add eval(expr) [ExprStmt] before the stmt containing
				// the expr. This is safe, since any exprs to the left in the
				// statement's tree which are live have already been
				// extracted.

				final Stmt oldStmt = expr.stmt();

				final Tree tree = parent.block().tree();

				// Replace the expr with an unused stack expr.
				final StackExpr t = tree.newStack(expr.type());
				expr.replaceWith(t, false);
				t.setValueNumber(expr.valueNumber());

				final ExprStmt stmt = new ExprStmt(expr);
				stmt.setValueNumber(expr.valueNumber());
				stmt.setKey(DeadCodeElimination.LIVE);

				tree.addStmtBefore(stmt, oldStmt);

				// The old statement is dead and will be removed later.
				Assert.isTrue(oldStmt.key() == DeadCodeElimination.DEAD,
						oldStmt + " should be dead");
			}
		});

		// Finally, remove the dead statements from the Tree.
		cfg.visit(new TreeVisitor() {
			public void visitTree(final Tree tree) {
				final Iterator e = tree.stmts().iterator();

				while (e.hasNext()) {
					final Stmt stmt = (Stmt) e.next();

					if (stmt.key() == DeadCodeElimination.DEAD) {
						if (stmt instanceof LabelStmt) {
							continue;
						}

						if (stmt instanceof JumpStmt) {
							continue;
						}

						if (DeadCodeElimination.DEBUG) {
							System.out.println("Removing DEAD " + stmt);
						}

						e.remove();
					}
				}
			}
		});

		worklist = null;
	}

	// /**
	// * Make all of a statement's children LIVE. I don't think its used.
	// *
	// * @param stmt
	// * A statement whose children to make live.
	// */
	// void reviveStmt(Stmt stmt) {
	// stmt.visit(new TreeVisitor() {
	// public void visitExpr(Expr expr) {
	// expr.setKey(LIVE);
	// expr.visitChildren(this);
	// }
	// });
	// }

	/**
	 * Make a node and all of its children (recursively) LIVE.
	 * 
	 * @param node
	 *            A node to make LIVE.
	 */
	void makeLive(Node node) {

		if (node instanceof StoreExpr) {
			// Make the StoreExpr, its target, and its RHS live. Add the
			// target and the RHS to the worklist.

			final StoreExpr expr = (StoreExpr) node;

			if (expr.key() == DeadCodeElimination.DEAD) {
				if (DeadCodeElimination.DEBUG) {
					System.out.println("making live " + expr + " in "
							+ expr.parent());
				}

				expr.setKey(DeadCodeElimination.LIVE);
			}

			if (expr.target().key() == DeadCodeElimination.DEAD) {
				if (DeadCodeElimination.DEBUG) {
					System.out.println("making live " + expr.target() + " in "
							+ expr);
				}

				expr.target().setKey(DeadCodeElimination.LIVE);

				if (expr.target() instanceof VarExpr) {
					worklist.add(expr.target());
				}
			}

			if (expr.expr().key() == DeadCodeElimination.DEAD) {
				if (DeadCodeElimination.DEBUG) {
					System.out.println("making live " + expr.expr() + " in "
							+ expr);
				}

				expr.expr().setKey(DeadCodeElimination.LIVE);

				if (expr.expr() instanceof VarExpr) {
					worklist.add(expr.expr());
				}
			}
		}

		if (node instanceof Expr) {
			// If one expression inside an ExprStmt is live, then the entire
			// ExprStmt is live.
			final Node parent = ((Expr) node).parent();

			if (parent instanceof ExprStmt) {
				node = parent;
			}
		}

		node.visit(new TreeVisitor() {
			public void visitStoreExpr(final StoreExpr expr) {
				// Don't make local variable targets live yet. If the
				// variable is used in a live expression, the target will be
				// made live later.
				if (expr.target() instanceof LocalExpr) {
					expr.expr().visit(this);

				} else {
					visitExpr(expr);
				}
			}

			public void visitVarExpr(final VarExpr expr) {
				if (expr.key() == DeadCodeElimination.DEAD) {
					if (DeadCodeElimination.DEBUG) {
						System.out.println("making live " + expr + " in "
								+ expr.parent());
					}

					expr.setKey(DeadCodeElimination.LIVE);
					worklist.add(expr);
				}
			}

			public void visitExpr(final Expr expr) {
				if (expr.key() == DeadCodeElimination.DEAD) {
					if (DeadCodeElimination.DEBUG) {
						System.out.println("making live " + expr + " in "
								+ expr.parent());
					}

					expr.setKey(DeadCodeElimination.LIVE);
				}

				expr.visitChildren(this);
			}

			public void visitStmt(final Stmt stmt) {
				if (stmt.key() == DeadCodeElimination.DEAD) {
					if (DeadCodeElimination.DEBUG) {
						System.out.println("making live " + stmt);
					}

					stmt.setKey(DeadCodeElimination.LIVE);
				}

				stmt.visitChildren(this);
			}
		});
	}
}
