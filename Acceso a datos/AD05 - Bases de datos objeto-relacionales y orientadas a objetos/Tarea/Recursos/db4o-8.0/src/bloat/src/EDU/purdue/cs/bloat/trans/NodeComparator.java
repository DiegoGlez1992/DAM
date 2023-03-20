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

import EDU.purdue.cs.bloat.tree.*;

/**
 * NodeComparator is a class used to differentiate nodes in an expression tree.
 */
public class NodeComparator {
	public static boolean DEBUG = false;

	/**
	 * Determines whether or not two <tt>Node</tt>s are equal.
	 */
	public static boolean equals(final Node v, final Node w) {
		class Bool {
			boolean value = false;
		}
		;

		final Bool eq = new Bool();

		v.visit(new TreeVisitor() {
			public void visitExprStmt(final ExprStmt stmt) {
				if (w instanceof ExprStmt) {
					eq.value = true;
				}
			}

			public void visitIfCmpStmt(final IfCmpStmt stmt) {
				if (w instanceof IfCmpStmt) {
					final IfCmpStmt s = (IfCmpStmt) w;
					eq.value = (stmt.trueTarget() == s.trueTarget())
							&& (stmt.falseTarget() == s.falseTarget())
							&& (stmt.comparison() == s.comparison());
				}
			}

			public void visitIfZeroStmt(final IfZeroStmt stmt) {
				if (w instanceof IfZeroStmt) {
					final IfZeroStmt s = (IfZeroStmt) w;
					eq.value = (stmt.trueTarget() == s.trueTarget())
							&& (stmt.falseTarget() == s.falseTarget())
							&& (stmt.comparison() == s.comparison());
				}
			}

			public void visitSCStmt(final SCStmt stmt) {
				if (w instanceof SCStmt) {
					final SCStmt s = (SCStmt) w;
					eq.value = (stmt.array() == s.array())
							&& (stmt.index() == s.index());
				}
			}

			public void visitSRStmt(final SRStmt stmt) {
				if (w instanceof SRStmt) {
					final SRStmt s = (SRStmt) w;
					eq.value = (stmt.array() == s.array())
							&& (stmt.start() == s.start())
							&& (stmt.end() == s.end());
				}
			}

			public void visitInitStmt(final InitStmt stmt) {
				if (w instanceof InitStmt) {
					eq.value = true;
				}
			}

			public void visitGotoStmt(final GotoStmt stmt) {
				if (w instanceof GotoStmt) {
					final GotoStmt s = (GotoStmt) w;
					eq.value = stmt.target() == s.target();
				}
			}

			public void visitLabelStmt(final LabelStmt stmt) {
				if (w instanceof LabelStmt) {
					final LabelStmt s = (LabelStmt) w;
					eq.value = stmt.label().equals(s.label());
				}
			}

			public void visitMonitorStmt(final MonitorStmt stmt) {
				if (w instanceof MonitorStmt) {
					final MonitorStmt s = (MonitorStmt) w;
					eq.value = stmt.kind() == s.kind();
				}
			}

			public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
				if (w instanceof PhiJoinStmt) {
					eq.value = true;
				}
			}

			public void visitPhiCatchStmt(final PhiCatchStmt stmt) {
				if (w instanceof PhiCatchStmt) {
					eq.value = true;
				}
			}

			public void visitCatchExpr(final CatchExpr expr) {
				// Catches are not equivalent.
				eq.value = false;
			}

			public void visitStackManipStmt(final StackManipStmt stmt) {
				if (w instanceof StackManipStmt) {
					final StackManipStmt s = (StackManipStmt) w;
					eq.value = stmt.kind() == s.kind();
				}
			}

			public void visitRetStmt(final RetStmt stmt) {
				if (w instanceof RetStmt) {
					final RetStmt s = (RetStmt) w;
					eq.value = stmt.sub() == s.sub();
				}
			}

			public void visitReturnExprStmt(final ReturnExprStmt stmt) {
				if (w instanceof ReturnExprStmt) {
					eq.value = true;
				}
			}

			public void visitReturnStmt(final ReturnStmt stmt) {
				if (w instanceof ReturnStmt) {
					eq.value = true;
				}
			}

			public void visitAddressStoreStmt(final AddressStoreStmt stmt) {
				if (w instanceof AddressStoreStmt) {
					final AddressStoreStmt s = (AddressStoreStmt) w;
					eq.value = stmt.sub() == s.sub();
				}
			}

			public void visitStoreExpr(final StoreExpr expr) {
				if (w instanceof StoreExpr) {
					eq.value = true;
				}
			}

			public void visitJsrStmt(final JsrStmt stmt) {
				if (w instanceof JsrStmt) {
					final JsrStmt s = (JsrStmt) w;
					eq.value = stmt.sub() == s.sub();
				}
			}

			public void visitSwitchStmt(final SwitchStmt stmt) {
				if (w instanceof SwitchStmt) {
					eq.value = stmt == w;
				}
			}

			public void visitThrowStmt(final ThrowStmt stmt) {
				if (w instanceof ThrowStmt) {
					eq.value = true;
				}
			}

			public void visitArithExpr(final ArithExpr expr) {
				if (w instanceof ArithExpr) {
					final ArithExpr e = (ArithExpr) w;
					eq.value = e.operation() == expr.operation();
				}
			}

			public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
				if (w instanceof ArrayLengthExpr) {
					eq.value = true;
				}
			}

			public void visitArrayRefExpr(final ArrayRefExpr expr) {
				if (w instanceof ArrayRefExpr) {
					eq.value = true;
				}
			}

			public void visitCallMethodExpr(final CallMethodExpr expr) {
				// Calls are never equal.
				eq.value = false;
			}

			public void visitCallStaticExpr(final CallStaticExpr expr) {
				// Calls are never equal.
				eq.value = false;
			}

			public void visitCastExpr(final CastExpr expr) {
				if (w instanceof CastExpr) {
					final CastExpr e = (CastExpr) w;
					eq.value = e.castType().equals(expr.castType());
				}
			}

			public void visitConstantExpr(final ConstantExpr expr) {
				if (w instanceof ConstantExpr) {
					final ConstantExpr e = (ConstantExpr) w;
					if (e.value() == null) {
						eq.value = expr.value() == null;
					} else {
						eq.value = e.value().equals(expr.value());
					}
				}
			}

			public void visitFieldExpr(final FieldExpr expr) {
				if (w instanceof FieldExpr) {
					final FieldExpr e = (FieldExpr) w;
					eq.value = e.field().equals(expr.field());
				}
			}

			public void visitInstanceOfExpr(final InstanceOfExpr expr) {
				if (w instanceof InstanceOfExpr) {
					final InstanceOfExpr e = (InstanceOfExpr) w;
					eq.value = e.checkType().equals(expr.checkType());
				}
			}

			public void visitLocalExpr(final LocalExpr expr) {
				if (w instanceof LocalExpr) {
					final LocalExpr e = (LocalExpr) w;
					eq.value = e.def() == expr.def();
				}
			}

			public void visitNegExpr(final NegExpr expr) {
				if (w instanceof NegExpr) {
					eq.value = true;
				}
			}

			public void visitNewArrayExpr(final NewArrayExpr expr) {
				eq.value = false;
			}

			public void visitNewExpr(final NewExpr expr) {
				eq.value = false;
			}

			public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
				eq.value = false;
			}

