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
 * FieldExpr represents the <i>getfield</i> opcode which fetches a field from
 * an object.
 * 
 * @see MemberRef
 */
public class FieldExpr extends MemRefExpr {
	// getfield

	Expr object; // The object whose field we are fetching

	MemberRef field; // The field to fetch

	/**
	 * Constructor.
	 * 
	 * @param object The object (result of an Expr) whose field is to be
	 *        fetched.
	 * @param field
	 *            The field of object to be fetched.
	 * @param type
	 *            The type of this expression.
	 */
	public FieldExpr(final Expr object, final MemberRef field, final Type type) {
		super(type);
		this.object = object;
		this.field = field;

		object.setParent(this);
	}

	public Expr object() {
		return object;
	}

	public MemberRef field() {
		return field;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
		if (visitor.reverse()) {
			object.visit(visitor);
		} else {
			object.visit(visitor);
		}
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitFieldExpr(this);
	}

	public int exprHashCode() {
		return 11 + object.exprHashCode() ^ type.simple().hashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof FieldExpr)
				&& ((FieldExpr) other).field.equals(field)
				&& ((FieldExpr) other).object.equalsExpr(object);
	}

	public Object clone() {
		return copyInto(new FieldExpr((Expr) object.clone(), field, type));
	}
}
