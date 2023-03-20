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
package EDU.purdue.cs.bloat.ssa;

import java.util.*;

import EDU.purdue.cs.bloat.cfg.*;
import EDU.purdue.cs.bloat.tree.*;

/**
 * A PhiReturnStmt is placed at the return site of a subroutine. This is
 * necessary because variables that are not referrenced inside a subroutine
 * (finally block) retain their value. This is a problem for SSA when a variable
 * is assigned to inside an exception handler. At the beginning of the finally
 * block, there would be a merge of two occurrences of the variable (one from
 * the the exception handler and another from the "outside world") and a phi
 * function should be placed accordingly. If the type of the variable was
 * changed inside the exception handler, the operands of phi function would be
 * of different types and that would be bad. To avoid this situation
 * PhiReturnStmt are placed before the next instruction to be executed after the
 * subroutine has returned. Note that each PhiReturnStmt has only one operand
 * that is the same variable as its target. The two variables will have
 * different version numbers, however.
 * <p>
 * The following diagram demonstrates PhiReturnStmt:
 * 
 * <pre>
 *                            a1 = 1   a2 = 2
 *                            b1 = 1   b2 = 2
 *                              jsr     jsr
 *                                \     /
 *                                 \   /
 *                                  \ /
 *                                   *
 *                         a3 = PhiJoinStmt(a1, a2)
 *                         b3 = PhiJoinStmt(b1, b2)
 *                                   |
 *                                b4 = 4  // b is defined in subrountine
 *                                   |
 *                                  ret
 *                                   *
 *                                  / \
 *                                 /   \
 *                                /     \
 *                               /       \
 *              a4 = PhiReturnStmt(a3)  a5 = PhiReturnStmt(a3)
 *              b5 = PhiReturnStmt(b4)  b6 = PhiReturnStmt(b4)
 * </pre>
 * 
 * After transformation, the PhiReturnStmts will become
 * 
 * <pre>
 *                       a1                    a2
 *                       b4                    b4
 * </pre>
 * 
 * The variable <tt>a</tt> is not modified in the subroutine, so it retains
 * its value from before the jsr. The variable <tt>b</tt> is modified in the
 * subroutine, so its value after the ret is the value it was assigned in the
 * subroutine.
 */
class PhiReturnStmt extends PhiStmt {
	Subroutine sub;

	Expr operand;

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            Local variable to which the result of this phi statement is to
	 *            be assigned.
	 * @param sub
	 *            The subroutine from which we are returning.
	 */
	public PhiReturnStmt(final VarExpr target, final Subroutine sub) {
		super(target);
		this.sub = sub;
		this.operand = (VarExpr) target.clone();
		operand.setParent(this);
		operand.setDef(null);
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		operand.visit(visitor);
	}

	public void visit(final TreeVisitor visitor) {
		visitChildren(visitor);
	}

	/**
	 * Returns the subroutine associated with this <tt>PhiReturnStmt</tt>.
	 */
	public Subroutine sub() {
		return sub;
	}

	/**
	 * Returns a collection containing the operands to the phi statement. In
	 * this case the collection contains the one operand.
	 */
	public Collection operands() {
		final ArrayList v = new ArrayList();
		v.add(operand);
		return v;
	}

	/**
	 * Returns the operand of this <tt>PhiReturnStmt</tt> statement. A
	 * <tt>PhiReturnStmt</tt> has only one operand because the block that
	 * begins an exception handler may have only one incoming edge (critical
	 * edges were split).
	 */
	public Expr operand() {
		return operand;
	}

	public String toString() {
		return "" + target() + " := Phi-Return(" + operand + ", " + sub + ")";
	}
}