			public void visitZeroCheckExpr(final ZeroCheckExpr expr) {
				if (w instanceof ZeroCheckExpr) {
					eq.value = true;
				}
			}

			public void visitRCExpr(final RCExpr expr) {
				if (w instanceof RCExpr) {
					eq.value = true;
				}
			}

			public void visitUCExpr(final UCExpr expr) {
				if (w instanceof UCExpr) {
					final UCExpr e = (UCExpr) w;
					eq.value = e.kind() == expr.kind();
				}
			}

			public void visitReturnAddressExpr(final ReturnAddressExpr expr) {
				if (w instanceof ReturnAddressExpr) {
					eq.value = true;
				}
			}

			public void visitShiftExpr(final ShiftExpr expr) {
				if (w instanceof ShiftExpr) {
					final ShiftExpr e = (ShiftExpr) w;
					eq.value = e.dir() == expr.dir();
				}
			}

			public void visitStackExpr(final StackExpr expr) {
				if (w instanceof StackExpr) {
					final StackExpr e = (StackExpr) w;
					eq.value = e.def() == expr.def();
				}
			}

			public void visitStaticFieldExpr(final StaticFieldExpr expr) {
				if (w instanceof StaticFieldExpr) {
					final StaticFieldExpr e = (StaticFieldExpr) w;
					eq.value = e.field().equals(expr.field());
				}
			}

