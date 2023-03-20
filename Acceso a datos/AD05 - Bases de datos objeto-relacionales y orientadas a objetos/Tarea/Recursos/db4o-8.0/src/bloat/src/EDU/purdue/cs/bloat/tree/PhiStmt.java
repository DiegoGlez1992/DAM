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

import java.util.*;

/**
 * A PhiStmt is inserted into a CFG in Single Static Assignment for. It is used
 * to "merge" uses of the same variable in different basic blocks.
 * 
 * @see PhiJoinStmt
 * @see PhiCatchStmt
 */
public abstract class PhiStmt extends Stmt implements Assign {
	VarExpr target; // The variable into which the Phi statement assigns

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            A stack expression or local variable that is the target of
	 *            this phi-statement.
	 */
	public PhiStmt(final VarExpr target) {
		this.target = target;
		target.setParent(this);
	}

	public VarExpr target() {
		return target;
	}

	/**
	 * Return the expressions (variables) defined by this PhiStmt. In this case,
	 * only the target is defined.
	 */
	public DefExpr[] defs() {
		return new DefExpr[] { target };
	}

	public abstract Collection operands();

	public Object clone() {
		throw new RuntimeException();
	}
}
