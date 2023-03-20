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
 * CheckExpr is a superclass for classes representing a check on an expression.
 * For instance, a CheckExpr is inserted into the tree before the divisor of a
 * divide operation. The CheckExpr checks to make sure that the divisor is not
 * zero.
 * 
 * @see RCExpr
 * @see UCExpr
 * @see ZeroCheckExpr
 */
public abstract class CheckExpr extends Expr {
	Expr expr;

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            An expression that is to be checked.
	 * @param type
	 *            The type of this expression.
	 */
	public CheckExpr(final Expr expr, final Type type) {
		super(type);
		this.expr = expr;
		expr.setParent(this);
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			expr.visit(visitor);
		} else {
			expr.visit(visitor);
		}
	}

	/**
	 * Returns the expression being checked.
	 */
	public Expr expr() {
		return expr;
	}

	public int exprHashCode() {
		return 9 + expr.exprHashCode() ^ type.simple().hashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof CheckExpr)
				&& ((CheckExpr) other).expr.equalsExpr(expr);
	}
}
