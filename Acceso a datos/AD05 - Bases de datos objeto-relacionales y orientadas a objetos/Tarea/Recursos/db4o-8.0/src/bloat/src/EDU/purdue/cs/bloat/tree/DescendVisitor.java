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
 * DecsendVisitor is the superclass of a few private classes of Type0Visitor and
 * Type1Visitor. It descends the tree, keeping track of the number of right
 * links that have been taken.
 */
public abstract class DescendVisitor extends TreeVisitor {

	Hashtable useInfoMap;

	Hashtable defInfoMap;

	boolean found;

	Node beginNode; // where this visitor starts its search from

	LocalExpr start; // where the original search began

	int exchangeFactor;

	public DescendVisitor(final Hashtable useInfoMap, final Hashtable defInfoMap) {
		this.useInfoMap = useInfoMap;
		this.defInfoMap = defInfoMap;
	}

	public boolean search(final Node beginNode, final LocalExpr start) {
		this.beginNode = beginNode;
		this.start = start;
		exchangeFactor = 0;
		found = false;

		beginNode.visit(this);

		return found;
	}

	public void visitExprStmt(final ExprStmt stmt) {
		stmt.expr().visit(this);
	}

	public void visitIfStmt(final IfStmt stmt) {

		if (stmt instanceof IfCmpStmt) {
			visitIfCmpStmt((IfCmpStmt) stmt);
		} else if (stmt instanceof IfZeroStmt) {
			visitIfZeroStmt((IfZeroStmt) stmt);
		}
	}

	public void visitIfCmpStmt(final IfCmpStmt stmt) {
		stmt.left().visit(this); // search the left branch
		if (!found) { // if nothing has been found
			exchangeFactor++; // increase the exchange factor,
			if (stmt.left().type().isWide()) {
				exchangeFactor++; // twice to get
			}
			// around wides
			if (exchangeFactor < 3) {
				stmt.right().visit(this); // search the right branch.
			}
		}

	}

	public void visitIfZeroStmt(final IfZeroStmt stmt) {

		stmt.expr().visit(this);

	}

	public void visitInitStmt(final InitStmt stmt) {

		// would have been checked by the Type0Visitor

	}

	public void visitGotoStmt(final GotoStmt stmt) {

	}

	public void visitLabelStmt(final LabelStmt stmt) {

	}

	public void visitMonitorStmt(final MonitorStmt stmt) {

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

	}

	public void visitReturnStmt(final ReturnStmt stmt) {

	}

	public void visitAddressStoreStmt(final AddressStoreStmt stmt) {

	}

	// StoreExprs are very difficult because they represent several
	// types of expressions. What we do will depend on what the target
	// of the store is: ArrayRefExpr, FieldExpr, StaticFieldExpr,
	// or LocalExpr

	public void visitStoreExpr(final StoreExpr expr) {
		final MemExpr target = expr.target();

		if (target instanceof ArrayRefExpr) {
			// ArrayRefExpr: the store will be something like an astore
			// which manipulates the stack like
			// arrayref, index, val => ...
			// so, think of the tree like
			// (StoreExpr)
			// / \
			// Array Ref .
			// / \
			// index value
			// This is unlike the structure of the tree BLOAT uses for
			// intermediate representation, but it better relates to what's
			// on the stack at what time

			((ArrayRefExpr) target).array().visit(this); // visit the
			// left child
			if (!found) { // if match wasn't found
				exchangeFactor++; // take the right branch
				if (exchangeFactor < 3) { // (an array ref isn't wide)
					// visit next left child
					((ArrayRefExpr) target).index().visit(this);
					if (!found) { // if match wasn't found
						exchangeFactor++;
						if (exchangeFactor < 3) {
							expr.expr().visit(this); // search the right
														// branch
						}
					} // end seaching RR
				}
			} // end searching R
		} // end case where target is ArrayRefExpr

		else if (target instanceof FieldExpr) {
			// FieldExpr: the store will be like a putfield
			// which manipulates the stack like
			// objref, val => ...
			// so, think of the tree like
			// (StoreExpr)
			// / \
			// Object Ref value

			((FieldExpr) target).object().visit(this); // visit the left child

			if (!found) {
				exchangeFactor++; // (an object ref isn't wide)
				if (exchangeFactor < 3) {
					expr.expr().visit(this);
				}
			}
		} // end case where target is FieldRef

		else if (target instanceof StaticFieldExpr) {
			// StaticFieldExpr: the store will be like a putstatic
			// which manipulates the stack like
			// val => ...
			// so, think of the tree like
			// (StoreExpr)
			// /
			// value

			expr.expr.visit(this);
		}

		else if (target instanceof LocalExpr) {
			// LocalExpr: the store will be like istore/astore/etc.
			// which manipulates the stack like
			// val => ...
			// so, think of the tree like
			// (StoreExpr)
			// /
			// value

			expr.expr.visit(this);
		}
	}

