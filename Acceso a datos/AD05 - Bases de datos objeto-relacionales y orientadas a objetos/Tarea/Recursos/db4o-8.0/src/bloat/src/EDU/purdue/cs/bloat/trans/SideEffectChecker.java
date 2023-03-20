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

import EDU.purdue.cs.bloat.editor.*;
import EDU.purdue.cs.bloat.tree.*;

/**
 * <tt>SideEffectChecker</tt> traverses a tree and determines is a node has
 * any side effects such as changing the stack, calling a function, or
 * performing a residency check. The side effects are represented by an integer
 * whose bits represent a certain kind of side effect.
 * 
 * <p>
 * 
 * <Tt>SideEffectChecker</tt> is a <tt>TreeVisitor</tt>. The way it works
 * is that after a <tt>SideEffectChecker</tt> is reset, an expression tree
 * <tt>Node</tt> is visited to determine whether or not it has side effects.
 * Neat.
 */
public class SideEffectChecker extends TreeVisitor {
	private int sideEffects = 0;

	public static final int STACK = (1 << 0);

	public static final int THROW = (1 << 1);

	public static final int CALL = (1 << 2);

	public static final int SYNC = (1 << 3);

	public static final int ALLOC = (1 << 4); // Allocates memory

	public static final int RC = (1 << 5);

	public static final int UC = (1 << 6);

	public static final int STORE = (1 << 7);

	public static final int ALIAS = (1 << 8);

	public static final int VOLATILE = (1 << 9);

	private EditorContext context;

	/**
	 * Constructor. The <tt>Context</tt> is needed to determine whether or not
	 * a field is VOLATILE, etc.
	 */
	public SideEffectChecker(final EditorContext context) {
		this.context = context;
	}

	public int sideEffects() {
		return sideEffects;
	}

	public boolean hasSideEffects() {
		return sideEffects != 0;
	}

	public void reset() {
		sideEffects = 0;
	}

	public void visitStoreExpr(final StoreExpr expr) {
		sideEffects |= SideEffectChecker.STORE;
		expr.visitChildren(this);
	}

	public void visitLocalExpr(final LocalExpr expr) {
		if (expr.isDef()) {
			sideEffects |= SideEffectChecker.STORE;
		}
		expr.visitChildren(this);
	}

	public void visitZeroCheckExpr(final ZeroCheckExpr expr) {
		sideEffects |= SideEffectChecker.THROW;
		expr.visitChildren(this);
	}

	public void visitRCExpr(final RCExpr expr) {
		sideEffects |= SideEffectChecker.RC;
		expr.visitChildren(this);
	}

	public void visitUCExpr(final UCExpr expr) {
		sideEffects |= SideEffectChecker.UC;
		expr.visitChildren(this);
	}

	public void visitNewMultiArrayExpr(final NewMultiArrayExpr expr) {
		// Memory allocation
		// NegativeArraySizeException
		sideEffects |= SideEffectChecker.THROW | SideEffectChecker.ALLOC;
		expr.visitChildren(this);
	}

	public void visitNewArrayExpr(final NewArrayExpr expr) {
		// Memory allocation
		// NegativeArraySizeException
		sideEffects |= SideEffectChecker.THROW | SideEffectChecker.ALLOC;
		expr.visitChildren(this);
	}

	public void visitCatchExpr(final CatchExpr expr) {
		// Stack change
		sideEffects |= SideEffectChecker.STACK;
		expr.visitChildren(this);
	}

	public void visitNewExpr(final NewExpr expr) {
		// Memory allocation
		sideEffects |= SideEffectChecker.ALLOC;
		expr.visitChildren(this);
	}

	public void visitStackExpr(final StackExpr expr) {
		// Stack change
		sideEffects |= SideEffectChecker.STACK;

		if (expr.isDef()) {
			sideEffects |= SideEffectChecker.STORE;
		}

		expr.visitChildren(this);
	}

	public void visitCastExpr(final CastExpr expr) {
		// ClassCastException
		if (expr.castType().isReference()) {
			sideEffects |= SideEffectChecker.THROW;
		}
		expr.visitChildren(this);
	}

	public void visitArithExpr(final ArithExpr expr) {
		// DivideByZeroException -- handled by ZeroCheckExpr
		/*
		 * if (expr.operation() == ArithExpr.DIV || expr.operation() ==
		 * ArithExpr.REM) {
		 * 
		 * if (expr.type().isIntegral() || expr.type().equals(Type.LONG)) {
		 * sideEffects |= THROW; } }
		 */

		expr.visitChildren(this);
	}

	public void visitArrayLengthExpr(final ArrayLengthExpr expr) {
		// NullPointerException
		sideEffects |= SideEffectChecker.THROW;
		expr.visitChildren(this);
	}

	public void visitArrayRefExpr(final ArrayRefExpr expr) {
		// NullPointerException, ArrayIndexOutOfBoundsException,
		// ArrayStoreException
		sideEffects |= SideEffectChecker.THROW;

		if (expr.isDef()) {
			sideEffects |= SideEffectChecker.STORE;
		}

		sideEffects |= SideEffectChecker.ALIAS;

		expr.visitChildren(this);
	}

	public void visitFieldExpr(final FieldExpr expr) {
		// NullPointerException -- handled by ZeroCheckExpr
		/*
		 * sideEffects |= THROW;
		 */

		if (expr.isDef()) {
			sideEffects |= SideEffectChecker.STORE;
		}

		final MemberRef field = expr.field();

		try {
			final FieldEditor e = context.editField(field);

			if (!e.isFinal()) {
				sideEffects |= SideEffectChecker.ALIAS;
			}

			if (e.isVolatile()) {
				sideEffects |= SideEffectChecker.VOLATILE;
			}

			context.release(e.fieldInfo());
		} catch (final NoSuchFieldException e) {
			// A field wasn't found. Silently assume it's not final and
			// is volatile.
			sideEffects |= SideEffectChecker.ALIAS;
			sideEffects |= SideEffectChecker.VOLATILE;
		}

		expr.visitChildren(this);
	}

	public void visitStaticFieldExpr(final StaticFieldExpr expr) {
		if (expr.isDef()) {
			sideEffects |= SideEffectChecker.STORE;
		}

		final MemberRef field = expr.field();

		try {
			final FieldEditor e = context.editField(field);

			if (e.isVolatile()) {
				sideEffects |= SideEffectChecker.VOLATILE;
			}

			context.release(e.fieldInfo());
		} catch (final NoSuchFieldException e) {
			// A field wasn't found. Silently assume it's volatile.
			sideEffects |= SideEffectChecker.VOLATILE;
		}

		expr.visitChildren(this);
	}

	public void visitCallStaticExpr(final CallStaticExpr expr) {
		// Call
		sideEffects |= SideEffectChecker.THROW | SideEffectChecker.CALL;
		expr.visitChildren(this);
	}

	public void visitCallMethodExpr(final CallMethodExpr expr) {
		// Call
		sideEffects |= SideEffectChecker.THROW | SideEffectChecker.CALL;
		expr.visitChildren(this);
	}

	public void visitMonitorStmt(final MonitorStmt stmt) {
		// Synchronization
		sideEffects |= SideEffectChecker.THROW | SideEffectChecker.SYNC;
		stmt.visitChildren(this);
	}

	public void visitStackManipStmt(final StackManipStmt stmt) {
		// Stack change
		sideEffects |= SideEffectChecker.STACK;
		stmt.visitChildren(this);
	}
}
