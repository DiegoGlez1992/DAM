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
 * StaticFieldExpr represents the <tt>getstatic</tt> opcode which gets a
 * static (class) field from a class.
 */
public class StaticFieldExpr extends MemRefExpr {
	// getstatic

	MemberRef field;

	/**
	 * Constructor.
	 * 
	 * @param field
	 *            The field to access.
	 * @param type
	 *            The type of this expression.
	 */
	public StaticFieldExpr(final MemberRef field, final Type type) {
		super(type);
		this.field = field;
	}

	public MemberRef field() {
		return field;
	}

	public void visitForceChildren(final TreeVisitor visitor) {
	}

	public void visit(final TreeVisitor visitor) {
		visitor.visitStaticFieldExpr(this);
	}

	public int exprHashCode() {
		return 21 + field.hashCode() ^ type.simple().hashCode();
	}

	public boolean equalsExpr(final Expr other) {
		return (other != null) && (other instanceof StaticFieldExpr)
				&& ((StaticFieldExpr) other).field.equals(field);
	}

	public Object clone() {
		return copyInto(new StaticFieldExpr(field, type));
	}
}
