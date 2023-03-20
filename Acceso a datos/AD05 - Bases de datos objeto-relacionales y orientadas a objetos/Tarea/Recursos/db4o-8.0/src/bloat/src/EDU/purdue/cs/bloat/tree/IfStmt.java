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
 * IfStmt is a super class of statements in which some expression is evaluated
 * and one of two branches is taken.
 * 
 * @see IfCmpStmt
 * @see IfZeroStmt
 */
public abstract class IfStmt extends JumpStmt {
	int comparison; // Type of comparison that is performed

	Block trueTarget; // Code to jump to if IfStmt is true

	Block falseTarget; // Code to jump to if IfStmt is false

	// Compairson operators...
	public static final int EQ = 0;

	public static final int NE = 1;

	public static final int GT = 2;

	public static final int GE = 3;

	public static final int LT = 4;

	public static final int LE = 5;

	/**
	 * Constructor.
	 * 
	 * @param comparison
	 *            Comparison operator used in this if statement.
	 * @param trueTarget
	 *            Basic Block that is executed when if statement is true.
	 * @param falseTarget
	 *            Basic Block that is executed when if statement is false.
	 */
	public IfStmt(final int comparison, final Block trueTarget,
			final Block falseTarget) {
		this.comparison = comparison;
		this.trueTarget = trueTarget;
		this.falseTarget = falseTarget;
	}

	/**
	 * @return Comparison operator for this if statement.
	 */
	public int comparison() {
		return comparison;
	}

	/**
	 * Set the comparison operator for this if statement to its logical
	 * negative.
	 */
	public void negate() {
		switch (comparison) {
		case EQ:
			comparison = IfStmt.NE;
			break;
		case NE:
			comparison = IfStmt.EQ;
			break;
		case LT:
			comparison = IfStmt.GE;
			break;
		case GE:
			comparison = IfStmt.LT;
			break;
		case GT:
			comparison = IfStmt.LE;
			break;
		case LE:
			comparison = IfStmt.GT;
			break;
		}

		final Block t = trueTarget;
		trueTarget = falseTarget;
		falseTarget = t;
	}

	public void setTrueTarget(final Block target) {
		this.trueTarget = target;
	}

	public void setFalseTarget(final Block target) {
		this.falseTarget = target;
	}

	public Block trueTarget() {
		return trueTarget;
	}

	public Block falseTarget() {
		return falseTarget;
	}
}
