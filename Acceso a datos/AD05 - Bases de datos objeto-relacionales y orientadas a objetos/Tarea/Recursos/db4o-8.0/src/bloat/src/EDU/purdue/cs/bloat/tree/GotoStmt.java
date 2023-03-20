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
 * Represents an unconditional branch to a basic block.
 */
public class GotoStmt extends JumpStmt {
	Block target; // The basic Block that is the target of this goto

	/**
	 * Constructor.
	 * 
	 * @param target
	 *            The basic Block that is the target of this goto statement.
	 */
	public GotoStmt(final Block target) {
		this.target = target;
	}

	public void setTarget(final Block target) {
		this.target = target;
	}

	public Block target() {
		return target;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitGotoStmt(this);
	}

	public Object clone() {
		return copyInto(new GotoStmt(target));
	}
}
