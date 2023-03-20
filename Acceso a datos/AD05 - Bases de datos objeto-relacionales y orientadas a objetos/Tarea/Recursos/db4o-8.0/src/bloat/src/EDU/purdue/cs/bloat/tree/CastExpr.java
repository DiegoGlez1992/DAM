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
 * CastExpr represents an expression that casts an object to a given type.
 */
public class CastExpr extends Expr {
	Expr expr; // An expression (object) to cast

	Type castType; // The Type to cast expr to

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            Expression (object) to be cast.
	 * @param type
	 *            The type to which to cast expr and as well as the type of this
	 *            expression.
	 */
	public CastExpr(final Expr expr, final Type type) {
		this(expr, type, type);
	}

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            Expression (object) to be cast.
	 * @param castType
	 *            The type to which to cast expr.
	 * @param type
	 *            The type of this expression.
	 */
	public CastExpr(final Expr expr, final Type castType, final Type type) {
		super(type);
		this.expr = expr;
		this.castType = castType;

		expr.setParent(this);
	}

	public Expr expr() {
		return expr;
	}

	public Type castType() {
		return castType;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			expr.visit(visitor);
		} else {
			expr.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitCastExpr(this);
	}

	public int exprHashCode() {
		return 7 + expr.exprHashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof CastExpr)
				&& ((CastExpr) other).castType.equals(castType)
				&& ((CastExpr) other).expr.equalsExpr(expr);
	}

	public Object clone() {
		return copyInto(new CastExpr((Expr) expr.clone(), castType, type));
	}
}
