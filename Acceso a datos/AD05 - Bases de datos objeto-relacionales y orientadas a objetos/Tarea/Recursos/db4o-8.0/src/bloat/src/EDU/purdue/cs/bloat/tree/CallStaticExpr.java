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

import EDU.purdue.cs.bloat.editor.*;

/**
 * CallStaticExpr represents the <tt>invokestatic</tt> opcode which invokes a
 * class (static) method. Static methods can always be inlined.
 * 
 * @see CallMethodExpr
 */
public class CallStaticExpr extends CallExpr {
	// invokestatic

	/**
	 * Constructor.
	 * 
	 * @param params
	 *            Parameters to the method.
	 * @param method
	 *            The (class) method to be invoked.
	 * @param type
	 *            The type of this expression.
	 */
	public CallStaticExpr(final Expr[] params, final MemberRef method,
			final Type type) {
		super(params, method, type);
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			for (int i = params.length - 1; i >= 0; i--) {
				params[i].visit(visitor);
			}
		} else {
			for (int i = 0; i < params.length; i++) {
				params[i].visit(visitor);
			}
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitCallStaticExpr(this);
	}

	public int exprHashCode() {
		int v = 6;

		for (int i = 0; i < params.length; i++) {
			v ^= params[i].exprHashCode();
		}

		return v;
	}

	public boolean equalsExpr(final Expr other) {
		return false;
	}

	public Object clone() {
		final Expr[] p = new Expr[params.length];

		for (int i = 0; i < params.length; i++) {
			p[i] = (Expr) params[i].clone();
		}

		return copyInto(new CallStaticExpr(p, method, type));
	}
}