	public void visitJsrStmt(final JsrStmt stmt) {

	}

	public void visitSwitchStmt(final SwitchStmt stmt) {

	}

	public void visitThrowStmt(final ThrowStmt stmt) {

	}

	public void visitStmt(final Stmt stmt) {

	}

	public void visitSCStmt(final SCStmt stmt) {

	}

	public void visitSRStmt(final SRStmt stmt) {

	}

	public void visitArithExpr(final ArithExpr expr) { // important one
		expr.left().visit(this); // visit the left branch

		if (!found) { // if a match isn't found yet
			exchangeFactor++; // increase the exchange factor
			if (expr.left().type().isWide()) {
				exchangeFactor++; // twice if wide
			}
			if (exchangeFactor < 3) {
				expr.right().visit(this); // visit right branch
			}
		}
	}

	public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
		expr.array().visit(this);
	}

	public void visitMemExpr(final MemExpr expr) {
		if (expr instanceof LocalExpr) {
			visitLocalExpr((LocalExpr) expr);
		}
	}

	public void visitMemRefExpr(final MemRefExpr expr) {

	}

	public void visitArrayRefExpr(final ArrayRefExpr expr) {

	}

	public void visitCallExpr(final CallExpr expr) {
		if (expr instanceof CallMethodExpr) {
			visitCallMethodExpr((CallMethodExpr) expr);
		} else if (expr instanceof CallStaticExpr) {
			visitCallStaticExpr((CallStaticExpr) expr);
		}
	}

	public void visitCallMethodExpr(final CallMethodExpr expr) {
		// Method invocations are to be thought of, in terms of
		// binary expression trees, as
		// (CallMethodExpr)
		// / \
		// receiver .
		// / \
		// arg1 .
		// / \
		// arg2 .
		// / \
		// arg3 ...
		// This might be the opposite of what one would think in terms
		// of currying (ie, one might think of currying in terms of
		// left associativity), but this gives a better picture of what
		// happens to the stack when invokestatic or invokevirtual is called:
		// objectref, [arg1, [arg2 ...]] => ...

		expr.receiver().visit(this);
		final Expr[] params = expr.params();
		if (!found && (exchangeFactor < 2) && (params.length > 0)) {
			exchangeFactor++; // (reciever won't be wide)
			params[0].visit(this);
		}

	}

	public void visitCallStaticExpr(final CallStaticExpr expr) {

		final Expr[] params = expr.params();
		if (params.length > 0) {
			params[0].visit(this);
		}
		if (!found && (exchangeFactor < 2) && (params.length > 1)) {
			exchangeFactor++;
			params[1].visit(this);
		}
	}

	public void visitCastExpr(final CastExpr expr) {
		expr.expr().visit(this);
	}

	public void visitConstantExpr(final ConstantExpr expr) {

	}

	public void visitFieldExpr(final FieldExpr expr) {
		expr.object.visit(this);
	}

	public void visitInstanceOfExpr(final InstanceOfExpr expr) {
		expr.expr().visit(this);
	}

	/* needs to be different for Type0 and Type1 */
	public abstract void visitLocalExpr(LocalExpr expr);

	public void visitNegExpr(final NegExpr expr) {
		expr.expr().visit(this);
	}

	public void visitNewArrayExpr(final NewArrayExpr expr) {
		expr.size().visit(this);
	}

	public void visitNewExpr(final NewExpr expr) {

	}

	public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
		// Think of the tree like
		// (NewMultiArrayExpr)
		// / \
		// count1 .
		// / \
		// count2 etc.
		// since multianewarray manipulates the stack like
		// count1, [count1 ...] => ...

		final Expr[] dims = expr.dimensions();
		if (dims.length > 0) {
			dims[0].visit(this);
		}
		if (!found && (exchangeFactor < 2) && (dims.length > 1)) {
			exchangeFactor++; // (count1 won't be wide)
			dims[1].visit(this);
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
		// perhaps add something here
	}

	public void visitRCExpr(final RCExpr expr) {

	}

	public void visitUCExpr(final UCExpr expr) {

	}

	public void visitReturnAddressExpr(final ReturnAddressExpr expr) {

	}

	public void visitShiftExpr(final ShiftExpr expr) {

	}

	public void visitVarExpr(final VarExpr expr) {
		if (expr instanceof LocalExpr) {
			visitLocalExpr((LocalExpr) expr);
		}
	}

	public void visitStaticFieldExpr(final StaticFieldExpr expr) {

	}

	public void visitExpr(final Expr expr) {

	}

}
