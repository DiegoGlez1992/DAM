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

import java.io.*;
import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;
import EDU.purdue.cs.bloat.util.*;

/**
 * <tt>ValueFolder</tt> uses a <tt>TreeVisitor</tt> to examine a
 * <tt>Node</tt> to determine whether or not it can be simplified. For
 * instance, redundent checks are removed and algebraic identities are
 * exploited.
 * 
 * @see SideEffectChecker
 */
class ValueFolder extends TreeVisitor {
	Node node; // (New) value of the folded node

	// If a value number has been reduced down to a constant number
	// (ConstantExpr), this array maintains that mapping.
	ResizeableArrayList values;

	// Keeps track of which value numbers correspond to expressions that
	// allocate new objects (NewExpr, NewArrayExpr, and NewMultiArrayExpr).
	BitSet news;

	// Local variable representing the this pointer
	LocalExpr thisPtr;

	// Used to determine whether or not a Node in the expression tree
	// has side effects
	SideEffectChecker sideEffects;

	// Do we actually replace expressions with common value #'s? Or do
	// we just calculate the value numbers.
	boolean replace;

	// 
	ArrayList clean;

	/**
	 * Constructor.
	 * 
	 * @param replace
	 *            Do we replace values with their folded values?
	 * @param context
	 *            Needed to create a <Tt>SideEffectChecker</tt>
	 */
	public ValueFolder(final boolean replace, final EditorContext context) {
		this.node = null;
		this.replace = replace;
		this.clean = new ArrayList();
		this.sideEffects = new SideEffectChecker(context);

		this.values = new ResizeableArrayList();
		this.news = new BitSet();
		this.thisPtr = null;
	}

	/**
	 * Cleans up nodes that
	 */
	public void cleanup() {
		final Iterator iter = clean.iterator();

		while (iter.hasNext()) {
			final Node node = (Node) iter.next();
			node.cleanup();
		}
	}

	/**
	 * Returns the simplified version of the most recently simplified
	 * <tt>Node</tt>. Returns null if no simplification occurred.
	 */
	public Node replacement() {
		return node;
	}

	public void visitNode(final Node node) {
	}

	public void visitLocalExpr(final LocalExpr expr) {
		// Determines whether or not the LocalExpr in question is the this
		// pointer. If the LocalExpr resides within an InitStmt, and the
		// LocalExpr is the first variable defined by the InitStmt, it is
		// the this pointer.

		if (thisPtr != null) {
			return;
		}

		if (expr.parent() instanceof InitStmt) {
			final InitStmt stmt = (InitStmt) expr.parent();

			final MethodEditor method = stmt.block().graph().method();

			if (!method.isStatic()) {
				Assert.isTrue(stmt.targets().length > 0);

				if (expr == stmt.targets()[0]) {
					thisPtr = expr;

					if (ValueFolding.DEBUG) {
						System.out.println("this = " + thisPtr);
					}
				}
			}
		}
	}

	public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
		if (!(stmt.target() instanceof LocalExpr)) {
			return;
		}

		// A PhiJoinStmt may be eliminated if it is meaningless (all of
		// its operands have the same value number) or it is redundent
		// (its value is already computed by another PhiJoinStmt).

		final int v = stmt.valueNumber();
		int ov = -1; // Operand's value number

		final Iterator iter = stmt.operands().iterator();

		// Examine each operand of the PhiJoinStmt.
		while (iter.hasNext()) {
			final Expr expr = (Expr) iter.next();

			if (replace) {
				sideEffects.reset();
				expr.visit(sideEffects);

				if (sideEffects.hasSideEffects()) {
					return;
				}
			}

			if (expr.valueNumber() == -1) {
				continue;
			}

			if ((ov != -1) && (expr.valueNumber() != ov)) {
				// At least two operands have different value numbers. The
				// PhiJoinStmt cannot be simplified.
				return;
			}

			ov = expr.valueNumber();
		}

		if (ov == -1) {
			// We cannot replace an PhiJoinStmt with no operands
			Assert.isFalse(replace && (stmt.operands().size() == 0));
			ov = v;
		}

		// All operands have same value number or -1.
		values.ensureSize(Math.max(v, ov) + 1);
		final ConstantExpr value = (ConstantExpr) values.get(ov);

