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
 * NewMultiArrayExpr represents the <tt>multianewarray</tt> opcode which
 * creates a new multidimensional array.
 */
public class NewMultiArrayExpr extends Expr {
	// multianewarray

	Expr[] dimensions;

	Type elementType;

	/**
	 * Constructor.
	 * 
	 * @param dimensions
	 *            Expressions representing the size of each of the dimensions in
	 *            the array.
	 * @param elementType
	 *            The type of the elements in the array.
	 * @param type
	 *            The type of this expression.
	 */
	public NewMultiArrayExpr(final Expr[] dimensions, final Type elementType,
			final Type type) {
		super(type);
		this.elementType = elementType;
		this.dimensions = dimensions;

		for (int i = 0; i < dimensions.length; i++) {
			dimensions[i].setParent(this);
		}
	}

	public Expr[] dimensions() {
		return dimensions;
	}

	public Type elementType() {
		return elementType;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			for (int i = dimensions.length - 1; i >= 0; i--) {
				dimensions[i].visit(visitor);
			}
		} else {
			for (int i = 0; i < dimensions.length; i++) {
				dimensions[i].visit(visitor);
			}
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitNewMultiArrayExpr(this);
	}

	public int exprHashCode() {
		int v = 17;

		for (int i = 0; i < dimensions.length; i++) {
			v ^= dimensions[i].hashCode();
		}

		return v;
	}

	public boolean equalsExpr(final Expr other) {
		return false;
	}

	public Object clone() {
		final Expr[] d = new Expr[dimensions.length];

		for (int i = 0; i < dimensions.length; i++) {
			d[i] = (Expr) dimensions[i].clone();
		}

		return copyInto(new NewMultiArrayExpr(d, elementType, type));
	}
}
