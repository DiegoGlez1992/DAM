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
 * LocalExpr represents an expression that accesses a variable in a method's
 * local variable table. Note that during register allocation the index becomes
 * the color that the LocalExpr (variable) is assigned.
 * 
 * @see Tree#newStackLocal
 * @see Tree#newLocal(Type)
 */
public class LocalExpr extends VarExpr implements LeafExpr {
	boolean fromStack;

	/**
	 * Constructor.
	 * 
	 * @param index
	 *            Index into the local variable table for this expression.
	 * @param fromStack
	 *            Is the local allocated on the stack?
	 * @param type
	 *            The type of this expression
	 */
	public LocalExpr(final int index, final boolean fromStack, final Type type) {
		super(index, type);
		this.fromStack = fromStack;
	}

	/**
	 * Constructor. LocalExpr is not allocated on the stack.
	 * 
	 * @param index
	 *            Index into the local variable table for this expression.
	 * @param type
	 *            The type of this expression.
	 */
	public LocalExpr(final int index, final Type type) {
		this(index, false, type);
	}

	public boolean fromStack() {
		return fromStack;
	}

	/**
	 * Returns true if the type of this expression is a return address.
	 */
	public boolean isReturnAddress() {
		return type().equals(Type.ADDRESS);
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitLocalExpr(this);
	}

	/**
	 * @param other
	 *            The other expression to compare against.
	 */
	public boolean equalsExpr(final Expr other) {
		return (other instanceof LocalExpr)
				&& ((LocalExpr) other).type.simple().equals(type.simple())
				&& (((LocalExpr) other).fromStack == fromStack)
				&& (((LocalExpr) other).index == index);
	}

	public int exprHashCode() {
		return 13 + (fromStack ? 0 : 1) + index + type.simple().hashCode();
	}

	public Object clone() {
		return copyInto(new LocalExpr(index, fromStack, type));
	}

}
