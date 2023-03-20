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
 * ArithExpr represents a binary arithmetic expression. It consists of two
 * operands and an operator.
 */
public class ArithExpr extends Expr {
	char operation; // Arithmetic operator

	Expr left; // Expression on left-hand side of operation

	Expr right; // Expression on right-hand side of operation

	// Operators...
	public static final char ADD = '+';

	public static final char SUB = '-';

	public static final char DIV = '/';

	public static final char MUL = '*';

	public static final char REM = '%';

	public static final char AND = '&';

	public static final char IOR = '|';

	public static final char XOR = '^';

	public static final char CMP = '?';

	public static final char CMPL = '<';

	public static final char CMPG = '>';

	/**
	 * Constructor.
	 * 
	 * @param operation
	 *            Arithmetic operation that this expression performs.
	 * @param left
	 *            Left-hand argument to operation.
	 * @param right
	 *            Right-hand argument to operation.
	 * @param type
	 *            The type of this expression.
	 */
	public ArithExpr(final char operation, final Expr left, final Expr right,
			final Type type) {
		super(type);
		this.operation = operation;
		this.left = left;
		this.right = right;

		left.setParent(this);
		right.setParent(this);
	}

	public int operation() {
		return operation;
	}

	public Expr left() {
		return left;
	}

	public Expr right() {
		return right;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			right.visit(visitor);
			left.visit(visitor);
		} else {
			left.visit(visitor);
			right.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitArithExpr(this);
	}

	public int exprHashCode() {
		return 1 + operation ^ left.exprHashCode() ^ right.exprHashCode();
	}

	/**
	 * Compare this arithmetic expression to another Expression.
	 * 
	 * @return True, if both expressions have the same contents.
	 */
	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof ArithExpr)
				&& (((ArithExpr) other).operation == operation)
				&& ((ArithExpr) other).left.equalsExpr(left)
				&& ((ArithExpr) other).right.equalsExpr(right);
	}

	public Object clone() {
		return copyInto(new ArithExpr(operation, (Expr) left.clone(),
				(Expr) right.clone(), type));
	}
}
