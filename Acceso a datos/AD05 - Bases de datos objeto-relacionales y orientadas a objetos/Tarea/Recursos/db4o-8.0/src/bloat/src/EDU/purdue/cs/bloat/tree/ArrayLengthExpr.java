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
 * <tt>ArrayLengthExpr</tt> represents the <i>arraylength</i> opcode which
 * gets length of an array.
 */
public class ArrayLengthExpr extends Expr {
	Expr array;

	/**
	 * Constructor.
	 * 
	 * @param array
	 *            Array whose length is sought.
	 * @param type
	 *            The type of this expression.
	 */
	public ArrayLengthExpr(final Expr array, final Type type) {
		super(type);
		this.array = array;
		array.setParent(this);
	}

	public Expr array() {
		return array;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			array.visit(visitor);
		} else {
			array.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitArrayLengthExpr(this);
	}

	public int exprHashCode() {
		return 3 + array.exprHashCode() ^ type.simple().hashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof ArrayLengthExpr)
				&& ((ArrayLengthExpr) other).array.equalsExpr(array);
	}

	public Object clone() {
		return copyInto(new ArrayLengthExpr((Expr) array.clone(), type));
	}
}
