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
 * ConstantExpr represents a constant expression. It is used when opcodes <i>ldc</i>,
 * <i>iinc</i>, and <i>getstatic</i> are visited. It value must be an Integer,
 * Long, Float, Double, or String.
 */
public class ConstantExpr extends Expr implements LeafExpr {
	// ldc

	Object value; // The operand to the ldc instruction

	/**
	 * Constructor.
	 * 
	 * @param value
	 *            The operand of the ldc instruction
	 * @param type
	 *            The Type of the operand
	 */
	public ConstantExpr(final Object value, final Type type) {
		super(type);
		this.value = value;
	}

	/**
	 * @return The operand of the ldc instruction
	 */
	public Object value() {
		return value;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitConstantExpr(this);
	}

	/**
	 * @return A hash code for this expression.
	 */
	public int exprHashCode() {
		if (value != null) {
			return 10 + value.hashCode();
		}

		return 10;
	}

	/**
	 * Compare this ConstantExpr to another Expr.
	 * 
	 * @param other
	 *            An Expr to compare this to.
	 * 
	 * @return True, if this and other are the same (that is, have the same
	 *         contents).
	 */
	public boolean equalsExpr(final Expr other) {
		if (!(other instanceof ConstantExpr)) {
			return false;
		}

		if (value == null) {
			return ((ConstantExpr) other).value == null;
		}

		if (((ConstantExpr) other).value == null) {
			return false;
		}

		return ((ConstantExpr) other).value.equals(value);
	}

	public Object clone() {
		return copyInto(new ConstantExpr(value, type));
	}
}
