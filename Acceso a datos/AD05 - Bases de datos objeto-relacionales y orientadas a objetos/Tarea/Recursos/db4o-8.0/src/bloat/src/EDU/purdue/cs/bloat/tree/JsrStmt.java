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
 * JsrStmt represents a <i>jsr</i> instruction that jumps to a subroutine.
 * Recall that a subroutine is used to implement the finally cause in exception
 * handlers. The <i>ret</i> instruction is used to return from a subroutine.
 * 
 * @see RetStmt
 * @see Subroutine
 */
public class JsrStmt extends JumpStmt {
	Subroutine sub; // Subroutine to which to jump

	Block follow; // Basic Block to execute upon returning

	// from the subroutine

	/**
	 * Constructor.
	 * 
	 * @param sub
	 *            Subroutine that this statement jumps to.
	 * @param follow
	 *            Basic Block following the jump statement.
	 */
	public JsrStmt(final Subroutine sub, final Block follow) {
		this.sub = sub;
		this.follow = follow;
	}

	public void setFollow(final Block follow) {
		this.follow = follow;
	}

	public Block follow() {
		return follow;
	}

	public Subroutine sub() {
		return sub;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitJsrStmt(this);
	}

	public Object clone() {
		return copyInto(new JsrStmt(sub, follow));
	}
}