			public void visitNode(final Node node) {
				throw new RuntimeException("No method for " + node);
			}
		});

		return eq.value;
	}

	/**
	 * Computes a hash code for a given <tt>Node</tt> based upon its type. The
	 * hash code of nodes that are composed of other nodes are based upon their
	 * composits.
	 */
	public static int hashCode(final Node node) {
		class Int {
			int value = 0;
		}
		;

		final Int hash = new Int();

		node.visit(new TreeVisitor() {
			public void visitExprStmt(final ExprStmt stmt) {
				hash.value = 1;
			}

			public void visitIfCmpStmt(final IfCmpStmt stmt) {
				hash.value = stmt.comparison() + stmt.trueTarget().hashCode()
						+ stmt.falseTarget().hashCode();
			}

			public void visitIfZeroStmt(final IfZeroStmt stmt) {
				hash.value = stmt.comparison() + stmt.trueTarget().hashCode()
						+ stmt.falseTarget().hashCode();
			}

			public void visitInitStmt(final InitStmt stmt) {
				hash.value = 2;
			}

			public void visitGotoStmt(final GotoStmt stmt) {
				hash.value = stmt.target().hashCode();
			}

			public void visitLabelStmt(final LabelStmt stmt) {
				hash.value = stmt.label().hashCode();
			}

			public void visitMonitorStmt(final MonitorStmt stmt) {
				hash.value = stmt.kind();
			}

			public void visitPhiJoinStmt(final PhiJoinStmt stmt) {
				hash.value = 3;
			}

			public void visitPhiCatchStmt(final PhiCatchStmt stmt) {
				hash.value = 4;
			}

			public void visitCatchExpr(final CatchExpr expr) {
				// Catches are not equivalent.
				hash.value = expr.hashCode();
			}

			public void visitStackManipStmt(final StackManipStmt stmt) {
				hash.value = stmt.kind();
			}

			public void visitRetStmt(final RetStmt stmt) {
				hash.value = 5;
			}

			public void visitReturnExprStmt(final ReturnExprStmt stmt) {
				hash.value = 6;
			}

			public void visitReturnStmt(final ReturnStmt stmt) {
				hash.value = 7;
			}

			public void visitAddressStoreStmt(final AddressStoreStmt stmt) {
				hash.value = 8;
			}

			public void visitStoreExpr(final StoreExpr expr) {
				hash.value = 9;
			}

			public void visitJsrStmt(final JsrStmt stmt) {
				hash.value = 10;
			}

			public void visitSwitchStmt(final SwitchStmt stmt) {
				hash.value = 11;
			}

			public void visitThrowStmt(final ThrowStmt stmt) {
				hash.value = 12;
			}

			public void visitArithExpr(final ArithExpr expr) {
				hash.value = expr.operation();
			}

			public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
				hash.value = 13;
			}

			public void visitArrayRefExpr(final ArrayRefExpr expr) {
				hash.value = 14;
			}

			public void visitCallMethodExpr(final CallMethodExpr expr) {
				// Calls are never equal.
				hash.value = expr.hashCode();
			}

			public void visitCallStaticExpr(final CallStaticExpr expr) {
				// Calls are never equal.
				hash.value = expr.hashCode();
			}

			public void visitCastExpr(final CastExpr expr) {
				hash.value = expr.castType().hashCode();
			}

			public void visitConstantExpr(final ConstantExpr expr) {
				if (expr.value() == null) {
					hash.value = 0;
				} else {
					hash.value = expr.value().hashCode();
				}
			}

			public void visitFieldExpr(final FieldExpr expr) {
				hash.value = expr.field().hashCode();
			}

			public void visitInstanceOfExpr(final InstanceOfExpr expr) {
				hash.value = expr.checkType().hashCode();
			}

			public void visitLocalExpr(final LocalExpr expr) {
				if (expr.def() != null) {
					hash.value = expr.def().hashCode();
				} else {
					hash.value = 0;
				}
			}

			public void visitNegExpr(final NegExpr expr) {
				hash.value = 16;
			}

			public void visitNewArrayExpr(final NewArrayExpr expr) {
				hash.value = expr.hashCode();
			}

			public void visitNewExpr(final NewExpr expr) {
				hash.value = expr.hashCode();
			}

			public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
				hash.value = expr.hashCode();
			}

			public void visitZeroCheckExpr(final ZeroCheckExpr expr) {
				hash.value = 15;
			}

			public void visitRCExpr(final RCExpr expr) {
				hash.value = 17;
			}

			public void visitUCExpr(final UCExpr expr) {
				hash.value = 18 + expr.kind();
			}

			public void visitReturnAddressExpr(final ReturnAddressExpr expr) {
				hash.value = 21;
			}

			public void visitSCStmt(final SCStmt stmt) {
				hash.value = 23;
			}

			public void visitSRStmt(final SRStmt stmt) {
				hash.value = 22;
			}

			public void visitShiftExpr(final ShiftExpr expr) {
				hash.value = expr.dir();
			}

			public void visitStackExpr(final StackExpr expr) {
				if (expr.def() != null) {
					hash.value = expr.def().hashCode();
				} else {
					hash.value = 0;
				}
			}

			public void visitStaticFieldExpr(final StaticFieldExpr expr) {
				hash.value = expr.field().hashCode();
			}

			public void visitNode(final Node node) {
				throw new RuntimeException("No method for " + node);
			}
		});

		return hash.value;
	}
}
