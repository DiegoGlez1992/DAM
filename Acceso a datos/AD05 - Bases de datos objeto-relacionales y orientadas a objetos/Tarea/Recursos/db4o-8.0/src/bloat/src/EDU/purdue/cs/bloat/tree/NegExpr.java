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
 * NegExpr represents the arithmetic negation of an expression.
 */
public class NegExpr extends Expr {
	Expr expr;

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            The expression to be negated.
	 * @param type
	 *            The type of this expression.
	 */
	public NegExpr(final Expr expr, final Type type) {
		super(type);
		this.expr = expr;
		expr.setParent(this);
	}

	public Expr expr() {
		return expr;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			expr.visit(visitor);
		} else {
			expr.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitNegExpr(this);
	}

	public int exprHashCode() {
		return 14 + expr.exprHashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof NegExpr)
				&& ((NegExpr) other).expr.equalsExpr(expr);
	}

	public Object clone() {
		return copyInto(new NegExpr((Expr) expr.clone(), type));
	}
}
