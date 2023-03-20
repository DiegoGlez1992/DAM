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
 * <tt>ShiftExpr</tt> represents a bit shift operation.
 */
public class ShiftExpr extends Expr {
	int dir;

	Expr expr;

	Expr bits;

	public static final int LEFT = 0;

	public static final int RIGHT = 1;

	public static final int UNSIGNED_RIGHT = 2;

	/**
	 * Constructor.
	 * 
	 * @param dir
	 *            The direction (LEFT, RIGHT, or UNSIGNED_RIGHT) in which to
	 *            shift.
	 * @param expr
	 *            The expression to shift.
	 * @param bits
	 *            The number of bits to shift.
	 * @param type
	 *            The type of this expression.
	 */
	public ShiftExpr(final int dir, final Expr expr, final Expr bits,
			final Type type) {
		super(type);
		this.dir = dir;
		this.expr = expr;
		this.bits = bits;

		expr.setParent(this);
		bits.setParent(this);
	}

	public int dir() {
		return dir;
	}

	public Expr expr() {
		return expr;
	}

	public Expr bits() {
		return bits;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			bits.visit(visitor);
			expr.visit(visitor);
		} else {
			expr.visit(visitor);
			bits.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitShiftExpr(this);
	}

	public int exprHashCode() {
		return 19 + dir ^ expr.exprHashCode() ^ bits.exprHashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof ShiftExpr)
				&& (((ShiftExpr) other).dir == dir)
				&& ((ShiftExpr) other).expr.equalsExpr(expr)
				&& ((ShiftExpr) other).bits.equalsExpr(bits);
	}

	public Object clone() {
		return copyInto(new ShiftExpr(dir, (Expr) expr.clone(), (Expr) bits
				.clone(), type));
	}
}
