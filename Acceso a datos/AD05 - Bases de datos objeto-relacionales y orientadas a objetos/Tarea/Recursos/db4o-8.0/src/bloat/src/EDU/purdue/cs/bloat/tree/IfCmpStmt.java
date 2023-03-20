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

import EDU.purdue.cs.bloat.cfg.*;

/**
 * IfCmpStmt consists of a comparison expression (a left-hand expression, a
 * comparison operator, and a right-hand expression) that is to be evaluated.
 */
public class IfCmpStmt extends IfStmt {
	Expr left;

	Expr right;

	/**
	 * Constructor.
	 * 
	 * @param comparison
	 *            Comparison operator for this if statement.
	 * @param left
	 *            Expression on the left side of the comparison.
	 * @param right
	 *            Expression on the right side of the comparison.
	 * @param trueTarget
	 *            Block executed if comparison evaluates to true.
	 * @param falseTarget
	 *            Block executed if comparison evaluates to false.
	 */
	public IfCmpStmt(final int comparison, final Expr left, final Expr right,
			final Block trueTarget, final Block falseTarget) {
		super(comparison, trueTarget, falseTarget);
		this.left = left;
		this.right = right;
		left.setParent(this);
		right.setParent(this);
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
		visitor.visitIfCmpStmt(this);
	}

	public Object clone() {
		return copyInto(new IfCmpStmt(comparison, (Expr) left.clone(),
				(Expr) right.clone(), trueTarget, falseTarget));
	}
}
