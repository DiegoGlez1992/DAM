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
 * NewExpr represents the <tt>new</tt> opcode that creates a new object of a
 * specified type.
 */
public class NewExpr extends Expr {
	// new
	Type objectType;

	/**
	 * Constructor.
	 * 
	 * @param objectType
	 *            The type of the object to create.
	 * @param type
	 *            The type of this expression.
	 */
	public NewExpr(final Type objectType, final Type type) {
		super(type);
		this.objectType = objectType;
	}

	/**
	 * Returns the <tt>Type</tt> of the object being created.
	 */
	public Type objectType() {
		return objectType;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitNewExpr(this);
	}

	public int exprHashCode() {
		return 16 + objectType.hashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return false;
	}

	public Object clone() {
		return copyInto(new NewExpr(objectType, type));
	}
}
