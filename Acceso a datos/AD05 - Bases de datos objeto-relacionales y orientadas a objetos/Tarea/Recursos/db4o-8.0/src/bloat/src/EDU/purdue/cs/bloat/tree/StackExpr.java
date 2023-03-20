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
 * StackExpr represents an expression that is stored on the stack.
 */
public class StackExpr extends VarExpr {
	/**
	 * Constructor.
	 * 
	 * @param index
	 *            Location (offset) in stack of the information to which the
	 *            expression refers. Index 0 represents the bottom of the stack.
	 * @param type
	 *            The type of this expression.
	 */
	public StackExpr(final int index, final Type type) {
		super(index, type);
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitStackExpr(this);
	}

	public int exprHashCode() {
		return 20 + index + type.simple().hashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other instanceof StackExpr)
				&& ((StackExpr) other).type.simple().equals(type.simple())
				&& (((StackExpr) other).index == index);
	}

	public Object clone() {
		return copyInto(new StackExpr(index, type));
	}
}
