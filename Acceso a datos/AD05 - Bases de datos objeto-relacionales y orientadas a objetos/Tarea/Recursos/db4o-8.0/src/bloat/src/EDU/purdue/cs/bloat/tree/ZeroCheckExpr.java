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
 * ZeroCheckExpr represents a check for a zero value. For instance, when a
 * division operation is performed. a ZeroCheckExpr is inserted to ensure that
 * the divisor is not zero. It is used when division is performed (<i>idiv</i>,
 * <i>ldiv</i>) a remainder is taken (<i>irem</i>, <i>lrem</i>), or a field
 * is accessed (<i>getfield</i>, <i>putfield</i).
 */
public class ZeroCheckExpr extends CheckExpr {
	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            The expression to check for a zero value.
	 * @param type
	 *            The type of this expression.
	 */
	public ZeroCheckExpr(final Expr expr, final Type type) {
		super(expr, type);
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitZeroCheckExpr(this);
	}

	public boolean equalsExpr(final Expr other) {
		return (other instanceof ZeroCheckExpr) && super.equalsExpr(other);
	}

	public Object clone() {
		return copyInto(new ZeroCheckExpr((Expr) expr.clone(), type));
	}
}