		if (value != null) {
			node = value;
			values.set(v, value);

			if (replace) {
				stmt.block().tree().removeStmt(stmt);
			}
		}
	}

	public void visitStoreExpr(final StoreExpr expr) {
		if (expr.expr() instanceof CheckExpr) {
			// This makes copy propagation more effective after PRE.
			// x := rc(y) --> rc(x := y)
			final CheckExpr rc = (CheckExpr) expr.expr();

			if (replace) {
				final Node parent = expr.parent();

				// x := rc(y) --> x := y
				expr.visit(new ReplaceVisitor(rc, rc.expr()));

				// rc(y) --> rc(x := y)
				rc.visit(new ReplaceVisitor(rc.expr(), expr));

				// x := rc(y) --> rc(x := y)
				parent.visit(new ReplaceVisitor(expr, rc));

				node = rc;

			} else {
				// Don't bother.
				node = null;
			}

			return;
		}

		if (expr.target() instanceof LocalExpr) {
			// If we're assigning into a local variable,

			final int v = expr.valueNumber();
			final int lv = expr.target().valueNumber();
			final int rv = expr.expr().valueNumber();

			int max = v;
			max = Math.max(max, lv);
			max = Math.max(max, rv);
			values.ensureSize(max + 1);

			boolean reffects = false;

			if (replace) {
				sideEffects.reset();
				expr.expr().visit(sideEffects);
				reffects = sideEffects.hasSideEffects();
			}

			final ConstantExpr rexpr = (ConstantExpr) values.get(rv);

			if (rexpr != null) {
				// The entire StoreExpr has a constant value
				if (!replace) {
					node = rexpr;
					values.set(v, node);

				} else if (!reffects && (expr.target().uses().size() == 0)) {
					// Replace the rhs with constant mapped to its value number
					node = new ConstantExpr(rexpr.value(), expr.type());
					node.setValueNumber(v);
					values.set(v, node);
					expr.replaceWith(node);
				}
			}
		}
	}

	public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
		// Keep track of the value numbers of expressions that create new
		// objects.

		if (expr.valueNumber() != -1) {
			if (ValueFolding.DEBUG) {
				System.out.println("New " + expr);
			}

			news.set(expr.valueNumber());
		}
	}

	public void visitNewArrayExpr(final NewArrayExpr expr) {
		// Keep track of the value numbers of expressions that create new
		// objects.

		if (expr.valueNumber() != -1) {
			if (ValueFolding.DEBUG) {
				System.out.println("New " + expr);
			}

			news.set(expr.valueNumber());
		}
	}

	public void visitNewExpr(final NewExpr expr) {
		// Keep track of the value number of expression that create new
		// objects.

		if (expr.valueNumber() != -1) {
			if (ValueFolding.DEBUG) {
				System.out.println("New " + expr);
			}

			news.set(expr.valueNumber());
		}
	}

	public void visitRCExpr(final RCExpr expr) {
		boolean move = false; // Can we remove the RCExpr

		final int v = expr.expr().valueNumber();

		if (expr.expr() instanceof RCExpr) {
			// If the expression being checked for residency is itself an
			// RCExpr, then the outer one is redundent.
			move = true;

			if (ValueFolding.DEBUG) {
				System.out.println("folding redundant rc in " + expr);
			}

		} else if (v != -1) {
			if ((thisPtr != null) && (thisPtr.valueNumber() == v)) {
				// We know that the this pointer is always resident, so we
				// don't need to perform an rc on it.
				move = true;

				if (ValueFolding.DEBUG) {
					System.out.println("folding rc(this) = " + expr);
				}

			} else if (news.get(v)) {
				// We know that the result of a new expression is always
				// resident, so we don't need to perform an rc on it.
				move = true;

				if (ValueFolding.DEBUG) {
					System.out.println("folding rc(new) = " + expr);
				}
			}
		}

		if (move) {
			node = expr.expr();

			if (replace) {
				// rc(this) --> this
				// rc(rc(x)) --> rc(x)
				node.setParent(null);
				expr.replaceWith(node, false);
				expr.cleanupOnly();
			}
		}
	}

	public void visitZeroCheckExpr(final ZeroCheckExpr expr) {
		boolean move = false;

		final int v = expr.expr().valueNumber();

		if (expr.expr() instanceof ZeroCheckExpr) {
			move = true;

			if (ValueFolding.DEBUG) {
				System.out.println("folding redundant ZeroCheck in " + expr);
			}

		} else if (v != -1) {
			if ((thisPtr != null) && (thisPtr.valueNumber() == v)) {
				// The this pointer can't be null.

				move = true;

				if (ValueFolding.DEBUG) {
					System.out.println("folding ZeroCheck(this) = " + expr);
				}

			} else if (news.get(v)) {
				// Newly created objects can't be null.
				move = true;

				if (ValueFolding.DEBUG) {
					System.out.println("folding ZeroCheck(new) = " + expr);
				}

			} else {
				// Check to see if the value number associated with the
				// expression being checked for zero-ness has a constant value
				// of zero.
				ConstantExpr eexpr = null;

				if (v < values.size()) {
					eexpr = (ConstantExpr) values.get(v);
				}

				if (eexpr != null) {
					final Object value = eexpr.value();

					if (value instanceof Long) {
						if (((Long) value).longValue() != 0L) {
							move = true;
						}

					} else if ((value instanceof Byte)
							|| (value instanceof Short)
							|| (value instanceof Integer)) {
						if (((Number) value).intValue() != 0) {
							move = true;
						}

					} else if (value instanceof Character) {
						if (((Character) value).charValue() != 0) {
							move = true;
						}
					}
				}
			}
		}

		if (move) {
			node = expr.expr();

			if (replace) {
				// ZeroCheck(1) --> 1
				// ZeroCheck(this) --> this
				// ZeroCheck(ZeroCheck(x)) --> ZeroCheck(x)
				node.setParent(null);
				expr.replaceWith(node, false);
				expr.cleanupOnly();
			}
		}
	}

	public void visitUCExpr(final UCExpr expr) {
		if (expr.expr() instanceof UCExpr) {
			// Remove redundent update checks

			final UCExpr uc = (UCExpr) expr.expr();

			if (uc.kind() == expr.kind()) {
				node = uc;

				if (replace) {
					// uc(uc(x)) --> uc(x)
					expr.visit(new ReplaceVisitor(uc, uc.expr()));
					uc.cleanupOnly();
				}
			}
		}
	}

	public void visitArithExpr(final ArithExpr expr) {
		if (expr.left().type().isIntegral()) {
			foldArithInteger(expr);
		} else if (expr.left().type().equals(Type.LONG)) {
			foldArithLong(expr);
		} else if (expr.left().type().equals(Type.FLOAT)) {
			foldArithFloat(expr);
		} else if (expr.left().type().equals(Type.DOUBLE)) {
			foldArithDouble(expr);
		}
	}

	/**
	 * Look for integer arithmetic identities...
	 */
	private void foldArithInteger(final ArithExpr expr) {
		final int v = expr.valueNumber();
		final int lv = expr.left().valueNumber();
		final int rv = expr.right().valueNumber();

		int max = v;
		max = Math.max(max, lv);
		max = Math.max(max, rv);
		values.ensureSize(max + 1);

		ConstantExpr lexpr = null;
		ConstantExpr rexpr = null;

		if ((0 <= lv) && (0 <= lv) && (lv < values.size())) {
			lexpr = (ConstantExpr) values.get(lv);
		}

		if ((0 <= rv) && (0 <= rv) && (rv < values.size())) {
			rexpr = (ConstantExpr) values.get(rv);
		}

		boolean leffects = false;
		boolean reffects = false;

		if (replace) {
			sideEffects.reset();
			expr.left().visit(sideEffects);
			leffects = sideEffects.hasSideEffects();

			sideEffects.reset();
			expr.right().visit(sideEffects);
			reffects = sideEffects.hasSideEffects();
		}

		if ((lexpr != null) && (rexpr != null) && !leffects && !reffects) {
			// Okay, both of the ArithExpr's operands evaluate to constants
			// and there are no side effects. We may be able to exploit
			// various algebraic identites.
			Integer value = null;

			final int lval = ((Number) lexpr.value()).intValue();
			final int rval = ((Number) rexpr.value()).intValue();

			switch (expr.operation()) {
			case ArithExpr.ADD:
				value = new Integer(lval + rval);
				break;
			case ArithExpr.AND:
				value = new Integer(lval & rval);
				break;
			case ArithExpr.DIV:
				if (rval != 0) {
					value = new Integer(lval / rval);
				}
				break;
			case ArithExpr.MUL:
				value = new Integer(lval * rval);
				break;
			case ArithExpr.IOR:
				value = new Integer(lval | rval);
				break;
			case ArithExpr.REM:
				if (rval != 0) {
					value = new Integer(lval % rval);
				}
				break;
			case ArithExpr.SUB:
				value = new Integer(lval - rval);
				break;
			case ArithExpr.XOR:
				value = new Integer(lval ^ rval);
				break;
			default:
				break;
			}

			if (value != null) {
				node = new ConstantExpr(value, expr.type());
				node.setValueNumber(v);

				values.set(v, node);

				if (replace) {
					expr.replaceWith(node);
				}
			}

		} else if ((lexpr == null) && (rexpr != null) && !reffects) {
			// Only the right operand evaluates to a constant...
			final int rval = ((Number) rexpr.value()).intValue();

			switch (rval) {
			case 0:
				// x + 0 = x
				// x - 0 = x
				// x | 0 = x
				// x * 0 = 0
				// x & 0 = 0
				switch (expr.operation()) {
				case ArithExpr.ADD:
				case ArithExpr.SUB:
				case ArithExpr.IOR:
					node = expr.left();

					if (replace) {
						node.setParent(null);
						expr.replaceWith(node, false);
						expr.right().cleanup();
						expr.cleanupOnly();
					}
					break;
				case ArithExpr.MUL:
				case ArithExpr.AND:
					node = new ConstantExpr(new Integer(0), expr.type());
					node.setValueNumber(v);

					values.set(v, node);

					if (replace) {
						expr.replaceWith(node);
					}
					break;
				}
				break;
			case 1:
				// x * 1 = x
				// x / 1 = x
				// x % 1 = 0
				switch (expr.operation()) {
				case ArithExpr.MUL:
				case ArithExpr.DIV:
					node = expr.left();

					if (replace) {
						node.setParent(null);
						expr.replaceWith(node, false);
						expr.right().cleanup();
						expr.cleanupOnly();
					}
					break;
				case ArithExpr.REM:
					node = new ConstantExpr(new Integer(0), expr.type());
					node.setValueNumber(v);

					values.set(v, node);

					if (replace) {
						expr.replaceWith(node);
					}
					break;
				}
				break;
			case -1:
				// x * -1 = -x
				// x / -1 = -x
				switch (expr.operation()) {
				case ArithExpr.MUL:
				case ArithExpr.DIV:
					if (replace) {
						expr.left().setParent(null);
						node = new NegExpr(expr.left(), expr.type());
						node.setValueNumber(v);
						expr.replaceWith(node, false);
						expr.right().cleanup();
						expr.cleanupOnly();

					} else {
						node = new NegExpr((Expr) expr.left().clone(), expr
								.type());
						node.setValueNumber(v);
						clean.add(node);
					}
					break;
				}
				break;
			}

		} else if ((lexpr != null) && (rexpr == null) && !leffects) {
			// Only left operand resolves to a constant value...
			final int lval = ((Number) lexpr.value()).intValue();

			switch (lval) {
			case 0:
				// 0 + x = x
				// 0 - x = -x
				// 0 | x = x
				// 0 * x = 0
				// 0 & x = 0
				switch (expr.operation()) {
				case ArithExpr.ADD:
				case ArithExpr.IOR:
					node = expr.right();

					if (replace) {
						node.setParent(null);
						expr.replaceWith(node, false);
						expr.left().cleanup();
						expr.cleanupOnly();
					}
					break;
				case ArithExpr.SUB:
					if (replace) {
						expr.right().setParent(null);
						node = new NegExpr(expr.right(), expr.type());
						node.setValueNumber(v);
						expr.replaceWith(node, false);
						expr.left().cleanup();
						expr.cleanupOnly();
					} else {
						node = new NegExpr((Expr) expr.right().clone(), expr
								.type());
						node.setValueNumber(v);
						clean.add(node);
					}
					break;
				case ArithExpr.MUL:
				case ArithExpr.AND:
					node = new ConstantExpr(new Integer(0), expr.type());
					node.setValueNumber(v);

					values.set(v, node);

					if (replace) {
						expr.replaceWith(node);
					}
					break;
				}
				break;
			case 1:
				// 1 * x = x
				switch (expr.operation()) {
				case ArithExpr.MUL:
					node = expr.right();

					if (replace) {
						node.setParent(null);
						expr.replaceWith(node, false);
						expr.left().cleanup();
						expr.cleanupOnly();
					}
					break;
				}
				break;
			case -1:
				// -1 * x = -x
				switch (expr.operation()) {
				case ArithExpr.MUL:
					if (replace) {
						expr.right().setParent(null);
						node = new NegExpr(expr.right(), expr.type());
						node.setValueNumber(v);
						expr.replaceWith(node, false);
						expr.left().cleanup();
						expr.cleanupOnly();
					} else {
						node = new NegExpr((Expr) expr.right().clone(), expr
								.type());
						node.setValueNumber(v);
						clean.add(node);
					}
					break;
				}
				break;
			}
		}
	}

	/**
	 * Look for long arithmetic indentities...
	 */
	private void foldArithLong(final ArithExpr expr) {
		final int v = expr.valueNumber();
		final int lv = expr.left().valueNumber();
		final int rv = expr.right().valueNumber();

		int max = v;
		max = Math.max(max, lv);
		max = Math.max(max, rv);
		values.ensureSize(max + 1);

		ConstantExpr lexpr = null;
		ConstantExpr rexpr = null;

		if ((0 <= lv) && (lv < values.size())) {
			lexpr = (ConstantExpr) values.get(lv);
		}

		if ((0 <= rv) && (rv < values.size())) {
			rexpr = (ConstantExpr) values.get(rv);
		}

		boolean leffects = false;
		boolean reffects = false;

		if (replace) {
			sideEffects.reset();
			expr.left().visit(sideEffects);
			leffects = sideEffects.hasSideEffects();

			sideEffects.reset();
			expr.right().visit(sideEffects);
			reffects = sideEffects.hasSideEffects();
		}

		if ((lexpr != null) && (rexpr != null) && !leffects && !reffects) {
			Number value = null;

			final long lval = ((Long) lexpr.value()).longValue();
			final long rval = ((Long) rexpr.value()).longValue();

			switch (expr.operation()) {
			case ArithExpr.ADD:
				value = new Long(lval + rval);
				break;
			case ArithExpr.AND:
				value = new Long(lval & rval);
				break;
			case ArithExpr.DIV:
				if (rval != 0) {
					value = new Long(lval / rval);
				}
				break;
			case ArithExpr.MUL:
				value = new Long(lval * rval);
				break;
			case ArithExpr.IOR:
				value = new Long(lval | rval);
				break;
			case ArithExpr.REM:
				if (rval != 0L) {
					value = new Long(lval % rval);
				}
				break;
			case ArithExpr.SUB:
				value = new Long(lval - rval);
				break;
			case ArithExpr.XOR:
				value = new Long(lval ^ rval);
				break;
			case ArithExpr.CMP:
				if (lval > rval) {
					value = new Integer(1);
				} else if (lval < rval) {
					value = new Integer(-1);
				} else {
					value = new Integer(0);
				}
				break;
			default:
				break;
			}

			if (value != null) {
				node = new ConstantExpr(value, expr.type());
				node.setValueNumber(v);

				values.set(v, node);

				if (replace) {
					expr.replaceWith(node);
				}
			}
		} else if ((lexpr == null) && (rexpr != null)) {
			final long rval = ((Long) rexpr.value()).longValue();

			if (reffects) {
				return;
			}

			if (rval == 0L) {
				// x + 0 = x
				// x - 0 = x
				// x | 0 = x
				// x * 0 = 0
				// x & 0 = 0
				switch (expr.operation()) {
				case ArithExpr.ADD:
				case ArithExpr.SUB:
				case ArithExpr.IOR:
					node = expr.left();

					if (replace) {
						node.setParent(null);
						expr.replaceWith(node, false);
						expr.right().cleanup();
						expr.cleanupOnly();
					}
					break;
				case ArithExpr.MUL:
				case ArithExpr.AND:
					node = new ConstantExpr(new Long(0L), expr.type());
					node.setValueNumber(v);

					values.set(v, node);

					if (replace) {
						expr.replaceWith(node);
					}
					break;
				}
			} else if (rval == 1L) {
				// x * 1 = x
				// x / 1 = x
				// x % 1 = 0
				switch (expr.operation()) {
				case ArithExpr.MUL:
				case ArithExpr.DIV:
					node = expr.left();

					if (replace) {
						node.setParent(null);
						expr.replaceWith(node, false);
						expr.right().cleanup();
						expr.cleanupOnly();
					}
					break;
				case ArithExpr.REM:
					node = new ConstantExpr(new Long(0L), expr.type());
					node.setValueNumber(v);

					values.set(v, node);

					if (replace) {
						expr.replaceWith(node);
					}
					break;
				}
			} else if (rval == -1L) {
				// x * -1 = -x
				// x / -1 = -x
				switch (expr.operation()) {
				case ArithExpr.MUL:
				case ArithExpr.DIV:
					if (replace) {
						expr.left().setParent(null);
						node = new NegExpr(expr.left(), Type.LONG);
						node.setValueNumber(v);
						expr.replaceWith(node, false);
						expr.right().cleanup();
						expr.cleanupOnly();
					} else {
						node = new NegExpr((Expr) expr.left().clone(),
								Type.LONG);
						node.setValueNumber(v);
						clean.add(node);
					}
					break;
				}
			}
		} else if ((lexpr != null) && (rexpr == null)) {
			final long lval = ((Long) lexpr.value()).longValue();
			if (lval == 0L) {
				// 0 + x = x
				// 0 - x = -x
				// 0 | x = x
				// 0 * x = 0
				// 0 & x = 0
				switch (expr.operation()) {
				case ArithExpr.ADD:
				case ArithExpr.IOR:
					node = expr.right();

					if (replace) {
						node.setParent(null);
						expr.replaceWith(node, false);
						expr.left().cleanup();
						expr.cleanupOnly();
					}
					break;
				case ArithExpr.SUB:
					if (replace) {
						expr.right().setParent(null);
						node = new NegExpr(expr.right(), Type.LONG);
						node.setValueNumber(v);
						expr.replaceWith(node, false);
						expr.left().cleanup();
						expr.cleanupOnly();
					} else {
						node = new NegExpr((Expr) expr.right().clone(),
								Type.LONG);
						node.setValueNumber(v);
						clean.add(node);
					}
					break;
				case ArithExpr.MUL:
				case ArithExpr.AND:
					node = new ConstantExpr(new Long(0L), expr.type());
					node.setValueNumber(v);

					values.set(v, node);

					if (replace) {
						expr.replaceWith(node);
					}
					break;
				}
			} else if (lval == 1L) {
				// 1 * x = x
				switch (expr.operation()) {
				case ArithExpr.MUL:
					node = expr.right();

					if (replace) {
						node.setParent(null);
						expr.replaceWith(node, false);
						expr.left().cleanup();
						expr.cleanupOnly();
					}
					break;
				}
			} else if (lval == -1L) {
				// -1 * x = -x
				switch (expr.operation()) {
				case ArithExpr.MUL:
					if (replace) {
						expr.right().setParent(null);
						node = new NegExpr(expr.right(), Type.LONG);
						node.setValueNumber(v);
						expr.replaceWith(node, false);
						expr.left().cleanup();
						expr.cleanupOnly();
					} else {
						node = new NegExpr((Expr) expr.right().clone(),
								Type.LONG);
						node.setValueNumber(v);
						clean.add(node);
					}
					break;
				}
			}
		}
	}

	/**
	 * Look for float arithmetic identities...
	 */
	private void foldArithFloat(final ArithExpr expr) {
		final int v = expr.valueNumber();
		final int lv = expr.left().valueNumber();
		final int rv = expr.right().valueNumber();

		int max = v;
		max = Math.max(max, lv);
		max = Math.max(max, rv);
		values.ensureSize(max + 1);

		ConstantExpr lexpr = null;
		ConstantExpr rexpr = null;

		if ((0 <= lv) && (lv < values.size())) {
			lexpr = (ConstantExpr) values.get(lv);
		}

		if ((0 <= rv) && (rv < values.size())) {
			rexpr = (ConstantExpr) values.get(rv);
		}

		if ((lexpr == null) || (rexpr == null)) {
			return;
		}

		final Float rvalue = (Float) rexpr.value();
		final Float lvalue = (Float) lexpr.value();

		if (lvalue.isNaN() || lvalue.isInfinite()) {
			return;
		}

		if (rvalue.isNaN() || rvalue.isInfinite()) {
			return;
		}

		boolean leffects = false;
		boolean reffects = false;

		if (replace) {
			sideEffects.reset();
			expr.left().visit(sideEffects);
			leffects = sideEffects.hasSideEffects();

			sideEffects.reset();
			expr.right().visit(sideEffects);
			reffects = sideEffects.hasSideEffects();

			if (leffects || reffects) {
				return;
			}
		}

		// Can't fold (x op C) = y since x may be NaN or infinite
		// or +/-0.0.

		Number value = null;

		final float lval = lvalue.floatValue();
		final float rval = rvalue.floatValue();

		switch (expr.operation()) {
		case ArithExpr.ADD:
			value = new Float(lval + rval);
			break;
		case ArithExpr.DIV:
			value = new Float(lval / rval);
			break;
		case ArithExpr.MUL:
			value = new Float(lval * rval);
			break;
		case ArithExpr.REM:
			value = new Float(lval % rval);
			break;
		case ArithExpr.SUB:
			value = new Float(lval - rval);
			break;
		case ArithExpr.CMP:
			if (lval > rval) {
				value = new Integer(1);
			} else if (lval < rval) {
				value = new Integer(-1);
			} else {
				value = new Integer(0);
			}
			break;
		default:
			break;
		}

		if (value != null) {
			node = new ConstantExpr(value, expr.type());
			node.setValueNumber(v);

			values.set(v, node);

			if (replace) {
				expr.replaceWith(node);
			}
		}
	}

	/**
	 * Look for double arithmetic identities...
	 */
	private void foldArithDouble(final ArithExpr expr) {
		final int v = expr.valueNumber();
		final int lv = expr.left().valueNumber();
		final int rv = expr.right().valueNumber();

		int max = v;
		max = Math.max(max, lv);
		max = Math.max(max, rv);
		values.ensureSize(max + 1);

		ConstantExpr lexpr = null;
		ConstantExpr rexpr = null;

		if ((0 <= lv) && (lv < values.size())) {
			lexpr = (ConstantExpr) values.get(lv);
		}

		if ((0 <= rv) && (rv < values.size())) {
			rexpr = (ConstantExpr) values.get(rv);
		}

		if ((lexpr == null) || (rexpr == null)) {
			return;
		}

		final Double rvalue = (Double) rexpr.value();
		final Double lvalue = (Double) lexpr.value();

		if (lvalue.isNaN() || lvalue.isInfinite()) {
			return;
		}

		if (rvalue.isNaN() || rvalue.isInfinite()) {
			return;
		}

		boolean leffects = false;
		boolean reffects = false;

		if (replace) {
			sideEffects.reset();
			expr.left().visit(sideEffects);
			leffects = sideEffects.hasSideEffects();

			sideEffects.reset();
			expr.right().visit(sideEffects);
			reffects = sideEffects.hasSideEffects();

			if (leffects || reffects) {
				return;
			}
		}

		// Can't fold (x op C) = y since x may be NaN or infinite
		// or +/-0.0.

		Number value = null;

		final double lval = lvalue.doubleValue();
		final double rval = rvalue.doubleValue();

		switch (expr.operation()) {
		case ArithExpr.ADD:
			value = new Double(lval + rval);
			break;
		case ArithExpr.DIV:
			value = new Double(lval / rval);
			break;
		case ArithExpr.MUL:
			value = new Double(lval * rval);
			break;
		case ArithExpr.REM:
			value = new Double(lval % rval);
			break;
		case ArithExpr.SUB:
			value = new Double(lval - rval);
			break;
		case ArithExpr.CMP:
			if (lval > rval) {
				value = new Integer(1);
			} else if (lval < rval) {
				value = new Integer(-1);
			} else {
				value = new Integer(0);
			}
			break;
		default:
			break;
		}

		if (value != null) {
			node = new ConstantExpr(value, expr.type());
			node.setValueNumber(v);

			values.set(v, node);

			if (replace) {
				expr.replaceWith(node);
			}
		}
	}

	public void visitCastExpr(final CastExpr expr) {
		// Note: we can't fold i2b, i2c, i2s, i2l, i2f, f2i, ...
		// We only fold (String) "" and (C) null.

		final int v = expr.valueNumber();
		final int ev = expr.expr().valueNumber();
		values.ensureSize(Math.max(v, ev) + 1);

		ConstantExpr eexpr = null;

		if ((0 <= ev) && (ev < values.size())) {
			eexpr = (ConstantExpr) values.get(ev);
		}

		if (eexpr == null) {
			return;
		}

		if (replace) {
			sideEffects.reset();
			expr.expr().visit(sideEffects);
			final boolean effects = sideEffects.hasSideEffects();

			if (effects) {
				return;
			}
		}

		final Object evalue = eexpr.value();

		if ((evalue instanceof String) && expr.castType().equals(Type.STRING)) {
			// The ConstantExpr must be ""
			node = new ConstantExpr(evalue, expr.castType());
			node.setValueNumber(v);

			values.set(v, node);

			if (replace) {
				expr.replaceWith(node);
			}

			return;
		}

		if (expr.castType().isReference()) {
			if ((evalue == null) && expr.castType().isReference()) {
				// The ConstantExpr is null
				node = new ConstantExpr(null, expr.castType());
				node.setValueNumber(v);

				values.set(v, node);

				if (replace) {
					expr.replaceWith(node);
				}
			}

			return;
		}
	}

	public void visitNegExpr(final NegExpr expr) {
		final int v = expr.valueNumber();
		final int ev = expr.expr().valueNumber();
		values.ensureSize(Math.max(v, ev) + 1);

		ConstantExpr eexpr = null;

		if ((0 <= ev) && (ev < values.size())) {
			eexpr = (ConstantExpr) values.get(ev);
		}

		if (eexpr != null) {
			// If the operand of the NegExpr is a constant value, simply
			// replace the constant with its negation and remove the NegExpr.

			final Number evalue = (Number) eexpr.value();

			boolean eeffects = false;

			if (replace) {
				sideEffects.reset();
				expr.expr().visit(sideEffects);
				eeffects = sideEffects.hasSideEffects();
			}

			if (!eeffects) {
				Number value = null;

				if (evalue instanceof Integer) {
					value = new Integer(-evalue.intValue());
				} else if (value instanceof Long) {
					value = new Long(-evalue.longValue());
				} else if (value instanceof Float) {
					value = new Float(-evalue.floatValue());
				} else if (value instanceof Double) {
					value = new Double(-evalue.doubleValue());
				}

				if (value != null) {
					node = new ConstantExpr(value, expr.type());
					node.setValueNumber(v);

					values.set(v, node);

					if (replace) {
						expr.replaceWith(node);
					}

					return;
				}
			}
		}

		if (expr.expr() instanceof NegExpr) {
			// -(-x) --> x

			final NegExpr neg = (NegExpr) expr.expr();
			node = neg.expr();

			if (replace) {
				expr.parent().visit(new ReplaceVisitor(expr, node));
				expr.cleanupOnly();
				neg.cleanupOnly();
			}
		}
	}

	public void visitShiftExpr(final ShiftExpr expr) {
		// Exploit shifting zero bits or shifting zero

		final int v = expr.valueNumber();
		final int ev = expr.expr().valueNumber();
		final int bv = expr.bits().valueNumber();

		int max = v;
		max = Math.max(max, ev);
		max = Math.max(max, bv);
		values.ensureSize(max + 1);

		ConstantExpr eexpr = null;
		ConstantExpr bexpr = null;

		if ((0 <= ev) && (ev < values.size())) {
			eexpr = (ConstantExpr) values.get(ev);
		}

		if ((0 <= bv) && (bv < values.size())) {
			bexpr = (ConstantExpr) values.get(bv);
		}

		Object evalue = null;
		Object bvalue = null;
		boolean eeffects = false;
		boolean beffects = false;

		if (eexpr != null) {
			evalue = eexpr.value();
		}

		if (bexpr != null) {
			bvalue = bexpr.value();
		}

		if (replace) {
			sideEffects.reset();
			expr.expr().visit(sideEffects);
			eeffects = sideEffects.hasSideEffects();

			sideEffects.reset();
			expr.bits().visit(sideEffects);
			beffects = sideEffects.hasSideEffects();
		}

		if ((eexpr == null) && (bexpr != null)) {
			if (bvalue.equals(new Integer(0)) || bvalue.equals(new Long(0))) {
				// x << 0 = x
				// x >> 0 = x
				// x >>> 0 = x
				if (!beffects) {
					node = expr.expr();

					if (replace) {
						node.setParent(null);
						expr.replaceWith(node, false);
						expr.bits().cleanup();
						expr.cleanupOnly();
					}
				}
			}

			return;
		}

		if (beffects) {
			return;
		}

		Object value = null;

		if (evalue instanceof Integer) {
			final int eval = ((Integer) evalue).intValue();

			if (eval == 0) {
				// 0 << x = 0
				// 0 >> x = 0
				// 0 >>> x = 0
				value = evalue;

			} else if (bvalue instanceof Integer) {
				if (replace && eeffects) {
					return;
				}

				final int bval = ((Integer) bvalue).intValue();

				switch (expr.dir()) {
				case ShiftExpr.LEFT:
					value = new Integer(eval << bval);
					break;
				case ShiftExpr.RIGHT:
					value = new Integer(eval >> bval);
					break;
				case ShiftExpr.UNSIGNED_RIGHT:
					value = new Integer(eval >>> bval);
					break;
				}
			}

		} else if (evalue instanceof Long) {
			final long eval = ((Long) evalue).longValue();

			if (eval == 0) {
				// 0 << x = 0
				// 0 >> x = 0
				// 0 >>> x = 0
				value = evalue;

			} else if (bvalue instanceof Integer) {
				if (replace && eeffects) {
					return;
				}

				final int bval = ((Integer) bvalue).intValue();

				switch (expr.dir()) {
				case ShiftExpr.LEFT:
					value = new Long(eval << bval);
					break;
				case ShiftExpr.RIGHT:
					value = new Long(eval >> bval);
					break;
				case ShiftExpr.UNSIGNED_RIGHT:
					value = new Long(eval >>> bval);
					break;
				}
			}
		}

		if (value != null) {
			node = new ConstantExpr(value, expr.type());
			node.setValueNumber(v);

			values.set(v, node);

			if (replace) {
				expr.replaceWith(node);
			}
		}
	}

	public void visitIfZeroStmt(final IfZeroStmt stmt) {
		// If the expression being compared to zero evaluates to a
		// constant, then try to exploit this fact.

		final Block block = stmt.block();
		final FlowGraph cfg = block.graph();

		final int v = stmt.valueNumber();
		final int ev = stmt.expr().valueNumber();
		values.ensureSize(Math.max(ev, v) + 1);

		ConstantExpr eexpr = null;

		if ((0 <= ev) && (ev < values.size())) {
			eexpr = (ConstantExpr) values.get(ev);
		}

		if (eexpr == null) {
			return;
		}

		final Object evalue = eexpr.value();

		boolean eeffects = false;

		if (replace) {
			sideEffects.reset();
			stmt.expr().visit(sideEffects);
			eeffects = sideEffects.hasSideEffects();

			if (eeffects) {
				return;
			}
		}

		JumpStmt jump;

		if (evalue instanceof Integer) {
			final int eval = ((Integer) evalue).intValue();

			boolean result;

			switch (stmt.comparison()) {
			case IfStmt.EQ:
				result = eval == 0;
				break;
			case IfStmt.NE:
				result = eval != 0;
				break;
			case IfStmt.GT:
				result = eval > 0;
				break;
			case IfStmt.GE:
				result = eval >= 0;
				break;
			case IfStmt.LT:
				result = eval < 0;
				break;
			case IfStmt.LE:
				result = eval <= 0;
				break;
			default:
				throw new RuntimeException();
			}

			if (result) {
				// Result is always true, replace IfZeroStmt with an
				// unconditional jump to the true target.
				jump = new GotoStmt(stmt.trueTarget());
				jump.catchTargets().addAll(stmt.catchTargets());
				node = jump;
				node.setValueNumber(v);

				if (replace) {
					stmt.replaceWith(node);
					cfg.removeEdge(block, stmt.falseTarget());
				}

			} else {
				// Result is always false, replace IfZeroStmt with an
				// unconditional jump to the false target.
				jump = new GotoStmt(stmt.falseTarget());
				jump.catchTargets().addAll(stmt.catchTargets());
				node = jump;
				node.setValueNumber(v);

				if (replace) {
					stmt.replaceWith(node);
					cfg.removeEdge(block, stmt.trueTarget());
				}
			}

		} else if (evalue == null) {
			// The expression always evaluates to null

			switch (stmt.comparison()) {
			case IfStmt.EQ:
				// Always jump to true target
				jump = new GotoStmt(stmt.trueTarget());
				jump.catchTargets().addAll(stmt.catchTargets());
				node = jump;
				node.setValueNumber(v);

				if (replace) {
					stmt.replaceWith(node);
					cfg.removeEdge(block, stmt.falseTarget());
				}
				break;

			case IfStmt.NE:
				// Always jump to false target
				jump = new GotoStmt(stmt.falseTarget());
				jump.catchTargets().addAll(stmt.catchTargets());
				node = jump;
				node.setValueNumber(v);

				if (replace) {
					stmt.replaceWith(node);
					cfg.removeEdge(block, stmt.trueTarget());
				}
				break;
			default:
				throw new RuntimeException();
			}
		}
	}

	public void visitIfCmpStmt(final IfCmpStmt stmt) {
		final Block block = stmt.block();
		final FlowGraph cfg = block.graph();

		final int v = stmt.valueNumber();
		final int lv = stmt.left().valueNumber();
		final int rv = stmt.right().valueNumber();

		int max = v;
		max = Math.max(max, lv);
		max = Math.max(max, rv);
		values.ensureSize(max + 1);

		ConstantExpr lexpr = null;
		ConstantExpr rexpr = null;

		if ((0 <= lv) && (lv < values.size())) {
			lexpr = (ConstantExpr) values.get(lv);
		}

		if ((0 <= rv) && (rv < values.size())) {
			rexpr = (ConstantExpr) values.get(rv);
		}

		Object lvalue = null;
		Object rvalue = null;

		if (lexpr != null) {
			lvalue = lexpr.value();
		}

		if (rexpr != null) {
			rvalue = rexpr.value();
		}

		boolean leffects = false;
		boolean reffects = false;

		if (replace) {
			sideEffects.reset();
			stmt.left().visit(sideEffects);
			leffects = sideEffects.hasSideEffects();

			sideEffects.reset();
			stmt.right().visit(sideEffects);
			reffects = sideEffects.hasSideEffects();
		}

		if ((lvalue instanceof Integer) && !leffects) {
			final int lval = ((Integer) lvalue).intValue();

			if ((lval == 0) && !((rvalue instanceof Integer) || reffects)) {
				// If two integers are being compared and the left operand is
				// zero, then we can replace the IfCmpStmt with a IfZeroStmt.

				int cmp;

				switch (stmt.comparison()) {
				case IfStmt.EQ:
					cmp = IfStmt.EQ;
					break;
				case IfStmt.NE:
					cmp = IfStmt.NE;
					break;
				case IfStmt.LT:
					cmp = IfStmt.GT;
					break;
				case IfStmt.LE:
					cmp = IfStmt.GE;
					break;
				case IfStmt.GT:
					cmp = IfStmt.LT;
					break;
				case IfStmt.GE:
					cmp = IfStmt.LE;
					break;
				default:
					throw new RuntimeException();
				}

				if (replace) {
					final JumpStmt jump = new IfZeroStmt(cmp, (Expr) stmt
							.right().clone(), stmt.trueTarget(), stmt
							.falseTarget());
					jump.catchTargets().addAll(stmt.catchTargets());
					node = jump;
					node.setValueNumber(v);
					stmt.replaceWith(node);

				} else {
					// Why bother! -- Nate
					// Why ask why! -- Dave
					node = null;
				}

				return;
			}
		}

		if ((rvalue instanceof Integer) && !reffects) {
			final int rval = ((Integer) rvalue).intValue();

			if ((rval == 0) && !((lvalue instanceof Integer) || leffects)) {
				// If IfCmpStmt compares two integers and the right operand is
				// zero, then replace the IfCmpStmt with an IfZeroStmt.
				final int cmp = stmt.comparison();

				if (replace) {
					final JumpStmt jump = new IfZeroStmt(cmp, (Expr) stmt
							.left().clone(), stmt.trueTarget(), stmt
							.falseTarget());
					jump.catchTargets().addAll(stmt.catchTargets());
					node = jump;
					node.setValueNumber(v);
					stmt.replaceWith(node);

				} else {
					// Why bother! -- Cut and paste! Way to go Nate!
					node = null;
				}

				return;
			}
		}

		if ((lexpr != null) && (lvalue == null) && !leffects) {
			if ((rexpr == null) || (rvalue != null) || reffects) {
				// Left operand evaluates to null. Replace IfCmpStmt with an
				// IfZeroStmt.
				final int cmp = stmt.comparison();

				if (replace) {
					final JumpStmt jump = new IfZeroStmt(cmp, (Expr) stmt
							.right().clone(), stmt.trueTarget(), stmt
							.falseTarget());
					jump.catchTargets().addAll(stmt.catchTargets());
					node = jump;
					node.setValueNumber(v);
					stmt.replaceWith(node);

				} else {
					// Why bother!
					node = null;
				}

				return;
			}
		}

		if ((rexpr != null) && (rvalue == null) && !reffects) {
			if ((lexpr == null) || (lvalue != null) || leffects) {
				// The right operand evaluates to null. Replace IfCmpStmt
				// with an IfZeroStmt. Note that we do not need to mess with
				// operators because if the lhs is being compared against
				// null, it must be a reference type and the only operators
				// are EQ and NE.

				final int cmp = stmt.comparison();

				if (replace) {
					final JumpStmt jump = new IfZeroStmt(cmp, (Expr) stmt
							.left().clone(), stmt.trueTarget(), stmt
							.falseTarget());
					jump.catchTargets().addAll(stmt.catchTargets());
					node = jump;
					node.setValueNumber(v);
					stmt.replaceWith(node);

				} else {
					// Why bother!
					node = null;
				}

				return;
			}
		}

		if (leffects || reffects) {
			return;
		}

		if ((lexpr == null) || (rexpr == null)) {
			return;
		}

		JumpStmt jump;

		if ((lvalue instanceof Integer) && (rvalue instanceof Integer)) {
			// Both operands evaluate to non-zero integers, evaluate the
			// comparison and go from there.

			final int lval = ((Integer) lvalue).intValue();
			final int rval = ((Integer) rvalue).intValue();

			boolean result;

			switch (stmt.comparison()) {
			case IfStmt.EQ:
				result = lval == rval;
				break;
			case IfStmt.NE:
				result = lval != rval;
				break;
			case IfStmt.GT:
				result = lval > rval;
				break;
			case IfStmt.GE:
				result = lval >= rval;
				break;
			case IfStmt.LT:
				result = lval < rval;
				break;
			case IfStmt.LE:
				result = lval <= rval;
				break;
			default:
				throw new RuntimeException();
			}

			if (result) {
				jump = new GotoStmt(stmt.trueTarget());
				jump.catchTargets().addAll(stmt.catchTargets());
				node = jump;
				node.setValueNumber(v);

				if (replace) {
					stmt.replaceWith(node);
					cfg.removeEdge(block, stmt.falseTarget());
				}

			} else {
				jump = new GotoStmt(stmt.falseTarget());
				jump.catchTargets().addAll(stmt.catchTargets());
				node = jump;
				node.setValueNumber(v);

				if (replace) {
					stmt.replaceWith(node);
					cfg.removeEdge(block, stmt.trueTarget());
				}
			}

		} else if ((lvalue == null) && (rvalue == null)) {
			switch (stmt.comparison()) {
			case IfStmt.EQ:
				jump = new GotoStmt(stmt.trueTarget());
				jump.catchTargets().addAll(stmt.catchTargets());
				node = jump;
				node.setValueNumber(v);

				if (replace) {
					stmt.replaceWith(node);
					cfg.removeEdge(block, stmt.falseTarget());
				}
				break;
			case IfStmt.NE:
				jump = new GotoStmt(stmt.falseTarget());
				jump.catchTargets().addAll(stmt.catchTargets());
				node = jump;
				node.setValueNumber(v);

				if (replace) {
					stmt.replaceWith(node);
					cfg.removeEdge(block, stmt.trueTarget());
				}
				break;
			default:
				throw new RuntimeException();
			}
		}
	}

	public void visitSwitchStmt(final SwitchStmt stmt) {
		// If the index of the SwitchStmt evaluates to a constant value,
		// then always take that target (may be the default target).
		// Replace the SwitchStmt with a GotoStmt.
		final Block block = stmt.block();
		final FlowGraph cfg = block.graph();

		final int v = stmt.valueNumber();
		final int iv = stmt.index().valueNumber();
		values.ensureSize(Math.max(v, iv) + 1);

		ConstantExpr iexpr = null;

		if ((0 <= iv) && (iv < values.size())) {
			iexpr = (ConstantExpr) values.get(iv);
		}

		boolean ieffects = false;

		if (replace) {
			sideEffects.reset();
			stmt.index().visit(sideEffects);
			ieffects = sideEffects.hasSideEffects();

			if (ieffects) {
				return;
			}
		}

		if (iexpr == null) {
			return;
		}

		if (!(iexpr.value() instanceof Integer)) {
			return;
		}

		JumpStmt jump;

		final Integer ivalue = (Integer) iexpr.value();

		final int ival = ivalue.intValue();

		boolean useDefault = true;

		for (int i = 0; i < stmt.values().length; i++) {
			if (stmt.values()[i] == ival) {
				jump = new GotoStmt(stmt.targets()[i]);
				jump.catchTargets().addAll(stmt.catchTargets());
				node = jump;
				node.setValueNumber(v);

				if (replace) {
					stmt.replaceWith(node);
				}
				useDefault = false;

			} else {
				// Definitely not to this target.
				if (replace) {
					cfg.removeEdge(block, stmt.targets()[i]);
				}
			}
		}

		if (useDefault) {
			jump = new GotoStmt(stmt.defaultTarget());
			jump.catchTargets().addAll(stmt.catchTargets());
			node = jump;
			node.setValueNumber(v);

			if (replace) {
				stmt.replaceWith(node);
			}
		} else {
			// Definitely not to the default target.
			if (replace) {
				cfg.removeEdge(block, stmt.defaultTarget());
			}
		}
	}

	void printValueNumbers(final PrintWriter pw) {
		if (pw == null) {
			return;
		}

		final Iterator iter = values.iterator();

		pw.println("Value Numbers mapped to constants\n");

		for (int i = 0; iter.hasNext(); i++) {
			final Object o = iter.next();
			if (o != null) {
				pw.println(i + " -> " + o);
			}
		}
	}
}
