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
 * UPExpr represents an update check opcode which checks the persistent store to
 * determine if a variable needs to be updated.
 */
public class UCExpr extends CheckExpr {
	public static final int POINTER = 1;

	public static final int SCALAR = 2;

	int kind;

	/**
	 * Constructor.
	 * 
	 * @param expr
	 *            The expression to check to see if it needs to be updated.
	 * @param kind
	 *            The kind of expression (POINTER or SCALAR) to be checked.
	 * @param type
	 *            The type of this expression.
	 */
	public UCExpr(final Expr expr, final int kind, final Type type) {
		super(expr, type);
		this.kind = kind;
	}

	public int kind() {
		return kind;
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitUCExpr(this);
	}

	public boolean equalsExpr(final Expr other) {
		return (other instanceof UCExpr) && super.equalsExpr(other)
				&& (((UCExpr) other).kind == kind);
	}

	public Object clone() {
		return copyInto(new UCExpr((Expr) expr.clone(), kind, type));
	}
}
